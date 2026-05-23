package lk.ijse.theserenitymentalhealththerapycenter.dto;

public class FinancialSummaryDTO {
    private double totalFee;
    private double paidAmount;
    private double dueBalance;
    private int prepaidSessionsAvailable;

    public FinancialSummaryDTO(double totalFee, double paidAmount, double dueBalance, int prepaidSessionsAvailable) {
        this.totalFee = totalFee;
        this.paidAmount = paidAmount;
        this.dueBalance = dueBalance;
        this.prepaidSessionsAvailable = prepaidSessionsAvailable;
    }

    public double getTotalFee() {
        return totalFee;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public double getDueBalance() {
        return dueBalance;
    }

    public int getPrepaidSessionsAvailable() {
        return prepaidSessionsAvailable;
    }
}
