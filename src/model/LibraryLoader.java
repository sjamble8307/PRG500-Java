package model;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LibraryLoader {

    // Reads items.csv with OpenCSV, then uses a Stream pipeline to map each row to a LibraryItem subclass.
    public static List<LibraryItem> loadItems(String path) throws IOException, CsvValidationException {
        List<String[]> rows = readRows(path);

        return rows.stream()
                .filter(row -> row.length > 1 && !row[1].isBlank())  // skip malformed/blank rows
                .map(LibraryLoader::parseItemRow)
                .filter(Objects::nonNull)                              // skip unknown types
                .collect(Collectors.toList());
    }

    // CSV columns: itemId, type, title, author, field1, field2, field3, field4[, checkedOut]
    private static LibraryItem parseItemRow(String[] c) {
        String id     = c[0].trim();
        String type   = c[1].trim();
        String title  = c[2].trim();
        String author = c[3].trim();

        LibraryItem item = switch (type) {
            case "Book" -> new Book(id, title, author, c[4].trim(), Integer.parseInt(c[5].trim()));
            case "Magazine" -> new Magazine(id, title, author, c[4].trim(), Integer.parseInt(c[5].trim()));
            case "DVD" -> new DVD(id, title, author, Integer.parseInt(c[4].trim()), c[5].trim());
            case "AudioBook" -> new AudioBook(id, title, author,
                    c[4].trim(), Integer.parseInt(c[5].trim()),
                    c[6].trim(), Double.parseDouble(c[7].trim()));
            default -> {
                System.err.println("Unknown item type: " + type);
                yield null;
            }
        };

        // Restore checked-out status if the optional 9th column is present.
        if (item != null && c.length > 8 && "true".equalsIgnoreCase(c[8].trim())) {
            item.setCheckedOut(true);
        }
        return item;
    }

    // Reads users.csv with OpenCSV, then uses a Stream pipeline to map each row to a User subclass.
    public static List<User> loadUsers(String path, List<LibraryItem> catalogue)
            throws IOException, CsvValidationException {
        List<String[]> rows = readRows(path);

        return rows.stream()
                .filter(row -> row.length > 1 && !row[1].isBlank())
                .map(row -> parseUserRow(row, catalogue))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // CSV columns: userId, role, name, contactInfo, extra
    private static User parseUserRow(String[] c, List<LibraryItem> catalogue) {
        String id      = c[0].trim();
        String role    = c[1].trim();
        String name    = c[2].trim();
        String contact = c[3].trim();

        return switch (role) {
            case "Member"    -> new Member(id, name, contact, Integer.parseInt(c[4].trim()));
            case "Librarian" -> new Librarian(id, name, contact, c[4].trim(), catalogue);
            default -> {
                System.err.println("Unknown user role: " + role);
                yield null;
            }
        };
    }

    // Convenience used by the UI layer: returns the first Librarian as a User, so callers
    // in other packages don't need access to the package-private Librarian subclass.
    public static User findLibrarian(List<User> users) {
        return users.stream()
                .filter(u -> u instanceof Librarian)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No librarian found in users.csv"));
    }

   // Shared utility method to read files.
    private static List<String[]> readRows(String path) throws IOException, CsvValidationException {
        List<String[]> rows = new ArrayList<>();
        try (CSVReader csv = new CSVReader(Files.newBufferedReader(Paths.get(path)))) {
            csv.skip(1);    // skip header row
            String[] row;
            while ((row = csv.readNext()) != null) {
                rows.add(row);
            }
        }
        return rows;
    }

    /**
     * Writes the full catalogue back to the CSV file, preserving the original
     * column layout and appending the optional {@code checkedOut} column.
     *
     * Called by the UI layer whenever the catalogue changes (add book, toggle availability).
     */
    public static void saveItems(String path, List<LibraryItem> items) throws IOException {
        try (Writer writer = Files.newBufferedWriter(Paths.get(path));
             CSVWriter csv = new CSVWriter(writer)) {
            // Write header – includes the optional checkedOut column
            csv.writeNext(new String[]{"itemId","type","title","author",
                    "field1","field2","field3","field4","checkedOut"});
            for (LibraryItem item : items) {
                csv.writeNext(item.toCsvRow());
            }
        }
    }
}
