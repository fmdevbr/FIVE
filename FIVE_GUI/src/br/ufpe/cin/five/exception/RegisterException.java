/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.exception;

/**
 *
 * @author Alexandre
 */
public class RegisterException extends Exception {

    /**
     * Constructs an instance of RegisterException with the specified detail message.
     * @param message - the detail message
     */
    public RegisterException(String message) {
        super("RegisterException: "+message);
    }

}
