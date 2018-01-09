package pl.mateusz.mazurek;
/*import java.io.*;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

public class Main {

    public static void main(String[] args)throws FileNotFoundException {
        Mat image=new Mat();
	// write your code here
        try{
            image= Highgui.imread("resources/lena1.png");
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }

        if (image.empty()){
            System.out.println("nie działa");
        }else{
            Highgui.imwrite("lena2.png",image);
        }
       // FileOutputStream file1=new FileOutputStream("plik1.txt");
      //  PrintWriter zapis = new PrintWriter("ala.txt");
       // zapis.println("Ala ma kota, a kot ma Alę");
       // zapis.close();

    }
}*/

import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.features2d.*;
import org.opencv.highgui.Highgui;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.features2d.*;
import org.opencv.highgui.Highgui;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import static org.opencv.core.Core.absdiff;

/**
 *
 * Created by Kinath on 8/6/2016.
 */
public class Main {

    /*public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        File lib = null;
        String os = System.getProperty("os.name");
        String bitness = System.getProperty("sun.arch.data.model");

       // System.out.println(lib.getAbsolutePath());
       // System.load(lib.getAbsolutePath());

        String bookObject = "C:\\Users\\Megane\\Desktop\\test\\oko.jpg";
        String bookScene = "C:\\Users\\Megane\\Desktop\\test\\sowa.jpg";
*/
    public static void main(String[] args) {

        File lib = null;
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        String os = System.getProperty("os.name");
        String bitness = System.getProperty("sun.arch.data.model");
        String bookObject = "rsc\\w.jpg";
        String bookScene = "rsc\\wo.jpg";

        //System.out.println("Started....");
        //System.out.println("Loading images...");
        Mat objectImage = Highgui.imread(bookObject, Highgui.CV_LOAD_IMAGE_COLOR);
        Mat sceneImage = Highgui.imread(bookScene, Highgui.CV_LOAD_IMAGE_COLOR);
        Mat result=new Mat();
        absdiff(objectImage,sceneImage,result);

        Highgui.imwrite("rsc\\res.jpg",result);
        System.out.println("Ended....");
    }
}