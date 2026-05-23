package lk.ijse.theserenitymentalhealththerapycenter.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import lk.ijse.theserenitymentalhealththerapycenter.bo.BOFactory;
import lk.ijse.theserenitymentalhealththerapycenter.bo.custom.*;
import lk.ijse.theserenitymentalhealththerapycenter.dto.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class ReceptionistDashboardContentController implements Initializable {

    @FXML
    private Label lblTodaySessions;
    @FXML
    private Label lblPendingPayments;
    @FXML
    private Label lblNewPatients;
    @FXML
    private javafx.scene.chart.BarChart<String, Number> incomeChart;

    private final TherapySessionBO sessionBO = BOFactory.getInstance().getBO(BOFactory.BOType.THERAPY_SESSION);
    private final PaymentBO paymentBO = BOFactory.getInstance().getBO(BOFactory.BOType.PAYMENT);
    private final PatientBO patientBO = BOFactory.getInstance().getBO(BOFactory.BOType.PATIENT);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadStats();
        loadChart();
    }

    private void loadChart() {
        try {
            java.util.Map<String, Double> incomeData = paymentBO.getIncomeByDate(7);
            javafx.scene.chart.XYChart.Series<String, Number> series = new javafx.scene.chart.XYChart.Series<>();
            series.setName("Income");

            for (java.util.Map.Entry<String, Double> entry : incomeData.entrySet()) {
                String label = java.time.LocalDate.parse(entry.getKey()).format(java.time.format.DateTimeFormatter.ofPattern("MMM dd"));
                series.getData().add(new javafx.scene.chart.XYChart.Data<>(label, entry.getValue()));
            }

            incomeChart.getData().clear();
            incomeChart.getData().add(series);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadStats() {
        try {
            String today = LocalDate.now().toString();

            // Today's sessions count
            List<TherapySessionDTO> allSessions = sessionBO.getAllSessions();
            long todayCount = allSessions.stream().filter(s -> today.equals(s.getDate())).count();
            lblTodaySessions.setText(String.valueOf(todayCount));

            // Pending payments
            List<PaymentDTO> payments = paymentBO.getAllPayments();
            long pending = payments.stream().filter(p -> "Pending".equalsIgnoreCase(p.getStatus())).count();
            lblPendingPayments.setText(String.valueOf(pending));

            // Total patients
            lblNewPatients.setText(String.valueOf(patientBO.getAllPatients().size()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
