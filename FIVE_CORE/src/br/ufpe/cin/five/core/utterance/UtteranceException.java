/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved. Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.utterance;
  
/**
 * Signals that an Classification exception has occurred.
 */
public class UtteranceException extends Exception {

    /**
     * Constructs an instance of UtteranceException with the specified detail message.
     * @param message - the detail message
     */
    public UtteranceException(String message) {
        super("UtteranceException: "+message);
    }
}
