/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.extraction.mgcc;

import br.ufpe.cin.five.core.extraction.ExtractionException;

/**
 * Signals that an MFCC exception has occurred.
 */
public class MgccException extends ExtractionException {

    /**
     * Constructs an instance of MgccException with the specified detail message.
     * @param message - the detail message
     */
    public MgccException(String message) {
        super("MgccException: "+message);
    }
}
