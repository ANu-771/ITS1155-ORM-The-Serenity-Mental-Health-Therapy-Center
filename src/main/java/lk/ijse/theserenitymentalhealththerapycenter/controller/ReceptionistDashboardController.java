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

public class ReceptionistDashboardController {

    @FXML private AnchorPane contentPane;
    @FXML private Label lblHeader;

    @FXML
    public void handleDashboard(ActionEvent event) {
        lblHeader.setText("Dashboard");
        contentPane.getChildren().clear();
    }

    @FXML
    public void handlePatients(ActionEvent event) {
        loadContent("view/PatientManagement.fxml", "Patient Management");
    }

    @FXML
    public void handleSessions(ActionEvent event) {
        loadContent("view/SessionManagement.fxml", "Session Management");
    }

    @FXML
    public void handlePayments(ActionEvent event) {
        loadContent("view/PaymentManagement.fxml", "Payment Management");
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
