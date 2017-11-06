using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Node : MonoBehaviour {
    public bool isRespawnNode = false;
    public bool isEndNode = false;
    public Vector3 currentPosition;
    public GameObject nextNode1;
    public GameObject nextNode2;
    public GameObject nextNode3;
    private void Start()
    {
        currentPosition = GetComponent<Transform>().position;
    }
}
