package uk.ac.ed.inf;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to store details of each restaurant based on
 * the information fetched from the REST Server.
 * @param name the name of the Restaurant
 * @param lng the longitude coordinate of the restaurant
 * @param lat the latitude coordinate of the restaurant
 * @param menu the items on the restaurant's menu
 */
public record Restaurant(
        @JsonProperty("name")
        String name,
        @JsonProperty("longitude")
        double lng,
        @JsonProperty("latitude")
        double lat,
        @JsonProperty("menu")
        List<Menu> menu){

    public static Restaurant[] restaurants;

    /**
     * This method is used as a getter function for all the
     * items that the restaurant has on its menu.
     * @return An array of menu objects consisting of all the items
     * the restaurant has on its menu
     */
    public Menu[] getMenu(){
        return menu.toArray(new Menu[0]);
    }

    /**
     * returns an array of all participating restaurants obtained from the given
     * server base address, along with their menus
     * @param serverBaseAddress the URL address of Server that is to be accessed
     */
    public static void getRestaurantsFromRestServer(URL serverBaseAddress){
        ArrayList<Restaurant> restaurantArrayList = RetrieveServerData
                .getExtensionDataFromURL("restaurants",serverBaseAddress,
                        new TypeReference<ArrayList<Restaurant>>(){});

        restaurants = restaurantArrayList.toArray(new Restaurant[0]);
    }

    public LngLat getRestaurantLngLat(){
        return new LngLat(lng,lat);
    }

    public double distanceBetween(LngLat otherLocation){
        LngLat restaurantLocation = getRestaurantLngLat();
        return restaurantLocation.distanceTo(otherLocation);
    }

}
