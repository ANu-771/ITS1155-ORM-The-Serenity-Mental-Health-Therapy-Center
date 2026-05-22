package lk.ijse.theserenitymentalhealththerapycenter.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.theserenitymentalhealththerapycenter.HelloApplication;

import java.net.URL;
import java.util.ResourceBundle;

public class ReceptionistDashboardController implements Initializable {

    @FXML private AnchorPane contentPane;
    @FXML private Label lblHeader;
    @FXML private Label lblSubheader;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadContent("view/ReceptionistDashboardContent.fxml", "Dashboard Home", "Welcome back, Receptionist");
    }

    @FXML
    public void handleDashboard(ActionEvent event) {
        loadContent("view/ReceptionistDashboardContent.fxml", "Dashboard Home", "Welcome back, Receptionist");
    }

    @FXML
    public void handlePatients(ActionEvent event) {
        loadContent("view/PatientManagement.fxml", "Patient Management", "Register and manage patient records");
    }

    @FXML
    public void handleSessions(ActionEvent event) {
        loadContent("view/SessionScheduling.fxml", "Session Scheduling", "Book and manage therapy sessions");
    }

    @FXML
    public void handlePayments(ActionEvent event) {
        loadContent("view/PaymentManagement.fxml", "Payment Management", "Process and track payments");
    }

    @FXML
    public void handleReports(ActionEvent event) {
        loadContent("view/Reports.fxml", "Reports & Analytics", "View financial and session statistics");
    }

    @FXML
    public void handleChangeCredentials(ActionEvent event) {
        loadContent("view/ChangeCredentials.fxml", "Change Credentials", "Update your username or password");
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        try {
            lk.ijse.theserenitymentalhealththerapycenter.util.SessionContext.clear();
            Stage stage = (Stage) contentPane.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("view/Login.fxml"));
            Parent root = loader.load();
            stage.getScene().setRoot(root);
            stage.setTitle("Serenity - Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadContent(String fxmlFile, String title, String subtitle) {
        try {
            lblHeader.setText(title);
            lblSubheader.setText(subtitle);
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
