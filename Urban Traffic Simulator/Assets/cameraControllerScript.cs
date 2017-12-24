using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class cameraControllerScript : MonoBehaviour {
    public GameObject mainCamera;
	// Use this for initialization
	void Start () {
        setDefaultCamera();
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
