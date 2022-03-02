package net.Mega2223.ASCIIAtor2.classesCopiadasDaAguaLib.utils.objects;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static net.Mega2223.ASCIIAtor2.classesCopiadasDaAguaLib.utils.ImageTools.createFlipped;


public class GraphRenderer {

    public List<List<double[]>> values;
    public Dimension dimension;
    public Color[] colours; //britanico pra não dar ambiguidade, genial dmite

    public GraphRenderer(List<List<double[]>> val, Dimension dims, Color[] colors) {
        values = val;
        dimension = dims;
        colours = colors;
    }

    public static BufferedImage getImage(List<List<double[]>> dobros, Dimension dim, Color[] colors) {
        return
                getImage(dobros, new ArrayList<List<double[]>>(), dim, colors);
    }

    public static BufferedImage getImage(List<List<double[]>> dobros, List<List<double[]>> linhasNEssenciais, Dimension dim, Color[] colors) {
        BufferedImage ret = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_4BYTE_ABGR);

        System.out.println(dim);

        List<double[]> gr = dobros.get(0);

        double minX = gr.get(0)[0];
        double maxX = gr.get(0)[0];
        double minY = gr.get(0)[1];
        double maxY = gr.get(0)[1];

        for (List<double[]> act : dobros) {
            for (double[] e : act) {
                if (e[0] > maxX) {
                    maxX = e[0];
                }
                if (e[1] > maxY) {
                    maxY = e[1];
                }
                if (e[0] < minX) {
                    minX = e[0];
                }
                if (e[1] < minY) {
                    minY = e[1];
                }
            }
        }

        double multIndexY = (dim.getHeight() / (maxY - minY));
        double multIndexX = (dim.getWidth() / (maxX - minX));

        multIndexY = multIndexY - (multIndexY / 100);
        multIndexX = multIndexX - (multIndexX / 100);

        List<List<double[]>> translated = translateGr(dobros);

        Graphics graphics = ret.getGraphics();

        double loc0X = (-minX) * multIndexX;
        double loc0Y = (-minY) * multIndexY;


        //x1 y1 x2 y2

        //-3,3 (L100)
        //0,6
        //3 pra frente

        //-3,3
        //3
        //

        graphics.setColor(Color.blue);

        List<List<double[]>> notExTR = new ArrayList<>();

        for (List<double[]> act : linhasNEssenciais) {
            List<double[]> go = new ArrayList<>();
            for (double[] inte : act) {
                double[] vaiag = {inte[0] - minX, inte[1] - minY};
                go.add(vaiag);
            }
            notExTR.add(go);
        }

        graphics.setColor(Color.lightGray);
        double[] ant = null;
        for (List<double[]> act : notExTR) {
            for (double[] inte : act) {
                if (ant == null) {
                    ant = inte;
                    continue;
                }
                int TXA = (int) (ant[0] * multIndexX);
                int TYA = (int) (ant[1] * multIndexY);
                int ACX = (int) (inte[0] * multIndexX);
                int ACY = (int) (inte[1] * multIndexY);
                graphics.drawLine(TXA, TYA, ACX, ACY);
                //System.out.println( "(" + (int)ant[0] + "\\"+ (int)ant[1] +"\\"+ (int)inte[0] + "\\"+ (int)inte[1] +"):   " + TXA + "\\" + TYA + "\\" + ACX + "\\" +ACY);
                ant = inte;
            }

            ant = null;
        }

        //((Graphics2D)graphics).drawString("eba",40,40);

        ant = null;
        int ind = 0;
        int colorIndex = 0;
        for (List<double[]> act : translated) {
            try{graphics.setColor(colors[ind]);}catch (ArrayIndexOutOfBoundsException ex){
                colorIndex = 0;
            }
            colorIndex++;
            for (double[] inte : act) {
                if (ant == null) {
                    ant = inte;
                    continue;
                }
                int TXA = (int) (ant[0] * multIndexX);
                int TYA = (int) (ant[1] * multIndexY);
                int ACX = (int) (inte[0] * multIndexX);
                int ACY = (int) (inte[1] * multIndexY);
                graphics.drawLine(TXA, TYA, ACX, ACY);
                //System.out.println( "(" + (int)ant[0] + "\\"+ (int)ant[1] +"\\"+ (int)inte[0] + "\\"+ (int)inte[1] +"):   " + TXA + "\\" + TYA + "\\" + ACX + "\\" +ACY);
                ant = inte;
            }
            graphics.setColor(Color.red);
            ind++;
            ant = null;
        }

        graphics.setColor(Color.gray);
        graphics.drawLine((int) loc0X, 0, (int) loc0X, ret.getHeight() - (ret.getHeight() / 100));
        graphics.drawLine(0, (int) loc0Y, ret.getWidth() - (ret.getWidth() / 100), (int) loc0Y);

        graphics.dispose();


        //System.out.println("==================================");
        return createFlipped(ret);
    }
    public static BufferedImage renderWithGrid(List<List<double[]>> dobros, List<List<double[]>> linhasNEssenciais, Dimension dim, Color[] colors, double frequency) {
        return renderWithGrid(dobros,linhasNEssenciais,dim,colors,new double[]{frequency,frequency});
    }


    public static BufferedImage renderWithGrid(List<List<double[]>> dobros, List<List<double[]>> linhasNEssenciais, Dimension dim, Color[] colors, double[] frequency) {

        List<double[]> first = dobros.get(0);
        if (first.size() == 0){
            boolean achei = false;
            int analise = 1;
            while (!achei){
                if(dobros.get(analise).size() != 0){
                    first = dobros.get(analise);
                    achei = true;
                }
            }
        }

        if (first.size() == 0){return null;}

        List<List<double[]>> linhasNEssenciaisB = new ArrayList<>();
        linhasNEssenciaisB.addAll(linhasNEssenciais);

        double minX = first.get(0)[0];
        double maxX = first.get(0)[0];
        double minY = first.get(0)[1];
        double maxY = first.get(0)[1];

        for (List<double[]> act : dobros) {
            for (double[] e : act) {
                if (e[0] > maxX) {
                    maxX = e[0];
                }
                if (e[1] > maxY) {
                    maxY = e[1];
                }
                if (e[0] < minX) {
                    minX = e[0];
                }
                if (e[1] < minY) {
                    minY = e[1];
                }
            }
        }


        for (double n = minY; n <= maxY; n = n + frequency[1]) {
            if ((int) (n / (n / 2)) == 0) {
                continue;
            }
            //System.out.println(n);
            //System.out.println(minX + n);
            //System.out.println(n +" desenhndo " + minY +":"+ maxY);
            List<double[]> eb = new ArrayList<>();
            eb.add(new double[]{maxX, n});
            eb.add(new double[]{minX, n});
            linhasNEssenciaisB.add(eb);
        }

        for (double n = minX; n <= maxX; n = n + frequency[0]) {
            if ((int) (n / (n / 2)) == 0) {
                continue;
            }
            //System.out.println(n);
            //System.out.println(minX + n);
            //System.out.println(n +" desenhndo " + minX +":"+ maxX);
            List<double[]> eb = new ArrayList<>();
            eb.add(new double[]{n, minY});
            eb.add(new double[]{n, maxY});
            linhasNEssenciaisB.add(eb);
        }

        return getImage(dobros, linhasNEssenciaisB, dim, colors);
    }

    protected static List<List<double[]>> translateGr(List<List<double[]>> gr) {
        List<List<double[]>> ret = new ArrayList();

        List<double[]> first = gr.get(0);
        double minX = first.get(0)[0];
        double maxX = first.get(0)[0];
        double minY = first.get(0)[1];
        double maxY = first.get(0)[1];

        for (List<double[]> act : gr) {
            for (double[] e : act) {
                if (e[0] > maxX) {
                    maxX = e[0];
                }
                if (e[1] > maxY) {
                    maxY = e[1];
                }
                if (e[0] < minX) {
                    minX = e[0];
                }
                if (e[1] < minY) {
                    minY = e[1];
                }
            }
        }
        //System.out.println(maxX +","+minX +","+maxY +","+minY +",");

        for (List<double[]> act : gr) {
            List<double[]> fin = new ArrayList<>();
            for (double[] e : act) {
                fin.add(new double[]{e[0] - minX, e[1] - minY});
            }
            ret.add(fin);
        }

        return ret;
    }

    /*
    public static BufferedImage renderWithGrid(List<List<double[]>> dobros,List<List<double[]>> linhasNEssenciais, Dimension dim, Color[] colors,double frequency){

     */

    public BufferedImage renderWithGrid(List<List<double[]>> extraLanes,double gridIndex){
        return renderWithGrid(this.values,extraLanes,this.dimension,this.colours,new double[]{gridIndex,gridIndex});
    }
    public BufferedImage renderWithGrid(List<List<double[]>> extraLanes, double[] gridIndex){
        return renderWithGrid(this.values,extraLanes,this.dimension,this.colours,gridIndex);
    }


    public BufferedImage renderImage() {
        return getImage(values, dimension, colours);

    }
}
