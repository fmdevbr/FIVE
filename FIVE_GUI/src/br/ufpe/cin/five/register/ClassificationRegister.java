/**
 * Copyright 2011 Federal University of Pernambuco. All Rights Reserved. Use is
 * subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.register;

import br.ufpe.cin.five.core.classification.Classification;
import br.ufpe.cin.five.core.classification.ClassificationException;
import br.ufpe.cin.five.core.classification.ClassificationUtil;
import br.ufpe.cin.five.core.classification.gmm.GmmParameters;
import br.ufpe.cin.five.core.classification.gmm.GmmProcess;
import br.ufpe.cin.five.core.classification.hmm.*;
import br.ufpe.cin.five.core.classification.svm.SvmParameters;
import br.ufpe.cin.five.core.classification.svm.SvmProcess;
import br.ufpe.cin.five.core.classification.svm.SvmResult;
import br.ufpe.cin.five.core.extraction.Extraction;
import br.ufpe.cin.five.core.project.Project;
import br.ufpe.cin.five.core.project.ProjectType;
import br.ufpe.cin.five.core.project.ProjectUtil;
import br.ufpe.cin.five.core.sample.Sample;
import br.ufpe.cin.five.core.sample.SampleFilter;
import br.ufpe.cin.five.core.speaker.Speaker;
import br.ufpe.cin.five.core.utterance.Utterance;
import br.ufpe.cin.five.exception.RegisterException;
import br.ufpe.cin.five.facade.Facade;
import br.ufpe.cin.five.gui.dialogs.FiveWorker;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Alexandre
 */
public class ClassificationRegister {

    private Project project;
    private SampleRegister sampleRegister;
    private ExtractionRegister extractionRegister;
    

    public ClassificationRegister(Project project) {
        this.project = project;
        this.sampleRegister = new SampleRegister(project);
        this.extractionRegister = new ExtractionRegister(project);
        
    }

    public void insert(Classification classification) throws RegisterException {
        if (search(classification.getId()) == null) {
            Facade.getInstance().getConcreteDAO().create(classification.getSampleFilter());
            Facade.getInstance().getConcreteDAO().create(classification);
            project.getClassifications().add(classification);
            Facade.getInstance().getConcreteDAO().update(this.project);
        } else {
            throw new RegisterException("Já existe uma classificação com este ID: " + classification.getId() + " no banco de dados.");
        }
    }

    public void update(Classification classification) throws RegisterException {
        if (search(classification.getId()) != null) {
            int classificationIndex = this.project.getClassifications().indexOf(classification);
            Facade.getInstance().getConcreteDAO().update(classification);
            this.project.getClassifications().set(classificationIndex, classification);
            Facade.getInstance().getConcreteDAO().update(this.project);
        } else {
            throw new RegisterException("Não existe uma classificação com este ID: " + classification.getId() + " no banco de dados.");
        }
    }

    public void remove(Classification classification) throws RegisterException {
        if (search(classification.getId()) != null) {
            this.project.getClassifications().remove(classification);
            Facade.getInstance().getConcreteDAO().update(this.project);
            SampleFilter sampleFilter = classification.getSampleFilter();
            classification.setSampleFilter(null);
            Facade.getInstance().getConcreteDAO().update(classification);
            sampleFilter.setSpeakers(null);
            sampleFilter.setUtterances(null);
            Facade.getInstance().getConcreteDAO().update(sampleFilter);
            Facade.getInstance().getConcreteDAO().delete(sampleFilter);
            Facade.getInstance().getConcreteDAO().delete(classification);
        } else {
            throw new RegisterException("Não existe uma classificação com este ID: " + classification.getId() + " no banco de dados.");
        }
    }

    public Classification search(int id) throws RegisterException {
        for (Classification classification : this.project.getClassifications()) {
            if (id != -1 && classification.getId() == id) {
                return classification;
            }
        }
        return null;
    }

    public Classification search(boolean active) throws RegisterException {
        for (Classification classification : this.project.getClassifications()) {
            if (classification.isActive()) {
                return classification;
            }
        }
        return null;
    }
    
    public void active(Classification classification) throws RegisterException {
        for (Classification match : project.getClassifications()) {
            if (match.equals(classification)) {
                match.setActive(true);                
            } else {
                match.setActive(false);
            }
            update(match);
        }
    }    

    public void process(Classification classification) throws RegisterException {
        try {
            new ClassificationWorker(classification, project).execute();
        } catch (RegisterException ex) {
            throw new RegisterException(ex.getMessage());
        }
    }

    public FiveWorker getClassificationWorker(Classification classification, Project project) throws RegisterException {
        return new ClassificationWorker(classification, project);
    }

    private class ClassificationWorker extends FiveWorker<Void, String> {

        private final Classification classification;
        private final Project project;

        ClassificationWorker(Classification classification, Project project) throws RegisterException {
            this.classification = classification;
            this.project = project;
        }

        @Override
        protected Void doInBackground() throws RegisterException {
            if (project.getType().equals(ProjectType.ASR)) {
                processSpeechClassification();
            } else if (project.getType().equals(ProjectType.ASV)) {
                processSpeakerClassification();
            }
            firePropertyChange("done", null, null);
            return null;
        }

        @Override
        protected void process(List<String> chunks) {
            for (String chunk : chunks) {
                System.out.println(chunk);
            }
        }

        private void processSpeechClassification() throws RegisterException {

            try {
                int progressCounter = 0;
                int amountOperation = 2;

                // Verificando se há alguma extração ativa
                Extraction activeExtraction = extractionRegister.search(true);
                if (activeExtraction == null) {
                    throw new ClassificationException("Não a nenhuma extração ativa, extraia os samples para pode classificar!");
                }

                // Filtrando as samples informadas na tela
                List<Sample> samples = sampleRegister.search(classification.getSampleFilter());
                List<Sample> extractiondSamples = sampleRegister.search(activeExtraction.getSampleFilter());
                for (Sample sample : samples) {
                    if (!extractiondSamples.contains(sample)) {
                        throw new ClassificationException("A classificação possui samples que não foram extraidos!");
                    }
                }

                publish("Extração Ativa: " + activeExtraction.getTechnique() + "\n");

                //Filtrando as locuções
                List<Utterance> utterances = null;
                if (!classification.getSampleFilter().getUtterances().isEmpty()) {
                    utterances = classification.getSampleFilter().getUtterances();
                } else {
                    utterances = project.getUtterances();
                }

                // Separando a base de dados
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
                    int nTrain = (int) Math.ceil(classification.getTrainPercentage() * samplesPerUtterance.size());
                    int nTest = (int) Math.floor(classification.getTestPercentage() * samplesPerUtterance.size());

                    publish("Locucao: " + utterance.getDescription() + " -> Treino: " + nTrain + " Teste: " + nTest);

                    ArrayList<Integer> indexBase = ClassificationUtil.randomNumbers(samplesPerUtterance.size());

                    for (int i = 0; i < samplesPerUtterance.size(); i++) {
                        if (i < nTrain) {
                            trainingSamples.add(samplesPerUtterance.get(indexBase.get(i)));
                        } else if (i >= nTrain && i < nTrain + nTest) {
                            testingSamples.add(samplesPerUtterance.get(indexBase.get(i)));
                        }
                    }
                }

                publish("\nIniciando classificacao de padroes...\n");

                publish("\nApagando classificacoes anteriores...\n");
                ProjectUtil.deleteDirectory(new File(project.getDirectory() + File.separator + "temp"), false);

                setProgress((++progressCounter * 100) / amountOperation);

                switch (classification.getTechnique()) {
                    case SVM:
                        SvmParameters svmParameters = (SvmParameters) classification;
                        SvmProcess svm = new SvmProcess(project.getDirectory());
                        svm.train(svmParameters, trainingSamples);
                        SvmResult result = svm.test(svmParameters, testingSamples);

                        publish("Error: " + result.getError());

                        int[][] confusionMatrix = result.getConfusionMatrix();

                        ArrayList<Integer> listClassId = new ArrayList<Integer>();
                        for (Sample sample : testingSamples) {
                            if (!listClassId.contains(sample.getUtterance().getId())) {
                                listClassId.add(sample.getUtterance().getId());
                            }
                        }

                        Collections.sort(listClassId);

                        System.out.print("Class Id ");
                        for (int z = 0; z < listClassId.size(); z++) {
                            System.out.print(leftSpaces(listClassId.get(z) + "") + "  ");
                        }
                        System.out.println("\n");

                        for (int i = 0; i < confusionMatrix.length; i++) {
                            System.out.print("   " + leftSpaces(listClassId.get(i) + "") + "    ");
                            for (int j = 0; j < confusionMatrix.length; j++) {
                                System.out.print(leftSpaces(confusionMatrix[i][j] + "") + "  ");
                            }
                            System.out.print("\n");
                        }

                        System.out.print("\n");

                        break;

                    case HMM:

                        HmmParameters hmmParameters = (HmmParameters) classification;

                        if (hmmParameters.getUnitSize().equals(HmmUnitSize.WORD)) {
                            HmmWordProcess hmm = new HmmWordProcess(project.getDirectory());
                            hmm.train(hmmParameters, trainingSamples);
                            hmm.test(hmmParameters, testingSamples);
                        } else if (hmmParameters.getUnitSize().equals(HmmUnitSize.PHONEMES)) {
                            HmmMonophoneProcess hmm = new HmmMonophoneProcess(project.getDirectory());
                            hmm.train(hmmParameters, trainingSamples);
                            hmm.test(hmmParameters, testingSamples);
                        } else if (hmmParameters.getUnitSize().equals(HmmUnitSize.TRIPHONEMES)) {
                            HmmTriphoneProcess hmm = new HmmTriphoneProcess(project.getDirectory());
                            hmm.train(hmmParameters, trainingSamples);
                            hmm.test(hmmParameters, testingSamples);
                        }
                        break;
                }
                active(classification);
                publish("\nConcluido");

            } catch (ClassificationException ex) {
                throw new RegisterException(ex.getMessage());
            }
        }

        private void processSpeakerClassification() throws RegisterException {

            try {
                // Verificando se há alguma extração ativa
                Extraction activeExtraction = extractionRegister.search(true);
                if (activeExtraction == null) {
                    throw new ClassificationException("Não a nenhuma extração ativa, extraia os samples para pode classificar!");
                }

                // Filtrando as samples informadas na tela
                List<Sample> samples = sampleRegister.search(classification.getSampleFilter());
                List<Sample> extractiondSamples = sampleRegister.search(activeExtraction.getSampleFilter());
                for (Sample sample : samples) {
                    if (!extractiondSamples.contains(sample)) {
                        throw new ClassificationException("A classificação possui samples que não foram extraidos!");
                    }
                }

                System.out.println("Extracao Ativa:" + activeExtraction.getTechnique() + " " + activeExtraction.getDescription());

                //Filtrando os locutores
                List<Speaker> speakers = null;
                if (classification.getSampleFilter().getSpeakers().isEmpty()) {
                    speakers = project.getSpeakers();
                } else {
                    speakers = classification.getSampleFilter().getSpeakers();
                }

                // Separando a base de dados
                for (Speaker speaker : speakers) {

                    System.out.println("## Locutor: " + speaker.getName() + " ##");

                    ArrayList<Sample> genuineSamples = new ArrayList<Sample>();
                    ArrayList<Sample> impostorSamples = new ArrayList<Sample>();

                    Utterance password = null;

                    for (Sample sample : samples) {

                        if (speaker.getName().equals(sample.getSpeaker().getName()) && sample.isGenuine()) {
                            genuineSamples.add(sample);
                            password = sample.getUtterance();
                        }
                    }

                    for (Sample sample : samples) {
                        if (sample.getUtterance().equals(password) && !speaker.getName().equals(sample.getSpeaker().getName())) {
                            impostorSamples.add(sample);
                        }
                    }

                    int nTrain = (int) Math.ceil(classification.getTrainPercentage() * genuineSamples.size());
                    int nTest = (int) Math.floor(classification.getTestPercentage() * genuineSamples.size());

                    System.out.println("Divisao da base de dados -> *Treino: " + nTrain + " *Teste: " + nTest);

                    ArrayList<Integer> indexBase = ClassificationUtil.randomNumbers(genuineSamples.size());

                    ArrayList<Sample> trainingSamples = new ArrayList<Sample>();
                    ArrayList<Sample> testingSamples = new ArrayList<Sample>();

                    for (int i = 0; i < genuineSamples.size(); i++) {
                        if (i < nTrain) {
                            trainingSamples.add(genuineSamples.get(indexBase.get(i)));
                        } else if (i >= nTrain && i < nTrain + nTest) {
                            testingSamples.add(genuineSamples.get(indexBase.get(i)));
                        }
                    }

                    testingSamples.addAll(impostorSamples);

                    switch (classification.getTechnique()) {
                        case GMM:
                            GmmParameters gmmParameters = (GmmParameters) classification;
                            GmmProcess gmm = new GmmProcess(project.getDirectory());
                            gmm.train(gmmParameters, trainingSamples);
                            gmm.test(gmmParameters, testingSamples);
                            break;
                    }
                }
                classification.setActive(true);
                update(classification);
            } catch (ClassificationException ex) {
                throw new RegisterException(ex.getMessage());
            }
        }
    }

    private String leftSpaces(String input) {
        String output;
        if (input.length() == 0) {
            output = "  " + input;
        } else if (input.length() == 1) {
            output = " " + input;
        } else {
            output = input;
        }
        return output;
    }
}
