
package br.ufpe.cin.five.core.classification.hts;

import java.io.File;
import javax.persistence.Entity;

/**
 *
 * @author Alexandre
 */

public class HtsParameters {

    private String VOICE;        
    private String TEMP;
    private String FILE_RAW;
    private String FILE_TRACE;
    private String FILE_LAB;
    private String FILE_WAV;
    private String FILE_MP3;     
    
    private String TREE_DUR;
    private String TREE_LF0;
    private String TREE_MGC;
    private String PDF_DUR;
    private String PDF_LF0;
    private String PDF_MGC;
    private String MGC_WIN1;
    private String MGC_WIN2;
    private String MGC_WIN3;
    private String LF0_WIN1;
    private String LF0_WIN2;
    private String LF0_WIN3;
    private String GV_MGC_PDF;
    private String GV_LF0_PDF;
    private String SR; // # sampling rate (Hz)
    private String FS; // # frame period (point)
    private String FW; // # frequency warping
    private String GM; // # pole/zero representation weight
    private String LG; // # use log gain instead of linear gain
    private String FR; // # frame period (sec)
    private Double PF; // # postfiltering factor
   
    
    public HtsParameters(String resourcesPath, String speaker){
        
        VOICE = speaker;
        
        TEMP = resourcesPath + File.separator + "temp";
        FILE_RAW = TEMP  + File.separator + "speech.raw";
        FILE_LAB = TEMP  + File.separator + "speech.lab";
        FILE_WAV = TEMP  + File.separator + "speech.wav";
        FILE_MP3 = TEMP  + File.separator + "speech.mp3";
        FILE_TRACE = TEMP  + File.separator + "speech.trace";        
        
        TREE_DUR = "tree-dur.inf";
        TREE_LF0 = "tree-lf0.inf";
        TREE_MGC = "tree-mgc.inf";
        PDF_DUR = "dur.pdf";
        PDF_LF0 = "lf0.pdf";
        PDF_MGC = "mgc.pdf";
        MGC_WIN1 = "mgc.win1";
        MGC_WIN2 = "mgc.win2";
        MGC_WIN3 = "mgc.win3";
        LF0_WIN1 = "lf0.win1";
        LF0_WIN2 = "lf0.win2";
        LF0_WIN3 = "lf0.win3";
        GV_MGC_PDF = "gv-mgc.pdf";
        GV_LF0_PDF = "gv-lf0.pdf";
        SR = "16000"; 
        FS = "90";    
        FW = "0.42";  
        GM = "0";     
        LG = "1";     
        FR = String.valueOf(Double.parseDouble(FS) / Double.parseDouble(SR));
        PF = 1.4; 
    }

    public String getFILE_LAB() {
        return FILE_LAB;
    }

    public void setFILE_LAB(String FILE_LAB) {
        this.FILE_LAB = FILE_LAB;
    }

    public String getFILE_MP3() {
        return FILE_MP3;
    }

    public void setFILE_MP3(String FILE_MP3) {
        this.FILE_MP3 = FILE_MP3;
    }

    public String getFILE_RAW() {
        return FILE_RAW;
    }

    public void setFILE_RAW(String FILE_RAW) {
        this.FILE_RAW = FILE_RAW;
    }

    public String getFILE_TRACE() {
        return FILE_TRACE;
    }

    public void setFILE_TRACE(String FILE_TRACE) {
        this.FILE_TRACE = FILE_TRACE;
    }

    public String getFILE_WAV() {
        return FILE_WAV;
    }

    public void setFILE_WAV(String FILE_WAV) {
        this.FILE_WAV = FILE_WAV;
    }

    public String getFR() {
        return FR;
    }

    public void setFR(String FR) {
        this.FR = FR;
    }

    public String getFS() {
        return FS;
    }

    public void setFS(String FS) {
        this.FS = FS;
    }

    public String getFW() {
        return FW;
    }

    public void setFW(String FW) {
        this.FW = FW;
    }

    public String getGM() {
        return GM;
    }

    public void setGM(String GM) {
        this.GM = GM;
    }

    public String getGV_LF0_PDF() {
        return GV_LF0_PDF;
    }

    public void setGV_LF0_PDF(String GV_LF0_PDF) {
        this.GV_LF0_PDF = GV_LF0_PDF;
    }

    public String getGV_MGC_PDF() {
        return GV_MGC_PDF;
    }

    public void setGV_MGC_PDF(String GV_MGC_PDF) {
        this.GV_MGC_PDF = GV_MGC_PDF;
    }

    public String getLF0_WIN1() {
        return LF0_WIN1;
    }

    public void setLF0_WIN1(String LF0_WIN1) {
        this.LF0_WIN1 = LF0_WIN1;
    }

    public String getLF0_WIN2() {
        return LF0_WIN2;
    }

    public void setLF0_WIN2(String LF0_WIN2) {
        this.LF0_WIN2 = LF0_WIN2;
    }

    public String getLF0_WIN3() {
        return LF0_WIN3;
    }

    public void setLF0_WIN3(String LF0_WIN3) {
        this.LF0_WIN3 = LF0_WIN3;
    }

    public String getLG() {
        return LG;
    }

    public void setLG(String LG) {
        this.LG = LG;
    }

    public String getMGC_WIN1() {
        return MGC_WIN1;
    }

    public void setMGC_WIN1(String MGC_WIN1) {
        this.MGC_WIN1 = MGC_WIN1;
    }

    public String getMGC_WIN2() {
        return MGC_WIN2;
    }

    public void setMGC_WIN2(String MGC_WIN2) {
        this.MGC_WIN2 = MGC_WIN2;
    }

    public String getMGC_WIN3() {
        return MGC_WIN3;
    }

    public void setMGC_WIN3(String MGC_WIN3) {
        this.MGC_WIN3 = MGC_WIN3;
    }

    public String getPDF_DUR() {
        return PDF_DUR;
    }

    public void setPDF_DUR(String PDF_DUR) {
        this.PDF_DUR = PDF_DUR;
    }

    public String getPDF_LF0() {
        return PDF_LF0;
    }

    public void setPDF_LF0(String PDF_LF0) {
        this.PDF_LF0 = PDF_LF0;
    }

    public String getPDF_MGC() {
        return PDF_MGC;
    }

    public void setPDF_MGC(String PDF_MGC) {
        this.PDF_MGC = PDF_MGC;
    }

    public Double getPF() {
        return PF;
    }

    public void setPF(Double PF) {
        this.PF = PF;
    }

    public String getSR() {
        return SR;
    }

    public void setSR(String SR) {
        this.SR = SR;
    }

    public String getTEMP() {
        return TEMP;
    }

    public void setTEMP(String TEMP) {
        this.TEMP = TEMP;
    }

    public String getTREE_DUR() {
        return TREE_DUR;
    }

    public void setTREE_DUR(String TREE_DUR) {
        this.TREE_DUR = TREE_DUR;
    }

    public String getTREE_LF0() {
        return TREE_LF0;
    }

    public void setTREE_LF0(String TREE_LF0) {
        this.TREE_LF0 = TREE_LF0;
    }

    public String getTREE_MGC() {
        return TREE_MGC;
    }

    public void setTREE_MGC(String TREE_MGC) {
        this.TREE_MGC = TREE_MGC;
    }

    public String getVOICE() {
        return VOICE;
    }

    public void setVOICE(String VOICE) {
        this.VOICE = VOICE;
    }            
    
}
