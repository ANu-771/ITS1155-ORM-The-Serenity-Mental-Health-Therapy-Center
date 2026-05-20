package lk.ijse.theserenitymentalhealththerapycenter.dto;

public class TherapySessionDTO {
    private String sessionId;
    private String date;
    private String time;
    private String status;
    private String patientId;
    private String patientName;
    private String therapistId;
    private String therapistName;
    private String programId;
    private String programName;

    public TherapySessionDTO() {}

    public TherapySessionDTO(String sessionId, String date, String time, String status, String patientId, String patientName, String therapistId, String therapistName, String programId, String programName) {
        this.sessionId = sessionId;
        this.date = date;
        this.time = time;
        this.status = status;
        this.patientId = patientId;
        this.patientName = patientName;
        this.therapistId = therapistId;
        this.therapistName = therapistName;
        this.programId = programId;
        this.programName = programName;
    }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getTherapistId() { return therapistId; }
    public void setTherapistId(String therapistId) { this.therapistId = therapistId; }
    public String getTherapistName() { return therapistName; }
    public void setTherapistName(String therapistName) { this.therapistName = therapistName; }
    public String getProgramId() { return programId; }
    public void setProgramId(String programId) { this.programId = programId; }
    public String getProgramName() { return programName; }
    public void setProgramName(String programName) { this.programName = programName; }
}
