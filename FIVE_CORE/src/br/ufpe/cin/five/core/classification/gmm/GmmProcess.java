/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.classification.gmm;

import br.ufpe.cin.five.core.classification.ClassificationException;
import br.ufpe.cin.five.core.classification.ClassificationProcess;
import br.ufpe.cin.five.core.classification.ClassificationUtil;
import br.ufpe.cin.five.core.sample.Sample;
import jMEF.MixtureModel;
import java.io.File;
import java.util.ArrayList;

/**
 * This an extended class extends that implements all methods necessary for a GMM process.
 */
public class GmmProcess extends ClassificationProcess {

    private String featuresPath;
    private String modelsPath;
    
    /**
     * Creates a new empty GmmProcess
     */    
    public GmmProcess(){}
    
    /**
     * Creates a new GmmProcess specifying the projectPath.
     * @param projectPath
     */  
    public GmmProcess (String projectPath) {
        this.featuresPath = projectPath + File.separator + "features";
        this.modelsPath = projectPath + File.separator + "models";
    }    
    
    /**
     * Implements a train process on a Gmm process.
     * @param params the parameters necessary for the trainning process.
     * @param trainningSamples the samples necessary for the trainning process.
     */
    @Override
    public void train(Object params, ArrayList<Sample> trainningSamples) throws ClassificationException {                       
        
        try {
            GmmCommands gmmCommands = new GmmCommands();
            
            System.out.println("---- CARREGANDO PARÂMETROS -----");                        
            
            GmmParameters gmmParameters = (GmmParameters) params;                                                    
            
            gmmCommands.setParameters(gmmParameters);
            
            System.out.println("---- PREPARANDO CONJUNTO DE AMOSTRAS PARA TREINAMENTO -----");                        
            
            ArrayList<double[]> trainingSet = new ArrayList<double[]>();        
            ArrayList<Integer> trainingClass = new ArrayList<Integer>();
            
            for (Sample sample : trainningSamples) {
                trainingSet.add(ClassificationUtil.loadFeatureFile(featuresPath + File.separator+ sample.getFeatureFile()));
                trainingClass.add(sample.getSpeaker().getId());            
            }          
            
            System.out.println("---- INICIANDO O TREINO -----");                                                        

            MixtureModel mm = gmmCommands.training(trainingSet);

            System.out.println("---- SALVANDO MODELOS -----");
            String modelFileName = modelsPath + File.separator + "models_"+trainingClass.get(0);

            gmmCommands.save(mm, modelFileName);                   
                                                                                          
        } catch (Exception ex) {
            throw new GmmException(ex.getMessage());
        }                  
    }    

     /**
     * Implements a test process on a Gmm process.
     * @param params the parameters necessary for the trainning process.
     * @param testSamples the samples necessary for the test process.
     * @return the Gmm result.
     */      
    @Override
    public GmmResult test(Object params, ArrayList<Sample> testSamples) throws ClassificationException {
        try {
            GmmCommands gmmCommands = new GmmCommands();
            
            System.out.println("---- CARREGANDO PARÂMETROS -----");                        
            
            GmmParameters gmmParameters = (GmmParameters) params;          
            
            gmmCommands.setParameters(gmmParameters);            
                       
            System.out.println("---- PREPARANDO CONJUNTO DE AMOSTRAS PARA TESTE -----");                                    
            
            ArrayList<Sample> genuineTestingSamples = new ArrayList<Sample>();
            ArrayList<Sample> impostorTestingSamples = new ArrayList<Sample>();
            
            for (Sample sample : testSamples) {
                if(sample.isGenuine()) {
                    genuineTestingSamples.add(sample);
                } else {
                    impostorTestingSamples.add(sample);
                }
            }
                               
            System.out.println("---- INICIANDO O TESTE -----");
            
            String modelFileName = modelsPath + File.separator + "models_"+genuineTestingSamples.get(0).getSpeaker().getId();
            MixtureModel model = gmmCommands.load(modelFileName);
            
            ArrayList<double[]> genuineFeatures = new ArrayList<double[]>();
            for (Sample sample : genuineTestingSamples) {
                genuineFeatures.add(ClassificationUtil.loadFeatureFile(featuresPath + File.separator + sample.getFeatureFile()));
            }
               
            ArrayList<double[]> impostorFeatures = new ArrayList<double[]>();
            for (Sample sample : genuineTestingSamples) {
                impostorFeatures.add(ClassificationUtil.loadFeatureFile(featuresPath + File.separator + sample.getFeatureFile()));
            }  
            
            return gmmCommands.testing(genuineFeatures, impostorFeatures, model);                       

        }catch (Exception ex) {
            throw new GmmException(ex.getMessage());
        }  
    }

     /**
     * Implements a test in a single sample.
     * @param feature the feature vector of a test sample.
     * @param model the obtained model in trainning process.
     * @return the sample' id.
     */     
    @Override
    public Object test(double[] feature, String modelPath) throws ClassificationException {
              
        GmmCommands gmmCommands = new GmmCommands();                    
        MixtureModel model = gmmCommands.load(modelPath);
               
        try {
            int probabilidade = gmmCommands.testing(feature, model);
        
            return probabilidade;
            
        } catch (GmmException ex){
            throw new ClassificationException(ex.getMessage());
        }
    }    
}
