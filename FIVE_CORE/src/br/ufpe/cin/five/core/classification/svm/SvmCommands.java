/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.classification.svm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;
import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

/**
 *
 * @author Alexandre
 */
public class SvmCommands {
    
    private svm_parameter param;
    
    public svm_parameter getParameters() {
        return param;
    }
    
    public void setParameters(svm_parameter params) {
        param = new svm_parameter();
        // default values
        param.svm_type = svm_parameter.C_SVC;
        //param.kernel_type = svm_parameter.RBF;
        param.nu = 0.5;
        param.cache_size = 100;
        param.C = 1;
        param.eps = 1e-3;
        param.p = 0.1;
        param.shrinking = 1;
        param.probability = 0;
        param.nr_weight = 0;
        param.weight_label = new int[0];
        param.weight = new double[0];
        
        if (params.svm_type != 0) {
            param.svm_type = params.svm_type;
        }
        
        if (params.kernel_type != 0) {
            param.kernel_type = params.kernel_type;
        }
        
        if (params.degree != 0) {
            param.degree = params.degree;
        }
        
        if (params.gamma != 0) {
            param.gamma = params.gamma;
        }
        
        if (params.coef0 != 0) {
            param.coef0 = params.coef0;
        }
        
        if (params.nu != 0) {
            param.nu = params.nu;
        }
        
        if (params.cache_size != 0) {
            param.cache_size = params.cache_size;
        }
        
        if (params.C != 0) {
            param.C = params.C;
        }
        
        if (params.eps != 0) {
            param.eps = params.eps;
        }
        
        if (params.p != 0) {
            param.p = params.p;
        }
        
        if (params.shrinking != 0) {
            param.shrinking = params.shrinking;
        }
        
        if (params.probability != 0) {
            param.probability = params.probability;
        }
    }

    /**
     * Train the svm using the set of feature vectors and the set of classes
     * @param trainingSet the list of features vector
     * @param trainingClass the list of classes
     * @return the svm Model
     */    
    public svm_model trainning(ArrayList<double[]> trainingSet, ArrayList<Integer> trainingClass) throws SvmException {
        try {
            Vector<Integer> vy = new Vector<Integer>();
            Vector<svm_node[]> vx = new Vector<svm_node[]>();
            int max_index = 0;
            
            for (int i = 0; i < trainingSet.size(); i++) {
                
                vy.addElement(trainingClass.get(i));
                
                double[] trainingValues = trainingSet.get(i);
                
                int m = trainingSet.get(i).length;
                svm_node[] x = new svm_node[m];
                for (int j = 0; j < m; j++) {
                    x[j] = new svm_node();
                    x[j].index = j;
                    x[j].value = trainingValues[j];
                }
                if (m > 0) {
                    max_index = Math.max(max_index, x[m - 1].index);
                }
                vx.addElement(x);
            }
            
            svm_problem prob = new svm_problem();
            prob.l = vy.size();
            prob.x = new svm_node[prob.l][];
            for (int i = 0; i < prob.l; i++) {
                prob.x[i] = vx.elementAt(i);
            }
            prob.y = new double[prob.l];
            for (int i = 0; i < prob.l; i++) {
                prob.y[i] = vy.elementAt(i);
            }
            
            if (param.gamma == 0 && max_index > 0) {
                param.gamma = 1.0 / max_index;
            }
            
            String error_msg = svm.svm_check_parameter(prob, param);
            
            if (error_msg != null) {
                System.err.print("Error: " + error_msg + "\n");
                System.exit(1);
            }            
            return svm.svm_train(prob, param);            
        } catch (Exception ex) {
            throw new SvmException(ex.getMessage());
        }        
    }
    
    /**
     * Calc the general erros and the confusionMatrix of a set of test features
     * @param testingSet the list of features vector
     * @param testingClass the list of classes
     * @param model the svm Model
     * @return the SvmResult
     */    
    public SvmResult testing(ArrayList<double[]> testingSet, ArrayList<Integer> testingClass, svm_model model) throws SvmException {
        try {            
            SvmResult result = new SvmResult();
            
            ArrayList<Integer> listClassId = new ArrayList<Integer>();
            for (Integer integer : testingClass) {
                if (!listClassId.contains(integer)) {
                    listClassId.add(integer);                    
                }
            }
            Collections.sort(listClassId);
            
            int predict_probability = param.probability;
            
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
            
            int correct = 0;
            int total = 0;
            double error = 0;
            double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;
            
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
            
            int[][] confusionMatrix = new int[listClassId.size()][listClassId.size()];
            for (int i = 0; i < testingSet.size(); i++) {
                
                int target = testingClass.get(i);
                
                double[] testingValues = testingSet.get(i);
                
                int m = testingSet.get(i).length;
                svm_node[] x = new svm_node[m];
                for (int j = 0; j < m; j++) {
                    x[j] = new svm_node();
                    x[j].index = j;
                    x[j].value = testingValues[j];
                }
                
                int answer;
                if (predict_probability == 1 && (svm_type == svm_parameter.C_SVC || svm_type == svm_parameter.NU_SVC)) {
                    answer = (int) svm.svm_predict_probability(model, x, prob_estimates);
                } else {
                    answer = (int) svm.svm_predict(model, x);
                }
                
                if (answer == target) {
                    ++correct;
                }
                confusionMatrix[listClassId.indexOf(target)][listClassId.indexOf(answer)] = confusionMatrix[listClassId.indexOf(target)][listClassId.indexOf(answer)] + 1;
                
                error += (answer - target) * (answer - target);
                sumv += answer;
                sumy += target;
                sumvv += answer * answer;
                sumyy += target * target;
                sumvy += answer * target;
                ++total;
            }
            
            result.setConfusionMatrix(confusionMatrix);
            
            if (svm_type == svm_parameter.EPSILON_SVR || svm_type == svm_parameter.NU_SVR) {
                System.out.print("Mean squared error = " + error / total + " (regression)\n");
                System.out.print("Squared correlation coefficient = "
                        + ((total * sumvy - sumv * sumy) * (total * sumvy - sumv * sumy))
                        / ((total * sumvv - sumv * sumv) * (total * sumyy - sumy * sumy))
                        + " (regression)\n");
            } else {                
                result.setError((double) (total - correct) / total * 100);
            }            
            return result;
        } catch (Exception ex) {
            throw new SvmException(ex.getMessage());
        }
    }
    
    /**
     * Test a specific sample
     * @param testingSample the feature vector of a specific sample
     * @param model the svm Model
     * @return the sample classe
     */     
    public int testing(double[] testingSample, svm_model model) throws SvmException {
        try {
            int predict_probability = param.probability;
            
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
            
            int m = testingSample.length;
            svm_node[] x = new svm_node[m];
            for (int j = 0; j < m; j++) {
                x[j] = new svm_node();
                x[j].index = j;
                x[j].value = testingSample[j];
            }
            
            if (predict_probability == 1 && (svm_type == svm_parameter.C_SVC || svm_type == svm_parameter.NU_SVC)) {
                v = (int) svm.svm_predict_probability(model, x, prob_estimates);
            } else {
                v = (int) svm.svm_predict(model, x);
            }
            return v;
        } catch (Exception ex) {
            throw new SvmException(ex.getMessage());
        }
    }
    
    /**
     * Load an svm
     * @param fileName the path of file
     * @return the svm model
     * @throws SmmException
     */
    public svm_model load(String modelFileName) throws SvmException {
        try {
            return svm.svm_load_model(modelFileName);
        } catch (IOException ex) {
            throw new SvmException(ex.getMessage());
        }
    }
    
    /**
     * Save the svm
     * @param svm_model of the gmm
     * @param fileName the path of file
     * @throws SmmException
     */
    public void save(String model_file_name, svm_model model) throws SvmException {
        try {
            svm.svm_save_model(model_file_name, model);
        } catch (Exception ex) {
            throw new SvmException(ex.getMessage());
        }
    }
}
