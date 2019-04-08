/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved. Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.api.synthesizer.vocoder;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;

/**
 *
 * @author Alexandre
 */
public class PlayThread extends Thread {

    private AudioFormat audioFormat;
    private AudioInputStream audioInputStream;
    private SourceDataLine sourceDataLine;

    byte tempBuffer[] = new byte[10000];

    public PlayThread(AudioFormat audioFormat, AudioInputStream audioInputStream, SourceDataLine sourceDataLine) {
        this.audioFormat = audioFormat;
        this.audioInputStream = audioInputStream;
        this.sourceDataLine = sourceDataLine;
    }

    public void run() {
        try {
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();

            int cnt;
            while ((cnt = audioInputStream.read(tempBuffer, 0, tempBuffer.length)) != -1) {
                if (cnt > 0) {
                    sourceDataLine.write(tempBuffer, 0, cnt);
                }
            }

            sourceDataLine.drain();
            sourceDataLine.close();

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
