/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.extraction.htk;

import br.ufpe.cin.five.core.extraction.Extraction;
import javax.persistence.Entity;

/**
 * This class contains all the information necessary to do the htk extraction.<br/>
 * numMelFilters - the number of mel filters<br/>
 * coefs - the number of coefficients<br/>
 * typeMFCCvector - the type of mfcc vector<br/>
 * vad - if has to do voice active detection<br/>
 * frameDuration - the time duration of a frame<br/>
 * alphaPreEmphasis - the alpha pre-emphasis parameter<br/>
 * overlap - the over lap<br/>
 * @author Vocal Lab
 */
@Entity 
public class HtkParameters extends Extraction {
    
    private int numMelFilters;
    private int numCoefs;
    private boolean decodeProcess;

    /**
     * Creates a default HTKParameters
     */
    public HtkParameters() {
        super();
        this.numCoefs = 39;
        this.numMelFilters = 22;
    }

    /**
     * Returns the number of mel filters
     * @return the number of mel filters
     */
    public int getNumMelFilters() {
        return numMelFilters;
    }

    /**
     * Sets the number of mel filters
     * @param numMelFilters the number of mel filters
     */
    public void setNumMelFilters(int numMelFilters) {
        this.numMelFilters = numMelFilters;
    }

    /**
     * Return the number of coefficients
     * @return the number of coefficients
     */
    public int getNumCoefs() {
        return numCoefs;
    }

    /**
     * Sets the number of coefficients
     * @param coefs the number of coefficients
     */
    public void setNumCoefs(int coefs) {
        this.numCoefs = coefs;
    }

    public boolean isDecodeProcess() {
        return decodeProcess;
    }

    public void setDecodeProcess(boolean decodeProcess) {
        this.decodeProcess = decodeProcess;
    }   
    
}
