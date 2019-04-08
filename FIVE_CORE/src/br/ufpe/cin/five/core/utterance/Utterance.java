/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved. Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.utterance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * This class defines an Utterance.<br/>
 * It contains information about an utterance, like:<br/>
 * Id - the id of the utterance.<br/>
 * Description - the utterance itself<br/>
 * Phrases - the phrases of the utterance<br/>
 */
@Entity
public class Utterance implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private int id;
    @Column(columnDefinition="TEXT")
    private String description;
    @ManyToMany(cascade = CascadeType.REMOVE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Phrase> phrases;

    /**
     * Creates a new empty Utterance
     */
    public Utterance() {                
        this.phrases = new ArrayList<Phrase>();
    }  
 
    /**
     * Return the id of this utterance.
     * @return the id of this utterance.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id of this utterance
     * @param id the id of this utterance
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Returns the description, or null if the utterance does not have it.
     * @return the description of this utterance
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the description of this utterance
     * @param description the description of this utterance
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the list of the phrases that this utterance contains, or an empty list if the utterance does not have it.
     * @return the list of the phrases
     */
    public List<Phrase> getPhrases() {
        return phrases;
    }

    /**
     * Sets the list of the phrases of this utterance.
     * @param phrases the list of the phrases of this utterance
     */
    public void setPhrases(List<Phrase> phrases) {
        this.phrases = phrases;
    }

    /**
     * Returns the description of this utterance
     * @return
     */
    @Override
    public String toString() {
        return this.description;
    }

    /**
     * Tests if the object is equals to this utterance.<br/>
     * The test is done by comparing if the object is instace of Utterance and then comparing the id of the object and this utterance
     * @param obj the object to comparing
     * @return true if the obj is equals to this utterance
     */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Utterance) ? this.id == ((Utterance) obj).getId() : false;
    }
}
