package Controllers;

import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import Models.Food;
import utils.SceneSwitcher;

import java.io.IOException;
import java.util.ArrayList;

public class FoodsController {

    @FXML
    private VBox itemsContainer;

    @FXML
    private Button checkoutButton;

    private ArrayList<Food> foodList = new ArrayList<>();
    private Label totalItemsLabel;

    public void SwitchToMain(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/UI/MainPage.fxml");
    }

    public void SwitchToCheckout(ActionEvent event) throws IOException {
        try {
            // Load the checkout page with FXMLLoader to pass data
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UI/CheckoutPage.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the food list
            CheckoutController checkoutController = loader.getController();
            checkoutController.setOrderItems(foodList);

            // Switch to the checkout scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initialize() {
        initializeFoodList();
        populateItems();
        setupCheckoutButton();
    }

    private void initializeFoodList() {
        // Burgers
        foodList.add(new Food("🍔 Classic Cheeseburger", 0, 1500));
        foodList.add(new Food("🍔 Double Bacon Burger", 0, 2000));
        foodList.add(new Food("🔥 BBQ Burger", 0, 1800));
        foodList.add(new Food("🍄 Mushroom Swiss Burger", 0, 1700));
        foodList.add(new Food("🥦 Veggie Burger", 0, 1600));
        foodList.add(new Food("🍗 Chicken Burger", 0, 1500));
        foodList.add(new Food("🌶️ Spicy Jalapeño Burger", 0, 1750));
        foodList.add(new Food("🧀 Blue Cheese Burger", 0, 1850));
        foodList.add(new Food("🥑 Guacamole Burger", 0, 1900));
        foodList.add(new Food("🍍 Hawaiian Burger", 0, 1800));

        // Sides
        foodList.add(new Food("🍟 French Fries", 0, 600));
        foodList.add(new Food("🧀 Cheese Fries", 0, 800));
        foodList.add(new Food("🧅 Onion Rings", 0, 700));
        foodList.add(new Food("🥗 Coleslaw", 0, 500));
        foodList.add(new Food("🧀 Mozzarella Sticks", 0, 900));

        // Sauces
        foodList.add(new Food("🍅 Ketchup", 0, 200));
        foodList.add(new Food("🥫 BBQ Sauce", 0, 300));
        foodList.add(new Food("🧄 Garlic Mayo", 0, 300));
        foodList.add(new Food("🌶️ Sriracha Mayo", 0, 300));
    }

    private void populateItems() {
        for (Food food : foodList) {
            HBox itemCard = createItemCard(food);
            itemsContainer.getChildren().add(itemCard);
        }
    }

    private HBox createItemCard(Food food) {
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15, 20, 15, 20));
        card.setStyle(
                "-fx-background-color: #f8f9fa;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #e0e0e0;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 12;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);"
        );

        // Item name
        Text nameText = new Text(food.getName());
        nameText.setFont(Font.font("System", FontWeight.SEMI_BOLD, 16));
        nameText.setFill(Color.web("#12372A"));
        nameText.setWrappingWidth(250);

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Quantity controls container
        HBox controls = new HBox(12);
        controls.setAlignment(Pos.CENTER);

        // Minus button
        Button minusBtn = createControlButton("-");
        minusBtn.setStyle(
                "-fx-background-color: #e8f5e9;" +
                        "-fx-text-fill: #2E8B57;" +
                        "-fx-font-size: 18;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-min-width: 40;" +
                        "-fx-min-height: 40;" +
                        "-fx-cursor: hand;"
        );

        // Quantity display
        Label quantityLabel = new Label(String.valueOf(food.getQuantity()));
        quantityLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        quantityLabel.setTextFill(Color.web("#12372A"));
        quantityLabel.setMinWidth(40);
        quantityLabel.setAlignment(Pos.CENTER);
        quantityLabel.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8;"
        );

        // Plus button
        Button plusBtn = createControlButton("+");
        plusBtn.setStyle(
                "-fx-background-color: #2E8B57;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 18;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-min-width: 40;" +
                        "-fx-min-height: 40;" +
                        "-fx-cursor: hand;"
        );

        // Button actions
        minusBtn.setOnAction(e -> {
            if (food.getQuantity() > 0) {
                food.setQuantity(food.getQuantity() - 1);
                quantityLabel.setText(String.valueOf(food.getQuantity()));
                updateTotalItems();
                animateButton(minusBtn);

                if (food.getQuantity() == 0) {
                    card.setStyle(card.getStyle().replace("#f8f9fa", "#fafafa"));
                }
            }
        });

        plusBtn.setOnAction(e -> {
            food.setQuantity(food.getQuantity() + 1);
            quantityLabel.setText(String.valueOf(food.getQuantity()));
            updateTotalItems();
            animateButton(plusBtn);

            if (food.getQuantity() == 1) {
                card.setStyle(card.getStyle().replace("#fafafa", "#f8f9fa"));
            }
        });

        // Hover effects
        addHoverEffect(minusBtn, "#d4edda", "#e8f5e9");
        addHoverEffect(plusBtn, "#25714a", "#2E8B57");

        controls.getChildren().addAll(minusBtn, quantityLabel, plusBtn);
        card.getChildren().addAll(nameText, spacer, controls);

        return card;
    }

    private Button createControlButton(String text) {
        Button button = new Button(text);
        button.setFocusTraversable(false);
        return button;
    }

    private void addHoverEffect(Button button, String hoverColor, String defaultColor) {
        button.setOnMouseEntered(e -> {
            String currentStyle = button.getStyle();
            button.setStyle(currentStyle.replace(defaultColor, hoverColor));
        });

        button.setOnMouseExited(e -> {
            String currentStyle = button.getStyle();
            button.setStyle(currentStyle.replace(hoverColor, defaultColor));
        });
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

    private void setupCheckoutButton() {
        if (checkoutButton != null) {
            totalItemsLabel = new Label("No items selected");
            totalItemsLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));
            totalItemsLabel.setTextFill(Color.web("#FFFFFF"));
            totalItemsLabel.setStyle("-fx-opacity: 0.9;");
        }
    }

    private void updateTotalItems() {
        int total = foodList.stream()
                .mapToInt(Food::getQuantity)
                .sum();

        if (totalItemsLabel != null) {
            if (total == 0) {
                totalItemsLabel.setText("No items selected");
            } else {
                totalItemsLabel.setText(total + (total == 1 ? " item selected" : " items selected"));
            }
        }

        // Update checkout button text
        if (checkoutButton != null) {
            if (total > 0) {
                checkoutButton.setText("Proceed to Checkout (" + total + ")");
                checkoutButton.setDisable(false);
            } else {
                checkoutButton.setText("Proceed to Checkout");
                checkoutButton.setDisable(true);
            }
        }
    }

    public ArrayList<Food> getSelectedItems() {
        ArrayList<Food> selected = new ArrayList<>();
        for (Food food : foodList) {
            if (food.getQuantity() > 0) {
                selected.add(food);
            }
        }
        return selected;
    }
}