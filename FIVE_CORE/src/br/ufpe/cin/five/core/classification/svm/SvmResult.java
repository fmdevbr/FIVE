/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.classification.svm;

import br.ufpe.cin.five.core.classification.ClassificationResult;

/**
 * This class contains the necessary parameters for the svm results presentation:<br/>
 * error, confusion matrix.
 */
public class SvmResult extends ClassificationResult {
    
    double error;
    int[][] confusionMatrix;

    /**
     * Return the confusion matrix obtained on a svm process.
     * @return the confusion matrix.
     */    
    public int[][] getConfusionMatrix() {
        return confusionMatrix;
    }

    /**
     * Sets the confusion matrix obtained ona svm process.
     * @param confusion matrix.
     */
    public void setConfusionMatrix(int[][] confusionMatrix) {
        this.confusionMatrix = confusionMatrix;
    }

    /**
     * Return the error rate obtained on a svm process.
     * @return the error rate.
     */    
    public double getError() {
        return error;
    }

    /**
     * Sets the error rate obtained on a svm process.
     * @param error rate.
     */    
    public void setError(double error) {
        this.error = error;
    }        
}
