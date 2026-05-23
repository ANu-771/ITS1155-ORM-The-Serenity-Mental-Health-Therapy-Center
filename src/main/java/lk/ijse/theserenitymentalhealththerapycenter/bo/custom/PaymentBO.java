package lk.ijse.theserenitymentalhealththerapycenter.bo.custom;

import lk.ijse.theserenitymentalhealththerapycenter.dto.PaymentDTO;

import java.util.List;

public interface PaymentBO {
    boolean savePayment(PaymentDTO paymentDTO) throws Exception;

    boolean updatePayment(PaymentDTO paymentDTO) throws Exception;

    boolean deletePayment(String id) throws Exception;

    PaymentDTO searchPayment(String id) throws Exception;

    List<PaymentDTO> getAllPayments() throws Exception;

    List<PaymentDTO> getPaymentsByPatient(String patientId) throws Exception;

    lk.ijse.theserenitymentalhealththerapycenter.dto.FinancialSummaryDTO getFinancialSummary(String patientId, String programId) throws Exception;

    String getNextId() throws Exception;
}
