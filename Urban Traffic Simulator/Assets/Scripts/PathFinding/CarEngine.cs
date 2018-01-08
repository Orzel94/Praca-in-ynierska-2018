using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class CarEngine : MonoBehaviour {
    public GameObject path;
    public float maxSteerAngle = 45f;
    public WheelCollider wheelFL;
    public WheelCollider wheelFR;
    public WheelCollider wheelRL;
    public WheelCollider wheelRR;
    public float maxMotorTorque = 600f;
    public float maxBreakingTorque = 1200f;
    public float currentSpeed;
    public float maxSpeed = 50f;
    public Vector3 centerOfMass;

    private List<Transform> nodes;
    private int currentNode = 0;
    public Boolean isBreaking = false;

    [Header("Obstacles sensors")]
    public float sensorLenght = 10f;
    public Vector3 frontSensorPosition = new Vector3(0f, 1f, 4f);
    public float frontSideSensorPosition = 1.5f;
    public float frontSensorAngle = 30f;
    [Header("Cameras")]
    public GameObject tpc;
    public GameObject fpc;


    private PathScript pathScript;
    private GameObject node;
    // Use this for initialization
    void Start () {
        GetComponent<Rigidbody>().centerOfMass = centerOfMass;
        pathScript = path.GetComponent<PathScript>();
        node = pathScript.startPoint;
        fpc.GetComponent<Camera>().enabled = false;
        tpc.GetComponent<Camera>().enabled = false;
    }
	
	// Update is called once per frame
	void FixedUpdate () {
        Sensonrs();
        ApplySteer();
        if (isBreaking)
        {
            Breaking();
        }
        else
        {
            Drive();
        }


        CheckWayPointDistance();
        DestroyNotMoving();
	}

    private IEnumerator DestroyNotMoving()
    {
        if (currentSpeed==0)
        {
            yield return new WaitForSeconds(10);
            if (currentSpeed == 0)
            {
                Destroy(this.gameObject);
            }
        }
    }

    private void OnMouseDown()
    {
        GameObject.FindGameObjectWithTag("MainCamera").GetComponent<Camera>().enabled = false;
        fpc.GetComponent<Camera>().enabled = true;
    }
    private void Update()
    {
        if (tpc.GetComponent<Camera>().enabled || fpc.GetComponent<Camera>().enabled)
        {
            if (Input.GetKeyDown("c"))
            {
                SwitchCamera();
            }
        }
    }

    private void SwitchCamera()
    {
        Camera[] cameras = new Camera[200];
        Camera.GetAllCameras(cameras);
        if (fpc.GetComponent<Camera>().enabled)
        {
            tpc.GetComponent<Camera>().enabled = true;
            
            foreach (var item in cameras)
            {
                if (item!=null)
                {
                    item.enabled = false;
                }
            }
        }
        else
        {
            fpc.GetComponent<Camera>().enabled = true;
            foreach (var item in cameras)
            {
                item.enabled = false;
            }
        }
    }

    private void Sensonrs()
    {
        RaycastHit hit;
        Vector3 sensorStartPos = transform.position;
        sensorStartPos += transform.forward * frontSensorPosition.z;
        sensorStartPos += transform.up * frontSensorPosition.y;
        Vector3 sensorStartPosR = sensorStartPos;
        Vector3 sensorStartPosL = sensorStartPos;
        sensorStartPosL -= transform.right * frontSideSensorPosition;
        sensorStartPosR += transform.right * frontSideSensorPosition;
        Boolean directFrontHit = false;
        //front center
        if (Physics.Raycast(sensorStartPos, transform.forward,out hit, sensorLenght/10))
        {
            Debug.DrawLine(sensorStartPos, hit.point);
            directFrontHit = true;
        }
        //front right side
        if (Physics.Raycast(sensorStartPosR, transform.forward, out hit, sensorLenght/10))
        {
            Debug.DrawLine(sensorStartPosR, hit.point);
            directFrontHit = true;
        }
        //angle
        if (Physics.Raycast(sensorStartPosR, Quaternion.AngleAxis(frontSensorAngle,transform.up)*transform.forward, out hit, sensorLenght/10))
        {
            Debug.DrawLine(sensorStartPosR, hit.point);
            directFrontHit = true;
        }
        //front left side
        
        if (Physics.Raycast(sensorStartPosL, transform.forward, out hit, sensorLenght/10))
        {
            Debug.DrawLine(sensorStartPosL, hit.point);
            directFrontHit = true;
        }
        //angle
        if (Physics.Raycast(sensorStartPosL, Quaternion.AngleAxis(-frontSensorAngle, transform.up) * transform.forward, out hit, sensorLenght/10))
        {
            Debug.DrawLine(sensorStartPosL, hit.point);
            directFrontHit = true;
        }
        if (directFrontHit)
        {
            stopCar();
            currentSpeed = 0f;
            isBreaking = true;
        }
        else
        {
            isBreaking = false;
        }

    }

    private void stopCar()
    {
        GetComponent<Rigidbody>().velocity = new Vector3(0, 0, 0);
    }

    private void CheckWayPointDistance()
    {
        if (Vector3.Distance(transform.position, node.transform.position) < 1.5f)
        {
            int i = 0;
            System.Random rnd = new System.Random();
            int mode = 0;
            if (node.GetComponent<Node>().nextNode1 != null) mode += 1;
            if (node.GetComponent<Node>().nextNode2 != null) mode += 10;
            if (node.GetComponent<Node>().nextNode3 != null) mode += 100;
            if (mode == 000)
            {
                DestroyObject(this);
            }
            if (mode == 001)
            {
                node = node.GetComponent<Node>().nextNode1;
            }
            if (mode == 010)
            {
                node = node.GetComponent<Node>().nextNode2;
            }
            if (mode == 100)
            {
                node = node.GetComponent<Node>().nextNode3;
            }
            if (mode == 011)
            {
                i = rnd.Next(1, 3);
                if (i == 1)
                {
                    node = node.GetComponent<Node>().nextNode1;
                }
                else if (i == 2)
                {
                    node = node.GetComponent<Node>().nextNode2;
                }

            }
            if (mode == 101)
            {
                i = rnd.Next(1, 3);
                if (i == 1)
                {
                    node = node.GetComponent<Node>().nextNode1;
                }
                else if (i == 2)
                {
                    node = node.GetComponent<Node>().nextNode3;
                }
            }
            if (mode == 110)
            {
                i = rnd.Next(1, 3);
                if (i == 1)
                {
                    node = node.GetComponent<Node>().nextNode2;
                }
                else if (i == 2)
                {
                    node = node.GetComponent<Node>().nextNode3;
                }

            }
            if (mode == 111)
            {
                i = rnd.Next(1, 4);
                if (i == 1)
                {
                    node = node.GetComponent<Node>().nextNode1;
                }
                else if (i == 2)
                {
                    node = node.GetComponent<Node>().nextNode2;
                }
                else if (i == 3)
                {
                    node = node.GetComponent<Node>().nextNode3;
                }
            }

        }
    }

    private void Drive()
    {
        wheelRL.brakeTorque = 0;
        wheelRR.brakeTorque = 0;
        
        currentSpeed = 2f * Mathf.PI * wheelFL.radius * wheelFL.rpm * 60 / 1000;
        if (currentSpeed < maxSpeed)
        {
            wheelFL.motorTorque = maxMotorTorque;
            wheelFR.motorTorque = maxMotorTorque;
        }
        else
        {
            wheelFL.motorTorque = 0f;
            wheelFR.motorTorque = 0f;
            Breaking();
        }
    }

    private void ApplySteer()
    {
        Vector3 relativeVector = transform.InverseTransformPoint(node.transform.position);
        float newSteer = (relativeVector.x / relativeVector.magnitude) * maxSteerAngle;
        wheelFL.steerAngle = newSteer;
        wheelFR.steerAngle = newSteer;
    }
    private void Breaking()
    {
        
        wheelRL.brakeTorque = maxBreakingTorque;
        wheelRR.brakeTorque = maxBreakingTorque;
        wheelFL.motorTorque = 0f;
        wheelFR.motorTorque = 0f;

    }
}
