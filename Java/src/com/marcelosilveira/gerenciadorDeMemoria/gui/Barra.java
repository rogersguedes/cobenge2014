/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.marcelosilveira.gerenciadorDeMemoria.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;
import javax.swing.JComponent;

/**
 *
 * @author Marcelo
 */
public class Barra extends JComponent {
    
    protected static class Quad{
        final int x; 
        final int comprimento;
        final Color color;
        final String processo;

        public Quad(int x, int comprimento, Color color, String processo) {
            this.x = x;
            this.comprimento = comprimento;
            this.color = color;
            this.processo = processo;
        }               
    }

    protected final ArrayList<Quad> quads;
    protected static Hashtable<String, Color> cores = new Hashtable();
    
    public Barra() {
        this(0, 0);
    }

    public Barra(int comprimento, int altura) {
        super();
        quads = new ArrayList<>();
        quads.add(new Quad(0, comprimento, Color.white, "------"));
        
        setSize(comprimento, altura);
        setMinimumSize(new Dimension(comprimento, altura));
        setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        int soma = 0;
        for (Quad quad : quads)
            soma += quad.comprimento;
        
        float mult = this.getSize().width/(float)soma;
        
        for (Quad quad : quads) {
            g.setColor(quad.color);
            g.fillRect((int)(quad.x * mult), 0, (int)(quad.comprimento * mult), this.getSize().height);
        }
    }
    
    public void load(String s)
    {
        quads.clear();
        Scanner sc = new Scanner(s);
        
        int x1;
        int x2;
        String process;
        Color color;
        
        while(sc.hasNextLine()){ 
            sc.next();
            x1 = sc.nextInt();
            sc.next();
            x2 = sc.nextInt();
            sc.next();
            
            process = sc.nextLine().substring(1);
            if(process.charAt(0)=='S')
                color = Color.red;
            else if(process.charAt(0)=='-')
                color = Color.white;
            else {
                if(cores.containsKey(process))
                    color = cores.get(process);
                else {
                    color = new Color((int)(Math.random()*255), 
                            (int)(Math.random()*255), (int)(Math.random()*255));
                    cores.put(process, color);
                }
            }
            
            quads.add(new Quad(x1, x2-x1+1, color, process.substring(1)));
        }
    
        repaint();
    }
}