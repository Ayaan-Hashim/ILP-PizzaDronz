package uk.ac.ed.inf;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.ArrayList;

/**
 * This class is used to ensure the singleton access pattern is
 * used to retrieve the Central Area coordinates
 */
public class SingletonAccess {
    /**
     * The instance that gets created once to ensure a singleton pattern.
     * Instantiating the object for lazy instantiation in order to save on memory and resources.
     */
    private static SingletonAccess singletonAccessObj;
    /**
     * Arraylist used to store the coordinates
     * of the central area's vertices as a list of LngLat objects
     */
    private ArrayList<LngLat> centralAreaCoOrds;

    /**
     * A private constructor so that only one object exists and other instances aren't made
     */
    private SingletonAccess(){
        readCentralArea();
    }

    /**
     * This method returns the only instance of the singleton class, if it exists.
     * In the case that no instances exist, a new instance is made.
     * @return the instance of the SingletonAccess class
     */
    public static SingletonAccess getInstance(){
        //If no instances of the class exist, then a new instance is made
        if (singletonAccessObj == null){
            singletonAccessObj = new SingletonAccess();
        }
        //If the instance exists, it gets returned
        return singletonAccessObj;
    }

    /**
     * This method is used to read from the REST Server and storing the coordinates
     * of the central area vertices in centralAreaCoOrds
     */
    public void readCentralArea(){
        centralAreaCoOrds = RetrieveServerData
                .getExtensionDataFromDefaultURL("centralArea", new TypeReference<ArrayList<LngLat>>(){});
    }

    /**
     * This method is the getter function of the centralAreaCoOrds, which consist of the
     * list containing the coordinates of the central area.
     * @return the list of coordinates of the central area.
     */
    public ArrayList<LngLat> getCentralAreaCoOrds(){
        singletonAccessObj.readCentralArea();
        return centralAreaCoOrds;
    }

}
