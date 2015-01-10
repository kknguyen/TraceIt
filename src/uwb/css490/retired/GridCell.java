package uwb.css490.retired;

import java.util.Random;

import android.graphics.PointF;

public class GridCell {
	
	private float topLeftX;
	private float topLeftY;
	private float sideLength;
	private PointF randPoint;
	private Random randGenerator;
	
	public GridCell(float x , float y, float length){
		this.topLeftX = x;
		this.topLeftY = y;
		this.sideLength = length;
		this.randGenerator = new Random();
		this.randPoint = new PointF(-1,-1);
	}
	
	public PointF getPoint(){
		return this.randPoint;
	}
	
	
	public PointF generatePoint(){

		float x = randGenerator.nextFloat()*(sideLength) + topLeftX;
		float y = randGenerator.nextFloat()*(sideLength) + topLeftY;
		
		randPoint.set(x, y);
		return getPoint();
	}

}
