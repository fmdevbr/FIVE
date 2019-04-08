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
public class HmmWordProcess extends ClassificationProcess {

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
     * Creates a new HmmProcess specifying the project path
     *
     * @param projectPath the project path
     */
    public HmmWordProcess(String projectPath) {
        this.projectPath = projectPath;
        this.featuresPath = projectPath + File.separator + "features";
        this.tempPath = projectPath + File.separator + "temp";
        this.htkPath = projectPath + File.separator + "resources" + File.separator + "htk-3.3";
    }
    public HmmWordProcess(String projectPath, String featuresPath) {
        this.projectPath = projectPath;
        this.featuresPath = featuresPath;
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
            String tempModelsPath = tempPath + File.separator + "models";
            ProjectUtil.createFolder(tempModelsPath);

            //Nome dos arquivos gerado
            String vocabularyFile = "vocabulary.txt";
            String grammarFile = "grammar.txt";
            String dictionaryFile = "dictionary.txt";
            String mlfTrainFile = "training.mlf";
            String scriptTrainFile = "trainingAll.scp";
            String scriptHeadFile = "training";

            //Criando arquivo de configurações
            String configFile = "configClass.txt";
            createConfigHtkFile(configFile);

            String head = "--------------------------------------------------------------------";
            StringBuffer header = new StringBuffer(head);
            int startHeader = 22;

            //Carregando parâmentros de classificação
            int numberGaussians = hmmParameters.getNumGaussians();
            int iterations = hmmParameters.getNumIteractions();
            int numberHERest = hmmParameters.getNumHERests();
            int numberStates = hmmParameters.getNumStates();
            HmmStatesType statesType = hmmParameters.getStatesType();
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

            System.out.println(header.insert(startHeader, "Classificacao HMM Word"));
            header = new StringBuffer(head);

            System.out.println(header.insert(startHeader, "Criando arquivos"));
            header = new StringBuffer(head);

            //Criando vocabulário,dicionário e gramática
            HmmCommands.createsVocabulary(words, tempPath + File.separator + vocabularyFile);
            HmmCommands.createsWordDictionary(words, tempPath + File.separator + dictionaryFile);
            HmmCommands.createsDefaultGrammar(words, tempPath + File.separator + grammarFile, "(" + '\n' + '\t' + "$COMAND" + '\n' + ")");

            System.out.println("Vocabulario,Dicionario e Gramatica criados na pasta:   ");
            System.out.println('\t' + tempPath);

            //Criando prototipos dos hmms
            for (Word word : words) {
                String pathHmm = prototypesPath + File.separator + word.getDescription();
                switch (statesType) {
                    case POR_FONEMA:
                        String[] phones = word.getPhoneticRepresentations().get(0).split(" ");
                        int numberOfStates = phones.length * numberStates + 2;
                        HmmCommands.createPrototype(topology, word.getDescription(), "MFCC_0_D_A", 39, numberOfStates, 1, pathHmm);
                        break;
                    case FIXO:
                        HmmCommands.createPrototype(topology, word.getDescription(), "MFCC_0_D_A", 39, numberStates, 1, pathHmm);
                        break;
                }
            }

            System.out.println("Prototipos criados na pasta:");
            System.out.println('\t' + prototypesPath);

            //Criando mlf de treino       
            HmmCommands.createsMlfTrain(HmmUnitSize.WORD, trainningSamples, wordsPerUtterance, mlfsPath + File.separator + mlfTrainFile);

            System.out.println("Mlfs criados na pasta: ");
            System.out.println('\t' + mlfsPath);

            //Criando scripts files
            Map<String, ArrayList<String>> mapWordToTrainSamples = new TreeMap<String, ArrayList<String>>();

            //Inicializando map
            for (Word word : words) {
                mapWordToTrainSamples.put(word.getDescription(), new ArrayList<String>());
            }
            for (Sample sample : trainningSamples) {
                List<Word> wrds = wordsPerUtterance.get(sample.getUtterance().getId());
                for (Word word : wrds) {
                    mapWordToTrainSamples.get(word.getDescription()).add(featuresPath + File.separator + sample.getFeatureFile());
                }
            }
            //Criando scripts de treino
            ArrayList<String> trainingFeatures = new ArrayList<String>();
            for (Sample sample : trainningSamples) {
                trainingFeatures.add(featuresPath + File.separator + sample.getFeatureFile());
            }
            HmmCommands.createsScriptFile(trainingFeatures, scriptPath + File.separator + scriptTrainFile);
            for (Word word : words) {
                ArrayList<String> samplesFeaturesPath = mapWordToTrainSamples.get(word.getDescription());
                HmmCommands.createsScriptFile(samplesFeaturesPath, scriptPath + File.separator + scriptHeadFile + word.getDescription() + ".scp");
            }

            System.out.println("Scripts de Treino criados na pasta:");
            System.out.println('\t' + scriptPath);

            //Criando os hedscripts
            for (int mixtures = 2; mixtures <= hmmParameters.getNumGaussians(); mixtures++) {
                for (Word word : words) {
                    switch (statesType) {
                        case POR_FONEMA:
                            String[] phones = word.getPhoneticRepresentations().get(0).split(" ");
                            int numberOfStates = phones.length * numberStates + 2;
                            HmmCommands.createsHedFile(word.getDescription(), mixtures, numberOfStates, hedscriptPath + File.separator + "mix" + mixtures + ".hed");
                            break;
                        case FIXO:
                            HmmCommands.createsHedFile(word.getDescription(), mixtures, numberStates, hedscriptPath + File.separator + "mix" + mixtures + ".hed");
                            break;
                    }
                }
            }

            System.out.println("HedScripts criados na pasta:");
            System.out.println('\t' + hedscriptPath);

            //Executando os comandos HTK
            System.out.println(header.insert(startHeader, "Executando comandos HTK"));
            header = new StringBuffer(head);
            File dirHtkModels = new File(tempModelsPath);

            //Hinit()
            File hmmInit = new File(dirHtkModels, "hmmInit");
            ProjectUtil.createFolder(hmmInit.getPath());
            System.out.println(header.insert(startHeader, "Executando HInit"));
            header = new StringBuffer(head);

            //Executando HInit
            String mlfFile = mlfsPath + File.separator + mlfTrainFile;
            configFile = tempPath + File.separator + configFile;
            for (Word word : words) {
                String prototypeFile = prototypesPath + File.separator + word;
                String scpTrainSript = scriptPath + File.separator + scriptHeadFile + word + ".scp";
                String outFolderModel = hmmInit.getPath();
                System.out.println("HInit " + word);
                HmmCommands.executeHInit(htkPath, configFile, iterations, scpTrainSript, prototypeFile, mlfFile, outFolderModel);
            }

            //HRest()
            System.out.println(header.insert(startHeader, "Executando HRest"));
            header = new StringBuffer(head);
            File hmm0 = new File(dirHtkModels, "hmm0");
            ProjectUtil.createFolder(hmm0.getPath());

            //Executano HRest
            mlfFile = mlfsPath + File.separator + mlfTrainFile;
            for (Word word : words) {
                String modelFile = hmmInit + File.separator + word;
                String scpTrainSript = scriptPath + File.separator + scriptHeadFile + word + ".scp";
                String outFolderModel = hmm0.getPath();
                System.out.println("HRrest " + word);
                HmmCommands.executeHRest(htkPath, configFile, iterations, scpTrainSript, modelFile, mlfFile, outFolderModel);
            }

            //HParse()
            System.out.println(header.insert(startHeader, "Executando HParse"));
            header = new StringBuffer(head);
            System.out.println("Executando HParse ");
            HmmCommands.executeHParse(htkPath, tempPath + File.separator + grammarFile, tempPath + File.separator + "net.txt");

            //Criando modelo para reestimação embebida;
            String pathHMM[] = new String[words.size()];
            for (int i = 0; i < words.size(); i++) {
                pathHMM[i] = tempModelsPath + File.separator + "hmm0" + File.separator + words.get(i).getDescription();
            }
            File hmm1_0 = new File(dirHtkModels, "hmm1_0");
            ProjectUtil.createFolder(hmm1_0.getPath());
            HmmCommands.createHmmEmbedded(pathHMM, tempModelsPath + File.separator + "hmm1_0" + File.separator + "models.mod");

            vocabularyFile = tempPath + File.separator + vocabularyFile;

            //HERest           
            for (int mixture = 1; mixture <= numberGaussians; mixture++) {
                String hmmDest = "";
                //HHed
                //Executando HHED
                String model = tempModelsPath + File.separator + "hmm" + (mixture - 1) + "_3" + File.separator + "models.mod";
                String outFolderModel = tempModelsPath + File.separator + "hmm" + mixture + "_" + 0;
                String outNameModel = "models.mod";
                String hedScript = hedscriptPath + File.separator + "mix" + mixture + ".hed";
                if (mixture != 1) {
                    System.out.println(header.insert(startHeader, "Executando HHEd"));
                    header = new StringBuffer(head);
                    String hmmDest0 = "hmm" + (mixture) + "_" + 0;
                    File hmmPaste = new File(dirHtkModels, hmmDest0);
                    ProjectUtil.createFolder(hmmPaste.getPath());
                    System.out.println("Adicionando " + mixture + " misturas (HHEd) ");
                    HmmCommands.executeHHed(htkPath, model, hedScript, vocabularyFile, outFolderModel, outNameModel);
                }

                System.out.println(header.insert(startHeader, "Executando HERest"));
                header = new StringBuffer(head);

                for (int i = 0; i < numberHERest; i++) {
                    String pasteSource = File.separator + "hmm" + mixture + "_" + i;
                    String hmmSource = tempModelsPath + pasteSource + File.separator + "models.mod";
                    String pasteDest = File.separator + "hmm" + mixture + "_" + (i + 1);
                    hmmDest = tempModelsPath + pasteDest;
                    File hmmPaste = new File(dirHtkModels, pasteSource);
                    ProjectUtil.createFolder(hmmPaste.getPath());
                    hmmPaste = new File(dirHtkModels, pasteDest);
                    ProjectUtil.createFolder(hmmPaste.getPath());
                    System.out.println("Executando HERest " + i);
                    mlfFile = mlfsPath + File.separator + mlfTrainFile;
                    String scpTrainScript = scriptPath + File.separator + scriptTrainFile;
                    String modelFile = hmmSource;
                    outFolderModel = hmmDest;
                    String listModels = vocabularyFile;
                    HmmCommands.executeHERest(htkPath, configFile, modelFile, mlfFile, scpTrainScript, listModels, outFolderModel);
                }
            }
        } catch (Exception ex) {
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
                String listModels = tempPath + File.separator + vocabularyFile;

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
            System.out.println(bestResult);
            System.out.println(bestMatriz);
            String pathHmm = modelsPath + File.separator + "hmm" + bestMixture + "_3";
            ProjectUtil.copyFile(pathHmm + File.separator + "models.mod", tempPath + File.separator + "models.mod");
            ProjectUtil.copyFile(vocabularyFile, "modelsList");

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
                    percent = new Double(pct);
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

            // Carrega arquivo de modelo
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
            String vocabularyFile = projectPath + File.separator + "vocabulary.txt";

            // Seta nome do arquivo de saída
            String outHViteFile = tempPath + File.separator + "hvite.mlf";

            // Executa comando
            HmmCommands.executeHVite(htkPath, tempPath + File.separator + configFile, modelFile, scpTestScript, netFile, dictionaryFile, vocabularyFile, outHViteFile);

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
