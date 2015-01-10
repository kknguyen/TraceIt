package uwb.css490.retired;

import java.util.ArrayList;
import java.util.Random;

import android.graphics.PointF;
import android.util.Log;

public class PolyGrid
{

	private final GridCell[][] grid;
	private final int dimention;
	private final float topLeftX;
	private final float topLeftY;
	private final float gridLength;
	private final Random randGenerator;
	private final ArrayList<PointF> pointList;

	public PolyGrid(float x, float y, int dim, float length)
	{
		this.topLeftX = x;
		this.topLeftY = y;
		this.dimention = dim;
		this.gridLength = length;
		this.randGenerator = new Random();
		this.grid = new GridCell[dim][dim];
		this.pointList = new ArrayList<PointF>();

		initGrid();
	}

	private void initGrid()
	{
		float curr_x = topLeftX;
		float curr_y = topLeftY;
		float cell_length = gridLength / dimention;

		for (int row = 0; row < dimention; row++)
		{
			for (int col = 0; col < dimention; col++)
			{
				grid[row][col] = new GridCell(curr_x, curr_y, cell_length);
				curr_x += cell_length;
			}
			curr_x = topLeftX;
			curr_y += cell_length;
		}
	}

	public void generatePoints(int n)
	{
		pointList.clear();
		int mx = -1;
		int my = -1;

		for (int i = 0; i < n; i++)
		{
			int x = randGenerator.nextInt(dimention);
			int y = randGenerator.nextInt(dimention);

			Log.d("SAdas", x + ", " + y + ", " + dimention);

			if (mx != x || my != y)
			{
				pointList.add(grid[x][y].generatePoint());
				mx = x;
				my = y;
			}
			else
			{
				i--;
			}

		}
	}

	public ArrayList<PointF> getPoints()
	{
		return pointList;
	}

}
