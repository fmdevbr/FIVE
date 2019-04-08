/**
 * Copyright 2011 Federal University of Pernambuco. 
 * All Rights Reserved. Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.api.recognizer.decoder;

import br.ufpe.cin.five.api.recognizer.RecognizerException;
import br.ufpe.cin.five.api.recognizer.Result;
import br.ufpe.cin.five.core.classification.Classification;
import br.ufpe.cin.five.core.classification.ClassificationTechnique;
import br.ufpe.cin.five.core.classification.gmm.GmmParameters;
import br.ufpe.cin.five.core.classification.gmm.GmmProcess;
import br.ufpe.cin.five.core.classification.hmm.HmmMonophoneProcess;
import br.ufpe.cin.five.core.classification.hmm.HmmParameters;
import br.ufpe.cin.five.core.classification.hmm.HmmUnitSize;
import br.ufpe.cin.five.core.classification.hmm.HmmWordProcess;
import br.ufpe.cin.five.core.classification.svm.SvmParameters;
import br.ufpe.cin.five.core.classification.svm.SvmProcess;
import br.ufpe.cin.five.core.dictionary.Dictionary;
import br.ufpe.cin.five.core.extraction.Extraction;
import br.ufpe.cin.five.core.extraction.ExtractionTechnique;
import br.ufpe.cin.five.core.extraction.WindowingTechnique;
import br.ufpe.cin.five.core.extraction.htk.HtkParameters;
import br.ufpe.cin.five.core.extraction.htk.HtkProcess;
import br.ufpe.cin.five.core.extraction.mfcc.MfccParameters;
import br.ufpe.cin.five.core.extraction.mfcc.MfccProcess;
import br.ufpe.cin.five.core.grammar.Grammar;
import br.ufpe.cin.five.core.grammar.RuleGrammar;
import br.ufpe.cin.five.core.project.ProjectType;
import br.ufpe.cin.five.core.sample.Audio;
import br.ufpe.cin.five.core.speaker.Speaker;
import br.ufpe.cin.five.core.utterance.Word;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author Alexandre
 */
public class Decoder {

    private String enginePath;
    private ProjectType projectType;
    private Extraction extraction;
    private Classification classification;
    private Dictionary dictionary;
    private Grammar grammar;
    private List<Speaker> speakers;

    public Decoder(String enginePath) throws RecognizerException {

        this.enginePath = enginePath;

        this.loadConfigFile();

        if (projectType == ProjectType.ASR) {
            this.loadDictionaryFile();
            this.loadGrammarFile();
        } else if (projectType == ProjectType.ASV) {
            this.loadSpeakerFile();
        }
    }

    private void loadConfigFile() throws RecognizerException {

        try {
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(enginePath + File.separator + "config.xml");
            Element root = document.getRootElement();

            this.projectType = ProjectType.valueOf(root.getChild("projectType").getText());

            // Read Feature Extraction flag
            Element elementExtraction = root.getChild("extraction");
            Element elementExtractionTechnique = elementExtraction.getChild("technique");
            String technique = elementExtractionTechnique.getText();
            ExtractionTechnique extractionTechnique = ExtractionTechnique.valueOf(technique);

            Element elementVad;
            Element elementNoiseSup;
            Element elementFrameDuration;
            Element elementAlphaPreEmphasis;
            Element elementOverLap;
            Element elementWindowing;
            Element elementNumMelFilters;
            Element elementCoefs;
            Element elementPowerSpectrum;
            Element elementLowerFreq;
            Element elementUpperFreq;
            Element elementFrameLength;

            switch (extractionTechnique) {
                case MFCC:
                    MfccParameters mfccParameters = new MfccParameters();
                    mfccParameters.setTechnique(extractionTechnique);
                    elementVad = elementExtraction.getChild("vad");
                    if (elementVad.getText().equals("true")) {
                        mfccParameters.setVad(true);
                    } else {
                        mfccParameters.setVad(false);
                    }
                    elementNoiseSup = elementExtraction.getChild("normalize");
                    if (elementNoiseSup.getText().equals("true")) {
                        mfccParameters.setNormalize(true);
                    } else {
                        mfccParameters.setNormalize(false);
                    }
                    elementFrameDuration = elementExtraction.getChild("frameDuration");
                    mfccParameters.setFrameDuration(new Integer(elementFrameDuration.getText()));
                    elementAlphaPreEmphasis = elementExtraction.getChild("alphaPreEmphasis");
                    mfccParameters.setPreEmphasis(new Double(elementAlphaPreEmphasis.getText()));
                    elementOverLap = elementExtraction.getChild("overLap");
                    mfccParameters.setOverlap(new Double(elementOverLap.getText()));
                    elementWindowing = elementExtraction.getChild("windowing");
                    if (elementWindowing.getText().equals("HAMMING")) {
                        mfccParameters.setWindowing(WindowingTechnique.HAMMING);
                    } else if (elementWindowing.getText().equals("HANNING")) {
                        mfccParameters.setWindowing(WindowingTechnique.HANNING);
                    } else {
                        mfccParameters.setWindowing(WindowingTechnique.RECTANGULAR);
                    }
                    elementNumMelFilters = elementExtraction.getChild("numMelFilters");
                    mfccParameters.setNumMelFilters(new Integer(elementNumMelFilters.getText()));
                    elementCoefs = elementExtraction.getChild("numCoefs");
                    mfccParameters.setNumCoefs(new Integer(elementCoefs.getText()));
                    elementPowerSpectrum = elementExtraction.getChild("powerSpectrum");
                    if (elementPowerSpectrum.getText().equals("true")) {
                        mfccParameters.setPowerSpectrum(true);
                    } else {
                        mfccParameters.setPowerSpectrum(false);
                    }
                    elementLowerFreq = elementExtraction.getChild("lowerFreq");
                    mfccParameters.setLowerFreq(new Double(elementLowerFreq.getText()));
                    elementUpperFreq = elementExtraction.getChild("upperFreq");
                    mfccParameters.setUpperFreq(new Double(elementUpperFreq.getText()));
                    elementFrameLength = elementExtraction.getChild("frameLength");
                    mfccParameters.setFrameLength(new Integer(elementFrameLength.getText()));
                    this.extraction = mfccParameters;
                    break;

                case HTK:
                    HtkParameters htkParameters = new HtkParameters();
                    htkParameters.setTechnique(extractionTechnique);
                    elementVad = elementExtraction.getChild("vad");
                    if (elementVad.getText().equals("true")) {
                        htkParameters.setVad(true);
                    } else {
                        htkParameters.setVad(false);
                    }
                    elementNoiseSup = elementExtraction.getChild("normalize");
                    if (elementNoiseSup.getText().equals("true")) {
                        htkParameters.setNormalize(true);
                    } else {
                        htkParameters.setNormalize(false);
                    }
                    elementFrameDuration = elementExtraction.getChild("frameDuration");
                    htkParameters.setFrameDuration(new Integer(elementFrameDuration.getText()));
                    elementAlphaPreEmphasis = elementExtraction.getChild("alphaPreEmphasis");
                    htkParameters.setPreEmphasis(new Double(elementAlphaPreEmphasis.getText()));
                    elementOverLap = elementExtraction.getChild("overLap");
                    htkParameters.setOverlap(new Double(elementOverLap.getText()));
                    elementWindowing = elementExtraction.getChild("windowing");
                    if (elementWindowing.getText().equals("HAMMING")) {
                        htkParameters.setWindowing(WindowingTechnique.HAMMING);
                    } else if (elementWindowing.getText().equals("HANNING")) {
                        htkParameters.setWindowing(WindowingTechnique.HANNING);
                    } else {
                        htkParameters.setWindowing(WindowingTechnique.RECTANGULAR);
                    }
                    elementNumMelFilters = elementExtraction.getChild("numMelFilters");
                    htkParameters.setNumMelFilters(new Integer(elementNumMelFilters.getText()));
                    elementCoefs = elementExtraction.getChild("numCoefs");
                    htkParameters.setNumCoefs(new Integer(elementCoefs.getText()));
                    this.extraction = htkParameters;
                    break;
            }

            Element elementClassification = root.getChild("classification");
            Element elementClassificationTechnique = elementClassification.getChild("technique");

            ClassificationTechnique classificationTechnique = ClassificationTechnique.valueOf(elementClassificationTechnique.getText());

            switch (classificationTechnique) {
                case SVM:
                    SvmParameters svmParameters = new SvmParameters();
                    svmParameters.setTechnique(classificationTechnique);
                    Element elementThresholdValue = elementClassification.getChild("thresholdValue");
                    svmParameters.setThresholdValue(Double.parseDouble(elementThresholdValue.getText()));
                    Element elementKernelType = elementClassification.getChild("kernelType");
                    svmParameters.setKernelType(Integer.parseInt(elementKernelType.getText()));
                    Element elementCost = elementClassification.getChild("cost");
                    svmParameters.setCost(Integer.parseInt(elementCost.getText()));
                    Element elementDegree = elementClassification.getChild("degree");
                    svmParameters.setDegree(Integer.parseInt(elementDegree.getText()));
                    Element elementGamma = elementClassification.getChild("gamma");
                    svmParameters.setGamma(Integer.parseInt(elementGamma.getText()));
                    this.classification = svmParameters;
                    break;
                case GMM:
                    GmmParameters gmmParameters = new GmmParameters();
                    gmmParameters.setTechnique(classificationTechnique);
                    Element elementThresholdValue2 = elementClassification.getChild("thresholdValue");
                    gmmParameters.setThresholdValue(Double.parseDouble(elementThresholdValue2.getText()));
                    this.classification = gmmParameters;
                    break;
                case HMM:
                    HmmParameters hmmParameters = new HmmParameters();
                    hmmParameters.setTechnique(classificationTechnique);
                    Element elementThresholdValue3 = elementClassification.getChild("thresholdValue");
                    hmmParameters.setThresholdValue(Double.parseDouble(elementThresholdValue3.getText()));
                    Element elementUnitSize = elementClassification.getChild("unitSize");
                    hmmParameters.setUnitSize(HmmUnitSize.valueOf(elementUnitSize.getText()));
                    this.classification = hmmParameters;
                    break;
            }

        } catch (Exception ex) {
            throw new RecognizerException(ex.getMessage());
        }
    }

    private void loadDictionaryFile() throws RecognizerException {

        dictionary = new Dictionary();

        try {
            BufferedReader br = new BufferedReader(new FileReader(enginePath + File.separator + "dictionary.txt"));
            ArrayList<Word> words = new ArrayList<Word>();
            if (classification instanceof SvmParameters) {
                while (br.ready()) {
                    String line = br.readLine();
                    String[] content = line.split(" ");
                    Word word = new Word();
                    word.setId(Integer.parseInt(content[0]));
                    word.setDescription(content[1]);
                    words.add(word);
                }
            } else if (classification instanceof HmmParameters) {
                while (br.ready()) {
                    String line = br.readLine();
                    String[] content = line.split("\t");
                    Word word = new Word();
                    word.setDescription(content[0]);
                    words.add(word);
                }
            }
            dictionary.setWords(words);
        } catch (Exception ex) {
            throw new RecognizerException(ex.getMessage());
        }
    }

    private void loadGrammarFile() throws RecognizerException {
        try {
            grammar = new Grammar();
            if (classification instanceof SvmParameters) {
                SAXBuilder builder = new SAXBuilder();
                Document document = builder.build(enginePath + File.separator + "grammar.txt");
                Element root = document.getRootElement();

                List<Element> ruleList = root.getChildren("rule");
                ArrayList<RuleGrammar> ruleGrammars = new ArrayList<RuleGrammar>();

                for (Element rule : ruleList) {
                    ArrayList<String> words = new ArrayList<String>();
                    Element list = rule.getChild("one-of");
                    List<Element> itens = list.getChildren("item");
                    for (Element item : itens) {
                        words.add(item.getText());
                    }
                    RuleGrammar ruleGrammar = new RuleGrammar();
                    ruleGrammar.setId(new Integer(rule.getAttributeValue("id")));
                    ruleGrammar.setWords(words);
                    ruleGrammar.setActive(true);
                    ruleGrammars.add(ruleGrammar);
                }
                grammar.setRuleGrammars(ruleGrammars);
            } else if (classification instanceof HmmParameters) {
                BufferedReader br = new BufferedReader(new FileReader(enginePath + File.separator + "grammar.txt"));
                ArrayList<RuleGrammar> ruleGrammars = new ArrayList<RuleGrammar>();
                ArrayList<String> words = new ArrayList<String>();
                while (br.ready()) {
                    String line = br.readLine();
                    String[] content = line.split("[|]");
                    for (int i = 0; i < content.length; i++) {
                        String word;
                        if (i == 0) {
                            String[] primeiroValor = content[i].split(" ");
                            word = primeiroValor[2];
                            words.add(word);
                        } else {
                            word = content[i];
                            words.add(word);
                        }
                    }
                    break;
                }
                RuleGrammar ruleGrammar = new RuleGrammar();
                ruleGrammar.setId(new Integer(0));
                ruleGrammar.setWords(words);
                ruleGrammar.setActive(true);
                ruleGrammars.add(ruleGrammar);
                grammar.setRuleGrammars(ruleGrammars);
            }
            this.setRuleState(0);
        } catch (Exception ex) {
            throw new RecognizerException(ex.getMessage());
        }
    }

    private void loadSpeakerFile() throws RecognizerException {

        speakers = new ArrayList<Speaker>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(enginePath + File.separator + "speakers"));

            while (br.ready()) {
                String line = br.readLine();
                String[] content = line.split(" ");
                Speaker speaker = new Speaker();
                speaker.setId(Integer.parseInt(content[0]));
                speaker.setName(content[1]);
                speakers.add(speaker);
            }
        } catch (Exception ex) {
            throw new RecognizerException(ex.getMessage());
        }
    }

    public Grammar getGrammar() {
        return grammar;
    }

    public void setRuleState(int ruleId) throws RecognizerException {
        grammar.setActiveRule(ruleId);
    }

    public List<Speaker> getSpeakerSet() {
        return speakers;
    }

    public Result decode(Audio audio) throws RecognizerException {

        try {

            Result res = new Result();

            double[] features = null;

            if (extraction.getTechnique().equals(ExtractionTechnique.MFCC)) {

                MfccParameters mfccParameters = (MfccParameters) extraction;
                MfccProcess mfccProcess = new MfccProcess();
                features = mfccProcess.extract(mfccParameters, audio);

            } else if (extraction.getTechnique().equals(ExtractionTechnique.HTK)) {

                HtkParameters htkParameters = (HtkParameters) extraction;
                htkParameters.setDecodeProcess(true);
                HtkProcess htkProcess = new HtkProcess(enginePath);
                htkProcess.extract(htkParameters, audio);
            }

            if (classification.getTechnique().equals(ClassificationTechnique.SVM)) {
                
                String modelPath = enginePath + File.separator + "models";                

                SvmProcess svm = new SvmProcess();
                int answer = (Integer) svm.test(features, modelPath);

                List<Word> words = dictionary.getWords();
                for (Word word : words) {
                    if (word.getId() == answer) {
                        res.setAnswer(word.getDescription());
                        res.setConfidence(0.0);
                        break;
                    }
                }

            } else if (classification.getTechnique().equals(ClassificationTechnique.HMM)) {

                HmmParameters params = (HmmParameters) classification;

                String answer = "";

                if (params.getUnitSize().equals(HmmUnitSize.WORD)) {
                    HmmWordProcess process = new HmmWordProcess(enginePath);
                    answer = (String) process.test(new double[1], "");
                } else if (params.getUnitSize().equals(HmmUnitSize.PHONEMES)) {
                    HmmMonophoneProcess process = new HmmMonophoneProcess(enginePath);
                    answer = (String) process.test(new double[1], "");
                }

                List<Word> words = dictionary.getWords();
                for (Word word : words) {
                    if (word.getDescription().equals(answer)) {
                        res.setAnswer(word.getDescription());
                        res.setConfidence(0.0);
                        break;
                    }
                }

            } else if (classification.getTechnique().equals(ClassificationTechnique.GMM)) {

                for (Speaker speaker : speakers) {

                    String modelPath = enginePath + File.separator + "models" + File.separator + speaker.getId() + File.separator + "models_" + speaker.getId();

                    GmmProcess gmmProcess = new GmmProcess();
                    int likehood = (Integer) gmmProcess.test(features, modelPath);

                    if (likehood >= classification.getThresholdValue()) {
                        res.setAnswer(speaker.getName());
                        res.setConfidence(likehood);
                        break;
                    } else {
                        res.setAnswer("locutor não reconhecido.");
                        res.setConfidence(likehood);
                    }
                }
            }
            return res;
        } catch (Exception ex) {
            throw new RecognizerException(ex.getMessage());
        }
    }
}
