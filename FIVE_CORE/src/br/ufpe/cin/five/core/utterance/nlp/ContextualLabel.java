package br.ufpe.cin.five.core.utterance.nlp;

import br.ufpe.cin.five.core.utterance.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContextualLabel {

    private Utterance utterance;
    private String filename;
    private BufferedWriter contextualLabelFile;

    private enum ContextualLabelItem {

        m1, m2, m3, m4, m5, m6, m7,
        s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11, s12, s13, s14, s15,
        w1, w2, w3, w4, w5, w6, w7, w8, w9, w10, w11, w12, w13,
        p1, p2, p3, p4, p5, p6, p7, p8,
        u1, u2, u3;
    }
    private EnumMap<ContextualLabelItem, String> phoneContextualLabel = new EnumMap<ContextualLabelItem, String>(ContextualLabelItem.class);
    private EnumMap<ContextualLabelItem, String> pauseContextualLabel = new EnumMap<ContextualLabelItem, String>(ContextualLabelItem.class);
    
    public static String SIL = "sil";
    public static String PAU = "pau";

    public ContextualLabel(Utterance utterance, String filename) {
        this.utterance = utterance;
        this.filename = filename;        
    }
    
    public void process() throws UtteranceException {

        try {
            contextualLabelFile = new BufferedWriter(new FileWriter(filename));
            List<Phrase> phrases = utterance.getPhrases();

            // artifice used to easy get previous and next
            int indexTotalWordsInPhrase;
            int indexTotalSyllablesInPhrase;
            int indexTotalPhrases = 0;
            int indexTotalWordsInUtterance = 0;
            int indexTotalSyllablesInUtterance = 0;
            int indexTotalPhonesInUtterance = 1;
            ArrayList<Word> totalWordsInPhrase;
            ArrayList<Syllable> totalSyllablesInPhrase;
            ArrayList<Phrase> totalPhrases = new ArrayList<Phrase>();
            ArrayList<Word> totalWordsInUtterance = new ArrayList<Word>();
            ArrayList<Syllable> totalSyllablesInUtterance = new ArrayList<Syllable>();
            ArrayList<String> totalPhonesInUtterance = new ArrayList<String>();
            totalPhonesInUtterance.add(SIL);
            for (int indexPhrase = 0; indexPhrase < phrases.size(); indexPhrase++) {
                Phrase phrase = phrases.get(indexPhrase);
                List<Word> words = phrase.getWords();
                totalPhrases.add(phrase);
                for (int indexWord = 0; indexWord < words.size(); indexWord++) {
                    Word word = words.get(indexWord);
                    List<Syllable> syllables = word.getSyllables();
                    totalWordsInUtterance.add(word);
                    for (int indexSyllable = 0; indexSyllable < syllables.size(); indexSyllable++) {
                        Syllable syllable = syllables.get(indexSyllable);
                        List<String> phones = syllable.getPhones();
                        totalSyllablesInUtterance.add(syllable);
                        for (int indexPhone = 0; indexPhone < phones.size(); indexPhone++) {
                            String phone = phones.get(indexPhone);
                            totalPhonesInUtterance.add(phone);
                        }
                    }
                }
                if (indexPhrase != phrases.size() - 1) {
                    totalPhonesInUtterance.add(PAU);
                }
            }
            totalPhonesInUtterance.add(SIL);

            /*
             * UTTERANCE PART - BEGIN
             * calculated once and used in all code
             */
            // number of syllables in utterance
            phoneContextualLabel.put(ContextualLabelItem.u1,
                    String.valueOf(totalSyllablesInUtterance.size()));
            pauseContextualLabel.put(ContextualLabelItem.u1,
                    String.valueOf(totalSyllablesInUtterance.size()));


            // number of words in utterance
            phoneContextualLabel.put(ContextualLabelItem.u2,
                    String.valueOf(totalWordsInUtterance.size()));
            pauseContextualLabel.put(ContextualLabelItem.u2,
                    String.valueOf(totalWordsInUtterance.size()));

            // number of phrases in utterance
            phoneContextualLabel.put(ContextualLabelItem.u3,
                    String.valueOf(totalPhrases.size()));
            pauseContextualLabel.put(ContextualLabelItem.u3,
                    String.valueOf(totalPhrases.size()));
            /*
             * UTTERANCE PART - END
             */

            indexTotalPhrases = -1;
            indexTotalWordsInPhrase = -1;
            indexTotalWordsInUtterance = -1;
            indexTotalSyllablesInPhrase = -1;
            indexTotalSyllablesInUtterance = -1;
            indexTotalPhonesInUtterance = -1;

            for (int indexPhrase = 0; indexPhrase < phrases.size(); indexPhrase++) {

                indexTotalPhrases++;
                indexTotalSyllablesInPhrase = -1;
                indexTotalWordsInPhrase = -1;

                List<Word> words = phrases.get(indexPhrase).getWords();
                totalWordsInPhrase = new ArrayList<Word>();
                totalSyllablesInPhrase = new ArrayList<Syllable>();
                for (int a = 0; a < words.size(); a++) {
                    Word word = words.get(a);
                    totalWordsInPhrase.add(word);
                    List<Syllable> syllables = word.getSyllables();
                    for (int b = 0; b < syllables.size(); b++) {
                        totalSyllablesInPhrase.add(syllables.get(b));
                    }
                }
 
                for (int indexWord = 0; indexWord < words.size(); indexWord++) {

                    indexTotalWordsInPhrase++;
                    indexTotalWordsInUtterance++;

                    List<Syllable> syllables = words.get(indexWord).getSyllables();

                    for (int indexSyllable = 0; indexSyllable < syllables.size(); indexSyllable++) {

                        indexTotalSyllablesInPhrase++;
                        indexTotalSyllablesInUtterance++;

                        List<String> phones = syllables.get(indexSyllable).getPhones();

                        for (int indexPhone = 0; indexPhone < phones.size(); indexPhone++) {

                            indexTotalPhonesInUtterance++;

                            /*
                             * PAUSE AND SILENCE - BEGIN
                             *
                             */
                            if (totalPhonesInUtterance.get(indexTotalPhonesInUtterance).equals(SIL)
                                    || totalPhonesInUtterance.get(indexTotalPhonesInUtterance).equals(PAU)
                                    || (indexPhrase == phrases.size() - 1 && indexWord == words.size() - 1
                                    && indexSyllable == syllables.size() - 1 && indexPhone == phones.size() - 1)) {

                                if (indexPhrase == phrases.size() - 1 && indexWord == words.size() - 1
                                        && indexSyllable == syllables.size() - 1 && indexPhone == phones.size() - 1) {
                                    indexTotalPhonesInUtterance++;
                                }

                                /*
                                 * PHONE PART - BEGIN
                                 */
                                // the phoneme identity before the previous phoneme
                                pauseContextualLabel.put(ContextualLabelItem.m1,
                                        indexTotalPhonesInUtterance > 1 ? totalPhonesInUtterance.get(indexTotalPhonesInUtterance - 2) : "y");

                                // the previous phoneme identity
                                pauseContextualLabel.put(ContextualLabelItem.m2,
                                        indexTotalPhonesInUtterance > 0 ? totalPhonesInUtterance.get(indexTotalPhonesInUtterance - 1) : "y");

                                // the current phoneme identity
                                pauseContextualLabel.put(ContextualLabelItem.m3,
                                        totalPhonesInUtterance.get(indexTotalPhonesInUtterance));

                                // the next phoneme identity
                                pauseContextualLabel.put(ContextualLabelItem.m4,
                                        (indexTotalPhonesInUtterance + 1) < totalPhonesInUtterance.size() ? totalPhonesInUtterance.get(indexTotalPhonesInUtterance + 1).toString() : "y");

                                // the phoneme after the next phoneme identity
                                pauseContextualLabel.put(ContextualLabelItem.m5,
                                        (indexTotalPhonesInUtterance + 2) < totalPhonesInUtterance.size() ? totalPhonesInUtterance.get(indexTotalPhonesInUtterance + 2).toString() : "y");

                                // position of the current phoneme identity in the current syllable (forward)
                                pauseContextualLabel.put(ContextualLabelItem.m6,
                                        String.valueOf(indexPhone + 1));

                                // position of the current phoneme identity in the current syllable (backward)
                                pauseContextualLabel.put(ContextualLabelItem.m7,
                                        String.valueOf(syllables.get(indexSyllable).getPhones().size() - indexPhone));

                                /*
                                 * PHONE PART - END
                                 */

                                /*
                                 * SYLLABLE PART - BEGIN
                                 */
                                // whether the previous syllable stressed or not (0: not stressed, 1: stressed)
                                pauseContextualLabel.put(ContextualLabelItem.s1,
                                        indexTotalSyllablesInUtterance > 0 ? (totalSyllablesInUtterance.get(indexTotalSyllablesInUtterance - 1).isStressed() ? "1" : "0") : "y");

                                // the number of phonemes in the previous syllable
                                pauseContextualLabel.put(ContextualLabelItem.s2,
                                        indexTotalSyllablesInUtterance > 0 ? String.valueOf(totalSyllablesInUtterance.get(indexTotalSyllablesInUtterance - 1).getPhones().size()) : "y");

                                // whether the current syllable stressed or not (0: not stressed, 1: stressed)
                                pauseContextualLabel.put(ContextualLabelItem.s3,
                                        totalSyllablesInUtterance.get(indexTotalSyllablesInUtterance).isStressed() ? "1" : "0");

                                // the number of phonemes in the current syllable
                                pauseContextualLabel.put(ContextualLabelItem.s4,
                                        String.valueOf(totalSyllablesInUtterance.get(indexTotalSyllablesInUtterance).getPhones().size()));

                                // whether the next syllable stressed or not (0: not stressed, 1: stressed)
                                pauseContextualLabel.put(ContextualLabelItem.s5,
                                        (indexTotalSyllablesInUtterance + 1) < totalSyllablesInUtterance.size() ? (totalSyllablesInUtterance.get(indexTotalSyllablesInUtterance + 1).isStressed() ? "1" : "0") : "y");

                                // the number of phonemes in the next syllable
                                pauseContextualLabel.put(ContextualLabelItem.s6,
                                        (indexTotalSyllablesInUtterance + 1) < totalSyllablesInUtterance.size() ? (String.valueOf(totalSyllablesInUtterance.get(indexTotalSyllablesInUtterance + 1).getPhones().size())) : "y");

                                // position of the current syllable in the current word (forward)
                                pauseContextualLabel.put(ContextualLabelItem.s7, "y");

                                // position of the current syllable in the current word (backward)
                                pauseContextualLabel.put(ContextualLabelItem.s8, "y");

                                // position of the current syllable in the current phrase (forward)
                                pauseContextualLabel.put(ContextualLabelItem.s9, "y");

                                // position of the current syllable in the current phrase (backward)
                                pauseContextualLabel.put(ContextualLabelItem.s10, "y");

                                // the number of stressed syllables before the current syllable in the current phrase
                                pauseContextualLabel.put(ContextualLabelItem.s11, "y");

                                // the number of stressed syllables after the current syllable in the current phrase
                                pauseContextualLabel.put(ContextualLabelItem.s12, "y");

                                // the number of syllables, counting from the previous stressed syllable to the current syllable in this utterance
                                pauseContextualLabel.put(ContextualLabelItem.s13, "y");

                                // the number of syllables, counting from the current syllable to the next stressed syllable in this utterance
                                pauseContextualLabel.put(ContextualLabelItem.s14, "y");

                                // name of the vowel of the current syllable
                                pauseContextualLabel.put(ContextualLabelItem.s15, "y");

                                /*
                                 * SYLLABLE PART - END
                                 */

                                /*
                                 * WORD PART - BEGIN
                                 */
                                // part-of-speech classification of the previous word
                                pauseContextualLabel.put(ContextualLabelItem.w1,
                                        indexTotalWordsInUtterance > 0 ? totalWordsInUtterance.get(indexTotalWordsInUtterance - 1).getGrammaticalClassification() : "y");

                                // the number of syllables in the previous word
                                pauseContextualLabel.put(ContextualLabelItem.w2,
                                        indexTotalWordsInUtterance > 0 ? String.valueOf(totalWordsInUtterance.get(indexTotalWordsInUtterance - 1).getSyllables().size()) : "y");

                                // part-of-speech of classification of the current word
                                pauseContextualLabel.put(ContextualLabelItem.w3, "y");

                                // the number of syllables in the current word
                                pauseContextualLabel.put(ContextualLabelItem.w4, "y");

                                // part-of-speech classification of the next word
                                pauseContextualLabel.put(ContextualLabelItem.w5,
                                        (indexTotalWordsInUtterance + 1) < totalWordsInUtterance.size() ? totalWordsInUtterance.get(indexTotalWordsInUtterance + 1).getGrammaticalClassification() : "y");

                                // the number of syllables in the next word
                                pauseContextualLabel.put(ContextualLabelItem.w6,
                                        (indexTotalWordsInUtterance + 1) < totalWordsInUtterance.size() ? String.valueOf(totalWordsInUtterance.get(indexTotalWordsInUtterance + 1).getSyllables().size()) : "y");

                                // position of the current word in the current phrase (forward)
                                pauseContextualLabel.put(ContextualLabelItem.w7, "y");

                                // position of the current word in the current phrase (backward)
                                pauseContextualLabel.put(ContextualLabelItem.w8, "y");

                                // the number of content words before the current word in the current phrase
                                pauseContextualLabel.put(ContextualLabelItem.w9, "y");

                                // the number of content words after the current word in the current phrase
                                pauseContextualLabel.put(ContextualLabelItem.w10, "y");

                                // the number of words counting from the previous content word to the current word in this utterance
                                pauseContextualLabel.put(ContextualLabelItem.w11, "y");

                                // the number of words counting from the current word to the next content word in this utterance
                                pauseContextualLabel.put(ContextualLabelItem.w12, "y");

                                // interrogation flag of the current word
                                pauseContextualLabel.put(ContextualLabelItem.w13, "y");
                                /*
                                 * WORD PART - END
                                 */


                                /*
                                 * PHRASE PART - BEGIN
                                 */
                                // number of syllables in previous phrase
                                pauseContextualLabel.put(ContextualLabelItem.p1,
                                        indexPhrase > 0 ? String.valueOf(getNumberOfSyllablesOnPhrase(phrases.get(indexPhrase - 1))) : "y");

                                // number of words in previous phrase
                                pauseContextualLabel.put(ContextualLabelItem.p2,
                                        indexPhrase > 0 ? String.valueOf(phrases.get(indexPhrase - 1).getWords().size()) : "y");

                                // number of syllables in current phrase
                                pauseContextualLabel.put(ContextualLabelItem.p3, "y");

                                // number of words in current phrase
                                pauseContextualLabel.put(ContextualLabelItem.p4, "y");

                                // number of syllables in next phrase
                                pauseContextualLabel.put(ContextualLabelItem.p5,
                                        indexPhrase < (phrases.size() - 1) ? String.valueOf(getNumberOfSyllablesOnPhrase(phrases.get(indexPhrase + 1))) : "y");

                                // number of words in next phrase
                                pauseContextualLabel.put(ContextualLabelItem.p6,
                                        indexPhrase < (phrases.size() - 1) ? String.valueOf(phrases.get(indexPhrase + 1).getWords().size()) : "y");

                                // position of current phrase in the utterance (forward)
                                pauseContextualLabel.put(ContextualLabelItem.p7,
                                        String.valueOf(indexPhrase + 1));

                                // position of current phrase in the utterance (backward)
                                pauseContextualLabel.put(ContextualLabelItem.p8,
                                        String.valueOf(phrases.size() - indexPhrase));
                                /*
                                 * PHRASE PART - END
                                 */
                                if (indexPhrase == phrases.size() - 1 && indexWord == words.size() - 1
                                        && indexSyllable == syllables.size() - 1 && indexPhone == phones.size() - 1) {
                                    indexTotalPhonesInUtterance--;
                                } else {
                                    indexTotalPhonesInUtterance++;
                                    writeLine(pauseContextualLabel);
                                }

                            }
                            /*
                             * PAUSE AND SILENCE - END
                             *
                             */

                            /*
                             * PHONE PART - BEGIN
                             */
                            // the phoneme identity before the previous phoneme
                            phoneContextualLabel.put(ContextualLabelItem.m1,
                                    indexTotalPhonesInUtterance > 1 ? totalPhonesInUtterance.get(indexTotalPhonesInUtterance - 2) : "y");

                            // the previous phoneme identity
                            phoneContextualLabel.put(ContextualLabelItem.m2,
                                    indexTotalPhonesInUtterance > 0 ? totalPhonesInUtterance.get(indexTotalPhonesInUtterance - 1) : "y");


                            // the current phoneme identity
                            phoneContextualLabel.put(ContextualLabelItem.m3,
                                    totalPhonesInUtterance.get(indexTotalPhonesInUtterance));

                            // the next phoneme identity
                            phoneContextualLabel.put(ContextualLabelItem.m4,
                                    (indexTotalPhonesInUtterance + 1) < totalPhonesInUtterance.size() ? totalPhonesInUtterance.get(indexTotalPhonesInUtterance + 1).toString() : "y");

                            // the phoneme after the next phoneme identity
                            phoneContextualLabel.put(ContextualLabelItem.m5,
                                    (indexTotalPhonesInUtterance + 2) < totalPhonesInUtterance.size() ? totalPhonesInUtterance.get(indexTotalPhonesInUtterance + 2).toString() : "y");

                            // position of the current phoneme identity in the current syllable (forward)
                            phoneContextualLabel.put(ContextualLabelItem.m6,
                                    String.valueOf(indexPhone + 1));

                            // position of the current phoneme identity in the current syllable (backward)
                            phoneContextualLabel.put(ContextualLabelItem.m7,
                                    String.valueOf(syllables.get(indexSyllable).getPhones().size() - indexPhone));
                            /*
                             * PHONE PART - END
                             */

                            /*
                             * SYLLABLE PART - BEGIN
                             */
                            // whether the previous syllable stressed or not (0: not stressed, 1: stressed)
                            phoneContextualLabel.put(ContextualLabelItem.s1,
                                    indexTotalSyllablesInUtterance > 0 ? (totalSyllablesInUtterance.get(indexTotalSyllablesInUtterance - 1).isStressed() ? "1" : "0") : "y");

                            // the number of phonemes in the previous syllable
                            phoneContextualLabel.put(ContextualLabelItem.s2,
                                    indexTotalSyllablesInUtterance > 0 ? String.valueOf(totalSyllablesInUtterance.get(indexTotalSyllablesInUtterance - 1).getPhones().size()) : "y");

                            // whether the current syllable stressed or not (0: not stressed, 1: stressed)
                            phoneContextualLabel.put(ContextualLabelItem.s3,
                                    totalSyllablesInUtterance.get(indexTotalSyllablesInUtterance).isStressed() ? "1" : "0");

                            // the number of phonemes in the current syllable
                            phoneContextualLabel.put(ContextualLabelItem.s4,
                                    String.valueOf(totalSyllablesInUtterance.get(indexTotalSyllablesInUtterance).getPhones().size()));

                            // whether the next syllable stressed or not (0: not stressed, 1: stressed)
                            phoneContextualLabel.put(ContextualLabelItem.s5,
                                    (indexTotalSyllablesInUtterance + 1) < totalSyllablesInUtterance.size() ? (totalSyllablesInUtterance.get(indexTotalSyllablesInUtterance + 1).isStressed() ? "1" : "0") : "y");

                            // the number of phonemes in the next syllable
                            phoneContextualLabel.put(ContextualLabelItem.s6,
                                    (indexTotalSyllablesInUtterance + 1) < totalSyllablesInUtterance.size() ? (String.valueOf(totalSyllablesInUtterance.get(indexTotalSyllablesInUtterance + 1).getPhones().size())) : "y");

                            // position of the current syllable in the current word (forward)
                            phoneContextualLabel.put(ContextualLabelItem.s7,
                                    String.valueOf(totalSyllablesInUtterance.get(indexTotalSyllablesInUtterance).getIndexInWord() + 1));

                            // position of the current syllable in the current word (backward)
                            phoneContextualLabel.put(ContextualLabelItem.s8,
                                    String.valueOf(totalWordsInUtterance.get(indexTotalWordsInUtterance).getSyllables().size() - totalSyllablesInUtterance.get(indexTotalSyllablesInUtterance).getIndexInWord()));

                            // position of the current syllable in the current phrase (forward)
                            phoneContextualLabel.put(ContextualLabelItem.s9,
                                    String.valueOf(indexTotalSyllablesInPhrase + 1));

                            // position of the current syllable in the current phrase (backward)
                            phoneContextualLabel.put(ContextualLabelItem.s10,
                                    String.valueOf(totalSyllablesInPhrase.size() - indexTotalSyllablesInPhrase));

                            // the number of stressed syllables before the current syllable in the current phrase
                            int numberOfStressedSyllables = 0;
                            for (int a = 0; a < indexTotalSyllablesInPhrase; a++) {
                                if (totalSyllablesInPhrase.get(a).isStressed()) {
                                    numberOfStressedSyllables++;
                                }
                            }
                            phoneContextualLabel.put(ContextualLabelItem.s11,
                                    String.valueOf(numberOfStressedSyllables));

                            // the number of stressed syllables after the current syllable in the current phrase
                            numberOfStressedSyllables = 0;
                            for (int a = indexTotalSyllablesInPhrase + 1; a < totalSyllablesInPhrase.size(); a++) {
                                if (totalSyllablesInPhrase.get(a).isStressed()) {
                                    numberOfStressedSyllables++;
                                }
                            }
                            phoneContextualLabel.put(ContextualLabelItem.s12,
                                    String.valueOf(numberOfStressedSyllables));

                            // the number of syllables, counting from the previous stressed syllable to the current syllable in this utterance
                            int indexPreviousStressedSyllable = -1;
                            for (int a = indexTotalSyllablesInUtterance - 1; a >= 0; a--) {
                                if (totalSyllablesInUtterance.get(a).isStressed()) {
                                    indexPreviousStressedSyllable = a;
                                }
                                if (indexPreviousStressedSyllable != -1) {
                                    break;
                                }
                            }
                            phoneContextualLabel.put(ContextualLabelItem.s13,
                                    indexPreviousStressedSyllable != -1 ? String.valueOf(indexTotalSyllablesInUtterance - indexPreviousStressedSyllable + 1) : "0");

                            // the number of syllables, counting from the current syllable to the next stressed syllable in this utterance
                            int indexNextStressedSyllable = -1;
                            for (int a = indexTotalSyllablesInUtterance + 1; a < totalSyllablesInUtterance.size(); a++) {
                                if (totalSyllablesInUtterance.get(a).isStressed()) {
                                    indexNextStressedSyllable = a;
                                }
                                if (indexNextStressedSyllable != -1) {
                                    break;
                                }

                            }
                            phoneContextualLabel.put(ContextualLabelItem.s14,
                                    indexNextStressedSyllable != -1 ? String.valueOf(indexNextStressedSyllable - indexTotalSyllablesInUtterance + 1) : "0");

                            // name of the vowel of the current syllable
                            Matcher matcher;
                            phoneContextualLabel.put(ContextualLabelItem.s15,
                                    (matcher = Pattern.compile(Syllabification.VOWELS).matcher(syllables.get(indexSyllable).getDescription())).find()
                                    ? syllables.get(indexSyllable).getDescription().substring(matcher.start(), matcher.end())
                                    : ((matcher = Pattern.compile(Syllabification.SEMIVOWELS).matcher(syllables.get(indexSyllable).getDescription())).find()
                                    ? syllables.get(indexSyllable).getDescription().substring(matcher.start(), matcher.end()) : "y"));
                            /*
                             * SYLLABLE PART - END
                             */

                            /*
                             * WORD PART - BEGIN
                             */
                            // part-of-speech classification of the previous word
                            phoneContextualLabel.put(ContextualLabelItem.w1,
                                    indexTotalWordsInUtterance > 0 ? totalWordsInUtterance.get(indexTotalWordsInUtterance - 1).getGrammaticalClassification() : "y");

                            // the number of syllables in the previous word
                            phoneContextualLabel.put(ContextualLabelItem.w2,
                                    indexTotalWordsInUtterance > 0 ? String.valueOf(totalWordsInUtterance.get(indexTotalWordsInUtterance - 1).getSyllables().size()) : "y");

                            // part-of-speech of classification of the current word
                            phoneContextualLabel.put(ContextualLabelItem.w3,
                                    totalWordsInUtterance.get(indexTotalWordsInUtterance).getGrammaticalClassification());

                            // the number of syllables in the current word
                            phoneContextualLabel.put(ContextualLabelItem.w4,
                                    String.valueOf(totalWordsInUtterance.get(indexTotalWordsInUtterance).getSyllables().size()));

                            // part-of-speech classification of the next word
                            phoneContextualLabel.put(ContextualLabelItem.w5,
                                    (indexTotalWordsInUtterance + 1) < totalWordsInUtterance.size() ? totalWordsInUtterance.get(indexTotalWordsInUtterance + 1).getGrammaticalClassification() : "y");

                            // the number of syllables in the next word
                            phoneContextualLabel.put(ContextualLabelItem.w6,
                                    (indexTotalWordsInUtterance + 1) < totalWordsInUtterance.size() ? String.valueOf(totalWordsInUtterance.get(indexTotalWordsInUtterance + 1).getSyllables().size()) : "y");

                            // position of the current word in the current phrase (forward)
                            phoneContextualLabel.put(ContextualLabelItem.w7,
                                    String.valueOf(indexTotalWordsInPhrase + 1));

                            // position of the current word in the current phrase (backward)
                            phoneContextualLabel.put(ContextualLabelItem.w8,
                                    String.valueOf(totalWordsInPhrase.size() - indexTotalWordsInPhrase));

                            // the number of content words before the current word in the current phrase
                            int numberOfContentWords = 0;
                            for (int a = 0; a < indexTotalWordsInPhrase - 1; a++) {
                                if (totalWordsInPhrase.get(a).getGrammaticalClassification().equals("content")) {
                                    numberOfContentWords++;
                                }
                            }
                            phoneContextualLabel.put(ContextualLabelItem.w9,
                                    String.valueOf(numberOfContentWords));

                            // the number of content words after the current word in the current phrase
                            numberOfContentWords = 0;
                            for (int a = indexTotalWordsInPhrase + 1; a < totalWordsInPhrase.size(); a++) {
                                if (totalWordsInPhrase.get(a).getGrammaticalClassification().equals("content")) {
                                    numberOfContentWords++;
                                }
                            }
                            phoneContextualLabel.put(ContextualLabelItem.w10,
                                    String.valueOf(numberOfContentWords));

                            // the number of words counting from the previous content word to the current word in this utterance
                            int indexPreviousContentWordInUtterance = -1;
                            for (int a = 0; a <= indexTotalWordsInUtterance; a++) {
                                if (totalWordsInUtterance.get(a).getGrammaticalClassification().equals("content")) {
                                    indexPreviousContentWordInUtterance = a;
                                }
                                if (indexPreviousContentWordInUtterance != -1) {
                                    break;
                                }
                            }
                            phoneContextualLabel.put(ContextualLabelItem.w11,
                                    indexPreviousContentWordInUtterance != -1 ? (indexTotalWordsInUtterance != indexPreviousContentWordInUtterance ? String.valueOf(indexTotalWordsInUtterance - indexPreviousContentWordInUtterance + 1) : "0") : "0");

                            // the number of words counting from the current word to the next content word in this utterance
                            int indexNextContentWordInUtterance = -1;
                            for (int a = indexTotalWordsInUtterance + 1; a < totalWordsInUtterance.size(); a++) {
                                if (totalWordsInUtterance.get(a).getGrammaticalClassification().equals("content")) {
                                    indexNextContentWordInUtterance = a;
                                }
                                if (indexNextContentWordInUtterance != -1) {
                                    break;
                                }
                            }
                            phoneContextualLabel.put(ContextualLabelItem.w12,
                                    indexNextContentWordInUtterance != -1 ? String.valueOf(indexNextContentWordInUtterance - indexTotalWordsInUtterance + 1) : "0");

                            // interrogation flag of the current word
                            phoneContextualLabel.put(ContextualLabelItem.w13,
                                    totalWordsInUtterance.get(indexTotalWordsInUtterance).getQuestionFlag() ? "1" : "0");
                            /*
                             * WORD PART - END
                             */

                            /*
                             * PHRASE PART - BEGIN
                             */
                            // number of syllables in previous phrase
                            phoneContextualLabel.put(ContextualLabelItem.p1,
                                    indexPhrase > 0 ? String.valueOf(getNumberOfSyllablesOnPhrase(phrases.get(indexPhrase - 1))) : "y");

                            // number of words in previous phrase
                            phoneContextualLabel.put(ContextualLabelItem.p2,
                                    indexPhrase > 0 ? String.valueOf(phrases.get(indexPhrase - 1).getWords().size()) : "y");

                            // number of syllables in current phrase
                            phoneContextualLabel.put(ContextualLabelItem.p3,
                                    String.valueOf(getNumberOfSyllablesOnPhrase(phrases.get(indexPhrase))));

                            // number of words in current phrase
                            phoneContextualLabel.put(ContextualLabelItem.p4,
                                    String.valueOf(totalWordsInPhrase.size()));

                            // number of syllables in next phrase
                            phoneContextualLabel.put(ContextualLabelItem.p5,
                                    indexPhrase < (phrases.size() - 1) ? String.valueOf(getNumberOfSyllablesOnPhrase(phrases.get(indexPhrase + 1))) : "y");

                            // number of words in next phrase
                            phoneContextualLabel.put(ContextualLabelItem.p6,
                                    indexPhrase < (phrases.size() - 1) ? String.valueOf(phrases.get(indexPhrase + 1).getWords().size()) : "y");

                            // position of current phrase in the utterance (forward)
                            phoneContextualLabel.put(ContextualLabelItem.p7,
                                    String.valueOf(indexPhrase + 1));

                            // position of current phrase in the utterance (backward)
                            phoneContextualLabel.put(ContextualLabelItem.p8,
                                    String.valueOf(phrases.size() - indexPhrase));
                            /*
                             * PHRASE PART - END
                             */

                            writeLine(phoneContextualLabel);
                            if (indexPhrase == phrases.size() - 1 && indexWord == words.size() - 1
                                    && indexSyllable == syllables.size() - 1 && indexPhone == phones.size() - 1) {
                                writeLine(pauseContextualLabel);
                            }


                        }
                    }
                }
            }
            contextualLabelFile.close();

        } catch (IOException ex) {
            throw new UtteranceException((ex.getMessage()));
        }
    }

    private static int getNumberOfSyllablesOnPhrase(Phrase phrase) {
        int numberOfSyllablesOnPhrase = 0;
        for (Word word : phrase.getWords()) {
            numberOfSyllablesOnPhrase += word.getSyllables().size();
        }
        return numberOfSyllablesOnPhrase;
    }

    private void writeLine(Map<ContextualLabelItem, String> contextualLabelMap) throws IOException {

        String labLine = contextualLabelMap.get(ContextualLabelItem.m1) + "^"
                + contextualLabelMap.get(ContextualLabelItem.m2) + "-"
                + contextualLabelMap.get(ContextualLabelItem.m3) + "+"
                + contextualLabelMap.get(ContextualLabelItem.m4) + "="
                + contextualLabelMap.get(ContextualLabelItem.m5)
                + "/M2:"
                + contextualLabelMap.get(ContextualLabelItem.m6) + "_"
                + contextualLabelMap.get(ContextualLabelItem.m7)
                + "/S1:"
                + contextualLabelMap.get(ContextualLabelItem.s1) + "_@"
                + contextualLabelMap.get(ContextualLabelItem.s2) + "-"
                + contextualLabelMap.get(ContextualLabelItem.s3) + "_@"
                + contextualLabelMap.get(ContextualLabelItem.s4) + "+"
                + contextualLabelMap.get(ContextualLabelItem.s5) + "_@"
                + contextualLabelMap.get(ContextualLabelItem.s6)
                + "/S2:"
                + contextualLabelMap.get(ContextualLabelItem.s7) + "_"
                + contextualLabelMap.get(ContextualLabelItem.s8)
                + "/S3:"
                + contextualLabelMap.get(ContextualLabelItem.s9) + "_"
                + contextualLabelMap.get(ContextualLabelItem.s10)
                + "/S4:"
                + contextualLabelMap.get(ContextualLabelItem.s11) + "_"
                + contextualLabelMap.get(ContextualLabelItem.s12)
                + "/S5:"
                + contextualLabelMap.get(ContextualLabelItem.s13) + "_"
                + contextualLabelMap.get(ContextualLabelItem.s14)
                + "/S6:"
                + contextualLabelMap.get(ContextualLabelItem.s15)
                + "/W1:"
                + contextualLabelMap.get(ContextualLabelItem.w1) + "_#"
                + contextualLabelMap.get(ContextualLabelItem.w2) + "-"
                + contextualLabelMap.get(ContextualLabelItem.w3) + "_#"
                + contextualLabelMap.get(ContextualLabelItem.w4) + "+"
                + contextualLabelMap.get(ContextualLabelItem.w5) + "_#"
                + contextualLabelMap.get(ContextualLabelItem.w6)
                + "/W2:"
                + contextualLabelMap.get(ContextualLabelItem.w7) + "_"
                + contextualLabelMap.get(ContextualLabelItem.w8)
                + "/W3:"
                + contextualLabelMap.get(ContextualLabelItem.w9) + "_"
                + contextualLabelMap.get(ContextualLabelItem.w10)
                + "/W4:"
                + contextualLabelMap.get(ContextualLabelItem.w11) + "_"
                + contextualLabelMap.get(ContextualLabelItem.w12)
                + "/W5:"
                + contextualLabelMap.get(ContextualLabelItem.w13)
                + "/P1:"
                + contextualLabelMap.get(ContextualLabelItem.p1) + "_!"
                + contextualLabelMap.get(ContextualLabelItem.p2) + "-"
                + contextualLabelMap.get(ContextualLabelItem.p3) + "_!"
                + contextualLabelMap.get(ContextualLabelItem.p4) + "+"
                + contextualLabelMap.get(ContextualLabelItem.p5) + "_!"
                + contextualLabelMap.get(ContextualLabelItem.p6)
                + "/P2:"
                + contextualLabelMap.get(ContextualLabelItem.p7) + "_"
                + contextualLabelMap.get(ContextualLabelItem.p8)
                + "/U:"
                + contextualLabelMap.get(ContextualLabelItem.u1) + "_$"
                + contextualLabelMap.get(ContextualLabelItem.u2) + "_&"
                + contextualLabelMap.get(ContextualLabelItem.u3);

//        contextualLabelFile.write((APPEND = APPEND == null ? "" : "\n") + labLine);
        contextualLabelFile.write(labLine + "\n");
    }
}
