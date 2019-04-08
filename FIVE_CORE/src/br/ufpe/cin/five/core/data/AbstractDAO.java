package br.ufpe.cin.five.core.data;

import br.ufpe.cin.five.core.project.Project;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

public abstract class AbstractDAO {

    private Session session;
    private Transaction tx;
    private String url;
    private String login;
    private String password;
    private DriverType driver;

    public AbstractDAO(String url, String login, String password, DriverType driver) {
        this.url = url;
        this.login = login;
        this.password = password;
        this.driver = driver;
        HibernateFactory.buildIfNeeded(url, login, password, driver);
    }

    protected void save(Object obj) {
        try {
            startOperation();
            session.save(obj);
            tx.commit();
        } catch (HibernateException e) {
            handleException(e);
        } finally {
            //HibernateFactory.close(session);
        }
    }
    
    protected void update(Object obj) {
        try {
            startOperation();
            session.merge(obj);
            tx.commit();
        } catch (HibernateException e) {
            handleException(e);
        } finally {
            //HibernateFactory.close(session);
        }
    }    

    protected void closeSession() {
        HibernateFactory.close(session);
    }

    protected void delete(Object obj) {
        try {
            startOperation();
            session.delete(obj);
            tx.commit();
        } catch (HibernateException e) {
            handleException(e);
        } finally {
            //  HibernateFactory.close(session);
        }
    }

    protected Object find(Class clazz, Long id) {
        Object obj = null;
        try {
            startOperation();
            obj = session.load(clazz, id);
            tx.commit();
        } catch (HibernateException e) {
            handleException(e);
        } finally {
            //  HibernateFactory.close(session);
        }
        return obj;
    }

    protected List findAll(Class clazz) {
        List objects = null;
        try {
            startOperation();
            Criteria criteria = session.createCriteria(clazz);
            objects = criteria.list();
            tx.commit();
        } catch (HibernateException e) {
            System.out.println(e.getMessage());
            handleException(e);
        } finally {
            //  HibernateFactory.close(session);
        }
        return objects;
    }

    protected Object getByDescription(String description, Class claz) {
        Object obj = null;
        try {
            startOperation();
            List itens = session.createCriteria(claz).add(Restrictions.like("description", description)).list();
            if (itens.size() > 0) {
                obj = itens.get(0);
            }
            tx.commit();
        } catch (HibernateException e) {
            handleException(e);
        } finally {
            // HibernateFactory.close(session);
        }
        return obj;
    }

    protected void handleException(HibernateException e) throws DataException {
        HibernateFactory.rollback(tx);
        throw new DataException(e);
    }

    protected void startOperation() throws HibernateException {
        if (session == null) {
            session = HibernateFactory.openSession(url, login, password, driver);
        }
        tx = session.beginTransaction();
    }
}