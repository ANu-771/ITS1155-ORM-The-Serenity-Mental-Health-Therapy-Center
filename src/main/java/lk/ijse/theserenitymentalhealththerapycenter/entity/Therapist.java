package lk.ijse.theserenitymentalhealththerapycenter.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "therapists")
public class Therapist {

    @Id
    @Column(length = 10)
    private String id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String specialization;

    @Column(name = "contact_number", length = 15)
    private String contactNumber;

    @Column(length = 100)
    private String email;

    @ManyToMany
    @JoinTable(
            name = "therapist_program",
            joinColumns = @JoinColumn(name = "therapist_id"),
            inverseJoinColumns = @JoinColumn(name = "program_id")
    )
    private List<TherapyProgram> therapyPrograms = new ArrayList<>();

    @OneToMany(mappedBy = "therapist", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TherapySession> therapySessions = new ArrayList<>();

    public Therapist() {
    }

    public Therapist(String id, String name, String specialization, String contactNumber, String email) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.contactNumber = contactNumber;
        this.email = email;
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

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
}
