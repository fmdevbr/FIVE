/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.classification;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;

/**
 * This class provides a set of useful methods for classification process.
 */
public class ClassificationUtil {

    /**
     * Makes a set of random numbers useful on trainning process.
     * @param size size of the set of random numbers.
     * @return the set of random numbers.
     */      
    public static ArrayList<Integer> randomNumbers(int size) throws ClassificationException {
        try {
            Random randomClass = new Random();
            ArrayList<Integer> randomNumbers = new ArrayList<Integer>();
            boolean end = false;
            int count = 0;
            int inicialNumber = randomClass.nextInt(size);
            randomNumbers.add(inicialNumber);
            while (!end && (count < size)) {
                boolean equals = true;
                int randomNumb = randomClass.nextInt(size);
                //Verifica se numero ja existe no array
                for (Integer onArray : randomNumbers) {
                    if (onArray != randomNumb) {
                        equals = false;
                    } else {
                        equals = true;
                        break;
                    }
                }
                //Adiciona se não tem numero no array
                if (!equals) {
                    randomNumbers.add(randomNumb);
                    count++;
                }
                if (randomNumbers.size() == size) {
                    end = true;
                }
            }
            return randomNumbers;
        } catch (Exception ex) {
            throw new ClassificationException(ex.getMessage());
        }
    }

    /**
     * Loads the features contained on a feature file.
     * @param filePath the path of the feature file.
     * @return the fetures.
     */        
    public static double[] loadFeatureFile(String filePath) throws ClassificationException {
        try {
            double[] bsReturn = null;        
            FileReader arquivo = new FileReader(filePath);
            BufferedReader buffRead = new BufferedReader(arquivo);
            while (buffRead.ready()) {
                String[] strings = buffRead.readLine().split(" ");
                double[] arrayDouble = new double[strings.length];
                for (int i = 0; i < strings.length; i++) {
                    arrayDouble[i] = new Double(strings[i]);
                }
                bsReturn = arrayDouble;
            }
            buffRead.close();
            return bsReturn;
        } catch (Exception ex) {
            throw new ClassificationException(ex.getMessage());
        }            
    }
}
