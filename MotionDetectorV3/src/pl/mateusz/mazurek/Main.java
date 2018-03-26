package pl.mateusz.mazurek;

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


    static Mat image1;
    static Mat image2;
    static Mat result;
    static Mat result1;
    static Mat result2;
    static Mat result3;

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);


        result=new Mat();
        result1=new Mat();
        result2=new Mat();
        result3=new Mat();

        Mat image1=Highgui.imread("rsc\\3372.png",Highgui.CV_LOAD_IMAGE_GRAYSCALE);
        Mat image2=Highgui.imread("rsc\\3373.png",Highgui.CV_LOAD_IMAGE_GRAYSCALE);
        Mat res1=new Mat();

        System.out.println("Welcome to MotionDetector V3..... Let's start!");
        System.out.println("Detecting files...");
        LoadFiles();
        ResultLoad();
        System.out.println("Motion detecting...");
        Detect();
        System.out.println("Saving results...");
        SaveResults();
        System.out.println("Calculating accuracy...");
        TestResults();
        System.out.println("All done! Check \"rsc\" directory for results.");

    }

    private static void TestResults() {
        int all=fileResults.size();
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

        for (Iterator<ArrayList<Integer>> it = fileResults.iterator();it.hasNext();) {
            ArrayList<Integer> res = it.next();
            boolean frameMatch=false;
            boolean match=false;
            for (Iterator<ArrayList<Integer>> it1 = detections.iterator();it1.hasNext();) {
                ArrayList<Integer> det = it1.next();
                if (res.get(0).equals(det.get((0)))){
                    frameMatch=true;
                    int x=det.get(1);
                    int y=det.get(2);

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
                        it.remove();
                        it1.remove();
                        break;
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
            out.println("FP: "+FP+" FN: "+(all-TP));
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
        Rect rect = Imgproc.boundingRect(contours.get(j));
        Moments moment = Imgproc.moments(contours.get(j));
        double area = moment.get_m00();
        if (area>10) {
            x = (moment.get_m10() / area);
            y = (moment.get_m01() / area);

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
        }else if(rect.x!=1&&rect.y!=1){
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
                MatOfPoint points = new MatOfPoint(p,p1,p2,p3);
                Rect rect = Imgproc.boundingRect(points);


                i++;
            }
        }
        catch(Exception e){
            System.out.println("file open failed....");
        }
    }

    private static void PreprocesImages() {
        absdiff(image1, image2,result);
        Highgui.imwrite("rsc\\diff.jpg",result);
        threshold(result,result1,20,255,THRESH_BINARY);
        Highgui.imwrite("rsc\\maskRes.jpg",result1);
        erode(result1,result2,getStructuringElement(MORPH_RECT,new Size(3,3)));
        Highgui.imwrite("rsc\\maskRes.jpg",result2);
        dilate(result2,result3,getStructuringElement(MORPH_RECT,new Size(40,40)));
        Highgui.imwrite("rsc\\maskRes.jpg",result3);
    }

    static void LoadFiles(){
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                char[] extension=new char[3];
                listOfFiles[i].getName().getChars(listOfFiles[i].getName().length()-3,listOfFiles[i].getName().length(),extension,0);
                if(String.valueOf(extension).equals("txt")){
                    inputResultFileName[0]=listOfFiles[i].getName();
                }else{
                    fileNames.add(listOfFiles[i].getName());
                }

            } else if (listOfFiles[i].isDirectory()) {
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

}