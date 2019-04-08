/**
 * Copyright 2011 Federal University of Pernambuco. 
 * All Rights Reserved. Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.utterance;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides a set of useful methods for uterrance process.
 */
public class UtteranceUtil {

    public static final String FLAG_STRESS = "\"";
    private static Matcher matcher;

    public static String replaceFound(int ruleNumber, String word, int startIndex, int endIndex, String toFind, String toChange) {

        String firstWord = "";
        String lastWord = "";
        int lastRuleNumber = 0;

        String tempWord1 = "", tempWord2 = "", tempWord3 = "";
        int incValue = 0;
        if (lastRuleNumber == ruleNumber) {
            incValue = lastWord.length() - firstWord.length();
        } else {
            firstWord = word;
        }
        startIndex = startIndex + incValue;
        endIndex = endIndex + incValue;
        if (startIndex == 0) {
            tempWord1 = word.substring(startIndex, endIndex);
            tempWord2 = word.substring(endIndex);
            tempWord1 = tempWord1.replaceAll(toFind, toChange);
        } else if (endIndex > word.length()) {
            if (startIndex > word.length()) {
                startIndex = startIndex - incValue;
                endIndex = endIndex - incValue;
            }
            tempWord1 = word.substring(0, startIndex);
            tempWord2 = toChange;
        } else {
            tempWord1 = word.substring(0, startIndex);
            tempWord2 = word.substring(startIndex, endIndex);
            tempWord3 = word.substring(endIndex);
            if ((matcher = Pattern.compile(toFind).matcher(tempWord2)).find()) {
                tempWord2 = tempWord2.replaceAll(toFind, toChange);
            } else {
                startIndex = startIndex - incValue;
                endIndex = endIndex - incValue;
                tempWord1 = word.substring(0, startIndex);
                tempWord2 = word.substring(startIndex, endIndex);
                tempWord3 = word.substring(endIndex);
                tempWord2 = tempWord2.replaceAll(toFind, toChange);
            }
        }
        lastRuleNumber = ruleNumber;
        lastWord = tempWord1 + tempWord2 + tempWord3;
        return lastWord;
    }

    public static int getNumberOfSyllablesOnPhrase(Phrase phrase) {
        int numberOfSyllablesOnPhrase = 0;
        for (Word word : phrase.getWords()) {
            numberOfSyllablesOnPhrase += word.getSyllables().size();
        }
        return numberOfSyllablesOnPhrase;
    }

    public static String getStressedSyllable(String syllabic) {
        String stressedSyllable = "";
        String[] syllables = syllabic.split("-");
        for (int a = 0; a < syllables.length; a++) {
            if (syllables[a].contains(FLAG_STRESS)) {
                stressedSyllable = String.valueOf(a);
            }
        }
        return stressedSyllable;
    }

    public static String putStressFlag(String phonetics, String separatedWord) {
        String stressedWord = "";        
        try{
            String preStress = phonetics.substring(0, phonetics.indexOf(FLAG_STRESS));
            String preStressRegex = "^";
            List<String> list = getListOf(preStress, Boolean.TRUE);
            for (String letter : list) {
                preStressRegex += "(" + letter + ")" + "(-)?";
            }

            if ((matcher = Pattern.compile(preStressRegex).matcher(separatedWord)).find()) {
                stressedWord = separatedWord.substring(0, matcher.end()) + FLAG_STRESS + separatedWord.substring(matcher.end());
            }

        }
        catch(Exception ex)
        {
             System.out.println(ex.getMessage() + " Na palavra: " + phonetics + " | " + separatedWord);
        }
        return stressedWord;
    }

//    public static String putStressFlag(String word, int stressIndex) {
//        return word.substring(0, stressIndex) + GraphemeToPhoneme.FLAG_STRESS + word.substring(stressIndex);
//    }   
    
    public static String putStressFlag(String phonetic, int stressIndex) {
        String[] phones = phonetic.split(" ");
        String p = "";
        for (int i = 0; i < phones.length; i++) {
            if ((i == stressIndex)) {
                p += FLAG_STRESS + " ";
            } 
            p += phones[i] + " ";
        }
        
        if(stressIndex == phones.length || stressIndex > phones.length || stressIndex==-1){
            p += FLAG_STRESS + " ";
        }
        
        return p.toString();
    }

    public static String removeStressFlag(String text) {
        return fixBlankSpaces(text.replaceAll(Word.FLAG_STRESS, "")).trim();
    }

    public static String fixBlankSpaces(String text) {
        return text.replaceAll("(\\s)+", " ");
    }

    public static String removeBlankSpaces(String text) {
        return text.replaceAll("(\\s)+", "");
    }

    public static String fixTildeLetterCase(String word, String letter) {
        if (word.contains("~")) {
            return word.replaceAll(letter + "~", letter.toLowerCase() + "~");
        }
        return word;
    }

    public static String fixTildeSpace(String word) {
        return word.replaceAll("[ ]{1,}[~][ ]{0,}", "~ ").replaceAll("\\s+", " ");
    }

    public static String removeLetterMarks(String letter) {
        letter = letter.replaceAll("[âáàãä]", "a");
        letter = letter.replaceAll("[êéèë]", "e");
        letter = letter.replaceAll("[îíìĩï]", "i");
        letter = letter.replaceAll("[ôóòõöô]", "o");
        letter = letter.replaceAll("[ûúùũü]", "u");
        letter = letter.replaceAll("[ŷýỳÿ]", "y");
        letter = letter.replaceAll("[ç]", "c");

        return fixBlankSpaces(letter);
    }

    public static String[] getStringVector(String text, Boolean hasBlankSpaces) {
        if (hasBlankSpaces) {
            return fixBlankSpaces(text).trim().split(" ");
        } else {
            return text.replaceAll("", " ").trim().split(" ");
        }
    }

    public static List<String> getListOf(String text, Boolean hasBlankSpaches) {
        List<String> lettersList = new ArrayList<String>();
        for (String letter : getStringVector(text, hasBlankSpaches)) {
            if (lettersList.indexOf(letter) == -1 || hasBlankSpaches) {
                lettersList.add((hasBlankSpaches) ? letter : removeLetterMarks(letter));
            }
        }
        if (!hasBlankSpaches) {
            List<String> newLetterList = new ArrayList<String>();
            for (String letter : lettersList) {
                if (newLetterList.indexOf(letter) == -1) {
                    newLetterList.add(letter);
                }
            }
            lettersList = newLetterList;
        }
        return lettersList;
    }
}
