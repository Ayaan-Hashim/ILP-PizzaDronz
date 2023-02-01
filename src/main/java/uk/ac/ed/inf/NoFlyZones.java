package uk.ac.ed.inf;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * This record class indicates to the program all the no-fly-zones.
 * @param coordinates this is a nested list of double values that indicate the coordinates for multiple no fly zones.
 */
@JsonIgnoreProperties("name")
public record NoFlyZones(
        @JsonProperty("coordinates")
        ArrayList<ArrayList<Double>> coordinates){
    /**
     * A nested static public arraylist of LngLat coordinates that define all the no fly zones
     */
    public static ArrayList<ArrayList<LngLat>> allCoOrdinates = new ArrayList<>();

    /**
     * This method sets the arraylist and stoores it so that all other classes can access it.
     */
    public static void setNoFlyZones(){
        ArrayList<NoFlyZones> noFlyZonesCoOrds = SingletonAccess.getInstance().getNoFlyZoneCoOrds();
        for (NoFlyZones i : noFlyZonesCoOrds){
            ArrayList<LngLat> noFlyZoneCoOrdinates = new ArrayList<>();
            for (ArrayList<Double> point : i.coordinates) {
                noFlyZoneCoOrdinates.add(new LngLat(point.get(0), point.get(1)));
            }
            allCoOrdinates.add(noFlyZoneCoOrdinates);
        }
    }
}
