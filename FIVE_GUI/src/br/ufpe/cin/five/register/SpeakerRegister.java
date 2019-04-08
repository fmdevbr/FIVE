/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.register;

import br.ufpe.cin.five.core.data.ConcreteDAO;
import br.ufpe.cin.five.core.project.Project;
import br.ufpe.cin.five.core.speaker.Speaker;
import br.ufpe.cin.five.exception.RegisterException;
import br.ufpe.cin.five.facade.Facade;

/**
 *
 * @author Alexandre
 */
public class SpeakerRegister {

    private Project project;    

    public SpeakerRegister(Project project) {
        this.project = project;        
                
    }

    public void insert(Speaker speaker) throws RegisterException {
        if (search(speaker.getId()) == null) {            
            Facade.getInstance().getConcreteDAO().create(speaker);                      
            this.project.getSpeakers().add(speaker);            
            Facade.getInstance().getConcreteDAO().update(this.project);
        } else {
            throw new RegisterException("Já existe um(a) locutor(a) com este ID: " + speaker.getId() + " no banco de dados.");
        }        
    }

    public void update(Speaker speaker) throws RegisterException {
        if (search(speaker.getId()) != null) {
            Facade.getInstance().getConcreteDAO().update(speaker);
            int speakerIndex = this.project.getSpeakers().indexOf(speaker);
            this.project.getSpeakers().set(speakerIndex, speaker);
            Facade.getInstance().getConcreteDAO().update(this.project);

        } else {
            throw new RegisterException("Não existe um(a) locutor(a) com este ID: " + speaker.getId() + " no banco de dados.");
        }        
    }

    public void remove(Speaker speaker) throws RegisterException {
        if (search(speaker.getId()) != null) {            
            this.project.getSpeakers().remove(speaker);            
            Facade.getInstance().getConcreteDAO().update(project);
            Facade.getInstance().getConcreteDAO().delete(speaker);
        } else {
            throw new RegisterException("Não existe um(a) locutor(a) com este ID: " + speaker.getId() + " no banco de dados.");
        }        
    }

    public Speaker search(int id) throws RegisterException {
        for (Speaker speaker : this.project.getSpeakers()) {
            if (id != -1 && speaker.getId() == id) {
                return speaker;
            }
        }
        return null;
    }    
}
