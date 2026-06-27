package ui;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import model.CoverFetcher;
import model.LibraryItem;

import java.io.ByteArrayInputStream;

/**
 * Scene 2 — Librarian Dashboard.
 *
 * Shows the whole catalogue in a TableView (including a live "Available" status),
 * and lets the librarian:
 *   - toggle the availability of the selected item, and
 *   - go to the Add Book scene to add a new book.
 *
 * A cover-art panel on the right fetches book covers from the Open Library API
 * using the Gson Maven dependency to parse the JSON response.
 */
public class DashboardScene {

    private final LibraryApp app;
    private final Label status = new Label();

    // ── Cover panel widgets ───────────────────────────────────────────────────
    private final ImageView         coverView  = new ImageView();
    private final ProgressIndicator spinner    = new ProgressIndicator();
    private final Label             coverLabel = new Label("Select an item\nto load its cover");

    /** Tracks the title of the last requested fetch to discard stale results. */
    private volatile String currentCoverTitle = "";

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
            item.setCheckedOut(!item.isCheckedOut());
            table.refresh();
            status.setText("'" + item.getTitle() + "' is now "
                    + (item.isCheckedOut() ? "checked out." : "available."));
            app.saveCatalogue();   // persist the change to items.csv
        });

        Button addBtn = new Button("Add item");
        addBtn.setOnAction(e -> app.showAddItem());

        HBox actions = new HBox(10, toggleBtn, addBtn);
        status.setWrapText(true);

        VBox leftPane = new VBox(14, topBar, new Label("Catalogue"), table, actions, status);
        VBox.setVgrow(leftPane, Priority.ALWAYS);
        HBox.setHgrow(leftPane, Priority.ALWAYS);

        // ── Cover panel (right side) ─────────────────────────────────────────
        coverView.setFitWidth(160);
        coverView.setFitHeight(220);
        coverView.setPreserveRatio(true);
        coverView.setSmooth(true);

        spinner.setVisible(false);
        spinner.setMaxSize(50, 50);

        coverLabel.setWrapText(true);
        coverLabel.setTextAlignment(TextAlignment.CENTER);
        coverLabel.setStyle("-fx-text-fill: #888;");

        StackPane coverStack = new StackPane(coverView, spinner, coverLabel);
        coverStack.setAlignment(Pos.CENTER);
        coverStack.setPrefWidth(180);
        coverStack.setStyle(
                "-fx-border-color: #ccc; -fx-border-radius: 6;" +
                "-fx-background-color: #f9f9f9; -fx-background-radius: 6;");

        Label coverHeading = new Label("Cover Art");
        coverHeading.setFont(Font.font(14));

        Label poweredBy = new Label("Books: Open Library\nDVDs: TMDb");
        poweredBy.setStyle("-fx-text-fill: #aaa; -fx-font-size: 10;");
        poweredBy.setTextAlignment(TextAlignment.CENTER);

        VBox rightPane = new VBox(10, coverHeading, coverStack, poweredBy);
        rightPane.setAlignment(Pos.TOP_CENTER);
        rightPane.setPadding(new Insets(0, 0, 0, 14));

        // When the user selects a row, kick off an async cover fetch
        table.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldItem, newItem) -> {
                    if (newItem != null) loadCoverAsync(
                            newItem.getTitle(),
                            newItem.getClass().getSimpleName());
                });

        HBox root = new HBox(14, leftPane, rightPane);
        root.setPadding(new Insets(20));
        return root;
    }

    /**
     * Spawns a thread that calls {@link CoverFetcher} (Gson-powered),
     * then delivers the result back to the JavaFX Application Thread.
     * DVDs are routed to TMDb; all other types use Open Library.
     */
    private void loadCoverAsync(String title, String itemType) {
        currentCoverTitle = title;
        coverView.setImage(null);
        coverLabel.setVisible(false);
        spinner.setVisible(true);

        Thread worker = new Thread(() -> {
            byte[] bytes = CoverFetcher.fetchCoverBytes(title, itemType);

            Platform.runLater(() -> {
                if (!title.equals(currentCoverTitle)) return; // discard stale result
                spinner.setVisible(false);
                if (bytes != null) {
                    coverView.setImage(new Image(new ByteArrayInputStream(bytes)));
                    coverLabel.setVisible(false);
                } else {
                    coverView.setImage(null);
                    coverLabel.setText("No cover found\nfor \u201c" + title + "\u201d");
                    coverLabel.setVisible(true);
                }
            });
        });
        worker.setDaemon(true);
        worker.setName("cover-fetch-" + title);
        worker.start();
    }

    private TableView<LibraryItem> buildTable() {
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
