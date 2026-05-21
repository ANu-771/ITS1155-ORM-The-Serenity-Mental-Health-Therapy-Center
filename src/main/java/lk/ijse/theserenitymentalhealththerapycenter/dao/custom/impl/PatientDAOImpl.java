package lk.ijse.theserenitymentalhealththerapycenter.dao.custom.impl;

import lk.ijse.theserenitymentalhealththerapycenter.config.FactoryConfiguration;
import lk.ijse.theserenitymentalhealththerapycenter.dao.custom.PatientDAO;
import lk.ijse.theserenitymentalhealththerapycenter.entity.Patient;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class PatientDAOImpl implements PatientDAO {

    @Override
    public boolean save(Patient entity) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.persist(entity);
            transaction.commit();
            return true;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean update(Patient entity) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.merge(entity);
            transaction.commit();
            return true;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean delete(String id) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            Patient patient = session.get(Patient.class, id);
            if (patient != null) {
                session.remove(patient);
                transaction.commit();
                return true;
            }
            transaction.rollback();
            return false;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public Patient search(String id) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.get(Patient.class, id);
        } finally {
            session.close();
        }
    }

    @Override
    public List<Patient> getAll() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.createQuery("FROM Patient", Patient.class).list();
        } finally {
            session.close();
        }
    }

    @Override
    public List<Patient> searchByName(String name) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Query<Patient> query = session.createQuery("FROM Patient p WHERE p.name LIKE :name", Patient.class);
            query.setParameter("name", "%" + name + "%");
            return query.list();
        } finally {
            session.close();
        }
    }

    @Override
    public List<Patient> getPatientsWithPrograms() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            // HQL join query to retrieve patients along with their enrolled therapy programs
            Query<Patient> query = session.createQuery(
                    "SELECT DISTINCT p FROM Patient p LEFT JOIN FETCH p.therapyPrograms", Patient.class);
            return query.list();
        } finally {
            session.close();
        }
    }

    @Override
    public List<Patient> getPatientsEnrolledInAllPrograms() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            // Step 1: Get total number of available therapy programs
            Long totalPrograms = session.createQuery(
                    "SELECT COUNT(tp) FROM TherapyProgram tp", Long.class).uniqueResult();

            // Step 2: HQL Join Query — patients who have registered for EVERY available program
            Query<Patient> query = session.createQuery(
                    "SELECT p FROM Patient p JOIN p.therapyPrograms tp " +
                    "GROUP BY p.id HAVING COUNT(tp) = :totalPrograms", Patient.class);
            query.setParameter("totalPrograms", totalPrograms);
            return query.list();
        } finally {
            session.close();
        }
    }

    @Override
    public String getLastId() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.createQuery("SELECT p.id FROM Patient p ORDER BY p.id DESC", String.class)
                    .setMaxResults(1).uniqueResult();
        } finally {
            session.close();
        }
    }
}
