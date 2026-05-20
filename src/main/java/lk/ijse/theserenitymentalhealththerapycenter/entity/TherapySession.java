package lk.ijse.theserenitymentalhealththerapycenter.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "therapy_sessions")
public class TherapySession {

    @Id
    @Column(name = "session_id", length = 10)
    private String sessionId;

    @Column(nullable = false, length = 15)
    private String date;

    @Column(nullable = false, length = 10)
    private String time;

    @Column(nullable = false, length = 20)
    private String status;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "therapist_id", nullable = false)
    private Therapist therapist;

    @ManyToOne
    @JoinColumn(name = "program_id", nullable = false)
    private TherapyProgram therapyProgram;

    public TherapySession() {
    }

    public TherapySession(String sessionId, String date, String time, String status, Patient patient, Therapist therapist, TherapyProgram therapyProgram) {
        this.sessionId = sessionId;
        this.date = date;
        this.time = time;
        this.status = status;
        this.patient = patient;
        this.therapist = therapist;
        this.therapyProgram = therapyProgram;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Therapist getTherapist() {
        return therapist;
    }

    public void setTherapist(Therapist therapist) {
        this.therapist = therapist;
    }

    public TherapyProgram getTherapyProgram() {
        return therapyProgram;
    }

    public void setTherapyProgram(TherapyProgram therapyProgram) {
        this.therapyProgram = therapyProgram;
    }
}
