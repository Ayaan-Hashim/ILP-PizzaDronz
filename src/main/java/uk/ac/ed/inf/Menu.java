package uk.ac.ed.inf;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class is used to store items on a certain restaurant's menu
 * from the REST server
 * @param name the name of the item
 * @param priceInPence the price of that item in pence
 */
public record Menu(
        @JsonProperty("name")
        String name,
        @JsonProperty("priceInPence")
        int priceInPence) {

}
