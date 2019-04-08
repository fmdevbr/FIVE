/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.classification.svm;

import br.ufpe.cin.five.core.classification.ClassificationException;

/**
 * Signals that an SVM exception has occurred.
 */
public class SvmException extends ClassificationException {

    /**
     * Constructs an instance of SvmException with the specified detail message.
     * @param message - the detail message
     */
    public SvmException(String message) {
        super("SvmException: "+message);
    }

}
