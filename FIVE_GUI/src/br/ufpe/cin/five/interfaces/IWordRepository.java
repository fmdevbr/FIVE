/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.interfaces;

import br.ufpe.cin.five.core.utterance.UtteranceException;
import br.ufpe.cin.five.core.utterance.Word;

/**
 *
 * @author Alexandre
 */
public interface IWordRepository {

    public abstract void insert(Word word) throws UtteranceException;
    public abstract void update(Word word) throws UtteranceException;
    public abstract void remove(Word word) throws UtteranceException;

}
