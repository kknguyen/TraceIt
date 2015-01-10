package uwb.css490.TraceIt.Polygon;

import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;

import java.util.*;

public class PolygonGenerator {

	private PointGenerator pointGenerator;
    private Path polygonPath;
    private ArrayList<LineSegment> linetList;
	private ArrayList<PointF> pointList;
	private boolean[] checkpoints;
	private Random randGenerator;
	private float canvasWidth;
	private float canvasHeight;

    public PolygonGenerator(float width, float height){
    	this.canvasWidth = width;
    	this.canvasHeight = height;
    	this.polygonPath = null;
    	this.linetList = new ArrayList<LineSegment>();
    	this.pointList = new ArrayList<PointF>();
    	this.randGenerator = new Random();
		pointGenerator = new PointGenerator(width, height);
    }

    /**
     * Returns the last randomly generated path
     * @return the last randomly generated path, if a generation failed, it will return null
     */
    public Path getPath(){
        return polygonPath;
    }
    
    public ArrayList<LineSegment> getLineSegments(){
    	return linetList;
    }

    /**
     * Generates a polygon based on a given difficulty.
     * @return the path that was randomly generated, maybe null
     */
    public Path generatePolygon(){

		long startTime = System.currentTimeMillis();

        pointList.clear();

        Log.d("array", "" + pointList.toString());

        int randy = randGenerator.nextInt(5);

		pointList = pointGenerator.generatePoints(3 + randy);

		long genTimer = System.currentTimeMillis();

		Log.d("SHAPE_TIMER", "Time to generate shape " +  (genTimer - startTime));

		pointList = centerPoints(pointList);

        checkpoints = new boolean[pointList.size()];
        initializeCheckPoints(checkpoints);

        // Send points to construct the polygon
        connectPoints(pointList);

		long sortTime = System.currentTimeMillis();
		Log.d("SORT_TIMER", "Time to sort shape " +  (sortTime - genTimer));
		Log.d("TOTAL_TIMER", "Total shape gen timer " +  (sortTime - startTime));

        // Maybe null
        return polygonPath;

    }
    
    private void initializeCheckPoints(boolean[] array){
    	for(int i = 0; i < array.length; i++){
    		array[i] = false;
    	}
    }

	private ArrayList<PointF> centerPoints(ArrayList<PointF> pointList)
	{

		if (pointList.size() < 3) return pointList;

		float minX = Integer.MAX_VALUE, maxX = 0, minY = Integer.MAX_VALUE, maxY = 0;

		// Get min/max values
		for (PointF pnt : pointList)
		{

			// Test X values
			if (pnt.x < minX)
				minX = pnt.x;

			if (pnt.x > maxX)
				maxX = pnt.x;

			// Test Y values
			if (pnt.y < minY)
				minY = pnt.y;

			if (pnt.y > maxY)
				maxY = pnt.y;


		}

		float xAdjustment = ((canvasWidth - maxX) - minX) / 2;
		float yAdjustment = ((canvasHeight - maxY) - minY) / 2;

		for (PointF pnt : pointList)
		{

			pnt.x += xAdjustment;
			pnt.y += yAdjustment;

		}

		return pointList;

    }


	/**
	 * Responsible for connecting a set of 3 or more points. Assumes a populated
	 * array. Draws lines from array[i] --> array[i + 1].
	 *
	 * @param points
	 *            Array of type Points, must contain at least 3
	 */
	private void connectPoints(ArrayList<PointF> points)
	{
		linetList.clear();

		// Anything less than 3 isnt a shape
		if (points.size() < 3) return;

		polygonPath = new Path();

		// Position the painter on the first point
		polygonPath.moveTo(points.get(0).x, points.get(0).y);

		// Paint lines in between shapes
		for (int i = 0; i < points.size(); i++)
		{
			PointF start = points.get(i);
			PointF end = points.get((i + 1) % points.size());

			linetList.add(new LineSegment(start, end));
			polygonPath.lineTo(points.get((i + 1) % points.size()).x,
					points.get((i + 1) % points.size()).y);
		}

	}

	public double getShortestDistance(PointF point)
	{
		if (linetList.size() <= 2) return -1;

		double shortestDistance = Integer.MAX_VALUE;

		for (LineSegment line : linetList)
		{
			double temp = line.getNormalDistance(point);
			shortestDistance = (temp < shortestDistance) ? temp
					: shortestDistance;
		}

		return shortestDistance;
	}

	private int getClosestVertex(PointF point)
	{
		PointF vertex;
		int index = -1;
		double minDistance = Integer.MAX_VALUE;

		for (int i = 0; i < pointList.size(); i++)
		{
			vertex = pointList.get(i);
			double tempDist = getDistance(point, vertex);
			if (tempDist < minDistance)
			{
				minDistance = tempDist;
				index = i;
			}
		}

		return index;
	}

	private double getDistance(PointF p1, PointF p2)
	{

		return Math.sqrt(Math.pow((p2.x - p1.x), 2)
				+ Math.pow((p2.y - p1.y), 2));

	}

	public boolean withinVertex(PointF point, float radius)
	{
		//Log.d("Points Array", pointList.toString());
		int index = getClosestVertex(point);

		if (index < 0) return false;

		if (getDistance(point, pointList.get(index)) <= radius)
		{
			//Log.d("Closest Point", pointList.get(index).toString());
			checkpoints[index] = true;
			return true;
		}

		return false;
	}

	public boolean passedAllCheckpoints()
	{
		Log.d("Win Array", Arrays.toString(checkpoints));

		for (int i = 0; i < checkpoints.length; i++)
		{
			if (checkpoints[i] == false) return false;
		}

		return true;
	}

	public void resetCheckpoints()
	{
		initializeCheckPoints(checkpoints);

	}

}
