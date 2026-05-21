package lk.ijse.theserenitymentalhealththerapycenter.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lk.ijse.theserenitymentalhealththerapycenter.HelloApplication;
import lk.ijse.theserenitymentalhealththerapycenter.bo.BOFactory;
import lk.ijse.theserenitymentalhealththerapycenter.bo.custom.UserBO;
import lk.ijse.theserenitymentalhealththerapycenter.dto.UserDTO;
import lk.ijse.theserenitymentalhealththerapycenter.exception.AuthenticationException;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;
    @FXML private ImageView imgLogo;

    private final UserBO userBO = BOFactory.getInstance().getBO(BOFactory.BOType.USER);

    @FXML
    public void handleLogin(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }

        try {
            UserDTO user = userBO.authenticateUser(username, password);

            Stage stage = (Stage) txtUsername.getScene().getWindow();

            String fxmlFile;
            if ("Admin".equalsIgnoreCase(user.getRole())) {
                fxmlFile = "view/AdminDashboard.fxml";
            } else {
                fxmlFile = "view/ReceptionistDashboard.fxml";
            }

            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource(fxmlFile));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Serenity - " + user.getRole() + " Dashboard");
            stage.centerOnScreen();

        } catch (AuthenticationException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Login failed. Please try again.");
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
    }
}
