/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved. Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.project;

/**
 * Signals that an Project exception has occurred.
 */
public class ProjectException extends Exception {

    /**
     * Constructs an instance of ProjectException with the specified detail message.
     * @param message - the detail message
     */
    public ProjectException(String message) {
        super("ProjectException: "+message);
    }
}
