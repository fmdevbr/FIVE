/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved. Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.grammar;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 *
 * @author Alexandre
 */
@Entity
public class RuleGrammar implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  
    private int id;
    private boolean active;
    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<String> words;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }
    
}
