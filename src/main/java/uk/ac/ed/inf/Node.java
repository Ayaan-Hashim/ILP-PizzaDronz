package uk.ac.ed.inf;

import java.util.ArrayList;

/**
 *This class is used to contain vital features that completely define a move that the drone makes.
 */
public class Node {
    /**
     * This final variable stores the location that the drone is at the beginning of the move.
     */
    public final LngLat location;

    /**
     * This stores the distance that the drone has travelled since it started attempting to get to its destination
     */
    public double g_scores;

    /**
     *This stores the euclidean distance from the current node to the destination the drone attempts to get to.
     */
    public double h_scores;

    /**
     * This stores the total score: the g_scores + the h_score.
     */
    public double f_scores;

    /**
     * This stores the list next nodes that the drone can get to from the current node.
     */
    public ArrayList<Node> nextNodes = new ArrayList<>();

    /**
     * This stores the parent Node of the current node, i.e., the node the drone was at before getting to
     * the current node.
     */
    public Node parent;

    /**
     * This stores the angle the drone turns in regard to the compass directions to execute the move.
     */
    public double angle;

    /**
     * This stores the destination the drone is attempting to find a path to.
     */
    public Node destinationNode;

    /**
     * This stores a boolean value that indicates whether the drone is in the central area or not.
     */
    public boolean inCentralArea;

    /**
     * This stores the value needed in order to compute ticks.
     */
    public long tickComputation;

    private final LngLat APPLETON_TOWER = new LngLat(-3.186874, 55.944494);

    /**
     * A parameterized constructor for Node, this constructor is used when we attempt to make a Node
     * for the destination of the path.
     * @param loc this stores the location that the node has (the location the drone attempts to get close to)
     */
    public Node(LngLat loc) {
        location = loc;

        // checks and stores whether the location of the node would be within the central area.
        inCentralArea = location.inCentralArea();
    }

    /**
     * A parameterized constructor for Node, this constructor is used when we make a node for a location
     * that is not the required destination nor is it close to the destination.
     * @param loc this stores the location that the node has (i.e., the location the drone is currently at)
     * @param destination this stores the node that depicts the destination of the path the drone attempts to find.
     */
    public Node(LngLat loc, Node destination) {
        location = loc;
        destinationNode = destination;

        //Calculating and storing the euclidean distance from the current location to the destination the
        // drone attempts to get to/ get close to.
        h_scores = loc.distanceTo(destinationNode.location);

        // checks and stores whether the location of the node would be within the central area.
        inCentralArea = location.inCentralArea();
    }

    /**
     * This method sets all the next nodes the  drone can travel to.
     */
    public void setNextNodes() {
        //Iterating over every direction the drone can move
        for (CompassDirection direction : CompassDirection.values()) {
            //Stores the location the drone would be at if it were to move at this direction.
            LngLat nextPos = location.nextPosition(direction);
            //Makes a new node for every one of the positions that the drone could get to.
            Node newNode = new Node(nextPos, destinationNode);
            //sets the parent of this next node to the current node
            newNode.parent = this;
            //sets the direction that the drone took to get the next node as the angle field of the next node
            newNode.angle = direction.getDirectionDegree();
            //Prunes the next nodes to make sure that they abide by the constraints
            if (pruneNextNodes(this,newNode)){
                /* adds the next nodes to a list of the next nodes the drone can travel to, only if
                the constraints are satisfied */
                nextNodes.add(newNode);
            }
        }
    }

    /**
     * This method checks if a point is *ON* the edge of a line.
     * The method was inspired by the GeeksForGeeks post:
     * <a href = "https://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/">on line-segment link</a>
     * @param p LngLat location that gets checked to be it on the line
     * @param q LngLat location that depicts the edge coordinate of the line
     * @param r LngLat location that depicts the other edge coordinate of the line
     * @return true if the point is on the line and false otherwise
     */
    private boolean onSegment(LngLat p, LngLat q, LngLat r) {
        return (q.lng() <= Math.max(p.lng(), r.lng()) && q.lng() >= Math.min(p.lng(), r.lng()) &&
                q.lat() <= Math.max(p.lat(), r.lat()) && q.lat() >= Math.min(p.lat(), r.lat()));
    }

    /**
     * This method checks id lines made by three LngLat coordinates are collinear,
     * have a clockwise or a counter-clock wise orientation.
     * This method was inspired by the GeeksForGeeks post:
     * <a href = "https://www.geeksforgeeks.org/orientation-3-ordered-points/">orientation link</a>
     * @param p LngLat location that whose orientations are to be checked.
     * @param q LngLat location that whose orientations are to be checked.
     * @param r LngLat location that whose orientations are to be checked.
     * @return 0 in case the points are collinear, 1 if they have a clockwise orientation and 2 if they have
     * a counter-clockwise orientation.
     */
    private int orientation(LngLat p, LngLat q, LngLat r) {
        double val = (q.lat() - p.lat()) * (r.lng() - q.lng()) -
                (q.lng() - p.lng()) * (r.lat() - q.lat());

        if (val == 0) return 0; // collinear

        return (val > 0) ? 1 : 2; // clock or counterclock wise
    }

    /**
     * This method checks if there is an intersection between four provided points.
     * Specifically, if the line segment p1q1 intersects p2q2.
     * The working of this method was taken from the GeeksForGeeks post :
     * <a href = "https://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/">Line Intersection link</a>
     * @param p1 The first coordinate of the first line segment which we check for intersection.
     * @param q1 The second coordinate of the first line segment which we check for intersection.
     * @param p2 The first coordinate of the second line segment which we check for intersection.
     * @param q2 The second coordinate of the second line segment which we check for intersection.
     * @return true if the line segments p1q2 and p2q2 intersects and false otherwise.
     */
    private boolean doIntersect(LngLat p1, LngLat q1, LngLat p2, LngLat q2)
    {
        // Calculates the orientations for the four points general and special cases
        int o1 = orientation(p1, q1, p2);
        int o2 = orientation(p1, q1, q2);
        int o3 = orientation(p2, q2, p1);
        int o4 = orientation(p2, q2, q1);

        // General case
        if (o1 != o2 && o3 != o4)
            return true;

        // Special Cases
        // p1, q1 and p2 are collinear and p2 lies on segment p1q1
        if (o1 == 0 && onSegment(p1, p2, q1)) return true;

        // p1, q1 and q2 are collinear and q2 lies on segment p1q1
        if (o2 == 0 && onSegment(p1, q2, q1)) return true;

        // p2, q2 and p1 are collinear and p1 lies on segment p2q2
        if (o3 == 0 && onSegment(p2, p1, q2)) return true;

        // p2, q2 and q1 are collinear and q1 lies on segment p2q2
        if (o4 == 0 && onSegment(p2, q1, q2)) return true;

        return false; // Doesn't fall in any of the above cases
    }

    /**
     * This method prunes the next nodes so that the nodes with which there would be an intersection with
     * the no-fly-zone are pruned out and the central area constraint is respected.
     * @param current The node that is depicting the drones current position
     * @param adjacent The node that is depicting the drones next position
     * @return true if the node should not be pruned out (further explored) and false otherwise.
     */
    private boolean pruneNextNodes(Node current, Node adjacent) {
        ArrayList<ArrayList<LngLat>> noFLyZonesList = NoFlyZones.allCoOrdinates;
        LngLat p1 = current.location;
        LngLat p2 = adjacent.location;
        for (ArrayList<LngLat> noFlyZone : noFLyZonesList) {
            for (int i = 0, j = noFlyZone.size() - 1; i < noFlyZone.size(); j = i++) {
                LngLat q1 = noFlyZone.get(i);
                LngLat q2 = noFlyZone.get(j);
                if (doIntersect(p1, p2, q1, q2)) {
                    return false;
                }
            }
        }
        return centralAreaPruning(adjacent);
    }

    private boolean centralAreaPruning(Node adjacent){
        if (destinationNode.location.equals(APPLETON_TOWER)){
            if(inCentralArea && !adjacent.location.inCentralArea()){
                return false;
            }
        }
        else{
            if(!inCentralArea && adjacent.location.inCentralArea()){
                return false;
            }
        }
        return true;
    }

}
