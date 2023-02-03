package uk.ac.ed.inf;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

/**
 * This class is responsible for generating the path that the drone takes to deliver the maximum amount
 * of orders possible for a certain date.
 */
public class Delivery {
    /**
     * A static list that contains all the orders in the day.
     */
    protected static ArrayList<Order> ordersToDeliver;

    /**
     * A final static LngLat variable that stores the location of Appleton Tower.
     */
    private static final LngLat APPLETON_TOWER = new LngLat(-3.186874, 55.944494);

    /**
     * A private variable that prioritises the closest deliveries the most, making it so that
     * the maximum number of deliveries can be made by the drone.
     */
    private static final PriorityQueue<Order> sortOrders = new PriorityQueue<>(new OrderPrioritisation());

    /**
     * A static final HashMap that stores the path the drone took as the value for the key of the
     * Order's unique hexadecimal order number.
     */
    protected static final HashMap<String, List<Node>> flightpathOrderMap = new HashMap<>();

    /**
     * A static private field to store the date of the drone's operation.
     */
    private static String date;

    /**
     * A list containing all the moves the drone makes to complete all deliveries on a day.
     */
    protected static final List<Node> movesMade = new ArrayList<>();

    /**
     * A list containing all the orders that the drone completes on a day.
     */
    private final List<Order> ordersDelivered = new ArrayList<>();

    /**
     * A parameterised constructor that gets all the orders that the drone has to try to deliver.
     *
     * @param dayExtension the day for which the drone attempts to get and deliver the orders for.
     */
    public Delivery(String dayExtension) throws IOException {
        //add the date to the url so that the drone knows which orders it has to deliver.
        String urlExtension = "orders/" + dayExtension;
        //gets and stores the orders that the drone attempts to deliver.
        ordersToDeliver = RetrieveServerData.getExtensionDataFromDefaultURL(urlExtension, new TypeReference<>() {});
        //saves the date
        date = dayExtension;
        //set the priority queue
        setOrderPriorityQueue();
        //Makes the path for most possible orders in the day
        deliveriesForDay();
        System.out.println("Delivered orders: " + ordersDelivered.size());
        System.out.println(movesMade.size());
        //Write the JSON and GeoJSon files
        writeFiles();
    }


    /**
     * This method makes the path that the drone would take in order to deliver the maximum number of
     * orders it possibly can.
     * @throws MalformedURLException when it is not able to retrieve the restaurant for an order.
     */
    private void deliveriesForDay() throws MalformedURLException{
        //start location for delivering the orders for the day is
        LngLat startLocation = APPLETON_TOWER;
        // An object to find the paths
        AStarSearch search = new AStarSearch();
        Node goBack = new Node(APPLETON_TOWER);

        while (!sortOrders.isEmpty()) {
            Order order = sortOrders.poll();
            Restaurant orderRestaurant = order.getRestaurant();
            LngLat restaurantLocation = orderRestaurant.getRestaurantLngLat();
            Node restaurantDelivery = new Node(restaurantLocation);
            Node pathToRestaurantEndNode = search.pathFindingAlgorithm(startLocation, restaurantDelivery);
            List<Node> pathToRestaurant = search.getPathBack();
            List<Node> pathToRestaurantFixed = pathToRestaurant.subList(1, pathToRestaurant.size());
            Node pathFromRestaurantEndNode = search.pathFindingAlgorithm(pathToRestaurantEndNode.location, goBack);
            List<Node> pathFromRestaurant = search.getPathBack();
            List<Node> pathFromRestaurantFixed = pathFromRestaurant.subList(1, pathFromRestaurant.size());
            startLocation = pathFromRestaurantEndNode.location;

            if (movesMade.size() + pathToRestaurantFixed.size() + pathFromRestaurantFixed.size() <= 2000) {
                order.setOutcomeDelivered(OrderOutcome.Delivered);
                movesMade.addAll(pathToRestaurantFixed);
                movesMade.addAll(pathFromRestaurantFixed);
                ordersDelivered.add(order);

                List<Node> allPaths = new ArrayList<>();
                allPaths.addAll(pathToRestaurantFixed);
                allPaths.addAll(pathFromRestaurantFixed);
                flightpathOrderMap.put(order.orderNo(), allPaths);
            }
        }
    }

    /**
     * this function calls all the relevant methods from teh OutputFileWriter to write the files
     * @throws IOException
     */
    private void writeFiles() throws IOException {
        OutputFileWriter.writeFlightpathFile(date, flightpathOrderMap);
        OutputFileWriter.deliveriesJsonWriter(ordersToDeliver, date);
        OutputFileWriter.outputGeoJsonWriter(movesMade, date);
    }

    /**
     * This method filters out the Invalid orders and prioritizes the valid ones by their distance to Appleton
     * to deliver as many orders as possible
     */
    private void setOrderPriorityQueue(){
        for (Order orders : ordersToDeliver) {
            orders.checkOrderValidation();
            OrderOutcome dliver = orders.getOrderOutcome();
            if (dliver.equals(OrderOutcome.ValidButNotDelivered)) {
                sortOrders.add(orders);
            }
        }
    }

    /**
     * This class is needed to compare the elements of the priority queue so that the most orders get delivered
     */
    public static class OrderPrioritisation implements Comparator<Order> {
        /**
         * This method overrides the compare function that takes two elements and compares them
         * @param o1 the first order to be compared.
         * @param o2 the second order to be compared.
         * @return Double value to indicate which order has higher priority
         */
        @Override
        public int compare(Order o1, Order o2) {
            try {
                return Double.compare(o1.getRestaurant().distanceBetween(APPLETON_TOWER),
                        o2.getRestaurant().distanceBetween(APPLETON_TOWER));
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public int getOrdersDelivered(){
        return ordersDelivered.size();
    }

    public int getMovesMade(){
        return movesMade.size();
    }
}
