package lk.ijse.theserenitymentalhealththerapycenter.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @Column(length = 10)
    private String id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 15)
    private String dob;

    @Column(name = "contact_number", length = 15)
    private String contactNumber;

    @Column(length = 10)
    private String gender;

    @Column(name = "medical_history", length = 1000)
    private String medicalHistory;

    @Column(name = "registration_date", length = 15)
    private String registrationDate;

    @ManyToMany
    @JoinTable(
            name = "patient_program",
            joinColumns = @JoinColumn(name = "patient_id"),
            inverseJoinColumns = @JoinColumn(name = "program_id")
    )
    private List<TherapyProgram> therapyPrograms = new ArrayList<>();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TherapySession> therapySessions = new ArrayList<>();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Payment> payments = new ArrayList<>();

    public Patient() {
    }

    public Patient(String id, String name, String dob, String contactNumber, String gender, String medicalHistory, String registrationDate) {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.contactNumber = contactNumber;
        this.gender = gender;
        this.medicalHistory = medicalHistory;
        this.registrationDate = registrationDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public List<TherapyProgram> getTherapyPrograms() {
        return therapyPrograms;
    }

    public void setTherapyPrograms(List<TherapyProgram> therapyPrograms) {
        this.therapyPrograms = therapyPrograms;
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
