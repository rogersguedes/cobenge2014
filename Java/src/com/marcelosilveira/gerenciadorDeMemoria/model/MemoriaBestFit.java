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
public class MemoriaBestFit extends Memoria{
    
    public MemoriaBestFit(int tamanho, int tamanhoSO) {
        super(tamanho, tamanhoSO);
    }

    @Override
    public boolean alocar(Processo processo) {       
        int alocacoes[] = new int[2*(memoria.size()+1)];
        int melhorInicial = 0;
        int melhorFinal = super.tamanho;      
        int posicao = 0; 
        for(int pegarIntervalos = 0; pegarIntervalos < memoria.size(); pegarIntervalos++) {
           alocacoes[posicao] = memoria.get(pegarIntervalos).getInicio();
           alocacoes[posicao+1] = memoria.get(pegarIntervalos).getFim();
            posicao = posicao + 2;       
        }      
        alocacoes[posicao] = super.tamanho;
        posicao = 1;    
        int index = 1;
        int ultIndex = 1;
        int flagPassei = 0;
        while(true) {
            if(((alocacoes[posicao+1] - alocacoes[posicao]) < (melhorFinal - melhorInicial)) && (processo.getTamanhoMemoria() <= (alocacoes[posicao+1] - alocacoes[posicao]))) {
                
                melhorInicial = alocacoes[posicao] + 1;
                melhorFinal = alocacoes[posicao+1] - 1;
                ultIndex = index;
                flagPassei = 1;
            }
            if(alocacoes[posicao+1] == super.tamanho) break;
            index++;
            posicao += 2;
        }       
        
        if(flagPassei == 1) {
            memoria.add(ultIndex, new IntervaloMemoria(processo, melhorInicial, melhorInicial + processo.getTamanhoMemoria() - 1));
            processo.setAlocado();
        }
        
        return processo.isAlocado();
    }
}