/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.interfaces;

import br.ufpe.cin.five.core.extraction.Extraction;
import br.ufpe.cin.five.core.extraction.ExtractionException;

/**
 *
 * @author Alexandre
 */
public interface IExtractionRepository {

    public abstract void insert(Extraction extraction) throws ExtractionException;
    public abstract void update(Extraction extraction) throws ExtractionException;
    public abstract void remove(Extraction extraction) throws ExtractionException;
    public abstract Extraction search(int id) throws ExtractionException;
    public abstract Extraction search(boolean active) throws ExtractionException;
    
}
