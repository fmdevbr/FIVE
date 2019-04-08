/**
 * Copyright 2011 Federal University of Pernambuco. All Rights Reserved. Use is
 * subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.gui.sample;

import br.ufpe.cin.five.core.sample.Sample;
import br.ufpe.cin.five.core.sample.SampleFilter;
import br.ufpe.cin.five.exception.RegisterException;
import br.ufpe.cin.five.facade.Facade;
import br.ufpe.cin.five.gui.dialogs.ResultsDialog;
import java.util.List;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;

/**
 *
 * @author Alexandre
 */
public class SampleDivisionDialog extends javax.swing.JDialog {

    private static final Logger logger = Logger.getLogger(SampleDialog.class);
    private Facade facade = Facade.getInstance();
    private SampleFilter filter;
    private int train;
    private int test;

    /**
     * Creates new form DataBaseImportDialog
     */
    public SampleDivisionDialog(java.awt.Frame parent, boolean modal, SampleFilter filter) {
        super(parent, modal);
        this.filter = filter;
        logger.info("Inicializando Sample Division Dialog.");
        initComponents();
    }

    private boolean validateFields(){
        return false;
    }        
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jlTrain = new javax.swing.JLabel();
        tfTrain = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jlTest = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        tfTest = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        btSeparate = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Transcrição");

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
                        .addContainerGap())))
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

        btSeparate.setText("Dividir");
        btSeparate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSeparateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btSeparate)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btSeparate)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btSeparateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSeparateActionPerformed
        logger.info("Botão btSeparate pressionado. Validando campos.");
        
        train = Integer.parseInt(tfTrain.getText());
        test = Integer.parseInt(tfTest.getText());
        
        if(validateFields()){        

            ResultsDialog dialog = new ResultsDialog(null, true, filter, train, test);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            
        } else {
            logger.warn("Informe os parâmetros necessarios");
            JOptionPane.showMessageDialog(null, "Informe os parâmetros necessários");
        }
    }//GEN-LAST:event_btSeparateActionPerformed
       
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btSeparate;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel jlTest;
    private javax.swing.JLabel jlTrain;
    private javax.swing.JTextField tfTest;
    private javax.swing.JTextField tfTrain;
    // End of variables declaration//GEN-END:variables

}
