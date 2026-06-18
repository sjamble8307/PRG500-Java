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

    /** Shared client – keeps a connection pool and honours HTTP/2. */
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(6))
            .build();

    private static final String SEARCH_BASE = "https://openlibrary.org/search.json";
    private static final String COVER_BASE  = "https://covers.openlibrary.org/b/id/";

   public static byte[] fetchCoverBytes(String title) {
        try {
            String encoded   = URLEncoder.encode(title, StandardCharsets.UTF_8);
            String searchUrl = SEARCH_BASE + "?title=" + encoded + "&limit=1&fields=cover_i";

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
            if (!firstDoc.has("cover_i")) return null;   // no cover on record

            int coverId = firstDoc.get("cover_i").getAsInt();

            String coverUrl = COVER_BASE + coverId + "-M.jpg";

            HttpRequest coverReq = HttpRequest.newBuilder()
                    .uri(URI.create(coverUrl))
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

