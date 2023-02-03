package uk.ac.ed.inf;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestServerRetrieval
{

    public TestServerRetrieval() throws MalformedURLException {

    }

    @BeforeEach
    void displayTestName(TestInfo testInfo)
    {
        System.out.println(testInfo.getDisplayName());
    }

    // the base URL address (no extensions) for the REST server
    URL baseUrl = new URL("https://ilp-rest.azurewebsites.net");

    @Test
    @DisplayName("Testing if all orders are retrieved correctly")
    void testTotalOrdersRetrieval()
    {
        List<Order> allOrders = RetrieveServerData.getExtensionDataFromURL("/orders",baseUrl,new TypeReference<>(){});
        assertEquals(7050, allOrders.size());
    }

    @Test
    @DisplayName("Testing if all orders for a given date are retrieved correctly")
    void testOrdersForGivenDateRetrieval()
    {
        String date = "2023-04-15";
        String extension = "/orders" + "/" + date;
        List<Order> allOrdersForGivenDate = RetrieveServerData.getExtensionDataFromURL(extension,baseUrl, new TypeReference<>(){});
        assertEquals(47, allOrdersForGivenDate.size());
    }

    @Test
    @DisplayName("Testing if the central campus area is retrieved correctly")
    void testCentralAreaRetrieval()
    {
        List<LngLat> centralAreaVertices = RetrieveServerData.getExtensionDataFromURL("/centralArea",baseUrl, new TypeReference<>(){});
        assertEquals(4, centralAreaVertices.size());
    }

    @Test
    @DisplayName("Testing if all restaurants are retrieved correctly")
    void testRestaurantRetrieval()
    {
        List<Restaurant> allRestaurants = RetrieveServerData.getExtensionDataFromURL("/restaurants",baseUrl, new TypeReference<>(){});
        assertEquals(4, allRestaurants.size());
    }

    @Test
    @DisplayName("Testing if all no-fly-zones are retrieved correctly")
    void testNoFlyZoneRetrieval()
    {
        List<NoFlyZones> allNoFlyZones = RetrieveServerData.getExtensionDataFromURL("/noFlyZones", baseUrl, new TypeReference<>(){});
        assertEquals(4, allNoFlyZones.size());
    }

}