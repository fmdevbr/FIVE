/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.extraction.mfcc;

import br.ufpe.cin.five.core.extraction.Extraction;
import javax.persistence.Entity;

/**
 * This class contains all the information necessary to do the mfcc extraction.<br/>
 * numMelFilters - the number of mel filters<br/>
 * numCoefs - the number of coefficients<br/>
 * powerSpectrum - if has to use power spectrun instead magnitude spectrum<br/>
 * lowerFreq - the lower frequency<br/>
 * upperFreq - the upper freqeuncy<br/>
 * frameLength - the lenght of frame<br/>
 */
@Entity
public class MfccParameters extends Extraction {

    
    private int numMelFilters;
    private int numCoefs;
    private boolean powerSpectrum;
    private double lowerFreq;
    private double upperFreq;
    private int frameLength;

    /**
     * Creates a default MFCCParameters
     */
    public MfccParameters() {
        super();        
        this.numMelFilters = 40;
        this.numCoefs = 13;
        this.powerSpectrum = true;
        this.lowerFreq = 100;
        this.upperFreq = -1;
        this.frameLength = -1;        
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
     * @param numMelFilters Sets the number of mel filters
     */
    public void setNumMelFilters(int numMelFilters) {
        this.numMelFilters = numMelFilters;
    }

    /**
     * Returns the number of coefficients
     * @return the number of coefficientss
     */
    public int getNumCoefs() {
        return numCoefs;
    }

    /**
     * Sets the number of coefficient
     * @param coefs the number of coefficient
     */
    public void setNumCoefs(int coefs) {
        this.numCoefs = coefs;
    }

    /**
     * Tests if has to use power spectrum instead of magnitude spectrum
     * @return true if has to use power spectrum
     */
    public boolean isPowerSpectrum() {
        return powerSpectrum;
    }

    /**
     * Specifies if has to use power spectrum instead of magnitude spectrum
     * @param powerSpectrum true if has to use power spectrum
     */
    public void setPowerSpectrum(boolean powerSpectrum) {
        this.powerSpectrum = powerSpectrum;
    }

    /**
     * Returns the lower frequency of mel filter bank
     * @return the lower frequency of mel filter bank
     */
    public double getLowerFreq() {
        return lowerFreq;
    }

    /**
     * Sets the lower frequency of mel filter bank
     * @param lowerFreq the lower frequency of mel filter bank
     */
    public void setLowerFreq(double lowerFreq) {
        this.lowerFreq = lowerFreq;
    }

    /**
     * Returns the upper frequency of mel filter bank
     * @return the upper frequency of mel filter bank
     */
    public double getUpperFreq() {
        return upperFreq;
    }

    /**
     * Sets the upper frequency of mel filter bank
     * @param upperFreq the upper frequency of mel filter bank
     */
    public void setUpperFreq(double upperFreq) {
        this.upperFreq = upperFreq;
    }  

    /**
     * Returns the frame length
     * @return the frame length
     */
    public int getFrameLength() {
        return frameLength;
    }

    /**
     * Sets the frame length
     * @param frameLength true the frame length
     */
    public void setFrameLength(int frameLength) {
        this.frameLength = frameLength;
    }    
}
