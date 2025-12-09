package Controllers;

import Databases.UserDatabase;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import Models.Food;
import Models.Session;
import utils.SceneSwitcher;

import java.io.IOException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CheckoutController {

    @FXML
    private VBox orderItemsContainer;

    @FXML
    private Text itemCountText;

    @FXML
    private Text subtotalText;

    @FXML
    private Text taxText;

    @FXML
    private Text totalText;

    @FXML
    private Text userBalanceText;

    @FXML
    private Button confirmButton;

    private ArrayList<Food> orderItems;
    private NumberFormat currencyFormat;

    public void initialize() {
        currencyFormat = NumberFormat.getNumberInstance(Locale.forLanguageTag("hu-HU"));
        orderItems = new ArrayList<>();

        // Update balance display
        updateBalanceDisplay();

        showEmptyOrderMessage();
        if (confirmButton != null) {
            confirmButton.setDisable(true);
        }
    }

    //UPDATE BALANCE
    private void updateBalanceDisplay() {
        if (userBalanceText != null) {
            int balance = Session.getCurrentBalance();
            userBalanceText.setText(formatCurrency(balance));
        }
    }

    private void populateOrderItems() {
        orderItemsContainer.getChildren().clear();

        // Filter only items with quantity > 0
        ArrayList<Food> selectedItems = new ArrayList<>();
        for (Food food : orderItems) {
            if (food.getQuantity() > 0) {
                selectedItems.add(food);
            }
        }

        if (selectedItems.isEmpty()) {
            showEmptyOrderMessage();
            confirmButton.setDisable(true);
            return;
        }

        for (Food food : selectedItems) {
            HBox itemRow = createOrderItemRow(food);
            orderItemsContainer.getChildren().add(itemRow);
        }

        updateOrderSummary();
        confirmButton.setDisable(false);
    }

    private HBox createOrderItemRow(Food food) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(15, 20, 15, 20));
        row.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #e0e0e0;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 12;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 2);"
        );

        // Item name
        Text nameText = new Text(food.getName());
        nameText.setFont(Font.font("System", FontWeight.SEMI_BOLD, 16));
        nameText.setFill(Color.web("#12372A"));
        nameText.setWrappingWidth(250);

        // Spacer
        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        // Quantity controls
        HBox controls = new HBox(12);
        controls.setAlignment(Pos.CENTER);

        Button minusBtn = createControlButton("-");
        Label quantityLabel = new Label(String.valueOf(food.getQuantity()));
        quantityLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        quantityLabel.setTextFill(Color.web("#12372A"));
        quantityLabel.setMinWidth(35);
        quantityLabel.setAlignment(Pos.CENTER);

        Button plusBtn = createControlButton("+");

        // Price text that updates with quantity
        Text priceText = new Text(formatCurrency(food.getPrice() * food.getQuantity()));
        priceText.setFont(Font.font("System", FontWeight.BOLD, 18));
        priceText.setFill(Color.web("#2E8B57"));

        minusBtn.setOnAction(e -> {
            if (food.getQuantity() > 1) {
                food.setQuantity(food.getQuantity() - 1);
                quantityLabel.setText(String.valueOf(food.getQuantity()));
                priceText.setText(formatCurrency(food.getPrice() * food.getQuantity()));
                updateOrderSummary();
                animateButton(minusBtn);
            } else if (food.getQuantity() == 1) {
                // Remove item
                food.setQuantity(0);
                populateOrderItems();
            }
        });

        plusBtn.setOnAction(e -> {
            food.setQuantity(food.getQuantity() + 1);
            quantityLabel.setText(String.valueOf(food.getQuantity()));
            priceText.setText(formatCurrency(food.getPrice() * food.getQuantity()));
            updateOrderSummary();
            animateButton(plusBtn);
        });

        controls.getChildren().addAll(minusBtn, quantityLabel, plusBtn);

        // Spacer
        Region spacer2 = new Region();
        spacer2.setMinWidth(20);

        row.getChildren().addAll(nameText, spacer1, controls, spacer2, priceText);

        return row;
    }

    private Button createControlButton(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: " + (text.equals("+") ? "#2E8B57" : "#e8f5e9") + ";" +
                        "-fx-text-fill: " + (text.equals("+") ? "white" : "#2E8B57") + ";" +
                        "-fx-font-size: 16;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-min-width: 35;" +
                        "-fx-min-height: 35;" +
                        "-fx-cursor: hand;"
        );
        button.setFocusTraversable(false);

        // Hover effect
        String defaultColor = text.equals("+") ? "#2E8B57" : "#e8f5e9";
        String hoverColor = text.equals("+") ? "#25714a" : "#d4edda";

        button.setOnMouseEntered(e ->
                button.setStyle(button.getStyle().replace(defaultColor, hoverColor)));
        button.setOnMouseExited(e ->
                button.setStyle(button.getStyle().replace(hoverColor, defaultColor)));

        return button;
    }

    private void showEmptyOrderMessage() {
        VBox emptyMessage = new VBox(15);
        emptyMessage.setAlignment(Pos.CENTER);
        emptyMessage.setPadding(new Insets(50));

        Text icon = new Text("🛒");
        icon.setFont(Font.font(60));

        Text message = new Text("Your cart is empty");
        message.setFont(Font.font("System", FontWeight.BOLD, 20));
        message.setFill(Color.web("#12372A"));

        Text subMessage = new Text("Add items from the menu to get started");
        subMessage.setFont(Font.font("System", FontWeight.NORMAL, 14));
        subMessage.setFill(Color.web("#666666"));

        emptyMessage.getChildren().addAll(icon, message, subMessage);
        orderItemsContainer.getChildren().add(emptyMessage);
    }

    private void updateOrderSummary() {
        int subtotal = 0;
        int totalItems = 0;

        for (Food food : orderItems) {
            if (food.getQuantity() > 0) {
                subtotal += food.getPrice() * food.getQuantity();
                totalItems += food.getQuantity();
            }
        }

        double tax = subtotal * 0.10;
        double total = subtotal + tax;

        itemCountText.setText(totalItems + (totalItems == 1 ? " item" : " items"));
        subtotalText.setText(formatCurrency(subtotal));
        taxText.setText(formatCurrency((int) tax));
        totalText.setText(formatCurrency((int) total));
    }

    private String formatCurrency(int amount) {
        return currencyFormat.format(amount) + " Ft";
    }

    private void animateButton(Button button) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(100), button);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(0.9);
        scale.setToY(0.9);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);
        scale.play();
    }

    @FXML
    public void backToMenu(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/UI/FoodsPage.fxml");
    }

    @FXML
    public void clearOrder(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clear Order");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("This will remove all items from your cart.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                for (Food food : orderItems) {
                    food.setQuantity(0);
                }
                populateOrderItems();
                updateOrderSummary();
            }
        });
    }

    @FXML
    public void confirmOrder(ActionEvent event) {
        // Check if there are any items
        boolean hasItems = orderItems.stream().anyMatch(food -> food.getQuantity() > 0);

        if (!hasItems) {
            showAlert("Empty Order", "Please add items to your cart before confirming.", Alert.AlertType.WARNING);
            return;
        }

        // Calculate total
        int subtotal = 0;
        for (Food food : orderItems) {
            if (food.getQuantity() > 0) {
                subtotal += food.getPrice() * food.getQuantity();
            }
        }
        int total = (int) (subtotal * 1.10); // Including 10% tax

        // Check if user has sufficient balance
        int currentBalance = Session.getCurrentBalance();
        if (currentBalance < total) {
            showAlert("Insufficient Balance",
                    "You don't have enough balance to complete this order.\n\n" +
                            "Order Total: " + formatCurrency(total) + "\n" +
                            "Your Balance: " + formatCurrency(currentBalance) + "\n" +
                            "Needed: " + formatCurrency(total - currentBalance),
                    Alert.AlertType.ERROR);
            return;
        }

        // Deduct balance and update session
        int newBalance = currentBalance - total;
        Session.login(Session.getCurrentUsername(), newBalance); // Update session with new balance

        // Update balance display
        updateBalanceDisplay();

        // Update database with new balance
        try {
            UserDatabase userDb = UserDatabase.getInstance();
            userDb.updateBalanceByUsername(Session.getCurrentUsername(), newBalance);
            userDb.updateScoreByUsername(Session.getCurrentUsername(),(int)(total*0.5));
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to update balance in database.", Alert.AlertType.ERROR);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Order Confirmed");
        alert.setHeaderText("Thank you for your order!");
        alert.setContentText("Your order has been placed successfully.\n\n" +
                "Total: " + totalText.getText() + "\n" +
                "Remaining Balance: " + formatCurrency(newBalance));

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    for (Food food : orderItems) {
                        food.setQuantity(0);
                    }
                    SceneSwitcher.switchScene(event, "/UI/MainPage.fxml");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // This method receives the food list from FoodsController
    public void setOrderItems(ArrayList<Food> items) {
        this.orderItems = items;
        populateOrderItems();
    }
}