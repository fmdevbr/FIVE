/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.gui.classification;

import org.apache.log4j.Logger;

/**
 *
 * @author Alexandre
 */
public class PanelSVMParams extends javax.swing.JPanel {

    private static final Logger logger = Logger.getLogger(PanelSVMParams.class);

    /** Creates new form PainelSVM */
    public PanelSVMParams() {
        logger.info("Inicializando PanelSVMParams.");
        initComponents();
    }

    public String getKernelType() {
        if (cbKernelType.getSelectedItem().equals("Linear")) {
            return "0";
        }
        if (cbKernelType.getSelectedItem().equals("Polinomial")) {
            return "1";
        }
        if (cbKernelType.getSelectedItem().equals("RBF")) {
            return "2";
        }
        if (cbKernelType.getSelectedItem().equals("Sigmóde")) {
            return "3";
        }
        return null;
    }

    public void setKernelType(String kernelType) {
        if (kernelType.equals("0")) {
            cbKernelType.setSelectedItem("Linear");
        }
        if (kernelType.equals("1")) {
            cbKernelType.setSelectedItem("Polinomial");
        }
        if (kernelType.equals("2")) {
            cbKernelType.setSelectedItem("RBF");
        }
        if (kernelType.equals("3")) {
            cbKernelType.setSelectedItem("Sigmóde");
        }
    }

    public String getGamma() {
        return tfGamma.getText();
    }

    public void setGamma(String gamma) {
        tfGamma.setText(gamma);
    }

    public String getCost() {
        return tfCost.getText();
    }

    public void setCost(String cost) {
        tfCost.setText(cost);
    }

    public String getDegree() {
        return tfDegree.getText();
    }

    public void setDegree(String degree) {
        tfDegree.setText(degree);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lbGravar3 = new javax.swing.JLabel();
        lbGravar2 = new javax.swing.JLabel();
        lbGravar1 = new javax.swing.JLabel();
        tfCost = new javax.swing.JTextField();
        tfGamma = new javax.swing.JTextField();
        tfDegree = new javax.swing.JTextField();
        cbKernelType = new javax.swing.JComboBox();
        lbGravar = new javax.swing.JLabel();

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbGravar3.setText("Degree:");

        lbGravar2.setText("Cost:");

        lbGravar1.setText("Gamma:");

        tfCost.setText("10");
        tfCost.setMaximumSize(new java.awt.Dimension(6, 20));

        tfGamma.setText("0");
        tfGamma.setMaximumSize(new java.awt.Dimension(6, 20));

        tfDegree.setText("0");
        tfDegree.setMaximumSize(new java.awt.Dimension(6, 20));

        cbKernelType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Linear", "Polinomial", "RBF", "Sigmóde" }));
        cbKernelType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbKernelTypeActionPerformed(evt);
            }
        });

        lbGravar.setText("Kernel:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbGravar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbKernelType, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbGravar1)
                    .addComponent(lbGravar2)
                    .addComponent(lbGravar3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tfCost, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfGamma, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfDegree, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(61, 61, 61))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbGravar1)
                            .addComponent(tfGamma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(11, 11, 11)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbGravar2)
                            .addComponent(tfCost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(11, 11, 11)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbGravar3)
                            .addComponent(tfDegree, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lbGravar)
                        .addComponent(cbKernelType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cbKernelTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbKernelTypeActionPerformed
        String kernelType = cbKernelType.getSelectedItem().toString();
        setKernelType(cbKernelType.getSelectedItem().toString());
        if (kernelType.equals("Linear")) {
            tfGamma.setText("0");
            tfCost.setText("10");
            tfDegree.setText("0");
        } else if (kernelType.equals("Polinomial")) {
            tfGamma.setText("-2");
            tfCost.setText("10");
            tfDegree.setText("2");
        } else if (kernelType.equals("RBF")) {
            tfGamma.setText("-2");
            tfCost.setText("12");
            tfDegree.setText("-8");
        } else if (kernelType.equals("Sigmóde")) {
            tfGamma.setText("-2");
            tfCost.setText("12");
            tfDegree.setText("-8");
        }
        
    }//GEN-LAST:event_cbKernelTypeActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cbKernelType;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lbGravar;
    private javax.swing.JLabel lbGravar1;
    private javax.swing.JLabel lbGravar2;
    private javax.swing.JLabel lbGravar3;
    private javax.swing.JTextField tfCost;
    private javax.swing.JTextField tfDegree;
    private javax.swing.JTextField tfGamma;
    // End of variables declaration//GEN-END:variables
}