/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.classification.svm;

import br.ufpe.cin.five.core.classification.ClassificationException;
import br.ufpe.cin.five.core.classification.ClassificationProcess;
import br.ufpe.cin.five.core.classification.ClassificationUtil;
import br.ufpe.cin.five.core.sample.Sample;
import java.io.File;
import java.util.ArrayList;
import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;

/**
 * This an extended class extends that implements all methods necessary for a SVM process.
 */
public class SvmProcess extends ClassificationProcess {
    
    private String featuresPath;
    private String modelsPath;
    
    /**
     * Creates a new empty SvmProcess
     */     
    public SvmProcess(){}
    
    /**
     * Creates a new SvmProcess specifying the projectPath.
     * @param projectPath
     */    
    public SvmProcess (String projectPath) {
        this.featuresPath = projectPath + File.separator + "features";
        this.modelsPath = projectPath + File.separator + "models";
    }

    /**
     * Implements a train process on a Svm process.
     * @param params the parameters necessary for the trainning process.
     * @param trainningSamples the samples necessary for the trainning process.
     */    
    @Override
    public void train(Object params, ArrayList<Sample> trainningSamples) throws ClassificationException {
        
        try {
            System.out.println("-- TREINAMENTO --\n");
            
            SvmCommands svmCommands = new SvmCommands();
            
            System.out.println("Carregando parametros...");                        
            
            SvmParameters svmParameters = (SvmParameters) params;                   

            svm_parameter libSvmParameters = new svm_parameter();                           
            libSvmParameters.kernel_type = svmParameters.getKernelType();
            libSvmParameters.degree = svmParameters.getDegree();
            libSvmParameters.gamma = Math.pow(2, svmParameters.getGamma());
            libSvmParameters.coef0 = Math.pow(2, svmParameters.getCost());            
            
            svmCommands.setParameters(libSvmParameters);            
            
            System.out.println("Preparando amostras...");                        
            
            ArrayList<double[]> trainingSet = new ArrayList<double[]>();        
            ArrayList<Integer> trainingClass = new ArrayList<Integer>();

            for (Sample sample : trainningSamples) {
                String featureFile = featuresPath + File.separator + sample.getFeatureFile();
                trainingSet.add(ClassificationUtil.loadFeatureFile(featureFile));
                trainingClass.add(sample.getUtterance().getId());
            }
                                   
            System.out.println("Treinando...");                        
            svm_model model = svmCommands.trainning(trainingSet, trainingClass);                       

            System.out.println("Salvando modelos...");
            String modelFileName = modelsPath + File.separator + "models";

            svmCommands.save(modelFileName, model);                           
            
            System.out.println("");
            
        } catch (Exception ex) {
            throw new SvmException(ex.getMessage());
        }        
    }
    
     /**
     * Implements a test process on a Svm process.
     * @param params the parameters necessary for the trainning process.
     * @param testSamples the samples necessary for the test process.
     * @return the Svm result.
     */    
    @Override
    public SvmResult test(Object params, ArrayList<Sample> testSamples) throws ClassificationException {
        
        try {
            System.out.println("-- TESTE --\n");
            
            SvmCommands svmCommands = new SvmCommands();
            
            System.out.println("Carregando parametros...");                             
            
            SvmParameters svmParameters = (SvmParameters) params;
                   
            svm_parameter svm_param = new svm_parameter();                           
            svm_param.kernel_type = svmParameters.getKernelType();
            svm_param.degree = svmParameters.getDegree();
            svm_param.gamma = Math.pow(2, svmParameters.getGamma());
            svm_param.coef0 = Math.pow(2, svmParameters.getCost());            
            
            svmCommands.setParameters(svm_param); 
                        
            System.out.println("Preparando amostras...");      
            
            ArrayList<double[]> testingSet = new ArrayList<double[]>();
            ArrayList<Integer> testingClass = new ArrayList<Integer>();
        
            for (Sample sample : testSamples) {
                String featureFile = featuresPath + File.separator + sample.getFeatureFile();
                testingSet.add(ClassificationUtil.loadFeatureFile(featureFile));
                testingClass.add(sample.getUtterance().getId());
            }
            
            System.out.println("Testando...");     

            String modelFileName = modelsPath + File.separator + "models";
            svm_model model = svmCommands.load(modelFileName);
        
            System.out.println("");
            
            return svmCommands.testing(testingSet, testingClass, model);                        
        
        } catch(Exception ex) {
            throw new SvmException(ex.getMessage());
        }            
    }

     /**
     * Implements a test in a single sample.
     * @param feature the feature vector of a test sample.
     * @param modelPath the obtained model in trainning process.
     * @return the sample' id.
     */     
    @Override
    public Object test(double[] feature, String modelPath) throws ClassificationException {

        SvmCommands svmCommands = new SvmCommands();
        svm_model model = svmCommands.load(modelPath);
        
        int predict_probability = 0;

        int v;

        if (predict_probability == 1) {
            if (svm.svm_check_probability_model(model) == 0) {
                System.err.print("Model does not support probabiliy estimates\n");
                System.exit(1);
            }
        } else {
            if (svm.svm_check_probability_model(model) != 0) {
                System.out.print("Model supports probability estimates, but disabled in prediction.\n");
            }
        }

        int svm_type = svm.svm_get_svm_type(model);
        int nr_class = svm.svm_get_nr_class(model);
        double[] prob_estimates = null;

        if (predict_probability == 1) {
            if (svm_type == svm_parameter.EPSILON_SVR || svm_type == svm_parameter.NU_SVR) {
                System.out.print("Prob. model for test data: target value = predicted value + z,\nz: Laplace distribution e^(-|z|/sigma)/(2sigma),sigma=" + svm.svm_get_svr_probability(model) + "\n");
            } else {
                int[] labels = new int[nr_class];
                svm.svm_get_labels(model, labels);
                prob_estimates = new double[nr_class];
                System.out.print("labels");
                for (int j = 0; j < nr_class; j++) {
                    System.out.print(" " + labels[j]);
                }
                System.out.print("\n");
            }
        }        
        
        int m = feature.length;
        svm_node[] x = new svm_node[m];
        for (int j = 0; j < m; j++) {
            x[j] = new svm_node();
            x[j].index = j;
            x[j].value = feature[j];
        }

        if (predict_probability == 1 && (svm_type == svm_parameter.C_SVC || svm_type == svm_parameter.NU_SVC)) {
            v = (int) svm.svm_predict_probability(model, x, prob_estimates);
        } else {
            v = (int) svm.svm_predict(model, x);
        }
        return v;                        
    }
}
