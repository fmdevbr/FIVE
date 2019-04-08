/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved. Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.sample;

import br.ufpe.cin.five.core.extraction.VAD;
import br.ufpe.cin.five.core.project.Project;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * This is an abstract class that specifies and implements the main methods necessary for the sample process.
 */
public abstract class SampleProcess {

    /**
     * Calculates the signal noise ralation of a specific sample.
     * @param project
     * @param audioFile
     */    
    public static int calculateSNR(Project project, String audioFile) throws SampleException {
       /* try {
            Audio audio = SampleUtil.waveRead(new File(project.getDirectory() + File.separator + "samples" + File.separator + audioFile));
            
            short noise[] = audio.getAudioData();
            
            short[] result = VAD.absCut(noise);
            
            short speech[] = result[0];
            noise = result[1];
           /* int endSpeech = 0;
            int startSpeech = 0;
            short[] noise = new short[audioData.length - (endSpeech - startSpeech)];
            short[] speech = new short[(endSpeech - startSpeech)];
            int index = 0;
            if (noise.length == 0) {
                return Double.NaN;
            }
            for (int i = 0; i < noise.length; i++) {
                if (i <= startSpeech) {
                    noise[i] = audioData[i];
                    index = 0;
                } else {
                    noise[i] = audioData[endSpeech + index];
                    index++;
                }
            }
            for (int i = 0; i < speech.length; i++) {
                speech[i] = audioData[startSpeech + i];
            }*/ 
      /*      double Pn = power(noise);
            double Ps = power(speech); 
            //double Pc = Pn - Ps;
            //Pc = power(sample) - Pn;
            double snr = 10 * Math.log10((Ps / Pn));
            if(snr <0)
                Pn = Pn;
            return (int)snr;
        } catch (UnsupportedAudioFileException ex) {
            throw new SampleException(ex.getMessage());
        } catch (IOException ex) {
            throw new SampleException(ex.getMessage());
        }*/
        return 0;
    }

    /**
     * Auxiliary method for signal power calculation.
     * @param sample
     */     
    private static double power(short[] sample) {
        double power = 0;
        for (int i = 0; i < sample.length; i++) {
            power = power + sample[i] * sample[i];
        }
        return power / sample.length;
    }    
}
