using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class cameraControllerScript : MonoBehaviour {
    public GameObject mainCamera;
    public GameObject[] monitoringCameras;
    int currentCameraIndex;
	// Use this for initialization
	void Start () {

        currentCameraIndex = 0;
        setDefaultCamera(mainCamera);
        
	}
	
	// Update is called once per frame
	void Update () {
		
	}
    void setDefaultCamera(GameObject mainCamera)
    {
        
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
