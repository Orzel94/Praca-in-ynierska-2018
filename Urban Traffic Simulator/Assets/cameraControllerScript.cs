using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using UnityEngine;

public class cameraControllerScript : MonoBehaviour {
    public GameObject mainCamera;
    public GameObject[] monitoringCameras;
    int currentCameraIndex;
    public string folder = "ScreenshotFolder";
    public int frameRate = 20;
    private Boolean capturing;
    private Dictionary<int, List<Vector3>> carPositions=new Dictionary<int, List<Vector3>>();
    public GameObject carFactory;
    private Camera currentCamera;
    // Use this for initialization
    void Start () {
        //myDocumentsPath + "/UrbanTraficSimulator/resultImages/";
        string myDocumentsPath = Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments);
        Directory.CreateDirectory(myDocumentsPath + "/UrbanTraficSimulator/resultImages/");
        folder = myDocumentsPath + "/UrbanTraficSimulator/resultImages/";
        currentCamera = mainCamera.GetComponent<Camera>();
        currentCameraIndex = 0;
        setDefaultCamera(mainCamera);
        capturing = false;

        // Set the playback framerate (real time will not relate to game time after this).
        Time.captureFramerate = frameRate;

        // Create the folder
        System.IO.Directory.CreateDirectory(folder);

    }
    void OnGUI()
    {
        if (!capturing)
        {
            if (GUILayout.Button("Start Capture"))
                capturing = !capturing;
        }
        else
        {
            if (GUILayout.Button("Stop Capture"))
            {
                capturing = !capturing;
                SaveData();
            }
                
        }
        
    }

    private void SaveData()
    {
        //myDocumentsPath + "/UrbanTraficSimulator/resultImages/";
        //string myDocumentsPath = Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments);
        //Directory.CreateDirectory(myDocumentsPath + "/UrbanTraficSimulator/resultImages/");
        int tryCounter= 0;
        while (true)
        {
            if (!File.Exists(folder + "\\results" + tryCounter.ToString() + ".txt"))
            {
                StreamWriter sw = File.CreateText(folder+"\\results"+tryCounter.ToString()+".txt");
                foreach (var item in carPositions)
                {
                    foreach (var pos in item.Value)
                    {
                        if (pos.x < 1 && pos.x > 0 && pos.y < 1 && pos.y > 0)
                        {
                            sw.WriteLine(item.Key + " " + pos.x + " " + pos.y);
                        }
                    }
                }
                sw.Close();
                carPositions = new Dictionary<int, List<Vector3>>();
                break;
            }
            else
            {
                tryCounter++;
            }
        }
    }

    void Awake()
    {
        Application.targetFrameRate = 20;
    }

    // Update is called once per frame
    void Update () {
        if (capturing)
        {
            // Append filename to folder name (format is '0005.png"')
            string name = string.Format("{0}\\{1:D04}.png", folder, Time.frameCount);
            TakeCarsPosition(Time.frameCount);
            // Capture the screenshot to the specified file.
            ScreenCapture.CaptureScreenshot(name);
        }
	}

    private void TakeCarsPosition(int frame)
    {
        List<Transform> cars = carFactory.GetComponentsInChildren<Transform>().Where(x => x.tag == "Player").ToList();

        List<Vector3> positions = new List<Vector3>();
        foreach (var item in cars)
        {
            positions.Add(currentCamera.WorldToViewportPoint(item.position));
        }
        carPositions.Add(frame, positions);
    }

    void setDefaultCamera(GameObject mainCamera)
    {
        currentCamera = mainCamera.GetComponent<Camera>();
        Camera[] cameras = new Camera[200];
        Camera.GetAllCameras(cameras);
        mainCamera.SetActive(true);
        mainCamera.GetComponent<Camera>().enabled = true;
        for (int i = 0; i < monitoringCameras.Length; i++)
        {
            if (mainCamera != monitoringCameras[i])
            {
                monitoringCameras[i].GetComponent<Camera>().enabled = false;
            }

        }
        foreach (var item in cameras)
        {
            if (item != null)
            {
                if (item != mainCamera.GetComponent<Camera>())
                {
                    item.enabled = false;
                }
            }
        }
       
    }
    private void FixedUpdate()
    {
        if (Input.GetKeyDown("m"))
        {
            setDefaultCamera(mainCamera);
        }
        if (Input.GetKeyDown("n"))
        {
            //if (mainCamera.activeSelf)
            //{
                currentCameraIndex++;
                if (currentCameraIndex == monitoringCameras.Length)
                {
                    currentCameraIndex = 0;
                    setDefaultCamera(mainCamera);
                }
                else
                {
                    setDefaultCamera(monitoringCameras[currentCameraIndex]);
                }
            //}
            
        }
    }

    //private void switchMonitoringCamera()
    //{
    //    currentCameraIndex++;
    //    if (currentCameraIndex==monitoringCameras.Length)
    //    {
    //        //setDefaultCamera();
    //    }
    //    else
    //    {
    //        monitoringCameras[currentCameraIndex].gameObject.SetActive(true);
    //        monitoringCameras[currentCameraIndex].GetComponent<Camera>().enabled = true;
    //        monitoringCameras[currentCameraIndex - 1].gameObject.SetActive(false);
    //    }


    //}
}
