/**
 * Copyright 2011 Federal University of Pernambuco. 
 * All Rights Reserved. Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */ 
package br.ufpe.cin.five.api.recognizer;

import br.ufpe.cin.five.api.recognizer.decoder.Decoder;
import br.ufpe.cin.five.core.grammar.Grammar;
import br.ufpe.cin.five.core.sample.Audio;
import br.ufpe.cin.five.core.sample.SampleUtil;
import br.ufpe.cin.five.core.speaker.Speaker;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alexandre
 */
public class Recognizer implements MicrophoneListener {

    private Decoder decoder;
    private Microphone mic;

    public Recognizer(String enginePath) throws RecognizerException {
        this.decoder = new Decoder(enginePath);
        this.mic = new Microphone();
    }

    public void StartRecognition() {
        this.mic.setAudioFormat();
        this.mic.addMicrophoneListener(this);
        this.mic.start();
    }

    public void StopRecognition() {
        this.mic.removeMicrophoneListener(this);
        this.mic.stop();
    }

    public Grammar getGrammar() {
        return this.decoder.getGrammar();
    }

    public void SetRuleState(int id) throws RecognizerException {
        this.decoder.setRuleState(id);
    }

    public List<Speaker> getSpeakerSet() {
        return this.decoder.getSpeakerSet();
    }
    // Event Recognizer part...
    private List<RecognizerListener> listeners = new ArrayList<RecognizerListener>();

    public void addRecognizerListener(RecognizerListener listener) {
        this.listeners.add(listener);
    }

    public void removeRecognizerListener(RecognizerListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void onAudioDataComplete(Audio audio) {
        if (audio.getAudioData().length > 0) {
            try {               
                Result result = decoder.decode(audio);
                this.notifyRecognizerListeners(result);
            } catch (Exception ex) {
                this.notifyRecognizerListenersException(ex);
            }
        }
    }

    private void notifyRecognizerListeners(Result result) {
        for (RecognizerListener listener : this.listeners) {
            listener.onRecognition(result);
        }
    }    
    
    public void notifyRecognizerListenersException(Exception ex) {
        for (RecognizerListener listener : this.listeners) {
            listener.onException(ex);
        }
    }

    public Result recognize(File audioFile) throws RecognizerException {
        Result result = null;
        try {
            Audio audio = SampleUtil.waveRead(audioFile);
            result = decoder.decode(audio);
        } catch (Exception ex) {
            throw new RecognizerException(ex.getMessage());
        }
        return result;
    }
}
