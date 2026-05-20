package lk.ijse.theserenitymentalhealththerapycenter.dto.tm;

public class PatientTM {
    private String id;
    private String name;
    private String dob;
    private String contactNumber;
    private String email;
    private String registrationDate;

    public PatientTM() {}

    public PatientTM(String id, String name, String dob, String contactNumber, String email, String registrationDate) {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.contactNumber = contactNumber;
        this.email = email;
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
    public String getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(String registrationDate) { this.registrationDate = registrationDate; }
}
