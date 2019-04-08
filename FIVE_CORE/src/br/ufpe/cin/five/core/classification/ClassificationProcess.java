/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.classification;

import br.ufpe.cin.five.core.sample.Sample;
import java.util.ArrayList;

/**
 * This is an abstract class that specifies and implements the main methods necessary for the classification process.
 */
public abstract class ClassificationProcess {
                 
     /**
     * Specifies the trainning process on the classification process.
     * @param params the parameters necessary for the trainning process.
     * @param trainningSamples the samples necessary for the trainning process.
     */
    public abstract void train(Object params, ArrayList<Sample> trainningSamples) throws ClassificationException;       
    
     /**
     * Specifies the test process on the classification process.
     * @param params the parameters necessary for the trainning process.
     * @param testSamples the samples necessary for the test process.
     * @return the classification result.
     */
    public abstract ClassificationResult test(Object params, ArrayList<Sample> testSamples) throws ClassificationException;       
    
     /**
     * Specifies the test in a single sample.
     * @param feature the feature vector of a test sample.
     * @param modelPath the obtained model in trainning process.
     * @return the sample' id.
     */    
    public abstract Object test(double[] feature, String modelPath) throws ClassificationException;               
}
