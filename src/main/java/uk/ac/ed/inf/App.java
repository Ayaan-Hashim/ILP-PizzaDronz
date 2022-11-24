package uk.ac.ed.inf;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws MalformedURLException {
        System.out.println( "Hello World!" );
        Restaurant [] participants = Restaurant.getRestaurantsFromRestServer(new URL("https://ilp-rest.azurewebsites.net"));
        System.out.println(Arrays.toString(participants));
    }
}
