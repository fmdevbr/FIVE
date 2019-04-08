/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.register;

import br.ufpe.cin.five.core.data.ConcreteDAO;
import br.ufpe.cin.five.core.engine.Engine;
import br.ufpe.cin.five.core.engine.EngineException;
import br.ufpe.cin.five.core.engine.EngineProcess;
import br.ufpe.cin.five.core.engine.SpeechEngine;
import br.ufpe.cin.five.core.grammar.RuleGrammar;
import br.ufpe.cin.five.core.project.Project;
import br.ufpe.cin.five.core.project.ProjectType;
import br.ufpe.cin.five.exception.RegisterException;
import br.ufpe.cin.five.facade.Facade;

/**
 *
 * @author Alexandre
 */
public class EngineRegister {

    private Project project;
    
    
    public EngineRegister(Project project) {
        this.project = project;
        
    }    

    public void insert(Engine engine) throws RegisterException {
        if (search(engine.getId()) == null) {            
           
            project.getEngines().add(engine);
            
            if(project.getType().equals(ProjectType.ASR)){            
                SpeechEngine speechEngine = (SpeechEngine) engine;
                Facade.getInstance().getConcreteDAO().create(speechEngine.getDictionary());                
                for (RuleGrammar ruleGrammar : speechEngine.getGrammar().getRuleGrammars()) {
                    Facade.getInstance().getConcreteDAO().create(ruleGrammar);
                }                                
                Facade.getInstance().getConcreteDAO().create(speechEngine.getGrammar());                        
            }            
            Facade.getInstance().getConcreteDAO().create(engine);
            Facade.getInstance().getConcreteDAO().update(project);
        } else {
            throw new RegisterException("Já existe um motor com este ID: " + engine.getId() + " no banco de dados.");
        }
        
    }

    public void update(Engine engine) throws RegisterException {
        if (search(engine.getId()) != null) {
            
            Facade.getInstance().getConcreteDAO().update(engine);                        
        } else {
            throw new RegisterException("Não existe um motor com este ID: " + engine.getId() + " no banco de dados.");
        }
        
    }

    public void remove(Engine engine) throws RegisterException {
        if (search(engine.getId()) != null) {
            project.getEngines().remove(engine);            
            Facade.getInstance().getConcreteDAO().update(project); 
            Facade.getInstance().getConcreteDAO().delete(engine);
        } else {
            throw new RegisterException("Não existe um motor com este ID: " + engine.getId() + " no banco de dados.");
        }
        
    }

    public Engine search(int id) throws RegisterException {
        for (Engine engine : this.project.getEngines()) {
            if (id != -1 && engine.getId() == id) {
                return engine;
            }
        }
        return null;
    }    
    
    public void generate(Engine engine) throws RegisterException {
        try{
            // Verificando se há alguma extração ativa
            if(!engine.getExtraction().isActive()) {           
                throw new RegisterException("O motor que você deseja gerar não foi extraído ou não está ativo.");
            }            
            
            // Verificando se há alguma engine ativa
            if(!engine.getClassification().isActive()) {           
                throw new RegisterException("O motor que você deseja gerar não foi classificado ou não está ativo.");
            }

            EngineProcess.generate(project, engine);
            
        } catch (EngineException ex) {
            throw new RegisterException(ex.getMessage());
        }
    }        
}
