/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.marcelosilveira.gerenciadorDeMemoria.gui;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marcelo
 */
public class Memoria extends JImagePanel {
    protected Barra barra;

    /**
     * Creates new form Memoria
     */
    public Memoria() {
        super();
        try {
            setImage(getClass().getResource("/com/marcelosilveira/gerenciadorDeMemoria/images/memoria.png"));
        } catch (Exception ex) {
            Logger.getLogger(Memoria.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        initComponents();
    }

    @SuppressWarnings("unchecked")
     private void initComponents() {
        barra = new Barra(500, 90);
        
         javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 40)
                .addComponent(barra)
                .addContainerGap(29, 40))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 20)
                .addComponent(barra)
                .addContainerGap(26, 70))
        );
    }
     
     public void load(String s) {
         barra.load(s);
     }
}