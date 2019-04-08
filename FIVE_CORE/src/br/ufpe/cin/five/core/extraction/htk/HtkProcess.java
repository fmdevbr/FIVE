/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.extraction.htk;

import br.ufpe.cin.five.core.extraction.ExtractionException;
import br.ufpe.cin.five.core.extraction.ExtractionProcess;
import br.ufpe.cin.five.core.project.ProjectUtil;
import br.ufpe.cin.five.core.sample.Audio;
import br.ufpe.cin.five.core.util.ShellCommandExecutor;
import br.ufpe.cin.five.core.util.WaveWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Alexandre
 */
public class HtkProcess extends ExtractionProcess {

    //private String samplesPath;
    private String featuresPath;
    private String tempPath;
    private String htkPath;

    /**
     * Creates a new empty HtkProcess
     */    
    public HtkProcess(){}
    
    public HtkProcess(String projectPath) {
        //this.samplesPath = projectPath + File.separator + "samples";        
        this.featuresPath = projectPath + File.separator + "features";        
        this.tempPath = projectPath + File.separator + "temp";  
        this.htkPath = projectPath + File.separator + "resources" + File.separator + "htk-3.3";
    }    
    
    /**
     * Do the extraction on the set of samples
     * @param samples the set of samples
     * @throws ExtractionException
     */
    @Override
    public double[] extract(Object params, Audio audio) throws ExtractionException {

        try {
            HtkParameters htkParameters = (HtkParameters) params;
                        
            ProjectUtil.createFolder(tempPath);            
            String configFile = tempPath + File.separator + "config.txt";            
            createConfigHtkFile(htkParameters, configFile);
          
            String audioFile = "";
            String featureFile = ""; 
            
            /*
            audio.setAudioData(VAD.absCut(audio.getAudioData())[0]);
            */      
            
            if (audio.getAudioName() == null) {
                
                audioFile = tempPath + File.separator + "captured.wav";
                featureFile = tempPath + File.separator + "informed.mfc";
                
                WaveWriter writer = new WaveWriter(new File(audioFile), audio.getAudioFormat());
                
                writer.write(audio.getAudioData());
                
            } else {
                
                audioFile = audio.getAudioPath()+ File.separator + audio.getAudioName();
                
                if (htkParameters.isDecodeProcess()){                    
                    featureFile = tempPath + File.separator + "informed.mfc";               
                } else {                    
                    featureFile = featuresPath + File.separator + audio.getAudioName().substring(0, audio.getAudioName().length()-3)+"mfc";
                }
            
            }                   
            
            String comando = htkPath + File.separator + "HCopy -C " + configFile + " " + audioFile + " " + featureFile;
            ShellCommandExecutor.execute(comando, htkPath);            
              
            return null;
        } catch (Exception ex) {
            throw new HtkException(ex.getMessage());            
        }
    }  
    
    /**
     * Creates the config file for extract with htk(hcopy)
     * @param extraction the parameters of the extraction
     * @param path the path of the config file
     * @throws IOException
     */
    private void createConfigHtkFile(HtkParameters extraction, String configFile) throws IOException {
        
        BufferedWriter bwConfig = new BufferedWriter(new FileWriter(configFile));
        //extraction.getTypeMFCCvector();
        bwConfig.write("# Coding parameters \n");
        bwConfig.write("TARGETKIND = MFCC_0_D_A \n");
        //configFile.write("TARGETRATE = " + mfcc.100000.0 + "\n");
        double targetRate = extraction.getFrameDuration() - (extraction.getFrameDuration() * extraction.getOverlap());
        double htkScale = 10000;
        bwConfig.write("TARGETRATE = " + targetRate * htkScale + "\n");
        bwConfig.write("SAVECOMPRESSED = T \n");
        bwConfig.write("SAVEWITHCRC = T \n");
        bwConfig.write("SOURCEFORMAT = WAVE \n");
        bwConfig.write("ENORMALISE = T \n");
        bwConfig.write("WINDOWSIZE = " + new Double(extraction.getFrameDuration()) * htkScale + "\n");
        bwConfig.write("USEHAMMING = " + "T" + "\n");
        bwConfig.write("PREEMCOEF = " + extraction.getPreEmphasis() + "\n");
        bwConfig.write("NUMCHANS = " + 26 + "\n");
        bwConfig.write("CEPLIFTER = " + extraction.getNumMelFilters() + "\n");
        bwConfig.write("NUMCEPS = " + 12 + "\n");
        bwConfig.write("NONUMESCAPES = TRUE \n");
        bwConfig.flush();
        bwConfig.close();       
    }
    
}
