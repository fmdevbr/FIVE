/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.extraction;

/**
 * This class provides a set of useful methods for pos-processing extraction.
 */

public class PosProcessing {

    /**
     * Applies a normalization with mean and standard deviation.
     * @param features the extracted features obtained on extraction process.
     * @return the normalized features.
     */       
    public static double[] normalization(double[] features) {        
        double media = 0;        
        for (int i = 0; i < features.length; i++) {            
            media += features[i];
        }        
        media = media/features.length;                
        double somatorio = 0;        
        for (int i = 0; i < features.length; i++) {
            somatorio += ((features[i] - media) * (features[i] - media));            
        }                
        double desvioPadrao = Math.sqrt(somatorio/features.length);              
        double[] ans = new double[features.length];        
        for (int i = 0; i < ans.length; i++) {
            ans[i] = (features[i] - media) / (desvioPadrao + 10);
        }        
        return ans;              
    }
    
    /**
     * Applies a normalization with min and max values.
     * @param features the extracted features obtained on extraction process.
     * @return the normalized features.
     */  
    public static double[] normalizationByMaxMin(double[] features) {                       
        double min = 0 , max = 0;        
        for (int i = 0; i < features.length; i++) {            
            if(features[i] > max) {
                max = features[i];
            }            
            if(features[i] < min){
                min = features[i];
            }
        }               
        double[] ans = new double[features.length];
        for (int i = 0; i < ans.length; i++) {
            ans[i] = (features[i] - min) / (max - min);
        }        
        return ans;              
    }        
}
