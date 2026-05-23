package lk.ijse.theserenitymentalhealththerapycenter.dto;

public class RegistrationInvoiceDTO {
    private String programName;
    private Double fee;
    private Integer totalSessions;
    private Integer coveredSessions;

    public RegistrationInvoiceDTO() {
    }

    public RegistrationInvoiceDTO(String programName, Double fee, Integer totalSessions, Integer coveredSessions) {
        this.programName = programName;
        this.fee = fee;
        this.totalSessions = totalSessions;
        this.coveredSessions = coveredSessions;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public Integer getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(Integer totalSessions) {
        this.totalSessions = totalSessions;
    }

    public Integer getCoveredSessions() {
        return coveredSessions;
    }

    public void setCoveredSessions(Integer coveredSessions) {
        this.coveredSessions = coveredSessions;
    }
}
