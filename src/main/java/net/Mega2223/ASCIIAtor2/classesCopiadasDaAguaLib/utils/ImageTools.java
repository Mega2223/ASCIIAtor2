package net.Mega2223.ASCIIAtor2.classesCopiadasDaAguaLib.utils;

import net.Mega2223.ASCIIAtor2.classesCopiadasDaAguaLib.utils.objects.GraphRenderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;


public class ImageTools {
    public static BufferedImage createFlipped(BufferedImage image) {
        AffineTransform at = new AffineTransform();
        at.concatenate(AffineTransform.getScaleInstance(1, -1));
        at.concatenate(AffineTransform.getTranslateInstance(0, -image.getHeight()));
        BufferedImage newImage = new BufferedImage(
                image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.transform(at);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }

    public static Image createFlipped(Image image){
        BufferedImage bufferedImage = new BufferedImage(
                image.getWidth(null),
                image.getHeight(null),
                BufferedImage.TYPE_4BYTE_ABGR);
        bufferedImage.createGraphics().drawImage(image,0,0,null);
        return createFlipped(bufferedImage);
    }

    public static Image getScaledGraph(Dimension legal, double grid, GraphRenderer renderer){
        double larger = 0;
        if (legal.getHeight() > legal.getWidth()){
            larger = legal.getHeight();
        } else {larger = legal.getWidth();}

        renderer.dimension = new Dimension((int)larger,(int)larger); //why
        final Image readj = renderer.renderWithGrid(new ArrayList<>(),grid).getScaledInstance((int)legal.getWidth(),(int)legal.getHeight(), BufferedImage.SCALE_AREA_AVERAGING);
        return readj;
    }

    public static BufferedImage getBufferedImage(Image img){
        BufferedImage ret = new BufferedImage(img.getWidth(null),img.getHeight(null),BufferedImage.TYPE_4BYTE_ABGR);
        ret.getGraphics().drawImage(img, 0, 0, null);
        return ret;
    }
    public static BufferedImage rotateImage(double degrees, BufferedImage image){
        double rads = Math.toRadians(degrees);
        double sin = Math.abs(Math.sin(rads));
        double cos = Math.abs(Math.cos(rads));
        int w = (int) Math.floor(image.getWidth() * cos + image.getHeight() * sin);
        int h = (int) Math.floor(image.getHeight() * cos + image.getWidth() * sin);
        BufferedImage rotatedImage = new BufferedImage(w, h, image.getType());
        AffineTransform at = new AffineTransform();
        at.translate(w / 2, h / 2);
        at.rotate(rads,0, 0);
        at.translate(-image.getWidth() / 2, -image.getHeight() / 2);
        AffineTransformOp rotateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
        rotateOp.filter(image,rotatedImage);
        return rotatedImage;
    }

	public static Image getImageByURL(String url) throws IOException {
        return getImageByURL(new URL(url));
    }

    public static Image getImageByURL(URL url) throws IOException {
        System.setProperty("http.agent", "Chrome");
        //sério isso?
        //isso é prova definitiva que HTTP é coisa do demônio
        //mano eu to mt puto vai se fuder era so fingir que era a porra do Chrome
        return ImageIO.read(url);
    }
	
}
