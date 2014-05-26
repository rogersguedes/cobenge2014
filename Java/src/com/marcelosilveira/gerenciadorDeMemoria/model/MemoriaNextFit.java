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
public class MemoriaNextFit extends Memoria {

    private int ultPro;

    public MemoriaNextFit(int tamanho, int tamanhoSO) {
        super(tamanho, tamanhoSO);
        ultPro = tamanhoSO;
    }

    @Override
    public boolean alocar(Processo processo) {
        int alocacoes[] = new int[2 * memoria.size() + 1];
        int posicao = 0;
        int index = 1;
        int aux = ultPro;

        for (int pegarIntervalos = 0; pegarIntervalos < memoria.size(); pegarIntervalos++) {
            alocacoes[posicao] = memoria.get(pegarIntervalos).getInicio();
            alocacoes[posicao + 1] = memoria.get(pegarIntervalos).getFim();
            posicao = posicao + 2;
        }
        alocacoes[posicao] = super.tamanho;

        posicao = 2;

        while (posicao <= alocacoes.length && alocacoes[posicao] - ultPro < 0) {
            posicao += 2;
            index++;
        }

        while (!processo.isAlocado() && posicao < alocacoes.length) {
            if (processo.getTamanhoMemoria() <= alocacoes[posicao] - aux) {
                memoria.add(index, new IntervaloMemoria(processo, aux + 1, aux + processo.getTamanhoMemoria()));
                processo.setAlocado();
                ultPro = aux + processo.getTamanhoMemoria();
            } else {
                if(posicao < alocacoes.length - 1) { 
                    aux = alocacoes[posicao + 1];
                    index++;
                    posicao += 2;
                } else break;
            }
        }

        if (!processo.isAlocado()) {
            IntervaloMemoria intervalo1, intervalo2;
            intervalo1 = memoria.get(0); //SO

            int i = 1;
            int inicio = intervalo1.getFim() + 1;
            int fim;
            while (!processo.isAlocado() && i < memoria.size() && memoria.get(i).getInicio() < ultPro) {
                intervalo2 = memoria.get(i);
                fim = intervalo2.getInicio() - 1;
                if (fim - inicio + 1 >= processo.getTamanhoMemoria()) {
                    memoria.add(i, new IntervaloMemoria(processo, inicio,
                            inicio + processo.getTamanhoMemoria() - 1));

                    ultPro = inicio + processo.getTamanhoMemoria() - 1;

                    processo.setAlocado();
                } else {
                    intervalo1 = intervalo2;
                    inicio = intervalo1.getFim() + 1;
                    i++;
                }
            }

        }

        return processo.isAlocado();
    }
}
