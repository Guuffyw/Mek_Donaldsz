package Controllers;

import Databases.UserDatabase;
import Models.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import utils.SceneSwitcher;

import java.sql.SQLException;

public class UserController {

    @FXML
    private Text usernameText;

    @FXML
    private Text pointsText;

    @FXML
    private Text rankText;

    @FXML
    private Text balanceText;

    @FXML
    private Text coupon500Text;

    @FXML
    private Text coupon1000Text;

    @FXML
    private TextField balanceInputField;

    // Current logged-in user
    private static String currentUsername;
    private String currentEmail;

    public static void setCurrentUsername(String username) {
        currentUsername = username;
    }

    @FXML
    public void initialize() {
        loadUserData();
    }

    //Loads UserData
    private void loadUserData() {
        if (currentUsername == null || currentUsername.isEmpty()) {
            showAlert("Error", "No user logged in", Alert.AlertType.ERROR);
            return;
        }

        try {
            UserDatabase userDb = UserDatabase.getInstance();

            // Get email for current username
            currentEmail = userDb.getEmailByUsername(currentUsername);

            if (currentEmail == null) {
                showAlert("Error", "User profile not found in database", Alert.AlertType.ERROR);
                return;
            }

            // Get user data using username (since we have direct methods now)
            int balance = userDb.getBalanceByUsername(currentUsername);

            // Update UI with user data
            usernameText.setText(currentUsername);
            balanceText.setText(String.format("%d Ft", balance));

            // Update Session with current balance
            Session.login(currentUsername, balance);

            int moneySpent = userDb.getMoneySpentByUsername(currentUsername);

            pointsText.setText(String.valueOf(moneySpent));

            String rank = calculateRank(moneySpent);
            rankText.setText(rank);

            int coupons500 = userDb.getCoupons500ByUsername(currentUsername);
            coupon500Text.setText(coupons500 + " available");

            int coupons1000 = userDb.getCoupons1000ByUsername(currentUsername);
            coupon1000Text.setText(coupons1000 + " available");

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load user data: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    //Calculate Rank
    private String calculateRank(int moneySpent) {
        if (moneySpent >= 50000) {
            return "Diamond";
        } else if (moneySpent >= 25000) {
            return "Platinum";
        } else if (moneySpent >= 10000) {
            return "Gold";
        } else if (moneySpent >= 5000) {
            return "Silver";
        } else if (moneySpent >= 1000) {
            return "Bronze";
        } else {
            return "Newbie";
        }
    }

    //Balance Adding
    @FXML
    private void handleAddBalance() {
        String inputText = balanceInputField.getText().trim();

        if (inputText.isEmpty()) {
            showAlert("Input Error", "Please enter an amount to add", Alert.AlertType.WARNING);
            return;
        }

        try {
            int amountToAdd = Integer.parseInt(inputText);

            if (amountToAdd <= 0) {
                showAlert("Invalid Amount", "Please enter a positive amount", Alert.AlertType.WARNING);
                return;
            }

            // Get current balance and calculate new balance
            UserDatabase userDb = UserDatabase.getInstance();
            int currentBalance = userDb.getBalanceByUsername(currentUsername);
            int newBalance = currentBalance + amountToAdd;

            // Update balance in database
            userDb.updateBalanceByUsername(currentUsername, newBalance);

            // Update Session with new balance
            Session.login(currentUsername, newBalance);

            // Reload user data to show updated balance
            loadUserData();

            // Clear input field
            balanceInputField.clear();

            showAlert("Success", String.format("%d Ft added to your balance!", amountToAdd), Alert.AlertType.INFORMATION);

        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter a valid whole number", Alert.AlertType.WARNING);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to update balance: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    //Logout Handler
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            // Clear current user and session
            currentUsername = null;
            currentEmail = null;
            Session.logout();

            // Navigate to login page
            SceneSwitcher.switchScene(event, "/UI/LoginPage.fxml");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Failed to load login page", Alert.AlertType.ERROR);
        }
    }

    //Main page
    @FXML
    private void SwitchToMain(ActionEvent event) {
        try {
            SceneSwitcher.switchScene(event, "/UI/MainPage.fxml");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Failed to load main page", Alert.AlertType.ERROR);
        }
    }

    //Alert
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
