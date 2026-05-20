package lk.ijse.theserenitymentalhealththerapycenter.dto;

public class TherapistDTO {
    private String id;
    private String name;
    private String specialization;
    private String contactNumber;
    private String email;

    public TherapistDTO() {}

    public TherapistDTO(String id, String name, String specialization, String contactNumber, String email) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.contactNumber = contactNumber;
        this.email = email;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
