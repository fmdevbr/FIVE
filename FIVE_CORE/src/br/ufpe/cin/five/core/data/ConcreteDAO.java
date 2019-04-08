/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufpe.cin.five.core.data;

import java.util.List;

/**
 *
 * @author vocallab
 */
public class ConcreteDAO extends AbstractDAO {

    public ConcreteDAO(String url, String login, String password, DriverType driver) {
        super(url,login,password,driver);
        this.closeSession();
    }

    
    public void closeSession()
    {
        super.closeSession();
    }
    /**
     * Insert a new Object into the database.
     *
     * @param event
     */
    public void create(Object object) throws DataException {
        super.save(object);
    }

    /**
     * Delete a detached Object from the database.
     *
     * @param event
     */
    public void delete(Object object) throws DataException {
        super.delete(object);
    }

    /**
     * Find an Object by its primary key.
     *
     * @param id
     * @return
     */
    public Object find(Long id, Class objType) throws DataException {
        return super.find(objType, id);
    }

    /**
     * Updates the object of a detached Event.
     *
     * @param event
     */
    public void update(Object obj) throws DataException {
        super.update(obj);
    }

    /**
     * Finds all Objects in the database given a type.
     *
     * @return
     */
    public List findAll(Class objType) throws DataException {
        return super.findAll(objType);
    }
    
             /**
     * Gets an iten by its description
     */
    public Object getByDescription(String description, Class clazz) throws DataException
    {
        return super.getByDescription(description, clazz);
    }
}
