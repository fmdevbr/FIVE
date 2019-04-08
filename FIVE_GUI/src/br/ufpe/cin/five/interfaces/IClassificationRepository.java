/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.interfaces;

import br.ufpe.cin.five.core.classification.Classification;
import br.ufpe.cin.five.core.classification.ClassificationException;

/**
 *
 * @author Alexandre
 */
public interface IClassificationRepository {

    public abstract void insert(Classification classification) throws ClassificationException;
    public abstract void update(Classification classification) throws ClassificationException;
    public abstract void remove(Classification classification) throws ClassificationException;
    public abstract Classification search(int id) throws ClassificationException;
    public abstract Classification search(boolean active) throws ClassificationException;
    
}
