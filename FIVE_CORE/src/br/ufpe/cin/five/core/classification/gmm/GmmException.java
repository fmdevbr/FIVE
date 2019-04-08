/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.classification.gmm;

import br.ufpe.cin.five.core.classification.ClassificationException;

/**
 * Signals that an GMM exception has occurred.
 */
public class GmmException extends ClassificationException {

    /**
     * Constructs an instance of GmmException with the specified detail message.
     * @param message - the detail message
     */
    public GmmException(String message) {
        super("GmmException: "+message);
    }

}
