/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved. Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.api.synthesizer.vocoder;

import br.ufpe.cin.five.api.synthesizer.SynthesizerException;
import br.ufpe.cin.five.core.classification.hts.HtsProcess;
import java.io.File;

/**
 *
 * @author Alexandre
 */
public class Vocoder {   
    
    private String enginePath;    
    
    public Vocoder(String enginePath) throws SynthesizerException {        
        this.enginePath = enginePath;                       
    }
   
    public File vocode(String phrase, String speaker, String fileName) throws SynthesizerException {               
        try {
            File waveFile;
            
            HtsProcess htsProcess = new HtsProcess(enginePath);
            waveFile = htsProcess.test(phrase, speaker, fileName);

            return waveFile;            
        } catch (Exception ex) {
            throw new SynthesizerException(ex.getMessage());
        }
    }              
}
