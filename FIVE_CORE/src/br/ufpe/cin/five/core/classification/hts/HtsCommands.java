/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.classification.hts;

import br.ufpe.cin.five.core.util.ShellCommandExecutor;
import br.ufpe.cin.five.core.util.ShellCommandExecutorException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Alexandre
 */
public class HtsCommands {

    //Boolean para debugar o sistem
    private static final boolean DEBUG = false;

    public static void executeHtsEngine(String htsPath, String speakerPath, String wavFile, String traceFile, String labFile) throws IOException, InterruptedException, ShellCommandExecutorException {
       
        String TREE_DUR = "\"" + speakerPath + File.separator + "tree-dur.inf" + "\"";
        String TREE_LF0 = "\"" + speakerPath + File.separator + "tree-lf0.inf" + "\"";
        String TREE_MGC = "\"" + speakerPath + File.separator + "tree-mgc.inf" + "\"";

        String PDF_DUR = "\"" + speakerPath + File.separator + "dur.pdf" + "\"";
        String PDF_LF0 = "\"" + speakerPath + File.separator + "lf0.pdf" + "\"";
        String PDF_MGC = "\"" + speakerPath + File.separator + "mgc.pdf" + "\"";

        String MGC_WIN1 = "\"" + speakerPath + File.separator + "mgc.win1" + "\"";
        String MGC_WIN2 = "\"" + speakerPath + File.separator + "mgc.win2" + "\"";
        String MGC_WIN3 = "\"" + speakerPath + File.separator + "mgc.win3" + "\"";

        String LF0_WIN1 = "\"" + speakerPath + File.separator + "lf0.win1" + "\"";
        String LF0_WIN2 = "\"" + speakerPath + File.separator + "lf0.win2" + "\"";
        String LF0_WIN3 = "\"" + speakerPath + File.separator + "lf0.win3" + "\"";

        String GV_MGC_PDF = "\"" + speakerPath + File.separator + "gv-mgc.pdf" + "\"";
        String GV_LF0_PDF = "\"" + speakerPath + File.separator + "gv-lf0.pdf" + "\"";

        Boolean useGV = Boolean.TRUE;

        String SR = "16000"; // # sampling rate (Hz)   8000
        String FS = "80"; // # frame period (point)      45
        String FW = "0.42"; // # frequency warping      0.07
        String GM = "0"; // # pole/zero representation weight
        String LG = "1"; // # use log gain instead of linear gain
        String FR = String.valueOf(Double.parseDouble(FS) / Double.parseDouble(SR)); // # frame period (sec)
        double PF = 1.4; // # postfiltering factor*/                

        String command = "\"" + htsPath + File.separator + "hts_engine" + "\"";
        command += " -td " + TREE_DUR + " -tf " + TREE_LF0 + " -tm " + TREE_MGC;
        command += " -md " + PDF_DUR + " -mf " + PDF_LF0 + " -mm " + PDF_MGC;

        command += " -dm " + MGC_WIN1 + " -dm " + MGC_WIN2 + " -dm " + MGC_WIN3;
        command += " -df " + LF0_WIN1 + " -df " + LF0_WIN2 + " -df " + LF0_WIN3;

        command += " -s " + SR + " -p " + FS + " -a " + FW;
        command += " -g " + GM + (LG.equals("1") ? " -l " : "") + " -b " + (PF - 1.0);
        if (useGV) {
            command += " -cm " + GV_MGC_PDF + " -cf " + GV_LF0_PDF + " -b 0.0";
        }

        command += " -ow " + "\""+wavFile+"\"";
        command += " -ot " + "\""+traceFile+"\"" + " " + "\""+labFile + "\"";

        if (DEBUG) {
            debugCommand(command);
        }
        ShellCommandExecutor.execute(command, htsPath);

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
