package com.marcelosilveira.gerenciadorDeMemoria.gui;

import com.marcelosilveira.gerenciadorDeMemoria.model.MemoriaBestFit;
import com.marcelosilveira.gerenciadorDeMemoria.model.MemoriaFirstFit;
import com.marcelosilveira.gerenciadorDeMemoria.model.MemoriaNextFit;
import com.marcelosilveira.gerenciadorDeMemoria.model.MemoriaWorstFit;
import com.marcelosilveira.gerenciadorDeMemoria.model.Processo;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 *
 * @author Marcelo
 */
public class Principal extends JFrame {
    private int[] memoriaProcesso;
    private int tamanhoMemoria;
    private int[] intervaloCriacaoProcesso;
    private int[] intervaloDuracaoProcesso;
    private int tamanhoMemoriaSO;
    private int NumeroDeProcessos;
    
    private com.marcelosilveira.gerenciadorDeMemoria.model.Memoria memoria[];
    private int[] tempoCriacaoProcesso;
    private int proximoIndiceCriacao;
    private ArrayList<Processo> processosEmEspera[];
    
    private ArrayList<Processo> processo[];
    private int[] tempoFinalizacao;
    private boolean finalizado[];
    
    private boolean esperar;
    private boolean parar;
    private long tempoPasso;
    
    private Random random;
    private Timer timer;

    /**
     * Creates new form Principal
     */
    public Principal() { 
        timer = new Timer();
        random = new Random();
        
        memoriaProcesso = new int[2];
        intervaloCriacaoProcesso = new int[2];
        intervaloDuracaoProcesso = new int[2];
        tamanhoMemoria = 0;
        tamanhoMemoriaSO = 0;
        NumeroDeProcessos = 0;
        
        initComponents();
        setSize(getPreferredSize());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
                
        jTextFieldNumeroDeProcessos.setText("100");
        jTextFieldTamanhoDaMemoria.setText("4000");
        jTextFieldMemoriaSO.setText("500");
        jTextFieldMemoriaProcessoMin.setText("50");
        jTextFieldMemoriaProcessoMax.setText("400");
        jTextFieldCriacaoProcessoMin.setText("10");
        jTextFieldCriacaoProcessoMax.setText("30");
        jTextFieldDuracaoProcessoMin.setText("10");
        jTextFieldDuracaoProcessoMax.setText("100");
        
        jDialogInicio.setLocationRelativeTo(null);
        jDialogInicio.setVisible(true);             
    }
    
    private void initVariaveis() {
        memoria = new com.marcelosilveira.gerenciadorDeMemoria.model.Memoria[4];
        processosEmEspera = new ArrayList[4];
        processo = new ArrayList[4];
        proximoIndiceCriacao = 0;
        
        tempoFinalizacao = new int[4];
        finalizado = new boolean[4];
        finalizado[0] = false;
        finalizado[1] = false;
        finalizado[2] = false;
        finalizado[3] = false;
        
        tempoPasso = 1000;
        
        esperar = true;
        parar = true;
        
        for(int i= 0; i<4; i++) {
            processosEmEspera[i] = new ArrayList();
            processo[i] = new ArrayList();
        }
        
        timer.cancel();
        timer = new Timer();
    }
    
    private void atualizaProcessos() {
        SimpleAttributeSet aSet = new SimpleAttributeSet();
        int x1, x2;
        String process;
        Color color;      
        
        JTextPane jtp[] = new JTextPane[4];
        jtp[0] = jTextPaneProcessosFirstFit;
        jtp[1] = jTextPaneProcessosNextFit;
        jtp[2] = jTextPaneProcessosBestFit;
        jtp[3] = jTextPaneProcessosWorstFit;
        
        for(int j= 0; j< 4; j++) {
            if(!finalizado[j]) {
                jtp[j].setText("");
                Scanner sc = new Scanner(memoria[j].toString());       
                for(int i= 0; sc.hasNextLine(); i++) {
                    sc.next();
                    x1 = sc.nextInt();
                    sc.next();
                    x2 = sc.nextInt();
                    sc.next();
                    process = sc.nextLine().substring(1);
                    if(process.charAt(0)=='S')
                        color = Color.red;
                    else if(process.charAt(0)=='-')
                        color = Color.BLACK;
                    else
                        color = Barra.cores.get(process);

                    StyleConstants.setForeground(aSet, color);
                    try {
                        jtp[j].getDocument().insertString(jtp[j].getDocument().getLength(),
                                "[ "+x1+" - "+x2+" ] "+process.toString()+"\n", aSet);
                    } catch (BadLocationException ex) {}
                }
            }
        }
    }
    
    private void atualizaEspera() {
        StringBuilder sb[] = new StringBuilder[4];
        for(int i= 0; i<4; i++) {
            sb[i] = new StringBuilder();
            for(Processo p : processosEmEspera[i]) {
                sb[i].append(p);
                sb[i].append(" - ");
                sb[i].append(p.getTamanhoMemoria());
                sb[i].append("MB\n");
            }
        }
        
        jTextAreaEsperaFirstFit.setText(sb[0].toString());
        jTextAreaEsperaNextFit.setText(sb[1].toString());
        jTextAreaEsperaBestFit.setText(sb[2].toString());
        jTextAreaEsperaWorstFit.setText(sb[3].toString());
    }
    
    private void updateGUI() {
        memoriaFirstFit.load(memoria[0].toString());
        memoriaNextFit.load(memoria[1].toString());
        memoriaBestFit.load(memoria[2].toString());
        memoriaWorstFit.load(memoria[3].toString());  
        
        atualizaEspera();
        atualizaProcessos();
        
        StringBuffer sb = new StringBuffer();
        
        float tempoMedioDeEspera = 0;
        int i = 0;
        for(Processo p : processo[0])
            if(p.isAlocado()) {
                tempoMedioDeEspera += p.getTempoAlocacao() - p.getTempoCriacao();
                i++;
            }
        if(i != 0)
            tempoMedioDeEspera = tempoMedioDeEspera/i;
        
        if(finalizado[0]) {
            sb.append("FirstFit - TT ");
            sb.append(tempoFinalizacao[0]);
            sb.append("s - TME ");
            sb.append(tempoMedioDeEspera);
            sb.append("s\n");
        }        
        
        jPanelFirstFit.setBorder(
                javax.swing.BorderFactory.createTitledBorder(
                        "First Fit - "+tempoFinalizacao[0]+"s - "+
                        memoria[0].getPercentageOfUsedMemory()+"% - TME " +
                        tempoMedioDeEspera+"s"));

        tempoMedioDeEspera = 0;
        i = 0;
        for(Processo p : processo[1])
            if(p.isAlocado()) {
                tempoMedioDeEspera += p.getTempoAlocacao() - p.getTempoCriacao();
                i++;
            }
        if(i != 0)
            tempoMedioDeEspera = tempoMedioDeEspera/i;
        
        if(finalizado[1]) {
            sb.append("NextFit - TT ");
            sb.append(tempoFinalizacao[1]);
            sb.append("s - TME ");
            sb.append(tempoMedioDeEspera);
            sb.append("s\n");
        } 
        
        jPanelNextFit.setBorder(
                javax.swing.BorderFactory.createTitledBorder(
                        "Next Fit - "+tempoFinalizacao[1]+"s - "+
                        memoria[1].getPercentageOfUsedMemory()+"% - TME " +
                        tempoMedioDeEspera+"s"));
        
        tempoMedioDeEspera = 0;
        i = 0;
        for(Processo p : processo[2])
            if(p.isAlocado()) {
                tempoMedioDeEspera += p.getTempoAlocacao() - p.getTempoCriacao();
                i++;
            }
        if(i != 0)
            tempoMedioDeEspera = tempoMedioDeEspera/i;
        
        if(finalizado[2]) {
            sb.append("BestFit - TT ");
            sb.append(tempoFinalizacao[2]);
            sb.append("s - TME ");
            sb.append(tempoMedioDeEspera);
            sb.append("s\n");
        } 
        
        jPanelBestFit.setBorder(
                javax.swing.BorderFactory.createTitledBorder(
                        "Best Fit - "+tempoFinalizacao[2]+"s - "+
                        memoria[2].getPercentageOfUsedMemory()+"% - TME " +
                        tempoMedioDeEspera+"s"));
        
        tempoMedioDeEspera = 0;
        i = 0;
        for(Processo p : processo[3])
            if(p.isAlocado()) {
                tempoMedioDeEspera += p.getTempoAlocacao() - p.getTempoCriacao();
                i++;
            }
        if(i != 0)
            tempoMedioDeEspera = tempoMedioDeEspera/i;
        
        if(finalizado[3]) {
            sb.append("WorstFit - TT ");
            sb.append(tempoFinalizacao[3]);
            sb.append("s - TME ");
            sb.append(tempoMedioDeEspera);
            sb.append("s\n");
        } 
        
        jPanelWorstFit.setBorder(
                javax.swing.BorderFactory.createTitledBorder(
                        "Worst Fit - "+tempoFinalizacao[3]+"s - "+
                        memoria[3].getPercentageOfUsedMemory()+"% - TME " +
                        tempoMedioDeEspera+"s"));
        
        jTextAreaSaida.setText(sb.toString()); 
    }
    
    private void cleanAll() {
        jSliderVelocidade.setValue(1);
        
        updateGUI();
        
        jTextAreaEntrada.setText("");
        jTextAreaSaida.setText("");        
    }
    
    private void adicionarProcessos() {
        int duracao, tamanho;
        for(;proximoIndiceCriacao < NumeroDeProcessos && tempoCriacaoProcesso[proximoIndiceCriacao] <= Processo.getTEMPO(); proximoIndiceCriacao++) {
            tamanho = memoriaProcesso[0] + random.nextInt(memoriaProcesso[1]
                      - memoriaProcesso[0] + 1);
            duracao = intervaloDuracaoProcesso[0] + random.nextInt(intervaloDuracaoProcesso[1]
                      - intervaloDuracaoProcesso[0] + 1);
           
            StringBuilder sb = new StringBuilder();
            sb.append("Processo ");
            sb.append(proximoIndiceCriacao);
           
            for(int i= 0; i<4; i++) {
                Processo p = new Processo(sb.toString(), tamanho, duracao);
                processo[i].add(p);
                processosEmEspera[i].add(p);
            }
           
            sb.append(" - ");
            sb.append(tamanho);
            sb.append("MB - ");
            sb.append('C');
            sb.append(Processo.getTEMPO());
            sb.append("s - D");
            sb.append(duracao);
            sb.append("s\n");
           
            try {
                jTextAreaEntrada.getDocument().insertString(
                        jTextAreaEntrada.getDocument().getLength(), sb.toString(), null);
            } catch (BadLocationException ex) {
                Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        jTextAreaEntrada.setCaretPosition(jTextAreaEntrada.getDocument().getLength());
        
        for(int i= 0; i<4; i++)
            for(int j= 0; j<processosEmEspera[i].size();) 
                if(memoria[i].alocar(processosEmEspera[i].get(j)))
                    processosEmEspera[i].remove(j);
                else
                    j++;
    }
    
    private void clock() {
        adicionarProcessos();
        
        if(jDialogInformacaoesDetalhadas.isVisible())
            mostraProcessos();
        
        if(esperar)
            updateGUI();
            
             
        Processo.incrementeTEMPO();
        for(int i= 0; i< 4; i++) {
            memoria[i].clock();
            if(!finalizado[i] && proximoIndiceCriacao == NumeroDeProcessos && memoria[i].numeroDeProcessos() == 0)
               finalizado[i] = true;
            if(!finalizado[i])
               tempoFinalizacao[i] = Processo.getTEMPO(); 
        }
        
        if(finalizado[0] && finalizado[1] && finalizado[2] && finalizado[3]) {
            timer.cancel();
            updateGUI();
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDialogInicio = new javax.swing.JDialog();
        jLabelImagemMemoria = new javax.swing.JLabel();
        jLabelNumeroDeProcessos = new javax.swing.JLabel();
        jTextFieldNumeroDeProcessos = new javax.swing.JTextField();
        jLabelTamanhoDaMemoria = new javax.swing.JLabel();
        jTextFieldTamanhoDaMemoria = new javax.swing.JTextField();
        jLabelMemoriaSO = new javax.swing.JLabel();
        jTextFieldMemoriaSO = new javax.swing.JTextField();
        jLabelMemoriaProcesso = new javax.swing.JLabel();
        jTextFieldMemoriaProcessoMin = new javax.swing.JTextField();
        jTextFieldMemoriaProcessoMax = new javax.swing.JTextField();
        jLabelCriacaoProcesso = new javax.swing.JLabel();
        jTextFieldCriacaoProcessoMin = new javax.swing.JTextField();
        jTextFieldCriacaoProcessoMax = new javax.swing.JTextField();
        jLabelDuracaoProcesso = new javax.swing.JLabel();
        jTextFieldDuracaoProcessoMin = new javax.swing.JTextField();
        jTextFieldDuracaoProcessoMax = new javax.swing.JTextField();
        jButtonIniciar = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabelFranklin = new javax.swing.JLabel();
        jLabelMarcelo = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabelSistemasOperacionais = new javax.swing.JLabel();
        jLabelEngenhariaDeComputaçãoIFCE = new javax.swing.JLabel();
        jDialogInformacaoesDetalhadas = new javax.swing.JDialog();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextPaneProcessosFirstFit1 = new javax.swing.JTextPane();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextPaneProcessosNextFit1 = new javax.swing.JTextPane();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPaneProcessosBestFit1 = new javax.swing.JTextPane();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextPaneProcessosWorstFit1 = new javax.swing.JTextPane();
        jPanelFirstFit = new javax.swing.JPanel();
        jScrollPaneEsperaFirstFit = new javax.swing.JScrollPane();
        jTextAreaEsperaFirstFit = new javax.swing.JTextArea();
        memoriaFirstFit = new com.marcelosilveira.gerenciadorDeMemoria.gui.Memoria();
        jScrollPaneProcessosFirstFit = new javax.swing.JScrollPane();
        jTextPaneProcessosFirstFit = new javax.swing.JTextPane();
        jPanelNextFit = new javax.swing.JPanel();
        jScrollPaneEsperaNextFit = new javax.swing.JScrollPane();
        jTextAreaEsperaNextFit = new javax.swing.JTextArea();
        memoriaNextFit = new com.marcelosilveira.gerenciadorDeMemoria.gui.Memoria();
        jScrollPaneProcessosNextFit = new javax.swing.JScrollPane();
        jTextPaneProcessosNextFit = new javax.swing.JTextPane();
        jPanelBestFit = new javax.swing.JPanel();
        jScrollPaneEsperaBestFit = new javax.swing.JScrollPane();
        jTextAreaEsperaBestFit = new javax.swing.JTextArea();
        memoriaBestFit = new com.marcelosilveira.gerenciadorDeMemoria.gui.Memoria();
        jScrollPaneProcessosBestFit = new javax.swing.JScrollPane();
        jTextPaneProcessosBestFit = new javax.swing.JTextPane();
        jPanelWorstFit = new javax.swing.JPanel();
        jScrollPaneEsperaWorstFit = new javax.swing.JScrollPane();
        jTextAreaEsperaWorstFit = new javax.swing.JTextArea();
        memoriaWorstFit = new com.marcelosilveira.gerenciadorDeMemoria.gui.Memoria();
        jScrollPaneProcessosWorstFit = new javax.swing.JScrollPane();
        jTextPaneProcessosWorstFit = new javax.swing.JTextPane();
        jPanelControle = new javax.swing.JPanel();
        jSliderVelocidade = new javax.swing.JSlider();
        jLabelVelocidade = new javax.swing.JLabel();
        jLabel1x = new javax.swing.JLabel();
        jLabel10x = new javax.swing.JLabel();
        jButtonPasso = new javax.swing.JButton();
        jButtonConcluir = new javax.swing.JButton();
        jButtonNovaSimulacao = new javax.swing.JButton();
        jToggleButton1 = new javax.swing.JToggleButton();
        jPanelEntrada = new javax.swing.JPanel();
        jScrollPaneEntrada = new javax.swing.JScrollPane();
        jTextAreaEntrada = new javax.swing.JTextArea();
        jPanelSaida = new javax.swing.JPanel();
        jScrollPaneSaida = new javax.swing.JScrollPane();
        jTextAreaSaida = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();

        jDialogInicio.setMinimumSize(new java.awt.Dimension(360, 490));
        jDialogInicio.setModal(true);
        jDialogInicio.setResizable(false);
        jDialogInicio.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                jDialogInicioWindowClosing(evt);
            }
        });

        jLabelImagemMemoria.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/marcelosilveira/gerenciadorDeMemoria/images/memoriaInit.png"))); // NOI18N

        jLabelNumeroDeProcessos.setText("Número de processos:");

        jLabelTamanhoDaMemoria.setText("Tamanho da memória (MB):");

        jLabelMemoriaSO.setText("Memória ocupada pelo SO (MB):");

        jLabelMemoriaProcesso.setText("Tamanho da memória por processo (MB):");

        jLabelCriacaoProcesso.setText("Intervalo de criação de processos (s):");

        jLabelDuracaoProcesso.setText("Duração de um processo (s):");

        jButtonIniciar.setText("Iniciar");
        jButtonIniciar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonIniciarActionPerformed(evt);
            }
        });

        jLabel24.setText("[");

        jLabel22.setText("]");

        jLabel25.setText(",");

        jLabel32.setText(",");

        jLabel28.setText(",");

        jLabel29.setText("[");

        jLabel30.setText("]");

        jLabel27.setText("]");

        jLabel31.setText("[");

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Desenvolvedores"));

        jLabelFranklin.setText("Franklin Vinicius Ribeiro Araujo");

        jLabelMarcelo.setText("Marcelo Silveira dos Santos");

        jLabel38.setText("Ulysses Alessandro Couto Rocha");

        jLabelSistemasOperacionais.setText("Disciplina: Sistemas Operacionais");

        jLabelEngenhariaDeComputaçãoIFCE.setText("Engenharia de Computação IFCE");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelFranklin, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabelMarcelo, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel38, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(74, 74, 74)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelEngenhariaDeComputaçãoIFCE)
                    .addComponent(jLabelSistemasOperacionais))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabelFranklin)
                .addGap(5, 5, 5)
                .addComponent(jLabelMarcelo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel38)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(jLabelSistemasOperacionais)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelEngenhariaDeComputaçãoIFCE))
        );

        javax.swing.GroupLayout jDialogInicioLayout = new javax.swing.GroupLayout(jDialogInicio.getContentPane());
        jDialogInicio.getContentPane().setLayout(jDialogInicioLayout);
        jDialogInicioLayout.setHorizontalGroup(
            jDialogInicioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDialogInicioLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabelImagemMemoria))
            .addGroup(jDialogInicioLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jDialogInicioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jDialogInicioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jDialogInicioLayout.createSequentialGroup()
                            .addGap(14, 14, 14)
                            .addComponent(jLabelCriacaoProcesso)
                            .addGap(5, 5, 5)
                            .addComponent(jLabel29)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jTextFieldCriacaoProcessoMin, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel28)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jTextFieldCriacaoProcessoMax, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel27))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDialogInicioLayout.createSequentialGroup()
                            .addComponent(jLabelDuracaoProcesso)
                            .addGap(5, 5, 5)
                            .addComponent(jLabel31)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jTextFieldDuracaoProcessoMin, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel32)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jTextFieldDuracaoProcessoMax, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel30))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDialogInicioLayout.createSequentialGroup()
                            .addGroup(jDialogInicioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jDialogInicioLayout.createSequentialGroup()
                                    .addGroup(jDialogInicioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabelMemoriaSO)
                                        .addComponent(jLabelTamanhoDaMemoria)
                                        .addComponent(jLabelMemoriaProcesso))
                                    .addGap(5, 5, 5)
                                    .addGroup(jDialogInicioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(jDialogInicioLayout.createSequentialGroup()
                                            .addComponent(jLabel24)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jTextFieldMemoriaProcessoMin, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jTextFieldMemoriaSO, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jTextFieldTamanhoDaMemoria, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(jDialogInicioLayout.createSequentialGroup()
                                    .addComponent(jLabelNumeroDeProcessos)
                                    .addGap(18, 18, 18)
                                    .addComponent(jTextFieldNumeroDeProcessos, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel25)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jTextFieldMemoriaProcessoMax, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel22)))
                    .addComponent(jButtonIniciar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jDialogInicioLayout.setVerticalGroup(
            jDialogInicioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDialogInicioLayout.createSequentialGroup()
                .addComponent(jLabelImagemMemoria)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jDialogInicioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelNumeroDeProcessos)
                    .addComponent(jTextFieldNumeroDeProcessos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jDialogInicioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelTamanhoDaMemoria)
                    .addComponent(jTextFieldTamanhoDaMemoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jDialogInicioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelMemoriaSO)
                    .addComponent(jTextFieldMemoriaSO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jDialogInicioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldMemoriaProcessoMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25)
                    .addComponent(jTextFieldMemoriaProcessoMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24)
                    .addComponent(jLabel22)
                    .addComponent(jLabelMemoriaProcesso))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jDialogInicioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldCriacaoProcessoMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28)
                    .addComponent(jTextFieldCriacaoProcessoMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29)
                    .addComponent(jLabel27)
                    .addComponent(jLabelCriacaoProcesso))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jDialogInicioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldDuracaoProcessoMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32)
                    .addComponent(jTextFieldDuracaoProcessoMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31)
                    .addComponent(jLabel30)
                    .addComponent(jLabelDuracaoProcesso))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonIniciar, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jDialogInformacaoesDetalhadas.setAutoRequestFocus(false);
        jDialogInformacaoesDetalhadas.setMinimumSize(new java.awt.Dimension(600, 600));
        jDialogInformacaoesDetalhadas.setResizable(false);

        jLabel1.setText("First Fit");

        jTextPaneProcessosFirstFit1.setEditable(false);
        jScrollPane5.setViewportView(jTextPaneProcessosFirstFit1);

        jLabel2.setText("Next Fit");

        jTextPaneProcessosNextFit1.setEditable(false);
        jScrollPane3.setViewportView(jTextPaneProcessosNextFit1);

        jLabel3.setText("Best Fit");

        jTextPaneProcessosBestFit1.setEditable(false);
        jScrollPane2.setViewportView(jTextPaneProcessosBestFit1);

        jLabel4.setText("Worst Fit");

        jTextPaneProcessosWorstFit1.setEditable(false);
        jScrollPane4.setViewportView(jTextPaneProcessosWorstFit1);

        javax.swing.GroupLayout jDialogInformacaoesDetalhadasLayout = new javax.swing.GroupLayout(jDialogInformacaoesDetalhadas.getContentPane());
        jDialogInformacaoesDetalhadas.getContentPane().setLayout(jDialogInformacaoesDetalhadasLayout);
        jDialogInformacaoesDetalhadasLayout.setHorizontalGroup(
            jDialogInformacaoesDetalhadasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialogInformacaoesDetalhadasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDialogInformacaoesDetalhadasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addGroup(jDialogInformacaoesDetalhadasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 573, Short.MAX_VALUE)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)))
                .addContainerGap(217, Short.MAX_VALUE))
        );
        jDialogInformacaoesDetalhadasLayout.setVerticalGroup(
            jDialogInformacaoesDetalhadasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDialogInformacaoesDetalhadasLayout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addGap(8, 8, 8)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addGap(7, 7, 7)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                .addGap(29, 29, 29))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Sistemas Operacionais - Projeto II – Estratégias de Alocação de Memória");
        setMinimumSize(new java.awt.Dimension(1280, 670));

        jPanelFirstFit.setBorder(javax.swing.BorderFactory.createTitledBorder("First Fit"));

        jTextAreaEsperaFirstFit.setColumns(20);
        jTextAreaEsperaFirstFit.setRows(5);
        jScrollPaneEsperaFirstFit.setViewportView(jTextAreaEsperaFirstFit);

        jScrollPaneProcessosFirstFit.setViewportView(jTextPaneProcessosFirstFit);

        javax.swing.GroupLayout jPanelFirstFitLayout = new javax.swing.GroupLayout(jPanelFirstFit);
        jPanelFirstFit.setLayout(jPanelFirstFitLayout);
        jPanelFirstFitLayout.setHorizontalGroup(
            jPanelFirstFitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFirstFitLayout.createSequentialGroup()
                .addComponent(memoriaFirstFit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneProcessosFirstFit, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneEsperaFirstFit, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        jPanelFirstFitLayout.setVerticalGroup(
            jPanelFirstFitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFirstFitLayout.createSequentialGroup()
                .addGroup(jPanelFirstFitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPaneProcessosFirstFit, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(memoriaFirstFit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPaneEsperaFirstFit, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );

        jPanelNextFit.setBorder(javax.swing.BorderFactory.createTitledBorder("Next Fit"));

        jTextAreaEsperaNextFit.setColumns(20);
        jTextAreaEsperaNextFit.setRows(5);
        jScrollPaneEsperaNextFit.setViewportView(jTextAreaEsperaNextFit);

        jScrollPaneProcessosNextFit.setViewportView(jTextPaneProcessosNextFit);

        javax.swing.GroupLayout jPanelNextFitLayout = new javax.swing.GroupLayout(jPanelNextFit);
        jPanelNextFit.setLayout(jPanelNextFitLayout);
        jPanelNextFitLayout.setHorizontalGroup(
            jPanelNextFitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelNextFitLayout.createSequentialGroup()
                .addComponent(memoriaNextFit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneProcessosNextFit, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneEsperaNextFit, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        jPanelNextFitLayout.setVerticalGroup(
            jPanelNextFitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelNextFitLayout.createSequentialGroup()
                .addGroup(jPanelNextFitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPaneProcessosNextFit, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(memoriaNextFit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPaneEsperaNextFit, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );

        jPanelBestFit.setBorder(javax.swing.BorderFactory.createTitledBorder("Best Fit"));

        jTextAreaEsperaBestFit.setColumns(20);
        jTextAreaEsperaBestFit.setRows(5);
        jScrollPaneEsperaBestFit.setViewportView(jTextAreaEsperaBestFit);

        jScrollPaneProcessosBestFit.setViewportView(jTextPaneProcessosBestFit);

        javax.swing.GroupLayout jPanelBestFitLayout = new javax.swing.GroupLayout(jPanelBestFit);
        jPanelBestFit.setLayout(jPanelBestFitLayout);
        jPanelBestFitLayout.setHorizontalGroup(
            jPanelBestFitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBestFitLayout.createSequentialGroup()
                .addComponent(memoriaBestFit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneProcessosBestFit, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneEsperaBestFit, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        jPanelBestFitLayout.setVerticalGroup(
            jPanelBestFitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBestFitLayout.createSequentialGroup()
                .addGroup(jPanelBestFitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPaneProcessosBestFit, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(memoriaBestFit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPaneEsperaBestFit, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );

        jPanelWorstFit.setBorder(javax.swing.BorderFactory.createTitledBorder("Worst Fit"));

        jTextAreaEsperaWorstFit.setColumns(20);
        jTextAreaEsperaWorstFit.setRows(5);
        jScrollPaneEsperaWorstFit.setViewportView(jTextAreaEsperaWorstFit);

        jScrollPaneProcessosWorstFit.setViewportView(jTextPaneProcessosWorstFit);

        javax.swing.GroupLayout jPanelWorstFitLayout = new javax.swing.GroupLayout(jPanelWorstFit);
        jPanelWorstFit.setLayout(jPanelWorstFitLayout);
        jPanelWorstFitLayout.setHorizontalGroup(
            jPanelWorstFitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelWorstFitLayout.createSequentialGroup()
                .addComponent(memoriaWorstFit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneProcessosWorstFit, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneEsperaWorstFit, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        jPanelWorstFitLayout.setVerticalGroup(
            jPanelWorstFitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelWorstFitLayout.createSequentialGroup()
                .addGroup(jPanelWorstFitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPaneProcessosWorstFit, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(memoriaWorstFit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPaneEsperaWorstFit, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );

        jPanelControle.setBorder(javax.swing.BorderFactory.createTitledBorder("Controle"));

        jSliderVelocidade.setMaximum(10);
        jSliderVelocidade.setMinimum(1);
        jSliderVelocidade.setToolTipText("");
        jSliderVelocidade.setValue(1);
        jSliderVelocidade.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSliderVelocidadeMouseReleased(evt);
            }
        });

        jLabelVelocidade.setText("Velocidade Simulação:");

        jLabel1x.setText("1X");

        jLabel10x.setText("10x");

        jButtonPasso.setText("Passo");
        jButtonPasso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPassoActionPerformed(evt);
            }
        });

        jButtonConcluir.setText("Concluir");
        jButtonConcluir.setPreferredSize(new java.awt.Dimension(117, 23));
        jButtonConcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConcluirActionPerformed(evt);
            }
        });

        jButtonNovaSimulacao.setText("Nova Simulação");
        jButtonNovaSimulacao.setPreferredSize(new java.awt.Dimension(117, 23));
        jButtonNovaSimulacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNovaSimulacaoActionPerformed(evt);
            }
        });

        jToggleButton1.setText("Pausar/Continuar");
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelControleLayout = new javax.swing.GroupLayout(jPanelControle);
        jPanelControle.setLayout(jPanelControleLayout);
        jPanelControleLayout.setHorizontalGroup(
            jPanelControleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelControleLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanelControleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelControleLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel1x)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSliderVelocidade, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10x)
                        .addGap(4, 4, 4))
                    .addGroup(jPanelControleLayout.createSequentialGroup()
                        .addGroup(jPanelControleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanelControleLayout.createSequentialGroup()
                                .addComponent(jToggleButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButtonPasso, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanelControleLayout.createSequentialGroup()
                                .addComponent(jButtonNovaSimulacao, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButtonConcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(5, 5, 5))))
            .addGroup(jPanelControleLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelVelocidade)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanelControleLayout.setVerticalGroup(
            jPanelControleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelControleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelControleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonConcluir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonNovaSimulacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelVelocidade)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelControleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSliderVelocidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1x)
                    .addComponent(jLabel10x))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelControleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonPasso)
                    .addComponent(jToggleButton1))
                .addContainerGap())
        );

        jPanelEntrada.setBorder(javax.swing.BorderFactory.createTitledBorder("Resumo Entrada"));

        jTextAreaEntrada.setColumns(20);
        jTextAreaEntrada.setRows(5);
        jScrollPaneEntrada.setViewportView(jTextAreaEntrada);

        javax.swing.GroupLayout jPanelEntradaLayout = new javax.swing.GroupLayout(jPanelEntrada);
        jPanelEntrada.setLayout(jPanelEntradaLayout);
        jPanelEntradaLayout.setHorizontalGroup(
            jPanelEntradaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPaneEntrada)
        );
        jPanelEntradaLayout.setVerticalGroup(
            jPanelEntradaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPaneEntrada, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
        );

        jPanelSaida.setBorder(javax.swing.BorderFactory.createTitledBorder("Resumo Saída"));

        jTextAreaSaida.setColumns(20);
        jTextAreaSaida.setRows(5);
        jTextAreaSaida.setText("Quando terminar exibir o \ndesempenho de cada algoritmo");
        jScrollPaneSaida.setViewportView(jTextAreaSaida);

        jButton1.setText("Informações Detalhadas");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelSaidaLayout = new javax.swing.GroupLayout(jPanelSaida);
        jPanelSaida.setLayout(jPanelSaidaLayout);
        jPanelSaidaLayout.setHorizontalGroup(
            jPanelSaidaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPaneSaida)
            .addGroup(jPanelSaidaLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jButton1))
        );
        jPanelSaidaLayout.setVerticalGroup(
            jPanelSaidaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSaidaLayout.createSequentialGroup()
                .addComponent(jScrollPaneSaida, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanelWorstFit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanelSaida, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanelNextFit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanelFirstFit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanelBestFit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, 0)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanelEntrada, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanelControle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanelFirstFit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelControle, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanelNextFit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanelBestFit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanelEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelWorstFit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanelSaida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 23, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void jButtonIniciarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonIniciarActionPerformed
        try {
            intervaloDuracaoProcesso[1] = Integer.parseInt(jTextFieldDuracaoProcessoMax.getText());
            intervaloDuracaoProcesso[0] = Integer.parseInt(jTextFieldDuracaoProcessoMin.getText());
            intervaloCriacaoProcesso[1] = Integer.parseInt(jTextFieldCriacaoProcessoMax.getText());
            intervaloCriacaoProcesso[0] = Integer.parseInt(jTextFieldCriacaoProcessoMin.getText());
            memoriaProcesso[1] = Integer.parseInt(jTextFieldMemoriaProcessoMax.getText());
            memoriaProcesso[0] = Integer.parseInt(jTextFieldMemoriaProcessoMin.getText());    
            tamanhoMemoriaSO = Integer.parseInt(jTextFieldMemoriaSO.getText());
            tamanhoMemoria = Integer.parseInt(jTextFieldTamanhoDaMemoria.getText());
            NumeroDeProcessos = Integer.parseInt(jTextFieldNumeroDeProcessos.getText());
        } catch (NumberFormatException e) {
            return;
        }
        
        jLabelNumeroDeProcessos.setForeground(Color.black);
        jLabelTamanhoDaMemoria.setForeground(Color.black);
        jLabelMemoriaSO.setForeground(Color.black);
        jLabelMemoriaProcesso.setForeground(Color.black);
        jLabelDuracaoProcesso.setForeground(Color.black);
        jLabelCriacaoProcesso.setForeground(Color.black);
        
        if(NumeroDeProcessos <= 0) {
            jLabelNumeroDeProcessos.setForeground(Color.red);
            return;
        }
        
        if(tamanhoMemoria <= 0) {
            jLabelTamanhoDaMemoria.setForeground(Color.red);
            return;
        }
        
        if(tamanhoMemoriaSO <= 0) {
            jLabelMemoriaSO.setForeground(Color.red);
            return;
        }
        
        if(tamanhoMemoriaSO > tamanhoMemoria) {
            jLabelTamanhoDaMemoria.setForeground(Color.red);
            jLabelMemoriaSO.setForeground(Color.red);
            return;
        }
        
        if(memoriaProcesso[0] <= 0 || memoriaProcesso[1] <= 0 || memoriaProcesso[0] > memoriaProcesso[1]) {
            jLabelMemoriaProcesso.setForeground(Color.red);
            return;
        }
        
        if(memoriaProcesso[1] > tamanhoMemoria - tamanhoMemoriaSO) {
            jLabelTamanhoDaMemoria.setForeground(Color.black);
            jLabelMemoriaProcesso.setForeground(Color.black);
            jLabelMemoriaSO.setForeground(Color.black);
            return;
        } 
        
        if(intervaloCriacaoProcesso[0] < 0 || intervaloCriacaoProcesso[1] < 0 || intervaloCriacaoProcesso[0] > intervaloCriacaoProcesso[1]) {
            jLabelCriacaoProcesso.setForeground(Color.red);
            return;
        }
        
        if(intervaloDuracaoProcesso[0] <= 0 || intervaloDuracaoProcesso[1] <= 0 || intervaloDuracaoProcesso[0] > intervaloDuracaoProcesso[1]) {
            jLabelDuracaoProcesso.setForeground(Color.red);
            return;
        }
        
        initVariaveis();
                
        memoria[0] = new MemoriaFirstFit(tamanhoMemoria, tamanhoMemoriaSO);
        memoria[1] = new MemoriaNextFit(tamanhoMemoria, tamanhoMemoriaSO);
        memoria[2] = new MemoriaBestFit(tamanhoMemoria, tamanhoMemoriaSO);   
        memoria[3] = new MemoriaWorstFit(tamanhoMemoria, tamanhoMemoriaSO);

        tempoCriacaoProcesso = new int[NumeroDeProcessos];
        tempoCriacaoProcesso[0] = intervaloCriacaoProcesso[0] + random.nextInt(
                intervaloCriacaoProcesso[1] - intervaloCriacaoProcesso[0] + 1);
        for(int i= 1; i< NumeroDeProcessos; i++) 
            tempoCriacaoProcesso[i] = tempoCriacaoProcesso[i-1] + intervaloCriacaoProcesso[0] +
                    random.nextInt(intervaloCriacaoProcesso[1] -
                            intervaloCriacaoProcesso[0] + 1);
        
        cleanAll();
        jDialogInicio.setVisible(false);
    }//GEN-LAST:event_jButtonIniciarActionPerformed

    private void jButtonPassoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPassoActionPerformed
        if(parar && !(finalizado[0] && finalizado[1] && finalizado[2] && finalizado[3]))
            clock();
    }//GEN-LAST:event_jButtonPassoActionPerformed

    private void jButtonNovaSimulacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNovaSimulacaoActionPerformed
        timer.cancel();
        Processo.resetTEMPO();
        initVariaveis();
        jDialogInicio.setVisible(true);
    }//GEN-LAST:event_jButtonNovaSimulacaoActionPerformed

    private void jDialogInicioWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_jDialogInicioWindowClosing
       System.exit(0);
    }//GEN-LAST:event_jDialogInicioWindowClosing

    private void jButtonConcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConcluirActionPerformed
        timer.cancel();
        esperar = false;
        while(!(finalizado[0] && finalizado[1] && finalizado[2] && finalizado[3]))
            clock();  
        updateGUI();
    }//GEN-LAST:event_jButtonConcluirActionPerformed

    private void jSliderVelocidadeMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSliderVelocidadeMouseReleased
        if(!parar) {
            timer.cancel();
            timer = new Timer();
            timer.schedule(new Simulacao(this), 0, tempoPasso/jSliderVelocidade.getValue());
        }
    }//GEN-LAST:event_jSliderVelocidadeMouseReleased

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        if(esperar) {
            parar = !parar;
            if(!parar) 
                timer.schedule(new Simulacao(this), 0, tempoPasso/jSliderVelocidade.getValue());    
            else{
                timer.cancel();
                timer = new Timer();
            }
        }
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if(!jDialogInformacaoesDetalhadas.isVisible())
            jDialogInformacaoesDetalhadas.setVisible(true); 
        else
            jDialogInformacaoesDetalhadas.setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    
    
       private void mostraProcessos(){
       JTextPane[] smemorias = new JTextPane[4];
        int i;
        smemorias[0]=jTextPaneProcessosFirstFit1;
        smemorias[1]=jTextPaneProcessosNextFit1;
        smemorias[2]=jTextPaneProcessosBestFit1;
        smemorias[3]=jTextPaneProcessosWorstFit1;
       
        for(i=0;i<4;i++){
            StringBuffer sb = new StringBuffer();
            for(Processo p : processo[i]){
                if(p.isFinalizado())
                    sb.append(p.getNome().toString() +"  Tamanho "+p.getTamanhoMemoria()+" MB"+"  Finalizado "+"  Tempo Criação "+ p.getTempoCriacao() +"  Tempo de Espera "+ (p.getTempoAlocacao()-p.getTempoCriacao()) +"  Tempo Finalização "+p.getTempoFinalizacao() +"\n");
                else
                    if(p.isAlocado())
                        sb.append(p.getNome().toString() +"  Tamanho "+p.getTamanhoMemoria()+" MB"+ "  Em Execução "+ "  Tempo Criação "+ p.getTempoCriacao()+"  Tempo de Espera "+ (p.getTempoAlocacao()-p.getTempoCriacao())+"\n");
                    else
                        sb.append(p.getNome().toString() +"  Tamanho "+p.getTamanhoMemoria()+" MB"+ "  Em Espera "+"  Tempo Criação "+ p.getTempoCriacao() +"\n");
            }
            smemorias[i].setText(sb.toString());
        }
        
   }

    
    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Principal().setVisible(true);
            }
        });
        
        
        
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButtonConcluir;
    private javax.swing.JButton jButtonIniciar;
    private javax.swing.JButton jButtonNovaSimulacao;
    private javax.swing.JButton jButtonPasso;
    private javax.swing.JDialog jDialogInformacaoesDetalhadas;
    private javax.swing.JDialog jDialogInicio;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10x;
    private javax.swing.JLabel jLabel1x;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabelCriacaoProcesso;
    private javax.swing.JLabel jLabelDuracaoProcesso;
    private javax.swing.JLabel jLabelEngenhariaDeComputaçãoIFCE;
    private javax.swing.JLabel jLabelFranklin;
    private javax.swing.JLabel jLabelImagemMemoria;
    private javax.swing.JLabel jLabelMarcelo;
    private javax.swing.JLabel jLabelMemoriaProcesso;
    private javax.swing.JLabel jLabelMemoriaSO;
    private javax.swing.JLabel jLabelNumeroDeProcessos;
    private javax.swing.JLabel jLabelSistemasOperacionais;
    private javax.swing.JLabel jLabelTamanhoDaMemoria;
    private javax.swing.JLabel jLabelVelocidade;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanelBestFit;
    private javax.swing.JPanel jPanelControle;
    private javax.swing.JPanel jPanelEntrada;
    private javax.swing.JPanel jPanelFirstFit;
    private javax.swing.JPanel jPanelNextFit;
    private javax.swing.JPanel jPanelSaida;
    private javax.swing.JPanel jPanelWorstFit;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPaneEntrada;
    private javax.swing.JScrollPane jScrollPaneEsperaBestFit;
    private javax.swing.JScrollPane jScrollPaneEsperaFirstFit;
    private javax.swing.JScrollPane jScrollPaneEsperaNextFit;
    private javax.swing.JScrollPane jScrollPaneEsperaWorstFit;
    private javax.swing.JScrollPane jScrollPaneProcessosBestFit;
    private javax.swing.JScrollPane jScrollPaneProcessosFirstFit;
    private javax.swing.JScrollPane jScrollPaneProcessosNextFit;
    private javax.swing.JScrollPane jScrollPaneProcessosWorstFit;
    private javax.swing.JScrollPane jScrollPaneSaida;
    private javax.swing.JSlider jSliderVelocidade;
    private javax.swing.JTextArea jTextAreaEntrada;
    private javax.swing.JTextArea jTextAreaEsperaBestFit;
    private javax.swing.JTextArea jTextAreaEsperaFirstFit;
    private javax.swing.JTextArea jTextAreaEsperaNextFit;
    private javax.swing.JTextArea jTextAreaEsperaWorstFit;
    private javax.swing.JTextArea jTextAreaSaida;
    private javax.swing.JTextField jTextFieldCriacaoProcessoMax;
    private javax.swing.JTextField jTextFieldCriacaoProcessoMin;
    private javax.swing.JTextField jTextFieldDuracaoProcessoMax;
    private javax.swing.JTextField jTextFieldDuracaoProcessoMin;
    private javax.swing.JTextField jTextFieldMemoriaProcessoMax;
    private javax.swing.JTextField jTextFieldMemoriaProcessoMin;
    private javax.swing.JTextField jTextFieldMemoriaSO;
    private javax.swing.JTextField jTextFieldNumeroDeProcessos;
    private javax.swing.JTextField jTextFieldTamanhoDaMemoria;
    private javax.swing.JTextPane jTextPaneProcessosBestFit;
    private javax.swing.JTextPane jTextPaneProcessosBestFit1;
    private javax.swing.JTextPane jTextPaneProcessosFirstFit;
    private javax.swing.JTextPane jTextPaneProcessosFirstFit1;
    private javax.swing.JTextPane jTextPaneProcessosNextFit;
    private javax.swing.JTextPane jTextPaneProcessosNextFit1;
    private javax.swing.JTextPane jTextPaneProcessosWorstFit;
    private javax.swing.JTextPane jTextPaneProcessosWorstFit1;
    private javax.swing.JToggleButton jToggleButton1;
    private com.marcelosilveira.gerenciadorDeMemoria.gui.Memoria memoriaBestFit;
    private com.marcelosilveira.gerenciadorDeMemoria.gui.Memoria memoriaFirstFit;
    private com.marcelosilveira.gerenciadorDeMemoria.gui.Memoria memoriaNextFit;
    private com.marcelosilveira.gerenciadorDeMemoria.gui.Memoria memoriaWorstFit;
    // End of variables declaration//GEN-END:variables

    private class Simulacao extends TimerTask {
        private Principal p;
        
        public Simulacao(Principal p) {
            this.p = p;
        }
        
        @Override
        public void run() {
            p.clock();        
        }
    }
}
