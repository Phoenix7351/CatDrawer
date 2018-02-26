package ramo.klevis.ml.Image;

/**
 * File: RandomImage.java
 *
 * Description:
 * Create a random color image.
 *
 * @author Yusuf Shakeel
 * Date: 01-04-2014 tue
 */
import com.google.common.graph.Graph;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.nio.Buffer;
import java.awt.Graphics;

public class RandomImage{
    public static File main(String args[])throws IOException{
        //image dimension
        int width = Integer.parseInt(args[0]);
        int height = Integer.parseInt(args[1]);
        //create buffered image object img
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        //file object
        File f = null;
        //create random image pixel by pixel
        /*
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){

                int p = makePixel(null, x, y);

                img.setRGB(x, y, p);
            }
        }
        */

        Graphics context = img.getGraphics();

        context.setColor(Color.white);
        context.fillRect(0, 0, width, height);
        context.setColor(Color.black);
        context.drawOval(width/4, height/4, 100, 100);
        context.dispose();


        //write image
        return writeFile(img, "start");
    }//main() ends here

    public static File alterExistingImage(File file, double threshold) throws IOException {
        BufferedImage previous = ImageIO.read(file);

        for(int y = 0; y < previous.getHeight(); y++){
            for(int x = 0; x < previous.getWidth(); x++){
                //if (Math.random() > threshold) {
                    //int previousPixel = previous.getRGB(x, y);
                    int p = makePixel(previous, x, y);
                    previous.setRGB(x, y, p);
                //}
            }
        }

        return writeFile(previous, "temp");
    }

    public static File writeFile(BufferedImage img, String fileName) {
        try{
            File f = new File("/Users/justintiffner/Documents/CatAndDogRecognizer-master/resources/generated.nosync/" + fileName + ".png");
            ImageIO.write(img, "png", f);
            return f;
        }catch(IOException e){
            System.out.println("Error: " + e);
            return null;
        }
    }

    public static int makePixel(BufferedImage previousImage, int x, int y) {
        long decider = oneOrZero(previousImage, x, y);

        int a = (int)(decider*255); //alpha
        int r = (int)(decider*255); //red
        int g = (int)(decider*255); //green
        int b = (int)(decider*255); //blue

        //int a = (int)(Math.random()*256); //alpha
        //int r = (int)(Math.random()*256); //red
        //int g = (int)(Math.random()*256); //green
        //int b = (int)(Math.random()*256); //blue

        //Color color = intToCol(((int) (Math.random()*256)));

        return (a<<24) | (r<<16) | (g<<8) | b;
    }

    public static long oneOrZero(BufferedImage previousImage, int x, int y) {

        double random = Math.random();

        //int previousPixel = 0;
        //if (previousImage != null) {
        //    previousPixel = previousImage.getRGB(x, y);
        //}

        //if (previousPixel < 0) {
         //   random -= 0.1;
        //}

        boolean neighborFound = false;

        if(previousImage != null) {
            for (int newX = x - 1; newX <= x + 1; newX++) {

                if(newX < 0 || newX >= previousImage.getWidth()) {
                    continue;
                }

                for (int newY = y - 1; newY <= y + 1; newY++) {

                    if(newY < 0 || newY >= previousImage.getHeight()) {
                        continue;
                    }

                    int neighborPixel = previousImage.getRGB(newX, newY);

                    if (neighborPixel != -1) {
                        random -= 0.1;
                        neighborFound = true;
                        break;
                    }
                }
            }
        }

        if (random <= 0.1 && neighborFound) {
            return 0;
        } else {
            return 1;
        }

        //return Math.round(Math.random());
    }

    public static Color intToCol(int colNum)
    {
        int rgbNum = 255 - (int) ((colNum/50.0)*255.0);
        return new Color (rgbNum,rgbNum,rgbNum);
    }
}//class ends here
