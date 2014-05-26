package com.marcelosilveira.gerenciadorDeMemoria.model;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Marcelo
 */
public class MemoriaFirstFit extends Memoria{
    public MemoriaFirstFit(int tamanho, int tamanhoSO) {
        super(tamanho, tamanhoSO);
    }

    @Override
    public boolean alocar(Processo processo) {
        IntervaloMemoria intervalo1, intervalo2;
        intervalo1 = memoria.get(0); //SO
        
        int i = 1;
        int inicio = intervalo1.getFim() + 1;
        int fim;
        while(!processo.isAlocado() && i < memoria.size()) {
            intervalo2 = memoria.get(i);
            fim = intervalo2.getInicio() - 1;
            if(fim - inicio + 1 >= processo.getTamanhoMemoria()) {
                memoria.add(i, new IntervaloMemoria(processo, inicio,
                        inicio + processo.getTamanhoMemoria() - 1));
                processo.setAlocado();
            } else {
                intervalo1 = intervalo2;
                inicio = intervalo1.getFim() + 1;
                i++;
            }
        }
        
        if(!processo.isAlocado())
        {
            fim = tamanho;
            if(fim - inicio + 1 >= processo.getTamanhoMemoria())
            {
                memoria.add(new IntervaloMemoria(processo, inicio,
                        inicio + processo.getTamanhoMemoria() - 1));
                processo.setAlocado();
            }
        }
        
        return processo.isAlocado();
    }
}