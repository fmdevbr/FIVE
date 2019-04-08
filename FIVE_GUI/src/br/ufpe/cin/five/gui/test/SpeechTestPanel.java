/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.gui.test;


import br.ufpe.cin.five.api.recognizer.Recognizer;
import br.ufpe.cin.five.api.recognizer.RecognizerListener;
import br.ufpe.cin.five.api.recognizer.Result;
import br.ufpe.cin.five.core.grammar.Grammar;
import br.ufpe.cin.five.core.grammar.RuleGrammar;
import br.ufpe.cin.five.facade.Facade;
import java.io.File;

import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.apache.log4j.Logger;

/**
 *
 * @author Alexandre
 */
public class SpeechTestPanel extends JPanel implements RecognizerListener {

    private static final Logger logger = Logger.getLogger(SpeechTestPanel.class);
    private Facade facade = Facade.getInstance();    
    
    private String projectPath = facade.getProject().getDirectory();
    
    private Recognizer recognizer = null; 
    private boolean startRecognizer = false;
    private boolean firstTime = true;
    
    private String rules[];
    private List<String> words;
    private RuleGrammar activeRulerGrammar;

    /** Creates new form TestOnLinePanel */
    public SpeechTestPanel() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        taResult = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jlWords = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jbStart = new javax.swing.JToggleButton();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jlRules = new javax.swing.JList();
        jLabel3 = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(749, 394));

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        taResult.setColumns(20);
        taResult.setEditable(false);
        taResult.setRows(5);
        jScrollPane1.setViewportView(taResult);

        jLabel2.setText("Resultado :");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addComponent(jLabel2)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jlWords.setEnabled(false);
        jScrollPane2.setViewportView(jlWords);

        jLabel1.setText("Regra  ativa :");

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
                        .addComponent(jLabel1)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jbStart.setText("Ligar");
        jbStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbStartActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(425, Short.MAX_VALUE)
                .addComponent(jbStart, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jbStart)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jlRules.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jlRulesMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jlRules);

        jLabel3.setText("Gramática :");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addComponent(jLabel3)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(180, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jbStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbStartActionPerformed
        if (!startRecognizer) {
            try {
                if (firstTime) {
                    
                    recognizer = new Recognizer(projectPath + File.separator + "engine");
                    recognizer.addRecognizerListener(this);
                    firstTime = false;
                   
                    Grammar grammar = recognizer.getGrammar();
                    activeRulerGrammar = grammar.getActiveRule();
                    words = activeRulerGrammar.getWords();
                    String strWords[] = new String[words.size()];
                    for (int i = 0; i < words.size(); i++) {
                        strWords[i] = words.get(i);
                    }
                    jlWords.setListData(strWords);
                    List<RuleGrammar> rgs = grammar.getRuleGrammars();
                    rules = new String[rgs.size()];
                    for (int i = 0; i < rgs.size(); i++) {
                        rules[rgs.get(i).getId()] = "Regra " + rgs.get(i).getId();
                    }
                    jlRules.setListData(rules);
                    jlRules.setSelectedValue(rules[activeRulerGrammar.getId()], true);                   
                }
                jbStart.setText("Desligar");
                recognizer.StartRecognition();
                startRecognizer = true;
            } catch (Exception ex) {
                logger.error(ex);
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        } else {
            recognizer.StopRecognition();
            jbStart.setText("Ligar");
            startRecognizer = false;
        }
}//GEN-LAST:event_jbStartActionPerformed

    private void jlRulesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlRulesMouseClicked
        if (!jlRules.getSelectedValue().equals(rules[this.activeRulerGrammar.getId()])) {
            Grammar grammar = recognizer.getGrammar();
            int idRuler;
            for (idRuler = 0; idRuler < rules.length; idRuler++) {
                if (jlRules.getSelectedValue().equals(rules[idRuler])) {
                    break;
                }
            }
            try {
                recognizer.SetRuleState(idRuler);
            } catch (Exception ex) {
                logger.error(ex);
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
            activeRulerGrammar = grammar.getActiveRule();
            words = activeRulerGrammar.getWords();
            String strWords[] = new String[words.size()];
            for (int i = 0; i < words.size(); i++) {
                strWords[i] = words.get(i);
            }
            jlWords.setListData(strWords);
        }
    }//GEN-LAST:event_jlRulesMouseClicked
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JToggleButton jbStart;
    private javax.swing.JList jlRules;
    private javax.swing.JList jlWords;
    private javax.swing.JTextArea taResult;
    // End of variables declaration//GEN-END:variables

    @Override
    public void onRecognition(Result result) {
        String utterance = result.getAnswer();
        if (utterance != null) {
            taResult.append(utterance + '\n');
            for (int i = 0; i < words.size(); i++) {
                if (utterance.equals(words.get(i))) {
                    jlWords.setSelectedValue(words.get(i), true);
                }
            }
        }
    }

    @Override
    public void onException(Exception ex) {
        recognizer.StopRecognition();
        jbStart.setText("Ligar");
        startRecognizer = false;
        logger.error(ex);
        JOptionPane.showMessageDialog(null, ex.getMessage());
    }
}