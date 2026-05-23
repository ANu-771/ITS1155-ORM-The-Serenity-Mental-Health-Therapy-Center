package lk.ijse.theserenitymentalhealththerapycenter.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @Column(name = "payment_id", length = 10)
    private String paymentId;

    @Column(nullable = false)
    private double amount;

    @Column(name = "payment_date", nullable = false, length = 15)
    private String paymentDate;

    @Column(name = "payment_method", length = 30)
    private String paymentMethod;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "covered_sessions", nullable = false)
    private int coveredSessions;

    @Column(name = "due_balance", nullable = false)
    private double dueBalance;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "program_id", nullable = false)
    private TherapyProgram therapyProgram;

    public Payment() {
    }

    public Payment(String paymentId, double amount, String paymentDate, String paymentMethod, String status, int coveredSessions, double dueBalance, Patient patient, TherapyProgram therapyProgram) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.coveredSessions = coveredSessions;
        this.dueBalance = dueBalance;
        this.patient = patient;
        this.therapyProgram = therapyProgram;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCoveredSessions() {
        return coveredSessions;
    }

    public void setCoveredSessions(int coveredSessions) {
        this.coveredSessions = coveredSessions;
    }

    public double getDueBalance() {
        return dueBalance;
    }

    public void setDueBalance(double dueBalance) {
        this.dueBalance = dueBalance;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public TherapyProgram getTherapyProgram() {
        return therapyProgram;
    }

    public void setTherapyProgram(TherapyProgram therapyProgram) {
        this.therapyProgram = therapyProgram;
    }
}
