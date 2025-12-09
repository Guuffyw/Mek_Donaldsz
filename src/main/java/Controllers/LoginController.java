package Controllers;

import Databases.AuthDatabase;
import Databases.UserDatabase;
import Models.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import utils.SceneSwitcher;

import java.io.IOException;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private AuthDatabase authDb;

    @FXML
    public void initialize() {
        try {
            authDb = new AuthDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to initialize databases.");
        }
    }

    @FXML
    public void SwitchToMain(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event,"/UI/MainPage.fxml");
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        try {
            String email = emailField.getText().trim();
            String password = passwordField.getText();

            if (email.isEmpty() || password.isEmpty()) {
                showAlert("Error", "Please fill in both fields.");
                return;
            }

            String passwordHash = Integer.toString(password.hashCode());

            if (!authDb.emailExists(email)) {
                showAlert("Login Failed", "No account found.");
                return;
            }

            if (authDb.validateLogin(email, passwordHash)) {
                // GET USERNAME AND BALANCE using singleton
                UserDatabase userDb = UserDatabase.getInstance();
                String username = userDb.getUsernameByEmail(email);
                int balance = userDb.getBalanceByEmail(email);

                // STORE USERNAME IN SESSION
                Session.login(username, balance);

                // Pass username to UserController
                UserController.setCurrentUsername(username);

                SceneSwitcher.switchScene(event, "/UI/UserPage.fxml");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Unexpected error: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        try {
            String email = emailField.getText().trim();
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                showAlert("Error", "Please fill in all fields.");
                return;
            }

            if (authDb.emailExists(email)) {
                showAlert("Already Registered", "This email is already in use.");
                return;
            }

            String passwordHash = Integer.toString(password.hashCode());

            // Add user to authentication DB
            authDb.registerUser(email, passwordHash);

            // Create profile in user DB using singleton
            UserDatabase userDb = UserDatabase.getInstance();
            userDb.createProfile(email, username);

            showAlert("Success", "Account created successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Unexpected error: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }
}
