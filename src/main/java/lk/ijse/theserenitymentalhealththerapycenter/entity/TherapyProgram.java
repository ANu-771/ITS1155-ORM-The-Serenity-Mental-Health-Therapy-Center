package lk.ijse.theserenitymentalhealththerapycenter.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "therapy_programs")
public class TherapyProgram {

    @Id
    @Column(name = "program_id", length = 10)
    private String programId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String duration;

    @Column(nullable = false)
    private double fee;

    @Column(name = "total_sessions", nullable = false)
    private int totalSessions;

    @Column(length = 500)
    private String description;

    @ManyToMany(mappedBy = "therapyPrograms", fetch = FetchType.LAZY)
    private List<Therapist> therapists = new ArrayList<>();

    @ManyToMany(mappedBy = "therapyPrograms", fetch = FetchType.LAZY)
    private List<Patient> patients = new ArrayList<>();

    @OneToMany(mappedBy = "therapyProgram", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TherapySession> therapySessions = new ArrayList<>();

    @OneToMany(mappedBy = "therapyProgram", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Payment> payments = new ArrayList<>();

    public TherapyProgram() {
    }

    public TherapyProgram(String programId, String name, String duration, double fee, int totalSessions, String description) {
        this.programId = programId;
        this.name = name;
        this.duration = duration;
        this.fee = fee;
        this.totalSessions = totalSessions;
        this.description = description;
    }

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public int getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(int totalSessions) {
        this.totalSessions = totalSessions;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Therapist> getTherapists() {
        return therapists;
    }

    public void setTherapists(List<Therapist> therapists) {
        this.therapists = therapists;
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public void setPatients(List<Patient> patients) {
        this.patients = patients;
    }

    public List<TherapySession> getTherapySessions() {
        return therapySessions;
    }

    public void setTherapySessions(List<TherapySession> therapySessions) {
        this.therapySessions = therapySessions;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }
}
