/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved. Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.util;

import java.io.*;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 *
 * @author Alexandre
 */
public class WaveWriter {

    private File audioFile;
    private AudioFormat audioFormat;

    public WaveWriter(File audioFile, AudioFormat audioFormat) {
        this.audioFile = audioFile;
        this.audioFormat = audioFormat;
    }

    public WaveWriter(String audioPath, AudioFormat audioFormat) {
        this.audioFile = new File(audioPath);
        this.audioFormat = audioFormat;
    }

    public void write(byte[] audioData) throws FileNotFoundException, IOException {
        InputStream byteArrayInputStream = new ByteArrayInputStream(audioData);
        AudioInputStream audioInputStream = new AudioInputStream(byteArrayInputStream,
                audioFormat, audioData.length / audioFormat.getFrameSize());

        FileOutputStream fos = new FileOutputStream(audioFile.getPath());
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, fos);
        fos.close();
    }

    public void write(short[] audioData) throws FileNotFoundException, IOException {
        byte sampleByte[] = convertAudioShortsToBytes(audioData);
        InputStream byteArrayInputStream = new ByteArrayInputStream(sampleByte);
        AudioInputStream audioInputStream = new AudioInputStream(byteArrayInputStream,
                audioFormat, sampleByte.length / audioFormat.getFrameSize());
        FileOutputStream fos = new FileOutputStream(audioFile.getPath());
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, fos);
        fos.close();
    }

    private byte[] convertAudioShortsToBytes(short[] audioData) {
        byte sampleByte[] = null;
        if (audioFormat.getSampleSizeInBits() == 16) {
            sampleByte = new byte[audioData.length * 2];
            for (int c = 0; c < audioData.length; c++) {
                sampleByte[2 * c] = (byte) audioData[c];
                sampleByte[2 * c + 1] = (byte) (audioData[c] >> 8);
            }
        } else {
            sampleByte = new byte[audioData.length];
            if (audioFormat.getEncoding().toString().equalsIgnoreCase("pcm_signed")) {
                for (int c = 0; c < audioData.length; c++) {
                    sampleByte[c] = (byte) audioData[c];
                }
            } else {
            }
        }
        return sampleByte;
    }
}
