/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved. Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.sample;

import br.ufpe.cin.five.core.speaker.Speaker;
import br.ufpe.cin.five.core.utterance.Utterance;
import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * This class denfines a Sample.<br/>
 * It contains information about the sample, like: <br/>
 * Id - the register identificator  <br/>
 * Utterance - the utterance that was talked.
 * Speaker - the spekaer that talked.
 * Environment - the environment that the recording was recorded. 
 * AudioFile - the wave file path that was recorded.
 * FeatureFile - the feature file path that was extracted.
 * Genuine - if the sample belongs toa a genuine speaker
 * Snr - the signal-to-noise-ratio. 
 */
@Entity
public class  Sample implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)   
    private int id;
    @ManyToOne( cascade = {CascadeType.PERSIST} )
    @LazyCollection(LazyCollectionOption.FALSE)
    private Utterance utterance;
    @ManyToOne( cascade = {CascadeType.PERSIST} )
    @LazyCollection(LazyCollectionOption.FALSE)
    private Speaker speaker;    
    private Environment environment;
    private String audioFile;
    private String featureFile;
    private boolean genuine;
    private int snr;

    /**
     * Creates a new empty Sample
     */
    public Sample() {
        this.utterance = new Utterance();
        this.speaker = new Speaker();
    }

    /**
     * Creates a new Sample specifying the utterance, speaker, environment and audioFile.
     * @param utterance
     * @param speaker
     * @param environment
     * @param audioFile
     */
    public Sample(Utterance utterance, Speaker speaker, Environment environment, String audioFile) {
        this.utterance = utterance;
        this.speaker = speaker;
        this.environment = environment;
        this.audioFile = audioFile;
    }

    /**
     * Return the id of this sample.
     * @return the id of this sample.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id of this sample
     * @param id the id of this sample
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Returns the environment of this sample.
     * @return the environment of this sample
     * @see Environment
     *
     */
    public Environment getEnvironment() {
        return environment;
    }

    /**
     * Sets the environment of this sample
     * @param environment the environment of this sample
     * @see Environment
     */
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /**
     * Returns the feature file path of this sample.
     * @return the feature file path of this sample
     */
    public String getFeatureFile() {
        return featureFile;
    }

    /**
     * Sets the feature file path of this sample
     * @param featureFiles the feature file path of this sample
     */
    public void setFeatureFile(String featureFiles) {
        this.featureFile = featureFiles;
    }

    /**
     * Returns the signal-to-noise-ratio of this sample
     * @return the signal-to-noise-ratio of this sample
     */
    public int getSnr() {
        return snr;
    }

    /**
     * Sets the signal-to-noise-ratio of this sample.
     * @param snr the signal-to-noise-ratio of this sample
     */
    public void setSnr(int snr) {
        this.snr = snr;
    }

    /**
     * Returns the id of the speaker of this sample.
     * @return the id of the speaker
     */
    public Speaker getSpeaker() {
        return speaker;
    }

    /**
     * Sets the id of the spekaer of this sample.
     * @param speaker the id of the spekaer
     */
    public void setSpeaker(Speaker speaker) {
        this.speaker = speaker;
    }

    /**
     * Returns the utterance id of this sample.
     * @return the utterance id of this sample
     */
    public Utterance getUtterance() {
        return utterance;
    }

    /**
     * Sets the utterance id of this sample
     * @param utterance id the utterance of this sample
     */
    public void setUtterance(Utterance utterance) {
        this.utterance = utterance;
    }

    /**
     * Returns the audioFile path of this sample.
     * @return the audioFile path of this sample
     */
    public String getAudioFile() {
        return audioFile;
    }

    /**
     * Sets the wave file path of this sample.
     * @param audioFile the audioFile path of this sample
     */
    public void setAudioFile(String audioFile) {
        this.audioFile = audioFile;
    }

    /**
     * Specifies if is a genuine sample
     * @param vad true if is a genuine sample
     */
    public void setGenuine(boolean genuine) {
        this.genuine = genuine;
    }

    /**
     * Tests if is a genuine sample
     * @return true if is a genuine sample
     */
    public boolean isGenuine() {
        return genuine;
    }

    /**
     * Returns the audioFile path of this sample.
     * @return the audioFile path of this sample.
     */
    @Override
    public String toString() {
        return this.getAudioFile();
    }

    /**
     * Tests if the object is equals to this sample.<br/>
     * The test is done by comparing if the object is instace of Sample and then comparing the audioFile of the object and this sample.
     * @param obj the object to comparing
     * @return true if the obj is equals to this sample
     */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Sample) ? this.id ==  ((Sample) obj).getId() : false;
    }
}
