package uk.ac.ed.inf;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * This class is the driver class of the entire system, calling all the relevant parts of the code to make it so that
 * the drone delivers the as many orders as possible for the day provided.
 */
public class  Controller
{
    /**
     * This static URL stores a valid link to the server's base address.
     */
    protected static URL serverBaseAddress;

    /**
     * This main method of the Controller is the driver of the entire system,
     * it works by calling all the needed methods in other classes.
     * @param args it gets the date at which the drone delivers the order for (as a String),
     *             the URL to the server's base address (As a String),
     *             and a random seed (as a String).
     * @throws IOException when it is not able to write a file JSON or GeoJSON for a certain day.
     */
    public static void main(String[] args) throws IOException {
        //Stores the first terminal input as the date
        String droneOperationDate = args[0];
        //calls a helper function to check if the terminal inputs are valid and if possible, fix the input.
        validateTerminalInputs(args);
        //Sets the default URL for the server's base address for all calls that don't require a special address
//        RetrieveServerData.setDefaultUrl(serverBaseAddress);
        //Read the Central Area once to abide by Singleton Design pattern
        SingletonAccess.getInstance().readCentralArea();
        //Read the restaurants that are available to get orders from.
        Restaurant.getRestaurantsFromRestServer(serverBaseAddress);
        //Set the No-Fly-Zone coordinates for the drone to not cross, this also abides by Singleton Pattern
        NoFlyZones.setNoFlyZones();
        Instant startTimeOfBeforePathGeneration = Instant.now();
        Delivery droneDeliveries = new Delivery(droneOperationDate);
        Instant timeAfterPathGeneration = Instant.now();
        System.out.println("Time taken to generate path for all Orders on " + droneOperationDate + " : " +
                Duration.between(startTimeOfBeforePathGeneration,timeAfterPathGeneration).getSeconds()+" seconds");
    }

    /**
     * this helper method, validates all the inputs passed to the Controller.
     * @param args it gets the date at which the drone delivers the order for (as a String),
     *             the URL to the server's base address (As a String),
     *             and a random seed (as a String).
     */
    private static void validateTerminalInputs(String[] args){
        inputsNumberValidator(args);
        dateValidation(args[0]);
        urlValidation(args[1]);
    }

    /**
     * This method checks if the date passed to the program is valid or not,
     * it also checks if the format the date was passed in was correct,
     * and finally it checks if the date passed is one on which the PizzaDronz service is not operational
     * @param dateToCheck the date whose validity is checked (as a string)
     */
    private static void dateValidation(String dateToCheck){
        try{
            //Sets the format which the date has to follow
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            /*Checks if the date passed is the same format as the one specified, otherwise
            throws a runtime error the program ends*/
            LocalDate date = LocalDate.parse(dateToCheck, format);
            //The last date the drone service is operational
            LocalDate endDate = LocalDate.parse("2023-05-31",format);
            //The first day the drone service is operational
            LocalDate startDate = LocalDate.parse("2023-01-01",format);
            //Checks if the day passed is before the service is launched or after the service has ended
            if (date.isAfter(endDate) || date.isBefore(startDate)){
                //Prints an error message stating the error
                System.err.println("The drone is not operational on the provided day.");
                //Exits the program
                System.exit(1);
            }
        }
        catch (RuntimeException e){
            //Prints an error message stating the error
            System.err.print("The Date was in an incorrect format or Date was not possible (Date not Valid) ");
            //Exits the program
            System.exit(1);
        }
    }

    /**
     * This method checks if the URL passed to the program is valid or not,
     * it also checks if the URL ends with a '/', if it does not then a '/' is added at the end of the URL.
     * @param urlToCheck The URL that has to be checked for validity.
     */
    private static void urlValidation(String urlToCheck) {
        //Trys to set the URL variable of serverBaseAddress to the String URL passed
        try{
            //Checks if the URL passed ends with a '/'
            if (!(urlToCheck.endsWith("/"))) {
                //Adds the '/' if the URL does not end with '/'
                urlToCheck = urlToCheck.concat("/");
            }
            serverBaseAddress = new URL(urlToCheck);
        }
        catch (MalformedURLException e){
            //Prints an error message stating the error
            System.err.println("The URL passed is invalid!");
            //Exits the program
            System.exit(1);
        }
    }

    /**
     * This method checks if the number of arguments passed to the program are incorrect.
     * In which case the programs is forced to stop.
     * @param args The arguments passed to the program.
     */
    private static void inputsNumberValidator(String[] args){
        if(args.length!=3){
            //Prints an error message stating the error
            System.err.println("Not all required inputs were passed");
            //Exits the program
            System.exit(1);
        }
    }

}
