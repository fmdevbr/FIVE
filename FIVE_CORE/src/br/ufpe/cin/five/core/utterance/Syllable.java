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
 * This class defines a Syllable.<br/>
 * It contains all information about a Syllable, like:<br/>
 * Description - the syllabe itself.<br/>
 * IndexInWord - the position of the syllable in word.<br/>
 * IsStreesed - specifies if the syllable is stressed.<br/>
 * Phones - the list of phones of the syllable
 */
@Entity
public class Syllable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private int id;
    @Column(columnDefinition = "varchar(100) COLLATE latin1_bin")
    private String description;
    private int indexInWord;        
    private Boolean isStressed;
    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @Column(columnDefinition = "varchar(100) COLLATE latin1_bin")
    private List<String> phones;

    /**
     * Creates a new empty Syllable
     */
    public Syllable() {
        this.phones = new ArrayList<String>();
    }
     
    /**
     * Return the position of this syllable in the word that it belongs.
     * @return  the position of this syllable in the word that it belongs
     */
    public int getIndexInWord() {
        return indexInWord;
    }

    /**
     * Sets the position of this syllable in the word that it belongs.
     * @param indexInWord  the position of this syllable in the word that it belongs
     */
    public void setIndexInWord(int indexInWord) {
        this.indexInWord = indexInWord;
    }

    /**
     * Returns the description of this syllable.
     * @return the description of this syllable
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of this syllable.
     * @param description the description of this syllable
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Tests if this syllable is stressed.
     * @return true if this syllable is stressed
     */
    public Boolean isStressed() {
        return isStressed;
    }

    /**
     * Specifies if this syllable is stressed.
     * @param isStressed true if this syllable is stressed
     */
    public void setStressed(Boolean isStressed) {
        this.isStressed = isStressed;
    }

    /**
     * Returns the phones list of this syllable.
     * @return the phones list of this syllable
     */
    public List<String> getPhones() {
        return phones;
    }

    /**
     * Sets the phones list of this syllable.
     * @param phones the phones list of this syllable
     */
    public void setPhones(List<String> phones) {
        this.phones = phones;
    }

    /**
     *  Returns the description of this syllable.
     * @return the description of this syllable
     */
    @Override
    public String toString() {
        return this.description;
    }

    /**
     * Tests if the object is equals to this syllable.<br/>
     * The test is done by comparing if the object is instace of Syllable and then comparing the id of the object with the id of this syllable
     * @param obj the object to comparing
     * @return true if the obj is equals to this phrase
     */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Syllable) ? this.description.equals( ((Syllable) obj).getDescription()) : false;
    }
}
