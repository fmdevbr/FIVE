/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufpe.cin.five.core.dictionary;

import br.ufpe.cin.five.core.utterance.Word;
import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author Alexandre
 */
@Entity
public class Dictionary implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  
    private int id;
    @ManyToMany(cascade = CascadeType.ALL)
    //@LazyCollection(LazyCollectionOption.FALSE)
    private List <Word> words;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Word> getWords() {
        return words;
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }        
    
}
