/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved. Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.engine;

import br.ufpe.cin.five.core.dictionary.Dictionary;
import br.ufpe.cin.five.core.grammar.Grammar;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * This class defines a speech engine.<br/>
 * It contains all information in a speech engine.<br/>
 * Dictionary - the dictionary used on engine.<br/>
 * Grammar - the grammar used on engine.<br/>
 */
@Entity
public class SpeechEngine extends Engine {

    @ManyToOne( cascade = {CascadeType.ALL} )
    //@LazyCollection(LazyCollectionOption.FALSE)
    private Dictionary dictionary;
    @ManyToOne( cascade = {CascadeType.ALL} )
    //@LazyCollection(LazyCollectionOption.FALSE)
    private Grammar grammar;

    /**
     * Creates a new empty SpeechEngine.
     */
    public SpeechEngine() {
        
    }

    /**
     * Return the dictionary of this engine.
     * @return the dictionary of this engine.
     */
    public Dictionary getDictionary() {
        return dictionary;
    }

    /**
     * Sets the dictionary of this engine
     * @param dictionary the config of this engine
     */
    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    /**
     * Return the grammar of this engine.
     * @return the grammar of this engine.
     */
    public Grammar getGrammar() {
        return grammar;
    }

    /**
     * Sets the grammar of this engine
     * @param grammar the model of this engine
     */
    public void setGrammar(Grammar grammar) {
        this.grammar = grammar;
    }         
}
