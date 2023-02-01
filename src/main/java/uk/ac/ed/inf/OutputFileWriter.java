package uk.ac.ed.inf;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

/**
 * This class is in charge of writing all the output files, Json as wel as the GeoJson.
 */
public class OutputFileWriter {

    /**
     * This is a helper method for writing the deliveries JSON file.
     * @param order This is the order whose attributes are to be written
     * @return the Object with the attributes
     */
    private static JSONObject deliveriesHelper(Order order) {
        //Creating a JSONObject object
        JSONObject pathObject = new JSONObject();
        String orderOutcomeString = (String.valueOf(order.getOrderOutcome()));
        //Inserting key-value pairs into the json object
        pathObject.put("orderNo", order.orderNo());
        pathObject.put("outcome", order.getOrderOutcome().toString());
        pathObject.put("costInPence", order.priceTotalInPence());
        return pathObject;
    }

    /**
     * this method is for writing the deliveries JSON file.
     * @param ordersList the list of all orders of a day
     * @param date the dae for which all the orders' attributes are to be set
     * @throws IOException when the attributes couldnt be written properly
     */
    public static void deliveriesJsonWriter(List<Order> ordersList, String date) throws IOException {
        String path = System.getProperty("user.dir") + "/resultfiles";
        Files.createDirectories(Path.of(path));

        JSONArray deliveries = new JSONArray();

        for(Order order : ordersList){
            JSONObject pathObject = deliveriesHelper(order);
            deliveries.add(pathObject);
            try {
                FileWriter file = new FileWriter(path + "/deliveries-" + date + ".json");
                file.write(deliveries.toJSONString());
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * this method gets the attribute that are to be written
     * @param orderNo this holds the orderNo for a delivered order
     * @param node each of the path taken to deliver this order
     * @return
     */
    public static JSONObject flightPathWriter(String orderNo, Node node) {

        JSONObject flightpath = new JSONObject();
        flightpath.put("orderNo", orderNo);
        flightpath.put("fromLongitude", node.parent.location.lng());
        flightpath.put("fromLatitude", node.parent.location.lat());
        flightpath.put("angle", node.angle);
        flightpath.put("toLongitude", node.location.lng());
        flightpath.put("toLatitude", node.location.lat());
        flightpath.put("ticksSinceStartOfCalculation", node.tickComputation);
        return flightpath;
    }

    /**
     *  This is a helper method for writing the flightpath JSON file.
     * @param map this holds the orderNo for a delivered order along with all the paths taken to deliver this order
     * @return the Object with the attributes of the path
     */
    private static JSONArray getFlightpath(HashMap<String, List<Node>> map) {
        JSONArray flightpaths = new JSONArray();
        for (String order : map.keySet()) {
            for (Node i : map.get(order)) {
                JSONObject x = flightPathWriter(order, i);
                flightpaths.add(x);
            }
        }
        return flightpaths;
    }


    /**
     * This method writes the entire flightpath
     * @param date the date for whic hthe flightpath is to be written
     * @param orderNoMap this holds the orderNo for a delivered order along with all the
     *                   paths taken to deliver this order
     * @throws IOException when the file cant be written
     */
    public static void writeFlightpathFile(String date, HashMap<String, List<Node>> orderNoMap) throws IOException {
        String path = System.getProperty("user.dir") + "/resultfiles";
        Files.createDirectories(Path.of(path));
        FileWriter file = new FileWriter(path + "/flightpath-" + date + ".json");

        file.write(getFlightpath(orderNoMap).toJSONString());
        file.close();

    }

    /**
     * This method gets and writes all the coordinates the drone takes as a geojson file
     * @param allMoves all the moves the drone takes
     * @param date the date for which teh file has to be written
     * @throws IOException when the file is not able to be written
     */
    public static void outputGeoJsonWriter(List<Node> allMoves, String date) throws IOException {
        String path = System.getProperty("user.dir") + "/resultfiles";
        Files.createDirectories(Path.of(path));

        ArrayList<Point> points = new ArrayList<>();
        for(Node move : allMoves){
            points.add(Point.fromLngLat(move.location.lng(),move.location.lat()));
        }
        LineString pathTaken = LineString.fromLngLats(points);
        Feature feature = Feature.fromGeometry(pathTaken);
        FeatureCollection totalfeature = FeatureCollection.fromFeature(feature);

        FileWriter file = new FileWriter(path + "/drone-" + date + ".geojson");
        file.write(totalfeature.toJson());
        file.close();
    }

}
