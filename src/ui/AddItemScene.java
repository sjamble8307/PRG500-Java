package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import model.LibraryItem;

/**
 * Scene 3 — Add Item.
 *
 * Supports all four library item types: Book, Magazine, DVD, AudioBook.
 * A ComboBox lets the librarian choose the type; only the relevant fields
 * are shown. The item is created via the appropriate factory on
 * {@code model.LibraryItem} and added to the shared ObservableList.
 */
public class AddItemScene {

    private final LibraryApp app;

    public AddItemScene(LibraryApp app) {
        this.app = app;
    }

    public Parent build() {
        Label heading = new Label("Add an Item");
        heading.setFont(Font.font(22));

        // ── Type selector ────────────────────────────────────────────────────
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Book", "Magazine", "DVD", "AudioBook");
        typeBox.setValue("Book");

        // ── Common fields ────────────────────────────────────────────────────
        TextField idField    = new TextField();  idField.setPromptText("e.g. B010");
        TextField titleField = new TextField();  titleField.setPromptText("Item title");

        // ── Book / AudioBook fields ──────────────────────────────────────────
        Label authorLbl  = new Label("Author:");
        TextField authorField = new TextField(); authorField.setPromptText("Author name");

        Label genreLbl   = new Label("Genre:");
        TextField genreField  = new TextField(); genreField.setPromptText("e.g. Fiction");

        Label pagesLbl   = new Label("Pages:");
        TextField pagesField  = new TextField(); pagesField.setPromptText("e.g. 320");

        // ── AudioBook-only fields ────────────────────────────────────────────
        Label narratorLbl   = new Label("Narrator:");
        TextField narratorField = new TextField(); narratorField.setPromptText("Narrator name");

        Label audioHrsLbl   = new Label("Duration (hrs):");
        TextField audioHrsField = new TextField(); audioHrsField.setPromptText("e.g. 8.5");

        // ── Magazine fields ──────────────────────────────────────────────────
        Label publisherLbl   = new Label("Publisher:");
        TextField publisherField = new TextField(); publisherField.setPromptText("Publisher name");

        Label issueDateLbl   = new Label("Issue Date:");
        TextField issueDateField = new TextField(); issueDateField.setPromptText("e.g. 2024-06");

        Label issueNumLbl   = new Label("Issue #:");
        TextField issueNumField = new TextField(); issueNumField.setPromptText("e.g. 42");

        // ── DVD fields ───────────────────────────────────────────────────────
        Label directorLbl   = new Label("Director:");
        TextField directorField = new TextField(); directorField.setPromptText("Director name");

        Label durationLbl   = new Label("Duration (min):");
        TextField durationField = new TextField(); durationField.setPromptText("e.g. 120");

        Label ratingLbl   = new Label("Rating:");
        TextField ratingField = new TextField(); ratingField.setPromptText("e.g. PG-13");

        // ── Form grid ────────────────────────────────────────────────────────
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.addRow(0, new Label("Type:"),  typeBox);
        form.addRow(1, new Label("ID:"),    idField);
        form.addRow(2, new Label("Title:"), titleField);

        // Rows that toggle visibility
        form.addRow(3, authorLbl,    authorField);
        form.addRow(4, genreLbl,     genreField);
        form.addRow(5, pagesLbl,     pagesField);
        form.addRow(6, narratorLbl,  narratorField);
        form.addRow(7, audioHrsLbl,  audioHrsField);
        form.addRow(8, publisherLbl, publisherField);
        form.addRow(9, issueDateLbl, issueDateField);
        form.addRow(10, issueNumLbl, issueNumField);
        form.addRow(11, directorLbl, directorField);
        form.addRow(12, durationLbl, durationField);
        form.addRow(13, ratingLbl,   ratingField);

        Label message = new Label();
        message.setWrapText(true);

        // ── Helper: show/hide rows based on type ─────────────────────────────
        Runnable updateVisibility = () -> {
            String type = typeBox.getValue();
            boolean isBook      = "Book".equals(type);
            boolean isMagazine  = "Magazine".equals(type);
            boolean isDVD       = "DVD".equals(type);
            boolean isAudioBook = "AudioBook".equals(type);

            // Book + AudioBook share author/genre/pages
            setRowVisible(authorLbl,    authorField,    isBook || isAudioBook);
            setRowVisible(genreLbl,     genreField,     isBook || isAudioBook);
            setRowVisible(pagesLbl,     pagesField,     isBook || isAudioBook);

            // AudioBook extras
            setRowVisible(narratorLbl,  narratorField,  isAudioBook);
            setRowVisible(audioHrsLbl,  audioHrsField,  isAudioBook);

            // Magazine
            setRowVisible(publisherLbl, publisherField, isMagazine);
            setRowVisible(issueDateLbl, issueDateField, isMagazine);
            setRowVisible(issueNumLbl,  issueNumField,  isMagazine);

            // DVD
            setRowVisible(directorLbl,  directorField,  isDVD);
            setRowVisible(durationLbl,  durationField,  isDVD);
            setRowVisible(ratingLbl,    ratingField,    isDVD);
        };

        typeBox.setOnAction(e -> updateVisibility.run());
        updateVisibility.run();  // set initial state

        // ── Add button ───────────────────────────────────────────────────────
        Button addBtn = new Button("Add");
        addBtn.setDefaultButton(true);
        addBtn.setOnAction(e -> {
            String type  = typeBox.getValue();
            String id    = idField.getText().trim();
            String title = titleField.getText().trim();

            if (id.isEmpty() || title.isEmpty()) {
                message.setText("ID and Title are required.");
                return;
            }

            try {
                LibraryItem item = switch (type) {
                    case "Book" -> {
                        String author = authorField.getText().trim();
                        String genre  = genreField.getText().trim();
                        int pages     = parseIntField(pagesField, 0);
                        if (author.isEmpty()) { message.setText("Author is required for a Book."); yield null; }
                        yield LibraryItem.createBook(id, title, author,
                                genre.isEmpty() ? "General" : genre, pages);
                    }
                    case "Magazine" -> {
                        String publisher = publisherField.getText().trim();
                        String issueDate = issueDateField.getText().trim();
                        int issueNum     = parseIntField(issueNumField, 1);
                        if (publisher.isEmpty()) { message.setText("Publisher is required for a Magazine."); yield null; }
                        yield LibraryItem.createMagazine(id, title, publisher,
                                issueDate.isEmpty() ? "Unknown" : issueDate, issueNum);
                    }
                    case "DVD" -> {
                        String director  = directorField.getText().trim();
                        int duration     = parseIntField(durationField, 0);
                        String rating    = ratingField.getText().trim();
                        if (director.isEmpty()) { message.setText("Director is required for a DVD."); yield null; }
                        yield LibraryItem.createDVD(id, title, director, duration,
                                rating.isEmpty() ? "Unrated" : rating);
                    }
                    case "AudioBook" -> {
                        String author   = authorField.getText().trim();
                        String genre    = genreField.getText().trim();
                        int pages       = parseIntField(pagesField, 0);
                        String narrator = narratorField.getText().trim();
                        double hrs      = parseDoubleField(audioHrsField, 0.0);
                        if (author.isEmpty()) { message.setText("Author is required for an AudioBook."); yield null; }
                        yield LibraryItem.createAudioBook(id, title, author,
                                genre.isEmpty() ? "General" : genre, pages,
                                narrator.isEmpty() ? "Unknown" : narrator, hrs);
                    }
                    default -> { message.setText("Unknown type selected."); yield null; }
                };

                if (item == null) return;

                app.getCatalogue().add(item);
                app.saveCatalogue();
                message.setText("Added " + type + " '" + title + "'. Add another, or go back.");

                idField.clear(); titleField.clear();
                authorField.clear(); genreField.clear(); pagesField.clear();
                narratorField.clear(); audioHrsField.clear();
                publisherField.clear(); issueDateField.clear(); issueNumField.clear();
                directorField.clear(); durationField.clear(); ratingField.clear();

            } catch (NumberFormatException ex) {
                message.setText("A numeric field contains an invalid value.");
            }
        });

        Button backBtn = new Button("Back to dashboard");
        backBtn.setOnAction(e -> app.showDashboard());

        HBox buttons = new HBox(10, addBtn, backBtn);
        buttons.setAlignment(Pos.CENTER_LEFT);

        VBox root = new VBox(16, heading, form, buttons, message);
        root.setPadding(new Insets(24));
        return root;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static void setRowVisible(javafx.scene.Node label,
                                      javafx.scene.Node field,
                                      boolean visible) {
        label.setVisible(visible);
        label.setManaged(visible);
        field.setVisible(visible);
        field.setManaged(visible);
    }

    private static int parseIntField(TextField field, int defaultValue) {
        String text = field.getText().trim();
        if (text.isBlank()) return defaultValue;
        return Integer.parseInt(text);
    }

    private static double parseDoubleField(TextField field, double defaultValue) {
        String text = field.getText().trim();
        if (text.isBlank()) return defaultValue;
        return Double.parseDouble(text);
    }
}

