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

    public static String convertToASCII(BufferedImage image) {

        return convertToASCII(image, new Dimension(image.getWidth(), image.getHeight()), true, false, TOLERANCES, VALUES, true);
    }

    public static String convertToASCII(BufferedImage image, Dimension dim, boolean considerAlpha, boolean darkMode, int[] tolerances, String[] values,boolean clearRunnables) {
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
        converter.convert(image, dim, considerAlpha, darkMode, tolerances,values,clearRunnables);
        while (!legal[0]);
        return converter.getString();
    }

    public static String getAdequateString(int lvl, boolean darkMode, String[] VALUES) {
        if (!darkMode) {
            return VALUES[lvl];
        } else {

            return VALUES[(4 - lvl)];
        }
    }
    private boolean isComplete = true;
    private Thread thread;
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
                    //System.out.println(x+":"+y);
                    int avgValue;
                    Color actolor = Color.decode(String.valueOf(image.getRGB((int) (x * defaultXMultiplier), (int) (y * defaultYMultiplier))));
                    avgValue = actolor.getBlue() + actolor.getGreen() + actolor.getRed();
                    avgValue = avgValue / 3;
                    //System.out.println(actolor.getRed() +"|"+ actolor.getGreen() +"|"+ actolor.getBlue()+"|"+avgValue);

                    if (avgValue < tolerances[0]) {
                        ret = ret + getAdequateString(0, darkMode, values);
                    } else if (avgValue < tolerances[1]) {
                        ret = ret + getAdequateString(1, darkMode, values);
                    } else if (avgValue < tolerances[2]) {
                        ret = ret + getAdequateString(2, darkMode, values);
                    } else if (avgValue < tolerances[3]) {
                        ret = ret + getAdequateString(3, darkMode, values);
                    } else {
                        ret = ret + getAdequateString(4, darkMode, values);
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

    public void addASCIIListener(ASCIIListener r) {
        runnables.add(r);
    }

    public void interrupt(){
        thread.interrupt();
        for (ASCIIListener ac : this.runnables) {
            ac.onInterrupt();
        }
    }

    @Deprecated
    public void testASCIIListeners() {
        System.out.println("RUNNABLELISTSIZE: " + runnables.size());
    }

    public ArrayList<ASCIIListener> getASCIIListeners() {
        return runnables;
    }

    public void cleanASCIIListeners() {
        runnables.clear();
    }

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

    public String getString() {
        return ret;
    }

    public void convert(BufferedImage image) {

        this.convert(image, new Dimension(image.getWidth(), image.getHeight()), true, false, TOLERANCES, VALUES,true);
    }

    public boolean isComplete() {
        return isComplete;
    }

    public static class RenderingInterruptedException extends Throwable {

    }

    public interface ASCIIListener {
        void onStringChange();
        default void onInterrupt(){}

        default void onEnd(){}
    }


}
