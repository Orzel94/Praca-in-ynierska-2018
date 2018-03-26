using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class PathScript : MonoBehaviour {

    public Color lineColor;
    public GameObject startPoint;

    private List<Transform> nodes = new List<Transform>();
    private void Start()
    {
        Node[] nodes2 = GetComponentsInChildren<Node>();
        foreach (var item in nodes2)
        {
            item.GetComponentInChildren<MeshRenderer>().enabled = false;
        }
    }
    private void OnDrawGizmosSelected()
    {
        Gizmos.color = lineColor;
        Transform[] pathTransforms = GetComponentsInChildren<Transform>();
        Node[] nodes1 = GetComponentsInChildren<Node>();
        nodes = new List<Transform>();

        for (int i = 0; i < pathTransforms.Length; i++)
        {
            if (pathTransforms[i] != transform)
            {
                nodes.Add(pathTransforms[i]);
            }
        }

        for (int i = 0; i < nodes.Count; i++)
        {
            Vector3 currentNode = nodes[i].position;
            Vector3 previousNode = new Vector3(0, 0, 0);
            if (i > 0)
            {
                previousNode = nodes[i - 1].position;
            }
            else if (i == 0 && nodes.Count > 1)
            {
                previousNode = nodes[nodes.Count - 1].position;
            }

            //Gizmos.DrawLine(previousNode, currentNode);
            Gizmos.DrawWireSphere(currentNode, 0.3f);
        }
        for (int i = 0; i < nodes1.Length; i++)
        {
            if (nodes1[i].nextNode1!=null)
            {
                Gizmos.DrawLine(nodes1[i].transform.position, nodes1[i].nextNode1.transform.position);
            }
            if (nodes1[i].nextNode2 != null)
            {
                Gizmos.DrawLine(nodes1[i].transform.position, nodes1[i].nextNode2.transform.position);
            }
            if (nodes1[i].nextNode3 != null)
            {
                Gizmos.DrawLine(nodes1[i].transform.position, nodes1[i].nextNode3.transform.position);
            }
        }
    }
}
