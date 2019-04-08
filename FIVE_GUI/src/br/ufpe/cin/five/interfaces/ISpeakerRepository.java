/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.interfaces;

import br.ufpe.cin.five.core.speaker.SpeakerException;
import br.ufpe.cin.five.core.speaker.Speaker;

/**
 *
 * @author Alexandre
 */
public interface ISpeakerRepository {

    public abstract void insert(Speaker speaker) throws SpeakerException;
    public abstract void update(Speaker speaker) throws SpeakerException;
    public abstract void remove(Speaker speaker) throws SpeakerException;
    public Speaker search(int id) throws SpeakerException;

}
