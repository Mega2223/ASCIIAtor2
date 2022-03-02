package net.Mega2223.ASCIIAtor2.objects;

import net.Mega2223.ASCIIAtor2.classesCopiadasDaAguaLib.utils.GenericTools;

import javax.imageio.ImageIO;
import javax.imageio.ImageTranscoder;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class Workspace extends JFrame {

    public static final String USER_DIR = System.getProperty("user.dir");
    int CadenciaDeRefreshes = 50;
    String[] values = ASCIIConverter.VALUES.clone();
    int[] tolerances = ASCIIConverter.TOLERANCES.clone();
    boolean doRenderUpdates = true;

    JLabel lastAction = new JLabel("");
    JLabel preview = new JLabel("");
    JLabel percentage = new JLabel("");

    BufferedImage selectedFile = null;
    String renderedImage = null;

    JMenuBar bar = new JMenuBar();
    JMenu abrir = new JMenu("Abrir");
    JMenuItem arquivo = new JMenuItem("De arquivo");
    JFileChooser chooser = null;
    JMenuItem CtrlV = new JMenuItem("Pegar imagem da sua área de transferência");
    JMenu ASCII = new JMenu("ASCII");
    JMenuItem renderizar = new JMenuItem("Renderização Rápida");
    JMenuItem ASCIIConfig = new JMenuItem("Renderização Completa");
    JMenu salvar = new JMenu("Salvar");
    JMenuItem CtrlC = new JMenuItem("Colocar sua String na área de transferência");
    JMenuItem textSave = new JMenuItem("Salvar como .txt");
    final ASCIIConverter converter = new ASCIIConverter();

    public Workspace() throws IOException {
        setLayout(new FlowLayout());
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        System.setProperty("http.agent", "Chrome");
        setIconImage(ImageIO.read(new URL("https://avatars.githubusercontent.com/u/59067466?s=400&u=9d154cbed85befb100018e3c9e4708875b51b141&v=4")));

        add(lastAction);
        lastAction.setFont(Font.decode("Consolas"));
        add(preview);
        preview.setFont(Font.decode("Consolas"));
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
        CtrlV.addActionListener(e -> {
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
            final double total = selectedFile.getHeight()*selectedFile.getWidth() + selectedFile.getHeight();
            converter.addRunnable(() -> {
                if(converter.getItnerations() % CadenciaDeRefreshes != 0){return;}
                System.out.println(converter.getString());
                refreshLabel(converter.getString());

            });
            converter.addRunnable(() -> {
                if(converter.getItnerations() % CadenciaDeRefreshes != 0){return;}
                double doble = ((double) converter.getItnerations()) / total;
                System.out.println(converter.getItnerations()+ ":" + total + "=" + doble);
                percentage.setText("Progresso: " + (int)((100* doble)) + "%");
            });
            converter.convert(selectedFile);

            String convert = converter.getString();
            renderedImage = convert;
            System.out.println(convert);
            refreshLabel(convert);

        });
        ASCIIConfig.addActionListener(e -> {
            @SuppressWarnings("unused")
            ConfigWindow configWindow = new ConfigWindow();
        });
        CtrlC.addActionListener(e -> {
            Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

            StringSelection selection = new StringSelection(renderedImage);
            systemClipboard.setContents(selection, null);

        });
        textSave.addActionListener(e -> {
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setFileFilter(new FileNameExtensionFilter("Arquivos de texto", "txt"));

            if(jFileChooser.showSaveDialog(this)!=JFileChooser.APPROVE_OPTION){return;}
            jFileChooser.setSelectedFile(new File(jFileChooser.getSelectedFile().getAbsolutePath() + ".txt"));
            File file = jFileChooser.getSelectedFile();
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write(renderedImage);
                writer.close();
                lastAction.setText("Arquivo salvo com sucesso :)");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Algo de errado aconteceu, fica o Stacktrace:\n" + ex);
                ex.printStackTrace();
            }
        });
        abrir.add(arquivo);
        abrir.add(CtrlV);

        ASCII.add(renderizar);
        ASCII.add(ASCIIConfig);

        salvar.add(CtrlC);
        salvar.add(textSave);
        
        bar.add(abrir);
        bar.add(ASCII);
        bar.add(salvar);

        setJMenuBar(bar);

        setVisible(true);
    }

    private void refreshLabel(String converterString) {
        if(converterString == null){return;}
        String[] splitted = converterString.split("\n");
        double fontMultiplier = 0.1 / splitted[0].length();
        int fontSize = (int) (5000 * fontMultiplier);
        fontSize++;
        preview.setFont(new Font(lastAction.getFont().getName(), Font.PLAIN, fontSize));
        preview.setSize(100,100);
        preview.setText(GenericTools.ConvertToHTML(splitted));
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

    //não, essa classe não é estática
    //loucura loucura loucura
    public class ConfigWindow extends JFrame{

        JSlider cadencia = new JSlider();
        JLabel cadenciaLabel = new JLabel("50");

        JLabel toleranciaLabel[] = {new JLabel(tolerances[0]+""),new JLabel(tolerances[1]+""),new JLabel(tolerances[2]+""),new JLabel(tolerances[3]+""),new JLabel(tolerances[4]+"")};

        static final String tol = "Tolerância ";
        JSlider tolerancias[] = {
                 new JSlider(0,255, ASCIIConverter.TOLERANCES[0])
                ,new JSlider(0,255,ASCIIConverter.TOLERANCES[1])
                ,new JSlider(0,255,ASCIIConverter.TOLERANCES[2])
                ,new JSlider(0,255,ASCIIConverter.TOLERANCES[3])
                ,new JSlider(0,255,ASCIIConverter.TOLERANCES[4])};
        //TALVEZ eu possa colocar essas declarações num loop de itnerações, mas eu prefiro não arriscar
        JTextField[] valueSetter = {new JTextField(values[0]),new JTextField(values[1]),new JTextField(values[2]),new JTextField(values[3]),new JTextField(values[4])};

        JTextField dimX = new JTextField("20");
        JTextField dimY = new JTextField("20");
        JButton ogDim = new JButton("Usar dimenções da Imagem");

        JButton renderizar = new JButton("Renderizar");

        JCheckBox darkMode = new JCheckBox();
        JCheckBox upDates = new JCheckBox();

        public ConfigWindow(){
            setSize(223,600);
            setVisible(true);
            setLayout(new FlowLayout());
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            cadencia.addChangeListener(e -> {
                CadenciaDeRefreshes = cadencia.getValue();
                cadenciaLabel.setText(CadenciaDeRefreshes + "");
            });

            add(new JLabel("Cadência de refreshes:"));
            add(cadenciaLabel);
            add(cadencia);

            for (int i = 0; i < tolerancias.length; i++) {
                JSlider ac = tolerancias[i];
                final int finalI = i;
                ac.addChangeListener(e -> {
                    tolerances[finalI] = ac.getValue();
                    toleranciaLabel[finalI].setText(tolerances[finalI] + "");
                });
                add(new JLabel("Tolerância de " + values[finalI] + ":"));
                add(toleranciaLabel[i]);
                add(ac);
            }

            for (int i = 0; i < values.length; i++) {
                JTextField act = valueSetter[i];
                add(new JLabel(i+"º Valor:"));
                int finalI = i;
                act.addActionListener(e -> {
                    values[finalI] = act.getText();
                });
                add(act);
            }

            add(new JLabel("Dimensões X e Y respectivamente:"));
            Dimension minimumSize = new Dimension(100, 40);
            dimX.setMinimumSize(minimumSize);
            dimY.setMinimumSize(minimumSize);
            add(dimX);add(dimY);

            add(new JLabel("Dark mode?"));
            add(darkMode);

            add(new JLabel("Fazer updates de texto?"));
            add(upDates);

            ogDim.addActionListener(e -> {
                dimX.setText(selectedFile.getWidth()+"");
                dimY.setText(selectedFile.getHeight()+"");
            });
            add(ogDim);

            renderizar.addActionListener(e -> {

                Dimension dim;
                try {
                    dim = new Dimension(Integer.parseInt(dimX.getText()), Integer.parseInt(dimY.getText()));
                } catch (NumberFormatException exception) {
                    renderizar.setText("Dimenções não são números :(");
                    return;
                }
                System.out.println(dim);
                final double tot = dim.getHeight() * dim.getWidth() + dim.getHeight();
                System.out.println("DECLARAÇÃO DE TOTAL: " + tot + "(" + dim.getHeight() + "*" + dim.getWidth() + "+" + dim.getHeight() + ")");

                //atualiza a label
                converter.addRunnable(() -> {
                    if (converter.getItnerations() % CadenciaDeRefreshes != 0 || !upDates.isSelected()) {
                        return;
                    }
                    refreshLabel(converter.getString());

                });
                //atualiza as porcentagens
                converter.addRunnable(() -> {

                    System.out.println("TOTAL: " + tot + "(" + dim.getHeight() + "*" + dim.getWidth() + "+" + dim.getHeight() + ")");
                    System.out.println("T:" + tot + ":" + converter.getItnerations());

                    if (converter.getItnerations() % CadenciaDeRefreshes != 0) {
                        return;
                    }
                    double doble = ((double) converter.getItnerations()) / tot;
                    percentage.setText("Progresso: " + (int) ((100 * doble)) + "%");
                });
                //atualiza a label no final
                converter.addRunnable(() -> {
                    if (!converter.isComplete()) {
                        return;
                    }
                    double doble = ((double) converter.getItnerations()) / tot;
                    percentage.setText("Progresso: " + (int) ((100 * doble)) + "%");
                    refreshLabel(converter.getString());
                    System.out.println(converter.getString());
                    renderedImage = converter.getString();
                });


                //fixme alpha
                converter.convert(selectedFile, dim, false, darkMode.isSelected(), tolerances, values,true);

                refreshLabel(converter.getString());
            });

            add(renderizar);
        }
    }
}
