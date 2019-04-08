/**
 * Copyright 2011 Federal University of Pernambuco. All Rights Reserved. Use is
 * subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.gui.engine;

import br.ufpe.cin.five.core.classification.ClassificationTechnique;
import br.ufpe.cin.five.core.classification.hmm.HmmParameters;
import br.ufpe.cin.five.core.classification.hmm.HmmUnitSize;
import br.ufpe.cin.five.core.utterance.Word;
import br.ufpe.cin.five.core.dictionary.Dictionary;
import br.ufpe.cin.five.core.engine.SpeechEngine;
import br.ufpe.cin.five.core.grammar.Grammar;
import br.ufpe.cin.five.core.grammar.RuleGrammar;
import br.ufpe.cin.five.core.utterance.Phrase;
import br.ufpe.cin.five.core.utterance.Utterance;
import java.util.ArrayList;


import java.util.List;

import org.apache.log4j.Logger;

/**
 *
 * @author Alexandre
 */
public class SpeechEnginePanel extends javax.swing.JPanel {

    private static final Logger logger = Logger.getLogger(SpeechEnginePanel.class);
    private SpeechEngine engine;

    public SpeechEnginePanel() {
        logger.info("Inicializando Speech Engine Panel");
        initComponents();
        this.engine = new SpeechEngine();
    }

    public void setEngine(SpeechEngine engine) {
        this.engine = engine;
    }

    public SpeechEngine getEngine() {
        return this.engine;
    }

    public void loadDictionary(List<Utterance> utterances) {

        Dictionary dictionary = new Dictionary();

        List<Word> words = new ArrayList<Word>();
        List<String> descriptions = new ArrayList<String>();
        int wordIndex = 1;
        for (Utterance utterance : utterances) {
            List<Phrase> phrases = utterance.getPhrases();
            for (Phrase phrase : phrases) {
                List<Word> wordsFound = phrase.getWords();
                for (Word word : wordsFound) {
                    if (!descriptions.contains(word.getDescription())) {
                        word.setId(wordIndex++);
                        words.add(word);

                    }
                    descriptions.add(word.getDescription());
                }
            }
        }
        dictionary.setWords(words);        
        showDictionary(dictionary);
    }

    public void showDictionary(Dictionary dictionary) {
        taDictionary.setText("");                      
        
        List<Word> words = dictionary.getWords();
        
        if(engine.getClassification().getTechnique().equals(ClassificationTechnique.HMM)) {
        
            HmmParameters params = (HmmParameters) engine.getClassification();
            
            for (Word word : words) {
                
                String description = word.getDescription();                
                String phoneticRepresentation = word.getPhoneticRepresentations().toString();
                
                if(params.getUnitSize().equals(HmmUnitSize.WORD)) {                
                    taDictionary.append(description + '\t' + "[" + description + "]" + '\t' + description + '\n');
                } else if(params.getUnitSize().equals(HmmUnitSize.PHONEMES)) {                                                            
                    taDictionary.append(description + '\t' + "[" + description + "]" + '\t' + phoneticRepresentation + '\n');
                }
            }
        }        
        engine.setDictionary(dictionary);        
    }

    public void loadGrammar(List<Utterance> utterances) {

        Grammar grammar = new Grammar();

        List<Word> words = new ArrayList<Word>();
        List<String> descriptions = new ArrayList<String>();
        int wordIndex = 1;
        for (Utterance utterance : utterances) {
            List<Phrase> phrases = utterance.getPhrases();
            for (Phrase phrase : phrases) {
                List<Word> wordsFound = phrase.getWords();
                for (Word word : wordsFound) {
                    if (!descriptions.contains(word.getDescription())) {
                        word.setId(wordIndex++);
                        words.add(word);
                    }
                    descriptions.add(word.getDescription());
                }
            }
        }

        RuleGrammar ruleGrammar = new RuleGrammar();
        ruleGrammar.setId(0);
        ruleGrammar.setWords(descriptions);

        List<RuleGrammar> ruleGrammars = new ArrayList<RuleGrammar>();
        ruleGrammars.add(ruleGrammar);

        grammar.setRuleGrammars(ruleGrammars);
        showGrammar(grammar);
    }

    public void showGrammar(Grammar grammar) {

        taGrammar.setText("");

        taGrammar.append("<grammar>\n");

        for (RuleGrammar ruleGrammar : grammar.getRuleGrammars()) {

            int ruleId = ruleGrammar.getId();

            taGrammar.append("  <rule>\n");
            taGrammar.append("    <id=" + ruleId + ">\n");
            taGrammar.append("    <one-of>\n");

            List<String> descriptions = ruleGrammar.getWords();
            for (String description : descriptions) {
                taGrammar.append("      <item>" + description + "</item>\n");
            }

            taGrammar.append("    </one-of>\n");
            taGrammar.append("  </rule>\n");
        }

        taGrammar.append("</grammar>");

        engine.setGrammar(grammar);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        taDictionary = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        taGrammar = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        taDictionary.setColumns(20);
        taDictionary.setEditable(false);
        taDictionary.setFont(new java.awt.Font("Lucida Console", 0, 13)); // NOI18N
        taDictionary.setRows(5);
        jScrollPane1.setViewportView(taDictionary);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Dicionário");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                    .addComponent(jLabel1))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(9, 9, 9)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        taGrammar.setColumns(20);
        taGrammar.setEditable(false);
        taGrammar.setFont(new java.awt.Font("Lucida Console", 0, 13)); // NOI18N
        taGrammar.setRows(5);
        taGrammar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                taGrammarMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(taGrammar);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("Gramática");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE)
                    .addComponent(jLabel4))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addGap(9, 9, 9)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void taGrammarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_taGrammarMouseClicked
//        GrammarDialog dialog = new GrammarDialog(null, true);
//        dialog.pack();
//        dialog.setLocationRelativeTo(this);
//        dialog.setVisible(true);
//        File grammarFile = new File(facade.getProject().getDirectory() + File.separator + "engine" + File.separator + "grammar.txt");
//        BufferedReader brGrammar;
//        try {
//            taGrammar.setText("");
//            brGrammar = new BufferedReader(new FileReader(grammarFile));
//            while (brGrammar.ready()) {
//                taGrammar.append(brGrammar.readLine() + "\n");
//            }
//            brGrammar.close();
//        } catch (IOException ex) {
//            java.util.logging.Logger.getLogger(SpeechEnginePanel.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }//GEN-LAST:event_taGrammarMouseClicked
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextArea taDictionary;
    private javax.swing.JTextArea taGrammar;
    // End of variables declaration//GEN-END:variables
}
