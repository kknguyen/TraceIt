package uwb.css490.TraceIt.Polygon;

import android.graphics.PointF;

/**
 * Created by Will on 11/6/2014.
 */
public class LineSegment {

    PointF startPoint;
    PointF endPoint;

    double slope;

    float yIntercept;
    
    double length;

    public LineSegment(){
        this.startPoint = new PointF(0,0);
        this.endPoint = new PointF(0,-1);
        this.slope = 0;
        this.yIntercept = 0;
        this.length = 0;
    }

    public LineSegment(PointF start, PointF end){
        this.startPoint = start;
        this.endPoint = end;
        this.slope = calcSlope();
        this.yIntercept = calcYIntercept();
        this.length = getDistance(startPoint,endPoint);
    }

    private double calcSlope() {
        double m = 0.0;

        m = (endPoint.y - startPoint.y)/(endPoint.x - startPoint.x);

        return m;
    }

    private float calcYIntercept(){
        float b = -1;

        b = (float) (startPoint.y - slope * startPoint.x);

        return b;
    }
    
	private double getDistance(PointF p1, PointF p2) {

		return Math.sqrt(Math.pow((p2.x - p1.x), 2)
				+ Math.pow((p2.y - p1.y), 2));

	}
	
	private double cosLaw(double a, double b, double c){
		double cos0 = 0;
		
		cos0 = (a*a + b*b - c*c)/(2*a*b);
		
		return cos0;
	}

    public double getNormalDistance(PointF point) {
        double distance = 0;
        
        if(isWithinSegment(point))
        	distance = Math.abs((point.y - slope*point.x - yIntercept)/Math.sqrt(slope * slope + 1));
        else {
        	double pToStart = getDistance(point, startPoint);
        	double pToEnd = getDistance(point, endPoint);
        	
        	distance = (pToStart < pToEnd) ? pToStart : pToEnd;
        }
        return distance;
    }
    
    public boolean isWithinSegment(PointF point){
    	boolean isWithin;
    	
    	double pToStart = getDistance(point, startPoint);
    	double pToEnd = getDistance(point, endPoint);
    	
    	double cosAngleA = cosLaw(pToStart, length, pToEnd);
    	double cosAngleB = cosLaw(pToEnd, length, pToStart);
    	
    	isWithin = (cosAngleA > 0 && cosAngleB > 0) ? true : false;
    	
    	return isWithin;
    }
    
}
