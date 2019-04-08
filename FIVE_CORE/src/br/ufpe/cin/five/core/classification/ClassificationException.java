/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.classification;

/**
 * Signals that an Classification exception has occurred.
 */
public class ClassificationException extends Exception {

    /**
     * Constructs an instance of ClassificationException with the specified detail message.
     * @param message - the detail message
     */
    public ClassificationException(String message) {
        super("ClassificationException: "+message);
    }
}
