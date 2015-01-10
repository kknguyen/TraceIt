package uwb.css490.TraceIt.Polygon;

import android.graphics.PointF;

/**
 * Created by Will on 11/23/2014.
 *
 * Assumptions: This assumes that the upper bound  > lower bound, and that the upper bound is lower than half the
 * canvas. This also assumes that the angle is always in radians and is less than 2pi.
 *
 */
public class PointSector {

    // Two angles in radians, between which the point should spawn
    double minAngle;
    double maxAngle;

    // Definition of an arc on which the point should spawn
    double innerRadius;
    double outerRadius;

    public PointSector(double minA, double maxA, double iR, double oR){
        minAngle = minA;
        maxAngle = maxA;
        innerRadius = iR;
        outerRadius = oR;
    }

    public PointF getPoint(){

        PointF genPoint = new PointF();

        // Pick random angle between the minAngle & maxAngle
        double randomAngle = minAngle + (maxAngle - minAngle) * Math.random();

        // Pick random radius between the inner radius and outer radius
        double randomRadius = innerRadius + (outerRadius - innerRadius) * Math.random();

        // Pick a random x & y based on the radius and angle
        float x = (float) (Math.cos(randomAngle) * randomRadius);
        float y = (float) (Math.sin(randomAngle) * randomRadius);


        genPoint.set(x, y);
        return genPoint;

    }


}
