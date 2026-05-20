package lk.ijse.theserenitymentalhealththerapycenter.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.theserenitymentalhealththerapycenter.HelloApplication;

public class AdminDashboardController {

    @FXML private AnchorPane contentPane;
    @FXML private Label lblHeader;

    @FXML
    public void handleDashboard(ActionEvent event) {
        lblHeader.setText("Dashboard");
        contentPane.getChildren().clear();
    }

    @FXML
    public void handleUserManagement(ActionEvent event) {
        loadContent("view/UserManagement.fxml", "User Management");
    }

    @FXML
    public void handleTherapists(ActionEvent event) {
        loadContent("view/TherapistManagement.fxml", "Therapist Management");
    }

    @FXML
    public void handlePrograms(ActionEvent event) {
        loadContent("view/TherapyProgramManagement.fxml", "Therapy Programs");
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        try {
            Stage stage = (Stage) contentPane.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("view/Login.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Serenity - Login");
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadContent(String fxmlFile, String title) {
        try {
            lblHeader.setText(title);
            Node node = FXMLLoader.load(HelloApplication.class.getResource(fxmlFile));
            contentPane.getChildren().clear();
            contentPane.getChildren().add(node);
            AnchorPane.setTopAnchor(node, 0.0);
            AnchorPane.setBottomAnchor(node, 0.0);
            AnchorPane.setLeftAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
