package net.Mega2223.ASCIIAtor2.classesCopiadasDaAguaLib.utils.objects;

public class Debugger {
    public static final int DEBUG_EVERYTHING = 0;
    public static final int DEBUG_EVERYTHING_BUT_LOOPS = 1;
    public static final int DEBUG_ONLY_RELEVANT = 2;
    public static final int DEBUG_ONLY_IMPORTANT = 3;
    public static final int DEBUG_NOTHING = 4;

    public int level = 0;

    public Debugger(){}
    public Debugger(int level){
        this.level = level;
    }

    public void debug(Object what, int level){
        if(level >= this.level){
            System.out.println(what);
        }
    }

}
