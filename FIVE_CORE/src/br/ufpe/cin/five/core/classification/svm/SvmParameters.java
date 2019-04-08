/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.classification.svm;

import br.ufpe.cin.five.core.classification.Classification;
import javax.persistence.Entity;


/**
 * This class contains the necessary parameters for the svm classification:<br/>
 * kernel type, cost, gama and degree.
 */
@Entity
public class SvmParameters extends Classification {

    private int kernelType;
    private int cost;
    private int gamma;
    private int degree;

    /**
     * Creates a new empty SvmParameters
     */    
    public SvmParameters() {
        super();
    }
    
     /**
     * Return the cost value of svm problem.
     * @return the cost value of svm problem.
     */
    public int getCost() {
        return cost;
    }

    /**
     * Sets the cost value of svm problem.
     * @param cost the cost value of svm problem.
     */    
    public void setCost(int cost) {
        this.cost = cost;
    }

     /**
     * Return the degree value of svm problem.
     * @return the degree value of svm problem.
     */
    public int getDegree() {
        return degree;
    }

    /**
     * Sets the degree value of svm problem.
     * @param degree the degree value of svm problem.
     */    
    
    public void setDegree(int degree) {
        this.degree = degree;
    }

     /**
     * Return the gamma value of svm problem.
     * @return the gamma value of svm problem.
     */

    public int getGamma() {
        return gamma;
    }

    /**
     * Sets the gamma value of svm problem.
     * @param gamma the gamma value of svm problem.
     */    
    
    public void setGamma(int gamma) {
        this.gamma = gamma;
    }

     /**
     * Return the kernel type of svm problem.
     * @return the kernel type of svm problem.
     */    
    public int getKernelType() {
        return kernelType;
    }

    /**
     * Sets the kernel type of svm problem.
     * @param cost the kernel type of svm problem.
     */    
    
    public void setKernelType(int kernelType) {
        this.kernelType = kernelType;
    }    
}
