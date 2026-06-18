package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import model.User;

/**
 * Scene 1 — Login.
 *
 * A simple form with a username field, a password field and a Login button.
 * Credentials are validated against the librarian loaded from users.csv:
 *   username = the librarian's name  (shantanu)
 *   password = the librarian's ID    (password)
 * On success we navigate to the Librarian Dashboard.
 */
public class LoginScene {

    private final LibraryApp app;

    public LoginScene(LibraryApp app) {
        this.app = app;
    }

    public Parent build() {
        Label heading = new Label("Community Library");
        heading.setFont(Font.font(26));

        Label subtitle = new Label("Librarian sign in");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(260);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(260);

        Label message = new Label();
        message.setWrapText(true);

        Button loginButton = new Button("Log in");
        loginButton.setDefaultButton(true); // also fires on Enter
        loginButton.setMaxWidth(260);

        User librarian = app.getLibrarian();
        Label hint = new Label("Hint:  " + librarian.getName() + " / " + "password");
        hint.setStyle("-fx-font-size: 11; -fx-opacity: 0.75;");

        Runnable attemptLogin = () -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                message.setText("Please enter both a username and a password.");
                return;
            }
            boolean ok = username.equalsIgnoreCase(librarian.getName())
                    && password.equalsIgnoreCase("password");
            if (!ok) {
                message.setText("Invalid credentials. Please try again.");
                passwordField.clear();
                return;
            }
            app.showDashboard();
        };

        loginButton.setOnAction(e -> attemptLogin.run());
        passwordField.setOnAction(e -> attemptLogin.run());

        VBox root = new VBox(12, heading, subtitle, usernameField, passwordField,
                loginButton, message, hint);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        return root;
    }
}
