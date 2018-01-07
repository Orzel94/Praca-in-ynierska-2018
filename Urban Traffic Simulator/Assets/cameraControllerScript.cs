using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class cameraControllerScript : MonoBehaviour {
    public GameObject mainCamera;
    public GameObject[] monitoringCameras;
	// Use this for initialization
	void Start () {
        for (int i = 1; i < monitoringCameras.Length; i++)
        {
            monitoringCameras[i].SetActive(false);
        }

        setDefaultCamera();
        if (Input.GetButtonDown("n"))
        {
            
        }
	}
	
	// Update is called once per frame
	void Update () {
		
	}
    void setDefaultCamera()
    {
        Camera[] cameras = new Camera[200];
        Camera.GetAllCameras(cameras);
        mainCamera.GetComponent<Camera>().enabled = true;
        foreach (var item in cameras)
        {
            if (item != mainCamera.GetComponent<Camera>())
            {
                item.enabled = false;
            }
        }
    }
    private void FixedUpdate()
    {
        if (Input.GetKeyDown("m"))
        {
            setDefaultCamera();
        }
    }
}
