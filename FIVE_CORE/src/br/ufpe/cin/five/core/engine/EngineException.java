/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved. Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.engine;

/**
 * Signals that an Engine exception has occurred.
 */
public class EngineException extends Exception {

    /**
     * Constructs an instance of EngineException with the specified detail message.
     * @param message - the detail message
     */
    public EngineException(String message) {
        super("EngineException: "+message);
    }
}
