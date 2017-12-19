using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class CarEngine : MonoBehaviour {
    public Transform path;
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
    // Use this for initialization
    void Start () {
        GetComponent<Rigidbody>().centerOfMass = centerOfMass;
        Transform[] pathTransforms = path.GetComponentsInChildren<Transform>();
        nodes = new List<Transform>();

        for (int i = 0; i < pathTransforms.Length; i++)
        {
            if (pathTransforms[i] != path.transform)
            {
                nodes.Add(pathTransforms[i]);
            }
        }
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
        if (fpc.GetComponent<Camera>().enabled)
        {
            fpc.GetComponent<Camera>().enabled = false;
            tpc.GetComponent<Camera>().enabled = true;
        }
        else
        {
            fpc.GetComponent<Camera>().enabled = true;
            tpc.GetComponent<Camera>().enabled = false;
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
        if (Physics.Raycast(sensorStartPos, transform.forward,out hit, sensorLenght))
        {
            Debug.DrawLine(sensorStartPos, hit.point);
            directFrontHit = true;
        }
        //front right side
        if (Physics.Raycast(sensorStartPosR, transform.forward, out hit, sensorLenght))
        {
            Debug.DrawLine(sensorStartPosR, hit.point);
            directFrontHit = true;
        }
        //angle
        if (Physics.Raycast(sensorStartPosR, Quaternion.AngleAxis(frontSensorAngle,transform.up)*transform.forward, out hit, sensorLenght))
        {
            Debug.DrawLine(sensorStartPosR, hit.point);
        }
        //front left side
        
        if (Physics.Raycast(sensorStartPosL, transform.forward, out hit, sensorLenght))
        {
            Debug.DrawLine(sensorStartPosL, hit.point);
            directFrontHit = true;
        }
        //angle
        if (Physics.Raycast(sensorStartPosL, Quaternion.AngleAxis(-frontSensorAngle, transform.up) * transform.forward, out hit, sensorLenght))
        {
            Debug.DrawLine(sensorStartPosL, hit.point);
        }
        if (directFrontHit)
        {
            isBreaking = true;
        }
        else
        {
            isBreaking = false;
        }

    }

    private void CheckWayPointDistance()
    {
        if (Vector3.Distance(transform.position, nodes[currentNode].position)<1.5f)
        {
            if (currentNode==nodes.Count-1)
            {
                currentNode = 0;
            }
            else
            {
                currentNode++;
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
        }
    }

    private void ApplySteer()
    {
        Vector3 relativeVector = transform.InverseTransformPoint(nodes[currentNode].position);
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
