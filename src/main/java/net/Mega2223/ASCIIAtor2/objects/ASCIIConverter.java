package net.Mega2223.ASCIIAtor2.objects;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class ASCIIConverter {

    public static final String DARKER = "@@";
    public static final String NOT_QUITE_DARK = "##";
    public static final String AVERAGE = "**";
    public static final String NOT_QUITE_LIGHT = "==";
    public static final String LIGHT = "--";

    public static final String[] VALUES = {DARKER, NOT_QUITE_DARK, AVERAGE, NOT_QUITE_LIGHT, LIGHT};
    public static final int[] TOLERANCES = {255 / 5, 255 / 5 * 2, 255 / 5 * 3, 255 / 5 * 4, 255 / 5 * 5};

    final ArrayList<ASCIIListener> runnables = new ArrayList<>();
    protected String ret;
    int itnerations = 0;

    public ASCIIConverter() {
    }

    /** Converte a Imagem em um ASCII em escala 1:1, criando uma instância dessa classe, fazendo a imagem e dispensando a instância
     * @param image: Imagem pra converter
     * */
    public static String convertToASCII(BufferedImage image) {
        return convertToASCII(image, new Dimension(image.getWidth(), image.getHeight()), true, false, TOLERANCES, VALUES);
    }
    /** Converte a Imagem em um ASCII com os argumentos especificados, criando uma instância dessa classe, fazendo a imagem e dispensando a instância
     * @param image: Imagem pra converter
     * @param dim: Dimenções do ASCII
     * @param considerAlpha: Considerar o canal Alpha
     * @param darkMode: Fazer do mais claro para o escuro ou do mais escuro para o claro
     * @param tolerances: Tolerâncias para serem ultilizadas
     * @param values: Strings as quais as tolerâncias farão referência
     * */
    public static String convertToASCII(BufferedImage image, Dimension dim, boolean considerAlpha, boolean darkMode, int[] tolerances, String[] values) {
        ASCIIConverter converter = new ASCIIConverter();
        final boolean[] legal = {false};
        converter.addASCIIListener(new ASCIIListener() {
            @Override
            public void onStringChange() {}

            @Override
            public void onInterrupt() {
                throw new RuntimeException("Foi interrompido");
            }

            @Override
            public void onEnd() {
                legal[0] =true;
            }
        });
        converter.convert(image, dim, considerAlpha, darkMode, tolerances,values,true);
        while (!legal[0]);
        return converter.getString();
    }

    /** Dá a string adequada conforme os parâmetros, para a formação de uma imagem ASCII
     * @param lvl Nível da String
     * @param darkMode Darkmode
     * @param VALUES Valores de referência
     * */
    public static String getAdequateString(int lvl, boolean darkMode, String[] VALUES) {
        if (!darkMode) {
            return VALUES[lvl];
        } else {

            return VALUES[(VALUES.length - 1 - lvl)];
        }
    }
    private boolean isComplete = true;
    private Thread thread;
    /** Converte a Imagem em um ASCII com os argumentos especificados, criando uma instância dessa classe, fazendo a imagem e dispensando a instância
     * @param image: Imagem pra converter
     * @param dimension: Dimenções do ASCII
     * @param considerAlpha: Considerar o canal Alpha
     * @param darkMode: Fazer do mais claro para o escuro ou do mais escuro para o claro
     * @param tolerances: Tolerâncias para serem ultilizadas
     * @param values: Strings as quais as tolerâncias farão referência
     * @param clearRunnables: Se o programa deve remover todas as suas ASCIIRunnables após a execução do código
     * */
    public void convert(BufferedImage image, Dimension dimension, boolean considerAlpha, boolean darkMode, int[] tolerances, String[] values, boolean clearRunnables) {

        Dimension dim = (Dimension) dimension.clone();
        Dimension imgDim = new Dimension(image.getWidth(), image.getHeight());

        double defaultXMultiplier = ((double)imgDim.width) / ((double) dim.width);
        double defaultYMultiplier = ((double) imgDim.height) /((double) dim.height);

        if(!isComplete){return;}
        
        isComplete = false;
        thread = new Thread(() -> {
            ret = "";
            isComplete = false;
            for (int y = 0; y < dim.height; y++) {
                for (int x = 0; x < dim.width; x++) {

                    int avgValue;
                    Color actolor = Color.decode(String.valueOf(image.getRGB((int) (x * defaultXMultiplier), (int) (y * defaultYMultiplier))));
                    avgValue = actolor.getBlue() + actolor.getGreen() + actolor.getRed();
                    avgValue = avgValue / 3;
                    int lvl = 0;
                    for (int act : tolerances){
                       if(avgValue < act){ret = ret + getAdequateString(lvl, darkMode, values);break;}
                       lvl++;
                       if(lvl >= tolerances.length - 1){ret = ret + getAdequateString(lvl, darkMode, values);}
                    }

                    ASCIIConverter.this.quickStringChange();
                }
                ret = ret + "\n";

                ASCIIConverter.this.quickStringChange();
            }
            ASCIIConverter.this.isComplete = true;
            ASCIIConverter.this.quickStringChange();
            ASCIIConverter.this.quickOnEnd();
            if (clearRunnables) {
                runnables.clear();
            }

        });

        thread.start();
        itnerations = 0;

    }
    /**Adiciona um ASCIIListener para ser executado*/
    public void addASCIIListener(ASCIIListener r) {
        runnables.add(r);
    }
    /**Interrompe a renderização do ASCII e ativa o método onInterrupt em todos os ASCIIListeners adicionaddos*/
    public void interrupt(){
        thread.interrupt();
        for (ASCIIListener ac : this.runnables) {
            ac.onInterrupt();
        }
    }

    @Deprecated
    /**Printa o tamanho da lista de ASCIIListeners, para propósitos de Debug*/
    public void testASCIIListeners() {
        System.out.println("RUNNABLELISTSIZE: " + runnables.size());
    }
    /**Dá uma CÓPIA dos ASCIIListeners*/
    public ArrayList<ASCIIListener> getASCIIListeners() {
        return (ArrayList<ASCIIListener>) runnables.clone();
    }

    /**Limpa a lista de ASCIIListeners*/
    public void cleanASCIIListeners() {
        runnables.clear();
    }
    /**Dá o número de itnerações atual, o número de itnerações esperadas no total sendo (Largura*Altura)+Altura, considerando que não hajam interrupções*/
    public int getItnerations() {
        return itnerations;
    }

    protected void quickStringChange() {
        itnerations++;
        for (ASCIIListener ac : this.runnables) {
            Thread thread = new Thread(ac::onStringChange);
            thread.start();
        }
    }

    protected void quickOnEnd(){
        for (ASCIIListener ac : this.runnables) {
            Thread thread = new Thread(ac::onEnd);
            thread.start();
        }
    }
    /**Retorna a String em sua atual forma, como a renderização é feita em uma Thread separada, ela pode não estar completa, verifique com isComplete() se a String está pronta ou não*/
    public String getString() {
        return ret;
    }

    /**Faz uma conversão básica da imagem pra um texto em escala 1:1*/
    public void convert(BufferedImage image) {

        this.convert(image, new Dimension(image.getWidth(), image.getHeight()), true, false, TOLERANCES, VALUES,true);
    }
    /**Se o processo de renderização da String já acabou*/
    public boolean isComplete() {
        return isComplete;
    }

    public static class RenderingInterruptedException extends Throwable {

    }
    /**Interface para ser implementada no ASCIIConverter, pra execução em determinados períodos no processo renderização do ASCII
     * */
    public interface ASCIIListener {
        void onStringChange();
        default void onInterrupt(){}

        default void onEnd(){}
    }


}
