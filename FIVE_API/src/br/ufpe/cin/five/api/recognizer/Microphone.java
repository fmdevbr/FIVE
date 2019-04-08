/**
 * Copyright 2011 Federal University of Pernambuco. 
 * All Rights Reserved. Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.api.recognizer;

import br.ufpe.cin.five.core.extraction.ExtractionUtil;
import br.ufpe.cin.five.core.sample.Audio;
import br.ufpe.cin.five.core.sample.SampleUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import javax.sound.sampled.*;

/**
 *
 * @author Alexandre
 */
public class Microphone implements Runnable {

    private ByteArrayOutputStream audioByteStream;
    private boolean audioDataComplete;
    private AudioFormat audioFormat;
    private TargetDataLine line;
    private Thread thread;
    public static boolean isCancelled = false;
    private List<MicrophoneListener> listeners = new ArrayList<MicrophoneListener>();
    int pauseMax;

    public Microphone() {
        this.audioByteStream = new ByteArrayOutputStream();
        pauseMax = 15;
    }

    public void addMicrophoneListener(MicrophoneListener listener) {
        this.listeners.add(listener);
    }

    public void removeMicrophoneListener(MicrophoneListener listener) {
        this.listeners.remove(listener);
    }

    private void notifyMicrophoneListeners(Audio audio) {
        for (MicrophoneListener listener : this.listeners) {
            listener.onAudioDataComplete(audio);
        }
    }

    public void start() {
        Microphone.isCancelled = false;
        thread = new Thread(this);
        thread.setName("Capture");
        thread.start();
    }

    public void stop() {
        Microphone.isCancelled = true;
        thread = null;
    }

    @Override
    public void run() {

        System.out.println("Microfone começou a reconhecer: ");
        int linha = 0;

        double lnE;
        double lnEmax;
        double lnEmin;
        double deltaE;

        double t = 0.99;

        int countPico = 0;
        int countFala = 0;
        int countPausa = 0;
        int countSil = 0;

        int speechMax = 50;


        String state = "sil";

        // define the required attributes for our line,
        // and make sure a compatible line is supported.

        AudioFormat format = this.getAudioFormat();
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        if (!AudioSystem.isLineSupported(info)) {
            return;
        }

        // get and open the target data line for capture.

        try {
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format, line.getBufferSize());
        } catch (LineUnavailableException ex) {
            return;
        } catch (SecurityException ex) {
            return;
        } catch (Exception ex) {
            return;
        }

        int numBytesRead = ExtractionUtil.calcFrameLength(audioFormat.getSampleRate(), 30);
        byte[] data = new byte[numBytesRead];
        Queue<byte[]> queue = new ArrayDeque<byte[]>();

        line.start();
        short[] audioFrame;

        line.read(data, 0, numBytesRead);

        audioFrame = SampleUtil.convertAudioDataBytesToShort(data, format);
        lnE = energy(audioFrame);

        lnEmax = lnE;
        lnEmin = lnE;

        while (isCancelled != true) {
            try {
                line.read(data, 0, numBytesRead);
                audioFrame = SampleUtil.convertAudioDataBytesToShort(data, format);
                lnE = energy(audioFrame);

                if (lnE >= lnEmax) {
                    lnEmax = lnE;
                } else {
                    lnEmax = (t * lnEmax) + ((1 - t) * lnE);
                }

                if (lnE <= lnEmin) {
                    lnEmin = lnE;
                } else {
                    lnEmin = (t * lnEmin) + ((1 - t) * lnE);
                }

                deltaE = lnEmax - lnEmin;

                if (state.equals("sil")) {

                    if (countSil < 100) {
                        countSil++;
                        queue.add(data);
                    } else {
                        countSil = 100;
                        queue.poll();
                        queue.add(data);
                    }

                    if ((lnEmax - lnE) < (deltaE * 0.5)) {
                        state = "pico";

                        for (int i = countSil - 1; i == 0; i--) {

                            audioByteStream.write(queue.poll());

                        }
                    } else {
                        state = "sil";
                    }

                    System.out.println(linha++ + " - " + state);

                } else if (state.equals("pico")) {

                    audioByteStream.write(data);
                    countPico++;

                    if ((lnE - lnEmin) < 0.8) {
                        audioByteStream.reset();
                        countSil = 0;
                        countPico = 0;
                        state = "sil";
                    } else if (countPico > 5) {
                        countPico = 0;
                        state = "fala";
                    } else {
                        state = "pico";
                    }

                    System.out.println(linha++ + " - " + state);

                } else if (state.equals("fala")) {

                    audioByteStream.write(data);
                    countFala++;

                    if ((lnE - lnEmin) < 0.8) {
                        state = "pausa";
                    } else if (countFala >= speechMax) {
                        audioByteStream.reset();
                        countFala = 0;
                        countSil = 0;
                        state = "sil";
                    } else {
                        state = "fala";
                    }

                    System.out.println(linha++ + " - " + state);

                } else if (state.equals("pausa")) {

                    countPausa++;

                    audioByteStream.write(data);

                    if ((lnEmax - lnE) < (0.5 * deltaE)) {
                        state = "fala";
                        countPausa = 0;
                    } else if (countPausa >= pauseMax) {
                        countPausa = 0;
                        Audio audio = new Audio(audioFormat);
                        // Escreve o áudio capturado pelo microfone
                        /*
                        byte[] audioData = audioByteStream.toByteArray();
                        byte[] audioToWrite = new byte[audioData.length - (audioData.length * countPausa)];
                        System.arraycopy(audioData, 0, audioToWrite, 0, audioToWrite.length);
                        WaveWriter writer = new WaveWriter(new File("/home/vocallab-pablo/TesteMic.wav"), audioFormat);
                        writer.write(audioToWrite);
                        System.out.println("Escrito com sucesso em: " + "/home/vocallab-pablo/TesteMic.wav");
                        * */                        
                        // fim
                        audio.setAudioData(SampleUtil.convertAudioDataBytesToShort(audioByteStream.toByteArray(), format));
                        this.notifyMicrophoneListeners(audio);
                        audioByteStream.reset();
                        countSil = 0;
                        state = "sil";

                    } else {
                        state = "pausa";
                    }

                    System.out.println(linha++ + " - " + state);
                }
            } catch (IOException ex) {
                return;
            }
        }

        // we reached the end of the stream.  stop and close the line.
        line.stop();
        line.close();
        line = null;

        thread = null;
    }

    public AudioFormat getAudioFormat() {
        return audioFormat;
    }

    public void setAudioFormat() {
        audioFormat = new AudioFormat(8000, 8, 1, true, false);
    }

    public boolean isAudioDataComplete() {
        return audioDataComplete;
    }

    public void setAudioDataComplete(boolean audioDataComplete) {
        this.audioDataComplete = audioDataComplete;
    }

    private double energy(short[] inputSignal) {
        double d = 0;
        for (int i = 1; i < inputSignal.length; i++) {
            d += (inputSignal[i] * inputSignal[i]);
        }
        return d / inputSignal.length;
    }

    public void setPauseThreshold(int pauseThreshold) {
        pauseMax = pauseThreshold;
    }
}
