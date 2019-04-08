/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved. Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.engine;

import br.ufpe.cin.five.core.classification.Classification;
import br.ufpe.cin.five.core.extraction.Extraction;
import br.ufpe.cin.five.core.project.ProjectType;
import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * This class defines a engine.<br/>
 * It contains all information in a engine.<br/>
 * Config - the config information necessary for engine.<br/>
 * Model - the models used on engine.<br/>
 */
@Entity
public class Engine implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String description;   
    private ProjectType projectType;
    @ManyToOne( cascade = {CascadeType.PERSIST} )
    @LazyCollection(LazyCollectionOption.FALSE)    
    private Extraction extraction;
    @ManyToOne( cascade = {CascadeType.PERSIST} )
    @LazyCollection(LazyCollectionOption.FALSE)    
    private Classification classification;
    private String modelFile;

    /**
     * Creates a new empty Engine.
     */
    public Engine() {
        
    }

    /**
     * Return the id of this engine.
     * @return the id of this engine.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id of this engine
     * @param id the config of this engine
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Return the description of this engine.
     * @return the description of this engine.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of this engine
     * @param description the model of this engine
     */
    public void setDescription(String description) {
        this.description = description;
    }    
    
    /**
     * Return the projectType of this engine.
     * @return the projectType of this engine.
     */
    public ProjectType getProjectType() {
        return projectType;
    }

    /**
     * Sets the projectType of this engine
     * @param projectType the config of this engine
     */
    public void setProjectType(ProjectType projectType) {
        this.projectType = projectType;
    }

    /**
     * Return the extraction of this engine.
     * @return the extraction of this engine.
     */
    public Extraction getExtraction() {
        return extraction;
    }

    /**
     * Sets the extraction of this engine
     * @param extraction the model of this engine
     */
    public void setExtraction(Extraction extraction) {
        this.extraction = extraction;
    }
    
    /**
     * Return the classification of this engine.
     * @return the classification of this engine.
     */
    public Classification getClassification() {
        return classification;
    }

    /**
     * Sets the classification of this engine
     * @param classification the model of this engine
     */
    public void setClassification(Classification classification) {
        this.classification = classification;
    }    
    
    /**
     * Return the modelFile of this engine.
     * @return the modelFile of this engine.
     */
    public String getModelFile() {
        return modelFile;
    }

    /**
     * Sets the modelFile of this engine
     * @param modelFile the model of this engine
     */
    public void setModelFile(String modelFile) {
        this.modelFile = modelFile;
    }       
    
    /**
     * Return the description of this engine.
     * @return the description of this engine
     */
    @Override
    public String toString() {
        return this.description;
    }

    /**
     * Tests if the object is equals to this engine.<br/>
     * The test is done by comparing if the object is instace of Classification and then comparing the id of the object and this classification.
     * @param obj the object to comparing
     * @return true if the obj is equals to this engine
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Engine ?  this.id == ((Engine) obj).getId() : false;
    }    
 
}
