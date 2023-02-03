package uk.ac.ed.inf;
//import org.junit.Test;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInfo;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestRestaurant
{

    public TestRestaurant() throws MalformedURLException {
    }

    @BeforeEach
    void displayTestName(TestInfo testInfo)
    {
        System.out.println(testInfo.getDisplayName());
    }

    URL base = new URL("https://ilp-rest.azurewebsites.net/");
    Restaurant[] restaurantsArr = Restaurant.restaurants;
    List<Restaurant> restaurantsList = Arrays.stream(restaurantsArr).toList();
    Restaurant restaurantIdx3 = restaurantsList.get(3);

    @Test
    @DisplayName("Testing different scenarios for getMenu method")

    public void testGetMenu()
    {
        Menu[] restaurantIdx3Menu = restaurantIdx3.getMenu();
        assertEquals(restaurantIdx3.menu().size(),
                Arrays.stream(restaurantIdx3Menu).toList().size());

        assertEquals(restaurantIdx3.menu().get(1).name(),
                Arrays.stream(restaurantIdx3Menu).toList().get(1).name());

        assertEquals(restaurantIdx3.menu().get(1).priceInPence(),
                Arrays.stream(restaurantIdx3Menu).toList().get(1).priceInPence());

        assertEquals(restaurantIdx3.menu(), Arrays.stream(restaurantIdx3Menu).toList());
        System.out.println("All tests in getMenu works!");
    }

    @Test
    public void printCoOrds(){
        System.out.println(restaurantIdx3);
        System.out.println(restaurantsArr[2].getRestaurantLngLat());
        System.out.println(restaurantIdx3.getRestaurantLngLat());
    }

}
