/**
 * Copyright 2011 Federal University of Pernambuco. All Rights Reserved. Use is
 * subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.gui.engine;

import br.ufpe.cin.five.core.classification.Classification;
import br.ufpe.cin.five.core.classification.hmm.HmmParameters;
import br.ufpe.cin.five.core.classification.svm.SvmParameters;
import br.ufpe.cin.five.core.engine.Engine;
import br.ufpe.cin.five.core.engine.SpeakerEngine;
import br.ufpe.cin.five.core.engine.SpeechEngine;
import br.ufpe.cin.five.core.engine.SynthesisEngine;
import br.ufpe.cin.five.core.extraction.Extraction;
import br.ufpe.cin.five.core.extraction.htk.HtkParameters;
import br.ufpe.cin.five.core.extraction.mfcc.MfccParameters;
import br.ufpe.cin.five.core.project.Project;
import br.ufpe.cin.five.core.project.ProjectType;
import br.ufpe.cin.five.core.utterance.Utterance;
import br.ufpe.cin.five.exception.RegisterException;

import br.ufpe.cin.five.facade.Facade;
import java.awt.CardLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;

/**
 *
 * @author Alexandre
 */
public class EngineDialog extends javax.swing.JDialog {

    private static final Logger logger = Logger.getLogger(EngineDialog.class);
    private Facade facade = Facade.getInstance();
    private Project project;
    private int operation;
    private Engine engine;
    private Extraction extraction;
    private Classification classification;
    private SpeechEnginePanel speechEnginePanel;
    private SpeakerEnginePanel speakerEnginePanel;
    private SynthesisEnginePanel synthesisEnginePanel;
    private CardLayout enginePanelCardLayout;

    /**
     * Creates new form EngineDialog
     */
    public EngineDialog(java.awt.Frame parent, boolean modal, Engine engine, int operation) {
        super(parent, modal);

        logger.info("Inicializando Engine Dialog. [Operation = " + operation + "]");
        initComponents();

        this.operation = operation;
        this.project = facade.getProject();

        this.enginePanelCardLayout = new CardLayout();
        this.pnEngine.setLayout(enginePanelCardLayout);
            
        if (facade.getProject().getType().equals(ProjectType.ASR)) {
            this.speechEnginePanel = new SpeechEnginePanel();
            this.pnEngine.add(this.speechEnginePanel, "Speech");
        } else if (facade.getProject().getType().equals(ProjectType.ASV)) {
            this.speakerEnginePanel = new SpeakerEnginePanel();
            this.pnEngine.add(this.speakerEnginePanel, "Speaker");
        } else if (facade.getProject().getType().equals(ProjectType.TTS)) {
            this.synthesisEnginePanel = new SynthesisEnginePanel();
            this.pnEngine.add(this.synthesisEnginePanel, "Synthesis");
        }
        this.pnEngine.setVisible(true);
        
        this.showModelFiles(project.getDirectory() + File.separator + "models");            

        if (operation == 1) {

            this.engine = engine;

            taDescription.setText(engine.getDescription());
            taDescription.setEnabled(false);

            this.showConfigFile(engine.getExtraction(), engine.getClassification());           
            
            cbModels.setSelectedItem(engine.getModelFile());            
            
            if (engine.getProjectType().equals(ProjectType.ASR)) {
                SpeechEngine speechEngine = (SpeechEngine) engine;
                speechEnginePanel.setEngine(speechEngine);
                speechEnginePanel.showDictionary(speechEngine.getDictionary());
                speechEnginePanel.showGrammar(speechEngine.getGrammar());

            } else if (engine.getProjectType().equals(ProjectType.ASV)) {
                SpeakerEngine speakerEngine = (SpeakerEngine) engine;
                speakerEnginePanel.setSpeakers(speakerEngine.getSpeakers());

            } else if (engine.getProjectType().equals(ProjectType.TTS)) {
                SynthesisEngine synthesisEngine = (SynthesisEngine) engine;
                synthesisEnginePanel.setSpeakers(synthesisEngine.getSpeakers());
            }
            
            lblClassification.setText(engine.getClassification().getTechnique().name());
            lblExtraction.setText(engine.getExtraction().getTechnique().name());
        } else {
            
            try {
                
                Extraction activeExtraction = facade.searchExtraction(true);                
                Classification activeClassification = facade.searchClassification(true);                
                                              
                if (facade.getProject().getType().equals(ProjectType.ASR)) {
                    
                    this.engine = new SpeechEngine();                                   
                    
                    this.engine.setProjectType(ProjectType.ASR);                                                        
                    
                    this.engine.setExtraction(activeExtraction);
                    this.showConfigFile(activeExtraction, activeClassification);

                    this.engine.setClassification(activeClassification);
                    this.showModelFiles(facade.getProject().getDirectory() + File.separator + "models");                                                       
                    
                    speechEnginePanel.setEngine((SpeechEngine)this.engine);
                                                                                                   
                    List<Utterance> utterances = null;
                    if (classification != null && classification.getSampleFilter().getUtterances().size() > 0) {
                        utterances = classification.getSampleFilter().getUtterances();
                    } else {
                        utterances = facade.getProject().getUtterances();
                    }                                                                               
                    speechEnginePanel.loadDictionary(utterances);
                    speechEnginePanel.loadGrammar(utterances);                                       
                }
                                
            } catch (RegisterException ex) {
                logger.error(ex.getMessage());
                JOptionPane.showMessageDialog(null, ex.getMessage());                
            }
        } 
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        pnEngine = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        taConfigFile = new javax.swing.JTextArea();
        jPanel6 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        taDescription = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        cbModels = new javax.swing.JComboBox();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblExtraction = new javax.swing.JLabel();
        lblClassification = new javax.swing.JLabel();
        btSave = new javax.swing.JButton();
        btCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Geração do Motor");
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout pnEngineLayout = new javax.swing.GroupLayout(pnEngine);
        pnEngine.setLayout(pnEngineLayout);
        pnEngineLayout.setHorizontalGroup(
            pnEngineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnEngineLayout.setVerticalGroup(
            pnEngineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 225, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnEngine, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnEngine, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Configurações");

        taConfigFile.setEditable(false);
        taConfigFile.setColumns(20);
        taConfigFile.setFont(new java.awt.Font("Lucida Console", 0, 13)); // NOI18N
        taConfigFile.setRows(8);
        jScrollPane2.setViewportView(taConfigFile);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel16.setText("Nome do Motor");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addGap(0, 80, Short.MAX_VALUE))
                    .addComponent(taDescription))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(taDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel17.setText("Modelos");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(cbModels, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbModels, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("Resumo das Técnicas Selecionadas");

        jLabel2.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel2.setText("Extração de Características:");

        jLabel4.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel4.setText("Classificação:");

        lblExtraction.setText("jLabel3");

        lblClassification.setText("jLabel5");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4)
                            .addComponent(lblExtraction)
                            .addComponent(lblClassification))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblExtraction)
                .addGap(25, 25, 25)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblClassification)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        btSave.setText("Salvar");
        btSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSaveActionPerformed(evt);
            }
        });

        btCancel.setText("Cancelar");
        btCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btCancel))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btCancel)
                    .addComponent(btSave))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSaveActionPerformed
        logger.info("Botão jSave pressionado. Validando campos.");
        if (validateFields()) {
            logger.info("Campos validados.");

            try {
                int engineId = engine.getId();

                ProjectType projetType = facade.getProject().getType();

                if (projetType.equals(ProjectType.ASR)) {
                    engine = new SpeechEngine();
                    ((SpeechEngine) engine).setDictionary(speechEnginePanel.getEngine().getDictionary());
                    ((SpeechEngine) engine).setGrammar(speechEnginePanel.getEngine().getGrammar());

                } else if (projetType.equals(ProjectType.ASV)) {
                    engine = new SpeakerEngine();
                    ((SpeakerEngine) engine).setSpeakers(speakerEnginePanel.getEngine().getSpeakers());

                } else if (projetType.equals(ProjectType.TTS)) {
                    engine = new SynthesisEngine();
                    ((SynthesisEngine) engine).setSpeakers(speakerEnginePanel.getEngine().getSpeakers());
                }

                engine.setProjectType(projetType);
                engine.setDescription(taDescription.getText());
                engine.setExtraction(extraction);
                engine.setClassification(classification);
                engine.setModelFile(cbModels.getSelectedItem().toString());

                if (operation == 0) {
                    facade.insertEngine(engine);
                    logger.info("Motor inserido com sucesso.");
                    JOptionPane.showMessageDialog(null, "Motor inserido com sucesso.");
                } else {
                    engine.setId(engineId);
                    facade.updateEngine(engine);
                    logger.info("Motor atualizado com sucesso.");
                    JOptionPane.showMessageDialog(null, "Motor atualizado com sucesso.");
                }
                this.setVisible(false);

            } catch (Exception ex) {
                logger.error(ex.getMessage());
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        } else {
            logger.warn("Informe os parâmetros necessários");
            JOptionPane.showMessageDialog(null, "Informe os parâmetros necessários");
        }
    }//GEN-LAST:event_btSaveActionPerformed

    private boolean validateFields() {
        if (taDescription.getText().equals("")) {
            return false;
        } else {
            return true;
        }
    }

    private void btCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCancelActionPerformed
        logger.info("Botão btCancel pressionado.");
        setVisible(false);
    }//GEN-LAST:event_btCancelActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btCancel;
    private javax.swing.JButton btSave;
    private javax.swing.JComboBox cbModels;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblClassification;
    private javax.swing.JLabel lblExtraction;
    private javax.swing.JPanel pnEngine;
    private javax.swing.JTextArea taConfigFile;
    private javax.swing.JTextField taDescription;
    // End of variables declaration//GEN-END:variables

    private void showModelFiles(String modelsPath) {
                            
        File modelFile = new File(modelsPath);
        File path = new File(modelFile.getPath());
        File[] files = path.listFiles();
      
        
        String[] filesDescriptions = new String[files.length + 1];
        filesDescriptions[0] = "";
        for (int i = 1; i < filesDescriptions.length; i++) {
            filesDescriptions[i] = files[i - 1].getName();
        }
        cbModels.setModel(new javax.swing.DefaultComboBoxModel(filesDescriptions));                 
    }

    private void showConfigFile(Extraction extraction, Classification classification) {

        taConfigFile.setText("");

        taConfigFile.append("<config>\n");

        taConfigFile.append("  <projectType>" + project.getType().toString() + "</projectType>\n");

        taConfigFile.append("  <extraction>\n");

        this.extraction = extraction;
        if (extraction != null) {
            switch (extraction.getTechnique()) {
                case HTK:
                    HtkParameters htkParams = (HtkParameters) extraction;
                    taConfigFile.append("    <technique>" + htkParams.getTechnique() + "</technique>\n");
                    taConfigFile.append("    <vad>" + htkParams.isVad() + "</vad>\n");
                    taConfigFile.append("    <normalize>" + htkParams.isNormalized() + "</normalize>\n");
                    taConfigFile.append("    <frameDuration>" + htkParams.getFrameDuration() + "</frameDuration>\n");
                    taConfigFile.append("    <alphaPreEmphasis>" + htkParams.getPreEmphasis() + "</alphaPreEmphasis>\n");
                    taConfigFile.append("    <overLap>" + htkParams.getOverlap() + "</overLap>\n");
                    taConfigFile.append("    <windowing>" + htkParams.getWindowing().toString() + "</windowing>\n");
                    taConfigFile.append("    <numMelFilters>" + htkParams.getNumMelFilters() + "</numMelFilters>\n");
                    taConfigFile.append("    <numCoefs>" + htkParams.getNumCoefs() + "</numCoefs>\n");
                    this.engine.setExtraction(htkParams);
                    break;

                case MFCC:
                    MfccParameters mfccParams = (MfccParameters) extraction;
                    taConfigFile.append("    <technique>" + mfccParams.getTechnique() + "</technique>\n");
                    taConfigFile.append("    <vad>" + mfccParams.isVad() + "</vad>\n");
                    taConfigFile.append("    <normalize>" + mfccParams.isNormalized() + "</normalize>\n");
                    taConfigFile.append("    <frameDuration>" + mfccParams.getFrameDuration() + "</frameDuration>\n");
                    taConfigFile.append("    <alphaPreEmphasis>" + mfccParams.getPreEmphasis() + "</alphaPreEmphasis>\n");
                    taConfigFile.append("    <overLap>" + mfccParams.getOverlap() + "</overLap>\n");
                    taConfigFile.append("    <windowing>" + mfccParams.getWindowing().toString() + "</windowing>\n");
                    taConfigFile.append("    <numMelFilters>" + mfccParams.getNumMelFilters() + "</numMelFilters>\n");
                    taConfigFile.append("    <numCoefs>" + mfccParams.getNumCoefs() + "</numCoefs>\n");
                    taConfigFile.append("    <powerSpectrum>" + mfccParams.isPowerSpectrum() + "</powerSpectrum>\n");
                    taConfigFile.append("    <lowerFreq>" + mfccParams.getLowerFreq() + "</lowerFreq>\n");
                    taConfigFile.append("    <upperFreq>" + mfccParams.getUpperFreq() + "</upperFreq>\n");
                    taConfigFile.append("    <frameLength>" + mfccParams.getFrameLength() + "</frameLength>\n");
                    this.engine.setExtraction(mfccParams);
                    break;
            }
        }

        taConfigFile.append("  </extraction>\n");
        taConfigFile.append("  <classification>\n");

        this.classification = classification;
        if (classification != null) {
            switch (classification.getTechnique()) {
                case HMM:
                    HmmParameters hmmParams = (HmmParameters) classification;
                    taConfigFile.append("    <technique>" + hmmParams.getTechnique() + "</technique>\n");
                    taConfigFile.append("    <numGaussians>" + hmmParams.getNumGaussians() + "</numGaussians>\n");
                    taConfigFile.append("    <numIterections>" + hmmParams.getNumIteractions() + "</numIterections>\n");
                    taConfigFile.append("    <numHERests>" + hmmParams.getNumHERests() + "</numHERests>\n");
                    taConfigFile.append("    <numStates>" + hmmParams.getNumStates() + "</numStates>\n");
                    taConfigFile.append("    <hmmTypes>" + hmmParams.getStatesType() + "</hmmTypes>\n");
                    taConfigFile.append("    <hmmtopology>" + hmmParams.getTopology() + "</hmmtopology>\n");
                    taConfigFile.append("    <hmmtopology>" + hmmParams.getTopology() + "</hmmtopology>\n");
                    taConfigFile.append("    <thresholdValue>" + hmmParams.getThresholdValue() + "</thresholdValue>\n");
                    taConfigFile.append("    <unitSize>" + hmmParams.getUnitSize() + "</unitSize>\n");
                    engine.setClassification(hmmParams);
                    break;

                case SVM:
                    SvmParameters svmParams = (SvmParameters) classification;
                    taConfigFile.append("    <technique>" + svmParams.getTechnique() + "</technique>\n");
                    taConfigFile.append("    <kernelType>" + svmParams.getKernelType() + "</kernelType>\n");
                    taConfigFile.append("    <cost>" + svmParams.getCost() + "</cost>\n");
                    taConfigFile.append("    <degree>" + svmParams.getDegree() + "</degree>\n");
                    taConfigFile.append("    <gamma>" + svmParams.getGamma() + "</gamma>\n");
                    taConfigFile.append("    <thresholdValue>" + svmParams.getThresholdValue() + "</thresholdValue>\n");
                    engine.setClassification(svmParams);
                    break;
            }
        }        
        taConfigFile.append("  </classification>\n");
        taConfigFile.append("</config>\n");
    }
}
