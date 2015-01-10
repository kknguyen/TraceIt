package uwb.css490.TraceIt.Polygon;

import android.graphics.PointF;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by William Pfeffer
 * Date: 11/6/2014
 *
 * Description: This is responsible for creating sectors based on the number of points to be generated. After these
 * sectors are created it selects a point within each sector.
 *
 */
public class PointGenerator {

    // Canvas information
    float width, height;

    // Generation constraint info
    // Chosen values for Radii
    double innerRadius, outerRadius;

    // 30 Degrees
    double MIN_ANGLE_RAD = Math.PI / 6;

    // Center values of the canvas
    int cX;
    int cY;

    ArrayList<PointSector> sectors = new ArrayList<PointSector>();

    public PointGenerator(float width, float height){

        this.height = height;
        this.width = width;

        // Chosen values for Radii
        innerRadius = width * .3;
        outerRadius = width * .45;

        // Center values of the canvas
        cX = (int) width / 2;
        cY = (int) height / 2;

    }

    /**
     * Initializes N number of sectors based on the number of points to be generated.
     * @param numPoints the number of points to be generated
     * @return success
     */
    private boolean initialize(int numPoints){

        if(numPoints < 3)
            return false;
        else if((2 * Math.PI / numPoints ) < MIN_ANGLE_RAD) {
            return false;
        }
        
        sectors.clear();
        // The padding on each side of the sector
        double SECTOR_PADDING = ((2 * Math.PI) / numPoints) * .15;

        for(int i = 0; i < numPoints; i++){

            double startAngle, endAngle;

            // The starting angle and ending angle of the sector
            startAngle = (((2 * Math.PI) / numPoints) * i) + SECTOR_PADDING;
            endAngle = (startAngle + ((2 * Math.PI) / numPoints)) - SECTOR_PADDING;

            // Create and add the sector
            PointSector sector = new PointSector(startAngle, endAngle, innerRadius, outerRadius);
            sectors.add(sector);

        }

        return true;

    }

    /**
     * This creates an array list of points, generated based on each sector generated ( # sectors == # points )
     * @param numToGen  The number of points to generate
     * @return  An ArrayList of points
     */
    public ArrayList<PointF> generatePoints(int numToGen){

        // Try to initialize
        if(!initialize(numToGen)){
            return null;
        }

        ArrayList<PointF> generatedPoints = new ArrayList<PointF>();

        // Create a point for each sector
        for(int i = 0; i < sectors.size(); i++){

            // Create Point
            PointF pnt = sectors.get(i).getPoint();

            // Adjust for center values
            pnt.offset(cX, cY);

            // Add to list
            generatedPoints.add(pnt);

        }

        return generatedPoints;

    }

}
