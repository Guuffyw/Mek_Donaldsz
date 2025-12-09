package Controllers;

import Models.Session;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import utils.SceneSwitcher;
import java.io.IOException;
import Models.Session;

public class MainController {

    @FXML
    private Text UserName;

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
}
