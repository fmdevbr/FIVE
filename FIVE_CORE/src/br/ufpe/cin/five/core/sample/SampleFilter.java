/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved. Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.sample;

import br.ufpe.cin.five.core.speaker.Gender;
import br.ufpe.cin.five.core.speaker.Speaker;
import br.ufpe.cin.five.core.utterance.Utterance;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 *
 * @author Alexandre
 */
@Entity
public class SampleFilter implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private int id;
    private int snr;
    private int minAge;
    private int maxAge;
    
    private Gender gender;
    
    private Environment environment;
    
    @ManyToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Utterance> utterances;
    
    @ManyToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Speaker> speakers;
    
    private String waveFile;


    public SampleFilter() {

        this.minAge = -1;
        this.maxAge = -1;
        this.gender = null;
        this.environment = null;
        this.snr = -1000;
        this.utterances = new ArrayList<Utterance>();
        this.speakers = new ArrayList<Speaker>();

    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public int getSnr() {
        return snr;
    }

    public void setSnr(int snr) {
        this.snr = snr;
    }

    public List<Speaker> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(List speakers) {
        this.speakers = speakers;
    }

    public List<Utterance> getUtterances() {
        return utterances;
    }

    public void setUtterances(List<Utterance> utterances) {
        this.utterances = utterances;
    }

    public String getWaveFile() {
        return waveFile;
    }

    public void setWaveFile(String waveFile) {
        this.waveFile = waveFile;
    }


    
}
