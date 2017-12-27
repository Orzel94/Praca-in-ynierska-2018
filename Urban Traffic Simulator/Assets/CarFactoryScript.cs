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
    public GameObject carPrefab4;
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
        carPrefabs.Add(carPrefab4);
        carPrefabs.Add(carPrefab5);
        carPrefabs.Add(carPrefab6);
        carPrefabs.Add(carPrefab7);
        carPrefabs.Add(carPrefab8);
        carPrefabs.Add(carPrefab9);
        for (int i = 0; i < carAmount; i+=9)
        {
            //Instantiate<GameObject>()
            foreach (var car in carPrefabs)
            {
                Instantiate<GameObject>(car, transform);
                yield return new WaitForSeconds(creationTime);
            }
            
        }
    }

    // Update is called once per frame
    void Update () {
		
	}
}
