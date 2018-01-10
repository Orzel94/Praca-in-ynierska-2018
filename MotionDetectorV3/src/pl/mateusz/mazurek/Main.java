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

import org.opencv.core.*;
import org.opencv.highgui.Highgui;

import java.io.File;
import java.util.*;

import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import static org.opencv.core.Core.*;
import static org.opencv.imgproc.Imgproc.*;


public class Main {
    static File folder = new File("rsc");
    static File[] listOfFiles = folder.listFiles();
    static ArrayList<String> fileNames=new ArrayList<>();
    static String[] inputResultFileName=new String[1];

    String frame1;
    String frame2;

    //System.out.println("Started....");
    //System.out.println("Loading images...");
    static Mat image1;
    static Mat image2;
    static Mat result;
    static Mat result1;
    static Mat result2;
    static Mat result3;
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
        result=new Mat();
        result1=new Mat();
        result2=new Mat();
        result3=new Mat();
        System.out.println("Welcome to MotionDetector V3..... Let's start!");

        LoadFiles();
        for (String item : fileNames ) {
            System.out.println(item);
        }
        String frame1;
        String frame2;
        ArrayList<ArrayList<Double>> detections=new ArrayList<>();
        for(int i=0;i<fileNames.size()-1;++i) {
            frame1 = "rsc\\"+fileNames.get(i);
            frame2 = "rsc\\"+fileNames.get(i+1);

            image1 = Highgui.imread(frame1, Highgui.CV_LOAD_IMAGE_GRAYSCALE);
            image2 = Highgui.imread(frame2, Highgui.CV_LOAD_IMAGE_GRAYSCALE);

            PreprocesImages();
            //Highgui.imwrite("rsc\\lena\\obj.jpg",image1);



            /*Highgui.imwrite("rsc\\lena\\res.jpg",result);
            Highgui.imwrite("rsc\\lena\\res1.jpg",result1);
            Highgui.imwrite("rsc\\lena\\res2.jpg",result2);
            Highgui.imwrite("rsc\\lena\\res3.jpg",result3);*/


            //tracking
            ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
            Imgproc.findContours(result3, contours, new Mat(), Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);
            Double x=0.0;
            Double y=0.0;
            if(i==0){
                detections.add(new ArrayList<>());
            }
            detections.add(new ArrayList<>());
            for (int j=0;j<contours.size();j++){
                Moments moment = Imgproc.moments(contours.get(j));
                double area = moment.get_m00();
                if (area>40) {
                    System.out.println("area: " + String.valueOf(area));
                    x = (moment.get_m10() / area)/1360;
                    System.out.println("x: " + String.valueOf(x));
                    y = (moment.get_m01() / area)/768;
                    System.out.println("y: " + String.valueOf(y));
                }
                if(i==0){
                    Scanner in = new Scanner(fileNames.get(i)).useDelimiter("[^0-9]+");
                    int integer = in.nextInt();
                    detections.get(i).add((double) integer);
                    detections.get(i).add(x);
                    detections.get(i).add(y);


                    in = new Scanner(fileNames.get(i+1)).useDelimiter("[^0-9]+");
                    integer = in.nextInt();
                    detections.get(i+1).add((double) integer);
                    detections.get(i+1).add(x);
                    detections.get(i+1).add(y);
                }else{
                    Scanner in = new Scanner(fileNames.get(i+1)).useDelimiter("[^0-9]+");
                    int integer = in.nextInt();
                    detections.get(i+1).add((double) integer);
                    detections.get(i+1).add(x);
                    detections.get(i+1).add(y);
                }
            }

        }
        File file = new File("rsc\\"+inputResultFileName[0]);
        try {
            Scanner scanner = new Scanner(file);
            scanner.useLocale(Locale.US);
            while (scanner.hasNext()) {
                int x = scanner.nextInt();
                double y = scanner.nextDouble();
                double z = scanner.nextDouble();
                System.out.println("x="+x+", y="+y+", z="+z);
            }
        }
        catch(Exception e){

        }


///////////////////////////////////////////////////////////
      /*  Rect rect = Imgproc.boundingRect(contours.get(0));
        //Mat mask = new Mat(rect.height,rect.width,CV_8UC1);
        //erode(mask,mask,getStructuringElement(MORPH_RECT,new Size(10,10)));
        Mat mask = new Mat(image1,rect);
        Highgui.imwrite("rsc\\lena\\mask.jpg",mask);
        Mat tempScene = new Mat();
        image2.copyTo(tempScene);
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


    }

    private static void PreprocesImages() {
        absdiff(image1, image2,result);
        threshold(result,result1,20,255,THRESH_BINARY);
        //blur(result1,result2, new Size(20,20));
        erode(result1,result2,getStructuringElement(MORPH_RECT,new Size(1,1)));
        dilate(result2,result3,getStructuringElement(MORPH_RECT,new Size(40,40)));
    }

    static void LoadFiles(){
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                System.out.println("File " + listOfFiles[i].getName());
                char[] extension=new char[3];
                listOfFiles[i].getName().getChars(listOfFiles[i].getName().length()-3,listOfFiles[i].getName().length(),extension,0);
                if(String.valueOf(extension).equals("txt")){
                    inputResultFileName[0]=listOfFiles[i].getName();
                }else{
                    fileNames.add(listOfFiles[i].getName());
                }

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
    }
}