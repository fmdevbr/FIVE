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
 * This class defines a Phrase.<br/>
 * It contains information about a phrase, like:<br/>
 * Description - the phrase itself.<br/>
 * Words - the words of the phrase.<br/>
 * QuestionFlag - Specifies if is the phrase is an interrogative phrase.
 */
@Entity
public class Phrase implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private int id;
    @Column(columnDefinition="TEXT")
    private String description;
    @ManyToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Word> words;
    private Boolean questionFlag;
    public static final String PHRASE_SEPARATORS = "(\\.|\\!|\\?)";
    public static final String PHRASE_MARKS = "[\\.\\-\\(\\);:,\\?!<>\\[\\]\\{\\}]";

    /**
     * Creates a new empty Phrase
     */
    public Phrase() {
        this.words = new ArrayList<Word>();
    }  

    /**
     * Returns the description of this phrase.
     * @return the description of this phrase
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the descrition if this phrase.
     * @param description the description of this phrase
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the list of the words, or an empty list if the phrase does not have it.
     * @return the list of the words
     */
    public List<Word> getWords() {
        return words;
    }

    /**
     * Sets the list of the words of this phrase.
     * @param words the list of the words of this phrase
     */
    public void setWords(List<Word> words) {
        this.words = words;
    }

    /**
     * Tests if this phrase is an interrogative phrase.
     * @return true if this phrase is an interrogative phrase
     */
    public Boolean getQuestionFlag() {
        return questionFlag;
    }

    /**
     * Spcifies if this phrase is an interrogative phrase.
     * @param questionFlag true if is the phrase is an interrogative phrase
     */
    public void setQuestionFlag(Boolean questionFlag) {
        this.questionFlag = questionFlag;
    }

    /**
     * Return the description of this phrase.
     * @return the description of this phrase
     */
    @Override
    public String toString() {
        return this.description;
    }

    /**
     * Tests if the object is equals to this phrase.<br/>
     * The test is done by comparing if the object is instace of Phrase and then comparing the id of the object and this phrase
     * @param obj the object to comparing
     * @return true if the obj is equals to this phrase
     */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Phrase) ? this.getDescription().equals(((Phrase) obj).getDescription()) : false;
    }
}
