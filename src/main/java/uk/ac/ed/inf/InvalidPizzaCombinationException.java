package uk.ac.ed.inf;

/**
 * A class used to throw a custom exception when
 * ordered pizza combination cannot be delivered by the same restaurant.
 */
public class InvalidPizzaCombinationException extends Exception {
    public InvalidPizzaCombinationException(String errorMessage) {
        super(errorMessage);
    }
}