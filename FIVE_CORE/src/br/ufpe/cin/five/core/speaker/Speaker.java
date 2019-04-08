/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved. Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.speaker;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * This class defines a Speaker.<br/>
 * It contains information about a speaker, like:<br/>
 * Id - the register identificator  <br/>
 * Name - the name of the speaker.<br/>
 * Region - the region of the speaker.<br/>
 * Gender - the gender of the speaker.<br/>
 * Age - the age of the speaker.<br/>
 */
@Entity
public class Speaker implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  
    private int id;
    private String name;   
    private String region;    
    private Gender gender;
    private int age;

    /**
     * Creates a new empty Speaker
     */
    public Speaker(){
    }
    
    /**
     * Creates a new Speaker specifying the name, region, gender and age
     * @param name
     * @param region
     * @param gender
     * @param age
     */    
    public Speaker(String name, String region, Gender gender, int age) {
        this.name = name;
        this.region = region;
        this.gender = gender;
        this.age = age;
    }
    
    /**
     * Return the id of this speaker.
     * @return the id of this speaker.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id of this speaker
     * @param id the id of this speaker
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Returns the age of this speaker.
     * @return the age of this speaker
     */
    public int getAge() {
        return age;
    }

    /**
     * Sets the age of this speaker.
     * @param age the age of this speaker
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Returns the gender of this speaker.
     * @return the gender of this speaker.
     *
     * @see Gender
     */
    public Gender getGender() {
        return gender;
    }

    /**
     * Sets the gender of this speaker.
     * @param gender the gender of this speaker
     */
    public void setGender(Gender gender) {
        this.gender = gender;
    }  

    /**
     * Returns the name of this speaker.
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this speaker.
     * @param name the name of this speaker
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the region of this speaker.
     * @return the region of this speaker
     */
    public String getRegion() {
        return region;
    }

    /**
     * Sets the region of this speaker.
     * @param region the region of this speaker
     */
    public void setRegion(String region) {
        this.region = region;
    }          

    /**
     * Return the name of this speaker.
     * @return the name of this speaker
     */
    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Tests if the object is equals to this speaker.<br/>
     * The test is done by comparing if the object is instace of Speaker and then comparing the login of the object and this speaker.
     * @param obj the object to comparing
     * @return true if the obj is equals to this speaker
     */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Speaker) ? this.id == ((Speaker) obj).getId() : false;
    }
}
