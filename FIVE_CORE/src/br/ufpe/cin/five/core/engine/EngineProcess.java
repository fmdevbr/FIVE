/**
 * Copyright 2011 Federal University of Pernambuco. 
 * All Rights Reserved. Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.engine;

import br.ufpe.cin.five.core.classification.Classification;
import br.ufpe.cin.five.core.classification.ClassificationTechnique;
import br.ufpe.cin.five.core.classification.hmm.*;
import br.ufpe.cin.five.core.classification.svm.SvmParameters;
import br.ufpe.cin.five.core.dictionary.Dictionary;
import br.ufpe.cin.five.core.extraction.Extraction;
import br.ufpe.cin.five.core.extraction.ExtractionTechnique;
import br.ufpe.cin.five.core.extraction.htk.HtkParameters;
import br.ufpe.cin.five.core.extraction.mfcc.MfccParameters;
import br.ufpe.cin.five.core.grammar.Grammar;
import br.ufpe.cin.five.core.grammar.RuleGrammar;
import br.ufpe.cin.five.core.project.Project;
import br.ufpe.cin.five.core.project.ProjectType;
import br.ufpe.cin.five.core.project.ProjectUtil;
import br.ufpe.cin.five.core.speaker.Speaker;
import br.ufpe.cin.five.core.utterance.Word;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * This is an abstract class that specifies and implements the main methods
 * necessary for the project process.
 */
public abstract class EngineProcess {

    /**
     * Generate a speech engine based on Project attributes.
     *
     * @param project
     * @param engine
     */
    public static void generate(Project project, Engine engine) throws EngineException {
        try {
            BufferedWriter out;

            String projectPath = project.getDirectory();

            //Cria pasta engine e subpastas
            String enginePath = projectPath + File.separator + "engines";
            ProjectUtil.createFolder(enginePath);

            enginePath = enginePath + File.separator + engine.getDescription();
            ProjectUtil.createFolder(enginePath);

            ProjectUtil.copyDirectory("lib", enginePath + File.separator + "lib");
            ProjectUtil.copyDirectory("resources", enginePath + File.separator + "resources");
            ProjectUtil.createFolder(enginePath + File.separator + "temp");

            //Cria configFile
            Element configElement = new Element("config");
            Element projectTypeElement = new Element("projectType");
            projectTypeElement.addContent(engine.getProjectType().toString());
            configElement.addContent(projectTypeElement);

            Element extractionElement = new Element("extraction");
            Extraction extraction = engine.getExtraction();

            if (extraction != null) {

                switch (extraction.getTechnique()) {
                    case HTK:
                        HtkParameters htkParams = (HtkParameters) extraction;
                        extractionElement.addContent(new Element("technique").addContent(ExtractionTechnique.HTK.toString()));
                        extractionElement.addContent(new Element("vad").addContent(htkParams.isVad() + ""));
                        extractionElement.addContent(new Element("normalize").addContent(htkParams.isNormalized() + ""));
                        extractionElement.addContent(new Element("frameDuration").addContent(htkParams.getFrameDuration() + ""));
                        extractionElement.addContent(new Element("alphaPreEmphasis").addContent(htkParams.getPreEmphasis() + ""));
                        extractionElement.addContent(new Element("overLap").addContent(htkParams.getOverlap() + ""));
                        extractionElement.addContent(new Element("windowing").addContent(htkParams.getWindowing() + ""));
                        extractionElement.addContent(new Element("numMelFilters").addContent(htkParams.getNumMelFilters() + ""));
                        extractionElement.addContent(new Element("numCoefs").addContent(htkParams.getNumCoefs() + ""));
                        break;
                    case MFCC:
                        MfccParameters mfccParams = (MfccParameters) extraction;
                        extractionElement.addContent(new Element("technique").addContent(ExtractionTechnique.MFCC.toString()));
                        extractionElement.addContent(new Element("vad").addContent(mfccParams.isVad() + ""));
                        extractionElement.addContent(new Element("normalize").addContent(mfccParams.isNormalized() + ""));
                        extractionElement.addContent(new Element("frameDuration").addContent(mfccParams.getFrameDuration() + ""));
                        extractionElement.addContent(new Element("alphaPreEmphasis").addContent(mfccParams.getPreEmphasis() + ""));
                        extractionElement.addContent(new Element("overLap").addContent(mfccParams.getOverlap() + ""));
                        extractionElement.addContent(new Element("windowing").addContent(mfccParams.getWindowing() + ""));
                        extractionElement.addContent(new Element("numMelFilters").addContent(mfccParams.getNumMelFilters() + ""));
                        extractionElement.addContent(new Element("numCoefs").addContent(mfccParams.getNumCoefs() + ""));
                        extractionElement.addContent(new Element("powerSpectrum").addContent(mfccParams.isPowerSpectrum() + ""));
                        extractionElement.addContent(new Element("lowerFreq").addContent(mfccParams.getLowerFreq() + ""));
                        extractionElement.addContent(new Element("upperFreq").addContent(mfccParams.getUpperFreq() + ""));
                        extractionElement.addContent(new Element("frameLength").addContent(mfccParams.getFrameLength() + ""));
                        break;
                }
            }

            configElement.addContent(extractionElement);

            Element classificationElement = new Element("classification");
            Classification classification = engine.getClassification();

            if (classification != null) {

                if (classification instanceof SvmParameters) {
                    SvmParameters svmParams = (SvmParameters) classification;
                    classificationElement.addContent(new Element("technique").addContent(ClassificationTechnique.SVM.toString()));
                    classificationElement.addContent(new Element("kernelType").addContent(svmParams.getKernelType() + ""));
                    classificationElement.addContent(new Element("cost").addContent(svmParams.getCost() + ""));
                    classificationElement.addContent(new Element("degree").addContent(svmParams.getDegree() + ""));
                    classificationElement.addContent(new Element("gamma").addContent(svmParams.getGamma() + ""));
                    classificationElement.addContent(new Element("thresholdValue").addContent(svmParams.getThresholdValue() + ""));
                }
                if (classification instanceof HmmParameters) {
                    HmmParameters hmmParams = (HmmParameters) classification;
                    classificationElement.addContent(new Element("technique").addContent(ClassificationTechnique.HMM.toString()));
                    classificationElement.addContent(new Element("numGaussians").addContent(String.valueOf(hmmParams.getNumGaussians())));
                    classificationElement.addContent(new Element("numIterections").addContent(String.valueOf(hmmParams.getNumIteractions())));
                    classificationElement.addContent(new Element("numHERests").addContent(String.valueOf(hmmParams.getNumHERests())));
                    classificationElement.addContent(new Element("numStates").addContent(String.valueOf(hmmParams.getNumStates())));
                    HmmStatesType hmmTypes = hmmParams.getStatesType();
                    classificationElement.addContent(new Element("hmmTypes").addContent(hmmTypes.toString()));
                    HmmTopology hmmTopology = hmmParams.getTopology();
                    classificationElement.addContent(new Element("hmmtopology").addContent(hmmTopology.toString()));
                    classificationElement.addContent(new Element("thresholdValue").addContent(hmmParams.getThresholdValue() + ""));
                    classificationElement.addContent(new Element("unitSize").addContent(hmmParams.getUnitSize() + ""));
                }
            }

            configElement.addContent(classificationElement);

            Document configDocument = new Document(configElement);
            XMLOutputter configOutputter = new XMLOutputter(Format.getPrettyFormat());

            File configFile = new File(enginePath + File.separator + "config.xml");
            out = new BufferedWriter(new FileWriter(configFile));
            out.write(configOutputter.outputString(configDocument));
            out.close();

            if (project.getType() == ProjectType.ASR) {

                SpeechEngine speechEngine = (SpeechEngine) engine;

                //Cria dicionário
                Dictionary dictionary = speechEngine.getDictionary();                
                List<Word> words = dictionary.getWords();

                if (engine.getClassification().getTechnique().equals(ClassificationTechnique.HMM)) {

                    HmmParameters params = (HmmParameters) engine.getClassification();
                    
                    String dictionaryFile = enginePath + File.separator + "dictionary.txt";

                    if (params.getUnitSize().equals(HmmUnitSize.WORD)) {
                        HmmCommands.createsWordDictionary(words, dictionaryFile);
                    } else if (params.getUnitSize().equals(HmmUnitSize.PHONEMES)) {
                        HmmCommands.createsMonophonesDictionary(words, dictionaryFile);
                    }
                }

                //Cria gramática
                if (classification instanceof SvmParameters) {

                    Grammar grammar = speechEngine.getGrammar();
                    RuleGrammar rulegrammar = grammar.getRuleGrammars().get(0);
                    List<String> descriptions = rulegrammar.getWords();                    
                    
                    File grammarFile = new File(enginePath + File.separator + "grammar.xml");

                    Element grammarElement = new Element("grammar");
                    Element ruleElement = new Element("rule");
                    ruleElement.setAttribute("id", "0");
                    Element oneofElement = new Element("one-of");
                    for (String description : descriptions) {
                        Element item = new Element("item");
                        item.addContent(description);
                        oneofElement.addContent(item);
                    }
                    ruleElement.addContent(oneofElement);
                    grammarElement.addContent(ruleElement);

                    Document grammarDocument = new Document(grammarElement);
                    XMLOutputter grammarOutputter = new XMLOutputter(Format.getPrettyFormat());

                    out = new BufferedWriter(new FileWriter(grammarFile));
                    out.write(grammarOutputter.outputString(grammarDocument));
                    out.close();

                } else if (classification instanceof HmmParameters) {
                                        
                    HmmParameters params = (HmmParameters) classification;

                    String grammarFile = enginePath + File.separator + "grammar.txt";                                                            
                    
                    if (params.getUnitSize().equals(HmmUnitSize.WORD)) {

                        String grammarCommand = "(" + '\n' + '\t' + "$COMAND" + '\n' + ")";
                        HmmCommands.createsDefaultGrammar(words, grammarFile, grammarCommand);
                        
                        String vocabularyFile = enginePath + File.separator + "vocabulary.txt";
                        HmmCommands.createsVocabulary(words, vocabularyFile);
                        
                    } else if (params.getUnitSize().equals(HmmUnitSize.PHONEMES)) {

                        String grammarCommand =  "(" + '\n' + '\t' + "(SILENCE\t$COMAND\tSILENCE)" + '\n' + ")";
                        HmmCommands.createsDefaultGrammar(words, grammarFile, grammarCommand);
                        
                        String monophonesList = enginePath + File.separator + "monophones.txt";                        
                        HmmCommands.createsMonophonesList(words, monophonesList);
                    }
                }

                //Copia modelo                
                String modelsPath = projectPath + File.separator + "models";
                ProjectUtil.copyFile(modelsPath + File.separator + speechEngine.getModelFile(), enginePath + File.separator + "models.mod");

            }
            if (project.getType() == ProjectType.ASV) {

                SpeakerEngine speakerEngine = (SpeakerEngine) engine;

                String modelPath = project.getDirectory() + File.separator + "models";

                //Cria arquivo de locutores
                File speakersFile = new File(project.getDirectory() + File.separator + "engines" + File.separator + "speakers.txt");
                out = new BufferedWriter(new FileWriter(speakersFile));

                List<Speaker> speakers = speakerEngine.getSpeakers();
                for (Speaker speaker : speakers) {

                    out.write(speaker.getId() + " " + speaker.getName() + "\n");

                    //Copia modelos                              
                    String originModel = modelPath + File.separator + "models_" + speaker.getId();

                    String destinPath = enginePath + File.separator + "models" + File.separator + speaker.getId();
                    ProjectUtil.createFolder(destinPath);

                    String destinModel = destinPath + File.separator + "models_" + speaker.getId();
                    ProjectUtil.copyFile(originModel, destinModel);
                }
                out.close();
            }
            if (project.getType() == ProjectType.TTS) {

                SynthesisEngine synthesisEngine = (SynthesisEngine) engine;

                String modelPath = project.getDirectory() + File.separator + "models";

                //Cria arquivo de locutores
                File speakersFile = new File(project.getDirectory() + File.separator + "engines" + File.separator + "speakers");
                out = new BufferedWriter(new FileWriter(speakersFile));

                List<Speaker> speakers = synthesisEngine.getSpeakers();
                for (Speaker speaker : speakers) {

                    out.write(speaker.getName() + "\n");

                    //Copia modelos                              
                    String originModel = modelPath + File.separator + "models_" + speaker.getId();

                    String destinPath = enginePath + File.separator + "models" + File.separator + speaker.getId();
                    ProjectUtil.createFolder(destinPath);

                    String destinModel = destinPath + File.separator + "models_" + speaker.getId();
                    ProjectUtil.copyFile(originModel, destinModel);
                }
                out.close();
            }
        } catch (Exception ex) {
            throw new EngineException(ex.getMessage());
        }
    }
}
