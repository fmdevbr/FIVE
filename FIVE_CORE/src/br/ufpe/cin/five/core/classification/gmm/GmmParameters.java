/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.classification.gmm;

import br.ufpe.cin.five.core.classification.Classification;
import javax.persistence.Entity;

/**
 * This class contains the necessary parameters for the gmm classification:<br/>
 * iterations, components.
 */
@Entity
public class GmmParameters extends Classification {

    private int iterations;
    private int components;

    /**
     * Creates a new empty GmmParameters
     */    
    public GmmParameters() {
        super();
    }

    /**
     * Return the number of gmm iterations.
     * @return the number of gmm iterations.
     */
    public int getIterations() {
        return iterations;
    }

    /**
     * Sets the number of gmm iterations.
     * @param iterations the number of gmm iterations.
     */
    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    /**
     * Return the number of gmm components.
     * @return the number of gmm components.
     */    
    public int getComponents() {
        return components;
    }

    /**
     * Sets the number of gmm components.
     * @param iterations the number of gmm components.
     */    
    public void setComponents(int components) {
        this.components = components;
    }
}
