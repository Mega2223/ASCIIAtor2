package net.Mega2223.ASCIIAtor2.classesCopiadasDaAguaLib.utils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class GenericTools {

    public static String ConvertToHTML(String[] eee) {
        String end = "";
        for (int ever = 0; ever < eee.length; ever++) {

            if (ever == 0) {
                end = "<html>" + eee[ever];
            } else {
                end = end + "<br/>" + eee[ever];
            }

        }
        return end;
    }
    public static List<String> tweetifyText(String textToSplitify, int maxLength) {
        List<String> finalTweets = new ArrayList<String>();
        String[] words = textToSplitify.split(" ");
        String actualTweet = "";

        for(int q = 0; q < words.length; q++){
            if((actualTweet + words[q]).length() > maxLength){ //280
                finalTweets.add(actualTweet);
                actualTweet = words[q] + " ";
            } else {actualTweet = actualTweet + words[q] + " ";}

        }


        //System.out.println("\n\n\n" + finalTweets.get(q));
        // eu poderia fazer um sistema de CopiaCola mas ia dar muito trabalho
        // e no final o objetivo de fazer isso é pra evitar trabalho

        return finalTweets;
    }
    public static String getAfterLastCharacter(String what, String afterWhat){

        StringBuilder e = new StringBuilder(what);
        e.delete(0,e.lastIndexOf(afterWhat));
        return e.toString();
    }
    public static String getBeforerFirstCharacter(String what, String afterWhat){

        StringBuilder e = new StringBuilder(what);
        e.delete(e.indexOf(afterWhat),e.length());
        return e.toString();
    }
    public static boolean isNumber(String string){
        try{Integer.parseInt(string); return true;} catch (NumberFormatException e){return false;}
    }
    
}
