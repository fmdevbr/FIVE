/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.extraction;

import br.ufpe.cin.five.core.sample.Audio;

/**
 * This is an abstract class that specifies and implements the main methods necessary for the extraction process.
 */
public abstract class ExtractionProcess {                                    
    
     /**
     * Specifies the extraction process on a extraction process.
     * @param params the parameters necessary for the extraction process.
     * @param preProcessedAudio the pre-processed audio whose features you want to extract.
     * @return the extracted features.
     */
    public abstract double[] extract(Object params, Audio audio) throws ExtractionException;       
           
}

