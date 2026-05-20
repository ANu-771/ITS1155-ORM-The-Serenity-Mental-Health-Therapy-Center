package lk.ijse.theserenitymentalhealththerapycenter.dao.custom;

import lk.ijse.theserenitymentalhealththerapycenter.dao.CrudDAO;
import lk.ijse.theserenitymentalhealththerapycenter.entity.Payment;

import java.util.List;

public interface PaymentDAO extends CrudDAO<Payment, String> {
    List<Payment> getPaymentsByPatient(String patientId) throws Exception;
}
