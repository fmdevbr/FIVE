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
 * This class defines a Word in a project.<br/>
 * It contains information about a word, like:<br/>
 * Description - the word itself<br/>
 * GrammaticalClassification - the grammatical classification of the word<br/>
 * Syllables - the syllables of the word<br/>
 * PhoneticRepresentations - the phonetic representation of the word<br/>
 * QuestionFlag - Specifies if is the last word an interrogative phrase<br/>
 */
@Entity
public class Word implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private int id;
    private String description;
    private String grammaticalClassification;
    @ManyToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Syllable> syllables;
    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<String> phoneticRepresentations;
    private Boolean questionFlag;
    public static final String FLAG_STRESS = "\"";

    /**
     * Creates a new empty Word
     */
    public Word() {
        this.syllables = new ArrayList<Syllable>();
        this.phoneticRepresentations = new ArrayList<String>();
    }
  
    /**
     * Return the Id of this word, or -1 if the word does not have it.
     * @return the Id of this word
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the Id of this word.
     * @param id the id of this word
     */
    public void setId(int id) {
        this.id = id;
    }
   
    /**
     * Returns the description of this word, or null if the word does not have it.
     * @return the description of this extraction
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of this word.
     * @param description the description of this word
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the list of syllables, or empty list if the word does not have it.
     * @return the list of syllables of this word
     */
    public List<Syllable> getSyllables() {
        return syllables;
    }

    /**
     * Sets the list of syllables of this word.
     * @param syllables the list of syllables of this word
     */
    public void setSyllables(List<Syllable> syllables) {
        this.syllables = syllables;
    }

    /**
     * Returns the grammatical classification of this word, or null if the word does not have it.
     * @return
     */
    public String getGrammaticalClassification() {
        return grammaticalClassification;
    }

    /**
     * Sets the grammatical classification of this word.
     * @param grammaticalClassification the grammatical classification of this word
     */
    public void setGrammaticalClassification(String grammaticalClassification) {
        this.grammaticalClassification = grammaticalClassification;
    }

    /**
     * Returns the list of phonetic representations, or a empty list if the word does not have it.
     * @return the lis of phonetic representations
     */
    public List<String> getPhoneticRepresentations() {
        return phoneticRepresentations;
    }

    /**
     * Sets the list of phonetic representations of this word
     * @param phoneticRepresentations the list of phonetic representations of this word
     */
    public void setPhoneticRepresentations(List<String> phoneticRepresentations) {
        this.phoneticRepresentations = phoneticRepresentations;
    }

    /**
     * Tests if this word is the last word an interrogative phrase
     * @return true if this word is the last word an interrogative phrase
     */
    public Boolean getQuestionFlag() {
        return questionFlag;
    }

    /**
     * Specifies if this word is the last word an interrogative phrase
     * @param questionFlag true if is the last word an interrogative phrase
     */
    public void setQuestionFlag(Boolean questionFlag) {
        this.questionFlag = questionFlag;
    }

    /**
     * Return the description of this word
     * @return
     */
    @Override
    public String toString() {
        return this.description;
    }

    /**
     * Tests if the object is equals to this word.<br/>
     * The test is done by comparing if the object is instace of Word and then comparing the id of the object and this word
     * @param obj the object to comparing
     * @return true if the obj is equals to this word
     */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Word) ? this.id == ((Word) obj).getId() : false;
    }
}
