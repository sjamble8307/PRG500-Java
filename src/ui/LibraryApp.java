package ui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import model.LibraryItem;
import model.LibraryLoader;
import model.User;

/**
 * Entry point of the JavaFX Library Management demo.
 *
 * Owns the single {@link Stage} and switches between three scenes:
 *
 *   Login  ->  Librarian Dashboard  <->  Add Book
 *
 * The library domain model lives in the {@code model} package and is reused
 * unchanged; this class loads the data once and shares it with the scenes.
 */
public class LibraryApp extends Application {

    private Stage primaryStage;

    // The catalogue is held as an ObservableList so the Dashboard table updates
    // automatically when the librarian adds a book or changes availability.
    private ObservableList<LibraryItem> catalogue;
    private User librarian;

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;

        // Reuse the existing loader and CSV files.
        var items = LibraryLoader.loadItems("data/items.csv");
        var users = LibraryLoader.loadUsers("data/users.csv", items);
        catalogue = FXCollections.observableArrayList(items);

        // This demo signs in as the librarian, so grab the one from users.csv.
        librarian = LibraryLoader.findLibrarian(users);

        stage.setTitle("Community Library");
        stage.setMinWidth(640);
        stage.setMinHeight(460);

        showLogin();
        stage.show();
    }

    // ── Scene navigation ────────────────────────────────────────────────────

    public void showLogin()     { switchTo(new LoginScene(this).build()); }
    public void showDashboard() { switchTo(new DashboardScene(this).build()); }
    public void showAddBook()   { switchTo(new AddBookScene(this).build()); }

    private void switchTo(Parent root) {
        Scene current = primaryStage.getScene();
        if (current == null) {
            primaryStage.setScene(new Scene(root, 680, 500));
        } else {
            current.setRoot(root); // keep the window size/position across navigation
        }
    }

    // ── Accessors used by the scenes ─────────────────────────────────────────

    public ObservableList<LibraryItem> getCatalogue() { return catalogue; }
    public User getLibrarian()                         { return librarian; }

    public static void main(String[] args) {
        launch(args);
    }
}
