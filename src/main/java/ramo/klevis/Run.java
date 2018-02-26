package ramo.klevis;

import org.apache.commons.io.FileUtils;
import org.nd4j.linalg.api.ndarray.INDArray;
import ramo.klevis.ml.Image.RandomImage;
import ramo.klevis.ml.ui.ProgressBar;
import ramo.klevis.ml.ui.UI;
import ramo.klevis.ml.vg16.VG16ForCat;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.zip.Adler32;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.FlowLayout;

/**
 * Created by klevis.ramo on 1/1/2018.
 */
public class Run {

    private static final String MODEL_URL = "https://dl.dropboxusercontent.com/s/djmh91tk1bca4hz/RunEpoch_class_2_soft_10_32_1800.zip?dl=0";

    public static void main(String[] args) throws Exception {
        downloadModelForFirstTime();

        //JFrame mainFrame = new JFrame();
        //ProgressBar progressBar = new ProgressBar(mainFrame, true);
        //progressBar.showProgressBar("Loading model, this make take several seconds!");

        VG16ForCat vg16ForCat = new VG16ForCat();
        vg16ForCat.loadModel();

        String[] argValues = { "224", "224" };
        File file = RandomImage.main(argValues);

        INDArray output = vg16ForCat.detectCat(file);
        double bestOuput = output.getDouble(0);

        while (bestOuput < 1.0) {
            File altfile = RandomImage.alterExistingImage(file, bestOuput);
            INDArray newOutput = vg16ForCat.detectCat(altfile);
            if (newOutput.getDouble(0) > bestOuput) {
                file = RandomImage.writeFile(ImageIO.read(altfile), "best");
                bestOuput = newOutput.getDouble(0);
                //DisplayImage.main(null);
                System.out.print("new best value: " + bestOuput);
                BufferedImage image = ImageIO.read(file );
                writeFile(image, String.valueOf(bestOuput*100));
            }
        }

        //System.out.print(output.toString());

        //for(int i = 0; i < 10; i++) {
            //file = RandomImage.alterExistingImage(file, 0.1);
            //DisplayImage.main(null);
            //System.out.print("on to the next one");
            //Thread.sleep(10000);


        //}



        System.out.print("Done");

        /*
        UI ui = new UI();
        Executors.newCachedThreadPool().submit(() -> {
            try {
                ui.initUI();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally {
                progressBar.setVisible(false);
                mainFrame.dispose();
            }
        });
        */

    }

    public static File writeFile(BufferedImage img, String filename) {
        try{
            File f = new File("/Users/justintiffner/Documents/CatAndDogRecognizer-master/resources/generated.nosync/" + filename + ".png");
            ImageIO.write(img, "png", f);
            return f;
        }catch(IOException e){
            System.out.println("Error: " + e);
            return null;
        }
    }

    private static void downloadModelForFirstTime() throws IOException {
        JFrame mainFrame = new JFrame();
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        ProgressBar progressBar = new ProgressBar(mainFrame, false);
        File model = new File("resources/model.zip");
        if (!model.exists() || FileUtils.checksum(model, new Adler32()).getValue() != 3082129141l) {
            model.delete();
            progressBar.showProgressBar("Downloading model for the first time 500MB!");
            URL modelURL = new URL(MODEL_URL);

            try {
                FileUtils.copyURLToFile(modelURL, model);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Failed to download model");
                throw new RuntimeException(e);
            } finally {
                progressBar.setVisible(false);
                mainFrame.dispose();
            }

        }
    }
}

