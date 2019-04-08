/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.extraction;

/**
 * This class defines the audio pre-processed.<br/>
 * It has:<br/>
 * Frames<br/>
 * Sample Rate<br/>
 * Frame Length<br/>
 * Number of Frames<br/>
 */
public class PreProcessedAudio {

    private double[][] frames;
    private double sampleRate;
    private int frameLenght;
    private int numFrames;

    /**
     * Creates a new Audio Pre-Processed.
     * @param frames the frames
     * @param sampleRate the sample rate
     * @param frameLenght the frame length
     * @param numFrames the number of frames
     */
    public PreProcessedAudio(double[][] frames, double sampleRate, int frameLenght, int numFrames) {
        this.frames = frames;
        this.sampleRate = sampleRate;
        this.frameLenght = frameLenght;
        this.numFrames = numFrames;
    }

    /**
     * Returns the frames of this pre-processed audio
     * @return the frames of this pre-processed audio
     */
    public double[][] getFrames() {
        return frames;
    }

    /**
     * Sets the frames of this pre-processed audio
     * @param frames of this pre-processed audio
     */
    public void setFrames(double[][] frames) {
        this.frames = frames;
    }
    
    /**
     * Returns the sample rate of this pre-processed audio
     * @return the sample rate of this pre-processed audio
     */
    public double getSampleRate() {
        return sampleRate;
    }

    /**
     * Sets the sample rate of this pre-processed audio
     * @param sample rate of this pre-processed audio
     */    
    public void setSampleRate(double sampleRate) {
        this.sampleRate = sampleRate;
    }    
    
    /**
     * Returns the frame length of this pre-processed audio
     * @return frame lenght of this pre-processed audio
     */
    public int getFrameLenght() {
        return frameLenght;
    }
    
    /**
     * Sets the frame length of this pre-processed audio
     * @param frame length of this pre-processed audio
     */
    public void setFrameLenght(int frameLenght) {
        this.frameLenght = frameLenght;
    }    

    /**
     * Returns the number of frames of this pre-processed audio
     * @return the number of frames of this pre-processed audio
     */
    public int getNumFrames() {
        return numFrames;
    }

    /**
     * Sets the number of frames of this pre-processed audio
     * @param number of frames of this pre-processed audio
     */
    public void setNumFrames(int numFrames) {
        this.numFrames = numFrames;
    }        
}
