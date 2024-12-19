package uk.ac.ed.inf.pathFinding;

import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.interfaces.LngLatHandling;

import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

public class LngLatHandler implements LngLatHandling {
    /**
     * get the distance between two positions
     * @param startPosition is where the start is
     * @param endPosition is where the end is
     * @return the Euclidean distance between the positions
     */
    @Override
    public double distanceTo(LngLat startPosition, LngLat endPosition) {
        // Calculated euclidian distance between two points
        return Math.sqrt(Math.pow(startPosition.lng() - endPosition.lng(), 2) + Math.pow(startPosition.lat() - endPosition.lat(), 2));
    }

    /**
     * check if two positions are close (<= than SystemConstants.DRONE_IS_CLOSE_DISTANCE)
     * @param startPosition is the starting position
     * @param otherPosition is the position to check
     * @return if the positions are close
     */
    @Override
    public boolean isCloseTo(LngLat startPosition, LngLat otherPosition) {
        return distanceTo(startPosition, otherPosition) < SystemConstants.DRONE_IS_CLOSE_DISTANCE;
    }

    /**
     * special handling shortcut for the central area. Here an implementation might add special improved processing as the central region is always rectangular
     * @param point to be checked
     * @param centralArea the central area
     * @return if the point is in the central area
     */
    @Override
    public boolean isInCentralArea(LngLat point, NamedRegion centralArea) {
        if (centralArea == null) {
            throw new IllegalArgumentException("the named region is null");
        } else if (!centralArea.name().equals("central")) {
            throw new IllegalArgumentException("the named region: " + centralArea.name() + " is not valid - must be: central");
        } else {
            return this.isInRegion(point, centralArea);
        }
    }

    /**
     * check if the @position is in the @region (includes the border)
     * @param position to check
     * @param region as a closed polygon
     * @return if the position is inside the region (including the border)
     */
    @Override
    public boolean isInRegion(LngLat position, NamedRegion region) {
        Path2D polygon = new Path2D.Double();
        polygon.moveTo(region.vertices()[0].lat(), region.vertices()[0].lng());

        for (LngLat vertex : region.vertices()) {
            polygon.lineTo(vertex.lat(), vertex.lng());
        }

        polygon.closePath();

        return polygon.contains(new Point2D.Double(position.lat(), position.lng()));
    }

    /**
     * find the next position if an @angle is applied to a @startPosition
     * @param startPosition is where the start is
     * @param angle is the angle to use in degrees
     * @return the new position after the angle is used
     */
    @Override
    public LngLat nextPosition(LngLat startPosition, double angle) {
        if (angle == 999){
            return startPosition;
        } else {
            double lng = startPosition.lng() + (0.00015 * Math.cos((angle)));
            double lat = startPosition.lat() + (0.00015 * Math.sin((angle)));
            return new LngLat(lng, lat);
        }
    }
}
