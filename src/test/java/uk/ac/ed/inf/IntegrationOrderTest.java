package uk.ac.ed.inf;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IntegrationOrderTest {

    public IntegrationOrderTest() throws MalformedURLException {
    }

    @BeforeEach
    void displayTestName(TestInfo testInfo)
    {
        System.out.println(testInfo.getDisplayName());
    }

    // the base URL address (no extensions) for the REST server
    URL baseUrl = new URL("https://ilp-rest.azurewebsites.net/");

    public List<OrderOutcome> validatingOrdersFromList(List<Order> ordersList){
        Restaurant.getRestaurantsFromRestServer(baseUrl);
        // The synthetic data on the server makes it so that for each day, there can only be 40 orders that are valid

        ArrayList<OrderOutcome> orderOutcomes = new ArrayList<>();
        for (Order orderI : ordersList){
            orderI.checkOrderValidation();
            OrderOutcome k = orderI.getOrderOutcome();
//            if(i.getOrderOutcome() == OrderOutcome.ValidButNotDelivered){
//                numberOfValid++;
//            }
            orderOutcomes.add(k);
        }

        //        for (OrderOutcome l : outcomeHashSet){
//            System.out.println(l + ": "+ Collections.frequency(orderOutcomes,l));
//        }

        return orderOutcomes;
//        return new HashSet<OrderOutcome>(orderOutcomes);
//        assertEquals(numberOfValid,40);
    }

    @Test
    @DisplayName("Checking if all the Outcomes have the expected distribution for a given day")
    public void testOrderIntegrationOnDay(){
        ArrayList<Order> orderDay1List = RetrieveServerData.getExtensionDataFromDefaultURL
                ( "orders/2023-01-01", new TypeReference<>(){});
        ArrayList<Order> orderDay2List = RetrieveServerData.getExtensionDataFromDefaultURL
                ( "orders/2023-03-01", new TypeReference<>(){});
        ArrayList<Order> orderDay3List = RetrieveServerData.getExtensionDataFromDefaultURL
                ( "orders/2023-05-25", new TypeReference<>(){});

        List<OrderOutcome> orderDay1OutcomeList = validatingOrdersFromList(orderDay1List);
        List<OrderOutcome> orderDay2OutcomeList = validatingOrdersFromList(orderDay2List);
        List<OrderOutcome> orderDay3OutcomeList = validatingOrdersFromList(orderDay3List);

        var day1Outcomes = makeOutcomeMap(orderDay1OutcomeList);
        var day2Outcomes = makeOutcomeMap(orderDay2OutcomeList);
        var day3Outcomes = makeOutcomeMap(orderDay3OutcomeList);

        assertTrue(checkDistribution(day1Outcomes,false));
        assertTrue(checkDistribution(day2Outcomes,false));
        assertTrue(checkDistribution(day3Outcomes,false));

        System.out.println("Order integration for each day works as intended");
    }

    @Test
    @DisplayName("Checking if all the Outcomes have the expected distribution for entire REST server")
    public void testOrderIntegrationServer(){
        ArrayList<Order> orderSeverList = RetrieveServerData.getExtensionDataFromDefaultURL
                ( "orders/", new TypeReference<>(){});
        List<OrderOutcome> orderOutcomeList = validatingOrdersFromList(orderSeverList);
        var serverOutcomes = makeOutcomeMap(orderOutcomeList);
        assertTrue(checkDistribution(serverOutcomes,true));
        System.out.println("Order integration for entire server works as intended");
    }

    public HashMap<OrderOutcome, Integer> makeOutcomeMap(List<OrderOutcome> outcomeSet){

        HashMap<OrderOutcome, Integer> orderOutcomeCountMap = new HashMap<>();

        for (OrderOutcome outcome : outcomeSet) {
            orderOutcomeCountMap.put(outcome,Collections.frequency(outcomeSet,outcome));
        }

        return orderOutcomeCountMap;
    }

    public Boolean checkDistribution(HashMap<OrderOutcome,Integer> outcomeMap, Boolean serverCheck){
        List<OrderOutcome> invalidOutcomes = new ArrayList<>();
        invalidOutcomes.add(OrderOutcome.InvalidCardNumber);
        invalidOutcomes.add(OrderOutcome.InvalidExpiryDate);
        invalidOutcomes.add(OrderOutcome.InvalidCvv);
        invalidOutcomes.add(OrderOutcome.InvalidTotal);
        invalidOutcomes.add(OrderOutcome.InvalidPizzaNotDefined);
        invalidOutcomes.add(OrderOutcome.InvalidPizzaCount);
        invalidOutcomes.add(OrderOutcome.InvalidPizzaCombinationMultipleSuppliers);


        for (OrderOutcome invalidOutcome: invalidOutcomes){
            if(!serverCheck) {
                if (outcomeMap.get(invalidOutcome) != 1) {
                    return false;
                }
            }
            else{
                if (outcomeMap.get(invalidOutcome) != 150) {
                    return false;
                }
            }
        }
        if(!serverCheck) {
            return outcomeMap.get(OrderOutcome.ValidButNotDelivered) == 40;
        }
        else{
            return outcomeMap.get(OrderOutcome.ValidButNotDelivered) == 6000;
        }
    }
}
