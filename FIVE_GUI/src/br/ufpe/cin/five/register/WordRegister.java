/**
 * Copyright 2011 Federal University of Pernambuco. All Rights Reserved. Use is
 * subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.register;

import br.ufpe.cin.five.core.project.Project;
import br.ufpe.cin.five.core.utterance.Phrase;
import br.ufpe.cin.five.core.utterance.Syllable;
import br.ufpe.cin.five.core.utterance.Utterance;
import br.ufpe.cin.five.core.utterance.Word;
import br.ufpe.cin.five.exception.RegisterException;
import br.ufpe.cin.five.facade.Facade;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Alexandre
 */
public class WordRegister {

    private Project project;

    public WordRegister(Project project) {
        this.project = project;
    }   

    public void insert(String word, String newPhoneticRepresentation) throws RegisterException {
        boolean found = false;
        List<Utterance> utterances = this.project.getUtterances();
        for (Utterance utterance : utterances) {
            List<Phrase> phrases = utterance.getPhrases();
            for (Phrase phrase : phrases) {
                List<Word> words = phrase.getWords();
                for (Word elementWord : words) {
                    if (elementWord.getDescription().equals(word)) {
                        found = true;
                        List<String> phoneticRepresentations = elementWord.getPhoneticRepresentations();
                        phoneticRepresentations.add(newPhoneticRepresentation);
                        Facade.getInstance().getConcreteDAO().update(elementWord);
                        break;
                    }
                }
            }
        }
        if (!found) {
            throw new RegisterException("A palavra " + word + "  não foi encontrada.");
        }
    }

    public void update(String word, String oldPhoneticRepresentation, String newPhoneticRepresentation) throws RegisterException {
        List<Utterance> utterances = this.project.getUtterances();
        boolean found = false;
        for (Utterance utterance : utterances) {
            List<Phrase> phrases = utterance.getPhrases();
            for (Phrase phrase : phrases) {
                List<Word> words = phrase.getWords();
                for (Word elementWord : words) {
                    if (elementWord.getDescription().equals(word)) {
                        List<String> phoneticRepresentations = elementWord.getPhoneticRepresentations();
                        phoneticRepresentations.remove(oldPhoneticRepresentation);
                        phoneticRepresentations.add(newPhoneticRepresentation);
                        found = true;
                        Facade.getInstance().getConcreteDAO().update(elementWord);
                        break;
                    }
                }
            }
        }
        if (!found) {
            throw new RegisterException("A palavra " + word + "  não foi encontrada.");
        }
    }

    public void remove(String word, String phoneticRepresentation) throws RegisterException {
        List<Utterance> utterances = this.project.getUtterances();
        for (Utterance utterance : utterances) {
            List<Phrase> phrases = utterance.getPhrases();
            for (Phrase phrase : phrases) {
                List<Word> words = phrase.getWords();
                for (Word wordElement : words) {
                    if (wordElement.getDescription().equals(word)) {
                        List<String> representations = wordElement.getPhoneticRepresentations();
                        if (representations.size() != 1) {
                            for (String representation : representations) {
                                if (phoneticRepresentation.equals(representation)) {
                                    representations.remove(representation);
                                    Facade.getInstance().getConcreteDAO().update(wordElement);
                                    break;
                                }
                            }
                        } else {
                            throw new RegisterException("Exclusão não permitida: Cada palavra deve ter no mínimo uma Representação fonética.");
                        }
                        break;
                    }
                }
            }
        }
    }           
    
    public void generateHistogram(Project project) throws RegisterException {
        try {
            List<Utterance> utterances = project.getUtterances();
            if (!utterances.isEmpty()) {

                Map dic = new HashMap();
                List<String> phones = new ArrayList<String>();

                for (Utterance u : utterances) {
                    for (Phrase p : u.getPhrases()) {
                        for (Word w : p.getWords()) {
                            for (Syllable s : w.getSyllables()) {
                                for (String phone : s.getPhones()) {
                                    if (phones.contains(phone)) {
                                        int quantidade = Integer.parseInt(dic.get(phone).toString());
                                        dic.remove(phone);
                                        dic.put(phone, quantidade + 1);
                                    } else {
                                        dic.put(phone, 1);
                                        phones.add(phone);
                                    }
                                }
                            }
                        }
                    }
                }
                FileWriter writer = new FileWriter(new File(project.getDirectory() + "/histograma.txt"), false);
                for (String s : phones) {
                    writer.append(s + ";" + dic.get(s) + "\n");
                }
                writer.close();
            } else {
                throw new RegisterException("Lista de Locuções vazia!");
            }
        } catch (IOException ex) {
            throw new RegisterException(ex.getMessage());
        }

    }    
}
