/**
 * Copyright 2011 Federal University of Pernambuco. All Rights Reserved. Use is
 * subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.gui;

import br.ufpe.cin.five.core.project.ProjectType;
import br.ufpe.cin.five.core.sample.Sample;
import br.ufpe.cin.five.exception.RegisterException;
import br.ufpe.cin.five.facade.Facade;
import br.ufpe.cin.five.gui.classification.ClassificationPanel;
import br.ufpe.cin.five.gui.dialogs.PreviewDialog;
import br.ufpe.cin.five.gui.extraction.ExtractionPanel;
import br.ufpe.cin.five.gui.project.NewProjetctDialog;
import br.ufpe.cin.five.gui.sample.SampleDialog;
import br.ufpe.cin.five.gui.sample.SamplePanel;
import br.ufpe.cin.five.gui.speaker.SpeakerPanel;
import br.ufpe.cin.five.gui.test.SpeakerTestPanel;
import br.ufpe.cin.five.gui.test.SpeechTestPanel;
import br.ufpe.cin.five.gui.test.SynthesisTestPanel;
import br.ufpe.cin.five.gui.utterance.UtterancePanel;
import br.ufpe.cin.five.gui.dictionary.DictionaryPanel;
import br.ufpe.cin.five.gui.engine.EnginePanel;
import br.ufpe.cin.five.gui.wordExceptions.WordExceptionPanel;
import br.ufpe.cin.five.util.FileTreeCellRenderer;
import br.ufpe.cin.five.util.FileTreeNode;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.tree.TreePath;
import org.apache.log4j.Logger;

/**
 *
 * @author Alexandre
 */
public class TelaPrincipal extends javax.swing.JFrame implements WindowListener {
//Jframe.implements WindowListener 

    private static final Logger logger = Logger.getLogger(TelaPrincipal.class);
    private Facade facade = Facade.getInstance();
    private JPanel panel;
    private JPanel panelTree;
    private JPanel panelIcons;
    private JTree jTree1;
    private Container area;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuAbrir;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuFechar;
    private javax.swing.JMenuItem jMenuNovo;
    private javax.swing.JMenuItem jMenuSair;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuItem jMenuAbrir2;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JMenuItem jMenuFechar2;
    private javax.swing.JMenuItem jMenuNovo2;
    private javax.swing.JMenuItem jMenuNovo3;
    private javax.swing.JMenuItem jMenuSair2;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JButton btNew;
    private javax.swing.JButton btOpen;
    private javax.swing.JButton btClose;
    private javax.swing.JTabbedPane jTabbedPane1;
    private SpeakerPanel speakerPannel;
    private UtterancePanel utterancePanel;
    private DictionaryPanel wordPanel;
    private SamplePanel dataBasePanel;
    private ExtractionPanel featurePanel;
    private ClassificationPanel classificationPanel;
    private EnginePanel enginePanel;
    private SpeechTestPanel speechTestPanel;
    private SpeakerTestPanel speakerTestPanel;
    private SynthesisTestPanel synthesisTestPanel;
    private WordExceptionPanel wordExceptionPanel;

    /**
     * Creates new form TelaPrincipal
     */
    public TelaPrincipal() {

        logger.info("Iniciando tela principal do FIVE...");

        this.addWindowListener(this);
        area = this.getContentPane();

        initMyComponents();
        initOptionsMenu();
        initPanelIcons();
    }

    private void initMyComponents() {

        setTitle("Framework for an Integrated Voice Environment");
        GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        setMaximizedBounds(e.getMaximumWindowBounds());
        setExtendedState(this.getExtendedState() | MAXIMIZED_BOTH);
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 700));

    }

    private void initOptionsMenu() {

        logger.info("Inicializando o menu de opções...");

        jMenuBar1 = new javax.swing.JMenuBar();

        jMenu1 = new javax.swing.JMenu();

        jSeparator1 = new javax.swing.JSeparator();
        jMenuNovo = new javax.swing.JMenuItem();
        jMenuAbrir = new javax.swing.JMenuItem();
        jMenuFechar = new javax.swing.JMenuItem();
        jMenuSair = new javax.swing.JMenuItem();

        jMenu1.setLabel("Arquivo");

        jMenuNovo.setText("Novo");
        jMenuNovo.setName("jmNovo"); // NOI18N
        jMenuNovo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuNovoActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuNovo);

        jMenuAbrir.setText("Abrir");
        jMenuAbrir.setName("jmAbrir"); // NOI18N
        jMenuAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuAbrirActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuAbrir);

        jMenuFechar.setText("Fechar");
        jMenuFechar.setName("jmFechar"); // NOI18N
        jMenuFechar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuFecharActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuFechar);

        jMenu1.add(jSeparator1);

        jMenuSair.setText("Sair");
        jMenuSair.setName("jmSair"); // NOI18N
        jMenuSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuSairActionPerformed(evt);
            }
        });        
    }

    private void initPanelIcons() {

        panelIcons = new JPanel();
        panelIcons.setPreferredSize(new Dimension(400, 43));
        panelIcons.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btNew = new javax.swing.JButton();
        btOpen = new javax.swing.JButton();
        btClose = new javax.swing.JButton();

        btNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/ufpe/cin/five/images/new.png"))); // NOI18N
        btNew.setPreferredSize(new java.awt.Dimension(37, 37));
        btNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuNovoActionPerformed(evt);
            }
        });

        btOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/ufpe/cin/five/images/open.png"))); // NOI18N
        btOpen.setPreferredSize(new java.awt.Dimension(37, 37));
        btOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuAbrirActionPerformed(evt);
            }
        });

        btClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/ufpe/cin/five/images/close.png"))); // NOI18N
        btClose.setPreferredSize(new java.awt.Dimension(37, 37));
        btClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuFecharActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelIconsLayout = new javax.swing.GroupLayout(panelIcons);
        panelIcons.setLayout(jPanelIconsLayout);
        jPanelIconsLayout.setHorizontalGroup(
                jPanelIconsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanelIconsLayout.createSequentialGroup()
                        .addComponent(btNew, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btOpen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addContainerGap(230, Short.MAX_VALUE)));
        jPanelIconsLayout.setVerticalGroup(
                jPanelIconsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanelIconsLayout.createSequentialGroup()
                        .addGroup(jPanelIconsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(btNew, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btOpen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap()));

        area.add(panelIcons, BorderLayout.PAGE_START);
        panelIcons.setVisible(true);

        validate();
    }

    private void initPanelTree() {

        File dir = new File(Facade.getInstance().getProject().getDirectory());
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                JOptionPane.showMessageDialog(this, "Informe um diretório válido!");
            }
        }

        panelTree = new JPanel();
        panelTree.setPreferredSize(new Dimension(230, 100));
        panelTree.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        SoftBevelBorder sbb = new SoftBevelBorder(SoftBevelBorder.LOWERED);
        panelTree.setBorder(new EmptyBorder(3, 3, 3, 3));
        panelTree.setBorder(sbb);
        panelTree.setLayout(new BoxLayout(panelTree, BoxLayout.Y_AXIS));

        File f = new File(dir.getPath());
        File[] roots = new File[1];
        roots[0] = f;

        FileTreeNode rootTreeNode = new FileTreeNode(roots);
        jTree1 = new JTree(rootTreeNode);
        jTree1.setCellRenderer(new FileTreeCellRenderer());
        jTree1.setRootVisible(true);
        jTree1.expandRow(1);
        jTree1.setEditable(false);
        jTree1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                TelaPrincipal.this.treeMouseClick(e);
            }
        });
        final JScrollPane jsp = new JScrollPane(jTree1);
        jsp.setBorder(new EmptyBorder(0, 0, 0, 0));
        panelTree.add(jsp, BorderLayout.CENTER);

        area.add(panelTree, BorderLayout.WEST);
        panelTree.setVisible(true);

        validate();
    }

    private void initTabbedPanel() {

        panel = new JPanel();

        jTabbedPane1 = new javax.swing.JTabbedPane();

        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        speakerPannel = new SpeakerPanel();
        jTabbedPane1.add(speakerPannel, 0);
        jTabbedPane1.setTitleAt(0, "Locutores");

        utterancePanel = new UtterancePanel();
        jTabbedPane1.add(utterancePanel, 1);
        jTabbedPane1.setTitleAt(1, "Locuções");

        wordPanel = new DictionaryPanel();
        jTabbedPane1.add(wordPanel, 2);
        jTabbedPane1.setTitleAt(2, "Dicionário");

        dataBasePanel = new SamplePanel();
        jTabbedPane1.add(dataBasePanel, 3);
        jTabbedPane1.setTitleAt(3, "Amostras");

        featurePanel = new ExtractionPanel();
        jTabbedPane1.add(featurePanel, 4);
        jTabbedPane1.setTitleAt(4, "Extração de Características");

        classificationPanel = new ClassificationPanel();
        jTabbedPane1.add(classificationPanel, 5);
        jTabbedPane1.setTitleAt(5, "Classificação dos Padrões");

        enginePanel = new EnginePanel();
        jTabbedPane1.add(enginePanel, 6);
        jTabbedPane1.setTitleAt(6, "Geração do Motor");

//        if (facade.getProject().getType() == ProjectType.ASR) {
//                                  
//            speechTestPanel = new SpeechTestPanel();
//            jTabbedPane1.add(speechTestPanel, 7);
//            jTabbedPane1.setTitleAt(7, "Ambiente de Teste"); 
//            
//        } else if (facade.getProject().getType() == ProjectType.ASV) {
//            
//            speakerTestPanel = new SpeakerTestPanel();
//            jTabbedPane1.add(speakerTestPanel, 7);
//            jTabbedPane1.setTitleAt(7, "Ambiente de Teste");             
//            
//        } else if (facade.getProject().getType() == ProjectType.TTS) {
//            
//            synthesisTestPanel = new SynthesisTestPanel();
//            jTabbedPane1.add(synthesisTestPanel, 7);
//            jTabbedPane1.setTitleAt(7, "Ambiente de Teste");                         
//        } 
//        wordExceptionPanel = new WordExceptionPanel();
//        jTabbedPane1.add(wordExceptionPanel,8);
//        jTabbedPane1.setTitleAt(8, "Exceções");    
        panel.setPreferredSize(new Dimension(300, 100));
        panel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        area.add(jTabbedPane1, BorderLayout.CENTER);
        panel.setVisible(true);
    }

    private void treeMouseClick(MouseEvent me) {

        if (me.getClickCount() == 2) {
            TreePath tp = this.jTree1.getPathForLocation(me.getX(), me.getY());
            if (tp != null) {
                FileTreeNode ftn = (FileTreeNode) tp.getLastPathComponent();
                if (ftn.file != null && ftn.file.isFile()) {
                    //Se for arquivo .wav abrimos a tela de edicao de sample.
                    if (ftn.file.getName().endsWith(".wav")) {
                        try {
                            File f = new File(ftn.file.getPath());
                            String waveFile = "waves" + File.separator + f.getName();

                            logger.info("Buscando sample: " + waveFile);
                            Sample sample = facade.searchSample(waveFile);

                            new SampleDialog(this, true, sample, "", 1).setVisible(true);
                        } catch (Exception ex) {
                            logger.error(ex);
                            JOptionPane.showMessageDialog(this, "Erro ao editar sample.", "Editar Sample", JOptionPane.ERROR_MESSAGE);
                        }

                    } else {
                        //Senao abrimos o visualizador de conteudo de arquivos.
                        new PreviewDialog(this, ftn.file).setVisible(true);
                    }
                }
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("FIVE");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setEnabled(false);
        setExtendedState(MAXIMIZED_BOTH);
        setMinimumSize(new java.awt.Dimension(800, 600));
        setUndecorated(true);
        setResizable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 763, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 509, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuSairActionPerformed(java.awt.event.ActionEvent evt) {
        logger.info("Encerrando aplicação.");
        int option = JOptionPane.showConfirmDialog(null, "Deseja sair do sistema?", "Five", JOptionPane.OK_CANCEL_OPTION);
        if (option == 0) {
            System.exit(0);
        }
    }

    private void jMenuFecharActionPerformed(java.awt.event.ActionEvent evt) {
        logger.info("Fechando projeto.");
        if (facade.getProject() != null) {
            try {
                facade.closeProject(facade.getProject());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Five não pode ser fechado nesse momento!");
            }
            this.jTabbedPane1.setVisible(false);
            this.panel.setVisible(false);
            this.panelTree.setVisible(false);
            validate();
            facade.setProject(null);
        }
    }

    private void jMenuAbrirActionPerformed(java.awt.event.ActionEvent evt) {

        logger.info("Abrindo projeto.");

        File arquivo = null;
        JFileChooser jfc = new JFileChooser();

        jfc.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }
                String name = f.getName();
                if (name.endsWith(".xml")) {
                    return true;
                }
                return false;
            }

            public String getDescription() {
                return "*.xml";
            }
        });

        if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            arquivo = jfc.getSelectedFile();
        }
        if (arquivo != null) {
            try {
                facade.openProject(arquivo);
                facade.initProjectRegisters();
            } catch (RegisterException ex) {
                logger.error(ex.getMessage());
                JOptionPane.showMessageDialog(null, ex.getMessage());
                return;
            }
            initTabbedPanel();
            initPanelTree();
            invalidate();
            validate();
        }

    }

    private void jMenuNovoActionPerformed(java.awt.event.ActionEvent evt) {

        logger.info("Novo projeto.");
        NewProjetctDialog tela = new NewProjetctDialog(this, true);
        tela.pack();
        tela.setVisible(true);

        if (facade.getProject() != null) {
            initTabbedPanel();
            initPanelTree();
            invalidate();
            validate();
        }
    }

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {
        if (jTabbedPane1.getSelectedIndex() == 0) {// Locutores
            speakerPannel.addAllLinesTableSpeaker();
        } else if (jTabbedPane1.getSelectedIndex() == 1) {// Locuções
            utterancePanel.addAllLinesTableUtterance();
        } else if (jTabbedPane1.getSelectedIndex() == 2) {// Dicionário
            wordPanel.addAllLinesTableWord();
        } else if (jTabbedPane1.getSelectedIndex() == 3) {//Amostras
            dataBasePanel.addAllLinesTableSample();
        } else if (jTabbedPane1.getSelectedIndex() == 4) {//Extraction
            featurePanel.addAllLinesTableExtraction();
        } else if (jTabbedPane1.getSelectedIndex() == 5) {//Classification
            classificationPanel.addAllLinesTableClassification();
        } else if (jTabbedPane1.getSelectedIndex() == 6) {//Engine
            enginePanel.addAllLinesTableEngine();
        } else if (jTabbedPane1.getSelectedIndex() == 8) {//Exceptions
            wordExceptionPanel.populateTable();
        }

        if (jTabbedPane1.getSelectedIndex() == 7) { // Ambiente de teste
            if (facade.getProject().getType() == ProjectType.ASV) {
                logger.info("Abrindo aba Motor de Verificação");
                speakerTestPanel.carregaLocutores();
            } else if (facade.getProject().getType() == ProjectType.TTS) {
                logger.info("Abrindo aba Motor de Síntese");
                synthesisTestPanel.carregaLocutores();
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaPrincipal().setVisible(true);
            }
        });
    }

    public void windowOpened(WindowEvent e) {
        //    throw new UnsupportedOperationException("Not supported yet.");
    }

    public void windowClosed(WindowEvent e) {
        //     throw new UnsupportedOperationException("Not supported yet.");
    }

    public void windowIconified(WindowEvent e) {
        //     throw new UnsupportedOperationException("Not supported yet.");
    }

    public void windowDeiconified(WindowEvent e) {
        //   throw new UnsupportedOperationException("Not supported yet.");
    }

    public void windowActivated(WindowEvent e) {
        //    throw new UnsupportedOperationException("Not supported yet.");
    }

    public void windowDeactivated(WindowEvent e) {
        //   throw new UnsupportedOperationException("Not supported yet.");
    }

    public void windowClosing(WindowEvent e) {
        try {
            if (facade.getProject() != null) {
                facade.closeProject(facade.getProject());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Five não pode ser fechado nesse momento!");
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
