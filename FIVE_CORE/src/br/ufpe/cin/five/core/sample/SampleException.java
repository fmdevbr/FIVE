/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved. Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.sample;

/**
 * Signals that an Sample exception has occurred.
 */
public class SampleException extends Exception {

    /**
     * Constructs an instance of SampleException with the specified detail message.
     * @param message - the detail message
     */
    public SampleException(String message) {
        super("SampleException: "+message);
    }
}
