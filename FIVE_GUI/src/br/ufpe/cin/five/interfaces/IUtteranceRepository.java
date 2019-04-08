/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.interfaces;

import br.ufpe.cin.five.core.utterance.Utterance;
import br.ufpe.cin.five.core.utterance.UtteranceException;

/**
 *
 * @author Alexandre
 */
public interface IUtteranceRepository {

    public abstract void insert(Utterance utterance) throws UtteranceException;
    public abstract void update(Utterance utterance) throws UtteranceException;
    public abstract void remove(Utterance utterance) throws UtteranceException;
    public Utterance search(int id) throws UtteranceException;

}
