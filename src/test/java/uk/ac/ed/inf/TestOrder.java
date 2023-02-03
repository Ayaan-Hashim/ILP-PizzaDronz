package uk.ac.ed.inf;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import static org.junit.jupiter.api.Assertions.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class TestOrder
{

    public TestOrder() throws MalformedURLException {}

    @BeforeEach
    void displayTestName(TestInfo testInfo)
    {
        System.out.println(testInfo.getDisplayName());
    }

    URL serverBaseAddress = new URL("https://ilp-rest.azurewebsites.net/");

    @Test
    @DisplayName("Checking working of order validation with synthetic data")
    void testOrderValidation(){
        Restaurant.getRestaurantsFromRestServer(serverBaseAddress);
        // first we test if the order with invalid card number gets picked up
        Order order1 = new Order("TestFunctions","2023-02-01","Test Name1",
                "12341234","04/24","122",2400,
                new ArrayList<>(Arrays.asList("Super Cheese","All Shrooms")));
        order1.checkOrderValidation();
        assertEquals(OrderOutcome.InvalidCardNumber, order1.getOrderOutcome());

        // Now we check for if the order had invalid cvv
        Order order2 = new Order("TestFunctions","2023-02-01","Test Name2",
                "2720038402742337","05/22","819",
                2400, new ArrayList<>(Arrays.asList("Proper Pizza","Pineapple & Ham & Cheese")));
        order2.checkOrderValidation();
        assertEquals(OrderOutcome.InvalidExpiryDate,order2.getOrderOutcome());

        Order order3 = new Order("TestFunctions","2023-02-01","Test Name3",
                "2720038402742337","05/23","8177",
                2400, new ArrayList<>(Arrays.asList("Proper Pizza","Pineapple & Ham & Cheese")));
        order3.checkOrderValidation();
        assertEquals(OrderOutcome.InvalidCvv,order3.getOrderOutcome());

        Order order4 = new Order("TestFunctions","2023-02-01","Test Name4",
                "2720038402742337","05/23","817",
                0, new ArrayList<>(Arrays.asList("Proper Pizza","Pineapple & Ham & Cheese")));
        order4.checkOrderValidation();
        assertEquals(OrderOutcome.InvalidTotal,order4.getOrderOutcome());

        Order order5 = new Order("TestFunctions","2023-02-01","Test Name5",
                "2720038402742337","05/23","817",
                2400, new ArrayList<>(Arrays.asList("Test Pizza","Pineapple & Ham & Cheese")));
        order5.checkOrderValidation();
        assertEquals(OrderOutcome.InvalidPizzaNotDefined,order5.getOrderOutcome());

        Order order6 = new Order("TestFunctions","2023-02-01","Test Name6",
                "2720038402742337","05/23","817",
                7000, new ArrayList<>(Arrays.asList("Proper Pizza","Pineapple & Ham & Cheese",
                "Proper Pizza","Pineapple & Ham & Cheese","Proper Pizza","Pineapple & Ham & Cheese")));
        order6.checkOrderValidation();
        assertEquals(OrderOutcome.InvalidPizzaCount,order6.getOrderOutcome());

        Order order7 = new Order("TestFunctions","2023-02-01","Test Name7",
                "2720038402742337","05/23","817",
                2400, new ArrayList<>(Arrays.asList("Super Cheese","Pineapple & Ham & Cheese")));
        order7.checkOrderValidation();
        assertEquals(OrderOutcome.InvalidPizzaCombinationMultipleSuppliers,order7.getOrderOutcome());

        Order order9 = new Order("TestFunctions","2023-02-01","Test Name4",
                "2720038402742337","05/23","817",
                2400, new ArrayList<>(Arrays.asList("Proper Pizza","Pineapple & Ham & Cheese")));
        order9.checkOrderValidation();
        assertEquals(OrderOutcome.ValidButNotDelivered,order9.getOrderOutcome());

//      the general 'Invalid' case, being raised for an invalid date of the order.
        Order order10 = new Order("TestFunctions","2022-02-01","Test Name4",
                "2720038402742337","05/23","817",
                2400, new ArrayList<>(Arrays.asList("Proper Pizza","Pineapple & Ham & Cheese")));
        order10.checkOrderValidation();
        assertEquals(OrderOutcome.Invalid,order10.getOrderOutcome());

    }

}
