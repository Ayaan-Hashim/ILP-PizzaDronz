package uk.ac.ed.inf;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is used to store orders made, once retrieved from the REST Server
 * @param orderNo the number used to identify a unique order
 * @param orderDate the date the order was placed
 * @param customer name of the person that placed the order
 * @param creditCardNumber the credit card number used by the
 *                         customer to place the order
 * @param creditCardExpiry the expiry date of the credit card
 *                         used for the order
 * @param cvv the cvv number on the credit card used for the order
 * @param priceTotalInPence the total price of paid by the
 *                          customer for the order
 * @param orderItems list of the items in the order
 */
public record Order(
        @JsonProperty("orderNo")
        String orderNo,
        @JsonProperty("orderDate")
        String orderDate,
        @JsonProperty("customer")
        String customer,
        @JsonProperty("creditCardNumber")
        String creditCardNumber,
        @JsonProperty("creditCardExpiry")
        String creditCardExpiry,
        @JsonProperty("cvv")
        String cvv,
        @JsonProperty("priceTotalInPence")
        int priceTotalInPence,
        @JsonProperty("orderItems")
        List<String> orderItems){


    /**
     * This method calculates the total cost in pence that would take to deliver
     * all the items ordered. This cost would also include the flat £1 delivery cost,
     * which would be in the form of 100 pence.
     * @param participatingRestaurants array of the restaurants that the order
     *                                 could be placed from, as retrieved from
     *                                 the REST Server
     * @param pizzaOrders varargs parameter passed representing the pizzas
     *                    that were ordered, whose cost is to be calculated
     * @return the total cost to deliver every item in the order in pence
     * @throws InvalidPizzaCombinationException in case ordered pizza combination
     * cannot be delivered by the same restaurant.
     */
    public int getDeliveryCost(Restaurant[] participatingRestaurants, String... pizzaOrders)
            throws InvalidPizzaCombinationException {
        if (pizzaOrders.length == 0) {
            return 0;
        }

        // iterating through every restaurant that was passed
        for (Restaurant restaurantI : participatingRestaurants) {
            // gets the menu for the restaurant in the current iteration
            Menu[] restaurantIMenu = restaurantI.getMenu();
        /*
         pizzaOrder gets the stream of the order of pizzas passed to it
         The varargs concept was obtained by the
         https://stackoverflow.com/questions/2330942/java-variable-number-of-arguments-for-a-method
         */
            var pizzaOrder = Stream.of(pizzaOrders);

            /* Creates a Map of the menu item's name and price.
             * Where the name is the key of the map and the price is the value
             */
            Map<String, Integer> menuNamePriceMap = Stream.of(restaurantIMenu)
                    .collect(Collectors.toMap(Menu::name, Menu::priceInPence));

            //If the all pizzas we're looking for is in the menu of the current restaurant
            if (menuNamePriceMap.keySet().containsAll(pizzaOrder.toList())) {

                //list to store the price of each pizza ordered
                List<Integer> priceList = new ArrayList<>();
                for (String pizza : pizzaOrders) {
                    //gets the price of the pizza we're looking for and stores it in the list
                    priceList.add(menuNamePriceMap.get(pizza));
                }
                /*
                Adds the flat delivery charge onto the price of the ordered pizzas
                This is achieved by using reduce on the stream of the list we just generated
                 and adding it to the base value of 100
                 */
                return priceList.stream().reduce(100, Integer::sum);
            }
        }
        /*
        An InvalidPizzaCombinationException is thrown when the pizzas ordered
         cant be delivered because it is an invalid combination.
         */
        throw new InvalidPizzaCombinationException("An Invalid Combination was passed!");

    }

}
