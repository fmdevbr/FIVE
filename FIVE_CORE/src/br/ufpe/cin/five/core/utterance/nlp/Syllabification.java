/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved. Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.utterance.nlp;

import br.ufpe.cin.five.core.utterance.UtteranceUtil;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 *
 * @author Carlos
 */
public class Syllabification {

    public static String VOWELS = "(a|a~|e|e~|E|i|i~|o|o~|O|u|u~)";
    public static String SEMIVOWELS = "(j|j~|w|w~)";
    public static String ALLVOWELS = "(a|a~|e|e~|E|i|i~|o|o~|O|u|u~|j|j~|w|w~)";
    public static String NOTTILDEALLVOWELS = "(a|e|E|i|o|O|u|j|w)";
    public static String TILDEVOWELS = "(a~|e~|i~|o~|u~|j~|w~)";
    private static String DOUBLECON = "((p|b|t|tS|d|dZ|k|g)(l|r)|(bk|bd|bZ|bs|bS|bt|km|kn|kt|ks|dm|dk|ds|fn|ft|gd|gm|gn|mn|ps|pn|pt|tm|tn))";// dv|dZ|

    public static String toSyllables(String phonetics) {

       // System.out.println(" -- divisão silábica: " + phonetics);
        
        String[] arrPhones = UtteranceUtil.fixBlankSpaces(UtteranceUtil.removeStressFlag(phonetics)).split(" ");

        String sepWord = "";
        String previousLetter, letter, nextLetter;

        if (arrPhones.length == 1) {

            sepWord += arrPhones[0];

        } else {

            for (int i = 0; i < arrPhones.length; i++) {

                if (i == 0) {

                    previousLetter = "";
                    letter = arrPhones[i];
                    nextLetter = arrPhones[i + 1];

                } else if (i == arrPhones.length - 1) {

                    previousLetter = arrPhones[i - 1];
                    letter = arrPhones[i];
                    nextLetter = "";

                } else {

                    previousLetter = arrPhones[i - 1];
                    letter = arrPhones[i];
                    nextLetter = arrPhones[i + 1];

                }

                if (Pattern.matches(TILDEVOWELS, letter)) {

                    if (Pattern.matches(NOTTILDEALLVOWELS, previousLetter)) {
                        sepWord += "-" + letter;
                    } else {
                        sepWord += letter;
                    }

                } else if (Pattern.matches(VOWELS, letter)) {

                    if (Pattern.matches(VOWELS, previousLetter)) {
                        sepWord += "-" + letter;
                    } else {
                        sepWord += letter;
                    }

                } else if (Pattern.matches(SEMIVOWELS, letter)) {

                    if (Pattern.matches(SEMIVOWELS, previousLetter)) {
                        sepWord += "-" + letter;
                    } else {
                        sepWord += letter;
                    }

                } else {

                    String letters = previousLetter + letter + nextLetter;
                    Matcher matcher = Pattern.compile(DOUBLECON).matcher(letters);
                    if (matcher.find()) {

                        if (Pattern.matches(TILDEVOWELS, previousLetter)) {
                            sepWord += "-" + letters.substring(matcher.start(), matcher.end());
                        } else if (Pattern.matches(VOWELS, previousLetter)) {
                            sepWord += "-" + letters.substring(matcher.start(), matcher.end());
                        } else {
                            sepWord += letters.substring(matcher.start(), matcher.end());
                        }

                        i++;

                    } else {

                        if (Pattern.matches(ALLVOWELS, previousLetter)) {

                            if (Pattern.matches(ALLVOWELS, nextLetter)) {
                                sepWord += "-" + letter;
                            } else {
                                sepWord += letter + ((sepWord.equals("") || nextLetter.equals("")) ? "" : "-");
                            }

                        } else {
                            sepWord += letter;
                        }
                    }
                }
            }
        }
        return UtteranceUtil.putStressFlag(phonetics, sepWord);

    }

    public static int getStressedSyllable(String syllabification) {
        int stressedSyllable = -1;
        String[] syllabs = syllabification.split("-");
        for (int a = 0; a < syllabs.length; a++) {
            if (syllabs[a].contains(GraphemeToPhoneme.FLAG_STRESS)) {
                stressedSyllable = a;
            }
        }
        return stressedSyllable;
    }

    public static void main(String[] args) {
        try {
            for (String word : JOptionPane.showInputDialog("Frase?").split(" ")) {
                System.out.println(toSyllables(GraphemeToPhoneme.toPhonemes("resources/dictionary", word)));
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
