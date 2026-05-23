package lk.ijse.theserenitymentalhealththerapycenter.dao.custom.impl;

import lk.ijse.theserenitymentalhealththerapycenter.config.FactoryConfiguration;
import lk.ijse.theserenitymentalhealththerapycenter.dao.custom.TherapyProgramDAO;
import lk.ijse.theserenitymentalhealththerapycenter.entity.TherapyProgram;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class TherapyProgramDAOImpl implements TherapyProgramDAO {

    @Override
    public boolean save(TherapyProgram entity) throws Exception {
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
    public boolean update(TherapyProgram entity) throws Exception {
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
            TherapyProgram program = session.get(TherapyProgram.class, id);
            if (program != null) {
                session.remove(program);
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
    public TherapyProgram search(String id) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.get(TherapyProgram.class, id);
        } finally {
            session.close();
        }
    }

    @Override
    public List<TherapyProgram> getAll() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.createQuery("FROM TherapyProgram", TherapyProgram.class).list();
        } finally {
            session.close();
        }
    }

    @Override
    public List<TherapyProgram> getProgramsByPatient(String patientId) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.createQuery(
                    "SELECT tp FROM Patient p JOIN p.therapyPrograms tp WHERE p.id = :id", TherapyProgram.class)
                    .setParameter("id", patientId)
                    .list();
        } finally {
            session.close();
        }
    }

    @Override
    public String getLastId() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.createQuery("SELECT tp.programId FROM TherapyProgram tp ORDER BY tp.programId DESC", String.class)
                    .setMaxResults(1).uniqueResult();
        } finally {
            session.close();
        }
    }
}
