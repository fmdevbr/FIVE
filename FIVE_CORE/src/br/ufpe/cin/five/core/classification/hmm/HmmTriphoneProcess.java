/**
 * Copyright 2011 Federal University of Pernambuco. All Rights Reserved. Use is
 * subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.classification.hmm;

import br.ufpe.cin.five.core.classification.ClassificationException;
import br.ufpe.cin.five.core.classification.ClassificationProcess;
import br.ufpe.cin.five.core.classification.ClassificationResult;
import br.ufpe.cin.five.core.extraction.mgcc.ForcedAlignment;
import br.ufpe.cin.five.core.project.ProjectType;
import br.ufpe.cin.five.core.project.ProjectUtil;
import br.ufpe.cin.five.core.sample.Sample;
import br.ufpe.cin.five.core.utterance.Phrase;
import br.ufpe.cin.five.core.utterance.Utterance;
import br.ufpe.cin.five.core.utterance.Word;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Alexandre
 */
public class HmmTriphoneProcess extends ClassificationProcess {

    private String samplesPath;
    private String featuresPath;
    private String tempPath;
    private String htkPath;
    private HashMap<Integer, List<Word>> wordsPerUtterance;
    private double maxPercent = 0;
    private int bestMixture = 0;
    private String bestMatriz = "";
    private String bestResult = "";

    /**
     * Creates a new HmmTriphoneProcess specifying the project path
     *
     * @param projectPath the project path
     */
    public HmmTriphoneProcess(String projectPath) {
        this.samplesPath = projectPath + File.separator + "samples";
        this.featuresPath = projectPath + File.separator + "features";
        this.tempPath = projectPath + File.separator + "temp";
        this.htkPath = projectPath + File.separator + "resources" + File.separator + "htk-3.3";        
    }

    @Override
    public void train(Object params, ArrayList<Sample> trainningSamples) throws ClassificationException {

        try {
            
            HmmParameters hmmParameters = (HmmParameters) params;
            
            // Cria pasta temporária
            ProjectUtil.createFolder(tempPath);

            //Path de todas as pastas em htk.
            String prototypesPath = tempPath + File.separator + "prototypes";
            ProjectUtil.createFolder(prototypesPath);
            String mlfsPath = tempPath + File.separator + "mlfs";
            ProjectUtil.createFolder(mlfsPath);
            String scriptPath = tempPath + File.separator + "scripts";
            ProjectUtil.createFolder(scriptPath);
            String hedscriptPath = tempPath + File.separator + "hedscripts";
            ProjectUtil.createFolder(hedscriptPath);
            String modelsPath = tempPath + File.separator + "models";
            ProjectUtil.createFolder(modelsPath);
        
            //Nome dos arquivos gerado
            //String monophonesList = "monophones.txt";
            //String vocabularyFile = "vocabulary.txt";
            String grammarFile = "grammar.txt";
            String dictionaryFile = "dictionary.txt";
            //String mlfTrainFile = "training.mlf";
            //String scriptTrainFile = "trainingAll.scp";
            String prototypeFile = tempPath + File.separator + "proto";

            //Criando arquivo de configurações
            String configFile = "configClass.txt";
            createConfigHtkFile(configFile);

            String head = "--------------------------------------------------------------------";
            StringBuffer header = new StringBuffer(head);
            int startHeader = 22;        
           
            //Captura caminho do htk                
            String mlfTest = "test.mlf";
            String scriptTestAll = "testAll.scp";                       
         
            ProjectUtil.copyFile(configFile, tempPath + File.separator + "prototypeConfig");
            
            //Numero de iteracoes e HERest
            int numberStates = hmmParameters.getNumStates();
            HmmTopology topology = hmmParameters.getTopology();
            
            //Buscando palavras não repetidas de todas as locucoes selecionadas
            wordsPerUtterance = new HashMap<Integer, List<Word>>();

            List<String> wordsSet = new ArrayList();            
            List<Word> words = new ArrayList<Word>();
            for (Sample sample : trainningSamples) {
                Utterance utterance = sample.getUtterance();
                List<Phrase> phrases = utterance.getPhrases();
                List<Word> allWordsUtterance = new ArrayList<Word>();
                for (Phrase phrase : phrases) {
                    List<Word> wrds = phrase.getWords();
                    for (Word word : wrds) {
                        allWordsUtterance.add(word);
                        if (!wordsSet.contains(word.getDescription())) {
                            wordsSet.add(word.getDescription());
                            words.add(word);
                        }
                    }
                }
                wordsPerUtterance.put(utterance.getId(), allWordsUtterance);
            }

            System.out.println(header.insert(startHeader, "Classificacao HMMFonetic"));
            header = new StringBuffer(head);

            System.out.println(header.insert(startHeader, "Criando Scripts"));
            header = new StringBuffer(head);

            //Criar hedscripts
            String pathHedscript = tempPath + File.separator + "hedscripts";
            int statesOfHMm = hmmParameters.getNumStates();
            for (int mixtures = 2; mixtures <= hmmParameters.getNumGaussians(); mixtures += 2) {
                BufferedWriter hedscript = new BufferedWriter(new FileWriter(pathHedscript + File.separator + "mix" + mixtures + ".hed"));
                String comando = "MU " + mixtures + " {*.state[2-" + (statesOfHMm + 1) + "].mix}";
                hedscript.write(comando + '\n');
                hedscript.close();
            }            
            System.out.println("HedScripts criados na pasta:");
            System.out.println('\t' + hedscriptPath);              
            
            //Criar mlf para Teste
//            BufferedWriter mlfs = new BufferedWriter(new FileWriter(tempPath + File.separator + mlfTest));
//            mlfs.write("#!MLF!#" + '\n');
//            for (Sample sample : testingSamples) {
//                String features = "features" + File.separator;
//                String featureFile = sample.getFeatureFile();
//                String out = featureFile.substring(features.length(), featureFile.length() - 3) + "lab";
//                mlfs.write('\"' + "*/" + out + '\"' + '\n');
//                ArrayList<Word> wrds = wordsPerUtterance.get(sample.getUtterance().getId());
//                for (Word word : wrds) {
//                    mlfs.write(word.getDescription().toUpperCase() + '\n');
//                }
//
//                mlfs.write("." + '\n');
//            }
//            mlfs.close();
//            System.out.println("MLF pata Teste Criado.");
            
            //Criar gramática padrão
            BufferedWriter gram = new BufferedWriter(new FileWriter(tempPath + File.separator + grammarFile));
            gram.write("$COMANDO = ");
            for (int countWord = 0; countWord < words.size(); countWord++) {
                String word = words.get(countWord).getDescription().toUpperCase();
                if (countWord + 1 < words.size()) {
                    gram.write(word + " | ");
                } else {
                    gram.write(word + " ;" + '\n');
                }
            }
            gram.write("(" + '\n' + '\t' + " SENT-START $COMANDO SENT-END" + '\n' + ")");
            gram.close();

            System.out.println("Gramatica Padrão Criada.");
            
            //Criar script de test
//            BufferedWriter scriptFileAll = new BufferedWriter(new FileWriter(tempPath + File.separator + scriptTestAll));
//                for (Sample sample : testingSamples) {
//                    scriptFileAll.write(projectPath + File.separator + sample.getFeatureFile() + '\n');
//                }
//            scriptFileAll.close();

            
            //Criar prototipos
            System.out.println(header.insert(startHeader, "Criando Prototipos"));
            header = new StringBuffer(head);
            
            HmmCommands.createPrototype(topology, "proto", "MFCC_0_D_A", 39, numberStates + 2, 1, prototypeFile);
            
            System.out.println("Prototipos criados");
            List<String> listAudio = new ArrayList<String>();
            List<String> listTxt = new ArrayList<String>();
            
            //Preenhcer lista de waves e texto para treino
            for (Sample sample : trainningSamples) {
                listAudio.add(samplesPath + File.separator + sample.getAudioFile());
                String txt = "";
                List<Word> wrds = wordsPerUtterance.get(sample.getUtterance().getId());
                for (Word word : wrds) {
                    txt += (word.getDescription().toUpperCase() + ' ');
                }
                listTxt.add(txt.trim());
            }
            
            System.out.println(header.insert(startHeader, "Executando treinamento Fonetico"));
            header = new StringBuffer(head);

            //Executar treino fonético
            String resourcesPath = System.getProperty("user.dir") + File.separator + "resources";
            ForcedAlignment fd = new ForcedAlignment(ProjectType.ASR, resourcesPath, tempPath, featuresPath, listAudio, listTxt);
            ArrayList<String> allWordList = new ArrayList<String>();
            for (Word word : words) {
                allWordList.add(word.getDescription().toUpperCase());
            }
            fd.setAllWordList(allWordList);
            fd.run();
            fd.join();

            String netFile = tempPath + File.separator + "net.txt";
            String wordListFile = tempPath + File.separator + "wordList";

            
            System.out.println("Criando rede através da gramatica");
            String grammarFileFile = tempPath + File.separator + grammarFile;
            HmmCommands.executeHParse(htkPath, grammarFileFile, netFile);            

            
            System.out.println(header.insert(startHeader, "Adicionando Misturas ao Modelo"));
            header = new StringBuffer(head);
            {//Adicionar misturas aos modelos

                String configClass = configFile;
                String wintriMLF = tempPath + File.separator + "wintri.mlf";
                String trainSCP = tempPath + File.separator + "trainMfccScp";
                String tiedlist = tempPath + File.separator + "tiedlist";
                String folderHMM16 = tempPath + File.separator + "HMM16";
                String folderHMM17 = tempPath + File.separator + "HMM17";
                String folderHMM18 = tempPath + File.separator + "HMM18";
                String folderHMM19 = tempPath + File.separator + "HMM19";

                // Criando Pasta para os protótipos
                ProjectUtil.createFolder(folderHMM16);
                ProjectUtil.createFolder(folderHMM17);
                ProjectUtil.createFolder(folderHMM18);
                ProjectUtil.createFolder(folderHMM19);

                System.out.println("Adicionando 2 misturas");
                {//Executando HHed para 2 misturas
                    String macro = tempPath + File.separator + "HMM15" + File.separator + "macros";
                    String def = tempPath + File.separator + "HMM15" + File.separator + "hmmdefs";
                    String hedscripts = tempPath + File.separator + "hedscripts" + File.separator + "mix2.hed";
                    String[] models = {macro, def};
                    HmmCommands.executeHHed(htkPath, models, hedscripts, tiedlist, folderHMM16, "models");
                }
                System.out.println("Reestimando os modelos");
                {//Executando HERest para 2 misturas
                    String model = folderHMM16 + File.separator + "models";
                    String[] models = {model};
                    HmmCommands.executeHERest(htkPath, configClass, models, wintriMLF, trainSCP, tiedlist, folderHMM17);
                    model = folderHMM17 + File.separator + "models";
                    models[0] = model;
                    HmmCommands.executeHERest(htkPath, configClass, models, wintriMLF, trainSCP, tiedlist, folderHMM18);
                    model = folderHMM18 + File.separator + "models";
                    models[0] = model;
                    HmmCommands.executeHERest(htkPath, configClass, models, wintriMLF, trainSCP, tiedlist, folderHMM19);
                }
                {//Executando HHed e HERest para 4, 6, 8, 10, 12 , 14 misturas
                    for (int mixture = 2; mixture <= hmmParameters.getNumGaussians(); mixture += 2) {//Começo do For
                        if (mixture != 2) {
                            System.out.println("Adicionando " + mixture + " misturas");
                            {//Executando HHEd
                                String model = folderHMM19 + File.separator + "models";
                                String hedscripts = tempPath + File.separator + "hedscripts" + File.separator + "mix" + mixture + ".hed";
                                String[] models = {model};
                                HmmCommands.executeHHed(htkPath, models, hedscripts, tiedlist, folderHMM16, "models");
                            }
                            System.out.println("Reestimando os modelos");
                            {//Executando HERest
                                String model = folderHMM16 + File.separator + "models";
                                String[] models = {model};
                                HmmCommands.executeHERest(htkPath, configClass, models, wintriMLF, trainSCP, tiedlist, folderHMM17);
                                model = folderHMM17 + File.separator + "models";
                                models[0] = model;
                                HmmCommands.executeHERest(htkPath, configClass, models, wintriMLF, trainSCP, tiedlist, folderHMM18);
                                model = folderHMM18 + File.separator + "models";
                                models[0] = model;
                                HmmCommands.executeHERest(htkPath, configClass, models, wintriMLF, trainSCP, tiedlist, folderHMM19);
                            }
                        }
                        System.out.println(header.insert(startHeader, "Testando Modelo"));
                        header = new StringBuffer(head);

                        {//Executar comando de Reconhecimento
                            System.out.println("Executando Hvite ");
                            {//HVite
                                String model = tempPath + File.separator + "HMM19" + File.separator + "models";
                                String tiedlistFile = tempPath + File.separator + "tiedlist";
                                String scpTestFile = tempPath + File.separator + scriptTestAll;
                                String outHViteFile = tempPath + File.separator + "tests" + File.separator + "HVite.mlf";
                                HmmCommands.executeHVite(htkPath, configFile, model, scpTestFile, netFile, dictionaryFile, tiedlistFile, outHViteFile);
                            }// Fim Hvite
                            System.out.println(header.insert(startHeader, "Resultado"));
                            header = new StringBuffer(head);
                            {//HResults
                                mlfTest = tempPath + File.separator + mlfTest; // Mudar colocar como global
                                String HViteDest = tempPath + File.separator + "tests" + File.separator + "HVite.mlf";// Mudar colocar como global
                                String resultFile = tempPath + File.separator + "result.txt";
                                HmmCommands.executeHResults(htkPath, mlfTest, wordListFile, HViteDest, true, resultFile);
                                analyzeAndReportResult(resultFile, mixture);
                            }//Fim Hresults
                        }// Fim Executar comando Reconhecimento                        
                        
                    }
                }//Final de adicionar mistura
                System.out.println(header.insert(startHeader - 5, "Melhor resultado: " + maxPercent + "  Numero de Gaussianas: " + bestMixture));
                System.out.println(bestMatriz);
                System.out.println(bestResult);
                String model = tempPath + File.separator + "HMM19" + File.separator + "models";
                ProjectUtil.copyFile(model, modelsPath + File.separator + "models");
                ProjectUtil.copyFile(tempPath + File.separator + "tiedlist", tempPath + File.separator + "modelsList");
                ProjectUtil.copyFile(tempPath + File.separator + "dict", tempPath + File.separator + "dictionary.txt");
                ProjectUtil.copyFile(tempPath + File.separator + "net.txt", tempPath + File.separator + "net.txt");
            }
        } catch(Exception ex) {
        
        }
    }   

    /**
     * Auxiliary method to analyze and report the result file
     * @param resultFile the result file
     * @param mixture the mixture that the result file was generated
     * @throws IOException
     */
    private void analyzeAndReportResult(String resultFile, int mixture) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(resultFile));
        String buffString = "";
        while (in.ready()) {
            buffString = buffString + in.readLine() + '\n';
        }
        String[] bufferArray = buffString.toString().split("\n");

        String result = "";
        int linha = 0;
        while (!bufferArray[linha].contains("=")) {
            result += bufferArray[linha++] + "\n";
        }

        String matriz = null;
        for (int i = 0; i < buffString.length(); i++) {
            if (buffString.charAt(i) == '=') {
                matriz = buffString.substring(i);
                break;
            }
        }
        double percent = 0;
        if (matriz != null) {
            String[] lines = matriz.split("\n");
            System.out.println(lines[5]);
            String[] lineOfResults = lines[5].split(" ");
            String correctString = lineOfResults[1];
            for (int i = 0; i < correctString.length(); i++) {
                if (correctString.charAt(i) == '=') {
                    String pct = correctString.substring(i + 1);
                    try {
                        percent = new Double(pct);
                    } catch (NumberFormatException ex) {
                        percent = 0;
                    }
                    if (percent > maxPercent) {
                        maxPercent = percent;
                        bestMixture = mixture;
                        bestMatriz = matriz;
                        bestResult = result;
                    }
                    break;
                }
            }
        }

    }
    
    private void createConfigHtkFile(String configFile) throws IOException {
        BufferedWriter bwConfig = new BufferedWriter(new FileWriter(tempPath + File.separator + configFile));
        bwConfig.write("# Coding parameters \n");
        bwConfig.write("SOURCEFORMAT = HTK \n");
        bwConfig.write("NONUMESCAPES = TRUE \n");
        bwConfig.flush();
        bwConfig.close();
    }

    @Override
    public ClassificationResult test(Object params, ArrayList<Sample> testSamples) throws ClassificationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object test(double[] feature, String model) throws ClassificationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
