/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zq.Mule;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
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
public class GetMule implements Statistics{

    private double redundancyRatio = 0;
    private BitSet[] adjMatrix;
    private int[] idmap;
    private int iterCount = 0;

    @Override
    public void execute(GraphModel gm, AttributeModel am) {
        //initialize and get the bit-representation of the graph
        Graph g = gm.getUndirectedGraph();
        int numNodes = g.getNodeCount();

        contructIDMap(g);
        adjMatrix = getBitRepresentation(g);
        int numRemainNodes = numNodes;
        BitSet subsmStatus = new BitSet(numNodes);


        boolean hasSubsumption = true;
        iterCount = 0;
        // for each un-subsumed node
        while(hasSubsumption){
            hasSubsumption = false;
            for(int i=0; i<numNodes; i++){
                if(!subsmStatus.get(i)){
                    //for each un-subsumed neighbour
                    for(int j=0; j<numNodes; j++){
                        if(i!=j && !subsmStatus.get(j) && adjMatrix[i].get(j)){
                            BitSet tempResult = (BitSet) adjMatrix[i].clone();
                            tempResult.and(adjMatrix[j]);
                            tempResult.or(subsmStatus);

                            BitSet tempSource = (BitSet) adjMatrix[i].clone();
                            tempSource.or(subsmStatus);
                            if(tempResult.equals(tempSource)){
                                subsmStatus.set(i);
                                numRemainNodes--;

                                hasSubsumption = true;
                                break;
                            }
                        }
                    }//end inner for
                }
            }//end outer for
            iterCount ++;
        }

        redundancyRatio = 1 - (double)numRemainNodes/(double)numNodes;

        //write result into node column
        AttributeTable nodeTable = am.getNodeTable();
        AttributeColumn ac = nodeTable.getColumn("Subsumed");
        if(ac == null){
            ac = nodeTable.addColumn("Subsumed", "Subsumed", AttributeType.BOOLEAN, AttributeOrigin.COMPUTED, false);
        }
        NodeIterable ni = g.getNodes();
        NodeIterator it = ni.iterator();
        int idPointer = 0;
        while(it.hasNext()){
            Node n = it.next();
            n.getAttributes().setValue(ac.getIndex(), subsmStatus.get(idPointer++));
        }
    }

    private int IDMapToJava(int gephiID){
        //converts gephi ID to java index
        return Arrays.binarySearch(idmap, gephiID);
    }

    private int IDMapToGephi(int javaID){
        return idmap[javaID];
    }

    private void contructIDMap(Graph g){
        int numNodes = g.getNodeCount();
        idmap = new int[numNodes];
        NodeIterable ni = g.getNodes();
        NodeIterator it = ni.iterator();

        int id = 0;
        while(it.hasNext()){
            Node n = it.next();
            idmap[id++] = n.getId();
        }
    }

    private BitSet[] getBitRepresentation(Graph g){
        int numNodes = g.getNodeCount();
        BitSet[] adjMatrix = new BitSet[numNodes];
        for(int i=0; i<numNodes; i++){
            adjMatrix[i] = new BitSet(numNodes);
            adjMatrix[i].set(i);
        }
        // set the bits
        EdgeIterable ei = g.getEdges();
        EdgeIterator it = ei.iterator();
        while(it.hasNext()){
            Edge edge = it.next();
            int sourceID = IDMapToJava(edge.getSource().getId());
            int targetID = IDMapToJava(edge.getTarget().getId());
            adjMatrix[sourceID].set(targetID);
            adjMatrix[targetID].set(sourceID);
        }

        return adjMatrix;
    }

    @Override
    public String getReport() {
        //return "MyReport = 1";
        String result = "";
        for(int i=0; i<adjMatrix.length; i++){
            result += adjMatrix[i].toString() + "\n";
        }
        return result;
    }

    public double getRedundancyRatio(){
        return this.redundancyRatio;
    }

    public int getIterCount(){
        return this.iterCount - 1;
    }
}
