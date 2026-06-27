package model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class CoverFetcher {

    /** Shared client – follows redirects, keeps a connection pool, honours HTTP/2. */
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(6))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    // ── Open Library ──────────────────────────────────────────────────────────
    private static final String OL_SEARCH_BASE = "https://openlibrary.org/search.json";
    private static final String OL_COVER_BASE  = "https://covers.openlibrary.org/b/id/";

    // ── TMDb ──────────────────────────────────────────────────────────────────
    // This key will expire at the end of July
    private static final String TMDB_API_KEY   = "eb80b69a712094bef73dfc19be89e764";
    private static final String TMDB_SEARCH    = "https://api.themoviedb.org/3/search/movie";
    private static final String TMDB_IMAGE_BASE = "https://image.tmdb.org/t/p/w342";

    /**
     * Convenience overload — assumes a book/non-DVD item type.
     */
    public static byte[] fetchCoverBytes(String title) {
        return fetchCoverBytes(title, "Book");
    }

    /**
     * Fetches cover art for the given item.
     * DVDs are looked up via TMDb; everything else via Open Library.
     *
     * @param title    item title
     * @param itemType one of "Book", "Magazine", "AudioBook", "DVD"
     * @return raw image bytes, or {@code null} if nothing is found
     */
    public static byte[] fetchCoverBytes(String title, String itemType) {
        if ("DVD".equalsIgnoreCase(itemType)) {
            return fetchFromTmdb(title);
        }
        return fetchFromOpenLibrary(title);
    }

    // ── Open Library ──────────────────────────────────────────────────────────

    private static byte[] fetchFromOpenLibrary(String title) {
        try {
            String encoded   = URLEncoder.encode(title, StandardCharsets.UTF_8);
            String searchUrl = OL_SEARCH_BASE + "?title=" + encoded + "&limit=1&fields=cover_i";

            HttpRequest searchReq = HttpRequest.newBuilder()
                    .uri(URI.create(searchUrl))
                    .timeout(Duration.ofSeconds(6))
                    .header("User-Agent", "LibraryManagementApp/1.0")
                    .GET()
                    .build();

            HttpResponse<String> searchResp =
                    HTTP.send(searchReq, HttpResponse.BodyHandlers.ofString());

            if (searchResp.statusCode() != 200) return null;
            JsonObject root = JsonParser.parseString(searchResp.body()).getAsJsonObject();
            JsonArray  docs = root.getAsJsonArray("docs");

            if (docs == null || docs.isEmpty()) return null;

            JsonObject firstDoc = docs.get(0).getAsJsonObject();
            if (!firstDoc.has("cover_i")) return null;

            int coverId  = firstDoc.get("cover_i").getAsInt();
            String coverUrl = OL_COVER_BASE + coverId + "-M.jpg";

            HttpRequest coverReq = HttpRequest.newBuilder()
                    .uri(URI.create(coverUrl))
                    .timeout(Duration.ofSeconds(8))
                    .GET()
                    .build();

            HttpResponse<byte[]> coverResp =
                    HTTP.send(coverReq, HttpResponse.BodyHandlers.ofByteArray());

            return coverResp.statusCode() == 200 ? coverResp.body() : null;

        } catch (Exception e) {
            return null;
        }
    }

    // ── TMDb ──────────────────────────────────────────────────────────────────

    private static byte[] fetchFromTmdb(String title) {
        try {
            String encoded   = URLEncoder.encode(title, StandardCharsets.UTF_8);
            String searchUrl = TMDB_SEARCH + "?api_key=" + TMDB_API_KEY
                    + "&query=" + encoded + "&limit=1";

            HttpRequest searchReq = HttpRequest.newBuilder()
                    .uri(URI.create(searchUrl))
                    .timeout(Duration.ofSeconds(6))
                    .header("User-Agent", "LibraryManagementApp/1.0")
                    .GET()
                    .build();

            HttpResponse<String> searchResp =
                    HTTP.send(searchReq, HttpResponse.BodyHandlers.ofString());

            if (searchResp.statusCode() != 200) return null;

            JsonObject root    = JsonParser.parseString(searchResp.body()).getAsJsonObject();
            JsonArray  results = root.getAsJsonArray("results");
            if (results == null || results.isEmpty()) return null;

            JsonObject first = results.get(0).getAsJsonObject();
            if (!first.has("poster_path") || first.get("poster_path").isJsonNull()) return null;

            String posterPath = first.get("poster_path").getAsString();
            String imageUrl   = TMDB_IMAGE_BASE + posterPath;

            HttpRequest coverReq = HttpRequest.newBuilder()
                    .uri(URI.create(imageUrl))
                    .timeout(Duration.ofSeconds(8))
                    .GET()
                    .build();

            HttpResponse<byte[]> coverResp =
                    HTTP.send(coverReq, HttpResponse.BodyHandlers.ofByteArray());

            return coverResp.statusCode() == 200 ? coverResp.body() : null;

        } catch (Exception e) {
            // Network unavailable, JSON malformed, timeout, etc. — silently degrade.
            return null;
        }
    }
}
