package lk.ijse.theserenitymentalhealththerapycenter.dto;

public class PatientDTO {
    private String id;
    private String name;
    private String dob;
    private String contactNumber;
    private String email;
    private String medicalHistory;
    private String registrationDate;

    public PatientDTO() {}

    public PatientDTO(String id, String name, String dob, String contactNumber, String email, String medicalHistory, String registrationDate) {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.contactNumber = contactNumber;
        this.email = email;
        this.medicalHistory = medicalHistory;
        this.registrationDate = registrationDate;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }
    public String getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(String registrationDate) { this.registrationDate = registrationDate; }
}
