/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zq.BrandesBetw;


import java.util.*;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.data.attributes.api.AttributeOrigin;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.graph.api.*;
import org.gephi.statistics.spi.Statistics;
/**
 *
 * @author zqlee1
 */
public class Brandes implements Statistics {

    private int[] idMap;

    @Override
    public void execute(GraphModel gm, AttributeModel am) {
        //setup the attribute table
        AttributeTable nodeTable = am.getNodeTable();
        AttributeColumn brandesColumn = nodeTable.getColumn("BRANDES");
        if(brandesColumn == null){
            brandesColumn = nodeTable.addColumn("BRANDES", "Brandes", AttributeType.DOUBLE, AttributeOrigin.COMPUTED, 0.0);
        }

        UndirectedGraph g = gm.getUndirectedGraph();
        NodeIterable nodes = g.getNodes();

        //setup the ID mapping
        idMap = new int[g.getNodeCount()];
        int id = 0;
        Iterator<Node> nodeIterator = nodes.iterator();
        while(nodeIterator.hasNext()){
            idMap[id] = nodeIterator.next().getId();
            id++;
        }

        //do the main computation
        double[] betweenness = brandesAlgo(gm);

        //set the result back to table
        nodes = g.getNodes();
        for(Node ni:nodes){
            ni.getAttributes().setValue(brandesColumn.getIndex(), betweenness[idMapToJava(ni.getId())]);
        }
    }

    private double[] brandesAlgo(GraphModel gm){
        UndirectedGraph g = gm.getUndirectedGraph();
        NodeIterable nodes = g.getNodes();
        int numNodes = g.getNodeCount();

        double[] betweenness = new double[numNodes];

        for(Node s:nodes){
            int sIndex = idMapToJava(s.getId());
            //initialize data structures
            Stack<Node> S = new Stack();
            LinkedList[] P = new LinkedList[numNodes];
            double[] sigma = new double[numNodes];
            double[] dist = new double[numNodes];
            double[] delta = new double[numNodes];
            LinkedList<Node> Q = new LinkedList();

            //initialize variables
            for(int i=0; i<numNodes; i++){
                P[i] = new LinkedList();
                sigma[i] = 0;
                dist[i] = -1;
                delta[i] = 0;
            }
            sigma[idMapToJava(s.getId())] = 1;
            dist[idMapToJava(s.getId())] = 0;

            Q.add(s);

            //start breadth first search
            while(!Q.isEmpty()){
                Node v = Q.remove();
                S.add(v);
                int vIndex = idMapToJava(v.getId());

                NodeIterable neighbors = g.getNeighbors(v);
                for(Node w:neighbors){
                    int wIndex = idMapToJava(w.getId());
                    // w found for the first time?
                    if(dist[wIndex] < 0){
                        Q.add(w);
                        dist[wIndex] = dist[vIndex] + 1;
                    }
                    //shortest path to w via v?
                    if(dist[wIndex] == dist[vIndex] + 1){
                        sigma[wIndex] += sigma[vIndex];
                        P[wIndex].add(v);
                    }
                }
            }

            // S returns vertices in order of non-increasing distance from s
            while(!S.isEmpty()){
                Node w = S.pop();
                int wIndex = idMapToJava(w.getId());
                for (Iterator it = P[wIndex].iterator(); it.hasNext();) {
                    Node v = (Node) it.next();
                    int vIndex = idMapToJava(v.getId());
                    delta[vIndex] += sigma[vIndex]/sigma[wIndex] * (1+delta[wIndex]);
                }

                if(wIndex != sIndex){
                    betweenness[wIndex] += delta[wIndex];
                }
            }
        }

        for(int i=0; i<betweenness.length; i++){
            betweenness[i] /= 2;
        }

        return betweenness;
    }

    private void printArray(double[] doubleArray){
        for(int i=0; i<doubleArray.length; i++){
            System.out.println(doubleArray[i]);
        }
    }
    private void printHeader(String msg){
        System.out.println("*********************************************");
        System.out.println(msg);
        System.out.println("*********************************************");
    }

    private int idMapToJava(int gephiID){
        return Arrays.binarySearch(idMap, gephiID);
    }
    private int idMapToGephi(int javaID){
        return idMap[javaID];
    }

    @Override
    public String getReport() {
        return null;
    }

}
