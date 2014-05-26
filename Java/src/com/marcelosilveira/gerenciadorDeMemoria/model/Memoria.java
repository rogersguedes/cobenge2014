/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.marcelosilveira.gerenciadorDeMemoria.model;

import java.util.ArrayList;

/**
 *
 * @author Marcelo
 */
public abstract class Memoria {
    public enum EstrategiaAlocacao {FIRST_FIT, BEST_FIT, WORST_FIT, NEXT_FIT;};
    
    protected ArrayList<IntervaloMemoria> memoria;
    protected int tamanho;
    
    public Memoria(int tamanho, int tamanhoSO) {
        this.tamanho = tamanho;
        memoria = new ArrayList<IntervaloMemoria>();
        memoria.add(new IntervaloMemoria(new Processo("SO", tamanhoSO, Integer.MAX_VALUE), 1, tamanhoSO));
    }
       
    public float getPercentageOfUsedMemory() {
        float soma = 0;
        for(IntervaloMemoria i : memoria) 
            soma += i.getFim() - i.getInicio() + 1;
        
        return 100*soma/(float)tamanho;
    }
    
    public void clock() {
        for(IntervaloMemoria i : memoria) 
            i.getProcesso().clock();
        
        for(int i=0; i< memoria.size();) {
            if(memoria.get(i).getProcesso().isFinalizado())
                memoria.remove(i);
            else
                i++;
        }
    }
    
    public int numeroDeProcessos() {
        return memoria.size()-1;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int fim = 0;
        for(IntervaloMemoria i : memoria) {
            if(i.getInicio() != fim + 1) {
                sb.append(String.format("[ %d - %d ] ------\n",
                        fim + 1, i.getInicio() - 1));
            }
            sb.append(i);
            sb.append("\n");
            fim = i.getFim();
        }
        if(fim != tamanho) 
            sb.append(String.format("[ %d - %d ] ------\n", fim + 1, tamanho));
        
        return sb.toString();
    }
    
    public abstract boolean alocar(Processo processo);
}