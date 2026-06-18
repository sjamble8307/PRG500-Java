package ui;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import model.LibraryItem;

/**
 * Scene 2 — Librarian Dashboard.
 *
 * Shows the whole catalogue in a TableView (including a live "Available" status),
 * and lets the librarian:
 *   - toggle the availability of the selected item, and
 *   - go to the Add Book scene to add a new book.
 * A status line at the bottom reports the result of the last action.
 */
public class DashboardScene {

    private final LibraryApp app;
    private final Label status = new Label();

    public DashboardScene(LibraryApp app) {
        this.app = app;
    }

    public Parent build() {
        Label welcome = new Label("Welcome, " + app.getLibrarian().getName() + "!");
        welcome.setFont(Font.font(22));

        Button logoutBtn = new Button("Log out");
        logoutBtn.setOnAction(e -> app.showLogin());

        HBox topBar = new HBox(10, welcome, spacer(), logoutBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);

        TableView<LibraryItem> table = buildTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        Button toggleBtn = new Button("Update availability");
        toggleBtn.setOnAction(e -> {
            LibraryItem item = table.getSelectionModel().getSelectedItem();
            if (item == null) {
                status.setText("Please select an item first.");
                return;
            }
            item.setCheckedOut(!item.isCheckedOut());     // flip availability
            table.refresh();                              // redraw the status column
            status.setText("'" + item.getTitle() + "' is now "
                    + (item.isCheckedOut() ? "checked out." : "available."));
        });

        Button addBtn = new Button("Add book");
        addBtn.setOnAction(e -> app.showAddBook());

        HBox actions = new HBox(10, toggleBtn, addBtn);

        status.setWrapText(true);

        VBox root = new VBox(14, topBar, new Label("Catalogue"), table, actions, status);
        root.setPadding(new Insets(20));
        return root;
    }

    private TableView<LibraryItem> buildTable() {
        // Bound directly to the shared ObservableList, so books added on the
        // Add Book scene appear here automatically.
        TableView<LibraryItem> table = new TableView<>(app.getCatalogue());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<LibraryItem, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getItemId()));

        TableColumn<LibraryItem, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getTitle()));

        TableColumn<LibraryItem, String> authorCol = new TableColumn<>("Author/Creator");
        authorCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getAuthor()));

        TableColumn<LibraryItem, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getClass().getSimpleName()));

        TableColumn<LibraryItem, String> availCol = new TableColumn<>("Available");
        availCol.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().isCheckedOut() ? "No" : "Yes"));

        table.getColumns().add(idCol);
        table.getColumns().add(titleCol);
        table.getColumns().add(authorCol);
        table.getColumns().add(typeCol);
        table.getColumns().add(availCol);
        return table;
    }

    private static HBox spacer() {
        HBox s = new HBox();
        HBox.setHgrow(s, Priority.ALWAYS);
        return s;
    }
}
