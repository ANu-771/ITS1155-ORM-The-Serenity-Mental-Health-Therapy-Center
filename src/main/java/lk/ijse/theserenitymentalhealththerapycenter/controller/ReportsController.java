package lk.ijse.theserenitymentalhealththerapycenter.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import lk.ijse.theserenitymentalhealththerapycenter.bo.BOFactory;
import lk.ijse.theserenitymentalhealththerapycenter.bo.custom.*;
import lk.ijse.theserenitymentalhealththerapycenter.util.JasperReportUtil;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ReportsController implements Initializable {

    @FXML
    private Label lblTotalPatients, lblTotalSessions, lblTotalRevenue, lblTotalTherapists;
    @FXML
    private DatePicker dpFromDate, dpToDate;
    @FXML
    private ComboBox<String> cmbReportType;
    @FXML
    private TableView<?> tblReport;

    private final PatientBO patientBO = BOFactory.getInstance().getBO(BOFactory.BOType.PATIENT);
    private final TherapySessionBO sessionBO = BOFactory.getInstance().getBO(BOFactory.BOType.THERAPY_SESSION);
    private final PaymentBO paymentBO = BOFactory.getInstance().getBO(BOFactory.BOType.PAYMENT);
    private final TherapistBO therapistBO = BOFactory.getInstance().getBO(BOFactory.BOType.THERAPIST);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbReportType.setItems(FXCollections.observableArrayList(
                "Financial Summary", "Therapy Session Statistics", "Patient Enrollment Report", "Therapist Performance"
        ));
        loadStats();
    }

    private void loadStats() {
        try {
            lblTotalPatients.setText(String.valueOf(patientBO.getAllPatients().size()));
            lblTotalSessions.setText(String.valueOf(sessionBO.getAllSessions().size()));
            lblTotalTherapists.setText(String.valueOf(therapistBO.getAllTherapists().size()));

            double totalRevenue = paymentBO.getAllPayments().stream()
                    .mapToDouble(p -> p.getAmount()).sum();
            lblTotalRevenue.setText(String.format("LKR %,.0f", totalRevenue));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleGenerate(ActionEvent event) {
        String type = cmbReportType.getValue();
        if (type == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a report type").showAndWait();
            return;
        }

        Map<String, Object> params = new HashMap<>();
        if (dpFromDate.getValue() != null) {
            params.put("fromDate", dpFromDate.getValue().toString());
        }
        if (dpToDate.getValue() != null) {
            params.put("toDate", dpToDate.getValue().toString());
        }

        String jrxmlPath;
        switch (type) {
            case "Financial Summary":
                jrxmlPath = "/reports/FinancialSummary.jrxml";
                break;
            case "Therapy Session Statistics":
                jrxmlPath = "/reports/SessionStatistics.jrxml";
                break;
            case "Patient Enrollment Report":
                jrxmlPath = "/reports/PatientEnrollment.jrxml";
                break;
            case "Therapist Performance":
                jrxmlPath = "/reports/TherapistPerformance.jrxml";
                break;
            default:
                new Alert(Alert.AlertType.WARNING, "Unknown report type").showAndWait();
                return;
        }

        JasperReportUtil.generateReport(jrxmlPath, params);
    }
}
