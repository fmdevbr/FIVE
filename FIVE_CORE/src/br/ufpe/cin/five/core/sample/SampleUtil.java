/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved. Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.sample;

import java.io.*;
import javax.sound.sampled.*;

/**
 * This class provides a set of useful methods for sample process.
 */
public class SampleUtil {

    public static Audio waveRead(File audioFile) throws UnsupportedAudioFileException, IOException {
        
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
        AudioFormat audioFormat = audioInputStream.getFormat();
        long frameLength = audioInputStream.getFrameLength();
        byte[] audioData = new byte[(int) (frameLength * audioFormat.getFrameSize())];
        audioInputStream.read(audioData);
        
        Audio audio = new Audio();
        audio.setAudioFormat(audioInputStream.getFormat());
        audio.setAudioData(convertAudioDataBytesToShort(audioData, audioFormat));
        audio.setAudioName(audioFile.getName());
        audio.setAudioPath(audioFile.getParent());
        
        return audio;
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
    
    public static void waveWrite(Audio audio) throws SampleException {
        try {
            byte[] audioData = convertAudioShortsToBytes(audio.getAudioData(), audio.getAudioFormat());
            InputStream byteArrayInputStream = new ByteArrayInputStream(audioData);
            AudioInputStream audioInputStream = new AudioInputStream(byteArrayInputStream,
                    audio.getAudioFormat(), audioData.length / audio.getAudioFormat().getFrameSize());
            FileOutputStream fos = new FileOutputStream(audio.getAudioName());
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, fos);
            fos.close();
        } catch (Exception ex){
            throw new SampleException(ex.getMessage());
        }
    }
    
    public static byte[] convertAudioShortsToBytes(short[] audioData, AudioFormat audioFormat) {
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
