package uk.ac.ed.inf;

/**
 * An enum class that has all the valid directions that the
 * drone can move in along with the angle of movement taking East as 0,
 * increasing in the counter-clock-wise.
 */
public enum CompassDirection {

    EAST(0),
    EAST_NORTH_EAST(22.5),
    NORTH_EAST(45),
    NORTH_NORTH_EAST(67.5),
    NORTH(90),
    NORTH_NORTH_WEST(112.5),
    NORTH_WEST(135),
    WEST_NORTH_WEST(157.5),
    WEST(180),
    WEST_SOUTH_WEST(202.5),
    SOUTH_WEST(225),
    SOUTH_SOUTH_WEST(247.5),
    SOUTH(270),
    SOUTH_SOUTH_EAST(292.5),
    SOUTH_EAST(315),
    EAST_SOUTH_EAST(337.5);

    final double directionDegree;

    /**
     * Parameterized constructor for CompassDirection enum that
     * assigns the parameter degree to the field directionDegree
     * @param degree The angle of the direction in degrees starting
     *               from East being 0 and increasing counter-clock-wise
     */
    CompassDirection(double degree){
        directionDegree = degree;
    }

    /**
     * This method serves to return the angle of the direction passed to it
     * @return the angle of the CompassDirection enum passed to it in degrees
     */
    double getDirectionDegree(){
        return directionDegree;
    }
}
