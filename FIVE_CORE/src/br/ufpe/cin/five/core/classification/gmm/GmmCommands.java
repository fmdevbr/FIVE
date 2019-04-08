/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.classification.gmm;

import jMEF.BregmanSoftClustering;
import jMEF.MixtureModel;
import jMEF.MultivariateGaussian;
import jMEF.PVector;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

/**
 * This class contains all the commands for the gmm classification:<br/>
 * trainGMM, calclikehood , save and load
 */
public class GmmCommands implements Serializable {   

    private GmmParameters gmmParameters;

    public GmmParameters getGmmParameters() {
        return gmmParameters;
    }    
    
    public void setParameters(GmmParameters gmmParameters) {
        this.gmmParameters = gmmParameters;
    }
    
    /**
     * Train the gmm using the set of feature vectors
     * @param trainningSet the list of features vector
     * @return the gmm Model
     */
    public MixtureModel training(ArrayList<double[]> trainningSet) throws GmmException {        
        try {
            MixtureModel mm;
            PVector[] train = new PVector[trainningSet.size()];
            for (int i = 0; i < train.length; i++) {
                train[i] = new PVector(trainningSet.get(i).length);
                train[i].array = trainningSet.get(i);
            }
            Vector<PVector>[] clusters = KMeans.run(train, gmmParameters.getComponents());
            mm = BregmanSoftClustering.initialize(clusters, new MultivariateGaussian());
            mm = BregmanSoftClustering.run(train, mm, gmmParameters.getIterations());
            return mm;
        } catch (Exception ex) {
            throw new GmmException(ex.getMessage());
        }        
    }

    /**
     * Calc the false acceptance and the false rejection of a set of genuine and impostor features
     * @param genuineFeatures the genuine feature vectors
     * @param impostorFeatures the imposto feature vectors
     * @param model the gmm model
     * @return the GmmResult
     */
    public GmmResult testing(ArrayList<double[]> genuineFeatures, ArrayList<double[]> impostorFeatures, MixtureModel model) throws GmmException {
        try {
            GmmResult result = new GmmResult();
            
            double probabilidade = 0;
                    
            PVector[] genuineTest = new PVector[genuineFeatures.size()];
            for (int i = 0; i < genuineTest.length; i++) {
                genuineTest[i] = new PVector(genuineFeatures.get(i).length);
                genuineTest[i].array = genuineFeatures.get(i);
            }

            for (int i = 0; i < genuineTest.length; i++) {
                double probabilidade1 = Math.log10(model.density(genuineTest[i]));
                probabilidade = probabilidade + probabilidade1;
            }
            
            result.setFrr(probabilidade / genuineFeatures.size());
            
            PVector[] impostoTest = new PVector[impostorFeatures.size()];
            for (int i = 0; i < impostoTest.length; i++) {
                impostoTest[i] = new PVector(impostorFeatures.get(i).length);
                impostoTest[i].array = impostorFeatures.get(i);
            }

            for (int i = 0; i < impostoTest.length; i++) {
                double probabilidade1 = Math.log10(model.density(impostoTest[i]));
                probabilidade = probabilidade + probabilidade1;
            }
            
            result.setFar(probabilidade / impostorFeatures.size());
            
            return result;
            
        }catch (Exception ex) {
            throw new GmmException(ex.getMessage());
        }        
    }

    /**
     * Test a specific sample
     * @param testingSample the feature vector of a specific sample
     * @param model the svm Model
     * @return the sample classe
     */     
    public int testing(double[] testSample, MixtureModel model) throws GmmException {
        try {
            double probabilidade = 0;
                    
            PVector[] genuineTest = new PVector[1];
            for (int i = 0; i < genuineTest.length; i++) {
                genuineTest[i] = new PVector(testSample.length);
                genuineTest[i].array = testSample;
            }

            for (int i = 0; i < genuineTest.length; i++) {
                double probabilidade1 = Math.log10(model.density(genuineTest[i]));
                probabilidade = probabilidade + probabilidade1;
            }
            
            return (int) probabilidade;
                       
        }catch (Exception ex) {
            throw new GmmException(ex.getMessage());
        }            
    }    
    
    /**
     * Load an gmm
     * @param fileName the path of file
     * @return the gmm model
     * @throws GmmException
     */
    public MixtureModel load(String fileName) throws GmmException {              
        return MixtureModel.load(fileName);                
    }

    /**
     * Save the gmm
     * @param MixtureModel of the gmm
     * @param fileName the path of file
     * @throws GmmException
     */
    public void save(MixtureModel model, String fileName) throws GmmException {
        MixtureModel.save(model, fileName);
    }
}
