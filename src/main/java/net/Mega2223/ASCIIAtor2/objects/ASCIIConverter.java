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

    public static final String VALUES[] = {DARKER, NOT_QUITE_DARK, AVERAGE, NOT_QUITE_LIGHT, LIGHT};
    public static final int[] TOLERANCES = {255 / 5 * 1, 255 / 5 * 2, 255 / 5 * 3, 255 / 5 * 4, 255 / 5 * 5};

    final ArrayList<ASCIIListener> runnables = new ArrayList<>();
    protected String ret;
    int itnerations = 0;

    public ASCIIConverter() {
    }

    public static String convertToASCII(BufferedImage image) {

        return convertToASCII(image, new Dimension(image.getWidth(), image.getHeight()), true, false, TOLERANCES, VALUES, true);
    }

    public static String convertToASCII(BufferedImage image, Dimension dim, boolean considerAlpha, boolean darkMode, int[] tolerances, String[] values,boolean clearRunnables) {
        ASCIIConverter converter = new ASCIIConverter();
        converter.convert(image, dim, considerAlpha, darkMode, tolerances,values,clearRunnables);
        while (!converter.isComplete()){}
        return converter.getString();
    }

    public static String getAdequateString(int lvl, boolean darkMode, String[] VALUES) {
        if (!darkMode) {
            return VALUES[lvl];
        } else {
            //System.out.println("v:"+VALUES[(4 - lvl)]);
            return VALUES[(4 - lvl)];
        }
    }
    private boolean isComplete = true;
    public void convert(BufferedImage image, Dimension dimension, boolean considerAlpha, boolean darkMode, int[] tolerances, String[] values, boolean clearRunnables) {

        Dimension dim = (Dimension) dimension.clone();
        Dimension imgDim = new Dimension(image.getWidth(), image.getHeight());

        double defaultXMultiplier = imgDim.width / dim.width;
        double defaultYMultiplier = imgDim.height / dim.height;

        //System.out.println("DADOS DA IMAGEM: [W:" + imgDim.getWidth() + " H:" + imgDim.getHeight() + "]");
        //System.out.println("DA DIMENÇÃO DESEJADA: [W:" + dim.getWidth() + " H:" + dim.getHeight() + "]");
        //System.out.println("AJUSTADORES DE PROPORÇÃO: [X:" + defaultXMultiplier + " Y:" + defaultYMultiplier + "]");
        
        if(!isComplete){return;}
        
        isComplete = false;
        Thread thread = new Thread(() -> {
            ret = "";
            isComplete = false;
            for (int y = 0; y < dim.height; y++) {
                for (int x = 0; x < dim.width; x++) {
                    //System.out.println(x+":"+y);
                    int avgValue;
                    Color actolor = Color.decode(String.valueOf(image.getRGB((int) (x*defaultXMultiplier), (int) (y*defaultYMultiplier))));
                    avgValue = actolor.getBlue() + actolor.getGreen() + actolor.getRed();
                    avgValue = avgValue / 3;
                    //System.out.println(actolor.getRed() +"|"+ actolor.getGreen() +"|"+ actolor.getBlue()+"|"+avgValue);

                    if (avgValue < tolerances[0]) {ret = ret + getAdequateString(0,darkMode,values);
                    } else if (avgValue < tolerances[1]) { ret = ret + getAdequateString(1,darkMode,values);
                    } else if (avgValue < tolerances[2]) { ret = ret + getAdequateString(2,darkMode,values);
                    } else if (avgValue < tolerances[3]) { ret = ret + getAdequateString(3,darkMode,values);
                    } else { ret = ret + getAdequateString(4,darkMode,values);
                    }
                    onStringChange();
                }
                ret = ret + "\n";

                onStringChange();
            }
            this.isComplete = true;
            onStringChange();
            if(clearRunnables){runnables.clear();}
            return;
        });

        thread.start();
        itnerations = 0;

        return;

    }

    public void addRunnable(ASCIIListener r) {
        runnables.add(r);
    }

    @Deprecated
    public void testRunnables() {
        System.out.println("RUNNABLELISTSIZE: " + runnables.size());
    }

    public ArrayList getRunnables() {
        return runnables;
    }

    /*public static class exampleClass{
        public final List<ASCIIListener> listeners;

        public static void main(String args[]){
            exampleClass exampleClass = new exampleClass();
            for (int i = 0; i < 100; i++) {
                exampleClass.add(() -> {
                    System.out.println("testClass");
                });
            }
        }
        public exampleClass(){
            listeners = new ArrayList<>();
        }
        public synchronized void add(ASCIIListener wh){
            listeners.add(wh);
            System.out.println("ADD: " + listeners.size());
        }

    }*/

    public void cleanRunnables() {
        runnables.clear();
    }

    public int getItnerations() {
        return itnerations;
    }

    public void onStringChange() {
        itnerations++;
        for (ASCIIListener ac : this.runnables) {
            Thread thread = new Thread(() -> ac.run());
            thread.start();
        }
    }

    public String getString() {
        return ret;
    }

    public void convert(BufferedImage image) {

        this.convert(image, new Dimension(image.getWidth(), image.getHeight()), true, false, TOLERANCES, VALUES,true);
    }

    public boolean isComplete() {
        return isComplete;
    }


    public static interface ASCIIListener {
        public void run();
    }


}
