package net.Mega2223.ASCIIAtor2.objects;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ASCIIConverter {

    public static final String DARKER = "@@";
    public static final String NOT_QUITE_DARK = "##";
    public static final String AVERAGE = "**";
    public static final String NOT_QUITE_LIGHT = "==";
    public static final String LIGHT = "--";

    public static final String VALUES[] = {DARKER, NOT_QUITE_DARK, AVERAGE, NOT_QUITE_LIGHT, LIGHT};

    public ASCIIConverter(){}

    String ret = "";
    int itnerations = 0;
    public String convert(BufferedImage image, Dimension dim, boolean considerAlpha, boolean darkMode, double[] tolerances){
        Dimension imgDim = new Dimension(image.getWidth(), image.getHeight());

        double defaultXMultiplier = imgDim.width/ dim.width;
        double defaultYMultiplier = imgDim.height/ dim.height;

        Thread thread = new Thread(() -> {
            for (int y = 0; y < dim.height; y++) {
                for (int x = 0; x < dim.width; x++) {
                    System.out.println("x: " + x + " y: " + y);
                    int avgValue;
                    Color actolor = Color.decode(String.valueOf(image.getRGB((int) (x*defaultXMultiplier), (int) (y*defaultYMultiplier))));
                    avgValue = actolor.getBlue() + actolor.getGreen() + actolor.getRed();
                    avgValue = avgValue / 3;
                    double avgEz = avgValue / 255;
                    if (avgEz > tolerances[0]) { ret = ret + getAdequateString(0,darkMode);
                    } else if (avgEz > tolerances[1]) { ret = ret + getAdequateString(1,darkMode);
                    } else if (avgEz > tolerances[2]) { ret = ret + getAdequateString(2,darkMode);
                    } else if (avgEz > tolerances[3]) { ret = ret + getAdequateString(3,darkMode);
                    } else { ret = ret + getAdequateString(4,darkMode);
                    }
                    onStringChange();
                }
                ret = ret + "\n";
                onStringChange();
            }
        });

        thread.start();
        String retor = ret;
        ret = "";
        itnerations = 0;
        return retor;

    }



    final ArrayList<ASCIIListener> runnables = new ArrayList<>();

    public void addRunnable(ASCIIListener r){
        runnables.add(r);
    }
    @Deprecated
    public void testRunnables(){
        System.out.println("RUNNABLELISTSIZE: " + runnables.size());
    }
    public ArrayList getRunnables(){
        return runnables;
    }
    public void cleanRunnables(){
        runnables.clear();
    }

    public int getItnerations(){
        return itnerations;
    }

    public void onStringChange(){
        itnerations++;
        for(ASCIIListener ac : this.runnables){
            ac.run();
        }
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

    public String getString(){return ret;}

    public String convert(BufferedImage image) {
        double[] tolerance = {1/5*1,1/5*2,1/5*3,1/5*4,1/5*5};
        return this.convert(image, new Dimension(image.getWidth(), image.getHeight()), true, false, tolerance);
    }

    public static String convertToASCII(BufferedImage image) {
        double[] tolerance = {1/5*1,1/5*2,1/5*3,1/5*4,1/5*5};
        return convertToASCII(image, new Dimension(image.getWidth(), image.getHeight()), true, false, tolerance);
    }

    public static String convertToASCII(BufferedImage image, Dimension dim, boolean considerAlpha, boolean darkMode, double[] tolerances) {
        ASCIIConverter converter = new ASCIIConverter();
        return converter.convert(image,dim,considerAlpha,darkMode,tolerances);
    }

    public static String getAdequateString(int lvl, boolean darkMode) {
        if (!darkMode) {
            return VALUES[lvl];
        } else {
            return VALUES[(4 - lvl)];
        }
    }


    public static interface ASCIIListener{
        public void run();
    }





}
