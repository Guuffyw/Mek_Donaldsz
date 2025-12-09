package Controllers;

import Databases.UserDatabase;
import Models.Session;
import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import javafx.util.Duration;
import utils.SceneSwitcher;
import java.io.IOException;
import java.sql.SQLException;

import Models.Session;

public class MainController {

    @FXML
    private Text UserName;
    @FXML
    private ImageView myImage;

    private int current500;
    private int current1000;
    private static String currentUsername;

    public void initialize() {
        String name = Session.getCurrentUsername();
        UserName.setText(name != null ? name : "No user logged in");
    }

    public void SwitchToLogin(ActionEvent event) throws IOException {
        if (Session.isLoggedIn()){
            SceneSwitcher.switchScene(event, "/UI/UserPage.fxml");
        }else{
            SceneSwitcher.switchScene(event, "/UI/LoginPage.fxml");
        }
    }

    public void SwitchToFoods(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/UI/FoodsPage.fxml");
    }

    public void spin() {

        if (!Session.isLoggedIn()) {
            showAlert("Error", "Log in first");
            return;
        }

        currentUsername = Session.getCurrentUsername();
        int randomNum = (int) (Math.random() * 361);

        // Spin animation
        RotateTransition spinAnim = new RotateTransition();
        spinAnim.setNode(myImage);
        spinAnim.setFromAngle(0);
        spinAnim.setToAngle(-(randomNum + 720));
        spinAnim.setDuration(Duration.seconds(3));

        spinAnim.setOnFinished(event -> {

            // Determine prize AFTER spin completes
            String title;
            String message;

            try {
                UserDatabase userDb = UserDatabase.getInstance();
                current500 = userDb.getCoupons500ByUsername(currentUsername);
                current1000 = userDb.getCoupons1000ByUsername(currentUsername);

                if ((randomNum > 135 && randomNum < 180) || (randomNum > 315 && randomNum < 360)) {
                    userDb.update500ByUsername(currentUsername, current500 + 1);
                    title = "WIN";
                    message = "YOU HAVE WON 500";

                } else if ((randomNum > 45 && randomNum < 90) || (randomNum > 225 && randomNum < 270)) {
                    userDb.update1000ByUsername(currentUsername, current1000 + 1);
                    title = "WIN";
                    message = "YOU HAVE WON 1000";

                } else {
                    title = "LOOSE";
                    message = "HAHA";
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            // Wait 3 seconds → show alert → reset wheel
            PauseTransition wait = new PauseTransition(Duration.seconds(1));
            wait.setOnFinished(e -> {
                showAlert(title, message);
                myImage.setRotate(0);
            });
            wait.play();
        });

        spinAnim.play();
    }





    private void showAlert(String title, String content) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }
}
