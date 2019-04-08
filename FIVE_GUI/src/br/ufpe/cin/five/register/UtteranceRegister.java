/**
 * Copyright 2011 Federal University of Pernambuco. All Rights Reserved. Use is
 * subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.register;

import br.ufpe.cin.five.core.project.Project;
import br.ufpe.cin.five.core.utterance.*;
import br.ufpe.cin.five.exception.RegisterException;
import br.ufpe.cin.five.facade.Facade;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alexandre
 */
public class UtteranceRegister {

    private Project project;

    public UtteranceRegister(Project project) {
        this.project = project;
    }

    public List<Phrase> refactorPhrase(List<Phrase> phrases) {
        List<Phrase> finalPhrases = new ArrayList<Phrase>();
        for (Phrase p : phrases) {
            Phrase phrase = (Phrase) Facade.getInstance().getConcreteDAO().getByDescription(p.getDescription(), Phrase.class);
            if (phrase == null) {
                p.setWords(refactorWords(p.getWords()));
                Facade.getInstance().getConcreteDAO().create(p);
                finalPhrases.add(p);
            } else {
                finalPhrases.add(phrase);
            }
        }
        return finalPhrases;
    }

    public List<Word> refactorWords(List<Word> words) {
        List<Word> finalWords = new ArrayList<Word>();
        for (Word w : words) {
            Word word = (Word) Facade.getInstance().getConcreteDAO().getByDescription(w.getDescription(), Word.class);
            if (word == null) {
                w.setSyllables(refactorSyllables(w.getSyllables()));
                Facade.getInstance().getConcreteDAO().create(w);
                finalWords.add(w);
            } else {
                finalWords.add(word);
            }
        }
        return finalWords;
    }

    public List<Syllable> refactorSyllables(List<Syllable> syllables) {
        List<Syllable> finalSyllables = new ArrayList<Syllable>();
        for (Syllable s : syllables) {
            Syllable syllable = (Syllable) Facade.getInstance().getConcreteDAO().getByDescription(s.getDescription(), Syllable.class);
            int h = 0;
            if (s.getDescription().equals("Lu")) {
                h++;
            }
            if (syllable == null) {
                Facade.getInstance().getConcreteDAO().create(s);
                finalSyllables.add(s);
            } else {
                finalSyllables.add(syllable);
            }
        }
        return finalSyllables;
    }

    public void removeSyllables(Word word) {
        List<Syllable> syllables = word.getSyllables();
        word.setSyllables(null);
        Facade.getInstance().getConcreteDAO().update(word);
        for (Syllable s : syllables) {
            Facade.getInstance().getConcreteDAO().delete(s);
        }
    }

    public void removeWords(Phrase phrase) {
        List<Word> words = phrase.getWords();
        phrase.setWords(null);
        Facade.getInstance().getConcreteDAO().update(phrase);
        for (Word w : words) {
            removeSyllables(w);
            Facade.getInstance().getConcreteDAO().delete(w);
        }
    }

    public void removePhrases(Utterance utterance) {
        List<Phrase> phrases = utterance.getPhrases();
        utterance.setPhrases(null);
        Facade.getInstance().getConcreteDAO().update(utterance);
    }

    public void insert(Utterance utterance) throws RegisterException {
        if (search(utterance.getId()) == null) {
            List<Phrase> phrases;
            try {
                phrases = UtteranceProcess.convertUtteranceToPhrases(utterance.getDescription(), "resources/exceptions.xml");
            } catch (UtteranceException ex) {
                throw new RegisterException("error in method convertUtteranceToPhrases: " + ex.getMessage());
            }
            Facade.getInstance().getConcreteDAO().create(utterance);
            utterance.setPhrases(refactorPhrase(phrases));
            Facade.getInstance().getConcreteDAO().update(utterance);

            project.getUtterances().add(utterance);
            Facade.getInstance().getConcreteDAO().update(this.project);
        } else {
            throw new RegisterException("Já existe uma locução com este ID: " + utterance.getId() + " no banco de dados.");
        }
    }

    public void update(Utterance utterance) throws RegisterException {
        if (search(utterance.getId()) != null) {

            List<Phrase> phrases;
            try {
                phrases = UtteranceProcess.convertUtteranceToPhrases(utterance.getDescription(), "resources/exceptions.xml");
            } catch (UtteranceException ex) {
                throw new RegisterException("error in method convertUtteranceToPhrases: " + ex.getMessage());
            }
            utterance.setPhrases(refactorPhrase(phrases));
            Facade.getInstance().getConcreteDAO().update(utterance);
            Facade.getInstance().getConcreteDAO().update(utterance);
            Facade.getInstance().getConcreteDAO().update(this.project);
            
        } else {
            throw new RegisterException("Não existe uma locução com este ID: " + utterance.getId() + " no banco de dados.");
        }
    }

    public void remove(Utterance utterance) throws RegisterException {
        if (search(utterance.getId()) != null) {
            this.project.getUtterances().remove(utterance);
            Facade.getInstance().getConcreteDAO().update(this.project);
            removePhrases(utterance);
            Facade.getInstance().getConcreteDAO().delete(utterance);
        } else {
            throw new RegisterException("Não existe uma locução com este ID: " + utterance.getId() + " no banco de dados.");
        }
    }

    public Utterance search(int id) throws RegisterException {
        for (Utterance utterance : project.getUtterances()) {
            if (id != -1 && utterance.getId() == id) {
                return utterance;
            }
        }
        return null;
    }
    
}
