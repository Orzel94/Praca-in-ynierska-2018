using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class CarFactoryScript : MonoBehaviour {
    public int carAmount;
    public int creationTime;
    public GameObject carPrefab1;
    public GameObject carPrefab2;
    public GameObject carPrefab3;
    public GameObject carPrefab5;
    public GameObject carPrefab6;
    public GameObject carPrefab7;
    public GameObject carPrefab8;
    public GameObject carPrefab9;



    // Use this for initialization
    void Start () {
        StartCoroutine(GenerateCars());
    }

    IEnumerator GenerateCars()
    {
        List<GameObject> carPrefabs = new List<GameObject>();
        carPrefabs.Add(carPrefab1);
        carPrefabs.Add(carPrefab2);
        carPrefabs.Add(carPrefab3);
        carPrefabs.Add(carPrefab5);
        carPrefabs.Add(carPrefab6);
        carPrefabs.Add(carPrefab7);
        carPrefabs.Add(carPrefab8);
        carPrefabs.Add(carPrefab9);
        int i = 0;
        for (;;)
        {
            //Instantiate<GameObject>()
            foreach (var car in carPrefabs)
            {
                Instantiate<GameObject>(car, new Vector3(401.06f, 1.744f, 290.35f),new Quaternion(0,180f,0,0), transform);
                ++i;
                if (i > carAmount) break;
                yield return new WaitForSeconds(creationTime);
            }
            if (i > carAmount) break;

        }
    }

    // Update is called once per frame
    void Update () {
		
	}
}
