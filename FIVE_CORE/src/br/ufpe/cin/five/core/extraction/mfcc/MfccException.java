/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.extraction.mfcc;

import br.ufpe.cin.five.core.extraction.ExtractionException;

/**
 * Signals that an MFCC exception has occurred.
 */
public class MfccException extends ExtractionException {

    /**
     * Constructs an instance of MFCCException with the specified detail message.
     * @param message - the detail message
     */
    public MfccException(String message) {
        super("MFCCException: "+message);
    }
}
