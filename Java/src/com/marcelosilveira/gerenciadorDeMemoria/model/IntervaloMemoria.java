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
public class IntervaloMemoria {
    private final Processo processo;
    private final int inicio;
    private final int fim;
    
    public IntervaloMemoria(Processo processo, int inicio, int fim) {
        this.processo = processo;
        this.inicio = inicio;
        this.fim = fim;
    }

    public Processo getProcesso() {
        return processo;
    }

    public int getInicio() {
        return inicio;
    }

    public int getFim() {
        return fim;
    }
    
    @Override
    public String toString() {
        return String.format("[ %d - %d ] %s", inicio, fim, processo);
    }
}