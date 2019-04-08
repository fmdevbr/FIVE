/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufpe.cin.five.core.dictionary;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author vocallab
 */
@Entity
public class PhoneticRepresentation implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  
    private int id;
    private String description;
    @ElementCollection
    private List<String> phoneticRepresentation;

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the phoneticRepresentation
     */
    public List<String> getPhoneticRepresentation() {
        return phoneticRepresentation;
    }

    /**
     * @param phoneticRepresentation the phoneticRepresentation to set
     */
    public void setPhoneticRepresentation(List<String> phoneticRepresentation) {
        this.phoneticRepresentation = phoneticRepresentation;
    }
    
}
