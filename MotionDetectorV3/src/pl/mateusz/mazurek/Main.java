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

import javafx.scene.effect.GaussianBlur;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.features2d.*;
import org.opencv.highgui.Highgui;

import java.io.File;
import java.util.*;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.features2d.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import static org.opencv.core.Core.*;
import static org.opencv.core.CvType.CV_32FC1;
import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.imgproc.Imgproc.*;

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
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        System.out.println("Welcom to MotionDetector V3..... Let's start!");

        File folder = new File("rsc");
        File[] listOfFiles = folder.listFiles();
        ArrayList<String> fileNames=new ArrayList<>();
        String inputResultFileName;
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                System.out.println("File " + listOfFiles[i].getName());
                char[] extension=new char[3];
                listOfFiles[i].getName().getChars(listOfFiles[i].getName().length()-3,listOfFiles[i].getName().length(),extension,0);
                if(extension.equals("txt")){

                }
                fileNames.add(listOfFiles[i].getName());
            } else if (listOfFiles[i].isDirectory()) {
                System.out.println("Directory " + listOfFiles[i].getName());
            }
        }


        Collections.sort(fileNames, new Comparator<String>() {
            public int compare(String o1, String o2) {
                return extractInt(o1) - extractInt(o2);
            }

            int extractInt(String s) {
                String num = s.replaceAll("\\D", "");
                // return 0 if no digits found
                return num.isEmpty() ? 0 : Integer.parseInt(num);
            }
        });
        for (String item : fileNames ) {
            System.out.println(item);
        }

        String bookObject = "rsc\\lena\\0771.png";
        String bookScene = "rsc\\lena\\0772.png";

        //System.out.println("Started....");
        //System.out.println("Loading images...");
        Mat objectImage = Highgui.imread(bookObject, Highgui.CV_LOAD_IMAGE_GRAYSCALE);
        Mat sceneImage = Highgui.imread(bookScene, Highgui.CV_LOAD_IMAGE_GRAYSCALE);
        Mat result=new Mat();
        Mat result1=new Mat();
        Mat result2=new Mat();
        Mat result3=new Mat();
        Mat gausianresult=new Mat();
        Highgui.imwrite("rsc\\lena\\obj.jpg",objectImage);
        absdiff(objectImage,sceneImage,result);
        threshold(result,result1,20,255,THRESH_BINARY);
        blur(result1,result2, new Size(20,20));
        erode(result1,result2,getStructuringElement(MORPH_RECT,new Size(1,1)));
        dilate(result2,result3,getStructuringElement(MORPH_RECT,new Size(40,40)));


        Highgui.imwrite("rsc\\lena\\res.jpg",result);
        Highgui.imwrite("rsc\\lena\\res1.jpg",result1);
        Highgui.imwrite("rsc\\lena\\res2.jpg",result2);
        Highgui.imwrite("rsc\\lena\\res3.jpg",result3);


        //tracking
        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(result3, contours, new Mat(), Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);

        for (int i=0;i<contours.size();i++){
            Moments moment = Imgproc.moments(contours.get(i));
            double area = moment.get_m00();
            if (area>40) {
                System.out.println("area: " + String.valueOf(area));
                double x = (moment.get_m10() / area)/1360;
                System.out.println("x: " + String.valueOf(x));
                double y = (moment.get_m01() / area)/768;
                System.out.println("y: " + String.valueOf(y));
            }
        }

///////////////////////////////////////////////////////////
      /*  Rect rect = Imgproc.boundingRect(contours.get(0));
        //Mat mask = new Mat(rect.height,rect.width,CV_8UC1);
        //erode(mask,mask,getStructuringElement(MORPH_RECT,new Size(10,10)));
        Mat mask = new Mat(objectImage,rect);
        Highgui.imwrite("rsc\\lena\\mask.jpg",mask);
        Mat tempScene = new Mat();
        sceneImage.copyTo(tempScene);
        int resultCols = tempScene.cols()-mask.cols()+1;
        int resultRows = tempScene.rows()-mask.rows()+1;
        Mat resTemp=new Mat();
        resTemp.create(resultRows,resultCols,CV_32FC1);
        matchTemplate(tempScene,mask,resTemp,Imgproc.TM_SQDIFF);
        normalize(resTemp,resTemp,0,1,NORM_MINMAX,1);

        double minVal;
        double maxVal;
        Point minLoc;
        Point maxLoc;
        Point matchLoc;
        MinMaxLocResult mmLR=minMaxLoc(resTemp);
        matchLoc=mmLR.minLoc;
        double x1= ((double) ((double)matchLoc.x / ((double)tempScene.width() / (double)2)));
        double y1= ((double) ((double)matchLoc.y / ((double)tempScene.height() / (double)2)));
        System.out.println("x: "+String.valueOf(x1));
        System.out.println("y: "+String.valueOf(y1));
        System.out.println("x: "+String.valueOf(matchLoc.y));*/

        /*File lib = null;

        String os = System.getProperty("os.name");
        String bitness = System.getProperty("sun.arch.data.model");
        String bookObject = "rsc\\w.jpg";
        String bookScene = "rsc\\wo.jpg";

        //System.out.println("Started....");
        //System.out.println("Loading images...");
        Mat objectImage = Highgui.imread(bookObject, Highgui.CV_LOAD_IMAGE_COLOR);
        Mat sceneImage = Highgui.imread(bookScene, Highgui.CV_LOAD_IMAGE_COLOR);
        Mat result=new Mat();
        Mat gausianresult=new Mat();

        absdiff(objectImage,sceneImage,result);

        Highgui.imwrite("rsc\\res.jpg",result);
        System.out.println("Ended....");*/
    }
}