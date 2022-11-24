package uk.ac.ed.inf;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInfo;
import static org.junit.jupiter.api.Assertions.*;

public class TestLngLat
{
    @BeforeEach
    void displayTestName(TestInfo testInfo)
    {
        System.out.println(testInfo.getDisplayName());
    }

    /*
    Starting with points that are on the vertices of the central area,
    so they are supposed to be considered as within the central area.
     */

    LngLat point1 = new LngLat(-3.192473, 55.946233); //Forrest Hill
    LngLat point2 = new LngLat(-3.184319, 55.946233); //KFC
    LngLat point3 = new LngLat(-3.192473, 55.942617); //Top of the Meadows
    LngLat point4 = new LngLat(-3.184319, 55.942617); //Buccleuch St Bus stop

    /*
    Then moving onto points that are on the boundary of the central area,
    that are supposed to be considered as within the central area.
     */

    // somewhere between KFC and Buccleuch St Bus stop (between point2 and point4)
    LngLat point5 = new LngLat(-3.184319, 55.945);
    //somewhere between KFC and Forrest Hill
    LngLat point6 = new LngLat(-3.190, 55.946233);
    //somewhere between Forrest Hill and Top of the Meadows
    LngLat point7 = new LngLat(-3.192473, 55.944);
    //somewhere between top of meadows and buccleuch st bus stop
    LngLat point8 = new LngLat(-3.185, 55.942617);

    /*
    Then moving onto points that are completely out of the central area
     */
    LngLat point9 = new LngLat(-3.184319, 58);
    LngLat point10 = new LngLat(-4,55.946233);
    LngLat point15 = new LngLat(-4, -60);

    /*
    Then points that are not that far away from the central area
     */
    LngLat point11 = new LngLat(-3.1925,55.946233);
    LngLat point12 = new LngLat(-3.192473,55.946234);
    LngLat point13 = new LngLat(-3.172623,55.946233);

    //Finally a point that is somewhere in the middle of the central area
    LngLat point14 = new LngLat(-3.184330, 55.942620);

    @Test
    @DisplayName("Testing different scenarios for inCentralArea method")
    public void testInCentralArea()
    {
        assertTrue(point1.inCentralArea());
        assertTrue(point2.inCentralArea());
        assertTrue(point3.inCentralArea());
        assertTrue(point4.inCentralArea());
        assertTrue(point5.inCentralArea());
        assertTrue(point6.inCentralArea());
        assertTrue(point7.inCentralArea());
        assertTrue(point8.inCentralArea());
        assertFalse(point9.inCentralArea());
        assertFalse(point10.inCentralArea());
        assertFalse(point11.inCentralArea());
        assertFalse(point12.inCentralArea());
        assertFalse(point13.inCentralArea());
        assertFalse(point15.inCentralArea());
        assertTrue(point14.inCentralArea());
        System.out.println("All test for inCentralArea works!");
    }

    @Test
    @DisplayName("Testing different scenarios for distanceTo method")
    public void testDistanceTo()
    {
        assertEquals(0.0023800254200345857, point5.distanceTo(point14));
        assertEquals(0.008154000000000217, point1.distanceTo(point2));
        assertEquals(115.94542913226212, point3.distanceTo(point15));
        assertEquals(0, point1.distanceTo(point1));
        System.out.println("All test for distanceTo works!");
    }

    @Test
    @DisplayName("Testing different scenarios for closeTo method")
    public void testCloseTo()
    {
        assertTrue(point1.closeTo(point1));
        assertTrue(point1.closeTo(point12));
        assertFalse(point15.closeTo(point14));
        assertFalse(point3.closeTo(point15));
        System.out.println("All test for closeTo works!");
    }

    @Test
    @DisplayName("Testing different scenarios for nextPosition method")
    public void testNextPosition()
    {
        assertEquals(point1, point1.nextPosition(null));
        var point1nextEast = point1.nextPosition(CompassDirection.EAST);
        assertEquals(point1.lng() + 0.00015, point1nextEast.lng());
        assertEquals(point1.lat(), point1nextEast.lat());
        var point13nextNorth = point13.nextPosition(CompassDirection.NORTH);
        assertEquals(point13.lat() + 0.00015, point13nextNorth.lat());
        assertEquals(point13.lng(), point13nextNorth.lng());
        var point15nextWest = point15.nextPosition(CompassDirection.WEST);
        assertEquals(point15.lng() - 0.00015, point15nextWest.lng());
        assertEquals(point15.lat(), point15nextWest.lat());
        var point8nextSouth = point8.nextPosition(CompassDirection.SOUTH);
        assertEquals(point8.lat() - 0.00015, point8nextSouth.lat());
        assertEquals(point8.lng(), point8nextSouth.lng());
        System.out.println("All test for nextPosition works!");
    }

}
