/**
 * Copyright 2011 Federal University of Pernambuco. All Rights Reserved. Use is
 * subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.facade;

import br.ufpe.cin.five.core.classification.Classification;
import br.ufpe.cin.five.core.data.ConcreteDAO;
import br.ufpe.cin.five.core.engine.Engine;
import br.ufpe.cin.five.core.extraction.Extraction;
import br.ufpe.cin.five.gui.dialogs.FiveWorker;
import br.ufpe.cin.five.core.project.Project;
import br.ufpe.cin.five.register.ExtractionRegister;
import br.ufpe.cin.five.register.ProjectRegister;
import br.ufpe.cin.five.register.SampleRegister;
import br.ufpe.cin.five.register.SpeakerRegister;
import br.ufpe.cin.five.register.ClassificationRegister;
import br.ufpe.cin.five.register.UtteranceRegister;
import br.ufpe.cin.five.register.WordRegister;
import br.ufpe.cin.five.core.sample.Sample;
import br.ufpe.cin.five.core.sample.SampleFilter;
import br.ufpe.cin.five.core.speaker.Speaker;
import br.ufpe.cin.five.core.utterance.Utterance;
import br.ufpe.cin.five.exception.RegisterException;
import br.ufpe.cin.five.register.EngineRegister;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alexandre
 */
public class Facade {

    private static Facade instance = null;
    private Project project = null;
    private ClassificationRegister classificationRegister;
    private ExtractionRegister extractionRegister;
    private SampleRegister sampleRegister;
    private SpeakerRegister speakerRegister;
    private UtteranceRegister utteranceRegister;
    private WordRegister wordRegister;
    private EngineRegister engineRegister;
    private ConcreteDAO concreteDAO;

    public ConcreteDAO getConcreteDAO() {
        return concreteDAO;
    }

    public void setConcreteDAO(ConcreteDAO concreteDAO) {
        this.concreteDAO = concreteDAO;
    }

    private Facade() {
    }

    public static Facade getInstance() {
        if (instance == null) {
            instance = new Facade();
            return instance;
        } else {
            return instance;
        }
    }

    public void initProjectRegisters() {
        if (concreteDAO == null) {
            concreteDAO = new ConcreteDAO(project.getDbUrl(), project.getDbLogin(), project.getDbPassword(), project.getDbDriver());
        }

        this.wordRegister = new WordRegister(project);
        this.sampleRegister = new SampleRegister(project);
        this.speakerRegister = new SpeakerRegister(project);
        this.speakerRegister = new SpeakerRegister(project);
        this.utteranceRegister = new UtteranceRegister(project);
        this.extractionRegister = new ExtractionRegister(project);
        this.classificationRegister = new ClassificationRegister(project);
        this.engineRegister = new EngineRegister(project);
    }

    // MÉTODOS DE CLASSIFICATION
    public void insertClassification(Classification classification) throws RegisterException {
        this.classificationRegister.insert(classification);
    }

    public void updateClassification(Classification classification) throws RegisterException {
        this.classificationRegister.update(classification);
    }

    public void removeClassification(Classification classification) throws RegisterException {
        this.classificationRegister.remove(classification);
    }

    public Classification searchClassification(int id) throws RegisterException {
        return this.classificationRegister.search(id);
    }

    public Classification searchClassification(boolean active) throws RegisterException {
        return this.classificationRegister.search(active);
    }

    public FiveWorker getClassificationWorker(Classification classification, Project project) throws RegisterException {
        return this.classificationRegister.getClassificationWorker(classification, project);
    }

    // MÉTODOS DE EXTRACTION
    public void insertExtraction(Extraction extraction) throws RegisterException {
        this.extractionRegister.insert(extraction);
    }

    public void updateExtraction(Extraction extraction) throws RegisterException {
        this.extractionRegister.update(extraction);
    }

    public void removeExtraction(Extraction extraction) throws RegisterException {
        this.extractionRegister.remove(extraction);
    }

    public Extraction searchExtraction(int id) throws RegisterException {
        return this.extractionRegister.search(id);
    }

    public Extraction searchExtraction(boolean active) throws RegisterException {
        return this.extractionRegister.search(active);
    }

    public FiveWorker getExtractionWorker(Extraction extraction, Project project) throws RegisterException {
        return this.extractionRegister.getExtractionWorker(extraction, project);
    }

    // MÉTODOS DE PROJECT
    public void createProject(Project project) throws RegisterException {
        ProjectRegister.create(project);

    }

    public void openProject(File projectFile) throws RegisterException {
        this.project = ProjectRegister.open(projectFile);
    }

    public void saveProject(Project project) throws RegisterException {
        ProjectRegister.save(project);
    }

    public void closeProject(Project project) throws RegisterException {
        ProjectRegister.close(project);
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }

    // MÉTODOS DE SAMPLE
    public void insertSample(Sample sample) throws RegisterException {
        sampleRegister.insert(sample);
    }

    public void updateSample(Sample sample) throws RegisterException {
        sampleRegister.update(sample);
    }

    public void removeSample(Sample sample) throws RegisterException {
        sampleRegister.remove(sample);
    }

    public Sample searchSample(int id) throws RegisterException {
        return sampleRegister.search(id);
    }

    public Sample searchSample(String waveFile) throws RegisterException {
        return sampleRegister.search(waveFile);
    }

    public List<Sample> searchSample(SampleFilter filter) throws RegisterException {
        return sampleRegister.search(filter);
    }

    public FiveWorker getCalculateSNRWorker(List<Sample> samples) throws RegisterException {
        return this.sampleRegister.getCalculateSNRWorker(samples);
    }

    public FiveWorker getSeparateDataBaseWorker(SampleFilter sampleFilter, int trainPercentage, int testPercentage) throws RegisterException {
        return this.sampleRegister.getSeparateDataBaseWorker(sampleFilter, trainPercentage, testPercentage);
    }    
    
    // MÉTODOS DE SPEAKER
    public void insertSpeaker(Speaker speaker) throws RegisterException {
        speakerRegister.insert(speaker);
    }

    public void removeSpeaker(Speaker speaker) throws RegisterException {
        speakerRegister.remove(speaker);
    }

    public void updateSpeaker(Speaker speaker) throws RegisterException {
        speakerRegister.update(speaker);
    }

    public Speaker searchSpeaker(int id) throws RegisterException {
        return speakerRegister.search(id);
    }

    // MÉTODOS DE UTTERANCE
    public void insertUtterance(Utterance utterance) throws RegisterException {
        utteranceRegister.insert(utterance);
    }

    public void updateUtterance(Utterance utterance) throws RegisterException {
        utteranceRegister.update(utterance);
    }

    public void removeUtterance(Utterance utterance) throws RegisterException {
        utteranceRegister.remove(utterance);
    }

    public Utterance searchUtterance(int id) throws RegisterException {
        return utteranceRegister.search(id);
    }
    
    // MÉTODOS DE WORD
    public void insertPhoneticRepresentation(String word, String newPhoneticRepresentation) throws RegisterException {
        wordRegister.insert(word, newPhoneticRepresentation);
    }

    public void updatePhoneticRepresentation(String word, String oldPhoneticRepresentation, String newPhoneticRepresentation) throws RegisterException {
        wordRegister.update(word, oldPhoneticRepresentation, newPhoneticRepresentation);
    }

    public void removePhoneticRepresentation(String word, String phoneticRepresentation) throws RegisterException {
        wordRegister.remove(word, phoneticRepresentation);
    }
    
    public void generateHistogram() throws RegisterException {
        wordRegister.generateHistogram(project);
    }    

    // MÉTODOS DE ENGINE
    public void insertEngine(Engine engine) throws RegisterException {
        engineRegister.insert(engine);
    }

    public void updateEngine(Engine engine) throws RegisterException {
        engineRegister.update(engine);
    }

    public void removeEngine(Engine engine) throws RegisterException {
        engineRegister.remove(engine);
    }

    public Engine searchEngine(int id) throws RegisterException {
        return engineRegister.search(id);
    }

    public void generateEngine(Engine engine) throws RegisterException {
        this.engineRegister.generate(engine);
    }
}
