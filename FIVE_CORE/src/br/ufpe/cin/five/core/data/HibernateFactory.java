/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufpe.cin.five.core.data;
 
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class HibernateFactory {

    private static SessionFactory sessionFactory;

    /**
     * Constructs a new Singleton SessionFactory
     *
     * @return
     * @throws HibernateException
     */
    public static SessionFactory buildSessionFactory(String url, String login, String password, DriverType driver) throws HibernateException {
        if (sessionFactory != null) {
            closeFactory();
        }
        return configureSessionFactory(url,login,password,driver);
    }

    /**
     * Builds a SessionFactory, if it hasn't been already.
     */
    public static SessionFactory buildIfNeeded(String url, String login, String password, DriverType driver) throws DataException {
        if (sessionFactory != null) {
            return sessionFactory;
        }
        try {
            return configureSessionFactory(url,login,password,driver);
        } catch (HibernateException e) {
            throw new DataException(e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static Session openSession(String url, String login, String password, DriverType driver) throws HibernateException {
        buildIfNeeded(url,login,password,driver);
        return sessionFactory.openSession();
    }

    public static void closeFactory() {
        if (sessionFactory != null) {
            try {
                sessionFactory.close();
            } catch (HibernateException ignored) {
                System.out.println("No foi possivel fechar a sesão!: " + ignored.getMessage());
            }
        }
    }

    public static void close(Session session) {
        if (session != null) {
            try {
                session.close();
            } catch (HibernateException ignored) {
                System.out.println("No foi possivel fechar a sesão!: " + ignored.getMessage());
            }
        }
    }

    public static void rollback(Transaction tx) {
        try {
            if (tx != null) {
                tx.rollback();
            }
        } catch (HibernateException ignored) {
            System.out.println("No foi possivel realizar o rollback!: " + ignored.getMessage());
        }
    }

    /**
     *
     * @return @throws HibernateException
     */
    private static SessionFactory configureSessionFactory(String url, String login, String password, DriverType driver) throws HibernateException {
        Configuration configuration = DriverUtils.getConfiguration(url,login,password,driver);               
        sessionFactory = configuration.buildSessionFactory();

        return sessionFactory;
    }
    
}