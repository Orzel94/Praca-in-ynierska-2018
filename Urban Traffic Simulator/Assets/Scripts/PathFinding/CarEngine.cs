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
    public float maxMotorTorque = 25f;
    public float maxBreakingTorque = 1200f;
    public float currentSpeed;
    public float maxSpeed = 10f;
    public Vector3 centerOfMass;

    private List<Transform> nodes;
    private int currentNode = 0;
    public Boolean isBreaking = false;

    [Header("Obstacles sensors")]
    public float sensorLenght = 10f;
    public float frontSensorPosition = 4f;
    public float frontSideSensorPosition = 2f;
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
    }
	
	// Update is called once per frame
	void FixedUpdate () {
        Sensonrs();
        ApplySteer();
        Breaking();
        Drive();
        CheckWayPointDistance();
	}

    private void Sensonrs()
    {
        RaycastHit hit;
        Vector3 sensorStartPos = transform.position;
        sensorStartPos.z += frontSensorPosition;
        //front center
        if (Physics.Raycast(sensorStartPos, transform.forward,out hit, sensorLenght))
        {
            Debug.DrawLine(sensorStartPos, hit.point);
        }
        //front right side
        sensorStartPos.x += frontSideSensorPosition;
        if (Physics.Raycast(sensorStartPos, transform.forward, out hit, sensorLenght))
        {
            Debug.DrawLine(sensorStartPos, hit.point);
        }
        //front left side
        sensorStartPos.x -= 2*frontSideSensorPosition;
        if (Physics.Raycast(sensorStartPos, transform.forward, out hit, sensorLenght))
        {
            Debug.DrawLine(sensorStartPos, hit.point);
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
        currentSpeed = 2f * Mathf.PI * wheelFL.radius * wheelFL.rpm * 60 / 1000;
        if (currentSpeed < maxSpeed && !isBreaking)
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
        if (isBreaking)
        {
            wheelRL.brakeTorque = maxBreakingTorque;
            wheelRR.brakeTorque = maxBreakingTorque;
        }
        else
        {
            wheelRL.brakeTorque = 0;
            wheelRR.brakeTorque = 0;
        }
    }
}
