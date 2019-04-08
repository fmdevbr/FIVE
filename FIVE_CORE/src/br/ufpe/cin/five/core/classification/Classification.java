/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.classification;

import br.ufpe.cin.five.core.sample.SampleFilter;
import java.io.Serializable;
import javax.persistence.*;

/**
 * This class defines a Classification.<br/>
 * It contains: <br/>
 * Id - the register identificator  <br/>
 * Active - the flag to verify the active register <br/>
 * Description - the description of the classification <br/>
 * Technique - the ClassificationTechinque especification <br/>
 * TrainPercentage - the train percentage of the set of samples <br/>
 * TestPercentage - the test percentage of the set of samples <br/> 
 * ThresholdValue - the value of threshold to use in a classification.<br/>
 * This is all necessary information to register one classification in a project,
 * but the information to form an acoustic model are described in the class that extends this class:<br/>
 * {@link ClassificationTechnique}<br/>
 */
@Entity
public class Classification implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private int id;
    private boolean active;
    private String description;    
    private ClassificationTechnique technique;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="sampleFilter")
    private SampleFilter sampleFilter;
    private double trainPercentage;
    private double testPercentage;
    private double thresholdValue;    
    

    /**
     * Return the id of this classification.
     * @return the id of this classification.
     */
    public int getId() { 
        return id;
    }

    /**
     * Sets the id of this classification.
     * @param id the id of this classification.
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Returns the technique of this classification,or null if the classification does not have it.
     * @return the technique of this classification.
     *
     * @see ClassificationTechniques
     */
    public ClassificationTechnique getTechnique() {
        return technique;
    }

    /**
     * Sets the technique of this classification.
     * @param technique  the technique of this classification.
     *
     * @see ClassificationTechniques
     */
    public void setTechnique(ClassificationTechnique technique) {
        this.technique = technique;
    }

    /**
     * Tests if the classification is active.
     * @return true if the classification is active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Specifies if the classification is active.
     * @param active true if the active is true
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Sets the test percentage of the classification samples set.<br/>
     * The percentage has to be between 0.0 and 1.0
     * @param testPercentage
     */
    public void setTestPercentage(double testPercentage) {
        this.testPercentage = testPercentage;
    }

    /**
     * Returns the test percentage of the classification samples set.
     * @return the test percentage of the classification samples set
     */
    public double getTestPercentage() {
        return testPercentage;
    }

    /**
     * Returns the train percentage of the classification samples set.
     * @return the train percentage of the classification samples set
     */
    public double getTrainPercentage() {
        return trainPercentage;
    }

    /**
     * Sets the train percentage of the classification samples set.
     * The percentage has to be between 0.0 and 1.0
     * @param trainPercentage
     */
    public void setTrainPercentage(double trainPercentage) {
        this.trainPercentage = trainPercentage;
    }
   
    /**
     * Return the description of this classification.
     * @return the description of this classification
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of this classification.
     * @param description the description of this classification
     */
    public void setDescription(String description) {
        this.description = description;
    }   

    /**
     * Returns the threshold value of this classification.
     * @return the threshold value of this classification
     */
    public double getThresholdValue() {
        return thresholdValue;
    }

    /**
     * Sets the threshold value of this classification.
     * @param thresholdValue the threshold value of this classification
     */
    public void setThresholdValue(double thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    /**
     * Returns the sample filter of this classification.     
     * @return the sample filter of this classification
     *
     * @see SampleFilter
     */
    public SampleFilter getSampleFilter() {
        return sampleFilter;
    }

    /**
     * Sets the sample filter of this classification
     * @param filter
     *
     * @see SampleFilter
     */
    public void setSampleFilter(SampleFilter filter) {
        this.sampleFilter = filter;
    }

    /**
     * Return the description of this classification.
     * @return the description of this classification
     */
    @Override
    public String toString() {
        return this.description;
    }

    /**
     * Tests if the object is equals to this classification.<br/>
     * The test is done by comparing if the object is instace of Classification and then comparing the id of the object and this classification.
     * @param obj the object to comparing
     * @return true if the obj is equals to this classification
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Classification ?  this.id == ((Classification) obj).getId() : false;
    }
}
