package ru.gb.main.utils.repositories;

import org.hibernate.Session;

import javax.persistence.criteria.CriteriaQuery;
import java.util.List;
import java.util.Optional;

public abstract class DatabaseRepository<T> implements iRepository<T> {
    protected Connector connector;

    public DatabaseRepository() {
        connector = new Connector();
    }

    @Override
    public boolean insertData(T... data) {
        try (Session session = connector.getSession()) {
            session.beginTransaction();
            for (T el : data) {
                session.save(el);
            }
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    @Override
    public boolean insertData(List<T> data) {
        try (Session session = connector.getSession()) {
            session.beginTransaction();
            for (T el : data) {
                session.save(el);
            }
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    @Override
    public boolean updateData(T... data) {
        try (Session session = connector.getSession()) {
            session.beginTransaction();
            for (T el : data) {
                session.update(el);
            }
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    @Override
    public Optional<T> readData(long id, Class<T> clazz) {
        Optional<T> optional = Optional.empty();
        try (Session session = connector.getSession()) {
            optional = Optional.of((T) session.get(clazz, id));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return optional;
    }

    @Override
    public Optional<List<T>> readAllData(Class<T> clazz) {
        Optional<List<T>> optional = Optional.empty();
        try (Session session = connector.getSession()) {
            CriteriaQuery<T> criteria = session.getCriteriaBuilder().createQuery(clazz);
            criteria.from(clazz);
            optional = Optional.of(session.createQuery(criteria).getResultList());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return optional;
    }

    @Override
    public boolean dropData(T... data) {
        try (Session session = connector.getSession()) {
            session.beginTransaction();
            for (T el : data) {
                session.delete(el);
            }
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}
