package uk.ac.ed.inf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class SystemTest
{
    public SystemTest() throws MalformedURLException
    {

    }

    @BeforeEach
    void displayTestName(TestInfo testInfo)
    {
        System.out.println(testInfo.getDisplayName());
    }

    // base URL for REST server
    URL serverBaseAddress = new URL("https://ilp-rest.azurewebsites.net/");

    @Test
    @DisplayName("Testing if the whole PizzaDronz app works as expected on day (1)")
    void testWholeSystemDay1() throws IOException {
        RetrieveServerData.setDefaultUrl(serverBaseAddress);
        SingletonAccess.getInstance().readCentralArea();
        Restaurant.getRestaurantsFromRestServer(serverBaseAddress);

        NoFlyZones.setNoFlyZones();
        String day ="2023-01-11";

        Instant startTimeOfBeforePathGeneration = Instant.now();
        Delivery droneMovement = new Delivery(day);
        Instant timeAfterPathGeneration = Instant.now();

        int movesMade = droneMovement.getMovesMade();
        assertTrue(movesMade < 2000);
        System.out.println("Moves made in the day are under 2000");

        int numberOrderDelivered = droneMovement.getOrdersDelivered();
        assertEquals(numberOrderDelivered,30);
        System.out.println("Drone maximizes the number of order it delivers");

        var timetaken =
                Duration.between(startTimeOfBeforePathGeneration,timeAfterPathGeneration).getSeconds();
        assertTrue(timetaken < 60);
        System.out.println("Time limit does not exceed");

        // check appropriate files are created
        String path = System.getProperty("user.dir") + "/resultfiles";
        String file1 = "deliveries-2023-01-11.json";
        String file2 = "drone-2023-01-11.geojson";
        String file3 = "flightpath-2023-01-11.json";
        String check1 = path + "/" + file1;
        String check2 = path + "/" + file2;
        String check3 = path + "/" + file3;
        assertTrue(new File(check1).exists());
        assertTrue(new File(check2).exists());
        assertTrue(new File(check3).exists());
        System.out.println("The files are written properly");
    }

    @Test
    @DisplayName("Testing if the whole PizzaDronz app works as expected on day (2)")
    void testWholeSystemDay2() throws IOException {
        RetrieveServerData.setDefaultUrl(serverBaseAddress);
        SingletonAccess.getInstance().readCentralArea();
        Restaurant.getRestaurantsFromRestServer(serverBaseAddress);

        NoFlyZones.setNoFlyZones();
        String day ="2023-02-03";

        Instant startTimeOfBeforePathGeneration = Instant.now();
        Delivery droneMovement = new Delivery(day);
        Instant timeAfterPathGeneration = Instant.now();

        int movesMade = droneMovement.getMovesMade();
        assertTrue(movesMade < 2000);
        System.out.println("Moves made in the day are under 2000");

        int numberOrderDelivered = droneMovement.getOrdersDelivered();
        assertEquals(numberOrderDelivered,30);
        System.out.println("Drone maximizes the number of order it delivers");

        var timetaken =
                Duration.between(startTimeOfBeforePathGeneration,timeAfterPathGeneration).getSeconds();
        assertTrue(timetaken < 60);
        System.out.println("Time limit does not exceed");

        // check appropriate files are created
        String path = System.getProperty("user.dir") + "/resultfiles";
        String file1 = "deliveries-2023-02-03.json";
        String file2 = "drone-2023-02-03.geojson";
        String file3 = "flightpath-2023-02-03.json";
        String check1 = path + "/" + file1;
        String check2 = path + "/" + file2;
        String check3 = path + "/" + file3;
        assertTrue(new File(check1).exists());
        assertTrue(new File(check2).exists());
        assertTrue(new File(check3).exists());
    }

    @Test
    @DisplayName("Testing if the whole PizzaDronz app works as expected on day (3)")
    void testWholeSystemDay3()throws IOException {
        RetrieveServerData.setDefaultUrl(serverBaseAddress);
        SingletonAccess.getInstance().readCentralArea();
        Restaurant.getRestaurantsFromRestServer(serverBaseAddress);

        NoFlyZones.setNoFlyZones();
        String day ="2023-03-21";

        Instant startTimeOfBeforePathGeneration = Instant.now();
        Delivery droneMovement = new Delivery(day);
        Instant timeAfterPathGeneration = Instant.now();

        int movesMade = droneMovement.getMovesMade();
        assertTrue(movesMade < 2000);
        System.out.println("Moves made in the day are under 2000");

        int numberOrderDelivered = droneMovement.getOrdersDelivered();
        assertEquals(numberOrderDelivered,30);
        System.out.println("Drone maximizes the number of order it delivers");

        var timetaken =
                Duration.between(startTimeOfBeforePathGeneration,timeAfterPathGeneration).getSeconds();
        assertTrue(timetaken < 60);
        System.out.println("Time limit does not exceed");

        String path = System.getProperty("user.dir") + "/resultfiles";
        String file1 = "deliveries-2023-03-21.json";
        String file2 = "drone-2023-03-21.geojson";
        String file3 = "flightpath-2023-03-21.json";
        String check1 = path + "/" + file1;
        String check2 = path + "/" + file2;
        String check3 = path + "/" + file3;
        assertTrue(new File(check1).exists());
        assertTrue(new File(check2).exists());
        assertTrue(new File(check3).exists());
    }

}