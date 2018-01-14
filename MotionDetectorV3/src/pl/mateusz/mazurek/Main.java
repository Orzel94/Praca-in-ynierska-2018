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
import java.io.PrintWriter;
import java.util.*;

import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import static java.lang.Math.abs;
import static org.opencv.core.Core.*;
import static org.opencv.imgproc.Imgproc.*;


public class Main {
    static File folder = new File("rsc");
    static File[] listOfFiles = folder.listFiles();
    static ArrayList<String> fileNames=new ArrayList<>();
    static String[] inputResultFileName=new String[1];
    static ArrayList<Integer> frames = new ArrayList<>();
    static int TP=0;
    static int TN=0;
    static int FP=0;
    static int FN=0;
    static ArrayList<ArrayList<Integer>> fileResults=new ArrayList<>();
    static ArrayList<ArrayList<Integer>> detections=new ArrayList<>();

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

        //TemplateMatching();

        result=new Mat();
        result1=new Mat();
        result2=new Mat();
        result3=new Mat();
        System.out.println("Welcome to MotionDetector V3..... Let's start!");

        LoadFiles();
        for (String item : fileNames ) {
            System.out.println(item);
        }



        ResultLoad();
        Detect();
        SaveResults();
        TestResults();



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

    private static void TestResults() {
        for (int fr:frames) {
            boolean cont=false;
            for (ArrayList<Integer> res:fileResults){
                if (res.get(0)==fr){
                    cont=true;
                    break;
                }
            }
            for (ArrayList<Integer> det:detections){
                if (det.get(0)==fr){
                    cont=true;
                    break;
                }
            }
            if (cont) continue;;
            TN++;
        }

        //for (ArrayList<Integer> res:fileResults) {
        for (Iterator<ArrayList<Integer>> it = fileResults.iterator();it.hasNext();) {
            ArrayList<Integer> res = it.next();
            boolean frameMatch=false;
            boolean match=false;
            //for (ArrayList<Integer> det:detections) {
            for (Iterator<ArrayList<Integer>> it1 = detections.iterator();it1.hasNext();) {
                ArrayList<Integer> det = it1.next();
                if (res.get(0).equals(det.get((0)))){
                    frameMatch=true;
                    int x=det.get(1);
                    int y=det.get(2);
                    //int w=abs(det.get(1)-det.get(3));
                    //int h=abs(det.get(2)-det.get(4));
                    int w=det.get(3);
                    int h=det.get(4);

                    int x1=res.get(1);
                    int y1=res.get(2);
                    int w1=abs(res.get(1)-res.get(3));
                    int h1=abs(res.get(2)-res.get(4));


                    int centerX=x+w/2;
                    int centerY=y+h/2;
                    int center1X=x1+w1/2;
                    int center1Y=y1-h1/2;


                    if(abs(centerX-center1X)<w&&w1>abs(centerX-center1X)){
                        if (abs(centerY-center1Y)<h&&h1>abs(centerY-center1Y)){
                            match=true;
                        }
                    }

                    if(match){
                        TP++;
                        //it.remove();
                        it1.remove();
                    }
                }

            }
            if(!frameMatch&&!match){
                FN++;
            }
        }
        for (ArrayList<Integer> det:detections) {
            FP++;
        }
        try {
            PrintWriter out = new PrintWriter("rsc\\classificationResults.txt");
            out.println("TP: "+TP+" TN: "+TN);
            out.println("FP: "+FP+" FN: "+FN);
            out.close();
        }
        catch (Exception e){
            System.out.println("Classificators saving failed....");
        }
    }
    private static void SaveResults() {
        try {
            PrintWriter out = new PrintWriter("rsc\\detectionResults.txt");
            for (ArrayList<Integer> item:detections) {
                out.println(item.get(0).toString()+" "+item.get(1).toString()+" "+item.get(2).toString()+" "+item.get(3).toString()+" "+item.get(4).toString());
            }
            out.close();
        }
        catch(Exception e){
            System.out.println("Result save failed....");
        }
    }

    private static void Detect() {
        Integer detectionIndex=0;
        for(int i=0;i<fileNames.size()-1;++i) {
            String frame1 = "rsc\\"+fileNames.get(i);
            String frame2 = "rsc\\"+fileNames.get(i+1);

            image1 = Highgui.imread(frame1, Highgui.CV_LOAD_IMAGE_GRAYSCALE);
            image2 = Highgui.imread(frame2, Highgui.CV_LOAD_IMAGE_GRAYSCALE);

            PreprocesImages();

            //tracking
            ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
            Imgproc.findContours(result3, contours, new Mat(), Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);


            if (contours.size()!=0) {
                for (int j = 0; j < contours.size(); j++) {
                    detectionIndex = getDetectionCoords(contours, j, i, detectionIndex);
                }
            }
        }
    }

    private static Integer getDetectionCoords(ArrayList<MatOfPoint> contours, int j, int i,Integer detectionIndex) {
        Double x=0.0;
        Double y=0.0;
        Rect rect = Imgproc.boundingRect(contours.get(0));
        Moments moment = Imgproc.moments(contours.get(j));
        double area = moment.get_m00();
        if (area>40) {
            System.out.println("area: " + String.valueOf(area));
            x = (moment.get_m10() / area);
            System.out.println("x: " + String.valueOf(x));
            y = (moment.get_m01() / area);
            System.out.println("y: " + String.valueOf(y));
            System.out.println("x1: " + String.valueOf(rect.x));
            System.out.println("y1: " + String.valueOf(rect.y));
            System.out.println("h1: " + String.valueOf(rect.height));
            System.out.println("w1: " + String.valueOf(rect.width));
        }
        if(i==0){
            Scanner in = new Scanner(fileNames.get(i)).useDelimiter("[^0-9]+");
            int integer = in.nextInt();
            if (detectionIndex!=0) detectionIndex++;
            detections.add(new ArrayList<>());
            detections.get(detectionIndex).add(integer);
            detections.get(detectionIndex).add(rect.x);
            detections.get(detectionIndex).add(rect.y);
            detections.get(detectionIndex).add(rect.height);
            detections.get(detectionIndex).add(rect.width);
            Mat mask = new Mat(image1,rect);
            Highgui.imwrite("rsc\\mask.jpg",mask);


            in = new Scanner(fileNames.get(i+1)).useDelimiter("[^0-9]+");
            integer = in.nextInt();
            detections.add(new ArrayList<>());
            detectionIndex++;
            detections.get(detectionIndex).add(integer);
            detections.get(detectionIndex).add(rect.x);
            detections.get(detectionIndex).add(rect.y);
            detections.get(detectionIndex).add(rect.height);
            detections.get(detectionIndex).add(rect.width);
        }else{
            Scanner in = new Scanner(fileNames.get(i+1)).useDelimiter("[^0-9]+");
            int integer = in.nextInt();
            detections.add(new ArrayList<>());
            if (detectionIndex!=0) detectionIndex++;
            detections.get(detectionIndex).add(integer);
            detections.get(detectionIndex).add(rect.x);
            detections.get(detectionIndex).add(rect.y);
            detections.get(detectionIndex).add(rect.height);
            detections.get(detectionIndex).add(rect.width);
        }
        return detectionIndex;
    }

    private static void ResultLoad() {
        File file = new File("rsc\\"+inputResultFileName[0]);
        try {
            Scanner scanner = new Scanner(file);
            scanner.useLocale(Locale.US);
            int i =0;
            while (scanner.hasNext()) {
                int f = scanner.nextInt();
                int x = scanner.nextInt();
                int y = scanner.nextInt();
                int x1 = scanner.nextInt();
                int y1 = scanner.nextInt();
                ArrayList<Integer> arr1=new ArrayList<>();
                arr1.add(f);
                arr1.add(x);
                arr1.add(y);
                arr1.add(x1);
                arr1.add(y1);
                fileResults.add(arr1);
                Point p= new Point(x,y);
                Point p1= new Point(x1,y);
                Point p2= new Point(x,y1);
                Point p3= new Point(x1,y1);
               /* MatOfPoint points = new MatOfPoint(p,p1,p2,p3);
                Rect rect = Imgproc.boundingRect(points);
                image1=Highgui.imread("rsc\\0694.png");
                Mat mask = new Mat(image1,rect);
                Highgui.imwrite("rsc\\maskRes.jpg",mask);*/

                i++;
                System.out.println("frame="+f+", x="+x+", y="+y+", x1="+x1+", y1="+y1);
            }
        }
        catch(Exception e){
            System.out.println("file open failed....");
        }
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
        for (String item:fileNames) {
            Scanner in = new Scanner(item).useDelimiter("[^0-9]+");
            int integer = in.nextInt();
            frames.add(integer);
        }

    }
    static void TemplateMatching(){
        Mat source;
        Mat template;
        String frame1;
        String frame2;

        frame1 = "rsc\\lena\\"+"source.jpg";
        frame2 = "rsc\\lena\\"+"template.jpg";

        source = Highgui.imread(frame1, Highgui.CV_LOAD_IMAGE_GRAYSCALE);
        template = Highgui.imread(frame2, Highgui.CV_LOAD_IMAGE_GRAYSCALE);
        Highgui.imwrite("rsc\\lena\\s1.jpg",source);
        Highgui.imwrite("rsc\\lena\\t1.jpg",template);
        Mat resultMatching = new Mat();
        int matchMethod=Imgproc.TM_CCOEFF_NORMED;

        Imgproc.matchTemplate(source,template,resultMatching,matchMethod);
        Highgui.imwrite("rsc\\lena\\s2.jpg",source);
        Highgui.imwrite("rsc\\lena\\t2.jpg",template);
        Highgui.imwrite("rsc\\lena\\resMatch.jpg",resultMatching);
        MinMaxLocResult mmr = Core.minMaxLoc(resultMatching);
        Point matchLoc=mmr.maxLoc;

        rectangle(source,matchLoc,new Point(matchLoc.x+template.cols(),matchLoc.y+template.rows()),new Scalar(255,255,255));
        Highgui.imwrite("rsc\\lena\\resMatch1.jpg",resultMatching);
    }
}