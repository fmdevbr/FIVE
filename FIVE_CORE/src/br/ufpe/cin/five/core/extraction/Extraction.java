/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.extraction;

import br.ufpe.cin.five.core.sample.SampleFilter;
import java.io.Serializable;
import javax.persistence.*;

/**
 * This class defines an Extraction.<br/>
 * It contains: <br/>
 * Id - the register identificator  <br/>
 * Active - the flag to verify the active register <br/>
 * Description - the description of the extraction <br/>
 * Technique - the ExtractionTechinque especification <br/>
 * VAD (Voice Activity Detection) - flag option for VAD use.
 * Normalize - flag option for normalization use.
 * Frame Duration - the miliseconds number for frame duration.
 * PreEmphasis - the alpha pre empahis especification.
 * Overlap - the percentage of signal overlap.
 * Windowing - the WindowingTechnique specification.
 * This is all necessary information to register one extraction in a project,
 * but the information to form an acoustic model are described in the class tha extends this class:<br/>
 * {@link ExtractionTechnique}<br/>
 * {@link WindowingTechnique}<br/>
 */
@Entity
public class Extraction implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private boolean active;
    private String description;
    @Enumerated(EnumType.STRING)
    private ExtractionTechnique technique;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="sampleFilter")
    private SampleFilter sampleFilter;
    private boolean vad;
    private boolean normalized;
    private int frameDuration;
    private double preEmphasis;
    private double overlap;    
    private WindowingTechnique windowing;       

    /**
     * Return the id of this extraction.
     * @return the id of this extraction.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id of this extraction
     * @param id the id of this extraction
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Tests if the extraction is active.
     * @return true if the languageModel is active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Specifies if the extraction is active.
     * @param active true if the active is true
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Returns the technique of this extraction, or null if the extraction does not have it.
     * @return the technique of this extraction
     *
     * @see ExtractionTechniques
     */
    public ExtractionTechnique getTechnique() {
        return technique;
    }

    /**
     * Sets the technique of this extraction.
     * @param technique the technique of this extraction
     *
     * @see ExtractionTechniques
     */
    public void setTechnique(ExtractionTechnique technique) {
        this.technique = technique;
    }

    /**
     * Returns the description of this extraction, or null if the extraction does not have it.
     * @return the description of this extraction
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of this extraction.
     * @param description the description of this extraction
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the sample filter of this extraction, or null if the extraction does not have it.
     * @return the sample filter of this extraction
     *
     * @see SampleFilter
     */
    public SampleFilter getSampleFilter() {
        return sampleFilter;
    }

    /**
     * Sets the sample filter of this extraction.
     * @param filter the sample filter of this extraction
     *
     * @see SampleFilter
     */
    public void setSampleFilter(SampleFilter filter) {
        this.sampleFilter = filter;
    }     

    /**
     * Tests if has to use voice active detection(VAD)
     * @return true if has to use voice active detection(VAD)
     */
    public boolean isVad() {
        return vad;
    }

    /**
     * Specifies if has to use voice active detection(VAD)
     * @param vad true if has to use voice active detection(VAD)
     */
    public void setVad(boolean vad) {
        this.vad = vad;
    }

    /**
     * Tests if has to use normalize function
     * @return true if has to use normalize function
     */
    public boolean isNormalized() {
        return normalized;
    }

    /**
     * Specifies if has to use normalize function
     * @param noiseSup true if has to use normalize function
     */
    public void setNormalize(boolean normalize) {
        this.normalized = normalize;
    }
    
    /**
     * Returns the time duration of a frame
     * @return the time duration of a frame
     */
    public int getFrameDuration() {
        return frameDuration;
    }

    /**
     * Sets the time duration of a frame
     * @param frameDuration the time duration of a frame
     */
    public void setFrameDuration(int frameDuration) {
        this.frameDuration = frameDuration;
    }

    /**
     * Returns the pre emphasis
     * @return the pre emphasis
     */
    public double getPreEmphasis() {
        return preEmphasis;
    }

    /**
     * Sets the  pre emphasis
     * @param PreEmphasis the alpha pre emphasis
     */
    public void setPreEmphasis(double alphaPreEmphasis) {
        this.preEmphasis = alphaPreEmphasis;
    }

    /**
     * Returns the percentage of overlap
     * @return the percentage of overlap
     */
    public double getOverlap() {
        return overlap;
    }

    /**
     * Sets the percentage of overlap
     * @param overlap the percentage of overlap
     */
    public void setOverlap(double overLap) {
        this.overlap = overLap;
    }

    /**
     * Returns the windowing technique
     * @return the windowing technique
     * @see WindowingTechniques
     */
    public WindowingTechnique getWindowing() {
        return windowing;
    }

    /**
     * Sets the windowing technique
     * @param windowing
     * @see WindowingTechniques
     */
    public void setWindowing(WindowingTechnique windowing) {
        this.windowing = windowing;
    }    
    
    /**
     * Return the description of this extraction.
     * @return the description of this extraction
     */
    @Override
    public String toString() {
        return this.description;
    }
    
    /**
     * Tests if the object is equals to this extraction.<br/>
     * The test is done by comparing if the object is instace of Extraction and then comparing the id of the object and this extraction
     * @param obj the object to comparing
     * @return true if the obj is equals to this extraction
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Extraction ? this.id == ((Extraction) obj).getId() : false;
    }
}
