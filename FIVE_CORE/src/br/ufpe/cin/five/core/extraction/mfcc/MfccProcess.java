/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.extraction.mfcc;

import br.ufpe.cin.five.core.extraction.ExtractionException;
import br.ufpe.cin.five.core.extraction.ExtractionProcess;
import br.ufpe.cin.five.core.extraction.ExtractionUtil;
import br.ufpe.cin.five.core.extraction.PosProcessing;
import br.ufpe.cin.five.core.extraction.PreProcessedAudio;
import br.ufpe.cin.five.core.extraction.PreProcessing;
import br.ufpe.cin.five.core.extraction.VAD;
import br.ufpe.cin.five.core.extraction.WindowingTechnique;
import br.ufpe.cin.five.core.sample.Audio;
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;
import java.io.File;

/**
 * This clas implements the mfcc extract.<br/>
 * The mfcc extract its composite by the follow steps:<br/>
 * Fast Fourier Transform <br/>
 * Calc of the power spectrum or magnitude spectrum <br/>
 * Mel Frequency Filter Bank <br/>
 * Discrete Sine Transform <br/>
 *
 * @see edu.emory.mathcs.jtransforms
 * @see MelFrequencyFilterBank
 * @see DiscreteCosineTransform
 */
public class MfccProcess extends ExtractionProcess {

    /**
     * Creates a new empty MfccProcess
     */     
    public MfccProcess(){}
    
    /**
     * Creates a new MfccProcess specifying the projectPath.
     * @param projectPath
     */    
    public MfccProcess (String projectPath) {
    }    
    
    /**
     * Do the mfcc extract.
     * @param audio the input audio
     * @param params the parameters of mfcc
     * @return the mfcc coefs
     * @throws ExtractionException
     */
    @Override
    public double[] extract(Object params, Audio audio) throws ExtractionException {
        try {            
            MfccParameters mfccParameters = (MfccParameters) params;           

            if (mfccParameters.isVad()) {
                audio.setAudioData(VAD.absCut(audio.getAudioData()));
            }
            double[] pheEmphasedAudio = PreProcessing.preEmphasis(audio.getAudioData(), mfccParameters.getPreEmphasis());
            int audioFrameLength = ExtractionUtil.calcFrameLength(audio.getAudioFormat().getSampleRate(), mfccParameters.getFrameDuration());
            double[][] audioFrames = PreProcessing.overlapping(pheEmphasedAudio, audioFrameLength, mfccParameters.getOverlap());
            WindowingTechnique window = mfccParameters.getWindowing();
            switch (window) {
                case HANNING:
                    audioFrames = PreProcessing.windowing(audioFrames, 0.54, audioFrameLength);
                    break;
                case HAMMING:
                    audioFrames = PreProcessing.windowing(audioFrames, 0.46, audioFrameLength);
                    break;
                case RECTANGULAR:
                    audioFrames = PreProcessing.windowing(audioFrames, 0, audioFrameLength);
                    break;
            }             

            PreProcessedAudio preProcessedAudio = new PreProcessedAudio(audioFrames, audio.getAudioFormat().getSampleRate(), audioFrameLength, audioFrames.length);

            // Variaveis referentes ao audio
            int preProcessedAudioframeLength = preProcessedAudio.getFrameLenght();
            int numFrames = preProcessedAudio.getNumFrames();
            double sampleRate = preProcessedAudio.getSampleRate();
            double[][] preProcessedAudioFrames = preProcessedAudio.getFrames();
            //Variavei referentes as propiedades
            int numMelFilters = mfccParameters.getNumMelFilters();
            int coefs = mfccParameters.getNumCoefs();
            boolean powerSpectrum = mfccParameters.isPowerSpectrum();
            double lowerFreq = mfccParameters.getLowerFreq();
            double upperFreq = mfccParameters.getUpperFreq();
            if (upperFreq == -1) {
                upperFreq = (sampleRate / 2) - 500;
            }
            //FTT array
            double fftArrays[][] = new double[numFrames][preProcessedAudioframeLength];
            //Chamda da fft
            DoubleFFT_1D fft = new DoubleFFT_1D(preProcessedAudioframeLength);
            //
            double[][] features = new double[numFrames][13];
            DiscreteCosineTransform dst = new DiscreteCosineTransform(13, numMelFilters);
            for (int i = 0; i < numFrames; i++) {
                System.arraycopy(preProcessedAudioFrames[i], 0, fftArrays[i], 0, preProcessedAudioframeLength);
                fft.realForward(fftArrays[i]);

                //Pode ser usado tanto o powerSpectrum quanto a magnitude
                double[] rsPower;
                if (powerSpectrum) {
                    rsPower = powerSpectrum(fftArrays[i]);
                } else {
                    rsPower = magnitudeSpectrum(fftArrays[i]);
                }

                double[] melSpec = melSpectrum(rsPower, numMelFilters, lowerFreq, upperFreq, sampleRate);
                features[i] = dst.process(melSpec);
            }

            double[] featuresMean = arithmeticMean(features);

            double[] answer = null;
            if(mfccParameters.getNumCoefs()==13) {
                answer = new double[coefs];
                System.arraycopy(featuresMean, 0, answer, 0, featuresMean.length);
            } else if (mfccParameters.getNumCoefs()==26) {
                answer = new double[26];                
                System.arraycopy(featuresMean, 0, answer, 0, featuresMean.length);            
                System.arraycopy(delta(featuresMean), 0, answer, featuresMean.length, featuresMean.length);
            } else if (mfccParameters.getNumCoefs()==39) {
                answer = new double[39];                
                System.arraycopy(featuresMean, 0, answer, 0, featuresMean.length);            
                System.arraycopy(delta(featuresMean), 0, answer, featuresMean.length, featuresMean.length);   
                System.arraycopy(delta_delta(featuresMean), 0, answer, featuresMean.length * 2, featuresMean.length);                        
            }                

            if(mfccParameters.isNormalized())     
                answer = PosProcessing.normalization(featuresMean);
            else
                answer = featuresMean;                 

            return answer;
            
        } catch(Exception ex){
            throw new MfccException(ex.getMessage());
        }           
    }

    /**
     * Calc the magnitude spectrum
     * @param inputComplex the input signal
     * @return the magnitude spectrum
     */
    private static double[] magnitudeSpectrum(double[] inputComplex) {
        double[] magnitude;
        magnitude = powerSpectrum(inputComplex);
        for (int i = 0; i < magnitude.length; i++) {
            magnitude[i] = Math.pow(magnitude[i], 0.5);
        }
        return magnitude;
    }

    /**
     * Calc the power spectrum(the square root of the magnitude spectrum).
     * @param inputComplex
     * @return the power spectrum
     */
    private static double[] powerSpectrum(double[] inputComplex) {
        double[] real = new double[inputComplex.length / 2];
        double[] img = new double[inputComplex.length / 2];
        System.arraycopy(inputComplex, 0, real, 0, inputComplex.length / 2);
        System.arraycopy(inputComplex, inputComplex.length / 2, img, 0, inputComplex.length / 2);
        double[] power = new double[inputComplex.length / 2];
        for (int i = 0; i < (inputComplex.length / 2); i++) {
            power[i] = (real[i] * real[i]) * (img[i] * img[i]);
        }
        return power;
    }

    /**
     * Calc the melSpectrum
     * @param inputComplex the input signal
     * @param numMelFilters the number of mel filters
     * @param lowerFreq the lower frequency
     * @param upperFreq the upper frequency
     * @param sampleRate the sample rate
     * @return the mel spectrum
     */
    private static double[] melSpectrum(double[] inputComplex, int numMelFilters, double lowerFreq, double upperFreq, double sampleRate) {
        MelFrequencyFilterBank melFrequency = new MelFrequencyFilterBank(numMelFilters, lowerFreq, upperFreq, sampleRate);
        return melFrequency.process(inputComplex);
    }   
    
    /**
     * Calcs the arithmetic mean of the matrix
     * @param matrix the matrix
     * @return the arithmetic mean of the matrix
     */
    private static double[] arithmeticMean(double[][] matrix) {
        double[] vector = matrix[0];
        for (int row = 0; row < vector.length; row++) {
            for (int line = 0; line < matrix.length; line++) {
                vector[row] += matrix[line][row];
            }
            vector[row] /= matrix.length;
        }

        return vector;
    }

    /**
     * Calcs the delta of the feature vector
     * @param vector the feature vector
     * @return
     */
    private static double[] delta(double[] vector) {
        double[] delta = new double[vector.length];
        delta[0] = vector[0];
        for (int i = 0; i < vector.length; i++) {
            if (i == 0) {
                delta[i] = (vector[i + 1] - 0) / 2;
            } else if (i == vector.length - 1) {
                delta[i] = (0 - vector[i - 1]) / 2;

            } else {
                delta[i] = (vector[i + 1] - vector[i - 1]) / 2;
            }
        }
        //    delta[0]= energy(delta);
        return delta;
    }

    /**
     * Calcs the delta delta of the feature vector
     * @param vector the feature vector
     * @return the delta delta
     */
    private static double[] delta_delta(double[] vector) {
        double[] delta = new double[vector.length];
        delta[0] = vector[0];
        for (int i = 0; i < vector.length; i++) {
            if (i == 0) {
                delta[i] = 0 - 2 * vector[i] + vector[i + 1];

            } else if (i == vector.length - 1) {
                delta[i] = vector[i - 1] - 2 * vector[i] + 0;

            } else {
                delta[i] = vector[i - 1] - 2 * vector[i] + vector[i + 1];
            }
        }
        //    delta[0]= energy(delta);
        return delta;
    }        
}
