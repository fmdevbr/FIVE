/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved. Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.engine;

import br.ufpe.cin.five.core.speaker.Speaker;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * This class defines a speech engine.<br/>
 * It contains all information in a speech engine.<br/>
 * Speaker - the speaker used on engine.<br/>
 * Password - the password used on engine.<br/>
 */
@Entity
public class SpeakerEngine extends Engine {

    @ManyToMany(cascade = CascadeType.PERSIST)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Speaker> speakers;

    /**
     * Creates a new empty SpeechEngine.
     */
    public SpeakerEngine() {
        
    }

     /**
     * Return the speaker of this engine.
     * @return the speaker of this engine.
     */
    public List<Speaker> getSpeakers() {
        return speakers;
    }

    /**
     * Sets the speaker of this engine
     * @param speaker the config of this engine
     */
    public void setSpeakers(List<Speaker> speakers) {
        this.speakers = speakers;
    }   
}
