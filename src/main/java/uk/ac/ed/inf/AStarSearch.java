package uk.ac.ed.inf;

import java.util.*;

/**
 * This class is used to find the path that the drone should take going between any two points using a
 * weighted A* (A star) search algorithm.
 */
public class AStarSearch {
    /**
     * This stores the object of the type Node for the endpoint when the A Star search reaches the destination.
     */
    public static Node endNode;

    /**
     * This final long variable stores the start time of the program which would be utilised when we compute the ticks.
     */
    public final long PROGRAM_START_TIME = System.nanoTime();

    /**
     * This method backtracks once the path going to a desired destination is found. It does so in order to get the
     * moves made by the drone (the angle the drone moves in, the start and end point of each move, the ticks taken
     * in that move.
     * @return The list of Nodes that depicts the entire path taken in order to get to the destination.
     * The first element in this would be the start point when looking for the path. And the last element would be
     * a location close to the destination.
     */
    public List<Node> getPathBack() {
        if(endNode == null){
            return null;
        }

        // An arraylist that stores the path of the moves that the drone takes.
        List<Node> path = new ArrayList<>();

        //Backtracking from the end node(Node when the drone gets close to the destination) to get the path taken.
        for (Node node = endNode; node != null; node = node.parent) {
            //Backtracking from each node till a parent node is found that is null
            path.add(node);
        }

        /*The path list that was just generated through backtracking is reversed to get the order of the path correctly
        * the first element being the start location of the path and the last element is the destination that the drone
        * was supposed to get to.
        */
        Collections.reverse(path);

        // A new hover node is added when the drone reaches its final destination.
        Node hover = new Node(endNode.location);
        //The hover node has the parent of the end node
        hover.parent = endNode;
        //The angle taken in this hover node will be null
        hover.angle = Double.NaN;
        //An appropriate tick is set
        hover.tickComputation = System.nanoTime() - PROGRAM_START_TIME;

        //The hover node is then added to the path
        path.add(hover);

        return path;
    }


    /**
     * This method finds the path the drone should take when getting to the destination starting from a given location.
     * The implementation of the algorithm was inspired by the GitHub Repo:
     * <a href = "https://gist.github.com/raymondchua/8064159">A Star Search Algorithm, Java Implementation</a>
     * @param startLocation The location that the drone starts from while finding the path to the destination.
     * @param goal The Node depicting the destination that the drone attempts to get to.
     * @return The node that is a location close to the destination that the drone attempted to get to.
     */
    public Node pathFindingAlgorithm(LngLat startLocation, Node goal) {
        Set<Node> explored = new HashSet<>();

        /*A new node is made for the start point of searching for the path, the destination being a
         node depicting the final location the drone should reach. */
        Node source = new Node(startLocation,goal);

        //The parent node the start location
        source.parent = null;

        PriorityQueue<Node> queue = new PriorityQueue<>(new Comparator<Node>(){
            //override compare method for the NodeTwo
            public int compare(Node i, Node j){
                return Double.compare(i.f_scores, j.f_scores);
            }
        });

        //cost from start
        source.g_scores = 0;
        queue.add(source);

        while (!queue.isEmpty()) {
            //the NodeTwo in having the lowest f_score value
            Node current = queue.poll();
            current.tickComputation = System.nanoTime() - PROGRAM_START_TIME;

            explored.add(current);

            //goal found
            if (current.location.closeTo(goal.location)) {
                endNode = current;
                return endNode;
            }
            //check every child of current NodeTwo
            current.setNextNodes();
            for (Node nextNode : current.nextNodes) {
                double cost = 0.00015;
                double temp_g_scores = current.g_scores + cost;
                double temp_f_scores = temp_g_scores + nextNode.h_scores * 1.05;
                                /*if child NodeTwo has been evaluated and
                                the newer f_score is higher, skip*/
                if ((explored.contains(nextNode)) &&
                        (temp_f_scores >= nextNode.f_scores)) {
                    continue;
                }
                                /*else if child NodeTwo is not in queue or
                                newer f_score is lower*/
                else if ((!queue.contains(nextNode)) ||
                        (temp_f_scores < nextNode.f_scores)) {

                    //The parent node for the next node explored is set at the current node
                    nextNode.parent = current;
                    // The appropriate g_scores and f scores for the next Node is set.
                    nextNode.g_scores = temp_g_scores;
                    nextNode.f_scores = temp_f_scores;
                    queue.remove(nextNode);
                    queue.add(nextNode);
                }
            }
        }
        return null;
    }

}
