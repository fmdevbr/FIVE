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
import br.ufpe.cin.five.core.project.ProjectUtil;
import br.ufpe.cin.five.core.sample.Sample;
import br.ufpe.cin.five.core.utterance.Phrase;
import br.ufpe.cin.five.core.utterance.Utterance;
import br.ufpe.cin.five.core.utterance.Word;
import java.io.*;
import java.util.*;

/**
 *
 * @author Alexandre
 */
public class HmmMonophoneProcess extends ClassificationProcess {

    private String projectPath;
    private String featuresPath;
    private String tempPath;
    private String htkPath;
    private HashMap<Integer, List<Word>> wordsPerUtterance;
    private double maxPercent = 0;
    private int bestMixture = 0;
    private String bestMatriz = "";
    private String bestResult = "";

    /**
     * Creates a new HmmMonoPhoneProcess specifying the project path
     *
     * @param projectPath the project path
     */
    public HmmMonophoneProcess(String projectPath) {
        this.projectPath = projectPath;
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
            String monophonesFile = "monophones.txt";
            //String vocabularyFile = "vocabulary.txt";
            String grammarFile = "grammar.txt";
            String dictionaryFile = "dictionary.txt";
            String mlfTrainFile = "training.mlf";
            String scriptTrainFile = "trainingAll.scp";
            String prototypeFile = "proto";

            //Criando arquivo de configurações
            String configFile = "configClass.txt";
            createConfigHtkFile(configFile);

            String head = "--------------------------------------------------------------------";
            StringBuffer header = new StringBuffer(head);
            int startHeader = 22;        
                               
            ///Carregando parâmentros de classificação
            int numberGaussians = hmmParameters.getNumGaussians();
            int numberHERest = hmmParameters.getNumHERests();
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

            System.out.println(header.insert(startHeader, "Classificacao HMM Monophone"));
            header = new StringBuffer(head);

            System.out.println(header.insert(startHeader, "Criando arquivos"));
            header = new StringBuffer(head);
                    
            //Criando vocabulário,dicionário e gramática
            //HmmCommands.createsVocabulary(words, tempPath + File.separator + vocabularyFile);
            HmmCommands.createsMonophonesDictionary(words, tempPath + File.separator + dictionaryFile);
            HmmCommands.createsDefaultGrammar(words, tempPath + File.separator + grammarFile, "(" + '\n' + '\t' + "(SILENCE\t$COMAND\tSILENCE)" + '\n' + ")");
            
            System.out.println("Vocabulario,Dicionario e Gramatica criados na pasta:   ");
            System.out.println('\t' + tempPath);
            
            //Criando lista de monophones
            HmmCommands.createsMonophonesList(words, tempPath + File.separator + monophonesFile);
            
            //Criando prototipos dos hmms                
            HmmCommands.createPrototype(topology, "proto", "MFCC_0_D_A", 39, numberStates + 2, 1, prototypesPath + File.separator + prototypeFile);
            System.out.println("Prototipos criados na pasta:");
            System.out.println('\t' + prototypesPath);            
            
            //Criando mlf de treino        
            HmmCommands.createsMlfTrain(HmmUnitSize.PHONEMES, trainningSamples, wordsPerUtterance, mlfsPath + File.separator + mlfTrainFile);            
            
            System.out.println("Mlfs criados na pasta: ");
            System.out.println('\t' + mlfsPath);
            
            //Criando scripts files
            ArrayList<String> trainingFeatures = new ArrayList<String>();
            for (Sample sample : trainningSamples) {
                trainingFeatures.add(featuresPath + File.separator + sample.getFeatureFile());
            }
            HmmCommands.createsScriptFile(trainingFeatures, scriptPath + File.separator + scriptTrainFile);

            System.out.println("Scripts de Treino e Teste criados na pasta:");
            System.out.println('\t' + scriptPath);   
            
            //Criando os hedscripts        
            for (int mixtures = 2; mixtures <= hmmParameters.getNumGaussians(); mixtures++) {
                HmmCommands.createsHedFile("*", mixtures, numberStates, hedscriptPath + File.separator + "mix" + mixtures + ".hed");
            }
            //Criando o hedscript do sil
            HmmCommands.createsHedSilFile(hedscriptPath + File.separator + "sil.hed");        

            System.out.println("HedScripts criados na pasta:");
            System.out.println('\t' + hedscriptPath);              
            
            //Executando os comandos HTK
            File dirHtkModels = new File(modelsPath);
            File hmm0 = new File(dirHtkModels, "hmm0");
            File hmm1_0 = new File(dirHtkModels, "hmm1_0");

            ProjectUtil.createFolder(hmm0.getPath());
            ProjectUtil.createFolder(hmm1_0.getPath());

            System.out.println(header.insert(startHeader, "Executando comandos HTK"));
            header = new StringBuffer(head);
            
            //HCompV()
            System.out.println(header.insert(startHeader, "Executando HCompV"));
            header = new StringBuffer(head);
            HmmCommands.executeHCompv(htkPath, tempPath + File.separator+ configFile, scriptPath + File.separator + scriptTrainFile, prototypesPath + File.separator + prototypeFile, hmm0.getPath());
            
            //Criando modelo para reestimação embebida     
            HmmCommands.createHmmEmbeddedMonophones(tempPath + File.separator + monophonesFile, hmm0.getPath() + File.separator + "proto", hmm0.getPath() + File.separator + "VFloors", "MFCC_0_D_A", hmm1_0.getPath());
            //HParse
            System.out.println(header.insert(startHeader, "Executando HParse"));
            header = new StringBuffer(head);
            System.out.println("Executando HParse ");

            HmmCommands.executeHParse(htkPath, tempPath + File.separator + grammarFile, tempPath + File.separator + "net.txt");            
            
            //HERest
            for (int mixture = 1; mixture <= numberGaussians; mixture++) {
                String hmmDest = "";

                {//Executando HHed
                    String model = modelsPath + File.separator + "hmm" + (mixture - 1) + "_3" + File.separator + "models.mod";
                    String outFolderModel = modelsPath + File.separator + "hmm" + mixture + "_" + 0;
                    String hedScript = hedscriptPath + File.separator + "mix" + mixture + ".hed";
                    if (mixture != 1) {
                        System.out.println(header.insert(startHeader, "Executando HHEd"));
                        header = new StringBuffer(head);
                        String hmmDest0 = "hmm" + (mixture) + "_" + 0;
                        File hmmPaste = new File(dirHtkModels, hmmDest0);
                        ProjectUtil.createFolder(hmmPaste.getPath());
                        System.out.println("Adicionando " + mixture + " misturas (HHEd) ");
                        HmmCommands.executeHHed(htkPath, model, hedScript, tempPath + File.separator + monophonesFile, outFolderModel, "models.mod");
                    }
                }
                System.out.println(header.insert(startHeader, "Executando HERest"));
                header = new StringBuffer(head);
                for (int i = 0; i < numberHERest; i++) {
                    String pasteSource = File.separator + "hmm" + mixture + "_" + i;
                    String hmmSource = modelsPath + pasteSource + File.separator + "models.mod";
                    String pasteDest = File.separator + "hmm" + mixture + "_" + (i + 1);
                    hmmDest = modelsPath + pasteDest;
                    File hmmPaste = new File(dirHtkModels, pasteSource);
                    ProjectUtil.createFolder(hmmPaste.getPath());
                    hmmPaste = new File(dirHtkModels, pasteDest);
                    ProjectUtil.createFolder(hmmPaste.getPath());
                    System.out.println("Executando HERest " + i);
                    String mlfFile = mlfsPath + File.separator + mlfTrainFile;
                    String scpTrainScript = scriptPath + File.separator + scriptTrainFile;
                    String modelFile = hmmSource;
                    String outFolderModel = hmmDest;
                    String listModels = tempPath + File.separator + monophonesFile;
                    HmmCommands.executeHERest(htkPath, tempPath + File.separator + configFile, modelFile, mlfFile, scpTrainScript, listModels, outFolderModel);
                }
                //Aperfeicoar o HMM do silencio
                if (mixture == 1) {
                    String pathHm = "hmm1_" + (numberHERest + 1);
                    String hmmSource = hmmDest + File.separator + "models.mod";
                    File hmmPaste = new File(dirHtkModels, pathHm);
                    ProjectUtil.createFolder(hmmPaste.getPath());
                    HmmCommands.executeHHed(htkPath, hmmSource, hedscriptPath + File.separator + "sil.hed", tempPath + File.separator + monophonesFile, hmmPaste.getPath(), "models.mod");
                    hmmDest = hmmPaste.getPath();
                    for (int i = 2; i <= 3; i++) {
                        pathHm = "hmm1_" + (numberHERest + i);
                        hmmPaste = new File(dirHtkModels, pathHm);
                        hmmSource = hmmDest + File.separator + "models.mod";
                        ProjectUtil.createFolder(hmmPaste.getPath());
                        hmmDest = hmmPaste.getPath();
                        String mlfFile = mlfsPath + File.separator + mlfTrainFile;
                        String scpTrainScript = scriptPath + File.separator + scriptTrainFile;
                        String modelFile = hmmSource;
                        String outFolderModel = hmmDest;
                        String listModels = tempPath + File.separator + monophonesFile;
                        HmmCommands.executeHERest(htkPath, tempPath + File.separator + configFile, modelFile, mlfFile, scpTrainScript, listModels, outFolderModel);

                    }
                }           
            }            
            
        } catch (Exception ex){
            throw new ClassificationException(ex.getMessage());    
        }
    }
        
    @Override
    public ClassificationResult test(Object params, ArrayList<Sample> testSamples) throws ClassificationException {
        try {

            HmmParameters hmmParameters = (HmmParameters) params;

            //Path de todas as pastas em htk.
            String mlfsPath = tempPath + File.separator + "mlfs";
            String scriptPath = tempPath + File.separator + "scripts";
            String modelsPath = tempPath + File.separator + "models";

            String testPath = tempPath + File.separator + "test";
            ProjectUtil.createFolder(testPath);
            String resultPath = tempPath + File.separator + "results";
            ProjectUtil.createFolder(resultPath);

            //Nome dos arquivos gerado
            String monophonesFile = "monophones.txt";
            String vocabularyFile = "vocabulary.txt";
            String dictionaryFile = "dictionary.txt";
            String mlfTestFile = "test.mlf";
            String scriptTestFile = "testAll.scp";
            String configFile = "configClass.txt";

            String head = "--------------------------------------------------------------------";
            StringBuffer header = new StringBuffer(head);
            int startHeader = 22;     
        
            //Criando mlf de teste
            HmmCommands.createsMlfTest(testSamples, wordsPerUtterance, mlfsPath + File.separator + mlfTestFile);

            //Criando scripts files para teste
            ArrayList<String> testingFeatures = new ArrayList<String>();
            for (Sample sample : testSamples) {
                testingFeatures.add(featuresPath + File.separator + sample.getFeatureFile());
            }
            HmmCommands.createsScriptFile(testingFeatures, scriptPath + File.separator + scriptTestFile);            
                        
            //Numero de iteracoes e HERest
            int numberGaussians = hmmParameters.getNumGaussians();
            int numberHERest = hmmParameters.getNumHERests();

            configFile = tempPath + File.separator + configFile;
            dictionaryFile = tempPath + File.separator + dictionaryFile;

            for (int mixture = 1; mixture <= numberGaussians; mixture++) {            
                
                String hmmDest = modelsPath + File.separator + "hmm" + mixture + "_" + numberHERest;
                
                //HVite
                System.out.println(header.insert(startHeader, "Executando HVite"));
                header = new StringBuffer(head);
                System.out.println("Executando Hvite " + mixture);
                String outHViteFile = testPath + File.separator + "HVite" + mixture + ".mlf";
                String modelFile = hmmDest + File.separator + "models.mod";
                String scpTestScript = scriptPath + File.separator + scriptTestFile;
                String netFile = tempPath + File.separator + "net.txt";
                String listModels = tempPath + File.separator + monophonesFile;

                HmmCommands.executeHVite(htkPath, configFile, modelFile, scpTestScript, netFile, dictionaryFile, listModels, outHViteFile);

                //HResult
                System.out.println(header.insert(startHeader, "Resultados para " + mixture + " MISTURAS"));
                header = new StringBuffer(head);
                
                String resultFile = resultPath + File.separator + "result.txt";
                HmmCommands.executeHResults(htkPath, mlfsPath + File.separator + mlfTestFile, tempPath + File.separator + vocabularyFile, outHViteFile, true, resultFile);

                analyzeAndReportResult(resultFile, mixture);
            }

            head = "====================================================================";
            header = new StringBuffer(head);
            System.out.println(header.insert(startHeader - 5, "Melhor resultado: " + maxPercent + "  Numero de Gaussianas: " + bestMixture));
            System.out.println(bestMatriz);
            System.out.println(bestResult);
            String pathHmm = modelsPath + File.separator + "hmm" + bestMixture + "_3";
            ProjectUtil.copyFile(pathHmm + File.separator + "models.mod", tempPath + File.separator + "models.mod");
            
        } catch (Exception ex) {
          throw new ClassificationException(ex.getMessage());                      
        }
        return null;
    }

    /**
     * Auxiliary method to analyze and report the result file
     *
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
    
    @Override
    public Object test(double[] feature, String model) throws ClassificationException {
        try {

            // Carrega arquivo de configuração
            String configFile = "configClass.txt";
            createConfigHtkFile(configFile);
            
            // Carrega arquivo de modelos
            String modelFile = projectPath + File.separator + "models.mod";

            // Cria arquivo de script de características
            String scpTestScript = tempPath + File.separator + "scriptTestFile.scp";
            ArrayList<String> features = new ArrayList<String>();
            features.add(tempPath + File.separator + "informed.mfc");
            HmmCommands.createsScriptFile(features, scpTestScript);

            // Carrega gramática e cria rede
            String grammarFile = projectPath + File.separator + "grammar.txt";
            String netFile = tempPath + File.separator + "net.txt";
            HmmCommands.executeHParse(htkPath, grammarFile, netFile);

            // Carregar Dicionário e cria vocabulário
            String dictionaryFile = projectPath + File.separator + "dictionary.txt";
            String monophonesFile = projectPath + File.separator + "monophones.txt";                        
                       
            // Seta nome do arquivo de saída
            String outHViteFile = tempPath + File.separator + "hvite.mlf";

            // Executa comando
            HmmCommands.executeHVite(htkPath, tempPath + File.separator + configFile, modelFile, scpTestScript, netFile, dictionaryFile, monophonesFile, outHViteFile);

            String anwser = readHVite(outHViteFile);

            return anwser;

        } catch (Exception ex) {
            throw new ClassificationException(ex.getMessage());
        }
    }

    private String readHVite(String outHviteFile) {
        String answer = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(outHviteFile));
            int i = 0;
            while (br.ready()) {
                String line = br.readLine();
                i++;
                if (i == 3) {
                    String[] content = line.split(" ");
                    answer = content[2];
                    break;
                }
            }

        } catch (Exception ex) {
        }
        return answer;
    }  
    
    private void createConfigHtkFile(String configFile) throws IOException {
        BufferedWriter bwConfig = new BufferedWriter(new FileWriter(tempPath + File.separator + configFile));
        bwConfig.write("# Coding parameters \n");
        bwConfig.write("SOURCEFORMAT = HTK \n");
        bwConfig.write("NONUMESCAPES = TRUE \n");
        bwConfig.flush();
        bwConfig.close();
    }    
}
