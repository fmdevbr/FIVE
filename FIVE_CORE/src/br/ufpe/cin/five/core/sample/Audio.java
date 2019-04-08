/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved. Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.sample;

import javax.sound.sampled.AudioFormat;

/**
 * This class defines a Audio.<br/>
 * It contains all information in an Audio.<br/>
 * Path - the file path indicator<br/>
 * AudioData - the data contained on audio file.<br/>
 * AudioFormat - the format of audio file.<br/>
 */
public class Audio {

    private String audioName;
    private String audioPath;
    private short[] audioData;
    private AudioFormat audioFormat;

    /**
     * Creates a new empty Audio.
     */
    public Audio() {}
    
    /**
     * Creates a new Audio specifying the audio format.
     * @param audioFormat
     */  
    public Audio(AudioFormat audioFormat) {
        this.audioFormat = audioFormat;
    }    
    
    /**
     * Return the audioData of this audio.
     * @return the audioData of this audio.
     */    
    public short[] getAudioData() {
        return audioData;
    }

    /**
     * Sets the audioData of this audio.
     * @param audioData the id of this audio.
     */
    public void setAudioData(short[] audioData) {
        this.audioData = audioData;
    }

    /**
     * Return the name of this audio.
     * @return the name of this audio.
     */  
    public String getAudioName() {
        return audioName;
    }

    /**
     * Sets the name of this audio.
     * @param name the id of this audio.
     */
    public void setAudioName(String fileName) {
        this.audioName = fileName;
    }

    /**
     * Return the name of this audio.
     * @return the name of this audio.
     */  
    public String getAudioPath() {
        return audioPath;
    }

    /**
     * Sets the name of this audio.
     * @param name the id of this audio.
     */
    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }    
    
    /**
     * Return the audioFormat of this audio.
     * @return the audioformat of this audio.
     */  
    public AudioFormat getAudioFormat() {
        return audioFormat;
    }
  
    /**
     * Sets the audioFormat of this audio.
     * @param audioFormat the id of this audio.
     */    
    public void setAudioFormat(AudioFormat audioFormat) {
        this.audioFormat = audioFormat;
    }
}
