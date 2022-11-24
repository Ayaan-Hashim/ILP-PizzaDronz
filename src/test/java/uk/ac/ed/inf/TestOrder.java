package uk.ac.ed.inf;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInfo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestOrder
{

    public TestOrder() throws MalformedURLException {}

    @BeforeEach
    void displayTestName(TestInfo testInfo)
    {
        System.out.println(testInfo.getDisplayName());
    }

    ArrayList<Order> ordersList = RetrieveServerData.getExtensionDataFromDefaultURL
            ( "orders", new TypeReference<>(){});
    URL serverBaseAddress = new URL("https://ilp-rest.azurewebsites.net/");
    Restaurant[] restaurantsArr = Restaurant.getRestaurantsFromRestServer(serverBaseAddress);
    Order orderIdx0 = ordersList.get(0);
    Order orderIdx2 = ordersList.get(2);

    Order orderIdx8 = ordersList.get(8);
    Menu fakeItem = new Menu("Special testing pizza", 646360);
    List<Menu> menuTests = Arrays.stream(new Menu[]{fakeItem}).toList();
    Restaurant fakeRestaurant = new Restaurant("ILP Testing", -3.1870,55.9444, menuTests);

    @Test
    @DisplayName("Testing different scenarios for getDeliveryCost method")
    public void testGetDeliveryCost() throws InvalidPizzaCombinationException
    {
        var orderIdx0Items = orderIdx0.orderItems().toArray(new String[0]);
        assertEquals(orderIdx0.priceTotalInPence(), orderIdx0.getDeliveryCost(restaurantsArr, orderIdx0Items));
        var orderIdx2Items = orderIdx2.orderItems().toArray(new String[0]);
        assertEquals(orderIdx2.priceTotalInPence(), orderIdx2.getDeliveryCost(restaurantsArr, orderIdx2Items));
        var orderIdx8Items = orderIdx8.orderItems().toArray(new String[0]);
        assertEquals(orderIdx8.priceTotalInPence(), orderIdx8.getDeliveryCost(restaurantsArr, orderIdx8Items));
        //testing for when no pizzas are ordered
        assertEquals(0, orderIdx8.getDeliveryCost(restaurantsArr));

        // testing for when it should not be able to deliver the pizza/s
        InvalidPizzaCombinationException exception1 = assertThrows(InvalidPizzaCombinationException.class,
                () -> orderIdx0.getDeliveryCost(restaurantsArr, "Super Cheese","Meat Lover"),
                "Didn't throw the exception");
        assertTrue(exception1.getMessage().contains("An Invalid Combination was passed!"));
        InvalidPizzaCombinationException exception2= assertThrows(InvalidPizzaCombinationException.class,
                () -> orderIdx0.getDeliveryCost(new Restaurant[]{fakeRestaurant}, orderIdx2Items),
                "Didn't throw the exception");
        assertTrue(exception2.getMessage().contains("An Invalid Combination was passed!"));
        System.out.println("GetDeliveryCost method works as expected!");
    }

}
