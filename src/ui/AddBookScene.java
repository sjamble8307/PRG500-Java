package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import model.LibraryItem;

/**
 * Scene 3 — Add Book.
 *
 * A form the librarian uses to add a new book to the catalogue. The new book is
 * created via the {@code model.LibraryItem.createBook(...)} factory and added to
 * the shared ObservableList, so it shows up immediately on the Dashboard table.
 * "Add" stays on this scene so several books can be added in a row; "Back"
 * returns to the Dashboard.
 */
public class AddBookScene {

    private final LibraryApp app;

    public AddBookScene(LibraryApp app) {
        this.app = app;
    }

    public Parent build() {
        Label heading = new Label("Add a Book");
        heading.setFont(Font.font(22));

        TextField idField     = new TextField();
        TextField titleField  = new TextField();
        TextField authorField = new TextField();
        TextField genreField  = new TextField();
        TextField pagesField  = new TextField();

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.addRow(0, new Label("ID:"),     idField);
        form.addRow(1, new Label("Title:"),  titleField);
        form.addRow(2, new Label("Author:"), authorField);
        form.addRow(3, new Label("Genre:"),  genreField);
        form.addRow(4, new Label("Pages:"),  pagesField);

        Label message = new Label();
        message.setWrapText(true);

        Button addBtn = new Button("Add");
        addBtn.setDefaultButton(true);
        addBtn.setOnAction(e -> {
            String id     = idField.getText().trim();
            String title  = titleField.getText().trim();
            String author = authorField.getText().trim();
            String genre  = genreField.getText().trim();

            if (id.isEmpty() || title.isEmpty() || author.isEmpty()) {
                message.setText("Please fill in at least ID, Title and Author.");
                return;
            }
            int pages;
            try {
                pages = pagesField.getText().isBlank() ? 0 : Integer.parseInt(pagesField.getText().trim());
            } catch (NumberFormatException ex) {
                message.setText("Pages must be a whole number.");
                return;
            }

            LibraryItem book = LibraryItem.createBook(
                    id, title, author, genre.isEmpty() ? "General" : genre, pages);
            app.getCatalogue().add(book);  // shows up on the Dashboard table immediately
            message.setText("Added '" + title + "'. Add another, or go back to the dashboard.");

            idField.clear(); titleField.clear(); authorField.clear();
            genreField.clear(); pagesField.clear();
        });

        Button backBtn = new Button("Back to dashboard");
        backBtn.setOnAction(e -> app.showDashboard());

        HBox buttons = new HBox(10, addBtn, backBtn);
        buttons.setAlignment(Pos.CENTER_LEFT);

        VBox root = new VBox(16, heading, form, buttons, message);
        root.setPadding(new Insets(24));
        return root;
    }
}
