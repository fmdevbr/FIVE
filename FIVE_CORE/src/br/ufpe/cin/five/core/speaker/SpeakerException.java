/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved. Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.speaker;

/**
 * Signals that an SpeakerException exception has occurred.
 */
public class SpeakerException extends Exception {

    /**
     * Constructs an instance of SpeakerException with the specified detail message.
     * @param message - the detail message
     */
    public SpeakerException(String message) {
        super("SpeakerException: "+message);
    }
}
