/**
 * Copyright 2011 Federal University of Pernambuco. All Rights Reserved. Use is
 * subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.api.synthesizer;

import br.ufpe.cin.five.api.synthesizer.vocoder.PlayThread;
import br.ufpe.cin.five.api.synthesizer.vocoder.Vocoder;
import java.io.File;
import javax.sound.sampled.*;

/**
 *
 * @author Alexandre
 */
public class Synthesizer {

    private Vocoder vocoder;

    public Synthesizer(String enginePath) throws SynthesizerException {
        this.vocoder = new Vocoder(enginePath);
    }

      public File synthesize(String phrase, String speaker, String fileName, 
        String engineType ) throws SynthesizerException {
        File audioFile = null;
        try {      
                 audioFile = vocoder.vocode(phrase, speaker, fileName, engineType);
        } catch (Exception ex) {
            throw new SynthesizerException(ex.getMessage());
        }
        return audioFile;
    }    
    
 
   

    public void play(File waveFile) throws SynthesizerException {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(waveFile);
            AudioFormat audioFormat = audioInputStream.getFormat();

            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);

            SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            new PlayThread(audioFormat, audioInputStream, sourceDataLine).start();

        } catch (Exception ex) {
            throw new SynthesizerException(ex.getMessage());
        }
    }

}
