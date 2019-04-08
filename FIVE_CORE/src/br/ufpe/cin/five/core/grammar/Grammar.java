/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved. Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.grammar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 *
 * @author Alexandre
 */
@Entity
public class Grammar implements Serializable {    
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  
    private int id;
     
    @ManyToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<RuleGrammar> ruleGrammars;

    public Grammar() {                
        ruleGrammars = new ArrayList<RuleGrammar>();        
    }

    public List<RuleGrammar> getRuleGrammars() {
        return ruleGrammars;
    }
    public RuleGrammar getRuleGrammar(int id){
        for (RuleGrammar ruleGrammar : ruleGrammars) {
            if(ruleGrammar.getId() == id){
                return ruleGrammar;
            }
        }
        return null;
    }
    public void setRuleGrammars(List<RuleGrammar> ruleGrammars) {
        this.ruleGrammars = ruleGrammars;
    }

    public void setActiveRule(int ruleId){

        for (RuleGrammar ruleGrammar : ruleGrammars) {

            if(ruleGrammar.isActive()){
                ruleGrammar.setActive(false);
            }

            if(ruleGrammar.getId() == ruleId){
                ruleGrammar.setActive(true);
            }
        }
    }

    public RuleGrammar getActiveRule(){

        for (RuleGrammar ruleGrammar : ruleGrammars) {
            if(ruleGrammar.isActive()) {
                return ruleGrammar;
            }
        }
        return null;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
}
