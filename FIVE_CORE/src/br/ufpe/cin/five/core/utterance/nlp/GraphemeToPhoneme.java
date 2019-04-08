/**
 * Copyright 2011 Federal University of Pernambuco. All Rights Reserved. Use is
 * subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.utterance.nlp;

import br.ufpe.cin.five.core.util.CharFilter;
import br.ufpe.cin.five.core.utterance.UtteranceException;
import br.ufpe.cin.five.core.utterance.UtteranceUtil;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 *
 * @author Carlos
 */
public class GraphemeToPhoneme {

    public static final String FLAG_STRESS = "\"";
    private static Matcher matcher1, matcher2;
    private static final String SP_SF = "(\\s|" + FLAG_STRESS + "){0,}";
    private static final String SF = "(" + FLAG_STRESS + ")";
    private static final String SP = "(\\s){0,}";
    private static final LetterList VOWELS_A = new LetterList("a", "â", "á", "à", "ã", "ä", "a~");
    private static final LetterList VOWELS_E = new LetterList("e", "ê", "é", "è", "ë", "e~", "E");
    private static final LetterList VOWELS_I = new LetterList("i", "î", "í", "ì", "ĩ", "ï", "i~");
    private static final LetterList VOWELS_O = new LetterList("o", "ô", "ó", "ò", "õ", "ö", "ô", "o~", "O");
    private static final LetterList VOWELS_U = new LetterList("u", "û", "ú", "ù", "ũ", "ü", "u~");
    private static final LetterList VOWELS = new LetterList(VOWELS_A, VOWELS_E, VOWELS_I, VOWELS_O, VOWELS_U);
//    TODO: substituir todos os y por i no pre-ṕrocessamento
//    private static final String    VOWELS_Y             = "y|ŷ|ý|ỳ|ÿ";
    private static final LetterList SEMIVOWELS_J = new LetterList("j", "j~");
    private static final LetterList SEMIVOWELS_W = new LetterList("w", "w~");
    private static final LetterList SEMIVOWELS = new LetterList(SEMIVOWELS_J, SEMIVOWELS_W);
    private static final LetterList ALL_VOWELS = new LetterList(VOWELS, SEMIVOWELS);
    private static final LetterList UNMARKED_VOWELS = new LetterList("a[^~]", "e[^~]", "i[^~]", "o[^~]", "u[^~]");
    private static final LetterList MARKED_VOWELS = new LetterList(VOWELS_A.less("a"), VOWELS_E.less("e", "E"), VOWELS_I.less("i"), VOWELS_O.less("o", "O"), VOWELS_U.less("u"));
    private static final LetterList MARKED_VOWELS_1 = new LetterList("á", "é", "í", "ó", "ú", "à", "è", "ì", "ò", "ù");
    private static final LetterList MARKED_VOWELS_2 = new LetterList("â", "ê", "î", "ô", "û");
    private static final LetterList MARKED_VOWELS_3 = new LetterList("ã", "õ");
    private static final LetterList MARKED_VOWELS_4 = new LetterList("ü");
    private static final LetterList SINGLE_NORMAL_CONSONANTS = new LetterList("b", "c", "ç", "d", "f", "g", "h", "j", "k", "l", "m", "n", "p", "q", "r", "s", "t", "v", "x", "w", "z");
    private static final LetterList SINGLE_PHONETIC_CONSONANTS = new LetterList("Z", "R", "L", "J", "S");
    private static final LetterList DOUBLE_NORMAL_CONSONANTS = new LetterList("lh", "nh", "qu", "gu", "ch");
    private static final LetterList DOUBLE_PHONETIC_CONSONANTS = new LetterList("dZ", "tS");
    private static final LetterList NORMAL_CONSONANTS = new LetterList(SINGLE_NORMAL_CONSONANTS, DOUBLE_NORMAL_CONSONANTS);
    private static final LetterList USAGE_CONSONANTS = new LetterList(SINGLE_NORMAL_CONSONANTS, SINGLE_PHONETIC_CONSONANTS, DOUBLE_PHONETIC_CONSONANTS);
    private static final LetterList ALL_CONSONANTS = new LetterList(SINGLE_NORMAL_CONSONANTS, SINGLE_PHONETIC_CONSONANTS, DOUBLE_NORMAL_CONSONANTS, DOUBLE_PHONETIC_CONSONANTS);
    private static final LetterList NORMAL_LETTERS = new LetterList(VOWELS, USAGE_CONSONANTS);
    private static final LetterList ALL_LETTERS = new LetterList(ALL_VOWELS, ALL_CONSONANTS);
    private static final LetterList MALE_PRONOUMS = new LetterList("este", "estes", "esse", "esses", "aquele", "aqueles", "daquele", "daqueles", "ele", "eles", "dele", "deles", "nele", "neles");
    private static final LetterList FEMALE_PRONOUMS = new LetterList("esta", "estas", "essa", "essas", "aquela", "aquelas", "daquela", "daquelas", "ela", "elas", "dela", "delas", "nela", "nelas");
    private static final LetterList OR_EXCEPTIONS = new LetterList("maior(es)?", "menor(es)?", "melhor(es)?", "pior(es)?", "suor(es)?");
    private static final LetterList OR2_EXCEPTIONS = new LetterList("força(s)?", "acordo(s)?");
    private static final LetterList OZ_EXCEPTIONS = new LetterList("arroz");
    private static final LetterList QU_EXCEPTIONS = new LetterList("cinquenta", "tranquilo");
    private static final LetterList KS_EXCEPTIONS = new LetterList("oxítono", "oxítona", "oxítonos", "oxítonas", "oxidar", "oxidação", "complexo", "anexar", "oxigênio", "oxíuro", "oxalato", "úxer", "uxoricida", "axila", "axiologia", "íxia", "táxi", "sintaxe");
    private static final LetterList KZ_EXCEPTIONS = new LetterList("ixofagia", "ixomielite", "ixolite", "ixômetro", "ixora", "ixoscopia", "ox-acético");
    private static final LetterList AX_EXCEPTIONS = new LetterList("máxim(a|o)(s)?");
    private static final LetterList EL_EXCEPTIONS = new LetterList("pelo(s)?|pela(s)?|cabelo(s)?|modelo(s)?|pesadelo(s)?|estrela(s)?");
    private static int ruleNumber = 0;

    private static int getStressedVowelIndex(String word) {

        word = word.replaceAll(" ", "");

        /*
         * RULE 01 ex: lhe
         *
         */
        if ((matcher1 = Pattern.compile("^(nh|lh)(" + VOWELS.less(MARKED_VOWELS_3) + ")(s)?$").matcher(word)).find()) {
            return matcher1.start() + 2;
        }

        /*
         * RULE 01 ex: galinha, zezinho
         *
         */
        if ((matcher1 = Pattern.compile("(nh|lh)(" + VOWELS.less(MARKED_VOWELS_3) + ")(s)?$").matcher(word)).find()) {
            return matcher1.start() - 1;
        }

        /*
         * RULE 02 ex: senhor, colher, mulher
         *
         */
        if ((matcher1 = Pattern.compile("(nh|lh)(" + VOWELS + ")(r)(a|e)?(s)?$").matcher(word)).find()) {
            return matcher1.start() + 2;
        }

        /*
         * RULE 03 ex: joão, órfão
         *
         */
        int index01 = 0, index02 = 0, index03 = 0, index04 = 0;
        int weight01 = 0, weight02 = 0, weight03 = 0, weight04 = 0;
        matcher1 = Pattern.compile("(" + MARKED_VOWELS + ")").matcher(word);
        while (matcher1.find()) {
            if (Pattern.matches("(" + MARKED_VOWELS_1 + ")", word.substring(matcher1.start(), matcher1.end()))) {
                weight01 = 4;
                index01 = matcher1.start();
            } else if (Pattern.matches("(" + MARKED_VOWELS_2 + ")", word.substring(matcher1.start(), matcher1.end()))) {
                weight02 = 3;
                index02 = matcher1.start();
            } else if (Pattern.matches("(" + MARKED_VOWELS_3 + ")", word.substring(matcher1.start(), matcher1.end()))) {
                weight03 = 2;
                index03 = matcher1.start();
            } else if (Pattern.matches("(" + MARKED_VOWELS_4 + ")", word.substring(matcher1.start(), matcher1.end()))) {
                weight04 = 1;
                index04 = matcher1.start() + 1;
            }
        }
        if ((weight01 > weight02) && (weight01 > weight03) && (weight01 > weight04)) {
            return index01;
        } else if ((weight02 > weight01) && (weight02 > weight03) && (weight02 > weight04)) {
            return index02;
        } else if ((weight03 > weight01) && (weight03 > weight02) && (weight03 > weight04)) {
            return index03;
        } else if ((weight04 > weight01) && (weight04 > weight02) && (weight04 > weight03)) {
            return index04;
        }

        /*
         * RULE 04 ex: uma, urso
         *
         */
        if ((matcher1 = Pattern.compile("^(" + VOWELS + ")(" + SINGLE_NORMAL_CONSONANTS + "){1,2}(" + VOWELS + ")(s)?$").matcher(word)).find()) {
            return matcher1.start();
        }

        /*
         * RULE 05 ex: caiu, traiu, pariu
         *
         */
        if ((matcher1 = Pattern.compile("(a)(" + SINGLE_NORMAL_CONSONANTS + ")?(i)(u)$").matcher(word)).find()) {
            return matcher1.end() - 2;
        }

        /*
         * RULE 05 ex: rua, crua, sua, tuas
         *
         */
        if ((matcher1 = Pattern.compile("(" + SINGLE_NORMAL_CONSONANTS + "){1,2}(o|u)(a)(s)?$").matcher(word)).find()) {
            String wordPart = word.substring(matcher1.start(), matcher1.end());
            String letter = wordPart.substring(0, wordPart.indexOf("u") == -1 ? wordPart.indexOf("o") : wordPart.indexOf("u"));
            if (letter.length() == 1) {
                return matcher1.start() + 1;
            } else {
                return matcher1.start() + 2;
            }
        }

        /*
         * RULE 05 ex: porque
         *
         */
        if ((matcher1 = Pattern.compile("(porque)(s)?$").matcher(word)).find()) {
            return matcher1.start() + 5;
        }

        /*
         * RULE 06 ex: caju, javali
         *
         */
        if ((matcher1 = Pattern.compile("(" + NORMAL_CONSONANTS + ")(i|u)(s)?$").matcher(word)).find()) {
            if (Pattern.matches(".*(" + DOUBLE_NORMAL_CONSONANTS + ").*", word.substring(matcher1.start(), matcher1.end()))) // equals("qui")
            {
                return matcher1.start() + 2;
            } else {
                return matcher1.start() + 1;
            }
        }

        /*
         * RULE 07 ex: meluco, pernambuco, caduco, estavam
         *
         */
        if ((matcher1 = Pattern.compile("(" + NORMAL_CONSONANTS + ")(" + VOWELS + ")(" + NORMAL_CONSONANTS + "){1,3}(" + VOWELS + ")(s|m|ns)?$").matcher(word)).find()) { // ^&&[^aeo]
            String wordPart = word.substring(matcher1.start(), matcher1.end());
            if (Pattern.compile("^(" + DOUBLE_NORMAL_CONSONANTS + ")").matcher(wordPart).find()) {
                return matcher1.start() + 2;
            } else {
                return matcher1.start() + 1;
            }
        }

        /*
         * RULE 08 ex: coisa, roupa, repouso
         *
         */
        if ((matcher1 = Pattern.compile("(" + VOWELS.less("a", "e", "o") + ")(" + SINGLE_NORMAL_CONSONANTS.less("n") + "){1,2}(" + VOWELS + ")(s)?$").matcher(word)).find()) {
            return matcher1.start() - 1;
        }

        /*
         * RULE 09 ex: zagueiro, mangueiras
         *
         *
         */
        if ((matcher1 = Pattern.compile("[^qc](u|o)(" + VOWELS + "){1,2}(" + NORMAL_LETTERS + "){1,2}(s|m)?$").matcher(word)).find()) {
            return matcher1.start() + 2;
        }

        /*
         * RULE 10 ex: creem, voos
         *
         */
        if ((matcher1 = Pattern.compile("(eem|êem|oo(s)?|ôo(s)?)$").matcher(word)).find()) {
            return matcher1.start();
        }

        /*
         * RULE 11 ex: saia, ensaio
         *
         */
        if ((matcher1 = Pattern.compile("(" + VOWELS.less(VOWELS_U) + ")(" + VOWELS.less(VOWELS_U) + "){1,2}(s|m)?$").matcher(word)).find()) {
            return matcher1.start();
        }

        /*
         * RULE 12 ex: porta, canja, dente
         *
         */
        if ((matcher1 = Pattern.compile("(" + VOWELS.less(VOWELS_I, VOWELS_U) + ")(" + SINGLE_NORMAL_CONSONANTS + "){1,2}(" + VOWELS + ")(s)?$").matcher(word)).find()) {
            return matcher1.start();
        }

        /*
         * RULE 13 ex: freira, azeite, auge
         *
         */
        if ((matcher1 = Pattern.compile("(" + VOWELS.less(VOWELS_U) + ")(" + VOWELS_I.plus(VOWELS_U) + ")(" + SINGLE_NORMAL_CONSONANTS.less("n") + "){1,2}(" + VOWELS + ")(s|m)?$").matcher(word)).find()) {
            return matcher1.start();
        }

        /*
         * RULE 14 ex: ainda, caindo, influindo
         *
         */
        if ((matcher1 = Pattern.compile("(" + VOWELS.less(VOWELS_U) + ")(" + VOWELS_I.plus(VOWELS_U) + ")(" + SINGLE_NORMAL_CONSONANTS + "){1,2}(" + VOWELS + ")(s)?$").matcher(word)).find()) {
            return matcher1.start() + 1;
        }

        /*
         * RULE 15 ex: pudim, bombom, comuns, alguma
         *
         */
        if ((matcher1 = Pattern.compile("[iou]{1}(m|n)(s)?$").matcher(word)).find()) {
            return matcher1.start();
        }

        /*
         * RULE 16 ex: propor, carrossel, durex, rapaz
         *
         */
        if ((matcher1 = Pattern.compile("[rlzxd]$").matcher(word)).find()) {
            return matcher1.start() - 1;
        }

        /*
         * RULE 17 ex: pneu, grau
         *
         */
        if ((matcher1 = Pattern.compile("(" + SINGLE_NORMAL_CONSONANTS + ")(" + VOWELS.less(VOWELS_U) + ")(" + VOWELS.less(VOWELS_E) + ")(s)?$").matcher(word)).find()) {
            return matcher1.start() + 1;
        }

        /*
         * RULE 18 ex: henrique, choque, destaque, cheque
         *
         */
        if ((matcher1 = Pattern.compile("(" + VOWELS + ")(q)(u)(e)(s)?$").matcher(word)).find()) {
            return matcher1.start();
        }

        /*
         * RULE 19 ex: bosque, tanque, palanques
         *
         */
        if ((matcher1 = Pattern.compile("(" + SINGLE_NORMAL_CONSONANTS + ")(q)(u)(e)(s)?$").matcher(word)).find()) {
            return matcher1.start() - 1;
        }

        /*
         * RULE 20 demais.
         *
         */
        if ((matcher1 = Pattern.compile("(" + VOWELS + ")").matcher(word)).find()) {
            int start = matcher1.start();
            if (word.length() <= 2) {
                return start;
            } else {
                if (start + 1 >= word.length()) {
                    return start;
                } else {
                    if (word.substring(start + 1, start + 2).equalsIgnoreCase("a")
                            || word.substring(start + 1, start + 2).equalsIgnoreCase("e")
                            || //word.substring(start+1, start+2).equalsIgnoreCase("i") ||
                            word.substring(start + 1, start + 2).equalsIgnoreCase("o")) // ||
                    //word.substring(start+1, start+2).equalsIgnoreCase("u") )
                    {
                        return start + 1;
                    } else {
                        return start;
                    }
                }
            }
        }
        return -1;
    }

    public static String toPhonemes(String word) throws UtteranceException {
        
        try {
            boolean hasVowel = Boolean.FALSE;
        
            for (int a = 0; a < word.length(); a++) {
                if (VOWELS.contains(String.valueOf(word.charAt(a)))) {
                    hasVowel = Boolean.TRUE;
                    break;
                }
            }

            if (!hasVowel) {
                throw new Exception("ERRO: Palavra sem vogal: " + word);
            }

            String phonemes = UtteranceUtil.putStressFlag(word, getStressedVowelIndex(word));
            List<String> lettersList = UtteranceUtil.getListOf(UtteranceUtil.removeLetterMarks(word), Boolean.FALSE);

            for (String letter : lettersList) {

                if (letter.equalsIgnoreCase("a")) {
                    phonemes = applyLetterRules_A(phonemes);

                } else if (letter.equalsIgnoreCase("b")) {
                    phonemes = applyLetterRules_B(phonemes);

                } else if (letter.equalsIgnoreCase("c")) {
                    phonemes = applyLetterRules_C(phonemes);

                } else if (letter.equalsIgnoreCase("d")) {
                    phonemes = applyLetterRules_D(phonemes);

                } else if (letter.equalsIgnoreCase("e")) {
                    phonemes = applyLetterRules_E(word, phonemes);

                } else if (letter.equalsIgnoreCase("f")) {
                    phonemes = applyLetterRules_F(phonemes);

                } else if (letter.equalsIgnoreCase("g")) {
                    phonemes = applyLetterRules_G(phonemes);

                } else if (letter.equalsIgnoreCase("h")) {
                    phonemes = applyLetterRules_H(phonemes);

                } else if (letter.equalsIgnoreCase("i")) {
                    phonemes = applyLetterRules_I(phonemes);

                } else if (letter.equalsIgnoreCase("j")) {
                    phonemes = applyLetterRules_J(phonemes);

                } else if (letter.equalsIgnoreCase("k")) {
                    phonemes = applyLetterRules_K(phonemes);

                } else if (letter.equalsIgnoreCase("l")) {
                    phonemes = applyLetterRules_L(phonemes);

                } else if (letter.equalsIgnoreCase("m")) {
                    phonemes = applyLetterRules_M(phonemes);

                } else if (letter.equalsIgnoreCase("n")) {
                    phonemes = applyLetterRules_N(phonemes);

                } else if (letter.equalsIgnoreCase("o")) {
                    phonemes = applyLetterRules_O(word, phonemes);

                } else if (letter.equalsIgnoreCase("p")) {
                    phonemes = applyLetterRules_P(phonemes);

                } else if (letter.equalsIgnoreCase("q")) {
                    phonemes = applyLetterRules_Q(word, phonemes);

                } else if (letter.equalsIgnoreCase("r")) {
                    phonemes = applyLetterRules_R(phonemes);

                } else if (letter.equalsIgnoreCase("s")) {
                    phonemes = applyLetterRules_S(phonemes);

                } else if (letter.equalsIgnoreCase("t")) {
                    phonemes = applyLetterRules_T(phonemes);

                } else if (letter.equalsIgnoreCase("u")) {
                    phonemes = applyLetterRules_U(phonemes);

                } else if (letter.equalsIgnoreCase("v")) {
                    phonemes = applyLetterRules_V(phonemes);

                } else if (letter.equalsIgnoreCase("x")) {
                    phonemes = applyLetterRules_X(word, phonemes);

                } else if (letter.equalsIgnoreCase("w")) {                
                    phonemes = applyLetterRules_W(phonemes);

                } else if (letter.equalsIgnoreCase("y")) { 
                    phonemes = applyLetterRules_Y(phonemes);

                } else if (letter.equalsIgnoreCase("z")) {
                    phonemes = applyLetterRules_Z(phonemes);
                }                        
            }
            return phonemes;
        } catch(Exception ex){
            System.out.println("Erro na palavra: "+word+" "+ex.getMessage());
        }
        return null;
    }

    public static String toPhonemes(String word, String dictionaryPath) throws UtteranceException{        

        String phonemes = null;

        if (dictionaryPath == null || dictionaryPath.isEmpty()) {
            dictionaryPath = "resources/dictionary";
        }

        String dictionaryFile = dictionaryPath + File.separator + CharFilter.replaceSpecial(word.toLowerCase()).charAt(0) + ".dic";

        SAXBuilder builder = new SAXBuilder();
        try {           
            Document document = builder.build(dictionaryFile);
            Element root = document.getRootElement();
            for (Object obj : root.getChildren("word")) {
                Element e = (Element) obj;
                if (e.getChild("des").getText().equalsIgnoreCase(word)) {
                    for (Object o : e.getChildren("rep")) {
                        phonemes = ((Element) o).getText();
                        break;
                    }
                    break;
                }
            }                                    
        } catch (Exception ex) {           
            throw new UtteranceException(ex.getMessage());
        }                

        if (phonemes == null){
            phonemes = toPhonemes(word);
        }
        
        phonemes = UtteranceUtil.putStressFlag(phonemes, getStressedVowelIndex(word));
        phonemes = UtteranceUtil.fixBlankSpaces(phonemes).trim();
            
        return phonemes;
    }    

    private static String applyLetterRules_A(String phonetics) {

        String phonemes = phonetics;

        /*
         * rule 01 ex: ivan
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(a)" + SP + "(n)" + SP + "$").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(a)" + SP + "(n)" + SP + "$", " a~ ");
        }

        /*
         * rule 02 ex: andam
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(a)" + SP + "(m)" + SP + "$").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(a)" + SP + "(m)" + SP + "$", " a~ w~ ");
        }

        /*
         * rule 03 ex: banho
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(a|â)" + SP + "(n)" + SP + "(h)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.start() + 1, "(a|â)", " a~ ");
        }

        /*
         * rule 04 ex: ambulatório
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(a|â)" + SP + "(m|n)" + SP + "(" + USAGE_CONSONANTS.less("n", "h") + ")").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end() - 1, "(a|â)" + SP + "(m|n)", " a~ ");
        }

        /*
         * rule 05 ex: banana
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(a|â)" + SP + "(m|n)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.start() + 1, "(a|â)", " a~ ");
        }

        /*
         * rule 06 ex: avião
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(ã|â)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(ã|â)", " a~ ");
        }

        /*
         * rule 07 ex: casa
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(a|á|à)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(a|á|à)", " a ");
        }

        return UtteranceUtil.fixTildeSpace(phonemes);

    }

    private static String applyLetterRules_B(String phonetics) {

        String phonemes = phonetics;

        /*
         * rule 01 ex: abstrair
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(b)" + SP + "(s)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(b)", " b j ");
        }

        /*
         * rule 01 ex: barriga
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(b)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(b)", " b ");
        }

        return phonemes;

    }

    private static String applyLetterRules_C(String phonetics) {

        String phonemes = phonetics;

        /*
         * rule 01 ex: centro
         */
        ruleNumber++;
        matcher1 = Pattern.compile("c" + SP_SF + "(E|e|ê|i|j)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "c", " s ");
        }

        /*
         * rule 03 ex: mascherano
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(s|S)" + SP + "(c)" + SP + "(h)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(c)" + SP + "(h)", " k ");
        }

        /*
         * rule 03 ex: chuva
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(c)" + SP + "(h)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(c)" + SP + "(h)", " S ");
        }

        /*
         * rule 06 ex: casa
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(c)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(c)", " k ");
        }

        /*
         * rule 01 ex: cachaça
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(ç)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(ç)", " s ");
        }

        return phonemes;

    }

    private static String applyLetterRules_D(String phonetics) {

        String phonemes = phonetics;

        /*
         * rule 01 ex: dedo
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(d)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(d)", " d ");
        }

        return phonemes;
    }

    private static String applyLetterRules_E(String word, String phonetics) {

        String phonemes = phonetics;

        /*
         * rule 01 ex: dez
         */
        ruleNumber++;
        matcher1 = Pattern.compile("^" + SP + "(" + USAGE_CONSONANTS.less("m", "f") + ")?" + SP_SF + "(e)" + SP + "(s|z)" + SP + "$").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(e)", " E ");
        }

        /*
         * rule 02 ex: superficie
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(i)" + SP + "(e)" + SP + "$").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(e)", "");
        }

        /*
         * rule 03 ex: rebelde
         */
        ruleNumber++;
        matcher1 = Pattern.compile(SF + SP + "(e)" + SP + "(l)" + SP + "(" + USAGE_CONSONANTS.less("h") + ")").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(e)", " E ");
        }

        /*
         * rule 04 ex: papel
         */
        ruleNumber++;
        matcher1 = Pattern.compile(SF + SP + "(e)" + SP + "(l)" + SP + "$").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(e)", " E ");
        }

        /*
         * rule 05 ex: mãe
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(a~|o~|ã|õ)" + SP + "(e)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(e)", " j~ ");
        }

        /*
         * rule 06 ex: aeroporto
         */
        ruleNumber++;
        matcher1 = Pattern.compile("^" + SP + "(a)" + SP_SF + "(e|é)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(e)", " E ");
        }

        /*
         * rule 07 ex: plateia
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(d|t|p|l|s|R|r" + SP + "r|l" + SP + "m|w" + SP + "m|q" + SP + "u)" + SP + SF + SP + "(e)" + SP + "(i|j)" + SP + "(a)" + SP + "(s)?" + SP + "$").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(e)", " E ");
        }

        /*
         * rule 08 ex: complexos
         */
        ruleNumber++;
        matcher1 = Pattern.compile(SF + SP + "(e)" + SP + "(x)" + SP + "(u|o)" + SP + "(s)?" + SP + "$").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(e)", " E ");
        }

//        // rule 09
//        ruleNumber++;
//	matcher = Pattern.compile("^"+SP_SF+"(e|ê)"+SP+"(x|s)"+SP+"("+CONSONANTS+")").matcher(phonemes);
//        while ( matcher.find() )
//            phonemes = BaseMethods.replaceFound(ruleNumber, phonemes, matcher.start(), matcher.end(), "(e)", " e ");

//        // rule 10
//        ruleNumber++;
//	matcher = Pattern.compile("^"+SP_SF+"(e)"+SP+"[("+CONSONANTS+")&&[^mnx]]"+SP_SF+"("+VOWELS+")").matcher(phonemes);
//        while ( matcher.find() )
//            phonemes = BaseMethods.replaceFound(ruleNumber, phonemes, matcher.start(), matcher.end(), "(e)", " e ");

//        // rule 11
//        ruleNumber++;
//	matcher = Pattern.compile("(e|ê)"+SP+"(n"+SP+"e|m"+SP+"e|s"+SP+"a|s"+SP+"s"+SP+"a|z"+SP+"a)"+SP+"$").matcher(phonemes);
//        while ( matcher.find() )
//            phonemes = BaseMethods.replaceFound(ruleNumber, phonemes, matcher.start(), matcher.start()+1, "(e)", " e ");

        /*
         * rule 09 ex: embora
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(e|é|ê)" + SP + "(m|n|j~)" + SP + "(" + USAGE_CONSONANTS.less("h") + ")").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(e|é|ê)" + SP + "(m|n|j~)", " e~ "); // j~
        }
        /*
         * rule 10 ex: tema
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(e|é|ê)" + SP + "(m|n|j~)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(e|é|ê)", " e~ ");
        }

//        // rule 21
//        ruleNumber++;
//	matcher = Pattern.compile("(e)"+SP+"("+CONSONANTS+")?"+"(i)"+SP+"("+VOWELS+")").matcher(phonemes);
//        while ( matcher.find() )
//            phonemes = BaseMethods.replaceFound(ruleNumber, phonemes, matcher.start(), matcher.start()+1, "(e)", " E ");

        /*
         * rule 11 ex: canivete
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(e)" + SP + "(s)?" + SP + "$").matcher(phonemes);
        while (matcher1.find()) {
            int start = matcher1.start();
            int end = matcher1.end();
            ruleNumber++;
            ;
            if (!((matcher2 = Pattern.compile("(" + USAGE_CONSONANTS + ")(q)(u)(e)(s)?$").matcher(word)).find() || (matcher2 = Pattern.compile("^(q)(u)(e)(s)?$").matcher(word)).find())) {
                phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, start, end, "(e)", " i ");
            }
        }

//        // rule 24
//        ruleNumber++;
//	matcher = Pattern.compile("^"+SP_SF+"("+VOWELS+")?"+SP+"[("+CONSONANTS+")&&[^c]]?"+SP_SF+"(e)"+SP+"[("+CONSONANTS+")]{1,2}"+SP+"(a|o)"+SP+"(s)?"+SP+"$").matcher(phonemes);
//        while ( matcher.find() )
//            phonemes = BaseMethods.replaceFound(ruleNumber, phonemes, matcher.start(), matcher.end(), "(e)", " E ");
//
//
//        // rule 00
//        ruleNumber++;
//	matcher = Pattern.compile("^"+SP+"("+VOWELS+")"+SP+"(b|t)"+SP_SF+"(e)"+SP+"(r)"+SP+"(t)"+SP+"("+VOWELS+")"+SP+"$").matcher(phonemes);
//        while ( matcher.find() )
//            phonemes = BaseMethods.replaceFound(ruleNumber, phonemes, matcher.start(), matcher.end(), "(e)"+SP+"(r)", " E R ");
//
//        // rule 00
//        ruleNumber++;
//	matcher = Pattern.compile("^"+SP+"("+CONSONANTS+")"+SP_SF+"(e)"+SP+"("+UNVOICED_CONSONANTS+")"+SP_SF+"(e)"+SP+"[^~]").matcher(phonemes);
//        while ( matcher.find() )
//            phonemes = BaseMethods.replaceFound(ruleNumber, phonemes, matcher.start(), matcher.end(), "(e)", " E ");
//
//        // rule 00
//        ruleNumber++;
//	matcher = Pattern.compile("(e)"+SP+"(("+CONSONANTS+")"+SP+"){1,2}").matcher(phonemes);
//        while ( matcher.find() )
//            phonemes = BaseMethods.replaceFound(ruleNumber, phonemes, matcher.start(), matcher.start()+1, "(e)", " e ");
//
//         rule 00
//         ruleNumber++;
//	 matcher = Pattern.compile("(r|R|s|v|m|p|b|t|d){1,}"+SP_SF+"(e)"+SP+"([("+CONSONANTS+")&&[^sj]]"+SP+"){1,2}"+SP_SF+"(a|o|e|u)"+SP+"((t|c)"+SP+"("+VOWELS+")"+SP+")?(s)?").matcher(phonemes); // +SP+"$"
//
//        ruleNumber++;
//	matcher = Pattern.compile("(r|R|s|v|m|p|b|t|d){1,}"+SP_SF+"(e)"+SP+"([("+CONSONANTS+")&&[^sj]]"+SP+"){1,2}"+SP_SF+"(a|o|e|u)"+SP_SF+"("+VOWELS+")").matcher(phonemes);
//        while ( matcher.find() )
//            phonemes = BaseMethods.replaceFound(ruleNumber, phonemes, matcher.start(), matcher.end(), "(e)", " e ");

        /*
         * rule 12 ex: mestre
         */
        ruleNumber++;
//	matcher = Pattern.compile("(b|c|d|f|g|h|j|k|l|p|q|r|s|t|x|z|ç|R|J|S|Z)"+SP+SF+SP+"(e)"+SP+"([("+CONSONANTS+")&&[d]]"+SP+"){1,3}"+SP+"(e|i)"+SP+"(s)?"+SP+"$").matcher(phonemes);
        matcher1 = Pattern.compile("(" + VOWELS.less(VOWELS_I) + ")" + SP_SF + "(" + USAGE_CONSONANTS.less("r") + ")" + SP + SF + SP + "(e)" + SP + "((" + USAGE_CONSONANTS.less("m", "n", "d", "j") + ")" + SP + "){1,3}" + SP + "(e|i)" + SP + "(s)?" + SP + "$").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.start() + phonemes.substring(matcher1.start(), matcher1.end()).indexOf("e") + 1, "(e)", " E ");
        }

        /*
         * rule 13 ex: regra
         */
        ruleNumber++;
//        String rule = "(r|R|s|S|v|m|p|t|d){1,}"+SP_SF+"(e)"+SP+"(b|c|g|h|m|n|p|qu|r|t|v|x|ç|R|J|S|Z|\\s){1,2}"+SP_SF+"("+VOWELS+")"; // [("+CONSONANTS+"|qu)&&[^sfdkzj]]
        String rule = "(r|R|s|S|v|m|p|t|d){1,}" + SP_SF + "(e)" + SP + "(b|c|g|h|m|n|p|qu|r|t|v|x|R|J|S){1,2}" + SP_SF + "(" + VOWELS + ")"; // [("+CONSONANTS+"|qu)&&[^sfdkzj]]
        matcher1 = Pattern.compile(rule).matcher(phonemes);
        while (matcher1.find()) {
            int start = matcher1.start();
            int end = matcher1.end();
            ruleNumber++;
            matcher2 = Pattern.compile(rule + SP_SF + "(" + VOWELS + "|j|z|b|d|w|l|r|R" + ")").matcher(phonemes);
            if (!matcher2.find()) {
                phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, start, end, "(e)", " E ");
            }
        }

        /*
         * rule 14 ex: neto
         */
        ruleNumber++;
        matcher1 = Pattern.compile("^" + SP + "(m|n|l|p|t|b)" + SP + SF + SP + "(e)" + SP + "(k|l|t|v|f)").matcher(phonemes); // sf_sf
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(e)", " E ");
        }

        /*
         * rule 15 ex: cafezinho
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(" + USAGE_CONSONANTS.less("r") + ")" + SP + "(e)" + SP + "(z)" + SP_SF + "(i|i~|a~|e)" + SP + "((t)|(n)" + SP + "(h)|(o))").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(e)", " E ");
        }

        /*
         * rule 16 ex: martelo
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(e|E)" + SP + "(l)" + SP_SF + "(a|o|u)" + SP + "(s)?" + SP + "$").matcher(phonemes); // "[^pb]"+SP_SF+
        while (matcher1.find()) {
            int start = matcher1.start();
            int end = matcher1.end();
            ruleNumber++;
            matcher1 = Pattern.compile("^(" + EL_EXCEPTIONS + ")$").matcher(word);
            if (matcher1.find()) {
                phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, start, end, "(e|E)", " e ");
            } else {
                phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, start, end, "(e|E)", " E ");
            }
        }

        /*
         * rule 17 ex: este
         */
        ruleNumber++;
        matcher1 = Pattern.compile("^(" + MALE_PRONOUMS + ")$").matcher(word);
        while (matcher1.find()) {
            ruleNumber++;
            matcher1 = Pattern.compile(SF + SP + "(e|E)").matcher(phonemes);
            while (matcher1.find()) {
                phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(e|E)", " e ");
            }
        }

        /*
         * rule 18 ex: esta
         */
        ruleNumber++;
        matcher1 = Pattern.compile("^(" + FEMALE_PRONOUMS + ")$").matcher(word);
        while (matcher1.find()) {
            ruleNumber++;
            matcher1 = Pattern.compile(SF + SP + "(e|E)").matcher(phonemes);
            while (matcher1.find()) {
                phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(e|E)", " E ");
            }
        }

        /*
         * rule 19 ex: e
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(é)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(é)", " E ");
        }

        /*
         * rule 20 ex: e
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(ê|e)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(ê|e)", " e ");
        }


        return UtteranceUtil.fixTildeLetterCase(UtteranceUtil.fixTildeSpace(phonemes), "E");

    }

    private static String applyLetterRules_F(String phonetics) {

        String phonemes = phonetics;

        /*
         * rule 01 ex: faca
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(f)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(f)", " f ");
        }

        return phonemes;

    }

    private static String applyLetterRules_G(String phonetics) {

        String phonemes = phonetics;

        /*
         * rule 01 ex: geral
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(g)" + SP_SF + "(ê|é|í|e|i)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(g)", " Z ");
        }


        /*
         * rule 02 ex: quando
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(g)" + SP + "(ü|u)" + "(" + VOWELS + ")").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(g)" + SP + "(u)", " g ");
        }


        /*
         * rule 02 ex: guerra
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(g)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(g)", " g ");
        }

        return phonemes;

    }

    private static String applyLetterRules_H(String phonetics) {

        String phonemes = phonetics;

        /*
         * rule 01 ex: hoje
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(h)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(h)", "");
        }

        return phonemes;
    }

    private static String applyLetterRules_I(String phonetics) {

        String phonemes = phonetics;

        /*
         * rule 01 ex: muito
         */
        ruleNumber++;
        matcher1 = Pattern.compile(SF + SP + "(u)" + SP + "(i)" + SP + "(t)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(i)", " j~ ");
        }

        /*
         * rule 02 ex: apoio
         */
        ruleNumber++;
        //matcher = Pattern.compile("("+VOWELS+")"+SP+"(i)"+SP_SF+"("+VOWELS+")").matcher(phonemes);
        matcher1 = Pattern.compile("(" + VOWELS.less(VOWELS_I) + ")" + SP + "(i)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(i)", " j ");
        }

        /*
         * rule 03 ex: abstnência
         */
        ruleNumber++;
        //matcher = Pattern.compile("("+VOWELS+")"+SP+"(i)"+SP_SF+"("+VOWELS+")").matcher(phonemes);
//	matcher = Pattern.compile("(i)"+SP_SF+"("+VOWELS.less(VOWELS_I, MARKED_VOWELS)+")").matcher(phonemes);
//        while ( matcher.find() ) {
//            int start = matcher.start();
//            int end   = matcher.end();
//            ruleNumber++;
//            matcher = Pattern.compile(SF+SP+"(i)"+SP_SF+"("+VOWELS.less(VOWELS_I)+")").matcher(phonemes);
//            if ( !matcher.find() )
//                phonemes = BaseMethods.replaceFound(ruleNumber, phonemes, start, end, "(i)", " j ");
//        }

        /*
         * rule 04 ex: timbre
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(i|í)" + SP + "(m|n|j~)" + SP_SF + "(" + USAGE_CONSONANTS.less("h") + ")").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(i|í)" + SP + "(m|n|j~)", " i~ ");
        }

        /*
         * rule 05 ex: time
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(i|í)" + SP + "(m|n|j~)" + SP_SF + "(" + VOWELS + "|h)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(i|í)", " i~ ");
        }

        /*
         * rule 06 ex: amigo
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(i|í)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(i|í)", " i ");
        }

        return UtteranceUtil.fixTildeSpace(phonemes);

    }

    private static String applyLetterRules_J(String phonetics) {

        String phonemes = phonetics;

        /*
         * rule 01 ex: jiboia
         */
        ruleNumber++;
        matcher1 = Pattern.compile("^" + SP + "(j)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(j)", " Z ");
        }

        /*
         * rule 02 ex: queijo
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(j)" + SP + "(j)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(j)" + SP + "(j)", " j Z ");
        }

        /*
         * rule 03 ex: queijo
         */
        ruleNumber++;
        matcher1 = Pattern.compile("[^jZ ]" + SP + "(j)" + SP + "[^jZ ]").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(j)", " Z ");
        }

        return phonemes;
    }

    private static String applyLetterRules_K(String phonetics) {

        String phonemes = phonetics;

        /*
         * rule 01 ex: katia
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(k)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.start() + 1, "(k)", " k ");
        }

        return phonemes;

    }

    private static String applyLetterRules_L(String phonetics) {

        String phonemes = phonetics;

        /*
         * rule 01 ex: falha
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(l)" + SP + "(h)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(l)" + SP + "(h)", " L ");
        }

        /*
         * rule 02 ex: albino
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(" + VOWELS + ")" + SP + "(l)" + SP + "(" + USAGE_CONSONANTS.less("h") + ")").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(l)", " w ");
        }

        // rule 03
        ruleNumber++;
        matcher1 = Pattern.compile("(l)" + SP + "$").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(l)", " w ");
        }

        /*
         * rule 05 ex: galo
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(l)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(l)", " l ");
        }

        return phonemes;
    }

    private static String applyLetterRules_M(String phonetics) {

        String phonemes = phonetics;

//        // rule 01
//        ruleNumber++;
//	  matcher = Pattern.compile("(e|é|ê|i)"+SP+"m"+SP+"_("+VOWELS+")[a-z]{0,}").matcher(phonemes+"_"+nextWord);
//        while ( matcher.find() )
//            phonemes = BaseMethods.replaceFound(ruleNumber, phonemes, matcher.start()+1, matcher.end(), "(e|é|ê|i)"+SP+"m"+SP+"_("+VOWELS+")[a-z]{0,}", " J ");

        /*
         * rule 01 ex: alguém
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(e|é|ê|i|e~|i~|E)" + SP + "(m)" + SP + "$").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start() + 1, matcher1.end(), "(m)", " j~ ");
        }

        /*
         * rule 02 ex: maria
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(m)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(m)", " m ");
        }

        return phonemes;

    }

    private static String applyLetterRules_N(String phonetics) {

        String phonemes = phonetics;

        /*
         * rule 01 ex: ganho
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(n)" + SP + "(h)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(n)" + SP + "(h)", " J ");
        }

        /*
         * rule 02 ex: novela
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(n)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(n)", " n ");
        }

        return phonemes;

    }

    private static String applyLetterRules_O(String word, String phonetics) {

        String phonemes = phonetics;

        /*
         * rule 04 ex: cão
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(ã|a~)" + SP + "(o)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.end() - 1, matcher1.end(), "(o)", " w~ ");
        }

        /*
         * rule 05 ex: inicio
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(" + VOWELS.less(VOWELS_O) + ")" + SP + "(o)" + SP + "(s)?" + SP + "$").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(o)", " w ");
        }

        /*
         * rule 06 ex: vôo
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(ô)" + SP + "(o)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(ô)" + SP + "(o)", " o w ");
        }

        /*
         * rule 07 ex: coordenação
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(o)" + SP + "(o)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(o)" + SP + "(o)", " o ");
        }

        /*
         * rule 08 ex:
         */
        ruleNumber++;
        matcher1 = Pattern.compile(SF + SP + "(o)" + SP + "(l)" + SP + "(" + USAGE_CONSONANTS.less("v", "f", "c", "h", "s") + ")").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(o)", " O ");
        }

        /*
         * rule 09 ex: sol
         */
        ruleNumber++;
        matcher1 = Pattern.compile(SF + SP + "(o)" + SP + "(l)" + SP + "$").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(o)", " O ");
        }

        /*
         * rule 10 ex: gostosa
         */
        ruleNumber++;
        matcher1 = Pattern.compile("((o)" + SP + "(s)" + SP + "(a)" + SP + "(s)?|(o)" + SP + "(s)" + SP + "(o)" + SP + "(s))" + SP + "$").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.start() + 1, "(o)", " O ");
        }

        /*
         * rule 11 ex: ombro
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(o|ô)" + SP + "(m|n)" + SP + "(" + USAGE_CONSONANTS.less("h") + ")").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(o|ô)" + SP + "(m|n)", " o~ ");
        }

        /*
         * rule 12 ex: omelete
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(o|ô)" + SP + "(m|n)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(o|ô)", " o~ ");
        }

        /*
         * rule 13 ex: tempo
         */
        ruleNumber++;
        matcher1 = Pattern.compile("o" + SP + "(s)?" + SP + "$").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(o)", " u ");
        }

//
//        // rule 00
//        ruleNumber++;
//	matcher = Pattern.compile("(i)"+SF+"(o)"+SP+"("+CONSONANTS+")"+SF+"(a)"+SP+"$").matcher(phonemes);
//        while ( matcher.find() )
//            phonemes = BaseMethods.replaceFound(ruleNumber, phonemes, matcher.start(), matcher.end(), "(o)", " O ");

//        // rule 00
//        ruleNumber++;
//	matcher = Pattern.compile("("+CONSONANTS+")"+SP+"(o)"+SP+"("+CONSONANTS+")"+SF).matcher(phonemes);
//        while ( matcher.find() )
//            phonemes = BaseMethods.replaceFound(ruleNumber, phonemes, matcher.start(), matcher.end(), "(o)", " o ");

        /*
         * rule 14 ex: igualar a rule 15
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(" + NORMAL_LETTERS + ")" + SP_SF + "(o)" + SP + "(" + USAGE_CONSONANTS.less("ç", "m") + ")" + SP_SF + "(" + VOWELS.less(VOWELS_O) + ")").matcher(phonemes);
        while (matcher1.find()) {
            int start = matcher1.start();
            int end = matcher1.end();
            String firstLetter = phonemes.substring(matcher1.start() + 1, matcher1.end()).trim().substring(0, 1);

            if ("prRbmzkid".contains(firstLetter)) {
                phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, start, end, "(o)", " O ");
            } else {
                phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, start, end, "(o)", " o ");
            }

        }

        /*
         * rule 15 ex:
         */
        ruleNumber++;
        String rule = "(o)" + SP + "(" + USAGE_CONSONANTS + ")" + SP + "(" + USAGE_CONSONANTS + ")" + SP_SF + "(" + VOWELS + ")";
        matcher1 = Pattern.compile(rule).matcher(phonemes);
        while (matcher1.find()) {

            ruleNumber++;
            matcher1 = Pattern.compile("^(" + OR2_EXCEPTIONS + ")$").matcher(word);
            if (!matcher1.find()) {

                ruleNumber++;
                matcher1 = Pattern.compile("(o)" + SP + "((r)" + SP + "(g|m|d|ç|s)" + SP_SF + "(" + VOWELS + ")"
                        + "|(r)" + SP + "(t)" + SP_SF + "(" + VOWELS.less(VOWELS_U) + ")"
                        + "|(r)" + SP + "(n)" + SP_SF + "(" + VOWELS.less(VOWELS_E) + ")"
                        + "|(s)" + SP + "(t)" + SP_SF + "(" + VOWELS_A.plus(VOWELS_I) + ")"
                        + "|(l)" + SP + "(t)" + SP_SF + "(" + VOWELS + ")"
                        + "|(c)" + SP + "(r)" + SP_SF + "(" + VOWELS + ")"
                        + //                                                   "|(b)"+SP+"(r)"+SP_SF+"(i)"+SP+"(n)"+SP+"(h)"+SP+"(a)" +
                        "|(b)" + SP + "(r)" + SP_SF + "(" + VOWELS_A.plus(VOWELS_E) + ")"
                        + "|(g)" + SP + "(r)" + SP_SF + "(" + VOWELS_A.plus(VOWELS_E) + ")"
                        + "|(g)" + SP + "(r)" + SP_SF + "(i)" + SP + "(n)" + SP + "(h)" + SP + "(a)"
                        + ")").matcher(phonemes);
                if (matcher1.find()) {

                    phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end() - 1, "(o)", " O ");

                } else {

                    matcher1 = Pattern.compile(rule + SP + "(s)" + SP + "$").matcher(phonemes);
                    while (matcher1.find()) {
                        phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end() - 1, "(o)", " O ");
                    }
                    //                else
                    //                    phonemes = BaseMethods.replaceFound(ruleNumber, phonemes, matcher.start(), matcher.end(), "(o)", " o " );

                }

            }

        }

        ruleNumber++;
        matcher1 = Pattern.compile("(o)" + SP + "(z)" + SP + "$").matcher(phonemes);
        while (matcher1.find()) {
            int start = matcher1.start();
            int end = matcher1.end();
            ruleNumber++;
            matcher1 = Pattern.compile("^(" + OZ_EXCEPTIONS + ")$").matcher(word);
            if (matcher1.find()) {
                phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, start, end, "(o)", " o ");
            } else {
                phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, start, end, "(o)", " O ");
            }
        }

        /*
         * rule 01 ex: ovo
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(o|ô)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(o|ô)", " o ");
        }

        /*
         * rule 02 ex: acessório
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(ó)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(ó)", " O ");
        }

        /*
         * rule 03 ex: organizações
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(õ)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(õ)", " o~ ");
        }


        // rule 09 exceptions
        ruleNumber++;
        matcher1 = Pattern.compile("^(" + OR_EXCEPTIONS + ")$").matcher(word);
        while (matcher1.find()) {
            ruleNumber++;
            matcher1 = Pattern.compile("(o|O)").matcher(phonemes);
            while (matcher1.find()) {
                phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.start() + 1, "(o)", " O ");
            }
        }

        return UtteranceUtil.fixTildeLetterCase(UtteranceUtil.fixTildeSpace(phonemes), "O");

    }

    private static String applyLetterRules_P(String phonetics) {

        String phonemes = phonetics;

        /*
         * rule 01 ex: philipe
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(p)" + SP + "(h)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(p)" + SP + "(h)", " f ");
        }

        /*
         * rule 01 ex: philipe
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(p)" + SP + "(n)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(p)", " p i ");
        }

        /*
         * rule 02 ex: pato
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(p)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(p)", " p ");
        }

        return phonemes;

    }

    private static String applyLetterRules_Q(String word, String phonetics) {

        String phonemes = phonetics;

        /*
         * rule 12 ex: tranquilo, cinquenta
         */
        ruleNumber++;
        matcher1 = Pattern.compile("^(" + QU_EXCEPTIONS + ")$").matcher(word);
        while (matcher1.find()) {
            ruleNumber++;
            matcher1 = Pattern.compile("(q)" + SP + "(u|ü)").matcher(phonemes);
            if (matcher1.find()) {
                phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(q)" + SP + "(u|ü)", " k w ");
            }
        }

        /*
         * rule 01 ex: quem
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(q)" + SP + "(u)" + SP_SF + "(" + VOWELS.less(VOWELS_A) + ")").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(q)" + SP + "(u)", " k ");
        }

        /*
         * rule 02 ex: quando
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(q)" + SP + "(ü|u)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(q)" + SP + "(u)", " k w ");
        }

        return phonemes;

    }

    private static String applyLetterRules_R(String phonetics) {

        String phonemes = phonetics;

        /*
         * rule 01 ex: carro
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(r)" + SP + "(r)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(r)" + SP + "(r)", " R ");
        }

        /*
         * rule 02 ex: honra
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(n|a~|e~|i~|o~)" + SP + "(r)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(r)", " R ");
        }

//        // rule 02
//        ruleNumber++;
//	matcher = Pattern.compile("(r)"+SP+"_(r)[a-z]{0,}").matcher(phonemes+"_"+nextWord);
//        while ( matcher.find() )
//            phonemes = BaseMethods.replaceFound(ruleNumber, phonemes, matcher.start(), matcher.end(), "(r)"+SP+"_(r)[a-z]{0,}", " R ");

        /*
         * rule 03 ex: rato
         */
        ruleNumber++;
        matcher1 = Pattern.compile("^" + SP + "(r)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "^" + SP + "(r)", " R ");
        }

        /*
         * rule 04 ex: calor
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(r)" + SP + "$").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(r)", " R ");
        }

        /*
         * rule 05 ex: prato
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(r)" + SP_SF + "(" + VOWELS + ")").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.start() + 1, "(r)", " r ");
        }

        /*
         * rule 06 ex: barba
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(r)" + SP + "(" + USAGE_CONSONANTS.less("r") + ")").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.start() + 1, "(r)", " R ");
        }

//        // rule 05
//        ruleNumber++;
//	matcher = Pattern.compile("(r)"+SP+"_("+VOWELS+"h)[a-z]{0,}").matcher(phonemes+"_"+nextWord);
//        while ( matcher.find() )
//            phonemes = BaseMethods.replaceFound(ruleNumber, phonemes, matcher.start(), matcher.end(), "(r)"+SP+"_("+VOWELS+"h)[a-z]{0,}", " r ");

//        // rule 06
//        ruleNumber++;
//	matcher = Pattern.compile("(r)"+SP+"_("+UNVOICED_CONSONANTS+")[a-z]{0,}").matcher(phonemes+"_"+nextWord);
//        while ( matcher.find() )
//            phonemes = BaseMethods.replaceFound(ruleNumber, phonemes, matcher.start(), matcher.end(), "(r)"+SP+"_("+UNVOICED_CONSONANTS+")[a-z]{0,}", " X ");
//
//        // rule 07
//        ruleNumber++;
//	matcher = Pattern.compile("(r)"+SP+"_("+VOICED_CONSONANTS+")[a-z]{0,}").matcher(phonemes+"_"+nextWord);
//        while ( matcher.find() )
//            phonemes = BaseMethods.replaceFound(ruleNumber, phonemes, matcher.start(), matcher.end(), "(r)"+SP+"_("+VOICED_CONSONANTS+")[a-z]{0,}", " R ");

//        // rule 08
//        ruleNumber++;
//	matcher = Pattern.compile("(r)"+SP+"("+UNVOICED_CONSONANTS+")").matcher(phonemes);
//        while ( matcher.find() )
//            phonemes = BaseMethods.replaceFound(ruleNumber, phonemes, matcher.start(), matcher.start()+1, "(r)", " X ");
//
//        // rule 09
//        ruleNumber++;
//	matcher = Pattern.compile("(r)"+SP+"("+VOICED_CONSONANTS+")").matcher(phonemes);
//        while ( matcher.find() )
//            phonemes = BaseMethods.replaceFound(ruleNumber, phonemes, matcher.start(), matcher.start()+1, "(r)", " R ");

        return phonemes;

    }

    private static String applyLetterRules_S(String phonetics) {

        String phonemes = phonetics;

        /*
         * rule 01 ex: trânsito
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(t)" + SP + "(r)" + SP_SF + "(a|â|a~)" + SP + "(n)?" + SP + "(s)" + SP + "(" + VOWELS + ")").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(s)", " z ");
        }

        /*
         * rule 02 ex: subsídio
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(b)" + SP + "(s)" + SP + SF).matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(s)", " z ");
        }

//        /* rule 04
//         * ex: rãs
//         */
//        ruleNumber++;
//	matcher = Pattern.compile("(ã|a~)"+SP+"(s)"+SP+"$").matcher(phonemes);
//        while ( matcher.find() )
//            phonemes = BaseMethods.replaceFound(ruleNumber, phonemes, matcher.start(), matcher.end(), "(s)", " j~ s ");


        /*
         * rule 03 ex: gás
         */
        ruleNumber++;
        matcher1 = Pattern.compile(SF + SP + "(" + VOWELS.less(VOWELS_I) + ")" + SP + "(s)" + SP + "$").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(s)", " j s ");
        }

        /*
         * rule 04 ex: show
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(s)" + SP + "(h)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(s)" + SP + "(h)", " S ");
        }

        /*
         * rule 05 ex: acintosamente
         */
        ruleNumber++;
        matcher1 = Pattern.compile("^" + SP + "(" + VOWELS.less(MARKED_VOWELS) + ")" + SP + "(s)" + SP_SF + "(i" + SP + "(m|n)|e" + SP + "(m|n)|i~|e~)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(s)", " s ");
        }

        /*
         * rule 05 ex: acintosamente
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(" + VOWELS.less(MARKED_VOWELS) + ")" + SP + "(s)" + SP_SF + "(" + MARKED_VOWELS + ")").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(s)", " s ");
        }

        /*
         * rule 05 ex: acintosamente
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(" + UNMARKED_VOWELS + ")" + SP + "(s)" + SP_SF + "((" + UNMARKED_VOWELS.less(VOWELS_I) + ")" + SP + "(" + USAGE_CONSONANTS.less("j") + "){2}|(" + MARKED_VOWELS.less(VOWELS_I) + ")" + SP + "(" + USAGE_CONSONANTS.less("j") + "))").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(s)", " z ");
        }


        /*
         * rule 05 ex: casa, caso
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(" + VOWELS.less(MARKED_VOWELS) + ")" + SP + "(s)" + SP_SF + "(" + VOWELS + ")").matcher(phonemes);
        while (matcher1.find()) {
            int start = matcher1.start();
            int end = matcher1.end();
            ruleNumber++;
            matcher2 = Pattern.compile("(" + VOWELS.less(VOWELS_I) + ")" + SP + "(s)" + SP_SF + "(ã|õ|i~|a~|o~)").matcher(phonemes);
            if (matcher2.find()) {
                phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, start, end, "(s)", " s ");
            } else {
                phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, start, end, "(s)", " z ");
            }
        }

        /*
         * rule 06 ex: crescer
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(s)" + SP + "(c)" + SP_SF + "(e|i)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(s)" + SP + "(c)", " s ");
        }

        /*
         * rule 03 ex: escola
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(s)" + SP + "(" + USAGE_CONSONANTS.less("h", "ç", "s") + ")").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(s)", " s ");
        }

//        // rule 12
//        ruleNumber++;
//	matcher = Pattern.compile("(s)"+SP+"_(j).*").matcher(phonemes+"_"+nextWord);
//        while ( matcher.find() )
//            phonemes = BaseMethods.replaceFound(ruleNumber, phonemes, matcher.start(), matcher.end(), "(s)"+SP+"_(j).*", " Z ");

        // rule 13
        ruleNumber++;
        matcher1 = Pattern.compile("(s)" + SP + "_(" + VOWELS + "|" + USAGE_CONSONANTS + ").*").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(s)" + SP + "_(" + VOWELS + "|" + USAGE_CONSONANTS + ").*", " z ");
        }

        /*
         * rule 08 ex: sapo
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(s)" + SP + "(s|ç)?").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(s)" + SP + "(ç|s)?", " s ");
        }

        return phonemes;
    }

    private static String applyLetterRules_T(String phonetics) {

        String phonemes = phonetics;

        /*
         * rule 06 ex: thiago
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(t)" + SP + "(h)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(t)" + SP + "(h)", " t ");
        }

        /*
         * rule 07 ex: teste
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(t)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.start() + 1, "(t)", " t ");
        }

        return phonemes;
    }

    private static String applyLetterRules_U(String phonetics) {

        String phonemes = phonetics;

        /*
         * rule 01 ex: quando, aula
         */
        ruleNumber++;
        matcher1 = Pattern.compile("((g|q)" + SP + "(ü|u)" + SP_SF + "(a)|(" + VOWELS.less(VOWELS_U) + ")" + SP + "(u))").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(u)", " w ");
        }

//        /* rule 02
//         * ex: quem
//         */
//        ruleNumber++;
//	  matcher1 = Pattern.compile("(g|q)"+SP+"(ü|u)"+SP_SF+"(e|i|o)").matcher(phonemes);
//        while ( matcher1.find() )
//            phonemes = BaseMethods.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(u)", "");

        /*
         * rule 03 ex: unha
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(u)" + SP + "(n)" + SP + "(h)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(u)", " u~ ");
        }

        /*
         * rule 04 ex: chumbo
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(u)" + SP + "(m|n|j~)" + SP + "(" + USAGE_CONSONANTS.less("h") + ")").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(u)" + SP + "(m|n|j~)", " u~ ");
        }

        /*
         * rule 05 ex: um
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(u)" + SP + "(m|n)" + SP + "$").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(u)" + SP + "(m|n)", " u~ ");
        }

        /*
         * rule 06 ex: espuma
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(u)" + SP + "(m|n|j~)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(u)", " u~ ");
        }

        /*
         * rule 06 ex: cinqüenta
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(ü)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(ü)", " w ");
        }

        /*
         * rule 07 ex: urubu
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(u|ú)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(u|ú)", " u ");
        }

        return UtteranceUtil.fixTildeSpace(phonemes);

    }

    private static String applyLetterRules_V(String phonetics) {

        String phonemes = phonetics;

        /*
         * rule 01 ex: vitória
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(v)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(v)", " v ");
        }

        return phonemes;

    }

    private static String applyLetterRules_X(String word, String phonetics) {

        String phonemes = phonetics;

        /*
         * rule 12 ex: maximo
         */
        ruleNumber++;
        matcher1 = Pattern.compile("^(" + AX_EXCEPTIONS + ")$").matcher(word);
        while (matcher1.find()) {
            ruleNumber++;
            matcher1 = Pattern.compile("(x)").matcher(phonemes);
            if (matcher1.find()) {
                phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(x)", " s ");
            }
        }

        /*
         * rule 01 ex: exceto
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(x)" + SP + "(c)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(x)" + SP + "(c)", " s ");
        }

        /*
         * rule 02 ex: exito
         */
        ruleNumber++;
        matcher1 = Pattern.compile("^" + SP_SF + "(e)" + SP + "(x)" + SP_SF + "(i|u|o|a|e)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(x)", " z ");
        }

        /*
         * rule 03 ex: oxigênio
         */
        ruleNumber++;
        matcher1 = Pattern.compile("^" + SP_SF + "(o|ó|a|á|i|í)" + SP + "(x)" + SP_SF + "(o|ó|a|á|i|í)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(x)", " k s ");
        }

        /*
         * rule 04 ex: táxi
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(a|á)" + SP + "(x)" + SP + "(e|i)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(x)", " k s ");
        }

        /*
         * rule 05 ex: complexo
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(e|E|o|ó|O)" + SP + "(x)" + SP_SF + "(a|o|ó|O|u)" + SP + "(s)?$").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(x)", " k s ");
        }

        /*
         * rule 06 ex: ixofagia
         */
        ruleNumber++;
        matcher1 = Pattern.compile("^" + SP_SF + "(i)" + SP + "(x)" + SP_SF + "(o|ó)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(x)", " k z ");
        }

        /*
         * rule 07 ex: aproximar
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(p)" + SP + "(r)" + SP_SF + "(o|O|ó)" + SP + "(x)" + SP_SF + "(i|í)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(x)", " s ");
        }

        /*
         * rule 08 ex: tóxico
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(o|ó)" + SP + "(x)" + SP_SF + "(i|í)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(x)", " S ");
        }

        /*
         * rule 10 ex: durex
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(x)" + SP + "$").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(x)", " k s ");
        }

        /*
         * rule 11 ex: xerox
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(x)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(x)", " S ");
        }

//        ruleNumber++;
//        matcher = Pattern.compile("(x|S|k"+SP+"(s|z))").matcher(phonemes);
//        while ( matcher.find() )

        return phonemes;

    }

    private static String applyLetterRules_W(String phonetics) {

        String phonemes = phonetics;

        /* rule 01
         * ex: show
         */
//        ruleNumber++;
//	matcher = Pattern.compile("(w)").matcher(phonemes);
//        while ( matcher.find() )
//            phonemes = BaseMethods.replaceFound(ruleNumber, phonemes, matcher.start(), matcher.end(), "(w)", " w ");
//
        return phonemes;

    }
    
    private static String applyLetterRules_Y(String phonetics) {

        String phonemes = phonetics;

        /* rule 01
         * ex: yanomami
         */
//        ruleNumber++;
//	matcher = Pattern.compile("(y)"+SP+"("+VOWELS+")").matcher(phonemes);
//        while ( matcher.find() )
//            phonemes = BaseMethods.replaceFound(ruleNumber, phonemes, matcher.start(), matcher.end(), "(y)", " j ");
//
//        /* rule 02
//         * ex: spray
//         */
//        ruleNumber++;
//	matcher = Pattern.compile("("+VOWELS+")"+SP+"(y)").matcher(phonemes);
//        while ( matcher.find() )
//            phonemes = BaseMethods.replaceFound(ruleNumber, phonemes, matcher.start(), matcher.end(), "(y)", " j ");
//
//        /* rule 03
//         * ex: tayssa
//         */
//        ruleNumber++;
//	matcher = Pattern.compile("(y)").matcher(phonemes);
//        while ( matcher.find() )
//            phonemes = BaseMethods.replaceFound(ruleNumber, phonemes, matcher.start(), matcher.end(), "(y)", " i ");

        return phonemes;

    }

    private static String applyLetterRules_Z(String phonetics) {

        String phonemes = phonetics;

        /*
         * rule 01 ex:
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(z)" + SP + "_(" + USAGE_CONSONANTS + ").*").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "z" + SP + "_(" + USAGE_CONSONANTS + ").*", " S ");
        }

        /*
         * rule 02 ex:
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(z)" + SP + "_(" + VOWELS.plus("h") + ").*").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "z" + SP + "_(" + VOWELS.plus("h") + ").*", " z ");
        }

        /*
         * rule 03 ex: faz
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(" + VOWELS.less(VOWELS_I) + ")" + SP + "(z)" + SP + "$").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(z)", " j S ");
        }

        /*
         * rule 04 ex: giz
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(z)" + SP + "$").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(z)", " S ");
        }

        /*
         * rule 05 ex: zeca
         */
        ruleNumber++;
        matcher1 = Pattern.compile("(z)").matcher(phonemes);
        while (matcher1.find()) {
            phonemes = UtteranceUtil.replaceFound(ruleNumber, phonemes, matcher1.start(), matcher1.end(), "(z)", " z ");
        }

        return phonemes;
    }

    public static void main(String[] args) {
        try {
            for (String word : JOptionPane.showInputDialog("Frase?").split(" ")) {
                System.out.println(toPhonemes(word));
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }   

    public static void saveNotFoundedWord(String word, String xmlLocation) {

        String filePath = xmlLocation + File.separator + "notFounded.dic";
        FileWriter projectXmlWriter = null;
        try {
            File projectXmlFile = new File(filePath);
            System.out.println("Aqui: " + projectXmlFile.getAbsolutePath());
            if (projectXmlFile.exists()) {
                projectXmlFile.delete();
            }

            projectXmlFile.createNewFile();

            org.jdom.Element rssElement = new org.jdom.Element("Words");
            org.jdom.Element projectElement = new org.jdom.Element("word");
            projectElement.addContent(new org.jdom.Element("description").addContent(word));

            projectElement.addContent(new Element("representation").addContent(""));

            rssElement.addContent(projectElement);
            org.jdom.Document projectDocument = new org.jdom.Document(rssElement);
            XMLOutputter projetctOutputter = new XMLOutputter(Format.getPrettyFormat());
            projectXmlWriter = new FileWriter(projectXmlFile);
            projetctOutputter.output(projectDocument, projectXmlWriter);
            projectXmlWriter.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                projectXmlWriter.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public static void createNotFoundedWords(String wordNotFounded, String xmlLocation) {
        String filePath = xmlLocation + File.separator + "notFounded.dic";

        try {

            SAXBuilder builder = new SAXBuilder();
            File xmlFile = new File(filePath);

            Document doc = (Document) builder.build(xmlFile);
            Element rootNode = doc.getRootElement();

            Element word = new Element("word").addContent(new Element("description").addContent(wordNotFounded));
            word.addContent(new Element("representation").addContent(""));

            rootNode.addContent(word);

            XMLOutputter xmlOutput = new XMLOutputter();

            // display nice nice
            xmlOutput.setFormat(Format.getPrettyFormat());
            xmlOutput.output(doc, new FileWriter(filePath));

            // xmlOutput.output(doc, System.out);

        } catch (JDOMException ex) {
            System.out.println("Erro: " + ex.getMessage() + "\n " + ex.getStackTrace().toString());
        } catch (IOException io) {
            System.out.println("Erro: " + io.getMessage() + "\n " + io.getStackTrace().toString());
        }
    }
}
