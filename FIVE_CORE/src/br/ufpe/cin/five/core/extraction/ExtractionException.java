/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.extraction;

/**
 * Signals that an Classification exception has occurred.
 */
public class ExtractionException extends Exception {

    /**
     * Constructs an instance of ExtractionException with the specified detail message.
     * @param message - the detail message
     */
    public ExtractionException(String message) {
        super("ExtractionException: "+message);
    }
}
