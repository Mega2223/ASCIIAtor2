package net.Mega2223.ASCIIAtor2.objects;

import net.Mega2223.utils.GenericTools;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Workspace extends JFrame {

    JLabel lastAction = new JLabel("");
    JLabel percentage = new JLabel("");

    BufferedImage selectedFile = null;
    String renderedImage = null;

    JMenuBar bar = new JMenuBar();
    JMenu abrir = new JMenu("Abrir");
    JMenuItem arquivo = new JMenuItem("De arquivo");
    JFileChooser chooser = null;
    JMenuItem ctrlV = new JMenuItem("Pegar imagem da sua área de transferência");
    JMenu ASCII = new JMenu("ASCII");
    JMenuItem renderizar = new JMenuItem("Renderizar ASCII");

    final ASCIIConverter converter = new ASCIIConverter();

    public Workspace() {
        setLayout(new FlowLayout());
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(lastAction);
        lastAction.setFont(Font.decode("Consolas"));
        add(percentage);
        percentage.setFont(Font.decode("Consolas"));

        arquivo.addActionListener(e -> {
            if (chooser != null) {
                chooser.setEnabled(false);
            }
            chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("Imagens", "png", "jpg", "jpeg", "img"));
            chooser.showOpenDialog(this);
            if (chooser.getFileSelectionMode() == JFileChooser.APPROVE_OPTION) {
                //System.out.println("File pego com sucesso");
                try {
                    selectedFile = ImageIO.read(chooser.getSelectedFile());
                    lastAction.setText("Arquivo " + chooser.getSelectedFile().getName() + " selecionado");
                } catch (IOException ex) {
                }

            }
        });
        ctrlV.addActionListener(e -> {
            try {
                selectedFile = loadImageFromClipboard();
                if (selectedFile != null) {
                    lastAction.setText("Imagem carregada");
                } else {
                    throw new UnsupportedFlavorException(DataFlavor.imageFlavor);
                }
            } catch (UnsupportedFlavorException ex) {
                lastAction.setText("Arquivo na área de transferência não é uma imagem :(");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        renderizar.addActionListener(e -> {
            final double total = selectedFile.getHeight()*selectedFile.getWidth();
            converter.addRunnable(() -> {
                System.out.println("eventRun");

                double doble = ((double) converter.getItnerations()) / total;
                System.out.println(converter.getItnerations()+ ":" + total + "=" + doble);
                percentage.setText("Progresso: " + (int)(100* doble) + "%");

                String converterString = converter.getString();
                String[] splitted = converterString.split("\n");
                double fontMultiplier = 0.1 / splitted[0].length();
                int fontSize = (int) (5000 * fontMultiplier);
                fontSize++;
                lastAction.setFont(new Font(lastAction.getFont().getName(), Font.PLAIN, fontSize));
                lastAction.setSize(100,100);
                lastAction.setText(GenericTools.ConvertToHTML(splitted));

                //retarda o programa propositalmente
                //TODO: remove isso, duh
                //quickSleep(100);
            });
            renderedImage = converter.convert(selectedFile);

            System.out.println(renderedImage);
        });

        abrir.add(arquivo);
        abrir.add(ctrlV);

        ASCII.add(renderizar);

        bar.add(abrir);
        bar.add(ASCII);

        setJMenuBar(bar);

        setVisible(true);
    }


    @Deprecated
    public void quickSleep(int milis){
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage loadImageFromClipboard() throws IOException, UnsupportedFlavorException {
        Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        try {//Carregamento de imagens
            return (BufferedImage) systemClipboard.getData(DataFlavor.imageFlavor);
        } catch (UnsupportedFlavorException e) {
            //Carregamento de arquivos
            return ImageIO.read((File) ((List) systemClipboard.getData(DataFlavor.javaFileListFlavor)).get(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
