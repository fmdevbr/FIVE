/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.extraction;

/**
 * This class provides a set of useful methods for extraction process.
 */
public class ExtractionUtil {
    
    /**
     * Calculates the frame length from sample rate and frame duration
     * @param sampleRate sample rate of audio.
     * @param frameDuration frame duration specifies for the user.
     * @return frame length.
     */      
    public static int calcFrameLength(double sampleRate, double frameDuration) {
        int frameLengthpower2 = 1;
        int duration = (int) (1 / (frameDuration * 0.001));
        double testDuration = sampleRate;
        double lastTestDuration = testDuration;
        while (testDuration > duration) {
            frameLengthpower2 = frameLengthpower2 * 2;
            lastTestDuration = testDuration;
            testDuration = sampleRate / frameLengthpower2;
        }
        if (Math.abs(testDuration - duration) < Math.abs(lastTestDuration - duration)) {
            return frameLengthpower2;
        } else {
            return frameLengthpower2 / 2;
        }
    }         
}
