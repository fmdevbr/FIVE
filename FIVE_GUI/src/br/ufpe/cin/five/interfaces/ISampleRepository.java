/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.interfaces;

import br.ufpe.cin.five.core.sample.Sample;
import br.ufpe.cin.five.core.sample.SampleException;
import br.ufpe.cin.five.core.sample.SampleFilter;
import java.util.ArrayList;

/**
 *
 * @author Alexandre
 */
public interface ISampleRepository {

    public abstract void insert(Sample sample) throws SampleException;
    public abstract void update(Sample sample) throws SampleException;
    public abstract void remove(Sample sample) throws SampleException;
    public abstract Sample search(String waveFile) throws SampleException;
    public abstract ArrayList<Sample> searchSample(SampleFilter filter) throws SampleException;
    
}
