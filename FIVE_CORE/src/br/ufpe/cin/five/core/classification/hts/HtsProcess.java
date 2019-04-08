/**
 * Copyright 2011 Federal University of Pernambuco. All Rights Reserved. Use is
 * subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.classification.hts;

import br.ufpe.cin.five.core.classification.ClassificationException;
import br.ufpe.cin.five.core.project.ProjectUtil;
import br.ufpe.cin.five.core.util.ShellCommandExecutorException;
import br.ufpe.cin.five.core.utterance.Utterance;
import br.ufpe.cin.five.core.utterance.UtteranceException;
import br.ufpe.cin.five.core.utterance.UtteranceProcess;
import br.ufpe.cin.five.core.utterance.nlp.ContextualLabel;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Alexandre
 */
public class HtsProcess {

    private String enginePath;
    private String tempPath;
    private String htsPath;

    public HtsProcess(String enginePath) {
        this.enginePath = enginePath;
        this.tempPath = enginePath + File.separator + "temp";
        this.htsPath = enginePath + File.separator + "resources" + File.separator + "hts_engine_API-1.06";
    }

    public void train() {
        preProcessing();
    }

    public File test(String phrase, String speaker, String fileName) throws ClassificationException {
        try {

            ProjectUtil.createFolder(tempPath);
            
            String traceFileName = tempPath + File.separator + fileName +".trace";
            String labFileName = tempPath + File.separator + fileName + ".lab";
            String wavFileName = tempPath + File.separator + fileName + ".wav";

            Utterance utterance = new Utterance();        
            utterance.setDescription(phrase);
            UtteranceProcess utteranceProcess = new UtteranceProcess(enginePath);
            utterance.setPhrases(utteranceProcess.convertUtteranceToPhrases(phrase));            
            
            ContextualLabel cLabel = new ContextualLabel(utterance, labFileName);
            cLabel.process();

            String speakerPath = enginePath + File.separator + "voices" + File.separator + speaker;

            HtsCommands.executeHtsEngine(htsPath, speakerPath, wavFileName, traceFileName, labFileName);
            
            File wavFile = new File(wavFileName);
            return wavFile;

        } catch (Exception ex) {
            throw new ClassificationException(ex.getMessage());
        }
    }

    private void preProcessing() {
        // settings
        String spkr = "m001";
        String data = "portuguese";
        String qnum = "001";
        String ver = "1";

        List<String> SET = new InstantiatedList(new String[]{"cmp", "dur"});
        List<String> cmp = new InstantiatedList(new String[]{"mgc", "lf0"});
        List<String> dur = new InstantiatedList(new String[]{"dur"});

        Map<String, List<String>> ref = new InstantiatedListsMap(new ItemMap("cmp", cmp),
                new ItemMap("dur", dur));

        // model structure
        Map<String, String> vSize = new LinkedHashMap();
        Map<String, String> nstream = new LinkedHashMap();

        // variance floors
        Map<String, String> vflr = new InstantiatedStringsMap(new ItemMap("mgc", "0.01"),
                new ItemMap("lf0", "0.01"),
                new ItemMap("dur", "0.01"));
        // minimum likelihood gain in clustering
        Map<String, String> thr = new InstantiatedStringsMap(new ItemMap("mgc", "000"),
                new ItemMap("lf0", "000"),
                new ItemMap("dur", "000"));

        // tree size control param. for MDL
        Map<String, String> mdlf = new InstantiatedStringsMap(new ItemMap("mgc", "1.0"),
                new ItemMap("lf0", "1.0"),
                new ItemMap("dur", "1.0"));

        // minimum occupancy counts
        Map<String, String> mocc = new InstantiatedStringsMap(new ItemMap("mgc", "10.0"),
                new ItemMap("lf0", "10.0"),
                new ItemMap("dur", " 5.0"));

        // stats load threshold
        Map<String, String> gam = new InstantiatedStringsMap(new ItemMap("mgc", "000"),
                new ItemMap("lf0", "000"),
                new ItemMap("dur", "000"));

        // feature type to mmf conversion
        Map<String, String> t2s = new InstantiatedStringsMap(new ItemMap("mgc", "cmp"),
                new ItemMap("lf0", "cmp"),
                new ItemMap("dur", "dur"));

        // stream start
        Map<String, String> strb = new InstantiatedStringsMap(new ItemMap("mgc", "1"),
                new ItemMap("lf0", "2"),
                new ItemMap("dur", "1"));

        // stream end
        Map<String, String> stre = new InstantiatedStringsMap(new ItemMap("mgc", "1"),
                new ItemMap("lf0", "4"),
                new ItemMap("dur", "5"));

        // msd information
        Map<String, String> msdi = new InstantiatedStringsMap(new ItemMap("mgc", "0"),
                new ItemMap("lf0", "1"),
                new ItemMap("dur", "0"));

        // stream weights
        Map<String, String> strw = new InstantiatedStringsMap(new ItemMap("mgc", "1.0"),
                new ItemMap("lf0", "1.0"),
                new ItemMap("dur", "1.0"));

        // feature order
        Map<String, String> ordr = new InstantiatedStringsMap(new ItemMap("mgc", "25"),
                new ItemMap("lf0", "1"),
                new ItemMap("dur", "5"));

        // number of windows
        Map<String, String> nwin = new InstantiatedStringsMap(new ItemMap("mgc", "3"),
                new ItemMap("lf0", "3"),
                new ItemMap("dur", "0"));

        // number of blocks for transforms
        Map<String, String> nblk = new InstantiatedStringsMap(new ItemMap("mgc", "3"),
                new ItemMap("lf0", "1"),
                new ItemMap("dur", "1"));

        // band width for transforms
        Map<String, String> band = new InstantiatedStringsMap(new ItemMap("mgc", "24"),
                new ItemMap("lf0", "0"),
                new ItemMap("dur", "0"));

        // minimum likelihood gain in clustering for GV
        Map<String, String> gvthr = new InstantiatedStringsMap(new ItemMap("mgc", "000"),
                new ItemMap("lf0", "000"));

        // tree size control for GV
        Map<String, String> gvmdlf = new InstantiatedStringsMap(new ItemMap("mgc", "1.0"),
                new ItemMap("lf0", "1.0"));

        // stats load threshold for GV
        Map<String, String> gvgam = new InstantiatedStringsMap(new ItemMap("mgc", "000"),
                new ItemMap("lf0", "000"));

        // sillent and pause phonemes
        String[] slnt = new String[]{"sil", "pau"};

        /*
         * Speech Analysis/Synthesis Setting
         */
        // speech analysis
        String sr = "16000"; // sampling rate (Hz)
        String fs = "80"; // frame period (point)
        String fw = "0.42"; // frequency warping
        String gm = "0"; // pole/zero representation weight
        String lg = "1"; // use log gain instead of linear gain
        String fr = String.valueOf(Double.parseDouble(fs) / Double.parseDouble(sr)); // frame period (sec)

        // speech synthesis
        String pf = "1.4"; // postfiltering factor
        String fl = "4096"; // length of impulse response
        String co = "2047"; // order of cepstrum to approximate mel-generalized cepstrum

        /*
         * Modeling/Generation Setting
         */
        // modeling
        String nState = "5"; // number of states
        String nIte = "5"; // number of iterations for embedded training
        String beam = "'1500 100 5000'"; // initial, inc, and upper limit of beam width
        String maxdev = "10"; // max standard dev coef to control HSMM maximum duration
        String mindur = "5"; // min state duration to be evaluated
        String wf = "3"; // mixture weight flooring

        // generation
        String maxEMiter = "20"; // max EM iteration
        String EMepsilon = "0.0001"; // convergence factor for EM iteration
        String useGV = "1"; // turn on GV
        String maxGViter = "50"; // max GV iteration
        String GVepsilon = "0.0001"; // convergence factor for GV iteration
        String minEucNorm = "0.01"; // minimum Euclid norm for GV iteration
        String stepInit = "1.0"; // initial step size
        String stepInc = "1.2"; // step size acceleration factor
        String stepDec = "0.5"; // step size deceleration factor
        String hmmWeight = "1.0"; // weight for HMM output prob.
        String gvWeight = "1.0"; // weight for GV output prob.
        String optKind = "'NEWTON'"; // optimization method (STEEPEST, NEWTON, or LBFGS)
        String nosilgv = "1"; // GV without silent and pause phoneme
        String cdgv = "1"; // context-dependent GV

        /*
         * Directories & Commands
         */
        // DIR
        String PROJECTDIR;
        String RESOURCESDIR;
        String FEATURESDIR;
        String TEMPDIR;
        String SPTK;
        String CYGWIN;
        String ACTIVETCL;
        String HTS;
        String STRAWBERRY;
        String OPENFST;
        String HTSENGINE;

        // PERL COMMANDS
        String PERL;

        // project directories
        String prjdir = "";
        String datdir = "";

        // HTS commands
        String HCOMPV;
        String HINIT;
        String HREST;
        String HEREST;
        String HHED;
        String HFST;
        String HMGENS;
        String ENGINE;

        // SPTK commands
        String X2X;
        String FREQT;
        String C2ACR;
        String VOPR;
        String MC2B;
        String SOPR;
        String B2MC;
        String EXCITE;
        String LSP2LPC;
        String MGC2MGC;
        String MGLSADF;
        String MERGE;
        String BCP;
        String LSPCHECK;
        String BCUT;
        String VSTAT;
        String NAN;

        // SoX (to add RIFF header)
        String SOX;
        String SOXOPTION;

        // OpenFst commands
        String FSTCOMPILE;
        String FSTCOMPOSE;
        String FSTSHORTESTPATH;
        String FSTPRINT;

        /*
         * Switch
         */
        String MKEMV = "1"; // preparing environments
        String HCMPV = "1"; // computing a global variance
        String IN_RE = "1"; // initialization & reestimation
        String MMMMF = "1"; // making a monophone mmf
        String ERST0 = "1"; // embedded reestimation (monophone)
        String MN2FL = "1"; // copying monophone mmf to fullcontext one
        String ERST1 = "1"; // embedded reestimation (fullcontext)
        String CXCL1 = "1"; // tree-based context clustering
        String ERST2 = "1"; // embedded reestimation (clustered)
        String UNTIE = "1"; // untying the parameter sharing structure
        String ERST3 = "1"; // embedded reestimation (untied)
        String CXCL2 = "1"; // tree-based context clustering
        String ERST4 = "1"; // embedded reestimation (re-clustered)
        String FALGN = "1"; // forced alignment using WFST for no-silent GV
        String MCDGV = "1"; // making global variance
        String MKUNG = "1"; // making unseen models (GV)
        String MKUN1 = "1"; // making unseen models (1mix)
        String PGEN1 = "1"; // generating speech parameter sequences (1mix)
        String WGEN1 = "1"; // synthesizing waveforms (1mix)
        String CONVM = "1"; // converting mmfs to the hts_engine file format
        String ENGIN = "1"; // synthesizing waveforms using hts_engine
        String SEMIT = "1"; // semi-tied covariance matrices
        String MKUNS = "1"; // making unseen models (stc)
        String PGENS = "1"; // generating speech parameter sequences (stc)
        String WGENS = "1"; // synthesizing waveforms (stc)
        String UPMIX = "1"; // increasing the number of mixture components (1mix -> 2mix)
        String ERST5 = "1"; // embedded reestimation (2mix)
        String MKUN2 = "1"; // making unseen models (2mix)
        String PGEN2 = "1"; // generating speech parameter sequences (2mix)
        String WGEN2 = "1"; // synthesizing waveforms (2mix)

        /*
         * File Locations
         */
        // data location file
        Map<String, String> scp = new InstantiatedStringsMap(new ItemMap("trn", datdir + "scp" + File.separator + "train.scp"),
                new ItemMap("gen", datdir + "scp" + File.separator + "gen.scp"));

        // model list files
        Map<String, String> lst = new InstantiatedStringsMap(new ItemMap("mon", datdir + "lists" + File.separator + "mono.list"),
                new ItemMap("ful", datdir + "lists" + File.separator + "full.list"),
                new ItemMap("all", datdir + "lists" + File.separator + "full_all.list"));

        // master label files
        Map<String, String> mlf = new InstantiatedStringsMap(new ItemMap("mon", datdir + "labels" + File.separator + "mono.mlf"),
                new ItemMap("ful", datdir + "labels" + File.separator + "full.mlf"));

        // configuration variable files
        Map<String, String> cfg = new InstantiatedStringsMap(new ItemMap("trn", prjdir + "configs" + File.separator + "trn.cnf"),
                new ItemMap("nvf", prjdir + "configs" + File.separator + "nvf.cnf"),
                new ItemMap("cnv", prjdir + "configs" + File.separator + "cnv.cnf"),
                new ItemMap("stc", prjdir + "configs" + File.separator + "stc.cnf"),
                new ItemMap("syn", prjdir + "configs" + File.separator + "syn.cnf"));

        // name of proto type definition file
        Map<String, String> prtfile = new LinkedHashMap<String, String>();

        // model files
        Map<String, String> model = new LinkedHashMap<String, String>();
        Map<String, String> hinit = new LinkedHashMap<String, String>();
        Map<String, String> hrest = new LinkedHashMap<String, String>();
        Map<String, String> vfloors = new LinkedHashMap<String, String>();
        Map<String, String> initmmf = new LinkedHashMap<String, String>();
        Map<String, String> monommf = new LinkedHashMap<String, String>();
        Map<String, String> fullmmf = new LinkedHashMap<String, String>();
        Map<String, String> clusmmf = new LinkedHashMap<String, String>();
        Map<String, String> untymmf = new LinkedHashMap<String, String>();
        Map<String, String> reclmmf = new LinkedHashMap<String, String>();
        Map<String, String> rclammf = new LinkedHashMap<String, String>();
        Map<String, String> tiedlst = new LinkedHashMap<String, String>();
        Map<String, String> hfst = new LinkedHashMap<String, String>();
        Map<String, String> stcmmf = new LinkedHashMap<String, String>();
        Map<String, String> stcammf = new LinkedHashMap<String, String>();
        Map<String, String> stcbase = new LinkedHashMap<String, String>();


        // statistics files
        Map<String, String> stats = null;


        // model edit files
        Map<String, String> hed = new LinkedHashMap<String, String>();
        Map<String, String> lvf = new LinkedHashMap<String, String>();
        Map<String, String> m2f = new LinkedHashMap<String, String>();
        Map<String, String> mku = new LinkedHashMap<String, String>();
        Map<String, String> unt = new LinkedHashMap<String, String>();
        Map<String, String> upm = new LinkedHashMap<String, String>();
        Map<String, String> cnv = new LinkedHashMap<String, String>();
        Map<String, String> cxc = new LinkedHashMap<String, String>();

        // questions about contexts
        Map<String, String> qs = new LinkedHashMap<String, String>();
        Map<String, String> qs_utt = new LinkedHashMap<String, String>();

        // decision tree files
        Map<String, String> trd = new LinkedHashMap<String, String>();
        Map<String, String> mdl = new LinkedHashMap<String, String>();
        Map<String, String> tre = new LinkedHashMap<String, String>();

        // converted model & tree files for hts_engine
        String voice = prjdir + File.separator + "voices" + File.separator + "qst" + qnum + File.separator + "ver" + ver;
        Map<String, String> trv = new LinkedHashMap<String, String>();
        Map<String, String> pdf = new LinkedHashMap<String, String>();

        // window files for parameter generation
        String windir = datdir + File.separator + "win";
        Map<String, String> win = new LinkedHashMap<String, String>();

        PROJECTDIR = this.enginePath + File.separator;
        RESOURCESDIR = PROJECTDIR + File.separator + "resources" + File.separator;
        FEATURESDIR = PROJECTDIR + "features" + File.separator;
        TEMPDIR = RESOURCESDIR + "temp" + File.separator;
        SPTK = RESOURCESDIR + "apps" + File.separator + "SPTK-3.3" + File.separator;
        CYGWIN = RESOURCESDIR + "apps" + File.separator + "cygwin" + File.separator;
        ACTIVETCL = RESOURCESDIR + "apps" + File.separator + "ActiveTcl" + File.separator;
        HTS = RESOURCESDIR + "apps" + File.separator + "HTS-2.1.1" + File.separator;
        STRAWBERRY = RESOURCESDIR + "apps" + File.separator + "strawberry-perl-5.12.0.1" + File.separator;
        OPENFST = RESOURCESDIR + "apps" + File.separator + "" + File.separator;
        HTSENGINE = RESOURCESDIR + "apps" + File.separator + "hts_engine" + File.separator;

        // PERL COMMANDS
        PERL = STRAWBERRY + "bin" + File.separator + "perl.exe";

        // project directories
        prjdir = PROJECTDIR + File.separator;
        datdir = PROJECTDIR + File.separator + "data" + File.separator;

        // HTS commands
        HCOMPV = HTS + "bin" + File.separator + "HCompV";
        HINIT = HTS + "bin" + File.separator + "HInit";
        HREST = HTS + "bin" + File.separator + "HRest";
        HEREST = HTS + "bin" + File.separator + "HERest";
        HHED = HTS + "bin" + File.separator + "HHEd";
        HFST = HTS + "bin" + File.separator + "HFst";
        HMGENS = HTS + "bin" + File.separator + "HMGenS";
        ENGINE = HTSENGINE + "bin" + File.separator + "hts_engine";

        // SPTK commands
        X2X = SPTK + "bin" + File.separator + "x2x";
        FREQT = SPTK + "bin" + File.separator + "freqt";
        C2ACR = SPTK + "bin" + File.separator + "c2acr";
        VOPR = SPTK + "bin" + File.separator + "vopr";
        MC2B = SPTK + "bin" + File.separator + "mc2b";
        SOPR = SPTK + "bin" + File.separator + "sopr";
        B2MC = SPTK + "bin" + File.separator + "b2mc";
        EXCITE = SPTK + "bin" + File.separator + "excite";
        LSP2LPC = SPTK + "bin" + File.separator + "lsp2lpc";
        MGC2MGC = SPTK + "bin" + File.separator + "mgc2mgc";
        MGLSADF = SPTK + "bin" + File.separator + "mglsadf";
        MERGE = SPTK + "bin" + File.separator + "merge";
        BCP = SPTK + "bin" + File.separator + "bcp";
        LSPCHECK = SPTK + "bin" + File.separator + "lspcheck";
        BCUT = SPTK + "bin" + File.separator + "bcut";
        VSTAT = SPTK + "bin" + File.separator + "vstat";
        NAN = SPTK + "bin" + File.separator + "nan";

        // SoX (to add RIFF header)
        SOX = RESOURCESDIR + "apps" + File.separator + "sox" + File.separator + "sox";
        SOXOPTION = "w";

        // OpenFst commands
        FSTCOMPILE = OPENFST + "bin" + File.separator + "fstcompile";
        FSTCOMPOSE = OPENFST + "bin" + File.separator + "fstcompose";
        FSTSHORTESTPATH = OPENFST + "bin" + File.separator + "fstshortestpath";
        FSTPRINT = OPENFST + "bin" + File.separator + "fstprint";

        int nPdfStreams;

        vSize.put("total", "0");
        nstream.put("total", "0");
        nPdfStreams = 0;

        for (String type : cmp) {
            vSize.put(type, String.valueOf(Integer.parseInt(nwin.get(type)) * Integer.parseInt(ordr.get(type))));
            vSize.put("total", String.valueOf(Integer.parseInt(vSize.get("total")) + Integer.parseInt(vSize.get(type))));
            nstream.put(type, String.valueOf(Integer.parseInt(stre.get(type)) - Integer.parseInt(strb.get(type)) + 1));
            nstream.put("total", String.valueOf(Integer.parseInt(nstream.get("total")) + Integer.parseInt(nstream.get(type))));
            nPdfStreams++;
        }

        // name of proto type definition file
        prtfile = new InstantiatedStringsMap(new ItemMap("cmp", prjdir + "proto" + File.separator + "qst" + qnum + File.separator + "ver" + ver + File.separator + "state-" + nState + "_stream-" + nstream.get("total")));

        for (String type : cmp) {
            prtfile.put("cmp", prtfile.get("cmp") + "_" + type + "-" + vSize.get(type));
        }
        prtfile.put("cmp", prtfile.get("cmp") + ".prt");

        // model files
        for (String set : SET) {
            model.put(set, prjdir + File.separator + "models" + File.separator + "qst" + qnum + File.separator + "ver" + ver + File.separator + set);
            hinit.put(set, model.get(set) + File.separator + "HInit");
            hrest.put(set, model.get(set) + File.separator + "HRest");
            vfloors.put(set, model.get(set) + File.separator + "vFloors");
            initmmf.put(set, model.get(set) + File.separator + "init.mmf");
            monommf.put(set, model.get(set) + File.separator + "monophone.mmf");
            fullmmf.put(set, model.get(set) + File.separator + "fullcontext.mmf");
            clusmmf.put(set, model.get(set) + File.separator + "clustered.mmf");
            untymmf.put(set, model.get(set) + File.separator + "untied.mmf");
            reclmmf.put(set, model.get(set) + File.separator + "re_clustered.mmf");
            rclammf.put(set, model.get(set) + File.separator + "re_clustered_all.mmf");
            tiedlst.put(set, model.get(set) + File.separator + "tiedlist");
            hfst.put(set, model.get(set) + File.separator + "HFst");
            stcmmf.put(set, model.get(set) + File.separator + "stc.mmf");
            stcammf.put(set, model.get(set) + File.separator + "stc_all.mmf");
            stcbase.put(set, model.get(set) + File.separator + "stc.base");
        }

        // statistics files
        for (String set : SET) {
            stats.put(set, prjdir + File.separator + "stats" + File.separator + "qst" + qnum + File.separator + "ver" + ver + File.separator + set + ".stats");
        }

        // model edit files
        for (String set : SET) {
            hed.put(set, prjdir + File.separator + "edfiles" + File.separator + "qst" + qnum + File.separator + "ver" + ver + File.separator + set);
            lvf.put(set, hed.get(set) + File.separator + "lvf.hed");
            m2f.put(set, hed.get(set) + File.separator + "m2f.hed");
            mku.put(set, hed.get(set) + File.separator + "mku.hed");
            unt.put(set, hed.get(set) + File.separator + "unt.hed");
            upm.put(set, hed.get(set) + File.separator + "upm.hed");
            for (String type : ref.get(set)) {
                cnv.put(type, hed.get(set) + File.separator + "cnv_$type.hed");
                cxc.put(type, hed.get(set) + File.separator + "cxc_$type.hed");
            }
        }

        // questions about contexts
        for (String set : SET) {
            for (String type : ref.get(set)) {
                qs.put(type, datdir + File.separator + "questions" + File.separator + "questions_qst" + qnum + ".hed");
                qs_utt.put(type, datdir + File.separator + "questions" + File.separator + "questions_utt_qst" + qnum + ".hed");
            }
        }

        // decision tree files
        for (String set : SET) {
            trd.put(set, prjdir + File.separator + "trees" + File.separator + "qst" + qnum + File.separator + "ver" + ver + File.separator + set);
            for (String type : ref.get(set)) {
                mdl.put(type, (thr.get(type).equals("000") ? "-m -a " + mdlf.get(type) : ""));
                tre.put(type, trd.get(set) + File.separator + type + ".inf");
            }
        }

        // converted model & tree files for hts_engine
        for (String set : SET) {
            for (String type : ref.get(set)) {
                trv.put(type, voice + File.separator + "tree-" + type + ".inf");
                pdf.put(type, voice + File.separator + type + ".pdf");
            }
        }


        // window files for parameter generation
        for (String type : cmp) {
            for (int d = 1; d <= Integer.parseInt(nwin.get(type)); d++) {
                //$win{$type}[ $d - 1 ] = "${type}.win${d}";
                win.put(type + (d - 1), type + ".win" + d);
            }
        }
    }
}
