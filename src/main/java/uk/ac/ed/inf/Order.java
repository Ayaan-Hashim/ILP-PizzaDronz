package uk.ac.ed.inf;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.time.YearMonth;

/**
 * This class is used to store orders made, once retrieved from the REST Server
 */
public final class Order {

    private OrderOutcome outcome;

    private static HashMap<Restaurant, List<String>> restaurantMenuMap;
    @JsonProperty("orderNo")
    private final String orderNo;
    @JsonProperty("orderDate")
    private final String orderDate;
    @JsonProperty("customer")
    private final String customer;
    @JsonProperty("creditCardNumber")
    private final String creditCardNumber;
    @JsonProperty("creditCardExpiry")
    private final String creditCardExpiry;
    @JsonProperty("cvv")
    private final String cvv;
    @JsonProperty("priceTotalInPence")
    private final int priceTotalInPence;
    @JsonProperty("orderItems")
    private final List<String> orderItems;

    /**
     * @param orderNo           the number used to identify a unique order
     * @param orderDate         the date the order was placed
     * @param customer          name of the person that placed the order
     * @param creditCardNumber  the credit card number used by the
     *                          customer to place the order
     * @param creditCardExpiry  the expiry date of the credit card
     *                          used for the order
     * @param cvv               the cvv number on the credit card used for the order
     * @param priceTotalInPence the total price of paid by the
     *                          customer for the order
     * @param orderItems        list of the items in the order
     */
    public Order(
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
            List<String> orderItems) {
        this.orderNo = orderNo;
        this.orderDate = orderDate;
        this.customer = customer;
        this.creditCardNumber = creditCardNumber;
        this.creditCardExpiry = creditCardExpiry;
        this.cvv = cvv;
        this.priceTotalInPence = priceTotalInPence;
        this.orderItems = orderItems;
    }

    /**
     * This method calculates the total cost in pence that would take to deliver
     * all the items ordered. This cost would also include the flat £1 delivery cost,
     * which would be in the form of 100 pence.
     *
     * @param participatingRestaurants array of the restaurants that the order
     *                                 could be placed from, as retrieved from
     *                                 the REST Server
     * @param pizzaOrders              varargs parameter passed representing the pizzas
     *                                 that were ordered, whose cost is to be calculated
     * @return the total cost to deliver every item in the order in pence
     */
    private int getDeliveryCost(Restaurant[] participatingRestaurants, String... pizzaOrders)
    {
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
            Stream<String> pizzaOrder = Stream.of(pizzaOrders);

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
        return 0;
    }

    private Map<String, Integer> getAllItems(Restaurant[] participating_restaurants) {
        Map<String, Integer> map = new HashMap<>();
        Arrays.stream(participating_restaurants).forEach(x -> {
            Arrays.stream(x.getMenu()).forEach(y -> map.put(y.name(), y.priceInPence()));
        });
//        System.out.println(map);
        return map;
    }

    private boolean checkPizzaAvailability(Restaurant[] restaurants) {
        return orderItems.stream().allMatch(getAllItems(restaurants)::containsKey);
    }

    private boolean orderQuantityValidation() {
        //No pizzas were passed
        if (orderItems.size() == 0) {
            return false;
        }
        return (orderItems.size() < 4);
    }

    private HashMap<Restaurant, List<String>> restaurantMenuItems(Restaurant[] restaurants) {
        HashMap<Restaurant, List<String>> restaurantItem = new HashMap<>();
        for (Restaurant restaurantI : restaurants) {
            // gets the menu for the restaurant in the current iteration
            Menu[] restaurantIMenu = restaurantI.getMenu();
            List<String> items = new ArrayList<>();
            for (Menu m : restaurantIMenu) {
                items.add(m.name());
            }
            restaurantItem.put(restaurantI, items);
        }
//        System.out.println(restaurantItem);
        return restaurantItem;
    }

    private boolean orderInvalidTotal(Restaurant[] restaurants) {
        var pizzas = orderItems.toArray(new String[0]);
        int calculated = getDeliveryCost(restaurants, pizzas);
        return calculated == priceTotalInPence;
    }

    private boolean orderCombinationValidation(Restaurant[] restaurants) {
        restaurantMenuMap = restaurantMenuItems(restaurants);
        Restaurant key_found = null;
        for (String pizza : orderItems) {
            Restaurant key = anyKeyForValue(restaurantMenuMap, pizza);
            if (key_found == null) {
                key_found = key;
            } else {
                if (!(key_found.equals(key))) {
                    return false;
                }
            }
        }
        return true;
    }

    private Restaurant anyKeyForValue(Map<Restaurant, List<String>> map, String value) {
        for (Map.Entry<Restaurant, List<String>> entry : map.entrySet()) {
            if (entry.getValue().contains(value)) return entry.getKey();
        }
        return null;
    }

    //    this.orderDate
    private boolean checkDateValidity() {
        String date = this.orderDate;
        if (date.length() != 10) {
//            System.out.println("length fail");
            return false;
        }
//        Since the date formatter doesn't make it so that a string like 2020-4-1 get flagged as an invalid date.
        String[] parts = date.split("-");
        if (parts[0].length() != 4) {
//            System.out.println("year format error");
            return false;
        }
        if (parts[1].length() != 2) {
//            System.out.println("month format error");
            return false;
        }
        if (parts[2].length() != 2) {
//            System.out.println("day format error. Length:" + parts[2].length());
            return false;
        }
        if (parts.length > 3) {
//            System.out.println("general format error");
            return false;
        }

        //Not sure about this -`v
        if (Integer.parseInt(parts[0]) != 2023) {
//            System.out.println("incorrect year");
            return false;
        }
//        System.out.println("date validity: "+isValid(date)+" date: "+orderDate+" or "+ date);
        return isValid(date);
    }


    private boolean checkCardNumber() {
        //Implementation of Luhn's Algorithm:
        int sum = 0;
        int creditLength = creditCardNumber.length();
        if (creditLength != 16) {
            return false;
        }
        if (!(creditCardNumber.matches("^4[0-9]{0,}$"))
                && !(creditCardNumber.matches("^(5[1-5]|222[1-9]|22[3-9]|2[3-6]|27[01]|2720)[0-9]{0,}$"))) {
            return false;
        }
        for (int i = creditLength - 2; i >= 0; i -= 2) {
            int pp = Character.getNumericValue(creditCardNumber.charAt(i));
            int pp_j = Character.getNumericValue(creditCardNumber.charAt(i + 1));
            int pd = doubleAndSumDigits(pp);
            sum += pd + pp_j;
        }
        return sum % 10 == 0;
    }

    private static int doubleAndSumDigits(int digit) {
        int doubledDigit = digit * 2;
        return (doubledDigit > 9) ? (doubledDigit - 9) : doubledDigit;
    }

    private boolean checkValidCVV() {
        //Assuming that only 3 digit CVVs are valid:
        return (cvv.matches("[0-9]+") && cvv.length() == 3);
    }

    private boolean checkCardExpiration() {
        String pp[] = creditCardExpiry.split("/");
        if (pp.length != 2) {
            return false;
        }
        int expiryMonth = Integer.parseInt(pp[0]);
        int expiryYear = Integer.parseInt(pp[1]);
        String[] orderDateParts = orderDate.split("-");
        int orderDay = Integer.parseInt(orderDateParts[2]);
        int orderMonth = Integer.parseInt(orderDateParts[1]);

        int orderYear = Integer.parseInt(orderDateParts[0].substring(2));

        if (expiryMonth < 0 || expiryMonth > 12) {
            return false;
        }
        if (expiryYear < orderYear) {
            return false;
        }
        if (expiryYear == orderYear) {
            if (orderMonth > expiryMonth) {
                return false;
            }
            if (orderMonth == expiryMonth) {
                int p = getNumberOfDaysInMonth(orderYear, orderMonth);
                return orderDay < p;
            }
        }

        return true;
    }

    // Method to get number of days in month
    private static int getNumberOfDaysInMonth(int year, int month) {
        YearMonth yearMonthObject = YearMonth.of(year, month);
        return yearMonthObject.lengthOfMonth();
    }

    private boolean isValid(String dateStr) {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }


    private OrderOutcome orderItemValidity(Restaurant[] restaurants) {
        if (!checkPizzaAvailability(restaurants)) {
            return OrderOutcome.InvalidPizzaNotDefined;
        }
        if (!orderCombinationValidation(restaurants)) {
            return OrderOutcome.InvalidPizzaCombinationMultipleSuppliers;
        }
        if (!orderInvalidTotal(restaurants)) {
            return OrderOutcome.InvalidTotal;
        }
        if (!orderQuantityValidation()) {
            return OrderOutcome.InvalidPizzaCount;
        }
        if (!checkDateValidity()) {
            return OrderOutcome.Invalid;
        }
        if (!checkValidCVV()) {
            return OrderOutcome.InvalidCvv;
        }
        if (!checkCardNumber()) {
            return OrderOutcome.InvalidCardNumber;
        }
        if (!checkCardExpiration()) {
            return OrderOutcome.InvalidExpiryDate;
        }
        return OrderOutcome.ValidButNotDelivered;
    }

    /**
     * This method sets the OrderOutcome the appropriate enum
     */
    public void checkOrderValidation(){
        Restaurant[] restaurants = Restaurant.restaurants;
        outcome = orderItemValidity(restaurants);
    }

    /**
     * A getter method that returns the orderoutcome
     * @return This returns the order outcome for the current order
     */
    public OrderOutcome getOrderOutcome(){
        return outcome;
    }

    /**
     * This method returns the restaurant that provides the current order
     * @return restaurant that provides the current order
     * @throws MalformedURLException if the URL is not formed properly
     */
    public Restaurant getRestaurant() throws MalformedURLException {
        OrderOutcome orderOutcome = getOrderOutcome();
        if (orderOutcome == OrderOutcome.ValidButNotDelivered) {
            return anyKeyForValue(restaurantMenuMap, orderItems.get(0));
        }
        return null;
    }

    @JsonProperty("orderNo")
    public String orderNo() {
        return orderNo;
    }

    @JsonProperty("orderDate")
    public String orderDate() {
        return orderDate;
    }

    @JsonProperty("customer")
    public String customer() {
        return customer;
    }

    @JsonProperty("creditCardNumber")
    public String creditCardNumber() {
        return creditCardNumber;
    }

    @JsonProperty("creditCardExpiry")
    public String creditCardExpiry() {
        return creditCardExpiry;
    }

    @JsonProperty("cvv")
    public String cvv() {
        return cvv;
    }

    @JsonProperty("priceTotalInPence")
    public int priceTotalInPence() {
        return priceTotalInPence;
    }

    @JsonProperty("orderItems")
    public List<String> orderItems() {
        return orderItems;
    }

    public void setOutcomeDelivered(OrderOutcome orderOutcome){
       this.outcome = orderOutcome;
    }

}
