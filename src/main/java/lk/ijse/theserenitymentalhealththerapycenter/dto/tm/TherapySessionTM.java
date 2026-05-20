package lk.ijse.theserenitymentalhealththerapycenter.dto.tm;

public class TherapySessionTM {
    private String sessionId;
    private String date;
    private String time;
    private String status;
    private String patientName;
    private String therapistName;
    private String programName;

    public TherapySessionTM() {}

    public TherapySessionTM(String sessionId, String date, String time, String status, String patientName, String therapistName, String programName) {
        this.sessionId = sessionId;
        this.date = date;
        this.time = time;
        this.status = status;
        this.patientName = patientName;
        this.therapistName = therapistName;
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
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getTherapistName() { return therapistName; }
    public void setTherapistName(String therapistName) { this.therapistName = therapistName; }
    public String getProgramName() { return programName; }
    public void setProgramName(String programName) { this.programName = programName; }
}
