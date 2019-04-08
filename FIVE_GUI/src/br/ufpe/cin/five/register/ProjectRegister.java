/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.register;

import br.ufpe.cin.five.core.data.ConcreteDAO;
import br.ufpe.cin.five.core.data.DataException;
import br.ufpe.cin.five.core.project.Project;
import br.ufpe.cin.five.core.project.ProjectException;
import br.ufpe.cin.five.core.project.ProjectProcess;
import br.ufpe.cin.five.core.sample.Sample;
import br.ufpe.cin.five.core.utterance.Utterance;
import br.ufpe.cin.five.exception.RegisterException;
import br.ufpe.cin.five.facade.Facade;
import java.io.File;


/**
 *
 * @author Alexandre
 */
public class ProjectRegister {        
      
     
    public static String getDatabasePath(Project project)
    {
        return project.getDirectory() + File.separator + "database";        
    }
    
    public static String getDatabasePath(String directory, String projectName)
    {       
               
        return directory + File.separator + "database";
    }
    
    public static void create(Project project) throws RegisterException {       
        try {                       
            ProjectProcess.create(project);
                                 
            Facade.getInstance().setConcreteDAO(new ConcreteDAO(project.getDbUrl(),project.getDbLogin(),project.getDbPassword(), project.getDbDriver())); 
            Facade.getInstance().getConcreteDAO().create(project);            
            
        } catch (ProjectException ex) {
            throw new RegisterException(ex.getMessage());
        }            
    }       

    public static Project open(File projectFile) throws RegisterException {
        try {            
            Project project = ProjectProcess.open(projectFile);
                        
            Facade.getInstance().setConcreteDAO(new ConcreteDAO(project.getDbUrl(), project.getDbLogin(), project.getDbPassword(), project.getDbDriver())); 
            Project p = null;            
            for(Object o : Facade.getInstance().getConcreteDAO().findAll(br.ufpe.cin.five.core.project.Project.class))
            {
                p = (Project)o;
                if(p.getName().equals(project.getName()))
                    break;
            }
            if(p!= null)
            {
                p.setDirectory(project.getDirectory());
                p.setDbUrl(project.getDbUrl());
                p.setDbPassword(project.getDbLogin());
                p.setDbLogin(project.getDbPassword());
                p.setDbDriver(project.getDbDriver());
                Facade.getInstance().getConcreteDAO().update(p);
                return p;            
            }
            else
                throw new RegisterException("Projeto não encontrado!");

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new RegisterException(ex.getMessage());
        }
    }

    public static void save(Project project) throws RegisterException {
        try {            
            Facade.getInstance().getConcreteDAO().update(project);            
        } catch (Exception ex) {
            throw new RegisterException(ex.getMessage());
        }            
    }
    
    public static int getNextSampleId(Project project)
    {       
        int lastId = 0;
        for(Object o: Facade.getInstance().getConcreteDAO().findAll(Sample.class))
            if(((Sample)o).getId()>0)
                lastId = ((Sample)o).getId();        
        
        lastId++;
        return lastId;        
    }
    
    public static int getNextUtteranceId(Project project)
    {        
        int lastId = 0;
        for(Object o: Facade.getInstance().getConcreteDAO().findAll(Utterance.class))
            if(((Utterance)o).getId()>0)
                lastId = ((Utterance)o).getId();        
        
        lastId++;
        return lastId;        
    }    

    public static void close(Project project) throws RegisterException {        
        Facade.getInstance().getConcreteDAO().update(project);        
        Facade.getInstance().getConcreteDAO().closeSession();
    }             
}
