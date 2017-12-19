using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class cameraControllerScript : MonoBehaviour {
    public GameObject mainCamera;
    public Camera main;
	// Use this for initialization
	void Start () {
        Camera[] cameras = new Camera[200];
        Camera.GetAllCameras(cameras);
        foreach (var item in cameras)
        {
            if (item!=main)
            {
                item.enabled = false;
            }
            
        }
        mainCamera.GetComponent<Camera>().enabled = true;
        main.enabled = true;
	}
	
	// Update is called once per frame
	void Update () {
		
	}
}
