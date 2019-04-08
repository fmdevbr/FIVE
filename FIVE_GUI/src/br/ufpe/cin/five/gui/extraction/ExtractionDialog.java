/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.gui.extraction;

import br.ufpe.cin.five.core.extraction.ExtractionTechnique;
import br.ufpe.cin.five.core.extraction.Extraction;
import br.ufpe.cin.five.core.extraction.mfcc.MfccParameters;
import br.ufpe.cin.five.core.sample.SampleFilter;
import br.ufpe.cin.five.core.speaker.Speaker;
import br.ufpe.cin.five.core.utterance.Utterance;
import br.ufpe.cin.five.core.sample.Environment;
import br.ufpe.cin.five.core.speaker.Gender;
import br.ufpe.cin.five.core.extraction.WindowingTechnique;
import br.ufpe.cin.five.core.extraction.htk.HtkParameters;
import br.ufpe.cin.five.core.extraction.mgcc.MgccParameters;
import br.ufpe.cin.five.facade.Facade;
import br.ufpe.cin.five.gui.dialogs.FilterDialog;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;

/**
 *
 * @author Alexandre
 */ 
public class ExtractionDialog extends JDialog {

    public static final String PANEL_MFCC = "MFCC";
    public static final String PANEL_HTK = "HTK";
    public static final String PANEL_MGCC = "MGCC";
    private static final Logger logger = Logger.getLogger(ExtractionDialog.class);
    private Facade facade = Facade.getInstance();
    private int operation;
    private Extraction extraction;
    private PanelMFCCParams mfccPanel;
    private PanelHTKParams htkPanel;
    private PanelMGCCParams mgccPanel;
    private FilterDialog filterDialog;
    private List<Utterance> utterancesFilter;
    private List<Speaker> speakersFilter;
    private CardLayout tecnicaCardLayout;

    /** Creates new form ExtractionDialog */
    public ExtractionDialog(java.awt.Frame parent, boolean modal, Extraction extraction, int operation) {
        super(parent, modal);

        logger.info("Inicializando Extraction Dialog.[Operation = " + operation + "]");
        initComponents();

        //Set maximum sizer
        tfUtterances.setMaximumSize(new Dimension(6, 20));
        tfSpeakers.setMaximumSize(new Dimension(6, 20));

        this.operation = operation;

        this.speakersFilter = new ArrayList<Speaker>();
        this.utterancesFilter = new ArrayList<Utterance>();

        //Inicializando os paineis de tecnica
        mfccPanel = new PanelMFCCParams();
        htkPanel = new PanelHTKParams();
        mgccPanel = new PanelMGCCParams();

        this.tecnicaCardLayout = new CardLayout();
        this.pnTecnicas.setLayout(tecnicaCardLayout);
        this.pnTecnicas.add(this.mfccPanel, PANEL_MFCC);
        this.pnTecnicas.add(this.htkPanel, PANEL_HTK);
        this.pnTecnicas.add(this.mgccPanel, PANEL_MGCC);
        this.pnTecnicas.setVisible(false);

        ExtractionTechnique[] techniques = ExtractionTechnique.values();
        String[] techniqueDescription = new String[techniques.length + 1];
        techniqueDescription[0] = "--";
        for (int i = 1; i < techniqueDescription.length; i++) {
            techniqueDescription[i] = techniques[i - 1].toString();
        }
        cbTechnique.setModel(new javax.swing.DefaultComboBoxModel(techniqueDescription));

        cbDuracaoFrame.setSelectedItem("25");
        cbSuperposicao.setSelectedItem("0.6");

        Environment[] environments = Environment.values();
        String[] environmentDescriptions = new String[environments.length + 1];
        environmentDescriptions[0] = "";
        for (int i = 1; i < environmentDescriptions.length; i++) {
            environmentDescriptions[i] = environments[i - 1].toString();
        }
        cbEnvironment.setModel(new javax.swing.DefaultComboBoxModel(environmentDescriptions));        
        
        Gender[] genders = Gender.values();
        String[] genderDescriptions = new String[genders.length + 1];
        genderDescriptions[0] = "";
        for (int i = 1; i < genderDescriptions.length; i++) {
            genderDescriptions[i] = genders[i - 1].toString();
        }
        cbGender.setModel(new javax.swing.DefaultComboBoxModel(genderDescriptions));        
        
        if (operation == 1) {

            this.extraction = extraction;

            if (extraction.isVad()) {
                rbVADsim.setSelected(true);
            } else {
                rbVADnao.setSelected(true);
            }

            if (extraction.isNormalized()) {
                rbNormalizeSim.setSelected(true);
            } else {
                rbNormalizeNao.setSelected(true);
            }

            cbTechnique.setSelectedItem(extraction.getTechnique().toString());

            cbSuperposicao.setSelectedItem(Double.toString(extraction.getOverlap()));
            cbDuracaoFrame.setSelectedItem(String.valueOf(extraction.getFrameDuration()));
            cbJanelamento.setSelectedItem(extraction.getWindowing().toString());
            tfPreEnfase.setText(Double.toString(extraction.getPreEmphasis()));

            taDescription.setText(extraction.getDescription());

            if (extraction instanceof MfccParameters) {
                MfccParameters mfccParams = (MfccParameters) extraction;
                mfccPanel.setNumFilters(mfccParams.getNumMelFilters());
                mfccPanel.setEspectrum(mfccParams.isPowerSpectrum());
                mfccPanel.setNumCoefs(mfccParams.getNumCoefs());

            } else if (extraction instanceof HtkParameters) {
                HtkParameters htkParams = (HtkParameters) extraction;
                htkPanel.setNumFilters(htkParams.getNumMelFilters());
                htkPanel.setNumCoefs(htkParams.getNumCoefs());
            } else if (extraction instanceof MgccParameters) {
                MgccParameters mgccParams = (MgccParameters) extraction;
                mgccPanel.getCbJanelamento().setSelectedIndex(mgccParams.getWindowtype());
                mgccPanel.getCbNormalizacao().setSelectedIndex(mgccParams.getNormalize());
                mgccPanel.getTxTamanhoFFT().setText(String.valueOf(mgccParams.getFftlen()));
                mgccPanel.getTxFatorDistorcaoFrequencia().setText(String.valueOf(mgccParams.getFreqwarp()));
                mgccPanel.getTxPesoPoleZeroAnaliseMGC().setText(String.valueOf(mgccParams.getGamma()));
                mgccPanel.getTxOrdemAnaliseMGC().setText(String.valueOf(mgccParams.getMgcorder()));
                mgccPanel.getCbGanho().setSelectedIndex(mgccParams.getLngain());
                mgccPanel.getTxLimiteInferiorExtracaoF0().setText(String.valueOf(mgccParams.getLowerf0()));
                mgccPanel.getTxLimiteSuperiorExtracaoF0().setText(String.valueOf(mgccParams.getUpperf0()));
                mgccPanel.getTxDesvioPadraoParaRuidosBrancos().setText(String.valueOf(mgccParams.getNoisemask()));
            }

            cbEnvironment.setSelectedItem(extraction.getSampleFilter().getEnvironment());
            cbGender.setSelectedItem(extraction.getSampleFilter().getGender());
            if (extraction.getSampleFilter().getMaxAge() != -1) {
                tfMaxAge.setText(String.valueOf(extraction.getSampleFilter().getMaxAge()));
            }
            if (extraction.getSampleFilter().getMinAge() != -1) {
                tfMinAge.setText(String.valueOf(extraction.getSampleFilter().getMinAge()));
            }
            cbSNR.setSelectedItem(extraction.getSampleFilter().getSnr() + " dB");

            utterancesFilter = extraction.getSampleFilter().getUtterances();
            speakersFilter = extraction.getSampleFilter().getSpeakers();

            tfSpeakers.setText(cbString(new ArrayList<Object>(speakersFilter)));
            tfUtterances.setText(cbString(new ArrayList<Object>(utterancesFilter)));
        } else {
            this.extraction = new Extraction();
        }
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
        jPanel1 = new javax.swing.JPanel();
        jlDescription = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        taDescription = new javax.swing.JTextArea();
        jPanel6 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jlSpeaker = new javax.swing.JLabel();
        jlAge = new javax.swing.JLabel();
        jlGender = new javax.swing.JLabel();
        cbGender = new javax.swing.JComboBox();
        tfMinAge = new javax.swing.JTextField();
        tfMaxAge = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jlUtterance = new javax.swing.JLabel();
        jlEnvironment = new javax.swing.JLabel();
        cbEnvironment = new javax.swing.JComboBox();
        jlSNR = new javax.swing.JLabel();
        cbSNR = new javax.swing.JComboBox();
        tfSpeakers = new javax.swing.JTextField();
        tfUtterances = new javax.swing.JTextField();
        btAddSpeaker = new javax.swing.JButton();
        btAddUtterance = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        rbVADnao = new javax.swing.JRadioButton();
        rbVADsim = new javax.swing.JRadioButton();
        jLabel9 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        cbSuperposicao = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        tfPreEnfase = new javax.swing.JTextField();
        cbDuracaoFrame = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        cbJanelamento = new javax.swing.JComboBox();
        jPanel7 = new javax.swing.JPanel();
        rbNormalizeNao = new javax.swing.JRadioButton();
        rbNormalizeSim = new javax.swing.JRadioButton();
        jLabel10 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jlTechnique = new javax.swing.JLabel();
        cbTechnique = new javax.swing.JComboBox();
        pnTecnicas = new javax.swing.JPanel();
        btSave = new javax.swing.JButton();
        btCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Extração de Características");
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jlDescription.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jlDescription.setText("Descrição");

        taDescription.setColumns(20);
        taDescription.setLineWrap(true);
        taDescription.setRows(5);
        taDescription.setWrapStyleWord(true);
        jScrollPane1.setViewportView(taDescription);

        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel15.setText("Filtros");

        jlSpeaker.setText("Locutor:");

        jlAge.setText("Idade:");

        jlGender.setText("Sexo:");

        jLabel21.setText("-");

        jlUtterance.setText("Locução:");

        jlEnvironment.setText("Ambiente:");

        jlSNR.setText("SNR:");

        cbSNR.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "5 dB", "10 dB", "15 dB" }));

        tfSpeakers.setEditable(false);

        tfUtterances.setEditable(false);

        btAddSpeaker.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/ufpe/cin/five/images/lupa_icone.png"))); // NOI18N
        btAddSpeaker.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAddSpeakerActionPerformed(evt);
            }
        });

        btAddUtterance.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/ufpe/cin/five/images/lupa_icone.png"))); // NOI18N
        btAddUtterance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAddUtteranceActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlSpeaker)
                            .addComponent(jlUtterance))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tfUtterances, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
                            .addComponent(tfSpeakers, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btAddSpeaker, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btAddUtterance, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(54, 54, 54)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jlEnvironment)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbEnvironment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jlSNR)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbSNR, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                                .addComponent(jlGender)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbGender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jlAge)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfMinAge, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfMaxAge, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jLabel15))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jlGender)
                            .addComponent(cbGender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlAge)
                            .addComponent(tfMinAge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tfMaxAge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel21))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbSNR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlEnvironment)
                            .addComponent(cbEnvironment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlSNR)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btAddSpeaker, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jlSpeaker)
                                    .addComponent(tfSpeakers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jlUtterance)
                                .addComponent(tfUtterances, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btAddUtterance, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        buttonGroup1.add(rbVADnao);
        rbVADnao.setText("Não");

        buttonGroup1.add(rbVADsim);
        rbVADsim.setText("Sim");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("Aplicar VAD?");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(rbVADsim)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(rbVADnao))
                    .addComponent(jLabel9))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbVADsim)
                    .addComponent(rbVADnao))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel6.setText("Superposição:");

        cbSuperposicao.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0.0", "0.25", "0.5", "0.6", "0.75" }));

        jLabel5.setText("Pré-ênfase:");

        tfPreEnfase.setText("0.97");

        cbDuracaoFrame.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10", "15", "20", "25", "30" }));

        jLabel8.setText("Duração Frame:");

        jLabel7.setText("Janelamento:");

        cbJanelamento.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "HAMMING", "HANNING", "RECTANGULAR" }));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(13, 13, 13)
                        .addComponent(cbSuperposicao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDuracaoFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 51, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(26, 26, 26)
                        .addComponent(tfPreEnfase, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(cbJanelamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(56, 56, 56))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(tfPreEnfase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jLabel7))
                            .addComponent(cbJanelamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jLabel6))
                            .addComponent(cbSuperposicao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jLabel8))
                            .addComponent(cbDuracaoFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(16, 16, 16))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        buttonGroup2.add(rbNormalizeNao);
        rbNormalizeNao.setText("Não");

        buttonGroup2.add(rbNormalizeSim);
        rbNormalizeSim.setText("Sim");

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("Normalizar?");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(rbNormalizeSim)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(rbNormalizeNao))
                    .addComponent(jLabel10))
                .addContainerGap(8, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbNormalizeSim)
                    .addComponent(rbNormalizeNao))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jlTechnique.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jlTechnique.setText("Técnica:");
        jPanel4.add(jlTechnique, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 22, -1, -1));

        cbTechnique.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbTechniqueActionPerformed(evt);
            }
        });
        jPanel4.add(cbTechnique, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 14, -1, -1));

        javax.swing.GroupLayout pnTecnicasLayout = new javax.swing.GroupLayout(pnTecnicas);
        pnTecnicas.setLayout(pnTecnicasLayout);
        pnTecnicasLayout.setHorizontalGroup(
            pnTecnicasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 650, Short.MAX_VALUE)
        );
        pnTecnicasLayout.setVerticalGroup(
            pnTecnicasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jPanel4.add(pnTecnicas, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 650, 100));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 674, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jlDescription)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 674, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btCancel)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btSave)
                    .addComponent(btCancel))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSaveActionPerformed
        logger.info("Botão btSave pressionado. Operation = " + operation);
        if (validateFields()) {
            logger.info("Campos validados.");
            try {
                int extractionId = extraction.getId();
                String technique = cbTechnique.getSelectedItem().toString();
                if (technique.equals("MFCC")) {
                    extraction = new MfccParameters();
                    ((MfccParameters) extraction).setNumMelFilters(mfccPanel.getNumFilters());
                    ((MfccParameters) extraction).setPowerSpectrum(mfccPanel.getEspectrum());
                    ((MfccParameters) extraction).setNumCoefs(mfccPanel.getNumCoefs());
                    extraction.setTechnique(ExtractionTechnique.MFCC);

                } else if (technique.equals("HTK")) {
                    extraction = new HtkParameters();
                    ((HtkParameters) extraction).setNumMelFilters(htkPanel.getNumFilters());
                    ((HtkParameters) extraction).setNumCoefs(htkPanel.getNumCoefs());
                    extraction.setTechnique(ExtractionTechnique.HTK);
                } else if (technique.equals("MGCC")) {
                    extraction = new MgccParameters();
                    ((MgccParameters) extraction).setWindowtype(mgccPanel.getCbJanelamento().getSelectedIndex());
                    ((MgccParameters) extraction).setNormalize(mgccPanel.getCbNormalizacao().getSelectedIndex());
                    ((MgccParameters) extraction).setFftlen(Integer.parseInt(mgccPanel.getTxTamanhoFFT().getText()));
                    ((MgccParameters) extraction).setFreqwarp(Double.parseDouble(mgccPanel.getTxFatorDistorcaoFrequencia().getText()));
                    ((MgccParameters) extraction).setGamma(Integer.parseInt(mgccPanel.getTxPesoPoleZeroAnaliseMGC().getText()));
                    ((MgccParameters) extraction).setMgcorder(Integer.parseInt(mgccPanel.getTxOrdemAnaliseMGC().getText()));
                    ((MgccParameters) extraction).setLowerf0(Integer.parseInt(mgccPanel.getTxLimiteInferiorExtracaoF0().getText()));
                    ((MgccParameters) extraction).setUpperf0(Integer.parseInt(mgccPanel.getTxLimiteSuperiorExtracaoF0().getText()));
                    ((MgccParameters) extraction).setNoisemask(Integer.parseInt(mgccPanel.getTxDesvioPadraoParaRuidosBrancos().getText()));
                    extraction.setTechnique(ExtractionTechnique.MGCC);
                }

                extraction.setVad(rbVADsim.isSelected());
                extraction.setNormalize(rbNormalizeSim.isSelected());
                extraction.setFrameDuration(new Integer(cbDuracaoFrame.getSelectedItem().toString()));
                extraction.setOverlap(new Double(cbSuperposicao.getSelectedItem().toString()));
                extraction.setPreEmphasis(new Double(tfPreEnfase.getText()));

                String janelamento = cbJanelamento.getSelectedItem().toString();
                WindowingTechnique windowing = null;
                if (janelamento.equals("HANNING")) {
                    windowing = WindowingTechnique.HANNING;
                } else if (janelamento.equals("HAMMING")) {
                    windowing = WindowingTechnique.HAMMING;
                } else if (janelamento.equals("RECTANGULAR")) {
                    windowing = WindowingTechnique.RECTANGULAR;
                }
                extraction.setWindowing(windowing);

                extraction.setDescription(taDescription.getText());

                logger.info("Criando filtro da pesquisa.");
                SampleFilter filter = new SampleFilter();

                filter.setSpeakers(speakersFilter);
                filter.setUtterances(utterancesFilter);

                if (cbEnvironment.getSelectedItem() != null && !cbEnvironment.getSelectedItem().toString().equals("")) {
                    filter.setEnvironment(Environment.valueOf((String) cbEnvironment.getSelectedItem()));
                }
                if (cbGender.getSelectedItem() != null && !cbGender.getSelectedItem().toString().equals("")) {
                    filter.setGender(Gender.valueOf((String) cbGender.getSelectedItem()));
                }
                if (!tfMaxAge.getText().equals("")) {
                    filter.setMaxAge(Integer.parseInt(tfMaxAge.getText()));
                }
                if (!tfMinAge.getText().equals("")) {
                    filter.setMinAge(Integer.parseInt(tfMinAge.getText()));
                }
                if (cbSNR.getSelectedItem() != null && !cbSNR.getSelectedItem().toString().equals("")) {
                    filter.setSnr(Integer.parseInt(cbSNR.getSelectedItem().toString().split(" ")[0]));
                }

                extraction.setSampleFilter(filter);

                if (operation == 0) {
                    facade.insertExtraction(extraction); 
                    logger.info("Extração inserida com sucesso.");
                    JOptionPane.showMessageDialog(null, "Extração inserida com sucesso.");
                } else {
                    extraction.setId(extractionId);
                    facade.updateExtraction(extraction);
                    logger.info("Extração atualizada com sucesso.");
                    JOptionPane.showMessageDialog(null, "Extração atualizada com sucesso.");
                }
                this.setVisible(false);

            } catch (Exception ex) {
                logger.error(ex.getMessage());
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        } else {
            logger.warn("Informe os parâmetros necessarios");
            JOptionPane.showMessageDialog(null, "Informe os parâmetros necessários");
        }
    }//GEN-LAST:event_btSaveActionPerformed

    private Boolean validateFields() {
        if ((!rbVADsim.isSelected() & !rbVADnao.isSelected())
                || (!rbNormalizeSim.isSelected() & !rbNormalizeNao.isSelected())
                || cbTechnique.getSelectedIndex() == 0
                || taDescription.getText().equals("")) {
            return false;
        } else {
            return true;
        }
    }

    private void btCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCancelActionPerformed
        logger.info("Botão btCancel pressionado.");
        this.setVisible(false);
    }//GEN-LAST:event_btCancelActionPerformed

    private void btAddSpeakerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAddSpeakerActionPerformed
        logger.info("Botão btAddSpeaker pressionado.");
        this.filterDialog = new FilterDialog(null, true, 1, new ArrayList<Object>(this.speakersFilter));
        filterDialog.pack();
        filterDialog.setLocationRelativeTo(this);
        filterDialog.setVisible(true);
        this.speakersFilter = new ArrayList<Speaker>();
        String cbString = "";
        for (int i = 0; i < filterDialog.getList().size(); i++) {
            Speaker speaker = (Speaker) filterDialog.getList().get(i);
            if (i != filterDialog.getList().size() - 1) {
                cbString += speaker.getName() + " ; ";
            } else {
                cbString += speaker.getName();
            }
            speakersFilter.add(speaker);
        }
        tfSpeakers.setText(cbString);
}//GEN-LAST:event_btAddSpeakerActionPerformed

    private void btAddUtteranceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAddUtteranceActionPerformed
        logger.info("Botão btAddUtterance pressionado.");
        this.filterDialog = new FilterDialog(null, true, 0, new ArrayList<Object>(this.utterancesFilter));
        this.filterDialog.pack();
        this.filterDialog.setLocationRelativeTo(this);
        this.filterDialog.setVisible(true);
        this.utterancesFilter = new ArrayList<Utterance>();
        String cbString = "";
        for (int i = 0; i < this.filterDialog.getList().size(); i++) {
            Utterance utterance = (Utterance) this.filterDialog.getList().get(i);
            if (i != this.filterDialog.getList().size() - 1) {
                cbString += utterance.getDescription() + " ; ";
            } else {
                cbString += utterance.getDescription();
            }
            this.utterancesFilter.add(utterance);
        }
        tfUtterances.setText(cbString);
}//GEN-LAST:event_btAddUtteranceActionPerformed

    private void cbTechniqueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbTechniqueActionPerformed
        if (!this.pnTecnicas.isVisible()) {
            this.pnTecnicas.setVisible(true);
        }
        this.tecnicaCardLayout.show(this.pnTecnicas, cbTechnique.getSelectedItem().toString());
}//GEN-LAST:event_cbTechniqueActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAddSpeaker;
    private javax.swing.JButton btAddUtterance;
    private javax.swing.JButton btCancel;
    private javax.swing.JButton btSave;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JComboBox cbDuracaoFrame;
    private javax.swing.JComboBox cbEnvironment;
    private javax.swing.JComboBox cbGender;
    private javax.swing.JComboBox cbJanelamento;
    private javax.swing.JComboBox cbSNR;
    private javax.swing.JComboBox cbSuperposicao;
    private javax.swing.JComboBox cbTechnique;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel jlAge;
    private javax.swing.JLabel jlDescription;
    private javax.swing.JLabel jlEnvironment;
    private javax.swing.JLabel jlGender;
    private javax.swing.JLabel jlSNR;
    private javax.swing.JLabel jlSpeaker;
    private javax.swing.JLabel jlTechnique;
    private javax.swing.JLabel jlUtterance;
    private javax.swing.JPanel pnTecnicas;
    private javax.swing.JRadioButton rbNormalizeNao;
    private javax.swing.JRadioButton rbNormalizeSim;
    private javax.swing.JRadioButton rbVADnao;
    private javax.swing.JRadioButton rbVADsim;
    private javax.swing.JTextArea taDescription;
    private javax.swing.JTextField tfMaxAge;
    private javax.swing.JTextField tfMinAge;
    private javax.swing.JTextField tfPreEnfase;
    private javax.swing.JTextField tfSpeakers;
    private javax.swing.JTextField tfUtterances;
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
