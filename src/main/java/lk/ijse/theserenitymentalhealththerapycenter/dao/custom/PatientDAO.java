package lk.ijse.theserenitymentalhealththerapycenter.dao.custom;

import lk.ijse.theserenitymentalhealththerapycenter.dao.CrudDAO;
import lk.ijse.theserenitymentalhealththerapycenter.entity.Patient;

import java.util.List;

public interface PatientDAO extends CrudDAO<Patient, String> {
    List<Patient> searchByName(String name) throws Exception;
    List<Patient> getPatientsWithPrograms() throws Exception;
    List<Patient> getPatientsEnrolledInAllPrograms() throws Exception;
}
