package br.ufpe.cin.five.core.classification.hmm;

import br.ufpe.cin.five.core.classification.Classification;
import javax.persistence.Entity;

/**
 * This class contains the common information between HmmPhoneticParameters and HmmWordParametes.<br/>
 * numberGuassians - the number of gaussians<br/>
 * numberHERest - the number of HERest(HTK command)<br/>
 * topology - the hmm topology<br/>
 * hmmTypes - the hmm types<br/>
 * numberStates - the number of states per phones<br/>
 * @author daniel
 * @see HmmTypes
 * @see HmmTopology
 */
@Entity
public class HmmParameters extends Classification {

    private int numGaussians;
    private int numIterections;
    private int numHERests;
    private int numStates;
    private HmmStatesType statesType;
    private HmmTopology topology;
    private HmmUnitSize unitSize;

    /**
     * Creates a default HmmParameters
     */
    public HmmParameters() {
        super();
        numGaussians = 10;
    }

    /**
     * Returns the number of gaussians
     * @return the number of guassuas
     */
    public int getNumGaussians() {
        return numGaussians;
    }

    /**
     * Sets the number of guassians
     * @param numberGaussians the number of guassian
     */
    public void setNumGaussians(int numGaussians) {
        this.numGaussians = numGaussians;
    }

    /**
     * Returns the number of Iteractions
     * @return the number of Iteractions
     */
    public int getNumIteractions() {
        return numIterections;
    }

    /**
     * Sets the number of HEIteractionsRest
     * @param numberHERest the number of Iteractions
     */
    public void setNumIteractions(int numIterections) {
        this.numIterections = numIterections;
    }    
    
    /**
     * Returns the number of HERest
     * @return the number of HERest
     */
    public int getNumHERests() {
        return numHERests;
    }

    /**
     * Sets the number of HERest
     * @param numberHERest the number of HERest
     */
    public void setNumHERests(int numHERests) {
        this.numHERests = numHERests;
    }

    /**
     * Returns the number of states
     * @return the number of states
     */
    public int getNumStates() {
        return numStates;
    }

    /**
     * Sets the number of states
     * @param numberStates the number of states
     */
    public void setNumStates(int numStates) {
        this.numStates = numStates;
    }

    /**
     * Returns the hmm types
     * @return the hmmTypes the hmm types
     * @see HmmTypes
     */
    public HmmStatesType getStatesType() {
        return statesType;
    }

    /**
     * Sets the hmm types
     * @param hmmTypes the hmmTypes
     * @see HmmTypes
     */
    
    public void setType(HmmStatesType statesTypes) {
        this.statesType = statesTypes;
    } 
    
    /**
     * Returns the hmm topology
     * @return the hmm topology
     * @see HmmTopology
     */
    public HmmTopology getTopology() {
        return topology;
    }

    /**
     * Sets the hmm topology
     * @param topology the hmm topology
     * @see HmmTopology
     */
    public void setTopology(HmmTopology hmmTopology) {
        this.topology = hmmTopology;
    }

    /**
     * Returns the hmm unit size
     * @return the hmm unit size
     * @see HmmUnitSize
     */
    public HmmUnitSize getUnitSize() {
        return unitSize;
    }

    /**
     * Sets the hmm topology
     * @param topology the hmm topology
     * @see HmmTopology
     */
    public void setUnitSize(HmmUnitSize unitSize) {
        this.unitSize = unitSize;
    }    
}
