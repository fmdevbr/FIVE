/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.extraction.mgcc;

import br.ufpe.cin.five.core.extraction.Extraction;
import javax.persistence.Entity;

/**
 *
 * @author Alexandre
 */
@Entity
public class MgccParameters extends Extraction {

    private int byteswap;
    private int sampfreq;
    private int framelen;
    private int frameshift;
    private int windowtype;
    private int normalize;
    private int fftlen;
    private double freqwarp;
    private int gamma;
    private int mgcorder;
    private int lngain;
    private int lowerf0;
    private int upperf0;
    private int noisemask;

    public MgccParameters() {
        super();
        this.byteswap = 0;
        this.sampfreq = 16000;
        this.framelen = 400;
        this.frameshift = 80;
    }

    public int getByteswap() {
        return byteswap;
    }

    public void setByteswap(int byteswap) {
        this.byteswap = byteswap;
    }

    public int getSampfreq() {
        return sampfreq;
    }

    public void setSampfreq(int sampfreq) {
        this.sampfreq = sampfreq;
    }

    public int getFramelen() {
        return framelen;
    }

    public void setFramelen(int framelen) {
        this.framelen = framelen;
    }

    public int getFrameshift() {
        return frameshift;
    }

    public void setFrameshift(int frameshift) {
        this.frameshift = frameshift;
    }

    public int getWindowtype() {
        return windowtype;
    }

    public void setWindowtype(int windowtype) {
        this.windowtype = windowtype;
    }

    public int getNormalize() {
        return normalize;
    }

    public void setNormalize(int normalize) {
        this.normalize = normalize;
    }

    public int getFftlen() {
        return fftlen;
    }

    public void setFftlen(int fftlen) {
        this.fftlen = fftlen;
    }

    public double getFreqwarp() {
        return freqwarp;
    }

    public void setFreqwarp(double freqwarp) {
        this.freqwarp = freqwarp;
    }

    public int getGamma() {
        return gamma;
    }

    public void setGamma(int gamma) {
        this.gamma = gamma;
    }

    public int getMgcorder() {
        return mgcorder;
    }

    public void setMgcorder(int mgcorder) {
        this.mgcorder = mgcorder;
    }

    public int getLngain() {
        return lngain;
    }

    public void setLngain(int lngain) {
        this.lngain = lngain;
    }

    public int getLowerf0() {
        return lowerf0;
    }

    public void setLowerf0(int lowerf0) {
        this.lowerf0 = lowerf0;
    }

    public int getUpperf0() {
        return upperf0;
    }

    public void setUpperf0(int upperf0) {
        this.upperf0 = upperf0;
    }

    public int getNoisemask() {
        return noisemask;
    }

    public void setNoisemask(int noisemask) {
        this.noisemask = noisemask;
    }


}
