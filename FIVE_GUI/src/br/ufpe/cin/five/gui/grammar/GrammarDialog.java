/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.gui.grammar;

import br.ufpe.cin.five.core.grammar.RuleGrammar;
import br.ufpe.cin.five.core.utterance.Phrase;
import br.ufpe.cin.five.core.utterance.Utterance;
import br.ufpe.cin.five.core.utterance.Word;
import br.ufpe.cin.five.facade.Facade;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;

/**
 *
 * @author Alexandre
 */
public class GrammarDialog extends JDialog {

    private static final Logger logger = Logger.getLogger(GrammarDialog.class);
    private String enginePath = Facade.getInstance().getProject().getDirectory() + File.separator + "engine";
    private List<String> wordsRule;
    List<RuleGrammar> ruleGrammars;
    int indexRule;

    /** Creates new form SpeakerDialog */
    public GrammarDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        Set<String> wordsSet = new TreeSet();
        List<Utterance> utt = Facade.getInstance().getProject().getUtterances();
        //Buscando palavras não repetidas de todas as locucoes selecionadas
        List<Word> wrds = new ArrayList<Word>();
        for (Utterance utterance : utt) {
            List<Phrase> phrases = utterance.getPhrases();
            for (Phrase phrase : phrases) {
                List<Word> temp = phrase.getWords();
                for (Word word : temp) {
                    if (wordsSet.add(word.getDescription())) {
                        wrds.add(word);
                    }
                }
            }
        }
        //Adicionando vocabulario a partir do projeto
        String words[] = new String[wrds.size()];
        for (int i = 0; i < wrds.size(); i++) {
            words[i] = wrds.get(i).getDescription();
        }
        jtVocabulary.setListData(words);
        //Carregando a gramatica
        this.loadGrammar();
        updateJtRules();
        jtRules.setSelectedIndex(0);
        //Carregando palavras da regra ativa
        wordsRule = ruleGrammars.get(0).getWords();
        indexRule = 0;
        updateJtWords();
    }

    private void updateJtRules() {
        String rules[] = new String[ruleGrammars.size()];
        for (int i = 0; i < ruleGrammars.size(); i++) {
            rules[i] = "Regra " + ruleGrammars.get(i).getId();
        }
        jtRules.setListData(rules);
    }

    private void updateJtWords() {
        String wrdsRule[] = new String[wordsRule.size()];
        for (int i = 0; i < wordsRule.size(); i++) {
            wrdsRule[i] = wordsRule.get(i);
        }
        jtWords.setListData(wrdsRule);
    }

    private void createGrammar() {
/*        File grammarFile = new File(enginePath + File.separator + "grammar.xml");
        Element grammarElement = new Element("grammar");
        for (RuleGrammar ruleGrammar : ruleGrammars) {
            Element ruleElement = new Element("rule");
            ruleElement.setAttribute("id", new Integer(ruleGrammar.getId()).toString());
            Element oneofElement = new Element("one-of");
            for (String palavra : ruleGrammar.getWords()) {
                Element item = new Element("item");
                item.addContent(palavra);
                oneofElement.addContent(item);
            }
            ruleElement.addContent(oneofElement);
            grammarElement.addContent(ruleElement);
        }

        Document grammarDocument = new Document(grammarElement);
        XMLOutputter grammarOutputter = new XMLOutputter(Format.getPrettyFormat());

        FileWriter grammarWriter;
        try {
            grammarWriter = new FileWriter(grammarFile);
            grammarOutputter.output(grammarDocument, grammarWriter);
            grammarWriter.close();
        } catch (IOException ex) {
            logger.error(ex);
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        this.setVisible(false);
        JOptionPane.showMessageDialog(null, "Gramatica criada com sucesso!");*/
    }

    private void loadGrammar() {
/*        try {
            Grammar grammar = new Grammar();
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(enginePath + File.separator + "grammar.xml");
            Element root = document.getRootElement();

            List<Element> ruleList = root.getChildren("rule");

            ArrayList<RuleGrammar> ruleGrammars = new ArrayList<RuleGrammar>();

            for (Element rule : ruleList) {

                ArrayList<String> words = new ArrayList<String>();
                Element list = rule.getChild("one-of");

                List<Element> itens = list.getChildren("item");
                for (Element item : itens) {
                    words.add(item.getText());
                }

                RuleGrammar ruleGrammar = new RuleGrammar();

                ruleGrammar.setId(new Integer(rule.getAttributeValue("id")));
                ruleGrammar.setWords(words);
                ruleGrammar.setActive(false);

                ruleGrammars.add(ruleGrammar);
            }
            grammar.setRuleGrammars(ruleGrammars);
            this.ruleGrammars = grammar.getRuleGrammars();
        } catch (Exception ex) {
            logger.error(ex);
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }*/
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtVocabulary = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        jbAddWord = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jbRemoveWord = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jtWords = new javax.swing.JList();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jtRules = new javax.swing.JList();
        jLabel3 = new javax.swing.JLabel();
        jbNewRule = new javax.swing.JButton();
        jbDelRule = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jbCreateGrammar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jScrollPane2.setViewportView(jtVocabulary);

        jLabel1.setText("Vocabulario :");

        jbAddWord.setText("Adicionar");
        jbAddWord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbAddWordActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(jLabel1))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(jbAddWord)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbAddWord)
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setText("Palavras :");

        jbRemoveWord.setText("Remover");
        jbRemoveWord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbRemoveWordActionPerformed(evt);
            }
        });

        jScrollPane4.setViewportView(jtWords);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addComponent(jLabel2))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addComponent(jbRemoveWord)))
                .addContainerGap(42, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbRemoveWord)
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jtRules.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtRulesMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jtRules);

        jLabel3.setText("Regras :");

        jbNewRule.setText("Criar");
        jbNewRule.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbNewRuleActionPerformed(evt);
            }
        });

        jbDelRule.setText("Deletar");
        jbDelRule.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbDelRuleActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addComponent(jLabel3))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jbNewRule)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jbDelRule)))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbDelRule)
                    .addComponent(jbNewRule))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jbCreateGrammar.setText("Gerar gramatica");
        jbCreateGrammar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbCreateGrammarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(420, Short.MAX_VALUE)
                .addComponent(jbCreateGrammar)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jbCreateGrammar)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtRulesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtRulesMouseClicked
        indexRule = jtRules.getSelectedIndex();
        wordsRule = ruleGrammars.get(indexRule).getWords();
        updateJtWords();

    }//GEN-LAST:event_jtRulesMouseClicked

    private void jbAddWordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbAddWordActionPerformed
        Object wrdsSelect[] = jtVocabulary.getSelectedValues();
        boolean exist = false;
        for (Object objSelect : wrdsSelect) {
            String wordSelect = (String) objSelect;
            for (String existWord : wordsRule) {
                if (existWord.equals(wordSelect)) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                wordsRule.add(wordSelect);
            }
        }
        updateJtWords();
        ruleGrammars.get(indexRule).setWords(wordsRule);
    }//GEN-LAST:event_jbAddWordActionPerformed

    private void jbRemoveWordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbRemoveWordActionPerformed
        Object wrdsSelect[] = jtWords.getSelectedValues();
        for (Object objSelect : wrdsSelect) {
            String wordSelect = (String) objSelect;
            if(wordsRule.size() > 1){
            wordsRule.remove(wordSelect);
            }else{
                JOptionPane.showMessageDialog(null, "A Regra "+ ruleGrammars.get(indexRule).getId() + " deve possuir pelo ou menos 1 palavra!");
            }
        }
        updateJtWords();
        ruleGrammars.get(indexRule).setWords(wordsRule);
    }//GEN-LAST:event_jbRemoveWordActionPerformed

    private void jbNewRuleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbNewRuleActionPerformed
        RuleGrammar newRuleGrammar = new RuleGrammar();
        newRuleGrammar.setWords(new ArrayList<String>());
        wordsRule = newRuleGrammar.getWords();
        indexRule = ruleGrammars.get(ruleGrammars.size() - 1).getId() + 1;
        newRuleGrammar.setId(indexRule);
        ruleGrammars.add(newRuleGrammar);
        updateJtRules();
        updateJtWords();
        jtRules.setSelectedIndex(indexRule);
    }//GEN-LAST:event_jbNewRuleActionPerformed

    private void jbDelRuleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbDelRuleActionPerformed
        ArrayList<RuleGrammar> rulesToDel = new ArrayList<RuleGrammar>();
        for (int indexSelected : jtRules.getSelectedIndices()) {
            if (indexSelected != 0) {
                rulesToDel.add(ruleGrammars.get(indexSelected));                
            } else {
                JOptionPane.showMessageDialog(null, "Regra 0 não pode ser deletada!");
            }
        }
        for (RuleGrammar ruleGrammar : rulesToDel) {
            ruleGrammars.remove(ruleGrammar);
        }
        updateJtRules();
    }//GEN-LAST:event_jbDelRuleActionPerformed

    private void jbCreateGrammarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbCreateGrammarActionPerformed
        createGrammar();
    }//GEN-LAST:event_jbCreateGrammarActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JButton jbAddWord;
    private javax.swing.JButton jbCreateGrammar;
    private javax.swing.JButton jbDelRule;
    private javax.swing.JButton jbNewRule;
    private javax.swing.JButton jbRemoveWord;
    private javax.swing.JList jtRules;
    private javax.swing.JList jtVocabulary;
    private javax.swing.JList jtWords;
    // End of variables declaration//GEN-END:variables
}
