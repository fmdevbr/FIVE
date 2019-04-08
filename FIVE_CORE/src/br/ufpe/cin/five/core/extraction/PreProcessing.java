/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.extraction;

/**
 * This class provides a set of useful methods for pre-processing extraction.
 */

public class PreProcessing {     
   
    /**
     * Applies a preEmphasis filter on audio.
     * @param inputSignal the read audio vector.
     * @param alpha the percentage of filtering.
     * @return the filtered audio.
     */      
    public static double[] preEmphasis(short[] inputSignal, double alpha) {
        double[] outputSignal = new double[inputSignal.length];
        outputSignal[0] = inputSignal[0];
        for (int i = 1; i < inputSignal.length; i++) {
            outputSignal[i] = inputSignal[i] - alpha * inputSignal[i - 1];
        }
        return outputSignal;
    }     
    
    /**
     * Applies a overlap process on audio.
     * @param inputSignal the read audio vector.
     * @param frameLength the lenght of the frame.
     * @param overlap the percentage of overlap.
     * @return the overlaped audio.
     */     
    public static double[][] overlapping(double[] inputSignal, int frameLength, double overlap) {
        int shiftInterval = (int) (frameLength * overlap);
        //Numero de frames
        int numFrames = (int) Math.ceil((double) inputSignal.length / (frameLength - shiftInterval));
        //Retorno
        double returnFraming[][] = new double[numFrames][frameLength];
        //Copiando os frames para posterior overlap
        double paddedSignal[] = new double[numFrames * frameLength];
        System.arraycopy(inputSignal, 0, paddedSignal, 0, inputSignal.length);
        //Fazendo o overlap e criando os frames
        for (int m = 0; m < numFrames; m++) {
            for (int n = 0; n < frameLength; n++) {
                returnFraming[m][n] = paddedSignal[m * (frameLength - shiftInterval) + n];
            }
        }
        return returnFraming;
    }           

    /**
     * Applies a windowing process on audio.
     * @param inputSignal the read audio vector.
     * @param alpha the percentage of windowing.
     * @param frameLength the lenght of the frame.
     * @return the windowed audio.
     */    
    public static double[][] windowing(double[][] inputSignal, double alpha, int frameLength) {
        double w[] = new double[frameLength];
        for (int i = 0; i < frameLength; i++) {
            w[i] = (1 - alpha) - alpha * Math.cos((2 * Math.PI * ((double) i / (frameLength - 1))));
        }
        for (int m = 0; m < inputSignal.length; m++) {
            for (int n = 0; n < frameLength; n++) {
                inputSignal[m][n] = inputSignal[m][n] * w[n];
            }
        }
        return inputSignal;
    }                
}
