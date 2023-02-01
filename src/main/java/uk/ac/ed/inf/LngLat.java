package uk.ac.ed.inf;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * The class LngLat is in the form of a record so that things like getters, constructors and such can be omitted
 * This class represents a location on the map in the form of longitude and latitude
 * This is done by representing the locations as: (longitude,latitude)
 * @param lng this represents the longitude
 * @param lat this represents the latitude
 */

@JsonIgnoreProperties("name")
public record LngLat(
		@JsonProperty("longitude")
		double lng,
		@JsonProperty("latitude")
		double lat){

	/**
	 * These final constants store the DISTANCE_TOLERANCE, which dictates
	 * how much distance between two points could be considered as close to each other.
	 */
	private static final double DISTANCE_TOLERANCE = 0.00015;
	/**
	 * The other final constant MOVE_DISTANCE, dictates how much the distance the drone
	 * covers in "1 move".
	 */
	private static final double MOVE_DISTANCE = 0.00015;


	// This method returns whether a point is within the central area or not.

	/**
	 * This method checks if the current point is one that lies within the Central Campus area,
	 * which is found in the REST Server.
	 * The implementation of the method has been inspired by the StackOverflow post with the link:
	 * <a href="https://stackoverflow.com/questions/217578/how-can-i-determine-whether-a-2d-point-is-within-a-polygon">
	 *     StackOverflow link</a>
	 * @return true if the point is within the Central Campus area, including the boundaries, false otherwise
	 */
	public Boolean inCentralArea(){
//      The `var` below uses local type inference inorder to infer
//      the type of arguments returned by the receiveCentralAreaCoOrd
//      method from the SingletonAccess class.
		ArrayList<LngLat> centralAreaCoOrds = SingletonAccess.getInstance().getCentralAreaCoOrds();
        int centralAreaPolygonSize = centralAreaCoOrds.size();
        boolean isPointInsideCentralArea = false;
        double pointToCheckLat = this.lat;
		for (int i = 0, j = centralAreaPolygonSize - 1; i < centralAreaPolygonSize; j = i++){
			//Storing the i-th and j-th points in order to check is the provided point lies between the polygon
			LngLat pointI = centralAreaCoOrds.get(i);
			LngLat pointJ = centralAreaCoOrds.get(j);
			double pointILat= pointI.lat();
			double pointILng= pointI.lng();
			double pointJLat= pointJ.lat();
			double pointJLng= pointJ.lng();

			//The following if statement has been translated in to java following the StackOverflow post
			if (((pointILat > pointToCheckLat) != (pointJLat > pointToCheckLat))
							&&
					(this.lng <
						((pointJLng-(pointILng)) * (pointToCheckLat - pointILat) /
											(pointJLat-pointILat) + pointILng))){
				isPointInsideCentralArea = !isPointInsideCentralArea;
			}
			/* This else-if part of the iteration checks if the point lies on the boundary of the central area
			* This is done by utilising the property of a straight line that dictates the fact that if a point
			* lies on a line; then the distance from the vertices to the point is equal to the distance
			* between the vertices.
			 */
			else if ((distanceTo(pointI) + distanceTo(pointJ)) == pointI.distanceTo(pointJ)) {
				return true;
			}
		}
		return isPointInsideCentralArea;
	}


	//This method returns the pythagorean distance between the provided point and another point
	/**
	 * This method calculates the Pythagorean distance between the provided point and another point
	 * @param otherPoint another point passed in the same format: (longitude,latitude)
	 *                      whose distance has to be calculated from the given point
	 * @return the Pythagorean distance between the two points as a Double type
	 */
	public Double distanceTo(LngLat otherPoint){
		double squareDistanceBetweenLats = Math.pow((otherPoint.lat - this.lat),2);
		double squareDistanceBetweenLngs = Math.pow((otherPoint.lng - this.lng),2);
		return Math.sqrt(squareDistanceBetweenLats + squareDistanceBetweenLngs);
	}

	/**
	 * This method checks if a point is close to the current point, the distance tolerance of which
	 * is the final variable DISTANCE_TOLERANCE
	 * @param point the point to be checked in (longitude, latitude) format
	 * @return true if the distance between the two points is within the given distance tolerance false otherwise
	 */
	public boolean closeTo(LngLat point){
		return distanceTo(point) < DISTANCE_TOLERANCE;
	}

	/**
	 * This method updates the position of the drone after it makes a move in a certain direction.
	 * The distance covered by a single move is stored in the final variable 'MOVE_DISTANCE'.
	 * In case the drone is just hovering (given by the provided direction being null), the position is unchanged.
	 * @param direction the direction of the move the drone is about to take (Given as an Enum 'CompassDirection')
	 * @return the updated position of the drone after a move has been made in the provided direction,
	 * this is in the (longitude,latitude) format
	 */
	public LngLat nextPosition(CompassDirection direction){
		//if the drone is hovering (i.e. not headed in any direction), the current position of the drone is returned.
		if (direction == null){
			return this;
		}
		/*
		gets the angle in degree of the direction, taking east as 0 and increasing counterclockwise
		This is done using the getDegreeDirection method in the enum CompassDirection
		 */
		double degreeOfDirection = direction.getDirectionDegree();
		double newLng = this.lng + MOVE_DISTANCE * Math.cos(Math.toRadians(degreeOfDirection));
		double newLat = this.lat + MOVE_DISTANCE * Math.sin(Math.toRadians(degreeOfDirection));
		return (new LngLat(newLng,newLat));
	}

}