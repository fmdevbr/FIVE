/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.extraction.htk;

import br.ufpe.cin.five.core.extraction.ExtractionException;

/**
 * Signals that an HTK exception has occurred.
 */
public class HtkException extends ExtractionException {

    /**
     * Constructs an instance of HtkException with the specified detail message.
     * @param message - the detail message
     */
    public HtkException(String message) {
        super("HtkException: "+message);
    }
}
