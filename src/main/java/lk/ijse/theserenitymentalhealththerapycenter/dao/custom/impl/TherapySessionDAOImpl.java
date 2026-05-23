package lk.ijse.theserenitymentalhealththerapycenter.dao.custom.impl;

import lk.ijse.theserenitymentalhealththerapycenter.config.FactoryConfiguration;
import lk.ijse.theserenitymentalhealththerapycenter.dao.custom.TherapySessionDAO;
import lk.ijse.theserenitymentalhealththerapycenter.entity.TherapySession;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class TherapySessionDAOImpl implements TherapySessionDAO {

    @Override
    public boolean save(TherapySession entity) throws Exception {
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
    public boolean update(TherapySession entity) throws Exception {
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
            TherapySession therapySession = session.get(TherapySession.class, id);
            if (therapySession != null) {
                session.remove(therapySession);
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
    public TherapySession search(String id) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.get(TherapySession.class, id);
        } finally {
            session.close();
        }
    }

    @Override
    public List<TherapySession> getAll() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.createQuery("SELECT ts FROM TherapySession ts JOIN FETCH ts.patient JOIN FETCH ts.therapist JOIN FETCH ts.therapyProgram", TherapySession.class).list();
        } finally {
            session.close();
        }
    }

    @Override
    public List<TherapySession> getSessionsByTherapist(String therapistId) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Query<TherapySession> query = session.createQuery(
                    "SELECT ts FROM TherapySession ts JOIN FETCH ts.patient JOIN FETCH ts.therapist JOIN FETCH ts.therapyProgram WHERE ts.therapist.id = :therapistId", TherapySession.class);
            query.setParameter("therapistId", therapistId);
            return query.list();
        } finally {
            session.close();
        }
    }

    @Override
    public List<TherapySession> getSessionsByPatient(String patientId) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Query<TherapySession> query = session.createQuery(
                    "SELECT ts FROM TherapySession ts JOIN FETCH ts.patient JOIN FETCH ts.therapist JOIN FETCH ts.therapyProgram WHERE ts.patient.id = :patientId", TherapySession.class);
            query.setParameter("patientId", patientId);
            return query.list();
        } finally {
            session.close();
        }
    }

    @Override
    public boolean checkConflict(String therapistId, String date, String time) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(ts) FROM TherapySession ts WHERE ts.therapist.id = :therapistId AND ts.date = :date AND ts.time = :time AND ts.status != 'CANCELLED'",
                    Long.class);
            query.setParameter("therapistId", therapistId);
            query.setParameter("date", date);
            query.setParameter("time", time);
            return query.uniqueResult() > 0;
        } finally {
            session.close();
        }
    }

    @Override
    public String getLastId() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.createQuery("SELECT ts.sessionId FROM TherapySession ts ORDER BY ts.sessionId DESC", String.class)
                    .setMaxResults(1).uniqueResult();
        } finally {
            session.close();
        }
    }
}
