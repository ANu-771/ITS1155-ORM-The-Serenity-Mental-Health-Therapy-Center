package lk.ijse.theserenitymentalhealththerapycenter.bo.custom.impl;

import lk.ijse.theserenitymentalhealththerapycenter.bo.custom.PaymentBO;
import lk.ijse.theserenitymentalhealththerapycenter.dao.DAOFactory;
import lk.ijse.theserenitymentalhealththerapycenter.dao.custom.PaymentDAO;
import lk.ijse.theserenitymentalhealththerapycenter.dao.custom.PatientDAO;
import lk.ijse.theserenitymentalhealththerapycenter.dao.custom.TherapyProgramDAO;
import lk.ijse.theserenitymentalhealththerapycenter.dto.PaymentDTO;
import lk.ijse.theserenitymentalhealththerapycenter.entity.*;
import lk.ijse.theserenitymentalhealththerapycenter.exception.InvalidInputException;
import lk.ijse.theserenitymentalhealththerapycenter.exception.PaymentException;

import java.util.ArrayList;
import java.util.List;

public class PaymentBOImpl implements PaymentBO {
    private final PaymentDAO paymentDAO = DAOFactory.getInstance().getDAO(DAOFactory.DAOType.PAYMENT);
    private final PatientDAO patientDAO = DAOFactory.getInstance().getDAO(DAOFactory.DAOType.PATIENT);
    private final TherapyProgramDAO programDAO = DAOFactory.getInstance().getDAO(DAOFactory.DAOType.THERAPY_PROGRAM);
    private final lk.ijse.theserenitymentalhealththerapycenter.dao.custom.TherapySessionDAO sessionDAO = DAOFactory.getInstance().getDAO(DAOFactory.DAOType.THERAPY_SESSION);

    @Override
    public boolean savePayment(PaymentDTO dto) throws Exception {
        if (dto.getAmount() <= 0) throw new PaymentException("Amount must be greater than zero");
        if (dto.getPatientId() == null) throw new InvalidInputException("Patient is required");
        if (dto.getProgramId() == null) throw new InvalidInputException("Program is required");

        Patient patient = patientDAO.search(dto.getPatientId());
        TherapyProgram program = programDAO.search(dto.getProgramId());
        Payment payment = new Payment(dto.getPaymentId(), dto.getAmount(), dto.getPaymentDate(), dto.getPaymentMethod(), dto.getStatus(), dto.getCoveredSessions(), dto.getDueBalance(), patient, program);
        return paymentDAO.save(payment);
    }

    @Override
    public boolean updatePayment(PaymentDTO dto) throws Exception {
        Patient patient = patientDAO.search(dto.getPatientId());
        TherapyProgram program = programDAO.search(dto.getProgramId());
        Payment payment = new Payment(dto.getPaymentId(), dto.getAmount(), dto.getPaymentDate(), dto.getPaymentMethod(), dto.getStatus(), dto.getCoveredSessions(), dto.getDueBalance(), patient, program);
        return paymentDAO.update(payment);
    }

    @Override
    public boolean deletePayment(String id) throws Exception {
        return paymentDAO.delete(id);
    }

    @Override
    public PaymentDTO searchPayment(String id) throws Exception {
        Payment p = paymentDAO.search(id);
        if (p == null) return null;
        return new PaymentDTO(p.getPaymentId(), p.getAmount(), p.getPaymentDate(), p.getPaymentMethod(), p.getStatus(), p.getCoveredSessions(), p.getDueBalance(), p.getPatient().getId(), p.getPatient().getName(), p.getTherapyProgram().getProgramId(), p.getTherapyProgram().getName());
    }

    @Override
    public List<PaymentDTO> getAllPayments() throws Exception {
        List<Payment> payments = paymentDAO.getAll();
        List<PaymentDTO> dtos = new ArrayList<>();
        for (Payment p : payments) {
            dtos.add(new PaymentDTO(p.getPaymentId(), p.getAmount(), p.getPaymentDate(), p.getPaymentMethod(), p.getStatus(), p.getCoveredSessions(), p.getDueBalance(), p.getPatient().getId(), p.getPatient().getName(), p.getTherapyProgram().getProgramId(), p.getTherapyProgram().getName()));
        }
        return dtos;
    }

    @Override
    public List<PaymentDTO> getPaymentsByPatient(String patientId) throws Exception {
        List<Payment> payments = paymentDAO.getPaymentsByPatient(patientId);
        List<PaymentDTO> dtos = new ArrayList<>();
        for (Payment p : payments) {
            dtos.add(new PaymentDTO(p.getPaymentId(), p.getAmount(), p.getPaymentDate(), p.getPaymentMethod(), p.getStatus(), p.getCoveredSessions(), p.getDueBalance(), p.getPatient().getId(), p.getPatient().getName(), p.getTherapyProgram().getProgramId(), p.getTherapyProgram().getName()));
        }
        return dtos;
    }

    @Override
    public String getNextId() throws Exception {
        String lastId = paymentDAO.getLastId();
        return lk.ijse.theserenitymentalhealththerapycenter.util.IdGenerator.generateNextId("PAY", lastId);
    }

    @Override
    public lk.ijse.theserenitymentalhealththerapycenter.dto.FinancialSummaryDTO getFinancialSummary(String patientId, String programId) throws Exception {
        TherapyProgram program = programDAO.search(programId);
        if (program == null) return new lk.ijse.theserenitymentalhealththerapycenter.dto.FinancialSummaryDTO(0, 0, 0, 0);

        List<Payment> allPayments = paymentDAO.getPaymentsByPatient(patientId);
        double paidAmount = 0;
        int totalCoveredSessions = 0;

        for (Payment p : allPayments) {
            if (p.getTherapyProgram().getProgramId().equals(programId)) {
                paidAmount += p.getAmount();
                totalCoveredSessions += p.getCoveredSessions();
            }
        }

        double dueBalance = program.getFee() - paidAmount;
        if (dueBalance < 0) dueBalance = 0;

        List<TherapySession> allSessions = sessionDAO.getSessionsByPatient(patientId);
        int bookedSessions = 0;
        for (TherapySession s : allSessions) {
            if (s.getTherapyProgram().getProgramId().equals(programId) && !s.getStatus().equals("CANCELLED")) {
                bookedSessions++;
            }
        }

        int prepaidSessionsAvailable = totalCoveredSessions - bookedSessions;
        if (prepaidSessionsAvailable < 0) prepaidSessionsAvailable = 0;

        return new lk.ijse.theserenitymentalhealththerapycenter.dto.FinancialSummaryDTO(program.getFee(), paidAmount, dueBalance, prepaidSessionsAvailable);
    }

    @Override
    public java.util.Map<String, Double> getIncomeByDate(int days) throws Exception {
        List<Payment> all = paymentDAO.getAll();
        java.time.LocalDate startDate = java.time.LocalDate.now().minusDays(days - 1);
        
        java.util.Map<String, Double> result = new java.util.LinkedHashMap<>();
        for (int i = 0; i < days; i++) {
            result.put(startDate.plusDays(i).toString(), 0.0);
        }

        for (Payment p : all) {
            try {
                java.time.LocalDate date = java.time.LocalDate.parse(p.getPaymentDate());
                if (!date.isBefore(startDate)) {
                    result.put(date.toString(), result.getOrDefault(date.toString(), 0.0) + p.getAmount());
                }
            } catch (Exception ignored) {}
        }
        return result;
    }

    @Override
    public java.util.Map<String, Double> getRevenueByMonth(int months) throws Exception {
        List<Payment> all = paymentDAO.getAll();
        java.time.YearMonth startMonth = java.time.YearMonth.now().minusMonths(months - 1);
        
        java.util.Map<String, Double> result = new java.util.LinkedHashMap<>();
        for (int i = 0; i < months; i++) {
            result.put(startMonth.plusMonths(i).toString(), 0.0);
        }

        for (Payment p : all) {
            try {
                java.time.LocalDate date = java.time.LocalDate.parse(p.getPaymentDate());
                java.time.YearMonth ym = java.time.YearMonth.from(date);
                if (!ym.isBefore(startMonth)) {
                    result.put(ym.toString(), result.getOrDefault(ym.toString(), 0.0) + p.getAmount());
                }
            } catch (Exception ignored) {}
        }
        return result;
    }
}
