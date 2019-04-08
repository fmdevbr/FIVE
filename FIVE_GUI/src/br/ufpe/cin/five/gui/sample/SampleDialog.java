/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.gui.sample;

import br.ufpe.cin.five.core.sample.Environment;
import br.ufpe.cin.five.core.sample.Sample;
import br.ufpe.cin.five.core.speaker.Gender;
import br.ufpe.cin.five.core.speaker.Speaker;
import br.ufpe.cin.five.core.util.WaveReader;
import br.ufpe.cin.five.core.utterance.Utterance;
import br.ufpe.cin.five.exception.RegisterException;
import br.ufpe.cin.five.facade.Facade;
import br.ufpe.cin.five.register.ProjectRegister;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Line2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;
import org.apache.log4j.Logger;

/**
 *
 * @author Alexandre
 */
public class SampleDialog extends JDialog implements ActionListener {

    private static final Logger logger = Logger.getLogger(SampleDialog.class);   
    private Facade facade = Facade.getInstance();
    private int operation;        
    private Sample sample;
    
    String projectName = "";
    String selectedSpeaker = "";
    String selectedUtterance = "";
    String nextSampleId = "";
    String audioFileName = "untitled";
    
    final int bufSize = 16384;
    FormatControls formatControls;
    Capture capture = new Capture();
    Playback playback = new Playback();
    AudioInputStream audioInputStream;
    SamplingGraph samplingGraph;    
    Vector lines = new Vector();
    double duration, seconds;
    String errStr;
    File file;       
    
    /** Creates new form SpeakerDialog */
    public SampleDialog(java.awt.Frame parent, boolean modal, Sample sample, String selectedSentence, int operation) {
        super(parent, modal);

        logger.info("Inicializando Sample Dialog. Operation = " + operation);
        initComponents();
        
        this.operation = operation;

        formatControls = new FormatControls();
        taSelectedUtterance.setText(selectedSentence);

        ArrayList<Speaker> speakers = new ArrayList<Speaker>(facade.getProject().getSpeakers());
        speakers.add(0, new Speaker());
        cbSpeaker.setModel(new javax.swing.DefaultComboBoxModel(speakers.toArray()));

        ArrayList<Utterance> lstUtterances = new ArrayList<Utterance>(facade.getProject().getUtterances());
        lstUtterances.add(0, new Utterance());
        cbUtterance.setModel(new javax.swing.DefaultComboBoxModel(lstUtterances.toArray()));

        Environment[] environments = Environment.values();
        String[] environmentDescriptions = new String[environments.length + 1];
        environmentDescriptions[0] = "";
        for (int i = 1; i < environmentDescriptions.length; i++) {
            environmentDescriptions[i] = environments[i - 1].toString();
        }
        cbEnvironment.setModel(new javax.swing.DefaultComboBoxModel(environmentDescriptions));

        projectName = facade.getProject().getName();

        carregarPlayer();

        if (operation == 1) {
            
            this.sample = sample;
            
            try {
                audioFileName = facade.getProject().getDirectory() + File.separator + "samples" + File.separator + sample.getAudioFile();
                File f = new File(audioFileName);
                this.file = f;
                createAudioInputStream(f, true);
                StringBuilder sb = new StringBuilder();
                sb.append("Arquivo wave informado = ".concat(f.getName()).concat("\n"));
                sb.append("Environemnt            = ".concat(sample.getEnvironment().toString()).concat("\n"));
                sb.append("Speaker                = ".concat(sample.getSpeaker()+""));
                logger.info(sb.toString());
                cbUtterance.setSelectedItem(sample.getUtterance());
                cbEnvironment.setSelectedItem(sample.getEnvironment().toString());
                cbGenuine.setSelectedIndex((sample.isGenuine() == true) ? 0 : 1);
                Speaker speaker = sample.getSpeaker();
                cbSpeaker.setSelectedItem(speaker);
                tfIdade.setText(String.valueOf(speaker.getAge()));
                tfRegiao.setText(speaker.getRegion());

                WaveReader waveReader = new WaveReader(f);
                int sampleRate = (int) waveReader.getAudioFormat().getSampleRate();
                int sampleBits = waveReader.getAudioFormat().getSampleSizeInBits();
                int nChannels = waveReader.getAudioFormat().getChannels();
                if(sampleRate == 8000){
                    rb8hkz.setSelected(true);
                }else{
                    rb16khz.setSelected(true);                    
                }
                if(sampleBits == 16){
                    rb16bits.setSelected(true);
                }else{
                    rb32bits.setSelected(true);
                }
                if(nChannels == 1){
                    rbMono.setSelected(true);
                }else{
                    rbStereo.setSelected(true);
                }

                if (speaker.getGender() == Gender.MASCULINO) {
                    rbMasculino.setSelected(true);
                    rbFeminino.setSelected(false);
                } else {
                    rbMasculino.setSelected(false);
                    rbFeminino.setSelected(true);
                }
                
                audioFileName = sample.getAudioFile();
                tfFileName.setText(audioFileName);
                nextSampleId = audioFileName.substring(getNextIdPosition(audioFileName), audioFileName.length()-4);
                
            } catch (Exception ex) {
                logger.error(ex);
                JOptionPane.showMessageDialog(null, ex);
            }
        } else {
            this.sample = new Sample();
            nextSampleId = ProjectRegister.getNextSampleId(facade.getProject()) + "";
            
            mountFileName(projectName, selectedSpeaker, selectedUtterance, nextSampleId);            
        }       
    }

    public void open() {
    }

    public void close() {
        if (playback.thread != null) {
            btPlay.doClick(0);
        }
        if (capture.thread != null) {
            btRecord.doClick(0);
        }
    }

    private void createAudioInputStream(File file, boolean updateComponents) {
        logger.info("Criando Input Stream de Audio.");
        if (file != null && file.isFile()) {
            try {
                this.file = file;
                errStr = null;
                audioInputStream = AudioSystem.getAudioInputStream(file);
                btPlay.setEnabled(true);
                audioFileName = file.getName();
                long milliseconds = (long) ((audioInputStream.getFrameLength() * 1000) / audioInputStream.getFormat().getFrameRate());
                duration = milliseconds / 1000.0;
                btSave.setEnabled(true);
                if (updateComponents) {
                    formatControls.setFormat(audioInputStream.getFormat());
                    samplingGraph.createWaveForm(null);
                }
            } catch (Exception ex) {
                logger.error(ex);
                reportStatus(ex.toString());
            }
        } else {
            logger.warn("Arquivo de audio precisa ser informado.");
            reportStatus("Audio file required.");
        }
    }

    private void saveToFile(String name, AudioFileFormat.Type fileType) {
        logger.info("Salvando arquivo de audio.");
        if (audioInputStream == null) {
            logger.warn("O arquivo de audio a ser salvo nao foi carregado.");
            reportStatus("No loaded audio to save");
            return;
        } else if (file != null) {
            createAudioInputStream(new File(name), false);
        }

        // reset to the beginnning of the captured data
        try {
            logger.info("Reiniciando para o inicio dos dados capturados");
            audioInputStream.reset();
        } catch (Exception e) {
            logger.error(e);
            reportStatus("Unable to reset stream " + e);
            return;
        }

        file = new File(audioFileName = name);
        try {
            if (AudioSystem.write(audioInputStream, fileType, file) == -1) {
                IOException ex = new IOException();
                logger.error(ex);
                throw ex;
            }
        } catch (Exception ex) {
            logger.error(ex);
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        samplingGraph.repaint();
    }

    private void reportStatus(String msg) {
        if ((errStr = msg) != null) {
            System.out.println(errStr);
            samplingGraph.repaint();
        }
    }

    private void carregarPlayer() {
        logger.info("Carregando player.");
        EmptyBorder eb = new EmptyBorder(3, 3, 3, 35);
        SoftBevelBorder sbb = new SoftBevelBorder(SoftBevelBorder.LOWERED);
        jPanel1.setBorder(new EmptyBorder(3, 3, 3, 3));
        jPanel4.setBorder(sbb);
        jPanel4.setLayout(new BoxLayout(jPanel4, BoxLayout.Y_AXIS));
        JPanel samplingPanel = new JPanel(new BorderLayout());
        eb = new EmptyBorder(5, 5, 5, 5);
        samplingPanel.setPreferredSize(new Dimension(200, 200));
        samplingPanel.setBorder(new CompoundBorder(eb, sbb));
        samplingPanel.add(samplingGraph = new SamplingGraph());
        jPanel4.add(samplingPanel);
    }  

    private void mountFileName(String project, String speaker, String utterance, String nextSampleId) {
        this.projectName = project;
        this.selectedSpeaker = speaker;
        this.selectedUtterance = utterance;
        this.nextSampleId = nextSampleId;
        String fileName = this.projectName + "_" + this.selectedSpeaker + "_" + this.selectedUtterance + "_" + this.nextSampleId;
        tfFileName.setText(fileName);
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
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        rb16bits = new javax.swing.JRadioButton();
        rb8hkz = new javax.swing.JRadioButton();
        rbMono = new javax.swing.JRadioButton();
        rbStereo = new javax.swing.JRadioButton();
        rb16khz = new javax.swing.JRadioButton();
        rb32bits = new javax.swing.JRadioButton();
        jPanel11 = new javax.swing.JPanel();
        btPlay = new javax.swing.JButton();
        btRecord = new javax.swing.JButton();
        btPause = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        lblIdade = new javax.swing.JLabel();
        tfIdade = new javax.swing.JTextField();
        tfRegiao = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        rbFeminino = new javax.swing.JRadioButton();
        rbMasculino = new javax.swing.JRadioButton();
        jLabel7 = new javax.swing.JLabel();
        cbSpeaker = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        taSelectedUtterance = new javax.swing.JTextArea();
        cbUtterance = new javax.swing.JComboBox();
        cbEnvironment = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        cbGenuine = new javax.swing.JComboBox();
        jPanel8 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        tfFileName = new javax.swing.JTextField();
        btSave = new javax.swing.JButton();
        btCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Amostra");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setMaximumSize(new java.awt.Dimension(586, 264));

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel4.setPreferredSize(new java.awt.Dimension(300, 200));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 394, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 209, Short.MAX_VALUE)
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N
        jLabel1.setText("Características da amostra");

        buttonGroup2.add(rb16bits);
        rb16bits.setText("16 bits");

        buttonGroup3.add(rb8hkz);
        rb8hkz.setText("8 KHz");

        buttonGroup4.add(rbMono);
        rbMono.setText("mono");

        buttonGroup4.add(rbStereo);
        rbStereo.setText("stereo");

        buttonGroup3.add(rb16khz);
        rb16khz.setText("16 KHz");

        buttonGroup2.add(rb32bits);
        rb32bits.setText("32 bits");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rb8hkz)
                            .addComponent(rbMono)
                            .addComponent(rb16bits))
                        .addGap(24, 24, 24)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rb32bits)
                            .addComponent(rb16khz)
                            .addComponent(rbStereo)))
                    .addComponent(jLabel1))
                .addContainerGap(67, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rb16bits)
                    .addComponent(rb32bits))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rb8hkz)
                    .addComponent(rb16khz))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbMono)
                    .addComponent(rbStereo))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jPanel11.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btPlay.setText("Play");
        btPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPlayActionPerformed(evt);
            }
        });

        btRecord.setText("Record");
        btRecord.setMaximumSize(new java.awt.Dimension(75, 25));
        btRecord.setMinimumSize(new java.awt.Dimension(75, 25));
        btRecord.setPreferredSize(new java.awt.Dimension(75, 25));
        btRecord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRecordActionPerformed(evt);
            }
        });

        btPause.setText("Pause");
        btPause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPauseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btPlay)
                .addGap(18, 18, 18)
                .addComponent(btPause)
                .addGap(18, 18, 18)
                .addComponent(btRecord, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(btPlay)
                .addComponent(btPause)
                .addComponent(btRecord, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 394, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N
        jLabel2.setText("Dados do Locutor");

        lblIdade.setText("Idade:");

        tfIdade.setEditable(false);
        tfIdade.setToolTipText("");

        tfRegiao.setEditable(false);
        tfRegiao.setToolTipText("");

        jLabel5.setText("Região:");

        buttonGroup1.add(rbFeminino);
        rbFeminino.setText("Feminino");
        rbFeminino.setEnabled(false);

        buttonGroup1.add(rbMasculino);
        rbMasculino.setText("Masculino");
        rbMasculino.setEnabled(false);

        jLabel7.setText("Locutor:");

        cbSpeaker.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbSpeakerActionPerformed(evt);
            }
        });

        jLabel9.setText("Sexo:");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(cbSpeaker, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblIdade)
                            .addComponent(tfIdade, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(tfRegiao, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(24, 24, 24)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(rbMasculino)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(rbFeminino))
                            .addComponent(jLabel9)))
                    .addComponent(jLabel2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jLabel9))
                        .addGap(25, 25, 25))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(lblIdade)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tfIdade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tfRegiao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rbMasculino)
                            .addComponent(rbFeminino)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbSpeaker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(55, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel12.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel6.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N
        jLabel6.setText("Dados da Locução");

        taSelectedUtterance.setColumns(20);
        taSelectedUtterance.setLineWrap(true);
        taSelectedUtterance.setRows(5);
        taSelectedUtterance.setWrapStyleWord(true);
        taSelectedUtterance.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        taSelectedUtterance.setEnabled(false);
        jScrollPane2.setViewportView(taSelectedUtterance);

        cbUtterance.setAutoscrolls(true);
        cbUtterance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbUtteranceActionPerformed(evt);
            }
        });

        jLabel3.setText("Ambiente:");

        jLabel8.setText("Locução:");

        jLabel10.setText("Genuína:");

        cbGenuine.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Sim", "Não" }));

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel8)
                    .addComponent(cbUtterance, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(cbEnvironment, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 100, Short.MAX_VALUE)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cbGenuine, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel10))
                        .addGap(73, 73, 73)))
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 384, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUtterance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbEnvironment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbGenuine, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel4.setFont(new java.awt.Font("DejaVu Sans", 1, 12)); // NOI18N
        jLabel4.setText("Nome do Arquivo:");

        tfFileName.setToolTipText("");

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

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfFileName, javax.swing.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btSave)
                .addGap(4, 4, 4)
                .addComponent(btCancel)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel4)
                .addComponent(tfFileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btSave)
                .addComponent(btCancel))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPlayActionPerformed
        logger.info("BotÃ£o btPlay pressionado.");
        if (btPlay.getText().startsWith("Play")) {
            logger.info("Playing: " + this.audioFileName.toString());
            playback.start();
            samplingGraph.start();
            btRecord.setEnabled(false);
            btPause.setEnabled(true);
            btPlay.setText("Stop");
        } else {
            logger.info("Stoping: " + this.audioFileName.toString());
            playback.stop();
            samplingGraph.stop();
            btRecord.setEnabled(true);
            btPause.setEnabled(false);
            btPlay.setText("Play");
        }
}//GEN-LAST:event_btPlayActionPerformed

    private void btRecordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btRecordActionPerformed
        logger.info("BotÃ£o btRecord pressionado.");
        if ((rb16bits.isSelected() || rb32bits.isSelected())
                && (rb8hkz.isSelected() || rb16khz.isSelected())
                && (rbMono.isSelected() || rbStereo.isSelected())) {
            if (btRecord.getText().startsWith("Record")) {
                file = null;
                capture.start();
                audioFileName = "untitled";
                samplingGraph.start();
                btPlay.setEnabled(false);
                btPause.setEnabled(true);
                btSave.setEnabled(false);
                btRecord.setText("Stop");
            } else {
                lines.removeAllElements();
                capture.stop();
                samplingGraph.stop();
                btPlay.setEnabled(true);
                btPause.setEnabled(false);
                btSave.setEnabled(true);
                btRecord.setText("Record");
            }
        } else {
            logger.warn("Informe as caracterÃ­sticas da amostra!");
            JOptionPane.showMessageDialog(null, "Informe as caracterÃ­sticas da amostra!", "Base de Dados", JOptionPane.INFORMATION_MESSAGE);
        }
}//GEN-LAST:event_btRecordActionPerformed

    private void btPauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPauseActionPerformed

        logger.info("BotÃ£o btPause pressionado.");

        if (btPause.getText().startsWith("Pause")) {
            if (capture.thread != null) {
                capture.line.stop();
            } else {
                if (playback.thread != null) {
                    playback.line.stop();
                }
            }
            btPause.setText("Resume");
        } else {
            if (capture.thread != null) {
                capture.line.start();
            } else {
                if (playback.thread != null) {
                    playback.line.start();
                }
            }
            btPause.setText("Pause");
        }
}//GEN-LAST:event_btPauseActionPerformed

    private void btSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSaveActionPerformed
        logger.info("BotÃ£o btSave pressionado. Operation = " + operation);
        if (validateFields()) {
            logger.info("Campos validados.");
            try {
                audioFileName = tfFileName.getText();
                if (!audioFileName.substring(audioFileName.length() - 3, audioFileName.length()).equals("wav")) {
                    audioFileName = audioFileName + ".wav";
                }
                Speaker speaker = (Speaker) cbSpeaker.getSelectedItem();
                if (operation == 0) {
                    sample = new Sample();
                }
                sample.setAudioFile(audioFileName);
                
                String extractionFile = audioFileName.substring(0, sample.getAudioFile().length() - 3) + "mfc";
                sample.setFeatureFile(extractionFile);
                
                sample.setSpeaker(speaker);

                sample.setEnvironment(Environment.valueOf(cbEnvironment.getSelectedItem().toString()));
                sample.setGenuine((cbGenuine.getSelectedIndex() == 0) ? true : false);
                
                List<Utterance> utterances = facade.getProject().getUtterances();
                for (int i = 0; i < utterances.size(); i++) {
                    if (utterances.get(i).getDescription().equalsIgnoreCase(taSelectedUtterance.getText())) {
                        sample.setUtterance(utterances.get(i));
                        if (operation == 0) {
                            facade.insertSample(sample);
                            
                            logger.info("Amostra inserida com sucesso.");
                            JOptionPane.showMessageDialog(null, "Amostra inserida com sucesso.");
                                                        
                            nextSampleId = ProjectRegister.getNextSampleId(facade.getProject()) + "";
                            mountFileName(projectName, selectedSpeaker, selectedUtterance, nextSampleId);                                                        
                        } else {
                            
                            facade.updateSample(sample);
                            
                            logger.info("Amostra atualizada com sucesso.");
                            JOptionPane.showMessageDialog(null, "Amostra atualizada com sucesso.");
                            
                            this.setVisible(false);
                        }
                        break;
                    }
                }
                logger.info("Salvando arquivo " + audioFileName);
                audioFileName = facade.getProject().getDirectory() + File.separator + "samples" + File.separator + audioFileName;
                saveToFile(audioFileName, AudioFileFormat.Type.WAVE);
                samplingGraph.repaint();
                logger.info("Arquivo " + audioFileName + " salvo com sucesso.");
            } catch (RegisterException ex) {
                logger.error(ex.getMessage());
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        } else {
            logger.warn("Informe os parÃ¢metros necessÃ¡rios");
            JOptionPane.showMessageDialog(null, "Informe os parÃ¢metros necessÃ¡rios");
        }        
}//GEN-LAST:event_btSaveActionPerformed

    private boolean validateFields() {
        if (cbSpeaker.getSelectedItem() == null
                || cbUtterance.getSelectedItem() == null
                || cbEnvironment.getSelectedItem() == null
                || cbGenuine.getSelectedItem() == null
                || tfFileName.getText().equals("")) {
            return false;
        } else {
            return true;
        }
    }

    private void btCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCancelActionPerformed
        logger.info("BotÃ£o btCancelar pressionado.");
        this.setVisible(false);        
}//GEN-LAST:event_btCancelActionPerformed

    private void cbSpeakerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSpeakerActionPerformed
        List<Speaker> speakers = facade.getProject().getSpeakers();
        int speakerId=0;
        for (Speaker speaker : speakers) {
            if (speaker == (Speaker)cbSpeaker.getSelectedItem()) {
                speakerId = speaker.getId();
                tfIdade.setText(String.valueOf(speaker.getAge()));
                tfRegiao.setText(speaker.getRegion());
                if (speaker.getGender() == Gender.MASCULINO) {
                    rbMasculino.setSelected(true);
                    rbFeminino.setSelected(false);
                } else {
                    rbMasculino.setSelected(false);
                    rbFeminino.setSelected(true);
                }
            }
        }
        nextSampleId = ProjectRegister.getNextSampleId(facade.getProject()) + "";
        mountFileName(projectName, speakerId+"", selectedUtterance, nextSampleId);
    }//GEN-LAST:event_cbSpeakerActionPerformed

    private void cbUtteranceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbUtteranceActionPerformed
        taSelectedUtterance.setText(cbUtterance.getSelectedItem().toString());
        nextSampleId = ProjectRegister.getNextSampleId(facade.getProject()) + "";
        mountFileName(projectName, selectedSpeaker, String.valueOf(((Utterance) cbUtterance.getSelectedItem()).getId()), nextSampleId);
    }//GEN-LAST:event_cbUtteranceActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btCancel;
    private javax.swing.JButton btPause;
    private javax.swing.JButton btPlay;
    private javax.swing.JButton btRecord;
    private javax.swing.JButton btSave;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.JComboBox cbEnvironment;
    private javax.swing.JComboBox cbGenuine;
    private javax.swing.JComboBox cbSpeaker;
    private javax.swing.JComboBox cbUtterance;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblIdade;
    private javax.swing.JRadioButton rb16bits;
    private javax.swing.JRadioButton rb16khz;
    private javax.swing.JRadioButton rb32bits;
    private javax.swing.JRadioButton rb8hkz;
    private javax.swing.JRadioButton rbFeminino;
    private javax.swing.JRadioButton rbMasculino;
    private javax.swing.JRadioButton rbMono;
    private javax.swing.JRadioButton rbStereo;
    private javax.swing.JTextArea taSelectedUtterance;
    private javax.swing.JTextField tfFileName;
    private javax.swing.JTextField tfIdade;
    private javax.swing.JTextField tfRegiao;
    // End of variables declaration//GEN-END:variables

    /**
     * Write data to the OutputChannel.
     */
    public class Playback implements Runnable {

        SourceDataLine line;
        Thread thread;

        public void start() {
            errStr = null;
            thread = new Thread(this);
            thread.setName("Playback");
            thread.start();
        }

        public void stop() {
            thread = null;
        }

        private void shutDown(String message) {
            if ((errStr = message) != null) {
                System.err.println(errStr);
                samplingGraph.repaint();
            }
            if (thread != null) {
                thread = null;
                samplingGraph.stop();
                btRecord.setEnabled(true);
                btPause.setEnabled(false);
                btPlay.setText("Play");
            }
        }

        @Override
        public void run() {

            // reload the file if loaded by file
            if (file != null) {
                createAudioInputStream(file, false);
            }

            // make sure we have something to play
            if (audioInputStream == null) {
                shutDown("No loaded audio to play back");
                return;
            }
            // reset to the beginnning of the stream
            try {
                //audioInputStream.reset();
            } catch (Exception e) {
                shutDown("Unable to reset the stream\n" + e);
                return;
            }

            // get an AudioInputStream of the desired format for playback
            AudioFormat format = formatControls.getFormat();
            AudioInputStream playbackInputStream = AudioSystem.getAudioInputStream(format, audioInputStream);

            if (playbackInputStream == null) {
                shutDown("Unable to convert stream of format " + audioInputStream + " to format " + format);
                return;
            }

            // define the required attributes for our line,
            // and make sure a compatible line is supported.

            DataLine.Info info = new DataLine.Info(SourceDataLine.class,
                    format);
            if (!AudioSystem.isLineSupported(info)) {
                shutDown("Line matching " + info + " not supported.");
                return;
            }

            // get and open the source data line for playback.

            try {
                line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(format, bufSize);
            } catch (LineUnavailableException ex) {
                shutDown("Unable to open the line: " + ex);
                return;
            }

            // play back the captured audio data

            int frameSizeInBytes = format.getFrameSize();
            int bufferLengthInFrames = line.getBufferSize() / 8;
            int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
            byte[] data = new byte[bufferLengthInBytes];
            int numBytesRead = 0;

            // start the source data line
            line.start();

            while (thread != null) {
                try {
                    if ((numBytesRead = playbackInputStream.read(data)) == -1) {
                        break;
                    }
                    int numBytesRemaining = numBytesRead;
                    while (numBytesRemaining > 0) {
                        numBytesRemaining -= line.write(data, 0, numBytesRemaining);
                    }
                } catch (Exception e) {
                    shutDown("Error during playback: " + e);
                    break;
                }
            }
            // we reached the end of the stream.  let the data play out, then
            // stop and close the line.
            if (thread != null) {
                line.drain();
            }
            line.stop();
            line.close();
            line = null;
            shutDown(null);
        }
    } // End class Playback

    /**
     * Reads data from the input channel and writes to the output stream
     */
    class Capture implements Runnable {

        TargetDataLine line;
        Thread thread;

        public void start() {
            errStr = null;
            thread = new Thread(this);
            thread.setName("Capture");
            thread.start();
        }

        public void stop() {
            thread = null;
        }

        private void shutDown(String message) {
            if ((errStr = message) != null && thread != null) {
                thread = null;
                samplingGraph.stop();
                /*                btPlay.setEnabled(true);
                btPause.setEnabled(false);
                btSalvar.setEnabled(true);
                btRecord.setText("Record");*/
                System.err.println(errStr);
                samplingGraph.repaint();
            }
        }

        public void run() {

            duration = 0;
            audioInputStream = null;
      
            int nChannels;
            int sampleRate;
            int sampleBits;
            
            if (rb16bits.isSelected()) {
                sampleBits = 16;
            } else {
                sampleBits = 32;
            }
            if (rb16khz.isSelected()) {
                sampleRate = 16000;
            } else {
                sampleRate = 8000;
            }
            if (rbMono.isSelected()) {
                nChannels = 1;
            } else {
                nChannels = 2;
            }
            AudioFormat format = new AudioFormat(sampleRate, sampleBits, nChannels, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class,format);

            if (!AudioSystem.isLineSupported(info)) {
                shutDown("Line matching " + info + " not supported.");
                return;
            }

            // get and open the target data line for capture.

            try {
                line = (TargetDataLine) AudioSystem.getLine(info);
                line.open(format, line.getBufferSize());
            } catch (LineUnavailableException ex) {
                shutDown("Unable to open the line: " + ex);
                return;
            } catch (SecurityException ex) {
                shutDown(ex.toString());
                //JavaSound.showInfoDialog();
                return;
            } catch (Exception ex) {
                shutDown(ex.toString());
                return;
            }

            // play back the captured audio data
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int frameSizeInBytes = format.getFrameSize();
            int bufferLengthInFrames = line.getBufferSize() / 8;
            int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
            byte[] data = new byte[bufferLengthInBytes];
            int numBytesRead;

            line.start();

            while (thread != null) {
                if ((numBytesRead = line.read(data, 0, bufferLengthInBytes)) == -1) {
                    break;
                }
                out.write(data, 0, numBytesRead);
            }

            // we reached the end of the stream.  stop and close the line.
            line.stop();
            line.close();
            line = null;

            // stop and close the output stream
            try {
                out.flush();
                out.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // load bytes into the audio input stream for playback

            byte audioBytes[] = out.toByteArray();
            ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
            audioInputStream = new AudioInputStream(bais, format, audioBytes.length / frameSizeInBytes);

            long milliseconds = (long) ((audioInputStream.getFrameLength() * 1000) / format.getFrameRate());
            duration = milliseconds / 1000.0;

            try {
                audioInputStream.reset();
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }

            samplingGraph.createWaveForm(audioBytes);
        }
    } // End class Capture

    /**
     * Controls for the AudioFormat.
     */
    class FormatControls extends JPanel {

        Vector groups = new Vector();
        JToggleButton linrB, ulawB, alawB, rate8B, rate11B, rate16B, rate22B, rate44B;
        JToggleButton size8B, size16B, signB, unsignB, litB, bigB, monoB, sterB;

        public FormatControls() {
            setLayout(new GridLayout(0, 1));
            EmptyBorder eb = new EmptyBorder(0, 0, 0, 5);
            BevelBorder bb = new BevelBorder(BevelBorder.LOWERED);
            CompoundBorder cb = new CompoundBorder(eb, bb);
            setBorder(new CompoundBorder(cb, new EmptyBorder(8, 5, 5, 5)));
            JPanel p1 = new JPanel();
            ButtonGroup encodingGroup = new ButtonGroup();
            linrB = addToggleButton(p1, encodingGroup, "linear", true);
            ulawB = addToggleButton(p1, encodingGroup, "ulaw", false);
            alawB = addToggleButton(p1, encodingGroup, "alaw", false);
            add(p1);
            groups.addElement(encodingGroup);

            JPanel p2 = new JPanel();
            JPanel p2b = new JPanel();
            ButtonGroup sampleRateGroup = new ButtonGroup();
            rate8B = addToggleButton(p2, sampleRateGroup, "8000", true);
            rate11B = addToggleButton(p2, sampleRateGroup, "11025", false);
            rate16B = addToggleButton(p2b, sampleRateGroup, "16000", false);
            rate22B = addToggleButton(p2b, sampleRateGroup, "22050", false);
            rate44B = addToggleButton(p2b, sampleRateGroup, "44100", false);
            add(p2);
            add(p2b);
            groups.addElement(sampleRateGroup);

            JPanel p3 = new JPanel();
            ButtonGroup sampleSizeInBitsGroup = new ButtonGroup();
            size8B = addToggleButton(p3, sampleSizeInBitsGroup, "8", false);
            size16B = addToggleButton(p3, sampleSizeInBitsGroup, "16", true);
            add(p3);
            groups.addElement(sampleSizeInBitsGroup);

            JPanel p4 = new JPanel();
            ButtonGroup signGroup = new ButtonGroup();
            signB = addToggleButton(p4, signGroup, "signed", true);
            unsignB = addToggleButton(p4, signGroup, "unsigned", false);
            add(p4);
            groups.addElement(signGroup);

            JPanel p5 = new JPanel();
            ButtonGroup endianGroup = new ButtonGroup();
            litB = addToggleButton(p5, endianGroup, "little endian", false);
            bigB = addToggleButton(p5, endianGroup, "big endian", true);
            add(p5);
            groups.addElement(endianGroup);

            JPanel p6 = new JPanel();
            ButtonGroup channelsGroup = new ButtonGroup();
            monoB = addToggleButton(p6, channelsGroup, "mono", true);
            sterB = addToggleButton(p6, channelsGroup, "stereo", false);
            add(p6);
            groups.addElement(channelsGroup);
        }

        private JToggleButton addToggleButton(JPanel p, ButtonGroup g, String name, boolean state) {
            JToggleButton b = new JToggleButton(name, state);
            p.add(b);
            g.add(b);
            return b;
        }

        public AudioFormat getFormat() {

            Vector v = new Vector(groups.size());
            for (int i = 0; i < groups.size(); i++) {
                ButtonGroup g = (ButtonGroup) groups.get(i);
                for (Enumeration e = g.getElements(); e.hasMoreElements();) {
                    AbstractButton b = (AbstractButton) e.nextElement();
                    if (b.isSelected()) {
                        v.add(b.getText());
                        break;
                    }
                }
            }

            AudioFormat.Encoding encoding = AudioFormat.Encoding.ULAW;
            String encString = (String) v.get(0);
            float rate = Float.valueOf((String) v.get(1)).floatValue();
            int sampleSize = Integer.valueOf((String) v.get(2)).intValue();
            String signedString = (String) v.get(3);
            boolean bigEndian = ((String) v.get(4)).startsWith("big");
            int channels = ((String) v.get(5)).equals("mono") ? 1 : 2;

            if (encString.equals("linear")) {
                if (signedString.equals("signed")) {
                    encoding = AudioFormat.Encoding.PCM_SIGNED;
                } else {
                    encoding = AudioFormat.Encoding.PCM_UNSIGNED;
                }
            } else if (encString.equals("alaw")) {
                encoding = AudioFormat.Encoding.ALAW;
            }
            return new AudioFormat(encoding, rate, sampleSize,
                    channels, (sampleSize / 8) * channels, rate, bigEndian);
        }

        public void setFormat(AudioFormat format) {
            AudioFormat.Encoding type = format.getEncoding();
            if (type == AudioFormat.Encoding.ULAW) {
                ulawB.doClick();
            } else if (type == AudioFormat.Encoding.ALAW) {
                alawB.doClick();
            } else if (type == AudioFormat.Encoding.PCM_SIGNED) {
                linrB.doClick();
                signB.doClick();
            } else if (type == AudioFormat.Encoding.PCM_UNSIGNED) {
                linrB.doClick();
                unsignB.doClick();
            }
            float rate = format.getFrameRate();
            if (rate == 8000) {
                rate8B.doClick();
            } else if (rate == 11025) {
                rate11B.doClick();
            } else if (rate == 16000) {
                rate16B.doClick();
            } else if (rate == 22050) {
                rate22B.doClick();
            } else if (rate == 44100) {
                rate44B.doClick();
            }
            switch (format.getSampleSizeInBits()) {
                case 8:
                    size8B.doClick();
                    break;
                case 16:
                    size16B.doClick();
                    break;
            }
            if (format.isBigEndian()) {
                bigB.doClick();
            } else {
                litB.doClick();
            }
            if (format.getChannels() == 1) {
                monoB.doClick();
            } else {
                sterB.doClick();
            }
        }
    } // End class FormatControls

    /**
     * Render a WaveForm.
     */
    class SamplingGraph extends JPanel implements Runnable {

        private Thread thread;
        private Font font10 = new Font("serif", Font.PLAIN, 10);
        private Font font12 = new Font("serif", Font.PLAIN, 12);
        Color jfcBlue = new Color(204, 204, 255);
        Color pink = new Color(255, 175, 175);

        public SamplingGraph() {
            setBackground(new Color(20, 20, 20));
        }

        public void createWaveForm(byte[] audioBytes) {

            lines.removeAllElements();  // clear the old vector

            AudioFormat format = audioInputStream.getFormat();
            if (audioBytes == null) {
                try {
                    audioBytes = new byte[(int) (audioInputStream.getFrameLength() * format.getFrameSize())];
                    audioInputStream.read(audioBytes);
                } catch (Exception ex) {
                    reportStatus(ex.toString());
                    return;
                }
            }

            Dimension d = new Dimension(372, 186);//getSize();
            int w = d.width;
            int h = d.height - 15;
            int[] audioData = null;
            if (format.getSampleSizeInBits() == 16) {
                int nlengthInSamples = audioBytes.length / 2;
                audioData = new int[nlengthInSamples];
                if (format.isBigEndian()) {
                    for (int i = 0; i < nlengthInSamples; i++) {
                        /* First byte is MSB (high order) */
                        int MSB = (int) audioBytes[2 * i];
                        /* Second byte is LSB (low order) */
                        int LSB = (int) audioBytes[2 * i + 1];
                        audioData[i] = MSB << 8 | (255 & LSB);
                    }
                } else {
                    for (int i = 0; i < nlengthInSamples; i++) {
                        /* First byte is LSB (low order) */
                        int LSB = (int) audioBytes[2 * i];
                        /* Second byte is MSB (high order) */
                        int MSB = (int) audioBytes[2 * i + 1];
                        audioData[i] = MSB << 8 | (255 & LSB);
                    }
                }
            } else if (format.getSampleSizeInBits() == 8) {
                int nlengthInSamples = audioBytes.length;
                audioData = new int[nlengthInSamples];
                if (format.getEncoding().toString().startsWith("PCM_SIGN")) {
                    for (int i = 0; i < audioBytes.length; i++) {
                        audioData[i] = audioBytes[i];
                    }
                } else {
                    for (int i = 0; i < audioBytes.length; i++) {
                        audioData[i] = audioBytes[i] - 128;
                    }
                }
            }

            int frames_per_pixel = audioBytes.length / format.getFrameSize() / w;
            byte my_byte = 0;
            double y_last = 0;
            int numChannels = format.getChannels();
            for (double x = 0; x < w && audioData != null; x++) {
                int idx = (int) (frames_per_pixel * numChannels * x);
                if (format.getSampleSizeInBits() == 8) {
                    my_byte = (byte) audioData[idx];
                } else {
                    my_byte = (byte) (128 * audioData[idx] / 32768);
                }
                double y_new = (double) (h * (128 - my_byte) / 256);
                lines.add(new Line2D.Double(x, y_last, x, y_new));
                y_last = y_new;
            }

            repaint();
        }

        @Override
        public void paint(Graphics g) {

            Dimension d = new Dimension(372, 186);//getSize();
            int w = d.width;
            int h = d.height;
            int INFOPAD = 15;

            Graphics2D g2 = (Graphics2D) g;
            g2.setBackground(getBackground());
            g2.clearRect(0, 0, w, h);
            g2.setColor(Color.white);
            g2.fillRect(0, h - INFOPAD, w, INFOPAD);

            if (errStr != null) {
                g2.setColor(jfcBlue);
                g2.setFont(new Font("serif", Font.BOLD, 18));
                g2.drawString("ERROR", 5, 20);
                AttributedString as = new AttributedString(errStr);
                as.addAttribute(TextAttribute.FONT, font12, 0, errStr.length());
                AttributedCharacterIterator aci = as.getIterator();
                FontRenderContext frc = g2.getFontRenderContext();
                LineBreakMeasurer lbm = new LineBreakMeasurer(aci, frc);
                float x = 5, y = 25;
                lbm.setPosition(0);
                while (lbm.getPosition() < errStr.length()) {
                    TextLayout tl = lbm.nextLayout(w - x - 5);
                    if (!tl.isLeftToRight()) {
                        x = w - tl.getAdvance();
                    }
                    tl.draw(g2, x, y += tl.getAscent());
                    y += tl.getDescent() + tl.getLeading();
                }
            } else if (capture.thread != null) {
                g2.setColor(Color.black);
                g2.setFont(font12);
                g2.drawString("Length: " + String.valueOf(seconds), 3, h - 4);
            } else {
                g2.setColor(Color.black);
                g2.setFont(font12);
                g2.drawString("File: " + audioFileName + "  Length: " + String.valueOf(duration) + "  Position: " + String.valueOf(seconds), 3, h - 4);

                if (audioInputStream != null) {
                    // .. render sampling graph ..
                    g2.setColor(jfcBlue);
                    for (int i = 1; i < lines.size(); i++) {
                        g2.draw((Line2D) lines.get(i));
                    }

                    // .. draw current position ..
                    if (seconds != 0) {
                        double loc = seconds / duration * w;
                        g2.setColor(pink);
                        g2.setStroke(new BasicStroke(3));
                        g2.draw(new Line2D.Double(loc, 0, loc, h - INFOPAD - 2));
                    }
                }
            }
        }

        public void start() {
            thread = new Thread(this);
            thread.setName("SamplingGraph");
            thread.start();
            seconds = 0;
        }

        public void stop() {
            if (thread != null) {
                thread.interrupt();
            }
            thread = null;
        }

        @Override
        public void run() {
            seconds = 0;
            while (thread != null) {
                if ((playback.line != null) && (playback.line.isOpen())) {

                    long milliseconds = (long) (playback.line.getMicrosecondPosition() / 1000);
                    seconds = milliseconds / 1000.0;
                } else if ((capture.line != null) && (capture.line.isActive())) {

                    long milliseconds = (long) (capture.line.getMicrosecondPosition() / 1000);
                    seconds = milliseconds / 1000.0;
                }

                try {
                    thread.sleep(100);
                } catch (Exception e) {
                    break;
                }

                repaint();

                while ((capture.line != null && !capture.line.isActive())
                        || (playback.line != null && !playback.line.isOpen())) {
                    try {
                        thread.sleep(10);
                    } catch (Exception e) {
                        break;
                    }
                }
            }
            seconds = 0;
            repaint();
        }
    } // End class SamplingGraph

    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private int getNextIdPosition(String waveFile){
        int numberOfSubscribes = 0;
        for (int i = 0; i < waveFile.length(); i++) {
            if(waveFile.trim().charAt(i) == '_') {
                numberOfSubscribes++;
            }
            if(numberOfSubscribes == 3){
                return i+1;
            }
        }
        return 0;
    }
}
