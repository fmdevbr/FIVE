/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved. Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.project;

import br.ufpe.cin.five.core.classification.Classification;
import br.ufpe.cin.five.core.data.DriverType;
import br.ufpe.cin.five.core.engine.Engine;
import br.ufpe.cin.five.core.extraction.Extraction;
import br.ufpe.cin.five.core.sample.Sample;
import br.ufpe.cin.five.core.speaker.Speaker;
import br.ufpe.cin.five.core.utterance.Utterance;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

/**
 * This class defines a Project.<br/>
 * It contains all information in a Project.<br/>
 * Id - the register identificator  <br/>
 * Name - the project name.<br/>
 * Type - the project type.<br/>
 * Directory - the directory of the project.<br/>
 * Utterances - the list of the utterances in the project.<br/>
 * Speakers - the list of the speakers in the project.<br/>
 * Extractions - the list of the extractions in the project.<br/>
 * Classifications - the list of the classifications in the project.<br/>
 * Samples - the list of the samples in the project.<br/>
 * Engine = the engine obtained in the end of classification.<br/>
 */
@Entity
public class Project implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  
    private int id;
    
    private String name;
    private ProjectType type;
    private String directory;
    private String dbUrl;  
    private String dbPassword;
    private String dbLogin;
    private DriverType dbDriver;
    
    @ManyToMany(cascade = CascadeType.ALL)
   // @LazyCollection(LazyCollectionOption.FALSE)
    private List<Speaker> speakers;
    
    @ManyToMany(cascade = CascadeType.ALL)
  //  @LazyCollection(LazyCollectionOption.FALSE)
    private List<Utterance> utterances;
    
    @ManyToMany(cascade = CascadeType.PERSIST)
  //  @LazyCollection(LazyCollectionOption.FALSE)
    private List<Sample> samples;
    
    @ManyToMany(cascade = CascadeType.ALL)
  //  @LazyCollection(LazyCollectionOption.FALSE)    
    private List<Extraction> extractions;    
    
 //   @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Classification> classifications;   
    
    @ManyToMany(cascade = CascadeType.ALL)
 //   @LazyCollection(LazyCollectionOption.FALSE)
    private List<Engine> engines;

    /**
     * Creates a new empty Project.
     */
    public Project() {
     
        this.speakers = new ArrayList<Speaker>();
        this.utterances = new ArrayList<Utterance>();
        this.samples = new ArrayList<Sample>();        
        this.extractions = new ArrayList<Extraction>();
        this.classifications = new ArrayList<Classification>();
        this.engines = new ArrayList<Engine>();        
    }

    /**
     * Creates a new Project specifying the name, type and directory.
     * @param name
     * @param type
     * @param directory
     */   
    public Project(String name, ProjectType type, String directory) {
        this.name = name;
        this.type = type;
        this.directory = directory;
 
        this.speakers = new ArrayList<Speaker>();
        this.utterances = new ArrayList<Utterance>();
        this.samples = new ArrayList<Sample>();
        this.extractions = new ArrayList<Extraction>();
        this.classifications = new ArrayList<Classification>();
        this.engines = new ArrayList<Engine>();
    }

    /**
     * Return the id of this project.
     * @return the id of this project.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id of this project
     * @param id the id of this project
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Returns the list of classifications, or an empty list if the project does not have it.
     * @return the list of classifications
     *
     * @see Classification
     */
    public List<Classification> getClassifications() {
        /*
        ConcreteDAO concreteDAO = new ConcreteDAO(urlMysql, loginMysql, senhaMysql);        
        return concreteDAO.getClassificationsFromProject(id);                
        * 
        */
        
        return classifications;
    }

    /**
     * Sets the list of classifications
     * @param classifications the list of classifications
     *
     * @see Classification
     */
    public void setClassifications(List<Classification> classifications) {
        this.classifications = classifications;
        
    }

    /**
     * Returns the project directory.
     * @return the project directory
     */
    public String getDirectory() {
        return directory;
    }

    /**
     * Sets the project directory.
     * @param directory the project directory
     */
    public void setDirectory(String directory) {
        this.directory = directory;
    }

    /**
     * Returns the list of extractions, or an empty list if the project does not have it.
     * @return the list of extractions
     *
     * @see Extraction
     */
    public List<Extraction> getExtractions() {
        /*
        ConcreteDAO concreteDAO = new ConcreteDAO(urlMysql, loginMysql, senhaMysql);
        
        return concreteDAO.getExtractionsFromProject(id);
        * 
        */
        
        return extractions;
        
    }

    /**
     * Sets the list of extractions.
     * @param extractions the list of extractions
     *
     * @see Extraction
     */
    public void setExtractions(List<Extraction> extractions) {
        this.extractions = extractions;
    }

    /**
     * Returns the project name.
     * @return the project name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the project name.
     * @param name the project name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Return the list of samples, or an empty list if the project does not have it.
     * @return the list of samples
     *
     * @see Sample
     */
    public List<Sample> getSamples() {
        /*
        ConcreteDAO concreteDAO = new ConcreteDAO(urlMysql, loginMysql, senhaMysql);
        
        return concreteDAO.getSamplesFromProject(id);
        * 
        */
        
        return samples;
    }

    /**
     * Sets the list of samples.
     * @param samples the list of sample
     *
     * @see Sample
     */
    public void setSamples(List<Sample> samples) {
        this.samples = samples;
    }

    /**
     * Return the list of speakers, or an empty list if the project does not have it.
     * @return the list of speakers
     *
     * @see Speaker
     */
    public List<Speaker> getSpeakers() {    
        /*
        ConcreteDAO concreteDAO = new ConcreteDAO(urlMysql, loginMysql, senhaMysql);
        
        return concreteDAO.getSpeakersFromProject(id);
        * 
        */
        return speakers;
    }

    /**
     * Sets the list of speakers.
     * @param speakers the list of speakers
     *
     * @see Speaker
     */
    public void setSpeakers(List<Speaker> speakers) {
        this.speakers = speakers;
    }

    /**
     * Returns the type of the project.
     * @return the type of the project
     *
     * @see ProjectTypes
     */
    public ProjectType getType() {
        return type;
    }

    /**
     * Sets the type of the project.
     * @param type the type of the project
     *
     * @see  ProjectTypes
     */
    public void setType(ProjectType type) {
        this.type = type;
    }

    /**
     * Return the list of utterances, or an empty list if the project does not have it.
     * @return the list of utterances
     *
     * @see Utterance
     */
    public List<Utterance> getUtterances() {
        /*
        ConcreteDAO concreteDAO = new ConcreteDAO(urlMysql, loginMysql, senhaMysql);
        
        return concreteDAO.getUtteranceFromProject(id);
        * 
        */
        return utterances;
    }

    /**
     * Sets the list of utterances.
     * @param utterances the list of utterances
     *
     * @see Utterance
     */
    public void setUtterances(List<Utterance> utterances) {
        this.utterances = utterances;
    }

    /**
     * Return the project engine.
     * @return the project engine.
     *
     * @see Utterance
     */
    public List<Engine> getEngines() {
        /*
       ConcreteDAO concreteDAO = new ConcreteDAO(urlMysql, loginMysql, senhaMysql);
        
        return concreteDAO.getEnginesFromProject(id);
        * 
        */
        
        return engines;
    }

    /**
     * Sets the project engine.
     * @param name the project name
     */    
    public void setEngines(ArrayList<Engine> engines) {
        this.engines = engines;
    }
    
    /**
     * Returns the name path of this sample.
     * @return the name path of this sample.
     */
    @Override
    public String toString() {
        return this.getName();
    }

    /**
     * Tests if the object is equals to this sample.<br/>
     * The test is done by comparing if the object is instace of Project and then comparing the name of the object and this project.
     * @param obj the object to comparing
     * @return true if the obj is equals to this sample
     */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Project) ? this.id ==  ((Project) obj).getId() : false;
    }

    /**
     * @return the dbUrl
     */
    public String getDbUrl() {
        return dbUrl;
    }

    /**
     * @param dbUrl the dbUrl to set
     */
    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    /**
     * @return the dbPassword
     */
    public String getDbPassword() {
        return dbPassword;
    }

    /**
     * @param dbPassword the dbPassword to set
     */
    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    /**
     * @return the dbLogin
     */
    public String getDbLogin() {
        return dbLogin;
    }

    /**
     * @param dbLogin the dbLogin to set
     */
    public void setDbLogin(String dbLogin) {
        this.dbLogin = dbLogin;
    }
 
    /**
     * @return the dbDriver
     */
    public DriverType getDbDriver() {
        return dbDriver;
    }

    /**
     * @param dbDriver the dbDriver to set
     */
    public void setDbDriver(DriverType dbDriver) {
        this.dbDriver = dbDriver;
    }

}
