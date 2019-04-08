/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.classification.gmm;

import br.ufpe.cin.five.core.classification.ClassificationResult;

/**
 * This class contains the necessary parameters for the gmm results presentation:<br/>
 * False Rejection Rate and False Acceptance Rate.
 */
public class GmmResult extends ClassificationResult {
    
    private double frr;
    private double far;

    /**
     * Return the False Acceptance Rate.
     * @return the False Acceptance Rate.
     */
    public double getFar() {
        return far;
    }

    /**
     * Sets the False Acceptance Rate.
     * @param False Acceptance Rate.
     */
    public void setFar(double far) {
        this.far = far;
    }

    /**
     * Return the False Rejection Rate.
     * @return the False Rejection  Rate.
     */
    public double getFrr() {
        return frr;
    }

    /**
     * Sets the False Rejection Rate.
     * @param False Rejection Rate.
     */    
    public void setFrr(double frr) {
        this.frr = frr;
    }            
}
