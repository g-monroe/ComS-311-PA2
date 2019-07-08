import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

/**
 * @author Christopher Woods
 * @author Gavin Monroe
 * @author Andrew Tran
 */
public class CommunicationsMonitor {

    public HashMap<Integer, List<ComputerNode>> cm;
    public ArrayList<Triplet> comms;

    /**
     * Constructor with no parameters
     */
    public CommunicationsMonitor() {
        cm = new HashMap<>();
        comms = new ArrayList<Triplet>();
    }

    /**
     * inserts the triple (c1, c2, and a timestamp) into an ArrayList
     * creating a Triplet and adding to an ArrayList takes O(1) time
     * @param c1
     * @param c2
     * @param timestamp
     */
    public void addCommunication(int c1, int c2, int timestamp) {
        comms.add(new Triplet(c1, c2, timestamp));
    }

    /**
     * Constructs the data structure as specified in the Section 2. This
     * method should run in O(n + m log m) time
     */
    public void createGraph() {
        //Sort the triples by increasing timestamp
        mergeSort(comms);
        //Scan the triples in sorted order
        /*while(!comms.isEmpty()) {
            Triplet t = comms.remove(0);*/
        for(int n = 0; n < comms.size(); n++){
            Triplet t = comms.get(n);
            int c1 = t.c1;
            int c2 = t.c2;
            int timestamp = t.t;
            //Add/use a node for c1 and place it in the list if it doesn't exist
            ComputerNode node1 = new ComputerNode(c1, timestamp);
            List<ComputerNode> tempList = cm.get(c1);
            boolean c1added = true;
            if(tempList == null){
                tempList = new ArrayList<ComputerNode>();
                cm.put(c1, tempList);
            }
            if(tempList.isEmpty()){
                tempList.add(node1);
            }else if(tempList.get(tempList.size()-1).getTimestamp() != timestamp){
                tempList.add(node1);
            }else{
                c1added = false;
                node1 = tempList.get(tempList.size()-1);
            }
            //Add/use a node for c2 and place it in the list if it doesn't exist
            ComputerNode node2 = new ComputerNode(c2, timestamp);
            tempList = cm.get(c2);
            boolean c2added = true;
            if(tempList == null){
                tempList = new ArrayList<ComputerNode>();
                cm.put(c2, tempList);
            }
            if(tempList.isEmpty()){
                tempList.add(node2);
            }else if(tempList.get(tempList.size()-1).getTimestamp() != t.t){
                tempList.add(node2);
            }else{
                c2added = false;
                node2 = tempList.get(tempList.size()-1);
            }
            //Add a directed edge from (Ci,tk) to (Cj,tk).
            node1.outNeighbors.add(node2);
            //Add a directed edge from (Cj,tk) to (Ci,tk).
            node2.outNeighbors.add(node1);
            //Add an edge from the previous timestamp to the new one for c1
            tempList = cm.get(c1);
            if(tempList.size() >= 2 && c1added){
                tempList.get(tempList.size() - 2).outNeighbors.add(node1);
            }
            //Add an edge from the previous timestamp to the new one for c2
            tempList = cm.get(c2);
            if(tempList.size() >= 2 && c2added){
                tempList.get(tempList.size() - 2).outNeighbors.add(node2);
            }
        }
    }

    /**
     * Determines whether computer c2 could be infected by time y if computer c1 was infected at
     * time x. If so, the method returns an ordered list of ComputerNode objects that represents
     * the transmission sequence. This sequence is a path in graph G. The first ComputerNode
     * object on the path will correspond to c1. Similarly, the last ComputerNode object on the
     * path will correspond to c2. If c2 cannot be infected, return null.
     * @param c1
     * @param c2
     * @param x
     * @param y
     * @return
     */
    public List<ComputerNode> queryInfection(int c1, int c2, int x, int y) {
        if(y < x) return null; //time travel is not possible
        //find the starting node
        List<ComputerNode> c1l = cm.get(c1);
        if(c1l == null) return null;
        if(c1l.size() == 0) return null;
        ComputerNode start = null;
        for(int i = 0; i < c1l.size(); i++){
            if(c1l.get(i).getTimestamp() >= x){
                start = c1l.get(i);
                break;
            }
        }
        if(start == null) return null;
        //find the ending node
        List<ComputerNode> c2l = cm.get(c2);
        if(c2l == null) return null;
        if(c2l.size() == 0) return null;
        ComputerNode end = null;
        for(int i = c2l.size() - 1; i >= 0; i--){
            if(c2l.get(i).getTimestamp() <= y){
                end = c2l.get(i);
                break;
            }
        }
        if(end == null) return null;
        //do DFS
        DFS(cm, start);
        if(end.getVisit() != ComputerNode.visit.black) return null;
        //make path
        List<ComputerNode> path = new ArrayList<ComputerNode>();
        //return single node if c1 == c2
        if(c1 == c2) {
            path.add(start);
            return path;
        }
        ComputerNode cur = end;
        path.add(cur);
        while(cur != start){
            cur = cur.pred;
            path.add(cur);
        }
        //reverse path
        int size = path.size();
        for(int i = 0; i < size/2; i++){
            ComputerNode temp = path.get(i);
            path.set(i, path.get(size - i - 1));
            path.set(size - i - 1, temp);
        }
        return path;
    }

    /**
     * Returns a HashMap
     * that represents the mapping between an Integer and a list of ComputerNode objects.
     * The Integer represents the ID of some computer Ci,
     * while the list consists of pairs (Ci, t1), (Ci, t2), . . ., (Ci, tk ), represented by
     * ComputerNode objects, that specify that Ci has
     * communicated with other computers at times t1, t2, . . ., tk . The list for each computer must
     * be ordered by time; i.e., t1 < t2 < · · · < tk .
     * @return
     */
    public HashMap<Integer, List<ComputerNode>> getComputerMapping() {
        return cm;
    }

    /**
     * Returns the list of ComputerNode
     * objects associated with computer c by performing a lookup in the mapping.
     * @param c
     * @return
     */
    public List<ComputerNode> getComputerMapping(int c) {
        return cm.get(c); //returns ArrayList, implementation of List interface
    }

    /**
     * Depth-first search from a given node in the graph, visiting all nodes in that connected component.
     *
     * @param graph The graph to search from
     * @param start The node to start searching from
     */
    public void DFS(HashMap<Integer, List<ComputerNode>> graph, ComputerNode start) {
        //Loop through Graph, and set all to unvisited
        //White = Unvisited, Grey = Processing, Black = Visited
        for(int s : graph.keySet()) {
            List<ComputerNode> v = graph.get(s);
            for(ComputerNode c : v){
                c.setVisit(ComputerNode.visit.white);
                c.pred = null;
            }
        }
        //Visit from starting node
        DFSVisit(start);
        //Finished
    }

    /**
     * Basic DFSVisit with predecessor setting from CLRS
     *
     * @param v
     */
    public void DFSVisit(ComputerNode v) {
        //Set Processing and Time
        v.setVisit(ComputerNode.visit.grey);
        // v.setTime(time);
        //Loop through adj Nodes and Visit/Process nodes.
        for (ComputerNode adjNode : v.getOutNeighbors()) {
            if (adjNode.getVisit() == ComputerNode.visit.white) {
                adjNode.pred = v; //Set Pred.
                DFSVisit(adjNode);
            }
        }
        //After Done Set as Processed/Visited
        v.setVisit(ComputerNode.visit.black);
        //Finished
    }

    /**
     * merge sort using timestamps of triplets, sorting in non-decreasing order
     * @param a ArrayList to be sorted
     * @return the sorted ArrayList
     */
    public ArrayList<Triplet> mergeSort(ArrayList<Triplet> a) {
        int m;
        ArrayList<Triplet> left = new ArrayList<Triplet>();
        ArrayList<Triplet> right = new ArrayList<Triplet>();

        if(a.size() == 1) {
            return a;
        }
        m = a.size()/2;
        for(int i = 0; i < m; i++) {
            left.add(a.get(i));
        }
        for(int i = m; i < a.size(); i++) {
            right.add(a.get(i));
        }
        left = mergeSort(left);
        right = mergeSort(right);
        merge(left, right, a);

        return a;
    }

    /**
     * merges two sorted ArrayLists
     * @param left left subarray
     * @param right right subarray
     * @param a ArrayList to be sorted
     */
    public void merge(ArrayList<Triplet> left, ArrayList<Triplet> right, ArrayList<Triplet> a) {
        int l = 0, r = 0, i = 0, app;

        while(l < left.size() && r < right.size()) {
            if(left.get(l).t < right.get(r).t) {
                a.set(i, left.get(l));
                l++;
            }
            else {
                a.set(i, right.get(r));
                r++;
            }
            i++;
        }

        ArrayList<Triplet> append;
        if(l >= left.size()) {
            append = right;
            app = r;
        }
        else {
            append = left;
            app = l;
        }
        for(int j = app; j < append.size(); j++) {
            a.set(i, append.get(j));
            i++;
        }
    }
}

/**
 * Class to store added Communication data, with c1 representing the first Computer, c2 representing the second,
 * and t representing the timestamp of the communication.
 */
class Triplet {

    int c1;
    int c2;
    int t;

    Triplet(){
        //do nothing
    }

    Triplet(int ChristopherWoods, int GavinMonroe, int AndrewTran){
        //THE OG TRIPLET
        c1 = ChristopherWoods;
        c2 = GavinMonroe;
        t = AndrewTran;
    }
}
