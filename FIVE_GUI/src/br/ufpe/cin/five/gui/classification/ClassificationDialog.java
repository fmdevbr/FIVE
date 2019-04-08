/**
 * Copyright 2011 Federal University of Pernambuco. All Rights Reserved. Use is
 * subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.gui.classification;

import br.ufpe.cin.five.core.classification.Classification;
import br.ufpe.cin.five.core.classification.ClassificationTechnique;
import br.ufpe.cin.five.core.classification.gmm.GmmParameters;
import br.ufpe.cin.five.core.classification.hmm.HmmParameters;
import br.ufpe.cin.five.core.classification.svm.SvmParameters;
import br.ufpe.cin.five.core.extraction.Extraction;
import br.ufpe.cin.five.core.extraction.ExtractionTechnique;
import br.ufpe.cin.five.facade.Facade;
import br.ufpe.cin.five.gui.dialogs.FilterDialog;
import br.ufpe.cin.five.core.sample.Environment;
import br.ufpe.cin.five.core.sample.SampleFilter;
import br.ufpe.cin.five.core.speaker.Gender;
import br.ufpe.cin.five.core.speaker.Speaker;
import br.ufpe.cin.five.core.utterance.Utterance;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author Alexandre
 */
public class ClassificationDialog extends javax.swing.JDialog {

    public static final String PANEL_GMM = ClassificationTechnique.GMM.toString();
    public static final String PANEL_SVM = ClassificationTechnique.SVM.toString();
    public static final String PANEL_HMM = ClassificationTechnique.HMM.toString();

    private static final Logger logger = Logger.getLogger(ClassificationDialog.class);
    private Facade facade = Facade.getInstance();
    private int operation;
    private Classification classification;

    private PanelGMMParams gmmPanel;
    private PanelSVMParams svmPanel;
    private PanelHMMParams hmmPanel;

    private FilterDialog filterDialog;
    private CardLayout tecnicaCardLayout;
    private List<Utterance> utterancesFilter;
    private List<Speaker> speakersFilter;

    private DefaultTableModel model;

    /**
     * Creates new form ClassificationDialog
     */
    public ClassificationDialog(java.awt.Frame parent, boolean modal, Classification classification, int operation) {
        super(parent, modal);

        logger.info("Inicializando Classification Dialog. [Operation = " + operation + "]");
        initComponents();

        //Set maximum sizer
//        tfUtterances.setMaximumSize(new Dimension(6, 20));
//        tfSpeakers.setMaximumSize(new Dimension(6, 20));        
        this.operation = operation;

        this.speakersFilter = new ArrayList<Speaker>();
        this.utterancesFilter = new ArrayList<Utterance>();

        //Inicializando os paineis de tecnica
        this.gmmPanel = new PanelGMMParams();
        this.svmPanel = new PanelSVMParams();
        this.hmmPanel = new PanelHMMParams();

        this.tecnicaCardLayout = new CardLayout();
        this.pnTecnicas.setLayout(tecnicaCardLayout);
        this.pnTecnicas.add(this.gmmPanel, PANEL_GMM);
        this.pnTecnicas.add(this.svmPanel, PANEL_SVM);
        this.pnTecnicas.add(this.hmmPanel, PANEL_HMM);
        this.pnTecnicas.setVisible(false);

        ClassificationTechnique[] techniques = ClassificationTechnique.values();
        String[] techniqueDescription = new String[techniques.length + 1];
        techniqueDescription[0] = "--";

        //Não haverá mais seleção direta de todas técnicas
        for (int i = 1; i < techniqueDescription.length; i++) {
            techniqueDescription[i] = techniques[i - 1].toString();
        }
        cbTechnique.setModel(new javax.swing.DefaultComboBoxModel(techniqueDescription));
        cbTechnique.setEnabled(false);

        Environment[] environments = Environment.values();
        String[] environmentDescriptions = new String[environments.length + 1];
        environmentDescriptions[0] = "";
        for (int i = 1; i < environmentDescriptions.length; i++) {
            environmentDescriptions[i] = environments[i - 1].toString();
        }
//        cbEnvironment.setModel(new javax.swing.DefaultComboBoxModel(environmentDescriptions));        

        Gender[] genders = Gender.values();
        String[] genderDescriptions = new String[genders.length + 1];
        genderDescriptions[0] = "";
        for (int i = 1; i < genderDescriptions.length; i++) {
            genderDescriptions[i] = genders[i - 1].toString();
        }
//        cbGender.setModel(new javax.swing.DefaultComboBoxModel(genderDescriptions));        
        //populando a lista de extrações ativas
        model = (DefaultTableModel) jTable1.getModel();
        jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        for (Extraction extraction : facade.getProject().getExtractions()) {
            if (extraction.isActive()) {
                model.addRow(new Object[]{extraction.getId(), extraction.getTechnique().toString(), extraction, extraction.isActive()});
            }
        }
        if (operation == 1) {

            this.classification = classification;

            for (int i = 0; i < jTable1.getRowCount(); i++) {
                if (jTable1.getModel().getValueAt(i, 0).equals(classification.getExtraction().getId())) {
                    jTable1.setRowSelectionInterval(i, i);
                }
            }

            tfTrain.setText((int) (classification.getTrainPercentage() * 100) + "");
            tfTest.setText((int) (classification.getTestPercentage() * 100) + "");
            tfValue.setText(classification.getThresholdValue() + "");

            cbTechnique.setSelectedItem(classification.getTechnique().toString());
            cbTechnique.setEnabled(true);
//            cbEnvironment.setSelectedItem(classification.getSampleFilter().getEnvironment());
//            cbGender.setSelectedItem(classification.getSampleFilter().getGender());
//            if (classification.getSampleFilter().getMaxAge() != -1) {
//                tfMaxAge.setText(String.valueOf(classification.getSampleFilter().getMaxAge()));
//            }
//            if (classification.getSampleFilter().getMinAge() != -1) {
//                tfMinAge.setText(String.valueOf(classification.getSampleFilter().getMinAge()));
//            }
//            cbSNR.setSelectedItem(classification.getSampleFilter().getSnr() + " dB");
//            utterancesFilter = classification.getSampleFilter().getUtterances();
//            speakersFilter = classification.getSampleFilter().getSpeakers();
//
//            tfSpeakers.setText(cbString(new ArrayList<Object>(speakersFilter)));
//            tfUtterances.setText(cbString(new ArrayList<Object>(utterancesFilter)));
            taDescription.setText(classification.getDescription());

            if (classification.getTechnique().toString().equals("GMM")) {
                GmmParameters gmmParams = (GmmParameters) classification;
                gmmPanel.setNumGaussianas(gmmParams.getComponents());
                gmmPanel.setNumIteracoes(gmmParams.getIterations());

            } else if (classification.getTechnique().toString().equals("SVM")) {
                SvmParameters svmParams = (SvmParameters) classification;
                svmPanel.setKernelType(String.valueOf(svmParams.getKernelType()));
                svmPanel.setGamma(String.valueOf(svmParams.getGamma()));
                svmPanel.setCost(String.valueOf(svmParams.getCost()));
                svmPanel.setDegree(String.valueOf(svmParams.getDegree()));

            } else if (classification.getTechnique().toString().equals("HMM")) {
                HmmParameters hmmParams = (HmmParameters) classification;
                hmmPanel.setNumGaussianas(hmmParams.getNumGaussians());
                hmmPanel.setNumIteractions((hmmParams.getNumIteractions()));
                hmmPanel.setNumHERests(hmmParams.getNumHERests());
                hmmPanel.setNumStates(hmmParams.getNumStates());
                hmmPanel.setType(hmmParams.getStatesType());
                hmmPanel.setTopology(hmmParams.getTopology());
                hmmPanel.setUnitSize(hmmParams.getUnitSize());
            }

//            cbEnvironment.setSelectedItem(classification.getSampleFilter().getEnvironment());
//            cbGender.setSelectedItem(classification.getSampleFilter().getGender());
//            if (classification.getSampleFilter().getMaxAge() != -1) {
//                tfMaxAge.setText(String.valueOf(classification.getSampleFilter().getMaxAge()));
//            }
//            if (classification.getSampleFilter().getMinAge() != -1) {
//                tfMinAge.setText(String.valueOf(classification.getSampleFilter().getMinAge()));
//            }
//            cbSNR.setSelectedItem(classification.getSampleFilter().getSnr() + " dB");
//
//            utterancesFilter = classification.getSampleFilter().getUtterances();
//            speakersFilter = classification.getSampleFilter().getSpeakers();
//
//            tfSpeakers.setText(cbString(new ArrayList<Object>(speakersFilter)));
//            tfUtterances.setText(cbString(new ArrayList<Object>(utterancesFilter)));            
        } else {
            this.classification = new Classification();
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
        jPanel2 = new javax.swing.JPanel();
        jlTrain = new javax.swing.JLabel();
        tfTrain = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jlTest = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        tfTest = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jlTechnique = new javax.swing.JLabel();
        cbTechnique = new javax.swing.JComboBox();
        pnTecnicas = new javax.swing.JPanel();
        jlDescription = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        taDescription = new javax.swing.JTextArea();
        jPanel5 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        tfValue = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jlTechnique1 = new javax.swing.JLabel();
        btSave = new javax.swing.JButton();
        btCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Classificação de Padrões");
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jlTrain.setText("Treino:");

        jLabel2.setText("%");

        jlTest.setText("Teste:");

        jLabel5.setText("%");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Divisão da base");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jlTrain)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfTrain, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jlTest)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfTest, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addGap(131, 131, 131))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlTrain)
                    .addComponent(tfTrain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jlTest)
                    .addComponent(tfTest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jlTechnique.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jlTechnique.setText("Técnica:");

        cbTechnique.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbTechniqueActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnTecnicasLayout = new javax.swing.GroupLayout(pnTecnicas);
        pnTecnicas.setLayout(pnTecnicasLayout);
        pnTecnicasLayout.setHorizontalGroup(
            pnTecnicasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 548, Short.MAX_VALUE)
        );
        pnTecnicasLayout.setVerticalGroup(
            pnTecnicasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 152, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jlTechnique)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbTechnique, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnTecnicas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(79, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlTechnique)
                    .addComponent(cbTechnique, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(121, Short.MAX_VALUE))
            .addComponent(pnTecnicas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jlDescription.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jlDescription.setText("Descrição");

        taDescription.setColumns(20);
        taDescription.setLineWrap(true);
        taDescription.setRows(5);
        taDescription.setWrapStyleWord(true);
        jScrollPane1.setViewportView(taDescription);

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel20.setText("Threashold");

        jLabel15.setText("Valor:");

        tfValue.setMinimumSize(new java.awt.Dimension(6, 25));
        tfValue.setPreferredSize(new java.awt.Dimension(6, 25));

        jLabel6.setText("%");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfValue, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addContainerGap(241, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
                        .addGap(172, 172, 172))))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(tfValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addContainerGap())
        );

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "id", "Técnica", "Descrição", "Extraído"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTable1MousePressed(evt);
            }
        });
        jScrollPane2.setViewportView(jTable1);

        jlTechnique1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jlTechnique1.setText("Extração:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlDescription)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 731, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jlTechnique1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 674, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jlTechnique1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlDescription)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btCancel))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btCancel)
                    .addComponent(btSave))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSaveActionPerformed
        logger.info("Botão jSave pressionado. Validando campos.");
        if (validateFields()) {
            logger.info("Campos validados.");
            if ((Integer.parseInt(tfTrain.getText()) + Integer.parseInt(tfTest.getText())) == 100) {
                try {
                    int classificationId = classification.getId();
                    String technique = cbTechnique.getSelectedItem().toString();
                    if (technique.equals("GMM")) {
                        classification = new GmmParameters();
                        ((GmmParameters) classification).setIterations(gmmPanel.getNumIteracoes());
                        ((GmmParameters) classification).setComponents(gmmPanel.getNumGaussianas());
                        if (!tfValue.getText().equals("")) {
                            ((GmmParameters) classification).setThresholdValue(new Double(tfValue.getText()));
                        }
                        classification.setTechnique(ClassificationTechnique.GMM);
                    } else if (technique.equals("SVM")) {
                        classification = new SvmParameters();
                        ((SvmParameters) classification).setKernelType(new Integer(svmPanel.getKernelType()));
                        ((SvmParameters) classification).setGamma(new Integer(svmPanel.getGamma()));
                        ((SvmParameters) classification).setCost(new Integer(svmPanel.getCost()));
                        ((SvmParameters) classification).setDegree(new Integer(svmPanel.getDegree()));
                        classification.setTechnique(ClassificationTechnique.SVM);
                    } else if (technique.equals("HMM")) {
                        classification = new HmmParameters();
                        ((HmmParameters) classification).setNumGaussians(new Integer(hmmPanel.getNumGaussianas()));
                        ((HmmParameters) classification).setNumIteractions(new Integer(hmmPanel.getNumIteractions()));
                        ((HmmParameters) classification).setNumHERests(new Integer(hmmPanel.getNumHERests()));
                        ((HmmParameters) classification).setNumStates(new Integer(hmmPanel.getNumStates()));
                        ((HmmParameters) classification).setType(hmmPanel.getType());
                        ((HmmParameters) classification).setTopology(hmmPanel.getTopology());
                        ((HmmParameters) classification).setUnitSize(hmmPanel.getUnitSize());
                        classification.setTechnique(ClassificationTechnique.HMM);
                    }

                    classification.setTrainPercentage(new Double(tfTrain.getText()) / 100);
                    classification.setTestPercentage(new Double(tfTest.getText()) / 100);

                    if (!tfValue.getText().equals("")) {
                        classification.setThresholdValue(Double.parseDouble(tfValue.getText()));
                    }

                    classification.setDescription(taDescription.getText());
                    //selecionando a extração
                    int line = jTable1.getSelectedRow();
                    Extraction extraction = (Extraction) jTable1.getValueAt(line, 2);
                    classification.setExtraction(extraction);

                    logger.info("Criando filtro da pesquisa.");
//                    SampleFilter filter = new SampleFilter();

//                    filter.setSpeakers(speakersFilter);
//                    filter.setUtterances(utterancesFilter);
//
//                    if (cbEnvironment.getSelectedItem() != null && !cbEnvironment.getSelectedItem().toString().equals("")) {
//                        filter.setEnvironment(Environment.valueOf((String) cbEnvironment.getSelectedItem()));
//                        
//                    }
//                    if (cbGender.getSelectedItem() != null && !cbGender.getSelectedItem().toString().equals("")) {
//                        filter.setGender(Gender.valueOf((String) cbGender.getSelectedItem()));
//                    }
//                    if (!tfMaxAge.getText().equals("")) {
//                        filter.setMaxAge(Integer.parseInt(tfMaxAge.getText()));
//                    }
//                    if (!tfMinAge.getText().equals("")) {
//                        filter.setMinAge(Integer.parseInt(tfMinAge.getText()));
//                    }
//                    if (cbSNR.getSelectedItem() != null && !cbSNR.getSelectedItem().toString().equals("")) {
//                        filter.setSnr(Integer.parseInt(cbSNR.getSelectedItem().toString().split(" ")[0]));
//                    }
                    classification.setSampleFilter(extraction.getSampleFilter());

                    if (operation == 0) {
                        facade.insertClassification(classification);
                        logger.info("Classificaçãoo inserida com sucesso.");
                        JOptionPane.showMessageDialog(null, "Classificação inserida com sucesso.");
                    } else {
                        classification.setId(classificationId);
                        facade.updateClassification(classification);
                        logger.info("Classificação atualizada com sucesso.");
                        JOptionPane.showMessageDialog(null, "Classificação atualizada com sucesso.");
                    }

                    this.setVisible(false);

                } catch (Exception ex) {
                    logger.error(ex.getMessage());
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            } else {
                logger.warn("A divisão da base de dados tem que totalizar 100%");
                JOptionPane.showMessageDialog(null, "A divisão da base de dados tem que totalizar 100%");
            }
        } else {
            logger.warn("Informe os parâmetros necessarios");
            JOptionPane.showMessageDialog(null, "Informe os parâmetros necessários");
        }
    }//GEN-LAST:event_btSaveActionPerformed

    private boolean validateFields() {
        if (tfTrain.getText().equals("")
                || tfTest.getText().equals("")
                || cbTechnique.getSelectedIndex() == -1
                || taDescription.getText().equals("")
                || jTable1.getSelectedRow() == -1) {
            return false;
        } else {
            return true;
        }
    }

    private void btCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCancelActionPerformed
        logger.info("Botão btCancel pressionado.");
        setVisible(false);
    }//GEN-LAST:event_btCancelActionPerformed

    private void cbTechniqueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbTechniqueActionPerformed
        if (!this.pnTecnicas.isVisible()) {
            this.pnTecnicas.setVisible(true);
        }
        this.tecnicaCardLayout.show(this.pnTecnicas, (String) cbTechnique.getSelectedItem());
    }//GEN-LAST:event_cbTechniqueActionPerformed

    private void jTable1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MousePressed
        if (evt.getClickCount() == 1 || evt.getClickCount() == 2) {
            int line = jTable1.getSelectedRow();
            cbTechnique.setEnabled(true);
            Extraction extraction = (Extraction) jTable1.getValueAt(line, 2);
            if (extraction.getTechnique().equals(ExtractionTechnique.HTK)) {
                cbTechnique.removeAllItems();
                cbTechnique.addItem(ClassificationTechnique.HMM.toString());
                cbTechnique.addItem(ClassificationTechnique.GMM.toString());
            }
            if (extraction.getTechnique().equals(ExtractionTechnique.MFCC)) {
                cbTechnique.removeAllItems();
                cbTechnique.addItem(ClassificationTechnique.SVM.toString());
                cbTechnique.addItem(ClassificationTechnique.GMM.toString());
            }
            if (extraction.getTechnique().equals(ExtractionTechnique.MGCC)) {
                cbTechnique.removeAllItems();
                cbTechnique.addItem(ClassificationTechnique.HMM.toString());
                cbTechnique.addItem(ClassificationTechnique.GMM.toString());
            }
        }
    }//GEN-LAST:event_jTable1MousePressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btCancel;
    private javax.swing.JButton btSave;
    private javax.swing.JComboBox cbTechnique;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel jlDescription;
    private javax.swing.JLabel jlTechnique;
    private javax.swing.JLabel jlTechnique1;
    private javax.swing.JLabel jlTest;
    private javax.swing.JLabel jlTrain;
    private javax.swing.JPanel pnTecnicas;
    private javax.swing.JTextArea taDescription;
    private javax.swing.JTextField tfTest;
    private javax.swing.JTextField tfTrain;
    private javax.swing.JTextField tfValue;
    // End of variables declaration//GEN-END:variables

    private String cbString(ArrayList<Object> list) {
        String cbString = "";
        for (int i = 0; i < list.size(); i++) {
            Object ob = list.get(i);
            if (i != list.size() - 1) {
                cbString += ob.toString() + ";";
            } else {
                cbString += ob.toString();
            }
        }
        return cbString;
    }
}
