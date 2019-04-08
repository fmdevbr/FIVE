/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.util;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author Alexandre
 */
public class WaveReader {

    private byte[] audioData;
    private AudioFormat audioFormat;

    public WaveReader(File audioFile) throws UnsupportedAudioFileException, IOException {
        openWaveFile(audioFile);
    }

    public WaveReader(String audioPath) throws UnsupportedAudioFileException, IOException {
        File audioFile = new File(audioPath);
        openWaveFile(audioFile);
    }

    private void openWaveFile(File audioFile) throws UnsupportedAudioFileException, IOException {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
        audioFormat = audioInputStream.getFormat();
        long frameLength = audioInputStream.getFrameLength();
        audioData = new byte[(int) (frameLength * audioFormat.getFrameSize())];
        audioInputStream.read(audioData);
    }

    public byte[] getAudioDataInBytes() {
        return audioData;
    }

    public short[] getAudioDataInShorts() {
        return convertAudioDataBytesToShort(audioData, audioFormat);
    }    

    public AudioFormat getAudioFormat() {
        return audioFormat;
    }

    public static short[] convertAudioDataBytesToShort(byte[] audioBytes, AudioFormat format) {
        short[] audioData = null;
        if (format.getSampleSizeInBits() == 16) {
            int nlengthInSamples = audioBytes.length / 2;
            audioData = new short[nlengthInSamples];

            if (format.isBigEndian()) {
                for (int i = 0; i < nlengthInSamples; i++) {
                    /* First byte is MSB (high order) */
                    short MSB = (short) audioBytes[2 * i];
                    /* Second byte is LSB (low order) */
                    short LSB = (short) audioBytes[2 * i + 1];
                    audioData[i] = (short) (MSB << 8 | (255 & LSB));
                }
            } else {
                for (int i = 0; i < nlengthInSamples; i++) {
                    /* First byte is LSB (low order) */
                    int LSB = (short) audioBytes[2 * i];
                    /* Second byte is MSB (high order) */
                    int MSB = (short) audioBytes[2 * i + 1];
                    audioData[i] = (short) (MSB << 8 | (255 & LSB));
                }
            }

        } else if (format.getSampleSizeInBits() == 8) {

            int nlengthInSamples = audioBytes.length;
            audioData = new short[nlengthInSamples];
            //No antigo estava "PCM_SIGN" mudei para "pcm_sing"
            if (format.getEncoding().toString().startsWith("PCM_SIGN")) {
                for (int i = 0; i < audioBytes.length; i++) {
                    audioData[i] = audioBytes[i];
                }
            } else {
                for (int i = 0; i < audioBytes.length; i++) {
                    audioData[i] = (short) (audioBytes[i] - 128);
                }
            }
        }
        return audioData;
    }
}
