/**
 * Copyright 2011 Federal University of Pernambuco. All Rights Reserved. Use is
 * subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.utterance;

import br.ufpe.cin.five.core.dictionary.PhoneticRepresentation;
import br.ufpe.cin.five.core.speaker.Gender;
import br.ufpe.cin.five.core.utterance.nlp.*;
import br.ufpe.cin.five.core.wordException.WordExceptionProcess;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is an abstract class that specifies and implements the main methods
 * necessary for the utterance process.
 */
public class UtteranceProcess {

    private static String dictionaryPath;
    private static Matcher matcher;
    private static final String REGEX_FORBBIDEN = "(\\(|\\)|\"|\')";
    private static final String REGEX_TELEPHONENUMBER = "^(\\([0]{0,1}[1-9][0-9]\\)){0,1}(\\s)?[0-9]{4}[\\-]{0,1}[0-9]{4}$";
    private static final String REGEX_NUMBER = "^[0-9]+$";

    /**
     * Creates a new UtteranceProcess specifying the project path
     *
     * @param enginePath the project path
     */
    public UtteranceProcess(String enginePath) {
        UtteranceProcess.dictionaryPath = enginePath + File.separator + "resources" + File.separator + "dictionary";
    }

    public static void importWords(String path) throws FileNotFoundException {
        System.out.println("Path: " + path);

        try {
            File file = new File(path);
            FileReader reader = new FileReader(file);
            BufferedReader buffer = new BufferedReader(reader);

            String linha = "";

            while (linha != null) {
                {
                    String[] splittedLine = buffer.readLine().split("    ");
                    PhoneticRepresentation p = new PhoneticRepresentation();
                    p.setDescription(splittedLine[0]);
                    List<String> phoneticRepresentations = new ArrayList<String>();
                    //System.out.println("Tamanho: " + splittedLine.length + " | Palavra: " + splittedLine[0]);
                    try {
                        phoneticRepresentations.add(splittedLine[1]);

                        p.setPhoneticRepresentation(phoneticRepresentations);

                        PhoneticRepresentationProcess.insertPhoneticRepresentation(p, "resources/dictionary/");
                    } catch (Exception ex) {
                        System.out.println("Erro!");
                        System.out.println("Tamanho: " + splittedLine.length + " | Palavra: " + splittedLine[0]);
                        System.out.println(ex.getMessage());
                    }
                }


            }
        } catch (IOException ex) {
            Logger.getLogger(UtteranceProcess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static List<Phrase> convertUtteranceToPhrases(String description) throws UtteranceException  {
        return UtteranceProcess.convertUtteranceToPhrases(description, "resources/exceptions.xml");
    }

    public static List<Phrase> convertUtteranceToPhrases(String description, String xmlLocation) throws UtteranceException {
        List<Phrase> phrases;
        try {
            phrases = new ArrayList<Phrase>();
            int phraseBegin = 0;
            for (int a = 0; a < description.length(); a++) {
                String character = String.valueOf(description.charAt(a));
                if (Pattern.compile(Phrase.PHRASE_SEPARATORS).matcher(character).find()) {
                    String phraseDescription = preProcessPhrase(description.substring(phraseBegin, a), xmlLocation).replaceAll(Phrase.PHRASE_MARKS, "");
                    Phrase phrase = new Phrase();
                    phrase.setDescription(phraseDescription);
                    phrase.setQuestionFlag(character.equals("nu?"));
                    phrase.setWords(convertPhraseToWords(phrase));
                    phrases.add(phrase);
                    phraseBegin = a + 1;
                }
            }
            if (phraseBegin < description.length()) {
                String phraseDescription = preProcessPhrase(description.substring(phraseBegin, description.length()), xmlLocation).replaceAll(Phrase.PHRASE_MARKS, "");
                Phrase phrase = new Phrase();
                if (!phraseDescription.isEmpty()) {
                    phrase.setDescription(phraseDescription);
                    phrase.setQuestionFlag(String.valueOf(description.charAt(description.length() - 1)).equals("?"));
                    phrase.setWords(convertPhraseToWords(phrase));
                    phrases.add(phrase);
                }
            }
        } catch (Exception ex) {
            throw new UtteranceException(ex.getMessage());
        }
        return phrases;
    }

    public static List<Word> convertPhraseToWords(Phrase phrase) {
        List<Word> words = new ArrayList<Word>();

        try {
            String[] phraseDescription = phrase.getDescription().split("(-|\\s)");
            //ArrayList<WordException> wordExceptions = properties.getWordExceptions();
            for (int a = 0; a < phraseDescription.length; a++) {
                String currentWord = phraseDescription[a];
                //String nextWord = (a < phraseDescription.length - 1) ? phraseDescription[a + 1] : "";
                //WordException wordException = new WordException();
                //wordException.setWord(phraseDescription[a]);
                //int wordExceptionIndex = wordExceptions.indexOf(wordException);
                //System.out.println("Palavra: " + currentWord);
                String phonetics = "";
                phonetics = GraphemeToPhoneme.toPhonemes(currentWord, dictionaryPath);
                // System.out.println("Phonema: " + currentWord);
                String syllabification = Syllabification.toSyllables(phonetics);
                // System.out.println("Syllables done ");
                String grammarClassification = GrammaticalClassification.getWordClassification(currentWord);
                //  System.out.println("Grammar classification done");
                List<Syllable> syllables = convertWordToSyllables(syllabification);
                List<String> phoneticRepresentation = new ArrayList<String>();
                phoneticRepresentation.add(UtteranceUtil.removeStressFlag(phonetics));
                Word word = new Word();
                word.setDescription(phraseDescription[a]);
                word.setGrammaticalClassification(grammarClassification);
                word.setSyllables(syllables);
                word.setPhoneticRepresentations(phoneticRepresentation);
                word.setQuestionFlag(phrase.getQuestionFlag());
                words.add(word);
            }

        } catch (Exception ex) {
            System.out.println("Erro: " + ex.getMessage());
        }
        return words;
    }

    private static List<Syllable> convertWordToSyllables(String word) {

        List<Syllable> syllables = new ArrayList<Syllable>();
        String stressedSyllable = UtteranceUtil.getStressedSyllable(word);
        String[] syllablesArray = UtteranceUtil.removeStressFlag(word).split("-");
        for (int indexSyllable = 0; indexSyllable < syllablesArray.length; indexSyllable++) {
            Syllable syllable = new Syllable();
            syllable.setIndexInWord(indexSyllable + 1);
            syllable.setDescription(syllablesArray[indexSyllable]);
            syllable.setStressed(stressedSyllable.equals(String.valueOf(indexSyllable)));
            syllable.setPhones(convertSyllableToPhones(syllablesArray[indexSyllable]));
            syllables.add(syllable);
        }
        return syllables;
    }

    private static List<String> convertSyllableToPhones(String syllable) {

        //  System.out.println(" -- convertSyllableToPhones");

        syllable = syllable.replaceAll("", " ").trim();
        // fix tilde
        syllable = syllable.replaceAll("[ ]{1,}[~][ ]{0,}", "~ ").replaceAll("\\s+", " ").trim();
        // return as arraylist
        return new ArrayList<String>(Arrays.asList(syllable.split(" ")));
    }

    public static String preProcessPhrase(String phrase) throws UtteranceException {
        return UtteranceProcess.preProcessPhrase(phrase, "resources/exceptions.xml");
    }

    public static String preProcessPhrase(String phrase, String xmlLocation) throws UtteranceException {

        String processedPhrase = UtteranceUtil.fixBlankSpaces(phrase.toLowerCase()).replaceAll("-", " ");

        try {
            processedPhrase = removeForbidden(processedPhrase);
            //processedPhrase = replaceExceptions(processedPhrase, xmlLocation);
            processedPhrase = replaceBlanks(processedPhrase);
            processedPhrase = replacePhoneNumbers(processedPhrase);
            processedPhrase = replaceHours(processedPhrase);
            processedPhrase = replaceDates(processedPhrase);
            processedPhrase = replaceCurrency(processedPhrase);
            processedPhrase = replacePercentage(processedPhrase);
            processedPhrase = replaceOrdinals(processedPhrase, Gender.FEMININO);
            processedPhrase = replaceOrdinals(processedPhrase, Gender.MASCULINO);
            processedPhrase = replaceNumbers(processedPhrase);
            //processedPhrase = replaceLetters(processedPhrase);
        } catch (Exception ex){
            throw new UtteranceException("error in method preProcessPhrase: "+ex.getMessage());
        }

        return processedPhrase.trim();
    }

    private static String removeForbidden(String phrase) throws UtteranceException {
        try {
            return phrase.replaceAll(REGEX_FORBBIDEN, "");
        } catch (Exception ex){
            throw new UtteranceException("error in method removeForbidden: "+ex.getMessage());
        }
    }

    private static String replacePhoneNumbers(String phrase) throws UtteranceException {
        String processedPhrase = "";
        try {
            for (String word : phrase.toLowerCase().split(" ")) {
                if ((matcher = Pattern.compile(REGEX_TELEPHONENUMBER).matcher(word)).find()) {
                    for (String number : word.substring(matcher.start(), matcher.end()).replaceAll("(\\(|\\)|\\-)", "").split("")) {
                        processedPhrase += " " + NumberToWord.getExtenseText(Integer.parseInt(number));
                    }
                } else {
                    processedPhrase += " " + word;
                }
            }
        } catch (Exception ex){
            throw new UtteranceException("error in method replacePhoneNumbers: "+ex.getMessage());
        }
        return processedPhrase;
    }

    private static String replaceHours(String phrase) throws UtteranceException {        
        String processedPhrase = phrase;
        try {
            String regex = "(([01]?[0-9]|2[0-3]):[0-5][0-9](h?hs?))|(([0-1][0-9]|2[0-3])h?hs?)|[0-9]h?hs?|(([01]?[0-9]|2[0-3]):[0-5][0-9])";

            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher hourMatcher = pattern.matcher(processedPhrase);

            String timeLiteral = "";

            while (hourMatcher.find()) {
                String timeFound = hourMatcher.group();
                timeLiteral = ExtensoHoras.convertTimetoLiteral(timeFound);
                processedPhrase = processedPhrase.replace(timeFound, timeLiteral);
            }
        } catch (Exception ex){
            throw new UtteranceException("error in method replaceHours: "+ex.getMessage());
        }
        return processedPhrase;
    }

    private static String replaceDates(String phrase) throws UtteranceException {
        String processedPhrase = phrase;
        try {
            String[] months = {"", "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
                "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};

            String regEx = "(\\d{1,2}\\s?[/\\-]\\s?)?\\d{1,2}\\s?[/\\-]\\s?(\\d{2,4})?";

            Long day;
            int month;
            Long longYear;
            String dayLiteral;
            String monthLiteral;
            String yearLiteral;
            String dateLiteral;

            matcher = Pattern.compile(regEx).matcher(processedPhrase);

            while (matcher.find()) {
                String dateFound = matcher.group();
                String[] splitDate = dateFound.split("[-/]");

                if (splitDate.length == 3) { //se a data for completa, dd/mm/yyyy
                    day = Long.valueOf(splitDate[0].trim()).longValue();
                    month = Integer.valueOf(splitDate[1].trim()).intValue();
                    longYear = Long.valueOf(splitDate[2].trim()).longValue();

                    dayLiteral = NumberToWord.getExtenseText(day);
                    monthLiteral = months[month];
                    yearLiteral = NumberToWord.getExtenseText(longYear).trim();
                    dateLiteral = dayLiteral + " de " + monthLiteral + " de " + yearLiteral;
                    processedPhrase = processedPhrase.replace(dateFound, dateLiteral);

                } else if (splitDate.length == 2) { //se a data for incompleta xx/xx
                    Long number1 = Long.valueOf(splitDate[0].trim()).longValue();
                    Long number2 = Long.valueOf(splitDate[1].trim()).longValue();

                    String number1Literal = NumberToWord.getExtenseText(number1);
                    String number2Literal = NumberToWord.getExtenseText(number2);
                    dateLiteral = number1Literal + " do " + number2Literal;
                    processedPhrase = processedPhrase.replace(dateFound, dateLiteral);
                }
            }
        } catch (Exception ex){
            throw new UtteranceException("error in method replaceDates: "+ex.getMessage());
        }
        return processedPhrase;
    }

    private static String replaceExceptions(String phrase, String xmlLocation) throws UtteranceException {
        String processedPhrase = phrase;
        try {
            WordExceptionProcess wordExcepionProcess = new WordExceptionProcess();
            processedPhrase = wordExcepionProcess.process(phrase, xmlLocation);
        } catch (Exception ex){
            throw new UtteranceException("error in method replaceExceptions: "+ex.getMessage());
        }            
        return processedPhrase;
    }

    private static String replaceCurrency(String phrase) throws UtteranceException {
        String processedPhrase = phrase;
        try {
            String regEx = "((EUR|BRL|USD|CAD|€|[RrUuCc]?\\$)\\s?)\\s?(\\d+[,\\.]?)+";
            matcher = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE).matcher(processedPhrase);

            while (matcher.find()) {

                String currencyFound = matcher.group();
                String money = ""; // o valor antes da vírgula
                String cents = ""; // os centavos
                String centsLiteral = "";
                String lastChar = "";
                String separador = "";

                String regExNumber = "(\\d+[,\\.]?)+";
                Pattern numberPattern = Pattern.compile(regExNumber, Pattern.CASE_INSENSITIVE);
                Matcher numberMatcher = numberPattern.matcher(currencyFound);
                String numberFound = "";

                if (numberMatcher.find()) {
                    numberFound = numberMatcher.group();
                    //remove trailing "." and ","
                    lastChar = numberFound.substring(numberFound.length() - 1);
                    if (lastChar.equals(".") || lastChar.equals(",")) {
                        numberFound = numberFound.substring(0, numberFound.length() - 1);
                    } else {
                        lastChar = "";
                    }
                    String[] splitNumber = null;
                    if (numberFound.contains(".") & numberFound.contains(",")) {
                        if (numberFound.lastIndexOf(",") > numberFound.lastIndexOf(".")) {
                            splitNumber = numberFound.split(",");
                        } else if (numberFound.lastIndexOf(",") < numberFound.lastIndexOf(".")) {
                            splitNumber = numberFound.split("\\.");
                        }
                        money = splitNumber[0];
                        money = money.replaceAll("[\\.,]", "");
                        Long moneyLong = Long.valueOf(money).longValue();
                        money = NumberToWord.getExtenseText(moneyLong);
                        cents = splitNumber[1];
                        cents = NumberToWord.getExtenseText(Long.parseLong(cents));
                    } else if (numberFound.contains(".") & !numberFound.contains(",") & numberFound.lastIndexOf(".") == numberFound.indexOf(".")) {
                        splitNumber = numberFound.split("\\.");
                        if (splitNumber[1].length() == 2) {
                            money = splitNumber[0];
                            cents = splitNumber[1];
                            Long centsLong = Long.valueOf(cents).longValue();
                            cents = NumberToWord.getExtenseText(centsLong);
                        } else if (splitNumber[1].length() == 1) {
                            money = splitNumber[0];
                            cents = splitNumber[1];
                            Long centsLong = Long.valueOf(cents).longValue();
                            cents = NumberToWord.getExtenseText(centsLong);
                            separador = "ponto";
                        } else {
                            money = numberFound.replaceAll("[\\.,]", "");
                        }
                        Long moneyLong = Long.valueOf(money).longValue();
                        money = NumberToWord.getExtenseText(moneyLong);

                    } else if (!numberFound.contains(".") & numberFound.contains(",") & numberFound.lastIndexOf(",") == numberFound.indexOf(",")) {
                        splitNumber = numberFound.split(",");
                        if (splitNumber[1].length() == 2) {
                            money = splitNumber[0];
                            cents = splitNumber[1];
                            Long centsLong = Long.valueOf(cents).longValue();
                            cents = NumberToWord.getExtenseText(centsLong);
                        } else if (splitNumber[1].length() == 1) {
                            money = splitNumber[0];
                            cents = splitNumber[1];
                            Long centsLong = Long.valueOf(cents).longValue();
                            cents = NumberToWord.getExtenseText(centsLong);
                            separador = "vírgula";
                        } else {
                            money = numberFound.replaceAll("[\\.,]", "");
                        }
                        Long moneyLong = Long.valueOf(money).longValue();
                        money = NumberToWord.getExtenseText(moneyLong);

                    } else {
                        money = numberFound;
                        money = money.replaceAll("[\\.,]", "");
                        Long moneyLong = Long.valueOf(money).longValue();
                        money = NumberToWord.getExtenseText(moneyLong);
                        cents = "";
                    }
                }

                String regExComplement = "((EUR|BRL|USD|CAD|€|[RrUuCc]?\\$)\\s?)";
                Pattern complementPattern = Pattern.compile(regExComplement, Pattern.CASE_INSENSITIVE);
                Matcher complementMatcher = complementPattern.matcher(currencyFound);
                String complementFound = "";

                while (complementMatcher.find()) {
                    if (!complementMatcher.group().equals("")) {
                        complementFound = complementMatcher.group().trim();

                        if (complementFound.equalsIgnoreCase("EUR") || complementFound.equalsIgnoreCase("€") || complementFound.contains("euro")) {
                            if (!money.equalsIgnoreCase("um")) {
                                complementFound = "euros";
                            } else {
                                complementFound = "euro";
                            }
                            if (cents.equalsIgnoreCase("zero")) {
                                centsLiteral = "";
                            } else if (cents.equalsIgnoreCase("um")) {
                                centsLiteral = "cent";
                            } else {
                                centsLiteral = "cents";
                            }
                        } else if (complementFound.equalsIgnoreCase("BRL") || complementFound.equalsIgnoreCase("R$") || complementFound.contains("rea")) {
                            if (!money.equalsIgnoreCase("um")) {
                                complementFound = "reais";
                            } else {
                                complementFound = "real";
                            }
                            if (cents.equalsIgnoreCase("zero")) {
                                centsLiteral = "";
                            } else if (cents.equalsIgnoreCase("um")) {
                                centsLiteral = "centavo";
                            } else {
                                centsLiteral = "centavos";
                            }
                        } else if (complementFound.equalsIgnoreCase("USD") || complementFound.equalsIgnoreCase("U$") || complementFound.contains("dólar")) {
                            if (!money.equalsIgnoreCase("um")) {
                                complementFound = "dólares";
                            } else {
                                complementFound = "dólar";
                            }
                            if (cents.equalsIgnoreCase("zero")) {
                                centsLiteral = "";
                            } else if (cents.equalsIgnoreCase("um")) {
                                centsLiteral = "cent";
                            } else {
                                centsLiteral = "cents";
                            }
                        } else if (complementFound.equalsIgnoreCase("CAD") || complementFound.equalsIgnoreCase("C$")) {
                            if (!money.equalsIgnoreCase("um")) {
                                complementFound = "dólares canadenses";
                            } else {
                                complementFound = "dólar canadense";
                            }
                            if (cents.equalsIgnoreCase("zero")) {
                                centsLiteral = "";
                            } else if (cents.equalsIgnoreCase("um")) {
                                centsLiteral = "cent";
                            } else {
                                centsLiteral = "cents";
                            }
                        }
                    }
                }
                if (!complementFound.equalsIgnoreCase("")) {
                    if (!cents.equals("") & !cents.equalsIgnoreCase("zero")) {
                        processedPhrase = processedPhrase.replace(currencyFound, money + " " + complementFound + " e " + cents + " " + centsLiteral + lastChar);
                    } else if (!separador.equals("")) {
                        processedPhrase = processedPhrase.replace(currencyFound, money + " " + separador + " " + complementFound + lastChar);
                    } else {
                        processedPhrase = processedPhrase.replace(currencyFound, money + " " + complementFound + lastChar);
                    }
                }
            }
        } catch (Exception ex){
            throw new UtteranceException("error in method replaceCurrency: "+ex.getMessage());
        }
        return processedPhrase;
    }

    private static String replacePercentage(String phrase) throws UtteranceException {
        String processedPhrase = phrase;
        try {
            String literalNumber = "";
            for (String s : phrase.split(" ")) {
                if ((matcher = Pattern.compile("\\d+([,\\.]?\\d{0,2}?%)").matcher(s)).find()) {

                    String percentage = matcher.group();
                    String onlyNumbers = percentage.replace("%", "");

                    String[] number = Decimals.splitIfDecimal(onlyNumbers);
                    for (String string : number) {
                        if (string != null) {
                            literalNumber = literalNumber + string + " ";
                        }
                    }


                    processedPhrase = processedPhrase.replace(percentage, literalNumber + "por cento");

                }
            }
        } catch (Exception ex){
            throw new UtteranceException("error in method replacePercentage: "+ex.getMessage());
        }
        return processedPhrase;
    }

    private static String replaceOrdinals(String phrase, Gender gender) throws UtteranceException {
        String processedPhrase = phrase;
        try {        
            String literalNumber;
            String regEx;
            String unicodeChar;

            if (gender == Gender.MASCULINO) {
                regEx = "\\d+\\s?\\u00ba";
                unicodeChar = "\\u00ba";
            } else {
                regEx = "\\d+\\s?\\u00aa";
                unicodeChar = "\\u00aa";
            }
            while ((matcher = Pattern.compile(regEx).matcher(processedPhrase)).find()) {
                String ordinal = matcher.group();
                String number = matcher.group().replaceAll(unicodeChar, "");
                Long numberLong = Long.valueOf(number).longValue();
                literalNumber = Ordinal.converte(numberLong, gender);
                processedPhrase = processedPhrase.replace(ordinal, literalNumber.trim());
            }
        } catch (Exception ex){
            throw new UtteranceException("error in method replaceOrdinals: "+ex.getMessage());
        }            
        return processedPhrase;
    }

    private static String replaceLetters(String phrase) throws UtteranceException {
        String processedPhrase = "";
        try {            
            String[] letters = {"a", "b", "c", "d", "é", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "ó", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
            String[] representations = {"á", "bê", "cê", "dê", "é", "í", "éfi", "gê", "agá", "í", "jóta", "ká", "éli", "êmi", "êni", "ó", "ú", "pê", "quê", "érri", "éssi", "tê", "ú", "vê", "dábliu", "xiz", "ípsilon", "zê"};

            for (String word : phrase.toLowerCase().split(" ")) {
                boolean finded = false;
                for (int i = 0; i < letters.length; i++) {
                    if (letters[i].equals(word) || letters[i].equals(word + ".")) {
                        processedPhrase += " " + representations[i];
                        finded = true;
                        break;
                    }
                }
                if (!finded) {
                    processedPhrase += " " + word;
                } else {
                    finded = false;
                }
            }
        } catch (Exception ex){
            throw new UtteranceException("error in method replaceLetters: "+ex.getMessage());
        }  
        return processedPhrase;
    }

    private static String replaceNumbers(String phrase) throws UtteranceException {
        String processedPhrase = "";
        try {
            for (String word : phrase.toLowerCase().split(" ")) {
                if (Pattern.compile(REGEX_NUMBER).matcher(word.replaceAll(Pattern.quote("."), "")).find()) {
                    processedPhrase += NumberToWord.getExtenseText(Integer.parseInt(word.replaceAll(Pattern.quote("."), "")));
                } else {
                    processedPhrase += " " + word;
                }
            }
        } catch (Exception ex){
            throw new UtteranceException("error in method replaceNumbers: "+ex.getMessage());
        }             
        return processedPhrase;
    }

    private static String replaceBlanks(String phrase) throws UtteranceException {
        String processedPhrase = phrase;
        try {
            String regEx = "\\s+";

            matcher = Pattern.compile(regEx).matcher(phrase);
            while (matcher.find()) {
                String blanksFound = matcher.group();
                processedPhrase = processedPhrase.replace(blanksFound, " ");
            }
        } catch (Exception ex){
            throw new UtteranceException("error in method replaceBlanks: "+ex.getMessage());
        }             
        return processedPhrase;
    }
}
