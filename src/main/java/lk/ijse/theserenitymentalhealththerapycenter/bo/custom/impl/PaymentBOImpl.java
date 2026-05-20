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

    @Override
    public boolean savePayment(PaymentDTO dto) throws Exception {
        if (dto.getAmount() <= 0) throw new PaymentException("Amount must be greater than zero");
        if (dto.getPatientId() == null) throw new InvalidInputException("Patient is required");
        if (dto.getProgramId() == null) throw new InvalidInputException("Program is required");

        Patient patient = patientDAO.search(dto.getPatientId());
        TherapyProgram program = programDAO.search(dto.getProgramId());
        Payment payment = new Payment(dto.getPaymentId(), dto.getAmount(), dto.getPaymentDate(), dto.getPaymentMethod(), dto.getStatus(), patient, program);
        return paymentDAO.save(payment);
    }

    @Override
    public boolean updatePayment(PaymentDTO dto) throws Exception {
        Patient patient = patientDAO.search(dto.getPatientId());
        TherapyProgram program = programDAO.search(dto.getProgramId());
        Payment payment = new Payment(dto.getPaymentId(), dto.getAmount(), dto.getPaymentDate(), dto.getPaymentMethod(), dto.getStatus(), patient, program);
        return paymentDAO.update(payment);
    }

    @Override
    public boolean deletePayment(String id) throws Exception { return paymentDAO.delete(id); }

    @Override
    public PaymentDTO searchPayment(String id) throws Exception {
        Payment p = paymentDAO.search(id);
        if (p == null) return null;
        return new PaymentDTO(p.getPaymentId(), p.getAmount(), p.getPaymentDate(), p.getPaymentMethod(), p.getStatus(), p.getPatient().getId(), p.getPatient().getName(), p.getTherapyProgram().getProgramId(), p.getTherapyProgram().getName());
    }

    @Override
    public List<PaymentDTO> getAllPayments() throws Exception {
        List<Payment> payments = paymentDAO.getAll();
        List<PaymentDTO> dtos = new ArrayList<>();
        for (Payment p : payments) {
            dtos.add(new PaymentDTO(p.getPaymentId(), p.getAmount(), p.getPaymentDate(), p.getPaymentMethod(), p.getStatus(), p.getPatient().getId(), p.getPatient().getName(), p.getTherapyProgram().getProgramId(), p.getTherapyProgram().getName()));
        }
        return dtos;
    }

    @Override
    public List<PaymentDTO> getPaymentsByPatient(String patientId) throws Exception {
        List<Payment> payments = paymentDAO.getPaymentsByPatient(patientId);
        List<PaymentDTO> dtos = new ArrayList<>();
        for (Payment p : payments) {
            dtos.add(new PaymentDTO(p.getPaymentId(), p.getAmount(), p.getPaymentDate(), p.getPaymentMethod(), p.getStatus(), p.getPatient().getId(), p.getPatient().getName(), p.getTherapyProgram().getProgramId(), p.getTherapyProgram().getName()));
        }
        return dtos;
    }
}
