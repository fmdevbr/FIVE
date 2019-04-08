package br.ufpe.cin.five.core.extraction.mgcc;

import br.ufpe.cin.five.core.project.ProjectType;
import br.ufpe.cin.five.core.util.ShellCommandExecutor;
import br.ufpe.cin.five.core.utterance.Phrase;
import br.ufpe.cin.five.core.utterance.UtteranceProcess;
import br.ufpe.cin.five.core.utterance.UtteranceUtil;
import br.ufpe.cin.five.core.utterance.nlp.GraphemeToPhoneme;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;

/**
 *
 * @author Carlos
 */
public class ForcedAlignment extends Thread {

    // PROJETC TYPE
    private ProjectType projectType;
    // LISTS
    private List<String> lstAudioFile;
    private List<String> lstAudioText;
    // DIRS
    private File VOXFORGEDIR;
    private File TEMPDIR;
    private String HTKDIR;
    private File MFCCDIR;
    // AUXILIAR
    private BufferedWriter BW01;
    private BufferedReader BR01;
    private String COMMAND;
    private String APPEND01;
    private String READLINE;
    // HMM FOLDERS
    private File HMM00, HMM01, HMM02, HMM03, HMM04,
                HMM05, HMM06, HMM07, HMM08, HMM09,
                HMM10, HMM11, HMM12, HMM13, HMM14,
                HMM15;
    // FILES
    private String _prompts;
    private String _wlist;
    private String _globalDed;
    private String _lexicon;
    private String _monophones1;
    private String _dict;
    private String _dlog;
    private String _monophones0;
    private String _wordsMlf;
    private String _mkphones0Led;
    private String _phones0Mlf;
    private String _mkphones1Led;
    private String _phones1Mlf;
    private String _codeTrainScp; // mfcc (to create)
    private String _trainScp;
    private String _audioConfig; // mfcc config
    private String _config; // prototype config
    private String _configFile;// config Acento
    private String _proto;
    private String _silHed;
    private String _alignedMlf;
    private String _hviteLog;
    private String _mktriLed;
    private String _triphones1;
    private String _wintriMlf;
    private String _mktriHed;
    private String _fulllist;
    private String _dicttri;
    private String _treeHed;
    private String _tiedlist;
    private String _alignConfig;
    private String _alignTrainScp;
    // ALIGNED FILE
    private String _alignedOut;
    // ALIGNMENT LOG
    private String _alignmentLog;
    // TREE
    private File _treeOriginalFile;
    // LISTS
    private List<String> lstWordsMlf;
    private List<String> lstWordList;
    private ArrayList<String> allWordList;
    private List<String> lstHmmDefsHMM00;
    private List<String> lstMacrosHMM00;
    private List<String> lstMacrosHMM04;
    private List<String> lstHmmDefsHMM04;
    private List<String> lstMonophones0;
    private List<String> lstPauHmmdefs;
    private List<String> lstProtoPartToHmmdefs;
    private List<String> lstProtoPartToMacros;
    
    private String SIL = "sil";
    private String PAU = "pau";

    public ForcedAlignment(ProjectType projectType, String resourcesPath, String tempPath, String mfccPath, List<String> aLstAudioFile, List<String> aLstAudioText) {
        
        this.projectType = projectType;
        this.lstAudioFile = aLstAudioFile;
        this.lstAudioText = aLstAudioText;
        
        TEMPDIR = new File(tempPath);
        MFCCDIR = new File(mfccPath);
        VOXFORGEDIR = new File(tempPath + File.separator + "voxforge");
        HTKDIR = resourcesPath + File.separator + "htk-3.3" + File.separator;
        
        _treeOriginalFile = new File(resourcesPath + File.separator + "scripts"+File.separator+"questions_qst001.hed");
        
        initFileVars();
    }

    private void initFileVars() {
        _prompts = VOXFORGEDIR + File.separator + "prompts";
        _wlist = VOXFORGEDIR + File.separator + "wordList";
        _globalDed = VOXFORGEDIR + File.separator + "global.ded";
        _lexicon = VOXFORGEDIR + File.separator + "lexiconDictionary";
        _monophones1 = VOXFORGEDIR + File.separator + "monophonesWithPause";
        _dict = VOXFORGEDIR + File.separator + "dict";
        _dlog = VOXFORGEDIR + File.separator + "hdmanLog";
        _monophones0 = VOXFORGEDIR + File.separator + "monophonesWithoutPause";
        _wordsMlf = VOXFORGEDIR + File.separator + "words.mlf";
        _mkphones0Led = VOXFORGEDIR + File.separator + "mkphones0.led";
        _phones0Mlf = VOXFORGEDIR + File.separator + "phonesWithoutPause";
        _mkphones1Led = VOXFORGEDIR + File.separator + "mkphones1.led";
        _phones1Mlf = VOXFORGEDIR + File.separator + "phonesWithPause";
        _codeTrainScp = VOXFORGEDIR + File.separator + "trainAudioMfccScp"; // mfcc (to create)
        _trainScp = VOXFORGEDIR + File.separator + "trainMfccScp";
        _audioConfig = VOXFORGEDIR + File.separator + "mfccConfig"; // mfcc config
        _config = VOXFORGEDIR + File.separator + "prototypeConfig"; // prototype config
        _configFile = VOXFORGEDIR + File.separator + "config"; // prototype config
        _proto = VOXFORGEDIR + File.separator + "proto";
        _silHed = VOXFORGEDIR + File.separator + "sil.hed";
        _alignedMlf = VOXFORGEDIR + File.separator + "aligned.mlf";
        _hviteLog = VOXFORGEDIR + File.separator + "hviteLog";
        _mktriLed = VOXFORGEDIR + File.separator + "mktri.led";
        _triphones1 = VOXFORGEDIR + File.separator + "triphonesWithPause";
        _wintriMlf = VOXFORGEDIR + File.separator + "wintri.mlf";
        _mktriHed = VOXFORGEDIR + File.separator + "mktri.hed";
        _fulllist = VOXFORGEDIR + File.separator + "fulllist";
        _dicttri = VOXFORGEDIR + File.separator + "dict-tri";
        _treeHed = VOXFORGEDIR + File.separator + "tree.hed";
        _tiedlist = VOXFORGEDIR + File.separator +  "tiedlist";
        _alignConfig = VOXFORGEDIR + File.separator + "forcedAlignmentConfig";
        _alignTrainScp = VOXFORGEDIR + File.separator + "forcedAlignmentTrainScp";
        // ALIGNED FILE
        _alignedOut = VOXFORGEDIR + File.separator + "aligned.out";
        // ALIGNMENT LOG
        _alignmentLog = VOXFORGEDIR + File.separator + "alignmentLog";
    }

    private void initializingLists() {
        lstWordsMlf = new LinkedList<String>();
        lstWordList = new LinkedList<String>();
        lstHmmDefsHMM00 = new LinkedList<String>();
        lstMacrosHMM00 = new LinkedList<String>();
        lstMacrosHMM04 = new LinkedList<String>();
        lstHmmDefsHMM04 = new LinkedList<String>();
        lstMonophones0 = new LinkedList<String>();
        lstPauHmmdefs = new LinkedList<String>();
        lstProtoPartToHmmdefs = new LinkedList<String>();
        lstProtoPartToMacros = new LinkedList<String>();
    }

    @Override
    public void run() {
        try {
            // creating directories
            if (!createDirectories()) {
                throw new Exception();
            }

            initializingLists();

            String _protoHMM00 = HMM00 + File.separator + "proto";
            String _vfloorsHMM00 = HMM00 + File.separator + "vFloors";

            String _hmmdefsHMM00 = HMM00 + File.separator + "hmmdefs";
            String _macrosHMM00 = HMM00 + File.separator + "macros";
            String _hmmdefsHMM01 = HMM01 + File.separator + "hmmdefs";
            String _macrosHMM01 = HMM01 + File.separator + "macros";
            String _hmmdefsHMM02 = HMM02 + File.separator + "hmmdefs";
            String _macrosHMM02 = HMM02 + File.separator + "macros";
            String _hmmdefsHMM03 = HMM03 + File.separator + "hmmdefs";
            String _macrosHMM03 = HMM03 + File.separator + "macros";
            String _hmmdefsHMM04 = HMM04 + File.separator + "hmmdefs";
            String _macrosHMM04 = HMM04 + File.separator + "macros";
            String _hmmdefsHMM05 = HMM05 + File.separator + "hmmdefs";
            String _macrosHMM05 = HMM05 + File.separator + "macros";
            String _hmmdefsHMM06 = HMM06 + File.separator + "hmmdefs";
            String _macrosHMM06 = HMM06 + File.separator + "macros";
            String _hmmdefsHMM07 = HMM07 + File.separator + "hmmdefs";
            String _macrosHMM07 = HMM07 + File.separator + "macros";
            String _hmmdefsHMM08 = HMM08 + File.separator + "hmmdefs";
            String _macrosHMM08 = HMM08 + File.separator + "macros";
            String _hmmdefsHMM09 = HMM09 + File.separator + "hmmdefs";
            String _macrosHMM09 = HMM09 + File.separator + "macros";
            String _hmmdefsHMM10 = HMM10 + File.separator + "hmmdefs";
            String _macrosHMM10 = HMM10 + File.separator + "macros";
            String _hmmdefsHMM11 = HMM11 + File.separator + "hmmdefs";
            String _macrosHMM11 = HMM11 + File.separator + "macros";
            String _hmmdefsHMM12 = HMM12 + File.separator + "hmmdefs";
            String _macrosHMM12 = HMM12 + File.separator + "macros";
            String _hmmdefsHMM13 = HMM13 + File.separator + "hmmdefs";
            String _macrosHMM13 = HMM13 + File.separator + "macros";
            String _hmmdefsHMM14 = HMM14 + File.separator + "hmmdefs";
            String _macrosHMM14 = HMM14 + File.separator + "macros";
            String _hmmdefsHMM15 = HMM15 + File.separator + "hmmdefs";
            String _macrosHMM15 = HMM15 + File.separator + "macros";

            String asterisk = System.getProperty("os.name").contains("Linux") ? "'*'" : "\"*\"";

            /*
             * CREATING AUXILIARY FILES
             */
      if (!createAuxiliaryFiles()) {
          throw new Exception();
      }
//
      /*
       * CREATING PRONUNCIATION DICTIONARY
       */
//
      /* creating file:
       * _prompts, wordList and words.mlf
       * collecting samples' utterances
       *
       * creating scp list files
       * codetrain.scp and train.scp
       */
     if (!createPronunciationDictionary()) {
          throw new Exception();
      }

      /* creating Lexicon Dictionary from WordList
       * _lexicon from _wlist
       */
      if (!createLexiconDictionary(projectType == ProjectType.TTS ? lstWordList : allWordList)) {
          throw new Exception();
      }
      
      /* Creating pronunciation dictionary
       * Create files:
       * 1) _dict - the pronunciation dictionnary for you Grammar and additional words required to create a phonetically balanced Acoustic Model;
       * 2) _monophones1, which is simply a list of the phones used in dict.
       * 3) _dlog = operation log file
       */                       
     COMMAND = HTKDIR + "HDMan -A -D -T 1 -m -w " + _wlist + " -C " + _configFile + " -n " + _monophones1 + " -i -l " + _dlog + " " + _dict + " " + _lexicon + " >> " + _alignmentLog;
      if (!ShellCommandExecutor.execute(COMMAND, VOXFORGEDIR)) {
          throw new Exception();
      }
//
      /* create file:
       * monophones0
       * that is monophones1 without "pau"
       */
      BR01 = new BufferedReader(new FileReader(_monophones1));            
      BW01 = new BufferedWriter(new FileWriterWithEncoding(_monophones0, "UTF-8"));
      APPEND01 = null;
      while (BR01.ready()) {
          if (!(READLINE = BR01.readLine()).equals(PAU)) {
              BW01.write((APPEND01 = APPEND01 == null ? "" : "\n").concat(READLINE));
          }
      }
      BR01.close();
      BW01.close();
//
//
      /*
       * CREATING TRANSCRIPTION FILES
       */
//
      /* create phone level transcriptions ( without "pau" )
       * _phones0Mlf
       */
      COMMAND = HTKDIR + "HLEd -A -D -T 1 -C " + _configFile + " -l " + asterisk + " -d " + _dict + " -i " + _phones0Mlf + " " + _mkphones0Led + " " + _wordsMlf + " >> " + _alignmentLog;
      if (!ShellCommandExecutor.execute(COMMAND, VOXFORGEDIR)) {
          throw new Exception();
      }
//
      /* create phone level transcriptions ( with "pau" )
       * _phones1Mlf
       */
      COMMAND = HTKDIR + "HLEd -A -D -T 1 -l " + asterisk + " -C " + _configFile + " -d " + _dict + " -i " + _phones1Mlf + " " + _mkphones1Led + " " + _wordsMlf + " >> " + _alignmentLog;
      if (!ShellCommandExecutor.execute(COMMAND, VOXFORGEDIR)) {
          throw new Exception();
      }
//
      if (projectType == ProjectType.TTS) {
          extractMfccForSynthesis();
      }
//
      System.out.println("CREATING TRANSCRIPTION FILES");
      
      /*
       * CREATING FLAT START MONOPHONES
       */
//
      /* creating prototype model configuration file
       * _protoHMM00, _vfloorsHMM00
       */
      
     System.out.println("CREATING FLAT START MONOPHONES");            
      
      System.out.println("reating prototype model configuration file * _protoHMM00, _vfloorsHMM00");            
      
      COMMAND = HTKDIR + "HCompV -A -D -T 1 -C " + _config + " -f 0.01 -m -S " + _trainScp + " -M " + HMM00 + " " + _proto + " >> " + _alignmentLog;
      if (!ShellCommandExecutor.execute(COMMAND, VOXFORGEDIR)) {
          throw new Exception();
      }
//
      /* preparing HMM00 hmmdefs and macros files from
       * _protoHMM00, _vfloorsHMM00
       */
      Boolean startCatch = Boolean.FALSE;
      BR01 = new BufferedReader(new FileReader(_protoHMM00));
      while (BR01.ready()) {
          READLINE = BR01.readLine().trim();
          if (startCatch == Boolean.TRUE || READLINE.equals("<BEGINHMM>")) {
              lstProtoPartToHmmdefs.add(READLINE);
              startCatch = Boolean.TRUE;
          } else if (startCatch == Boolean.FALSE && !READLINE.equals("~h \"proto\"")) {
              lstProtoPartToMacros.add(READLINE);
          }
      }
      BR01.close();
//
      /* creating HMM00 hmmdefs file
       * _hmmdefsHMM00
       */
     BR01 = new BufferedReader(new FileReader(_monophones0));
      while (BR01.ready()) {
          lstHmmDefsHMM00.add("~h \"" + BR01.readLine().trim() + "\"");
          lstHmmDefsHMM00.addAll(lstProtoPartToHmmdefs);
      }
      BR01.close();
      FileUtils.writeLines(new File(_hmmdefsHMM00), "UTF-8", lstHmmDefsHMM00);
//
      /* creating HMM00 hmmdefs file
       * _macrosHMM00
       */
      BR01 = new BufferedReader(new FileReader(_vfloorsHMM00));
      lstMacrosHMM00.addAll(lstProtoPartToMacros);
      while (BR01.ready()) {
          lstMacrosHMM00.add(BR01.readLine());
      }
      BR01.close();
      FileUtils.writeLines(new File(_macrosHMM00), "UTF-8", lstMacrosHMM00);
                              
      /*
       * RE-ESTIMATE MONOPHONES
       */
//
      /* creating hmmdefs and macros in
       * HMM01
       */
//
      System.out.println("RE-ESTIMATE MONOPHONES");
      
      System.out.println("creating hmmdefs and macros in * HMM01");
      
      COMMAND = HTKDIR + "HERest -A -D -T 1 -C " + _config + " -I " + _phones0Mlf + " -t 250.0 150.0 1000.0 -S " + _trainScp + " -H " + _macrosHMM00 + " -H " + _hmmdefsHMM00 + " -M " + HMM01 + " " + _monophones0 + " >> " + _alignmentLog;
      System.out.println(COMMAND);
      if (!ShellCommandExecutor.execute(COMMAND, VOXFORGEDIR)) {
          throw new Exception();
      }
//
      /* creating hmmdefs and macros in
       * HMM02
       */
//
      System.out.println("creating hmmdefs and macros in * HMM02");
//
      COMMAND = HTKDIR + "HERest -A -D -T 1 -C " + _config + " -I " + _phones0Mlf + " -t 250.0 150.0 1000.0 -S " + _trainScp + " -H " + _macrosHMM01 + " -H " + _hmmdefsHMM01 + " -M " + HMM02 + " " + _monophones0 + " >> " + _alignmentLog;
      if (!ShellCommandExecutor.execute(COMMAND, VOXFORGEDIR)) {
          throw new Exception();
      }
     
      /* creating hmmdefs and macros in
       * HMM03
       */
      
      System.out.println("creating hmmdefs and macros in * HMM03");
      
      COMMAND = HTKDIR + "HERest -A -D -T 1 -C " + _config + " -I " + _phones0Mlf + " -t 250.0 150.0 1000.0 -S " + _trainScp + " -H " + _macrosHMM02 + " -H " + _hmmdefsHMM02 + " -M " + HMM03 + " " + _monophones0 + " >> " + _alignmentLog;
      if (!ShellCommandExecutor.execute(COMMAND, VOXFORGEDIR)) {
          throw new Exception();
      }           
      
      /*
       * FIXING THE SILENCE MODELS
       */
//
      /* Preparing HMM04 hmmdefs and macro
       * _hmmdefsHMM04, _macrosHMM04
       */
      
     System.out.println("FIXING THE SILENCE MODELS");
      
      BR01 = new BufferedReader(new FileReader(_macrosHMM03));
      while (BR01.ready()) {
          lstMacrosHMM04.add(BR01.readLine());
      }
      BR01.close();
      FileUtils.writeLines(new File(_macrosHMM04), "UTF-8", lstMacrosHMM04);
//
      /* creating hmmdefs and macros in
       * HMM04
       */
     BR01 = new BufferedReader(new FileReader(_hmmdefsHMM03));
      startCatch = Boolean.FALSE;
      Boolean isSil = Boolean.FALSE;

      /*Nao esta formando o HMM04 */
      while (BR01.ready()) {
          
          String readLine = BR01.readLine();
//
          if (startCatch) {
              lstPauHmmdefs.add(readLine);
          }
//
          if (readLine.equals("~h \"" + SIL + "\"")) {
              isSil = Boolean.TRUE;
              startCatch = Boolean.TRUE;
              lstPauHmmdefs.add("~h \"" + PAU + "\"");
//
          } else if (isSil && readLine.equals("<BEGINHMM>")) {
              startCatch = Boolean.FALSE;
              
//
          } else if (isSil && readLine.equals("<NUMSTATES> 5")) {
              lstPauHmmdefs.add("<NUMSTATES> 3");
              startCatch = Boolean.FALSE;
//
          } else if (isSil && readLine.equals("<STATE> 2")) {
              startCatch = Boolean.FALSE;
//
          } else if (isSil && readLine.equals("<STATE> 3")) {
              startCatch = Boolean.TRUE;
              lstPauHmmdefs.add("<STATE> 2");
//
          } else if (isSil && startCatch && readLine.contains("<GCONST>")) {
              startCatch = Boolean.FALSE;
//
          } else if (isSil && readLine.equals("<STATE> 4")) {
              startCatch = Boolean.FALSE;
//
          } else if (isSil && readLine.equals("<TRANSP> 5")) {
              startCatch = Boolean.FALSE;
              lstPauHmmdefs.add("<TRANSP> 3");
              lstPauHmmdefs.add(" 0.0 1.0 0.0\n 0.0 0.9 0.1\n 0.0 0.0 0.0");
              lstPauHmmdefs.add("<ENDHMM>");
              
          }
          lstHmmDefsHMM04.add(readLine);
      }
      BR01.close();
      lstHmmDefsHMM04.addAll(lstPauHmmdefs);
      FileUtils.writeLines(new File(_hmmdefsHMM04), "UTF-8", lstHmmDefsHMM04);           
      
      /* creating hmmdefs and macros in
       * HMM05
       */
      
      System.out.println("creating hmmdefs and macros in * HMM05");
              
      COMMAND = HTKDIR + "HHEd -A -D -T 1 -C " + _configFile + " -H " + _macrosHMM04 + " -H " + _hmmdefsHMM04 + " -M " + HMM05 + " " + _silHed + " " + _monophones1 + " >> " + _alignmentLog;
      if (!ShellCommandExecutor.execute(COMMAND, VOXFORGEDIR)) {
          throw new Exception();
      }
//
      /* creating hmmdefs and macros in
       * HMM06
       */
      
      System.out.println("creating hmmdefs and macros in * HMM06");
      
      COMMAND = HTKDIR + "HERest -A -D -T 1 -C " + _config + " -I " + _phones1Mlf + " -t 250.0 150.0 3000.0 -S " + _trainScp + " -H " + _macrosHMM05 + " -H " + _hmmdefsHMM05 + " -M " + HMM06 + " " + _monophones1 + " >> " + _alignmentLog;
      if (!ShellCommandExecutor.execute(COMMAND, VOXFORGEDIR)) {
          throw new Exception();
      }
//
      /* creating hmmdefs and macros in
       * HMM07
       */
      
      System.out.println("creating hmmdefs and macros in * HMM07");
      
      COMMAND = HTKDIR + "HERest -A -D -T 1 -C " + _config + " -I " + _phones1Mlf + " -t 250.0 150.0 3000.0 -S " + _trainScp + " -H " + _macrosHMM06 + " -H " + _hmmdefsHMM06 + " -M " + HMM07 + " " + _monophones1 + " >> " + _alignmentLog;
      if (!ShellCommandExecutor.execute(COMMAND, VOXFORGEDIR)) {
          throw new Exception();
      }
//
      /*
       * REALIGNING TRAINING DATA
       */
//
      /* creating aligned master label file
       * _alignedMlf
       */
      
      System.out.println("REALIGNING TRAINING DATA");
      
      COMMAND = HTKDIR + "HVite -A -D -T 1 -l " + asterisk + " -C " + _configFile + " -o SWT -b SENT-END -C " + _config + " -H " + _macrosHMM07 + " -H " + _hmmdefsHMM07 + " -i " + _alignedMlf + " -m -t 250.0 150.0 1000.0 -y lab -a -I " + _wordsMlf + " -S " + _trainScp + " " + _dict + " " + _monophones1 + " > " + _hviteLog;
      if (!ShellCommandExecutor.execute(COMMAND, VOXFORGEDIR)) {
          throw new Exception();
      }
//
      /* creating hmmdefs and macros in
       * HMM08
       */
      
      System.out.println("creating hmmdefs and macros in * HMM08");
      
      COMMAND = HTKDIR + "HERest -A -D -T 1 -C " + _config + " -I " + _alignedMlf + " -t 250.0 150.0 3000.0 -S " + _trainScp + " -H " + _macrosHMM07 + " -H " + _hmmdefsHMM07 + " -M " + HMM08 + " " + _monophones1 + " >> " + _alignmentLog;
      if (!ShellCommandExecutor.execute(COMMAND, VOXFORGEDIR)) {
          throw new Exception();
      }
//
      /* creating hmmdefs and macros in
       * HMM09
       */
      
      System.out.println("creating hmmdefs and macros in * HMM09");
      
      COMMAND = HTKDIR + "HERest -A -D -T 1 -C " + _config + " -I " + _alignedMlf + " -t 250.0 150.0 3000.0 -S " + _trainScp + " -H " + _macrosHMM08 + " -H " + _hmmdefsHMM08 + " -M " + HMM09 + " " + _monophones1 + " >> " + _alignmentLog;
      if (!ShellCommandExecutor.execute(COMMAND, VOXFORGEDIR)) {
          throw new Exception();
      }
//
      /*
       * CREATING TRIPHONES FROM MONOPHONES
       */
//
      /* creating files
       * _triphones1, _wintriMlf
       */
      
      System.out.println("CREATING TRIPHONES FROM MONOPHONES");
      
      COMMAND = HTKDIR + "HLEd -A -D -T 1 -n " + _triphones1 + " -C " + _configFile + " -l " + asterisk + " -i " + _wintriMlf + " " + _mktriLed + " " + _alignedMlf + " >> " + _alignmentLog;
      if (!ShellCommandExecutor.execute(COMMAND, VOXFORGEDIR)) {
          throw new Exception();
      }
//
      /* creating file
       * _mktriHed
       */
      List<String> mktriHedList = new LinkedList<String>();
      mktriHedList.add("CL " + _triphones1);
      BR01 = new BufferedReader(new FileReader(_monophones1));
      while (BR01.ready()) {
          if (!(READLINE = BR01.readLine()).equals("")) {
              mktriHedList.add("TI T_" + READLINE + " {(*-" + READLINE + "+*," + READLINE + "+*,*-" + READLINE + ").transP}");
          }
      }
      FileUtils.writeLines(new File(_mktriHed), "UTF-8", mktriHedList);
//
      /* creating hmmdefs and macros in
       * HMM10
       */
      
      System.out.println("creating hmmdefs and macros in * HMM10");
      
      COMMAND = HTKDIR + "HHEd -A -D -T 1 -C " + _configFile + " -H " + _macrosHMM09 + " -H " + _hmmdefsHMM09 + " -M " + HMM10 + " " + _mktriHed + " " + _monophones1 + " >> " + _alignmentLog;
      if (!ShellCommandExecutor.execute(COMMAND, VOXFORGEDIR)) {
          throw new Exception();
      }
//
      /* creating hmmdefs and macros in
       * HMM11
       */
      
      System.out.println("creating hmmdefs and macros in * HMM11");
      
      COMMAND = HTKDIR + "HERest -A -D -T 1 -C " + _config + " -I " + _wintriMlf + " -t 250.0 150.0 3000.0 -S " + _trainScp + " -H " + _macrosHMM10 + " -H " + _hmmdefsHMM10 + " -M " + HMM11 + " " + _triphones1 + " >> " + _alignmentLog;
      if (!ShellCommandExecutor.execute(COMMAND, VOXFORGEDIR)) {
          throw new Exception();
      }
//
      /* creating hmmdefs and macros in
       * HMM12
       */
      
      System.out.println("creating hmmdefs and macros in * HMM12");
      
      COMMAND = HTKDIR + "HERest -A -D -T 1 -C " + _config + " -I " + _wintriMlf + " -t 250.0 150.0 3000.0 -s stats -S " + _trainScp + " -H " + _macrosHMM11 + " -H " + _hmmdefsHMM11 + " -M " + HMM12 + " " + _triphones1 + " >> " + _alignmentLog;
      if (!ShellCommandExecutor.execute(COMMAND, VOXFORGEDIR)) {
          throw new Exception();
      }
//
//
      /*
       * CREATING TIED-STATE TRIPHONES
       */
//
      /* creating files
       * _fulllist, _dicttri
       */
      
      System.out.println("CREATING TIED-STATE TRIPHONES");
      
      COMMAND = HTKDIR + "HDMan -A -D -T 1 -b " + PAU + " -C " + _configFile + " -n " + _fulllist + " -g " + _globalDed + " -l flog " + _dicttri + " " + _lexicon + " >> " + _alignmentLog;
      if (!ShellCommandExecutor.execute(COMMAND, VOXFORGEDIR)) {
          throw new Exception();
      }
//
      /* updating _fulllist
       * _fulllist + _triphones1
       */
      updateFullListFile(_fulllist, _triphones1);
//
      /* creating
       * _treeHed
       */
      FileUtils.copyFile(_treeOriginalFile, new File(_treeHed));
//
      /* updating
       * _treeHed
       */
      BR01 = new BufferedReader(new FileReader(_monophones0));
      while (BR01.ready()) {
          lstMonophones0.add(BR01.readLine());
      }
      BR01.close();
      BW01 = new BufferedWriter(new FileWriterWithEncoding(new File(_treeHed), "UTF-8", Boolean.TRUE));
      for (int state = 2; state <= 4; state++) {
          for (int phone = 0; phone < lstMonophones0.size(); phone++) {
              BW01.write("TB 350 \"ST_" + lstMonophones0.get(phone) + "_" + state + "_\" {(\"" + lstMonophones0.get(phone) + "\",\"*-" + lstMonophones0.get(phone) + "+*\",\"" + lstMonophones0.get(phone) + "+*\",\"*-" + lstMonophones0.get(phone) + "\").state[" + state + "]}\n");
          }
      }
      BW01.write("\nTR 1\n \nAU \"fulllist\"\nCO \"tiedlist\"\n \nST \"trees\"");
      BW01.close();
//
      /* creating hmmdefs and macros in
       * HMM13
       * and _tiedlist
       */
      
      System.out.println("creating hmmdefs and macros in * HMM13");
      
      COMMAND = HTKDIR + "HHEd -A -D -T 1 " + " -C " + _configFile + " -H " + _macrosHMM12 + " -H " + _hmmdefsHMM12 + " -M " + HMM13 + " " + _treeHed + " " + _triphones1 + " > tree.log";
      if (!ShellCommandExecutor.execute(COMMAND, VOXFORGEDIR)) {
          throw new Exception();
      }

            /* creating hmmdefs and macros in
             * HMM14
             */
            
            System.out.println("creating hmmdefs and macros in * HMM14");
            
            COMMAND = HTKDIR + "HERest -A -D -T 1 -T 1 -C " + _config + " -I " + _wintriMlf + " -s stats -t 250.0 150.0 3000.0 -S " + _trainScp + " -H " + _macrosHMM13 + " -H " + _hmmdefsHMM13 + " -M " + HMM14 + " " + _tiedlist + " >> " + _alignmentLog;
            if (!ShellCommandExecutor.execute(COMMAND, VOXFORGEDIR)) {
                throw new Exception();
            }

            /* creating hmmdefs and macros in
             * HMM15
             */
            
            System.out.println("creating hmmdefs and macros in * HMM15");            
            
            COMMAND = HTKDIR + "HERest -A -D -T 1 -T 1 -C " + _config + " -I " + _wintriMlf + " -s stats -t 250.0 150.0 3000.0 -S " + _trainScp + " -H " + _macrosHMM14 + " -H " + _hmmdefsHMM14 + " -M " + HMM15 + " " + _tiedlist + " >> " + _alignmentLog;
            if (!ShellCommandExecutor.execute(COMMAND, VOXFORGEDIR)) {
                throw new Exception();
            }

            if (projectType == ProjectType.TTS) {
                doAlignment(asterisk, _macrosHMM15, _hmmdefsHMM15);
            }

        } catch (Exception ex) {
            System.out.println("erro ao executar comando: " + COMMAND + " "+ex.getMessage());
        }
    }

    private Boolean doAlignment(String asterisk, String _macrosHMM15, String _hmmdefsHMM15) {
        try {
            /*
             * FORCED ALIGNMENT
             * (considering that have no missing words)
             */

            /* aligning, create the
             * _alignedOut
             */
            COMMAND = HTKDIR + "HVite -A -D -T 1 -l " + asterisk + " -C " + _configFile + "  -a -b SENT-END -m -C " + _alignConfig + " -H " + _macrosHMM15 + " -H " + _hmmdefsHMM15 + " -m -t 250.0 150.0 1000.0 -I " + _wordsMlf + " -i " + getAlignedOut() + " -S " + _alignTrainScp + " " + _dict + " " + _tiedlist + " >> " + _alignmentLog;
            if (!ShellCommandExecutor.execute(COMMAND, VOXFORGEDIR)) {
                throw new Exception();
            }
            return Boolean.TRUE;
        } catch (Exception e) {
            System.out.println(e);
            return Boolean.FALSE;
        }
    }

    private Boolean extractMfccForSynthesis() {
        try {
            /*
             * CODING THE AUDIO DATA
             */

            /* creating MFCC files
             * for each audio file
             */
            COMMAND = HTKDIR + "HCopy -A -D -T 1 -C " + _audioConfig + " -S " + _codeTrainScp + " >> " + _alignmentLog;
            if (!ShellCommandExecutor.execute(COMMAND, VOXFORGEDIR)) {
                throw new Exception();
            }
            return Boolean.TRUE;
        } catch (Exception e) {
            System.out.println(e);
            return Boolean.FALSE;
        }
    }

    private Boolean createDirectories() {
        try {            
            if (!VOXFORGEDIR.exists()) {
                if (!VOXFORGEDIR.mkdirs()) {
                    throw new Exception(VOXFORGEDIR.getAbsolutePath());
                }
            }             
            HMM00 = new File(VOXFORGEDIR + File.separator + "HMM00");
            if (!HMM00.exists()) {
                if (!HMM00.mkdirs()) {
                    throw new Exception(HMM00.getAbsolutePath());
                }
            }
            HMM01 = new File(VOXFORGEDIR + File.separator + "HMM01");
            if (!HMM01.exists()) {
                if (!HMM01.mkdirs()) {
                    throw new Exception(HMM01.getAbsolutePath());
                }
            }
            HMM02 = new File(VOXFORGEDIR + File.separator + "HMM02");
            if (!HMM02.exists()) {
                if (!HMM02.mkdirs()) {
                    throw new Exception(HMM02.getAbsolutePath());
                }
            }
            HMM03 = new File(VOXFORGEDIR + File.separator + "HMM03");
            if (!HMM03.exists()) {
                if (!HMM03.mkdirs()) {
                    throw new Exception(HMM03.getAbsolutePath());
                }
            }
            HMM04 = new File(VOXFORGEDIR + File.separator + "HMM04");
            if (!HMM04.exists()) {
                if (!HMM04.mkdirs()) {
                    throw new Exception(HMM04.getAbsolutePath());
                }
            }
            HMM05 = new File(VOXFORGEDIR + File.separator + "HMM05");
            if (!HMM05.exists()) {
                if (!HMM05.mkdirs()) {
                    throw new Exception(HMM05.getAbsolutePath());
                }
            }
            HMM06 = new File(VOXFORGEDIR + File.separator + "HMM06");
            if (!HMM06.exists()) {
                if (!HMM06.mkdirs()) {
                    throw new Exception(HMM06.getAbsolutePath());
                }
            }
            HMM07 = new File(VOXFORGEDIR + File.separator + "HMM07");
            if (!HMM07.exists()) {
                if (!HMM07.mkdirs()) {
                    throw new Exception(HMM07.getAbsolutePath());
                }
            }
            HMM08 = new File(VOXFORGEDIR + File.separator + "HMM08");
            if (!HMM08.exists()) {
                if (!HMM08.mkdirs()) {
                    throw new Exception(HMM08.getAbsolutePath());
                }
            }
            HMM09 = new File(VOXFORGEDIR + File.separator + "HMM09");
            if (!HMM09.exists()) {
                if (!HMM09.mkdirs()) {
                    throw new Exception(HMM09.getAbsolutePath());
                }
            }
            HMM10 = new File(VOXFORGEDIR + File.separator + "HMM10");
            if (!HMM10.exists()) {
                if (!HMM10.mkdirs()) {
                    throw new Exception(HMM10.getAbsolutePath());
                }
            }
            HMM11 = new File(VOXFORGEDIR + File.separator + "HMM11");
            if (!HMM11.exists()) {
                if (!HMM11.mkdirs()) {
                    throw new Exception(HMM11.getAbsolutePath());
                }
            }
            HMM12 = new File(VOXFORGEDIR + File.separator + "HMM12");
            if (!HMM12.exists()) {
                if (!HMM12.mkdirs()) {
                    throw new Exception(HMM12.getAbsolutePath());
                }
            }
            HMM13 = new File(VOXFORGEDIR + File.separator + "HMM13");
            if (!HMM13.exists()) {
                if (!HMM13.mkdirs()) {
                    throw new Exception(HMM13.getAbsolutePath());
                }
            }
            HMM14 = new File(VOXFORGEDIR + File.separator + "HMM14");
            if (!HMM14.exists()) {
                if (!HMM14.mkdirs()) {
                    throw new Exception(HMM14.getAbsolutePath());
                }
            }
            HMM15 = new File(VOXFORGEDIR + File.separator + "HMM15");
            if (!HMM15.exists()) {
                if (!HMM15.mkdirs()) {
                    throw new Exception(HMM15.getAbsolutePath());
                }
            }

            if (!MFCCDIR.exists()) {
                if (!MFCCDIR.mkdirs()) {
                    throw new Exception(MFCCDIR.getAbsolutePath());
                }
            }
            return Boolean.TRUE;
        } catch (Exception ex) {
            System.out.println("Exception in ForcedAlignment.java -> createDirectories(): "+ex.getMessage());
            return Boolean.FALSE;
        }
    }

    private Boolean createAuxiliaryFiles() {
        try {
            /* creating file:
             * _globalDed
             */
            BW01 = new BufferedWriter(new FileWriterWithEncoding(_globalDed, "UTF-8"));
            BW01.write("AS " + PAU + "\nRS cmu\nMP " + SIL + " " + SIL + " " + PAU);
            BW01.close();

            /* creating file:
             * _mkphones0Led
             */
            BW01 = new BufferedWriter(new FileWriterWithEncoding(_mkphones0Led, "UTF-8"));
            BW01.write("EX\nIS " + SIL + " " + SIL + "\nDE " + PAU + "\n ");
            BW01.close();

            /* creating file:
             * _mkphones1Led
             */
            BW01 = new BufferedWriter(new FileWriterWithEncoding(_mkphones1Led, "UTF-8"));
            BW01.write("EX\nIS " + SIL + " " + SIL + "\n ");
            BW01.close();

            if (projectType == ProjectType.TTS) {
                createConfigFileSynthesis();
            }

            /* creating file
             * _silHed
             */
            BW01 = new BufferedWriter(new FileWriterWithEncoding(_silHed, "UTF-8"));
            BW01.write("AT 2 4 0.2 {" + SIL + ".transP}\n"
                    + "AT 4 2 0.2 {" + SIL + ".transP}\n"
                    + "AT 1 3 0.3 {" + PAU + ".transP}\n"
                    + "TI silst {" + SIL + ".state[3]," + PAU + ".state[2]}");
            BW01.close();

            /* creating file
             * _mktriLed
             */
            BW01 = new BufferedWriter(new FileWriterWithEncoding(_mktriLed, "UTF-8"));
            BW01.write("WB " + PAU + "\nWB " + SIL + "\nTC\n ");
            BW01.close();

            /* creating file
             * _configFile
             */
            BW01 = new BufferedWriter(new FileWriterWithEncoding(_configFile, "UTF-8"));
            BW01.write("NONUMESCAPES = TRUE" + '\n');
            BW01.close();

            return Boolean.TRUE;
        } catch (Exception ex) {
            System.out.println("Exception in ForcedAlignment.java -> createAuxiliaryFiles(): "+ex.getMessage());
            return Boolean.FALSE;
        }
    }

    private Boolean createConfigFileSynthesis() {
        try {
            /* creatiing MFCC configuration file
             * _audioConfig
             */
            BW01 = new BufferedWriter(new FileWriterWithEncoding(_audioConfig, "UTF-8"));
            BW01.write( //"SOURCERATE = 1250\n" +
                    //"SOURCEKIND = WAVEFORM\n" +
                    "SOURCEFORMAT = WAV\n"
                    + "TARGETKIND = MFCC_0_D\n"
                    + "TARGETRATE = 100000.0\n"
                    + "SAVECOMPRESSED = T\n"
                    + "SAVEWITHCRC = T\n"
                    + "WINDOWSIZE = 250000.0\n"
                    + "USEHAMMING = T\n"
                    + "PREEMCOEF = 0.97\n"
                    + "NUMCHANS = 26\n"
                    + "CEPLIFTER = 22\n"
                    + "NUMCEPS = 12");
            BW01.close();

            /* creating prototype model configuration file
             * _proto
             */            
            BW01 = new BufferedWriter(new FileWriterWithEncoding(_proto, "UTF-8"));
            BW01.write("~o <VecSize> 25 <MFCC_0_D_N_Z>\n"
                    + "~h \"proto\"\n"
                    + "<BeginHMM>\n"
                    + "  <NumStates> 5\n"
                    + "  <State> 2\n"
                    + "    <Mean> 25\n"
                    + "      0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0\n"
                    + "    <Variance> 25\n"
                    + "      1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0\n"
                    + " <State> 3\n"
                    + "    <Mean> 25\n"
                    + "      0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0\n"
                    + "    <Variance> 25\n"
                    + "      1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0\n"
                    + " <State> 4\n"
                    + "    <Mean> 25\n"
                    + "      0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0\n"
                    + "    <Variance> 25\n"
                    + "      1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0\n"
                    + " <TransP> 5\n"
                    + "  0.0 1.0 0.0 0.0 0.0\n"
                    + "  0.0 0.6 0.4 0.0 0.0\n"
                    + "  0.0 0.0 0.6 0.4 0.0\n"
                    + "  0.0 0.0 0.0 0.7 0.3\n"
                    + "  0.0 0.0 0.0 0.0 0.0\n"
                    + "<EndHMM>");
            BW01.close();

            /* creating prototype model configuration file
             * _config
             */
            BW01 = new BufferedWriter(new FileWriterWithEncoding(_config, "UTF-8"));
            BW01.write("TARGETKIND = MFCC_0_D_N_Z\n"
                    + "TARGETRATE = 100000.0\n"
                    + "SAVECOMPRESSED = T\n"
                    + "SAVEWITHCRC = T\n"
                    + "WINDOWSIZE = 250000.0\n"
                    + "USEHAMMING = T\n"
                    + "PREEMCOEF = 0.97\n"
                    + "NUMCHANS = 26\n"
                    + "CEPLIFTER = 22\n"
                    + "NUMCEPS = 12");
            BW01.close();

            /* creating alignment configuration file
             * _alignConfig
             */
            BW01 = new BufferedWriter(new FileWriterWithEncoding(_alignConfig, "UTF-8"));
            BW01.write("SOURCEFORMAT = WAV\n"
                    + "TARGETKIND = MFCC_D_N_Z_0\n"
                    + "TARGETRATE = 100000.0\n"
                    + "SAVECOMPRESSED = T\n"
                    + "SAVEWITHCRC = T\n"
                    + "WINDOWSIZE = 250000.0\n"
                    + "USEHAMMING = T\n"
                    + "PREEMCOEF = 0.97\n"
                    + "NUMCHANS = 26\n"
                    + "CEPLIFTER = 22\n"
                    + "NUMCEPS = 12");
            BW01.close();

            return Boolean.TRUE;
        } catch (Exception ex) {
            System.out.println("Exception in ForcedAlignment.java -> createConfigFileSynthesis(): "+ex.getMessage());
            return Boolean.FALSE;
        }
    }

    private Boolean createPronunciationDictionary() {
        try {
            // prompts
            StringBuilder sbPrompts = new StringBuilder();

            // mfcc codetrain
            StringBuilder sbCodeTrain = new StringBuilder();

            // mfcc train
            StringBuilder sbTrain = new StringBuilder();

            // mfcc alignTrain
            StringBuilder sbAlignTrain = new StringBuilder();

            for (int a = 0; a < lstAudioFile.size(); a++) {
                String audioFile = lstAudioFile.get(a);
                String audioText = UtteranceProcess.preProcessPhrase(UtteranceUtil.fixBlankSpaces(lstAudioText.get(a)).toUpperCase().replaceAll(Phrase.PHRASE_MARKS, ""));
                String mfccFile = MFCCDIR + File.separator + audioFile.substring(audioFile.lastIndexOf(File.separator) + 1, audioFile.length() - 4) + ".mfc";

                sbPrompts.append("*/".concat(audioFile.substring(audioFile.lastIndexOf(File.separator) + 1, audioFile.length() - 4)).concat(" ").concat(audioText.trim().toUpperCase()).concat("\n"));
                sbCodeTrain.append(audioFile.concat(" ").concat(mfccFile).concat("\n"));
                sbTrain.append(mfccFile.concat("\n"));
                sbAlignTrain.append(audioFile.concat("\n"));

                // words.mlf
                lstWordsMlf.add("#!MLF!#");
                lstWordsMlf.add("\"*/" + audioFile.substring(audioFile.lastIndexOf(File.separator) + 1, audioFile.length() - 4) + ".lab\"");

                String[] arrText = audioText.toUpperCase().split(" ");
                for (int b = 0; b < arrText.length; b++) {
                    // wordlist
                    if (!arrText[b].equals("") && lstWordList.indexOf(arrText[b]) == -1) {
                        lstWordList.add(arrText[b]);
                    }
                    if (!arrText[b].equals("")) {
                        lstWordsMlf.add(arrText[b]);
                    }
                }
                lstWordsMlf.add(".");

            }
            
            FileUtils.writeStringToFile(new File(_prompts), sbPrompts.toString(), "UTF-8");

            // codetrain.scp, train.scp and aligntrain.scp            
            FileUtils.writeStringToFile(new File(_codeTrainScp), sbCodeTrain.toString(), "UTF-8");
            FileUtils.writeStringToFile(new File(_trainScp), sbTrain.toString(), "UTF-8");
            FileUtils.writeStringToFile(new File(_alignTrainScp), sbAlignTrain.toString(), "UTF-8");

            FileUtils.writeLines(new File(_wordsMlf), "UTF-8", lstWordsMlf);

            lstWordList.add("SENT-END");
            lstWordList.add("SENT-START");
            Collections.sort(lstWordList);
            if (projectType == ProjectType.TTS) {                
                FileUtils.writeLines(new File(_wlist), "UTF-8", lstWordList);
            } else {
                allWordList.add("SENT-END");
                allWordList.add("SENT-START");
                Collections.sort(allWordList);                
                FileUtils.writeLines(new File(_wlist), "UTF-8", allWordList);
            }
            return Boolean.TRUE;
        } catch (Exception ex) {
            System.out.println("Exception in ForcedAlignment.java -> createPronunciationDictionary(): "+ex.getMessage());
            return Boolean.FALSE;
        }
    }

    private Boolean createLexiconDictionary(List<String> wordList) {
        try {
            List<String> lstLexicon = new ArrayList<String>();
            for (String word : wordList) {
                if(word.equals("INCONSTITUCIONALIDADE"))
                {
                    int b = 1;
                }
                String currWord = (word.equals("SENT-END") || word.equals("SENT-START")) ? "" : word;
                String phonemes = (currWord.equals("")) ? SIL : UtteranceUtil.removeStressFlag(GraphemeToPhoneme.toPhonemes(currWord.toLowerCase(), ""));
             
                int nSpaces = (30 - word.length());
                String firstSpaces = "";
                String secondSpaces = "";

                for (int a = 0; a < nSpaces; a++) {
                    firstSpaces += " ";
                }
                if (word.equals("SENT-END") || word.equals("SENT-START")) {
                    for (int a = 0; a < word.trim().length(); a++) {
                        secondSpaces += " ";
                    }
                }
                lstLexicon.add(word + firstSpaces + "[" + ((word.equals("SENT-END") || word.equals("SENT-START")) ? "" : word) + "]" + firstSpaces + secondSpaces + phonemes.trim());
            }           
            FileUtils.writeLines(new File(_lexicon), "UTF-8", lstLexicon);
            return Boolean.TRUE;
        } catch (Exception ex) {
            System.out.println("Exception in ForcedAlignment.java -> createLexiconDictionary(): "+ex.getMessage());
            return Boolean.FALSE;
        }
    }

    private Boolean updateFullListFile(String _fulllist, String _triphones1) {
        try {
            List<String> newFullList = new LinkedList<String>();
            BR01 = new BufferedReader(new FileReader(_fulllist));
            while (BR01.ready()) {
                if (newFullList.indexOf(READLINE = BR01.readLine()) == -1) {
                    newFullList.add(READLINE);
                }
            }
            BR01.close();
            BR01 = new BufferedReader(new FileReader(_triphones1));
            while (BR01.ready()) {
                if (newFullList.indexOf(READLINE = BR01.readLine()) == -1) {
                    newFullList.add(READLINE);
                }
            }
            BR01.close();
            FileUtils.writeLines(new File(_fulllist), "UTF-8", newFullList);
            return Boolean.TRUE;
        } catch (Exception ex) {
            System.out.println("Exception in ForcedAlignment.java -> updateFullListFile():"+ex.getMessage());
            return Boolean.FALSE;
        }
    }
    
    public String getAlignedOut() {
        return _alignedOut;
    }

    public void setAllWordList(ArrayList<String> wordList) {
        this.allWordList = wordList;
    }    
}
