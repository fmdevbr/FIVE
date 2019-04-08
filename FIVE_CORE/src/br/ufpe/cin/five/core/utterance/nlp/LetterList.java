/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved. Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.utterance.nlp;

import java.util.Arrays;
import java.util.LinkedList;

/**
 *
 * @author Carlos
 */
public class LetterList extends LinkedList<String> {

    private String APPEND;

    public LetterList(LetterList... lists) {
        for (LetterList letterList : lists) {
            addAll(letterList);
        }
    }

    public LetterList(String... letters) {
        addAll(Arrays.asList(letters));
    }

    public String getRegEx() {
        APPEND = null;
        String regEx = "";
        for (String letter : this) {
            regEx += (APPEND = APPEND == null ? "" : "|") + letter;
        }
        return regEx;
    }

    public LetterList plus(LetterList toAdd) {
        LetterList plus = new LetterList(this);
        plus.addAll(toAdd);
        return plus;
    }

    public LetterList plus(String... toAdd) {
        LetterList plus = new LetterList(this);
        plus.addAll(Arrays.asList(toAdd));
        return plus;
    }

    public LetterList less(LetterList... toRemove) {
        LetterList less = new LetterList(this);
        for (LetterList letterList : toRemove) {
            for (String letter : letterList) {
                if (less.contains(letter)) {
                    less.remove(letter);
                }
            }
        }
        return less;
    }

    public LetterList less(String... toRemove) {
        LetterList less = new LetterList(this);
        for (String letter : toRemove) {
            if (less.contains(letter)) {
                less.remove(letter);
            }
        }
        return less;
    }

    @Override
    public String toString() {
        return getRegEx();
    }
}