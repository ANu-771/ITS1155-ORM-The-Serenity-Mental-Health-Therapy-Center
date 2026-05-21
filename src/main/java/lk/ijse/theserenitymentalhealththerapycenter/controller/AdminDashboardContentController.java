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
import lk.ijse.theserenitymentalhealththerapycenter.dto.tm.RecentActivityTM;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AdminDashboardContentController implements Initializable {

    @FXML private Label lblTotalPatients;
    @FXML private Label lblActiveTherapists;
    @FXML private Label lblTotalPrograms;
    @FXML private Label lblTotalRevenue;
    @FXML private TableView<RecentActivityTM> tblRecentActivity;

    private final PatientBO patientBO = BOFactory.getInstance().getBO(BOFactory.BOType.PATIENT);
    private final TherapistBO therapistBO = BOFactory.getInstance().getBO(BOFactory.BOType.THERAPIST);
    private final TherapyProgramBO programBO = BOFactory.getInstance().getBO(BOFactory.BOType.THERAPY_PROGRAM);
    private final PaymentBO paymentBO = BOFactory.getInstance().getBO(BOFactory.BOType.PAYMENT);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadStats();
        loadRecentActivity();
    }

    private void loadStats() {
        try {
            lblTotalPatients.setText(String.valueOf(patientBO.getAllPatients().size()));
            lblActiveTherapists.setText(String.valueOf(therapistBO.getAllTherapists().size()));
            lblTotalPrograms.setText(String.valueOf(programBO.getAllPrograms().size()));

            double total = paymentBO.getAllPayments().stream().mapToDouble(PaymentDTO::getAmount).sum();
            lblTotalRevenue.setText(String.format("LKR %,.0f", total));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRecentActivity() {
        try {
            ObservableList<RecentActivityTM> list = FXCollections.observableArrayList();

            // Load recent therapists
            List<TherapistDTO> therapists = therapistBO.getAllTherapists();
            for (TherapistDTO t : therapists) {
                list.add(new RecentActivityTM(t.getId(), t.getName(), "Therapist", t.getSpecialization()));
            }

            // Load recent programs
            List<TherapyProgramDTO> programs = programBO.getAllPrograms();
            for (TherapyProgramDTO p : programs) {
                list.add(new RecentActivityTM(p.getProgramId(), p.getName(), "Program", p.getDuration()));
            }

            tblRecentActivity.setItems(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
