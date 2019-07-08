import java.util.List;
import java.util.ArrayList;

/**
 * @author Christopher Woods
 * @author Gavin Monroe
 * @author Andrew Tran
 */
public class ComputerNode {

    public enum visit{
        white, black, grey, nil; //for DFS
    }
    public int ID;
    public int timestamp;
    public ComputerNode pred;
    public visit state;
    public List<ComputerNode> outNeighbors;

    /**
     * constructs a computer node
     * @param ID computer
     * @param timestamp time of communication
     */
    public ComputerNode(int ID, int timestamp) {
        this.ID = ID;
        this.timestamp = timestamp;
        this.state = visit.nil;
        this.outNeighbors = new ArrayList<ComputerNode>();
    }

    /**
     *
     * @return ID of associated computer
     */
    public int getID() {
        return ID;
    }

    /**
     *
     * @return the timestamp associated with this node
     */
    public int getTimestamp() {
        return timestamp;
    }

    // Returns a list of ComputerNode objects
    //to which there is outgoing edge from this ComputerNode object.

    /**
     *
     * @return a list of ComputerNode objects to which there is
     * and outgoing edge from this ComputerNode object
     */
    public List<ComputerNode> getOutNeighbors() {
        return outNeighbors; //returns ArrayList, which is an implementation of List interface
    }

    /**
     * Set color of node for DFS reference
     *
     * @param input Color of node
     */
    public void setVisit(visit input){
        this.state = input;
    }

    /**
     * Get color of node in terms of DFS
     *
     * @return Color of node
     */
    public visit getVisit(){
        return this.state;
    }
}
