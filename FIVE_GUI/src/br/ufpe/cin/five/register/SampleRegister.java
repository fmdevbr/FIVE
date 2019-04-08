/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.register;

import br.ufpe.cin.five.core.classification.ClassificationUtil;
import br.ufpe.cin.five.core.project.Project;
import br.ufpe.cin.five.core.sample.Sample;
import br.ufpe.cin.five.core.sample.SampleFilter;
import br.ufpe.cin.five.core.sample.SampleProcess;
import br.ufpe.cin.five.core.speaker.Speaker;
import br.ufpe.cin.five.core.utterance.Utterance;
import br.ufpe.cin.five.exception.RegisterException;
import br.ufpe.cin.five.facade.Facade;
import br.ufpe.cin.five.gui.dialogs.FiveWorker;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alexandre
 */
public class SampleRegister {

    private Project project;
    private SpeakerRegister speakerRegister;
    private UtteranceRegister utteranceRegister;    

    public SampleRegister(Project project) {
        this.project = project;
        this.speakerRegister = new SpeakerRegister(project);
        this.utteranceRegister = new UtteranceRegister(project);        
    }

    public void insert(Sample sample) throws RegisterException {
        if (search(sample.getAudioFile()) == null) {                       
            Facade.getInstance().getConcreteDAO().create(sample);
            this.project.getSamples().add(sample);
            Facade.getInstance().getConcreteDAO().update(this.project);
        } else {
            throw new RegisterException("Já existe uma amostra com este ID: " + sample.getId()+ " no banco de dados.");
        }        
    }

    public void update(Sample sample) throws RegisterException {
        if (search(sample.getAudioFile()) != null) {            
            Facade.getInstance().getConcreteDAO().update(sample);
        } else {
            throw new RegisterException("Não existe uma amostra com este ID: " + sample.getId() + " no banco de dados.");
        }       
    }

    public void remove(Sample sample) throws RegisterException {
        if (search(sample.getAudioFile()) != null) {
            
            String projectPath = this.project.getDirectory();

            String waveName = sample.getAudioFile();
            File waveFile = new File(projectPath + File.separator + "samples" + File.separator + waveName);
            if (waveFile.exists()) {
                waveFile.delete();
            }

            String featureName = sample.getFeatureFile();
            File featureFile = new File(projectPath + File.separator + "features" + File.separator + featureName);
            if (featureFile.exists()) {
                featureFile.delete();
            }

            this.project.getSamples().remove(sample);                        
            Facade.getInstance().getConcreteDAO().update(this.project);
            Facade.getInstance().getConcreteDAO().delete(sample);            

        } else {
            throw new RegisterException("Não existe uma amostra com este ID: " + sample.getId() + " no banco de dados.");
        }
    }

    public Sample search(int id) throws RegisterException {
        for (Sample sample : this.project.getSamples()) {
            if (id != -1 && sample.getId() == id) {
                return sample;
            }
        }
        return null;
    }
           
    public Sample search(String audioFile) throws RegisterException {
        for (Sample sample : this.project.getSamples()) {
            if (!audioFile.equals("") && sample.getAudioFile().equals(audioFile)) {
                return sample;
            }
        }
        return null;
    }

    public List<Sample> search(SampleFilter filter) throws RegisterException {
        
        try {       
            List<Sample> samples = this.project.getSamples();
            List<Sample> samplesFound = new ArrayList<Sample>();

            for (Sample sample : samples) {
                boolean snrFilter = (filter.getSnr() == -1000 || sample.getSnr() > filter.getSnr()) ? true : false;
                boolean environmentFilter = (filter.getEnvironment() == null || filter.getEnvironment() == sample.getEnvironment()) ? true : false;

                Speaker speaker = this.speakerRegister.search(sample.getSpeaker().getId());

                boolean genderFilter = (filter.getGender() == null || filter.getGender() == speaker.getGender()) ? true : false;
                boolean speakersFilter = (filter.getSpeakers().isEmpty() || filter.getSpeakers().contains(speaker)) ? true : false;
                boolean ageFilter = (filter.getMinAge() == -1 && filter.getMaxAge() == -1)
                        ? true
                        : ((filter.getMaxAge() == -1 && (filter.getMinAge() <= speaker.getAge()))
                        ? true
                        : ((filter.getMinAge() <= speaker.getAge() && (filter.getMaxAge() >= speaker.getAge()))
                        ? true
                        : false));

                Utterance utterance = this.utteranceRegister.search(sample.getUtterance().getId());
                boolean utterancesFilter = (filter.getUtterances().isEmpty() || filter.getUtterances().contains(utterance)) ? true : false;

                if (genderFilter && utterancesFilter && ageFilter && speakersFilter && environmentFilter && snrFilter) {
                    samplesFound.add(sample);
                }
            }
            return samplesFound;
        }catch (Exception ex) {
            throw new RegisterException(ex.getMessage());
        }
    }

    public FiveWorker getCalculateSNRWorker(List<Sample> sample) {
        return new CalculateSNRWorker(sample);
    }

    private class CalculateSNRWorker extends FiveWorker<Void, String> {

        private final List<Sample> samples;

        CalculateSNRWorker(List<Sample> samples) {
            this.samples = samples;
        }

        @Override
        protected Void doInBackground() throws RegisterException {
            try {
                int progressCounter = 0;
                int amountOperation = 1;
                amountOperation = samples.size();
                publish("Tipo de projeto: " + project.getType());
                publish("Calculando SNR : ");
                for (Sample sample : samples) {
                    double snr = SampleProcess.calculateSNR(project, sample.getAudioFile());
                    publish("SNR do Arquivo: " + sample.getAudioFile() + " SNR: " + snr);
                    sample.setSnr((int)snr);
                    update(sample);
                    setProgress((++progressCounter * 100) / amountOperation);
                }
                firePropertyChange("done", null, null);
                return null;
            } catch (Exception ex) {
                throw new RegisterException(ex.getMessage());
            }
        }

        @Override
        protected void process(List<String> chunks) {
            for (String chunk : chunks) {
                System.out.println(chunk);
            }
        }
    }
    
    public FiveWorker getSeparateDataBaseWorker(SampleFilter sampleFilter, int trainPercentage, int testPercentage) {
        return new SeparateDataBaseWorker(sampleFilter, trainPercentage, testPercentage);
    }

    private class SeparateDataBaseWorker extends FiveWorker<Void, String> {

        private final SampleFilter filterFilter;
        private final int trainPercentage;
        private final int testPercentage;

        SeparateDataBaseWorker(SampleFilter sampleFilter, int trainPercentage, int testPercentage) {
            this.filterFilter = sampleFilter;
            this.trainPercentage = trainPercentage;
            this.testPercentage = testPercentage;
        }

        @Override
        protected Void doInBackground() throws RegisterException {
            try {
                int progressCounter = 0;
                int amountOperation = 1;                
                
                publish("Tipo de projeto: " + project.getType());
                publish("Separando a base de dados : ");

                List<Utterance> utterances = null;
                if (!filterFilter.getUtterances().isEmpty()) {
                    utterances = filterFilter.getUtterances();
                } else {
                    utterances = project.getUtterances();
                }

                amountOperation = utterances.size();
                
                // Separando a base de dados

                List<Sample> samples = search(filterFilter);
                
                ArrayList<Sample> trainingSamples = new ArrayList<Sample>();
                ArrayList<Sample> testingSamples = new ArrayList<Sample>();

                publish("Separando base de dados...\n");

                setProgress((++progressCounter * 100) / amountOperation);

                for (Utterance utterance : utterances) {

                    ArrayList<Sample> samplesPerUtterance = new ArrayList<Sample>();
                    for (Sample sample : samples) {
                        if (sample.getUtterance().getDescription().equals(utterance.getDescription()) && !sample.getFeatureFile().equals("")) {
                            samplesPerUtterance.add(sample);
                        }
                    }
                    int nTrain = (int) Math.ceil(trainPercentage * samplesPerUtterance.size());
                    int nTest = (int) Math.floor(testPercentage * samplesPerUtterance.size());

                    publish("Locucao: " + utterance.getDescription() + " -> Treino: " + nTrain + " Teste: " + nTest);

                    ArrayList<Integer> indexBase = ClassificationUtil.randomNumbers(samplesPerUtterance.size());

                    for (int i = 0; i < samplesPerUtterance.size(); i++) {
                        if (i < nTrain) {
                            trainingSamples.add(samplesPerUtterance.get(indexBase.get(i)));
                        } else if (i >= nTrain && i < nTrain + nTest) {
                            testingSamples.add(samplesPerUtterance.get(indexBase.get(i)));
                        }
                    }                    
                    setProgress((++progressCounter * 100) / amountOperation);                    
                }                                                                       
                firePropertyChange("done", null, null);
                return null;
            } catch (Exception ex) {
                throw new RegisterException(ex.getMessage());
            }
        }

        @Override
        protected void process(List<String> chunks) {
            for (String chunk : chunks) {
                System.out.println(chunk);
            }
        }
    }
    
}
