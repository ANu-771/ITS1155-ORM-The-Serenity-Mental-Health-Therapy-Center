package lk.ijse.theserenitymentalhealththerapycenter.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;
import lk.ijse.theserenitymentalhealththerapycenter.bo.BOFactory;
import lk.ijse.theserenitymentalhealththerapycenter.bo.custom.*;
import lk.ijse.theserenitymentalhealththerapycenter.dto.*;
import lk.ijse.theserenitymentalhealththerapycenter.dto.tm.TherapySessionTM;
import lk.ijse.theserenitymentalhealththerapycenter.exception.InvalidInputException;
import lk.ijse.theserenitymentalhealththerapycenter.exception.PaymentRequiredException;
import lk.ijse.theserenitymentalhealththerapycenter.exception.SchedulingConflictException;
import lk.ijse.theserenitymentalhealththerapycenter.util.ValidationUtil;
import javafx.application.Platform;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class SessionManagementController implements Initializable {

    @FXML
    private ComboBox<String> cmbPatient, cmbProgram, cmbTherapist, cmbStatus, cmbTime, cmbPaymentMethod;
    @FXML
    private TextField txtSessionId, txtPaymentAmount;
    @FXML
    private DatePicker dpDate;
    @FXML
    private Label lblTotalFee, lblPaidAmount, lblDueBalance, lblPrepaidSessions;
    @FXML
    private Button btnProcessPayment, btnScheduleSession;
    @FXML
    private TableView<TherapySessionTM> tblSessions;

    private final TherapySessionBO sessionBO = BOFactory.getInstance().getBO(BOFactory.BOType.THERAPY_SESSION);
    private final PatientBO patientBO = BOFactory.getInstance().getBO(BOFactory.BOType.PATIENT);
    private final TherapistBO therapistBO = BOFactory.getInstance().getBO(BOFactory.BOType.THERAPIST);
    private final TherapyProgramBO programBO = BOFactory.getInstance().getBO(BOFactory.BOType.THERAPY_PROGRAM);
    private final PaymentBO paymentBO = BOFactory.getInstance().getBO(BOFactory.BOType.PAYMENT);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbStatus.setItems(FXCollections.observableArrayList("SCHEDULED", "COMPLETED", "CANCELLED"));
        cmbTime.setItems(FXCollections.observableArrayList("08:00 AM", "09:00 AM", "10:00 AM", "11:00 AM", "12:00 PM",
                "01:00 PM", "02:00 PM", "03:00 PM", "04:00 PM", "05:00 PM"));
        cmbPaymentMethod.setItems(FXCollections.observableArrayList("Cash", "Card", "Bank Transfer"));

        dpDate.setDayCellFactory(getDisablePastDatesCellFactory());
        txtSessionId.setEditable(false);

        generateNextId();
        loadPatientsAndTherapists();
        loadTable();

        cmbPatient.valueProperty().addListener((obs, oldVal, newVal) -> {
            cmbProgram.getItems().clear();
            clearFinancialSummary();
            if (newVal != null) {
                String patientId = extractId(newVal);
                loadProgramsForPatient(patientId);
            }
        });

        cmbProgram.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && cmbPatient.getValue() != null) {
                String patientId = extractId(cmbPatient.getValue());
                String programId = extractId(newVal);
                updateFinancialSummary(patientId, programId);
            } else {
                clearFinancialSummary();
            }
        });

        tblSessions.getSelectionModel().selectedItemProperty().addListener((obs, old, nw) -> {
            if (nw != null) {
                txtSessionId.setText(nw.getSessionId());
                dpDate.setValue(LocalDate.parse(nw.getDate()));
                cmbTime.setValue(nw.getTime());
                cmbStatus.setValue(nw.getStatus());
            }
        });
    }

    private void loadPatientsAndTherapists() {
        try {
            List<PatientDTO> patients = patientBO.getAllPatients();
            ObservableList<String> pList = FXCollections.observableArrayList();
            for (PatientDTO p : patients)
                pList.add(p.getId() + " - " + p.getName());
            cmbPatient.setItems(pList);

            List<TherapistDTO> therapists = therapistBO.getAllTherapists();
            ObservableList<String> tList = FXCollections.observableArrayList();
            for (TherapistDTO t : therapists)
                tList.add(t.getId() + " - " + t.getName());
            cmbTherapist.setItems(tList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadProgramsForPatient(String patientId) {
        javafx.concurrent.Task<List<TherapyProgramDTO>> task = new javafx.concurrent.Task<>() {
            @Override
            protected List<TherapyProgramDTO> call() throws Exception {
                return programBO.getProgramsByPatient(patientId);
            }
        };

        task.setOnSucceeded(event -> {
            ObservableList<String> prList = FXCollections.observableArrayList();
            for (TherapyProgramDTO p : task.getValue()) {
                prList.add(p.getProgramId() + " - " + p.getName());
            }
            cmbProgram.setItems(prList);
        });

        task.setOnFailed(event -> task.getException().printStackTrace());
        new Thread(task).start();
    }

    private void updateFinancialSummary(String patientId, String programId) {
        javafx.concurrent.Task<FinancialSummaryDTO> task = new javafx.concurrent.Task<>() {
            @Override
            protected FinancialSummaryDTO call() throws Exception {
                return paymentBO.getFinancialSummary(patientId, programId);
            }
        };

        task.setOnSucceeded(event -> {
            FinancialSummaryDTO summary = task.getValue();
            lblTotalFee.setText(String.format("%.2f", summary.getTotalFee()));
            lblPaidAmount.setText(String.format("%.2f", summary.getPaidAmount()));
            lblDueBalance.setText(String.format("%.2f", summary.getDueBalance()));
            lblPrepaidSessions.setText(String.valueOf(summary.getPrepaidSessionsAvailable()));

            if (summary.getDueBalance() > 0) {
                lblDueBalance.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            } else {
                lblDueBalance.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
            }
        });

        task.setOnFailed(event -> task.getException().printStackTrace());
        new Thread(task).start();
    }

    private void clearFinancialSummary() {
        lblTotalFee.setText("0.00");
        lblPaidAmount.setText("0.00");
        lblDueBalance.setText("0.00");
        lblPrepaidSessions.setText("0");
        lblDueBalance.setStyle("");
    }

    @FXML
    void handleProcessPayment(ActionEvent event) {
        if (!ValidationUtil.validateRequired(cmbPatient) || !ValidationUtil.validateRequired(cmbProgram)) {
            new Alert(Alert.AlertType.WARNING, "Please select a Patient and a Program.").showAndWait();
            return;
        }
        if (!ValidationUtil.validateRequired(txtPaymentAmount) || !ValidationUtil.validateRequired(cmbPaymentMethod)) {
            ValidationUtil.showRequiredFieldsError();
            return;
        }

        try {
            double amount = Double.parseDouble(txtPaymentAmount.getText().trim());
            if (amount <= 0) {
                new Alert(Alert.AlertType.WARNING, "Payment amount must be greater than zero.").showAndWait();
                return;
            }

            String patientId = extractId(cmbPatient.getValue());
            String programId = extractId(cmbProgram.getValue());
            String paymentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String nextPaymentId = paymentBO.getNextId();

            FinancialSummaryDTO currentSummary = paymentBO.getFinancialSummary(patientId, programId);

            // Note: coveredSessions here should be calculated.
            // In the previous payment system, this logic was needed.
            // We'll approximate: 1 payment covers equivalent fraction of sessions.
            // Wait, actually, let's just make it simple: assuming each payment covers
            // proportional sessions
            // or we'll just set it to total sessions if paying full.
            // But to avoid complex math in controller, we'll just set covered sessions to 1
            // for this POS demo unless they pay in full.

            // Let's implement simple proportional covered sessions logic:
            double programFee = currentSummary.getTotalFee();
            int totalSessions = Integer.parseInt(lblPrepaidSessions.getText()); // this is available, but wait, program
                                                                                // total sessions is better.

            // It's safer to get the program total sessions.
            TherapyProgramDTO program = programBO.searchProgram(programId);
            int coveredSessions = (int) Math.floor((amount / program.getFee()) * program.getTotalSessions());
            if (coveredSessions <= 0 && amount > 0)
                coveredSessions = 1;

            double dueBalance = currentSummary.getDueBalance() - amount;
            if (dueBalance < 0)
                dueBalance = 0;

            PaymentDTO paymentDTO = new PaymentDTO(nextPaymentId, amount, paymentDate, cmbPaymentMethod.getValue(),
                    "COMPLETED", coveredSessions, dueBalance, patientId, null, programId, null);

            paymentBO.savePayment(paymentDTO);

            new Alert(Alert.AlertType.INFORMATION, "Payment Processed Successfully!").showAndWait();
            txtPaymentAmount.clear();
            cmbPaymentMethod.setValue(null);

            updateFinancialSummary(patientId, programId);

        } catch (NumberFormatException ex) {
            new Alert(Alert.AlertType.WARNING, "Invalid payment amount.").showAndWait();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).showAndWait();
        }
    }

    @FXML
    void handleSave(ActionEvent e) {
        if (!ValidationUtil.validateRequired(cmbPatient) || !ValidationUtil.validateRequired(cmbProgram)
                || !ValidationUtil.validateRequired(cmbTherapist)) {
            new Alert(Alert.AlertType.WARNING, "Please select Patient, Program, and Therapist.").showAndWait();
            return;
        }
        if (dpDate.getValue() == null) {
            dpDate.setStyle("-fx-border-color: #e74c3c;");
            return;
        }
        if (cmbTime.getValue() == null || cmbStatus.getValue() == null) {
            ValidationUtil.showRequiredFieldsError();
            return;
        }

        try {
            String dateStr = dpDate.getValue().toString();
            String timeStr = cmbTime.getValue();

            TherapySessionDTO dto = new TherapySessionDTO(
                    txtSessionId.getText().trim(), dateStr, timeStr, cmbStatus.getValue(),
                    extractId(cmbPatient.getValue()), null,
                    extractId(cmbTherapist.getValue()), null,
                    extractId(cmbProgram.getValue()), null);

            sessionBO.saveSession(dto);
            new Alert(Alert.AlertType.INFORMATION, "Session saved successfully!").showAndWait();
            loadTable();
            updateFinancialSummary(extractId(cmbPatient.getValue()), extractId(cmbProgram.getValue()));
            handleClear(null);
            generateNextId();

        } catch (PaymentRequiredException ex) {
            new Alert(Alert.AlertType.WARNING, ex.getMessage()).showAndWait();
        } catch (SchedulingConflictException ex) {
            new Alert(Alert.AlertType.ERROR, "Scheduling Conflict: " + ex.getMessage()).showAndWait();
        } catch (InvalidInputException ex) {
            new Alert(Alert.AlertType.WARNING, ex.getMessage()).showAndWait();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).showAndWait();
        }
    }

    @FXML
    void handleUpdate(ActionEvent e) {
        try {
            String dateStr = dpDate.getValue().toString();
            String timeStr = cmbTime.getValue();
            TherapySessionDTO dto = new TherapySessionDTO(
                    txtSessionId.getText().trim(), dateStr, timeStr, cmbStatus.getValue(),
                    extractId(cmbPatient.getValue()), null,
                    extractId(cmbTherapist.getValue()), null,
                    extractId(cmbProgram.getValue()), null);

            sessionBO.updateSession(dto);
            new Alert(Alert.AlertType.INFORMATION, "Session updated successfully!").showAndWait();
            loadTable();
            handleClear(null);
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).showAndWait();
        }
    }

    @FXML
    void handleDelete(ActionEvent e) {
        String id = txtSessionId.getText();
        if (id == null || id.trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please select a session to delete.").showAndWait();
            return;
        }
        if (!ValidationUtil.confirmDelete())
            return;
        try {
            sessionBO.deleteSession(id.trim());
            new Alert(Alert.AlertType.INFORMATION, "Session deleted successfully!").showAndWait();
            loadTable();
            handleClear(null);
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
        }
    }

    @FXML
    void handleClear(ActionEvent e) {
        txtSessionId.clear();
        dpDate.setValue(null);
        cmbTime.setValue(null);
        cmbStatus.setValue(null);
        cmbPatient.setValue(null);
        cmbTherapist.setValue(null);
        cmbProgram.setValue(null);
        dpDate.setStyle("");
        cmbTime.setStyle("");
        ValidationUtil.resetStyles(txtSessionId);
        generateNextId();
    }

    private void loadTable() {
        try {
            List<TherapySessionDTO> all = sessionBO.getAllSessions();
            ObservableList<TherapySessionTM> list = FXCollections.observableArrayList();
            for (TherapySessionDTO s : all) {
                list.add(new TherapySessionTM(s.getSessionId(), s.getDate(), s.getTime(), s.getStatus(),
                        s.getPatientName(), s.getTherapistName(), s.getProgramName()));
            }
            tblSessions.setItems(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String extractId(String comboVal) {
        return comboVal != null ? comboVal.split(" - ")[0] : null;
    }

    private void generateNextId() {
        try {
            txtSessionId.setText(sessionBO.getNextId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Callback<DatePicker, DateCell> getDisablePastDatesCellFactory() {
        return datePicker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #e9ecef; -fx-text-fill: #adb5bd;");
                }
            }
        };
    }
}
