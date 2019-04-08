package br.ufpe.cin.five.core.classification.hmm;

import br.ufpe.cin.five.core.sample.Sample;
import br.ufpe.cin.five.core.util.ShellCommandExecutor;
import br.ufpe.cin.five.core.util.ShellCommandExecutorException;
import br.ufpe.cin.five.core.utterance.Word;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * This class has all the commands for classification.<br/>
 * It contains the commands from HTK:<br/>
 * HInit, HRest, HParse, HHed, HERest, HVite, HResults and HCompv.<br/>
 * Also contains the commands:<br/>
 * CreatePrototype, CreateHmmEmbedded
 * @author daniel
 */
public class HmmCommands {

    //Boolean para debugar o sistem
    private static final boolean DEBUG = false;

    /**
     * Creates a hide markov model prototype specifying the topology, the name of the prototype, the parameterKind, the vector sizer, the number of states, the mixtures per states and the path destination.<br/>
     * This prototype will be written on a file with the path specifying by the parameter "outPathDest".
     * @param hmmTopology the topology of the prototype
     * @param name the name of the prototype
     * @param parameterkind the paramerter kind(ex. MFCC_0_D_A)
     * @param vectorsize the length of the features vector
     * @param numberstates the number of states of the hmm prototype
     * @param mixturesperstate the number of mixtures per states
     * @param outPathDest the destination path of the prototype
     * @throws IOException 
     * 
     * @see HmmTopology
     *
     */
    public static void createPrototype(HmmTopology hmmTopology, String name, String parameterkind, int vectorsize, int numberstates, int mixturesperstate, String outPathDest) throws IOException {
        double mixture_weight = (double) (1.0 / mixturesperstate);
        double initial_mean = 0.0;
        double initial_variance = 1.0;
        BufferedWriter hmmFile = new BufferedWriter(new FileWriter(outPathDest));
        /* output header */
        hmmFile.write("~o <VecSize> " + vectorsize + " <" + parameterkind + ">\n");
        hmmFile.write("~h \"" + name + "\"\n");
        hmmFile.write("<BeginHMM>\n");
        hmmFile.write(" <NumStates> " + numberstates + "\n");

        /* output each emitting state (ie. not the first or last) */
        for (int state = 2; state < numberstates; state++) {

            hmmFile.write(" <State> " + state + "\n");
            hmmFile.write("  <NumMixes>");
            hmmFile.write(" " + mixturesperstate);
            hmmFile.write("\n");

            for (int mixture = 1; mixture <= mixturesperstate; mixture++) {
                hmmFile.write("   <Mixture> " + mixture + " " + mixture_weight + "\n");
                /* output mean values for each mixture */
                hmmFile.write("    <Mean> " + vectorsize + "\n");
                hmmFile.write("    ");
                for (int feature = 1; feature <= vectorsize; feature++) {
                    hmmFile.write(" " + initial_mean);
                }
                hmmFile.write("\n");

                /* output diagonal covariance values for each mixture */
                hmmFile.write("    <Variance> " + vectorsize + "\n");
                hmmFile.write("    ");
                for (int featureT = 1; featureT <= vectorsize; featureT++) {
                    hmmFile.write(" " + initial_variance);
                }
                hmmFile.write("\n");
            }
        }
        /* transition matrix */
        switch (hmmTopology) {
            case SIMPLES:
                hmmFile.write(" <TransP> " + numberstates + "\n");
                for (int statefrom = 1; statefrom <= numberstates; statefrom++) {
                    hmmFile.write(" ");
                    for (int stateto = 1; stateto <= numberstates; stateto++) {
                        if (statefrom == 1 && stateto == 2) {
                            hmmFile.write(" " + 1.0);
                        } else if (statefrom > 1 && statefrom < numberstates && (stateto == statefrom || stateto == (statefrom + 1))) {
                            hmmFile.write(" " + 0.5);
                        } else {
                            hmmFile.write(" " + 0.0);
                        }
                    }
                    hmmFile.write("\n");
                }
                break;
            case LEFT_RIGHT:
                hmmFile.write(" <TransP> " + numberstates + "\n");
                for (int statefrom = 1; statefrom <= numberstates; statefrom++) {
                    hmmFile.write(" ");
                    for (int stateto = 1; stateto <= numberstates; stateto++) {
                        if (statefrom == 1) {
                            if (stateto == 2 | stateto == 3) {
                                hmmFile.write(" " + 0.5);
                            } else {
                                hmmFile.write(" " + 0.0);
                            }
                        } else if (statefrom > 1 && statefrom < numberstates - 2) {
                            if (statefrom == stateto) {
                                hmmFile.write(" " + 0.4);
                            } else if (statefrom + 1 == stateto) {
                                hmmFile.write(" " + 0.3);
                            } else if (statefrom + 2 == stateto) {
                                hmmFile.write(" " + 0.3);
                            } else {
                                hmmFile.write(" " + 0.0);
                            }
                        } else if (statefrom == numberstates - 2 || statefrom == numberstates - 1) {
                            if (statefrom == stateto | statefrom + 1 == stateto) {
                                hmmFile.write(" " + 0.5);
                            } else {
                                hmmFile.write(" " + 0.0);
                            }
                        } else {
                            hmmFile.write(" " + 0.0);
                        }
                    }
                    hmmFile.write("\n");
                }
                break;
        }
        /* output footer */
        hmmFile.write("<EndHMM>\n");
        hmmFile.flush();
        hmmFile.close();

    }

    /**
     * Executes HInit.
     * It initializes the model for the classification using the prototype and the train set
     * @param htkBin the folder path to HTK binaries     
     * @param configFile the config path file
     * @param iteration the number of iterations of HInit
     * @param scpTrainScript the script containing the train set
     * @param prototypeFile the prototype path file
     * @param mlfFile the path mlfFile
     * @param outFolderModel the output folder
     * @throws InterruptedException
     * @throws IOException
     * @throws ShellCommandExecutorException
     * @see ExtractionTechniques
     */
    public static void executeHInit(String htkBin, String configFile, int iteration, String scpTrainScript, String prototypeFile, String mlfFile, String outFolderModel) throws InterruptedException, IOException, ShellCommandExecutorException {
        String iterationParameter = "-i " + iteration;
        String scpTrainScriptParameter = "-S " + scpTrainScript;
        String outFolderModelParameter = " -M " + outFolderModel;
        String mlfFileParameter = "-I " + mlfFile;
        String prototypeFileParameter = prototypeFile;
        String configParameter = "-C " + configFile;
        String command = htkBin + File.separator + "HInit -T 01 " + iterationParameter + " " + configParameter + " " + scpTrainScriptParameter + " " + outFolderModelParameter + " " + mlfFileParameter + " " + prototypeFileParameter;
        if (DEBUG) {
            debugCommand(command);
        }
        ShellCommandExecutor.execute(command, htkBin);
    }

    /**
     * Executes HRest.
     * It performs basic Baum-Welch re-estimation.
     * @param htkBin the folder path to HTK binaries     
     * @param configFile the config path file
     * @param iteration the number of iterations of HInit
     * @param scpTrainScript the script containing the train set
     * @param modelFile the model path file
     * @param mlfFile the path mlfFile
     * @param outFolderModel the output folder
     * @throws InterruptedException
     * @throws IOException
     * @throws ShellCommandExecutorException
     */
    public static void executeHRest(String htkBin, String configFile, int iteration, String scpTrainScript, String modelFile, String mlfFile, String outFolderModel) throws InterruptedException, IOException, ShellCommandExecutorException {
        String iterationParameter = "-i " + iteration;
        String scpTrainScriptParameter = "-S " + scpTrainScript;
        String mlfFileParameter = "-I " + mlfFile;
        String OutFolderModelParameter = "-M " + outFolderModel;
        String modelFileParameter = modelFile;
        String configParameter = "-C " + configFile;
        String command = htkBin + File.separator + "HRest -T 01 " + iterationParameter + " " + configParameter + " " + scpTrainScriptParameter + " " + OutFolderModelParameter + " " + mlfFileParameter + " " + modelFileParameter;
        if (DEBUG) {
            debugCommand(command);
        }
        ShellCommandExecutor.execute(command, htkBin);
    }

    /**
     * Executes HParse.
     * It generated a networking using a grmmar
     * @param htkBin the folder path of HTK binaries
     * @param grammar the grammar path file
     * @param outNet the output net file path
     * @throws IOException
     * @throws InterruptedException     
     * @throws ShellCommandExecutorException
     */
    public static void executeHParse(String htkBin, String grammar, String outNet) throws IOException, InterruptedException, ShellCommandExecutorException {
        String command = htkBin + File.separator + "HParse " + grammar + " " + outNet;
        if (DEBUG) {
            debugCommand(command);
        }
        ShellCommandExecutor.execute(command, htkBin);
    }

    /**
     * Creates a unic file with all the hmm models inside.
     * @param modelFiles the model files paths
     * @param outEmbeddedModel the output path of the embedded model
     * @throws IOException
     */
    public static void createHmmEmbedded(String modelFiles[], String outEmbeddedModel) throws IOException {
        BufferedWriter modelsFile = new BufferedWriter(new FileWriter(outEmbeddedModel));
        for (String name : modelFiles) {
            FileReader arquivo = new FileReader(name);
            BufferedReader in = new BufferedReader(arquivo);
            while (in.ready()) {
                String linha = in.readLine();
                modelsFile.write(linha + "\n");
            }
            modelsFile.flush();
            in.close();
        }
        modelsFile.close();
    }

    /**
     * Executes HHed.
     * @param htkBin the folder path of HTK binaries
     * @param models the models file path
     * @param hedScript the hed script
     * @param listModels the list of models
     * @param outFolderModel the output folder
     * @param outNameModel the name of the output model          
     * @throws InterruptedException
     * @throws IOException
     * @throws ShellCommandExecutorException
     */
    public static void executeHHed(String htkBin, String[] models, String hedScript, String listModels, String outFolderModel, String outNameModel) throws InterruptedException, IOException, ShellCommandExecutorException {
        String modelsParameter = "";
        for (String model : models) {
            modelsParameter += "-H " + model + " ";
        }
        String hedScriptParameter = hedScript;
        String listModelsParameter = listModels;
        String outFolderModelParameter = "-M " + outFolderModel;
        String outNameModelParameter = "-w " + outNameModel;
        String command = htkBin + File.separator + "HHEd -T 01 " + modelsParameter + " " + outFolderModelParameter + " " + outNameModelParameter + " " + hedScriptParameter + " " + listModelsParameter;
        if (DEBUG) {
            debugCommand(command);
        }
        ShellCommandExecutor.execute(command, htkBin);
    }

    /**
     * Executes HHed.
     * @param htkBin the folder path of HTK binaries
     * @param model  the models file path
     * @param hedScript the hed script
     * @param listModels the list of models
     * @param outFolderModel the output folder
     * @param outNameModel the name of the output model
     * @throws IOException
     * @throws InterruptedException
     * @throws ShellCommandExecutorException
     */
    public static void executeHHed(String htkBin, String model, String hedScript, String listModels, String outFolderModel, String outNameModel) throws InterruptedException, IOException, ShellCommandExecutorException {
        String[] models = {model};
        executeHHed(htkBin, models, hedScript, listModels, outFolderModel, outNameModel);
    }

    /**
     * Executes HERest.
     * It performs embedded training version of the Baum-Welch algorithm.
     * @param htkBin the folder path of HTK binaries
     * @param configFile the config file to this command
     * @param modelFile the input model to re-estimate
     * @param mlfFile the script contains the trasncriptos of the samples
     * @param scpTrainScript the script file contains the train set
     * @param listModel the list models to re-estimate
     * @param outFolderModel the output folder
     * @throws InterruptedException
     * @throws IOException
     * @throws ShellCommandExecutorException
     */
    public static void executeHERest(String htkBin, String configFile, String modelFile, String mlfFile, String scpTrainScript, String listModel, String outFolderModel) throws InterruptedException, IOException, ShellCommandExecutorException {
        String[] models = {modelFile};
        executeHERest(htkBin, configFile, models, mlfFile, scpTrainScript, listModel, outFolderModel);
    }

    /**
     * Executes HERest.
     * It performs embedded training version of the Baum-Welch algorithm.
     * @param htkBin the folder path of HTK binaries
     * @param configFile the config file to this command
     * @param models  the input models to re-estimate
     * @param mlfFile the script contains the trasncriptos of the samples
     * @param scpTrainScript the script file contains the train set
     * @param listModel the list models to re-estimate
     * @param outFolderModel the output folder
     * @throws InterruptedException
     * @throws IOException
     * @throws ShellCommandExecutorException
     */
    public static void executeHERest(String htkBin, String configFile, String models[], String mlfFile, String scpTrainScript, String listModel, String outFolderModel) throws InterruptedException, IOException, ShellCommandExecutorException {
        String modelsParameter = "";
        for (String model : models) {
            modelsParameter += "-H " + model + " ";
        }
        String configFileParameter = "-C " + configFile;
        String mlfFileParameter = "-I " + mlfFile;
        String scpTrainScriptParameter = "-S " + scpTrainScript;
        String outPahtModelParameter = "-M " + outFolderModel;
        String prunningParameter = "-t 250.0 150.0 1000.0";
        String command = htkBin + File.separator + "HERest -T 01 " + configFileParameter + " " + mlfFileParameter + " " + prunningParameter + " " + scpTrainScriptParameter + " " + modelsParameter + " " + outPahtModelParameter + " " + listModel;
        if (DEBUG) {
            debugCommand(command);
        }
        ShellCommandExecutor.execute(command + " > temp.txt", htkBin);
    }

    /**
     * Execute Hvite
     * HVite is a general-purpose Viterbi word recogniser.
     * @param htkBin the folder path of HTK binaries
     * @param configFile the config file path
     * @param modelFile the model file path
     * @param scpTestScript the script contains the transcriptions test samples
     * @param netFile the net file path
     * @param dictionaryFile the dictionary file path
     * @param listModels the file path contians the list of models
     * @param outHviteFile the output file contains the recognizer words     
     * @throws InterruptedException
     * @throws IOException
     * @throws ShellCommandExecutorException
     */
    public static void executeHVite(String htkBin, String configFile, String modelFile, String scpTestScript, String netFile, String dictionaryFile, String listModels, String outHviteFile) throws InterruptedException, IOException, ShellCommandExecutorException {

        String configFileParameter = "-C " + configFile;
        String modelFileParameter = "-H " + modelFile;
        String scpTestScriptParameter = "-S " + scpTestScript;
        String netFileParameter = "-w " + netFile;
        String outHviteFileParameter = "-i " + outHviteFile;
        String dictionaryFileParameter = dictionaryFile;
        String listModelsParameter = listModels;
        String command = htkBin + File.separator + "HVite -T 01 " + " " + configFileParameter + " " + modelFileParameter + " " + scpTestScriptParameter + " " + netFileParameter + " " + outHviteFileParameter + " " + dictionaryFileParameter + " " + listModelsParameter;
        if (DEBUG) {
            debugCommand(command);
        }
        ShellCommandExecutor.execute(command + " > temp.txt", htkBin);
    }

    /**
     * Execute HResults.
     * HResults is the HTK performance analysis tool.
     * @param htkBin the folder path of HTK binaries
     * @param mlfFile the transcription file of the test set
     * @param vocabularyFile the vocabulary
     * @param hviteFile the transcription file generated by the recognizer
     * @param matrix specifies if output the confusion matrix
     * @param resultFile the output file
     * @throws InterruptedException
     * @throws IOException
     * @throws ShellCommandExecutorException
     */
    public static void executeHResults(String htkBin, String mlfFile, String vocabularyFile, String hviteFile, boolean matrix, String resultFile) throws InterruptedException, IOException, ShellCommandExecutorException {
        String matrixParameter = "";
        if (matrix) {
            matrixParameter = "-p";
        }
        String mlfFileParameter = "-I " + mlfFile;
        String vocabularyFileParameter = vocabularyFile;
        String outHviteFileParameter = hviteFile;

        String command = htkBin + File.separator + "HResults -T 07 -t " + matrixParameter + " " + mlfFileParameter + " " + vocabularyFileParameter + " " + outHviteFileParameter;

        if (DEBUG) {
            debugCommand(command);
        }
        ShellCommandExecutor.execute(command + " > " + resultFile, htkBin);
    }

    /**
     * Execute HCompv.
     * Calculate the global mean and covariance of a set of training data
     * @param htkBin the folder path of HTK binaries
     * @param configFile the config file
     * @param scriptFileALL the script file that contains all the train set
     * @param prototype the hmm prototype
     * @param outFolderModel the output folder of the model
     * @throws InterruptedException
     * @throws IOException
     * @throws ShellCommandExecutorException
     */
    public static void executeHCompv(String htkBin, String configFile, String scriptFileALL, String prototype, String outFolderModel) throws InterruptedException, IOException, ShellCommandExecutorException {
        String configFileParameter = "-C " + configFile;
        String scriptFileAllParameter = "-S " + scriptFileALL;
        String prototypeParameter = prototype;
        String outFolderModelParameter = "-M " + outFolderModel;
        String otherParameter = "-f 0.01 -m";
        String command = htkBin + File.separator + "HCompV " + " " + configFileParameter + " " + otherParameter + "  " + scriptFileAllParameter + " " + outFolderModelParameter + " " + prototypeParameter;
        if (DEBUG) {
            debugCommand(command);
        }
        ShellCommandExecutor.execute(command, htkBin);
    }

    /**
     * Creates the embedded monophones model.
     * @param monophonesList the list of monophones.
     * @param prototype the prototype
     * @param vFloors the vFloors
     * @param typeMFCC the mfcc type
     * @param outFolderEmbeddedHmm the output file
     * @throws IOException
     */
    public static void createHmmEmbeddedMonophones(String monophonesList, String prototype, String vFloors, String typeMFCC, String outFolderEmbeddedHmm) throws IOException {
        BufferedWriter hmmdefs = new BufferedWriter(new FileWriter(outFolderEmbeddedHmm + File.separator + "models.mod"));
        BufferedReader monophones = new BufferedReader(new FileReader(monophonesList));
        BufferedReader vfloors = new BufferedReader(new FileReader(vFloors));
        hmmdefs.write("~o \n <VecSize> 39 \n <" + typeMFCC + ">" + '\n');
        while (vfloors.ready()) {
            //System.out.println(vfloors.readLine());
            hmmdefs.write(vfloors.readLine() + '\n');
        }
        while (monophones.ready()) {
            String monophone = monophones.readLine();
            BufferedReader proto = new BufferedReader(new FileReader(prototype));
            while (proto.ready()) {
                String line = proto.readLine();
                if (line.contains("~h")) {
                    hmmdefs.write("~h " + '\"' + monophone + '\"' + '\n');
                } else if (line.contains("~o")) {
                    proto.readLine();
                    proto.readLine();
                } else {
                    hmmdefs.write(line + '\n');
                }
            }
            proto.close();
        }
        hmmdefs.close();
        monophones.close();
    }

    /**
     * Creates the vocabulary(list of the words)
     * @param words the list of the word
     * @param vocabularyFile the path o vocabularyFile
     * @throws IOException
     */
    public static void createsVocabulary(List<Word> words, String vocabularyFile) throws IOException {
        BufferedWriter voc = new BufferedWriter(new FileWriter(vocabularyFile));
        for (int countWord = 0; countWord < words.size(); countWord++) {
            String word = words.get(countWord).getDescription();
            voc.write(word + '\n');
        }
        voc.close();
    }

    /**
     * Createas a word based dictionary
     * @param words the list of the words
     * @param dictionaryFile the path of dictionary file
     * @throws IOException
     */
    public static void createsWordDictionary(List<Word> words, String dictionaryFile) throws IOException {
        BufferedWriter dic = new BufferedWriter(new FileWriter(dictionaryFile));
        for (int countWord = 0; countWord < words.size(); countWord++) {
            String word = words.get(countWord).getDescription();
            dic.write(word + '\t' + "[" + word + "]" + '\t' + word + '\n');
        }
        dic.close();
    }

    /**
     * Creates a monophones based dictionary
     * @param words the list of the words
     * @param dictionaryFile the path of dictionary file
     * @throws IOException
     */
    public static void createsMonophonesDictionary(List<Word> words, String dictionaryFile) throws IOException {
        BufferedWriter dic = new BufferedWriter(new FileWriter(dictionaryFile));
        for (int countWord = 0; countWord < words.size(); countWord++) {
            String word = words.get(countWord).getDescription();
            dic.write(word + '\t');
            for (String phones : words.get(countWord).getPhoneticRepresentations()) {
                dic.write(phones);
            }
            dic.write('\n');
        }
        dic.write("SILENCE" + '\t' + "[]" + '\t' + "sil");
        dic.write('\n');
        dic.close();
    }

    /**
     * Creates the list of monophones that correspond to the list of the words
     * @param words the list of the words
     * @param monophonesFile the path of monophones file
     * @return the list of monophones
     * @throws IOException
     */
    public static void createsMonophonesList(List<Word> words, String monophonesFile) throws IOException {
        BufferedWriter pho = new BufferedWriter(new FileWriter(monophonesFile));
        Set<String> setMonophones = new TreeSet<String>();
        for (int countWord = 0; countWord < words.size(); countWord++) {
            Word word = words.get(countWord);
            for (String monophone : word.getPhoneticRepresentations()) {
                String[] m = monophone.split(" ");
                for (String mo : m) {
                    if (setMonophones.add(mo)) {
                        pho.write(mo + '\n');
                    }
                }
            }
        }
        pho.write("sil");
        pho.close();
    }

    /**
     * Creates a default grammar
     * @param words the list of the words
     * @param grammarFile the path of grammar file
     * @param grammarCommand the grammar command
     * @throws IOException
     */
    public static void createsDefaultGrammar(List<Word> words, String grammarFile, String grammarCommand) throws IOException {
        BufferedWriter gram = new BufferedWriter(new FileWriter(grammarFile));
        gram.write("$COMAND = ");
        for (int countWord = 0; countWord < words.size(); countWord++) {
            String word = words.get(countWord).getDescription();
            if (countWord + 1 < words.size()) {
                gram.write(word + " | ");
            } else {
                gram.write(word + " ;" + '\n');
            }
        }
        gram.write(grammarCommand);
        gram.close();
    }

    /**
     * Creates the mlf file for train
     * @param hmmType the type of hmm( word or phonetic)
     * @param trainingSamples the set of sample
     * @param wordsPerUtterance the map between words and utterance
     * @param mlfTrainFile the path of mlf train file
     * @throws IOException
     */
    public static void createsMlfTrain(HmmUnitSize unitSize, List<Sample> trainingSamples, Map<Integer, List<Word>> wordsPerUtterance, String mlfTrainFile) throws IOException {

        BufferedWriter mlfs = new BufferedWriter(new FileWriter(mlfTrainFile));
        mlfs.write("#!MLF!#" + '\n');
        for (Sample sample : trainingSamples) {
            //String features = "features" + File.separator;
            String featureFile = sample.getFeatureFile();
            featureFile = featureFile.substring(0, featureFile.length() - 3) + "lab";
            mlfs.write('\"' + "*/" + featureFile + '\"' + '\n');
            List<Word> wrds = wordsPerUtterance.get(sample.getUtterance().getId());            
            switch (unitSize) {
                case WORD:
                    for (Word word : wrds) {
                        mlfs.write(word.getDescription() + '\n');
                    }
                    mlfs.write("." + '\n');
                    break;
                case PHONEMES:
                    mlfs.write("sil" + '\n');
                    for (int i = 0; i < wrds.size(); i++) {
                        Word word = wrds.get(i);
                        for (String phone : word.getPhoneticRepresentations()) {
                            for (String monophone : phone.split(" ")) {
                                mlfs.write(monophone + '\n');
                            }
                        }
                        if (i + 1 == wrds.size()) {
                            mlfs.write("sil" + '\n');
                            mlfs.write("." + '\n');
                        }
                    }
                    break;
            }
        }
        mlfs.close();
    }

    /**
     * Creates the mlf file for test
     * @param testingSamples the testing samples
     * @param wordsPerUtterance the map between word and utterance
     * @param mlfTestFile the path of mlf file
     * @throws IOException
     */
    public static void createsMlfTest(List<Sample> testingSamples, Map<Integer, List<Word>> wordsPerUtterance, String mlfTestFile) throws IOException {
        BufferedWriter mlfs = new BufferedWriter(new FileWriter(mlfTestFile));
        mlfs.write("#!MLF!#" + '\n');
        for (Sample sample : testingSamples) {
            String featureFile = sample.getFeatureFile();
            featureFile = featureFile.substring(0, featureFile.length() - 3) + "lab";            
            mlfs.write('\"' + "*/" + featureFile + '\"' + '\n');
            List<Word> wrds = wordsPerUtterance.get(sample.getUtterance().getId());
            for (Word word : wrds) {
                mlfs.write(word.getDescription() + '\n');
            }
            mlfs.write("." + '\n'); 
        }
        mlfs.close();
    }

    /**
     * Creates the script file
     * @param features the set of features path
     * @param scriptFile the path of script file
     * @throws IOException
     */
    public static void createsScriptFile(ArrayList<String> features, String scriptFile) throws IOException {
        BufferedWriter scriptFileAll = new BufferedWriter(new FileWriter(scriptFile));
        for (String feature : features) {
            scriptFileAll.write(feature + '\n');
        }
        scriptFileAll.close();
    }

    /**
     * Creates the hedsrcipt that will be use by HHed
     * @param modelName the name of the model
     * @param mixtures the number of mixtures in the model
     * @param numberStates the numbero of states
     * @param hedFile the path of hed file
     * @throws IOException
     */
    public static void createsHedFile(String modelName, int mixtures, int numberStates, String hedFile) throws IOException {
        BufferedWriter hedscript = new BufferedWriter(new FileWriter(hedFile));
        String comando = "MU " + mixtures + " {(\"" + modelName + "\").state[2-" + (numberStates + 1) + "].mix}";
        hedscript.write(comando + '\n');
        hedscript.close();
    }

    /**
     * Creates the hed script for sil
     * @param hedSilFile the path of hed sricpt file
     * @throws IOException
     */
    public static void createsHedSilFile(String hedSilFile) throws IOException {
        BufferedWriter hedscript = new BufferedWriter(new FileWriter(hedSilFile));
        hedscript.write("AT 2 4 0.2 {sil.transP} \n AT 4 2 0.2 {sil.transP}");
        hedscript.close();
    }

    /** 
     * Executes Hvite for test one
     * @param htkBin the path of HTK binaries
     * @param scpFile the script file that contains the path of the features
     * @param modelFile the model file
     * @param mfcFile the path of mfc file tha contains the features
     * @param netFile the path of net file
     * @param vocabularyFile the path of vocabulary file
     * @param dictionaryFile the path of dictionary file
     * @param config the path of config file
     * @return the result
     * @throws IOException
     * @throws InterruptedException
     * @throws ShellCommandExecutorException
     * @see Result
     */
    public static String testHmm(String htkBin, String scpFile, String modelFile, String mfcFile, String netFile, String vocabularyFile, String dictionaryFile, String config) throws IOException, InterruptedException, ShellCommandExecutorException {

        String result = new String();
        File mfc = new File(scpFile + File.separator + "_scpTemp.scp");
        BufferedWriter mfcScp = new BufferedWriter(new FileWriter(mfc));
        mfcScp.write(mfcFile);
        mfcScp.close();
        String comando;
        if (config == null) {
            comando = htkBin + File.separator + "HVite -T 01 -H " + modelFile + " -S " + mfc.getPath() + " -w " + netFile + " -i " + scpFile + File.separator + "_HVite.mlf " + dictionaryFile + " " + vocabularyFile;
        } else {
            comando = htkBin + File.separator + "HVite -T 01 -H " + modelFile + " -S " + mfc.getPath() + " -w " + netFile + " -i " + scpFile + File.separator + "_HVite.mlf " + dictionaryFile + " " + vocabularyFile + " -C " + config;
        }
        ShellCommandExecutor.execute(comando, htkBin);
        BufferedReader readResult = new BufferedReader(new FileReader(scpFile + File.separator + "_HVite.mlf"));
        readResult.readLine();
        readResult.readLine();
        String line = readResult.readLine();
        String utterance = "";
        while (!line.equals(".")) {
            String[] results = line.split(" ");
            System.out.println("Locução: " + results[2]);
            utterance += results[2] + " ";
            line = readResult.readLine();
            //result.setConfidence(new Double(results[3]));
        }
        result = utterance;
        readResult.close();
        return result;
    }

    private static void debugCommand(String command) {
        BufferedWriter debug = null;
        try {
            debug = new BufferedWriter(new FileWriter("logs" + File.separator + "HmmCommands.log", true));
            debug.write(command + '\n');
            debug.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                debug.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
