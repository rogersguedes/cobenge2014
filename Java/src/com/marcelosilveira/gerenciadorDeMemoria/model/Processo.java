/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.marcelosilveira.gerenciadorDeMemoria.model;

/**
 *
 * @author Marcelo
 */
public class Processo {
    private static int TEMPO = 0;
    
    public static int getTEMPO() {
        return TEMPO;
    }
    
    public static void incrementeTEMPO() {
        TEMPO++;
    }
    
    public static void resetTEMPO() {
        TEMPO = 0;
    }
    
    private String nome;
    private int tempoCriacao;
    private int tempoAlocacao;
    private boolean alocado;
    private int tempoFinalizacao;
    private boolean finalizado;
    private int tamanhoMemoria;
    private int duracao;
    
    public Processo(String nome, int tamanhoMemoria, int duracao) {
        this.nome = nome;
        this.tamanhoMemoria = tamanhoMemoria;
        this.duracao = duracao;
        tempoCriacao = TEMPO;
        tempoAlocacao = 0;
        tempoFinalizacao = 0;
        alocado = false;
        finalizado = false;        
    }

    public String getNome() {
        return nome;
    }

    public int getTempoCriacao() {
        return tempoCriacao;
    }

    public int getTempoAlocacao() {
        return tempoAlocacao;
    }

    public int getTempoFinalizacao() {
        return tempoFinalizacao;
    }

    public boolean isAlocado() {
        return alocado;
    }

    public void setAlocado() {
        this.alocado = true;
        tempoAlocacao = TEMPO;
    }

    public boolean isFinalizado() {
        return finalizado;
    }

    public void clock() {
        if(alocado && !finalizado) {
            duracao--;
            if(duracao == 0) {
                tempoFinalizacao = TEMPO;
                finalizado = true;
            }
        }
    }
    
    public String toString() {
        return nome;
    }

    public int getTamanhoMemoria() {
        return tamanhoMemoria;
    }
}