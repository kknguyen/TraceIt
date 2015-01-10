package uwb.css490.retired;

import android.graphics.PointF;

import java.util.Comparator;

/**
 * Created by William Pfeffer
 * Date: 11/8/2014
 * <p/>
 * Description:
 * <p/>
 * Expected Use:
 * <p/>
 * Limitations:
 */
public class PointCompareDesc implements Comparator<PointF> {

    @Override
    public int compare(PointF one, PointF two){

        if(one.x > two.x){
            return -1;
        }
        else if(one.x < two.x){
            return 1;
        }
        else if(one.y > two.y){
            return -1;
        }
        else
        {
            return 1;
        }

    }

}
