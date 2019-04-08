/**
 * Copyright 2011 Federal University of Pernambuco. 
 * All Rights Reserved. Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */ 
package br.ufpe.cin.five.api.recognizer;

import br.ufpe.cin.five.core.sample.Audio;

/**
 *
 * @author Alexandre
 */
public interface MicrophoneListener {

    public void onAudioDataComplete(Audio audio);

}
