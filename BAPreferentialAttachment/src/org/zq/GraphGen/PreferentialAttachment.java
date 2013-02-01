/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zq.GraphGen;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import org.gephi.io.generator.spi.Generator;
import org.gephi.io.generator.spi.GeneratorUI;
import org.gephi.io.importer.api.ContainerLoader;
import org.gephi.io.importer.api.EdgeDraft;
import org.gephi.io.importer.api.NodeDraft;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;


/**
 *
 * @author zqlee1
 */
@ServiceProvider(service = Generator.class)
public class PreferentialAttachment implements Generator {

    protected ProgressTicket progress;
    protected boolean cancel = false;

    private int numInitNodes = 5;
    private int numNodes = 100;
    private int numEdges = 3;

    @Override
    public void generate(ContainerLoader container) {
        NodeDraft[] nodeList = new NodeDraft[numNodes];
        LinkedList<EdgeDraft> edgeList = new LinkedList();

        generateBA(container, nodeList, edgeList);

        // fill in the graph
        Iterator<EdgeDraft> edgeIter = edgeList.iterator();

        for(int i=0; i<nodeList.length; i++){
            container.addNode(nodeList[i]);
        }
        while(edgeIter.hasNext()){
            container.addEdge(edgeIter.next());
        }
    }

    private void generateBA(ContainerLoader container,
            NodeDraft[] nodeList, LinkedList<EdgeDraft> edgeList){

        Progress.start(progress, nodeList.length);

        NodeDraft curNode;
        // initialize the nodes
        for(int i=0; i<numNodes; i++){
            curNode = container.factory().newNodeDraft();
            curNode.setLabel("Node " + i);
            nodeList[i] = curNode;
        }
        // create the initial clique
        for(int i=0; i<numInitNodes; i++){
            for(int j=i+1; j<numInitNodes; j++){
                EdgeDraft e = container.factory().newEdgeDraft();
                e.setSource(nodeList[i]);
                e.setTarget(nodeList[j]);
                e.setType(EdgeDraft.EdgeType.UNDIRECTED);
            }
        }



        int curNodeIndex = numInitNodes;
        int[] nodeDeg = new int[numNodes];
        for(int i=0; i<curNodeIndex; i++){
            nodeDeg[i] = numInitNodes - 1;
        }
        int degSum = numInitNodes * (numInitNodes - 1);

        Progress.progress(progress, curNodeIndex);

        // create link with preferential attachment

        while(curNodeIndex < numNodes && !cancel){
            nodeDeg[curNodeIndex] = numEdges;
            //link the new edges according to preferential rule
            LinkedList targetList = new LinkedList();
            int numEdgeAdded = 0;
            int curTarget = 0;
            while(numEdgeAdded <numEdges && !cancel){
                boolean added = false;
                while(!added  && !cancel){
                    curTarget = prefAttach(Arrays.copyOfRange(nodeDeg, 0, curNodeIndex), degSum);
                    added = !targetList.contains(curTarget);
                }
                targetList.add(curTarget);
                nodeDeg[curTarget] += 1;

                //add the edge
                EdgeDraft e = container.factory().newEdgeDraft();
                e.setSource(nodeList[curNodeIndex]);
                e.setTarget(nodeList[curTarget]);
                edgeList.add(e);


                numEdgeAdded++;
            }

            degSum += numEdges + numEdges;//source + target
            curNodeIndex++;
            Progress.progress(progress, curNodeIndex);
        }

        Progress.finish(progress);
        progress = null;
    }

    private int prefAttach(int[] nodeDeg, int degSum){
        int target = 0;

        double[] weightList = new double[nodeDeg.length];
        for(int i=0; i<nodeDeg.length; i++){
            weightList[i] = (double)nodeDeg[i]/degSum;
        }

        double weightPointer = Math.random();
        double curSum = 0;

        while(curSum <= weightPointer){
            curSum += weightList[target];
            target++;
        }

        // adjust for the overshoot
        target--;

        return target;
    }

    public String getName() {
        return "Preferential Attachment";
    }

    public GeneratorUI getUI() {
        return Lookup.getDefault().lookup(PreferentialAttachmentUI.class);
    }

    public boolean cancel() {
        cancel = true;
        return true;
    }

    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progress = progressTicket;
    }

    public int getNumInitNodes(){
        return this.numInitNodes;
    }

    public void setNumInitNodes(int numInitNodes){
        this.numInitNodes = numInitNodes;
    }

    public int getNumNodes(){
        return this.numNodes;
    }

    public void setNumNodes(int numNodes){
        this.numNodes = numNodes;
    }

    public int getNumEdges(){
        return this.numEdges;
    }

    public void setNumEdges(int numEdges){
        this.numEdges = numEdges;
    }
 }
