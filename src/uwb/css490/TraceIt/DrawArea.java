package uwb.css490.TraceIt;

import java.util.ArrayList;
import java.util.Random;

import android.graphics.*;
import uwb.css490.TraceIt.Polygon.PolygonGenerator;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

public class DrawArea extends SurfaceView implements Runnable, DebugMode {

	private static final int FRAME_NUMBER = 8;
	private static final int FRAME_RATE = 30;

	Thread altUIThread = null;
	SurfaceHolder altUIHolder;
	boolean canRun = false;

	Paint mPaint;
	Paint shapePaint;

	Paint debugPaint;

	Bitmap mBitmap, mBackground, animBitmap;
	Canvas mCanvas;

	public Path mPath, polygonPath;
	PathMeasure mPathMeasure, polygonPathMeasure;
	float pathCopyLength, polyPathLength;

	Context mContext;
	//ArrayList<Boolean> matchStatistics;
	int hits, misses;

	PointF startPoint;
	boolean doneDrawing;
	public int pid;
	public boolean canDraw;
	boolean isAnimating;
	private boolean markedForClear;
	int currFrame;
	int matchPercentage;
	boolean passedChecks;

	Activity main;

	public PolygonGenerator pgen;

	// int[] pageFlipResources;
	Bitmap[] pageFlipResources;

	private float mX, mY;
	private static final float TOUCH_TOLERANCE = 0;
	private static final int POLY_STROKE = 30;
	private static final int LINE_STROKE = 20;

	public DrawArea(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context;
		altUIHolder = getHolder();

		this.setBackgroundColor(Color.TRANSPARENT);
		this.setZOrderOnTop(true); // necessary
		altUIHolder.setFormat(PixelFormat.TRANSPARENT);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(0xFFFFFFFF);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(dpConverter(LINE_STROKE));

		shapePaint = new Paint();
		shapePaint.setAntiAlias(true);
		shapePaint.setDither(true);
		shapePaint.setColor(0xAA222222);
		shapePaint.setStyle(Paint.Style.STROKE);
		shapePaint.setStrokeJoin(Paint.Join.ROUND);
		shapePaint.setStrokeCap(Paint.Cap.ROUND);
		shapePaint.setStrokeWidth(dpConverter(POLY_STROKE));

		debugPaint = new Paint();
		debugPaint.setAntiAlias(true);
		debugPaint.setDither(true);
		debugPaint.setColor(0xFF00FF00);
		debugPaint.setStyle(Paint.Style.STROKE);
		debugPaint.setStrokeJoin(Paint.Join.ROUND);
		debugPaint.setStrokeCap(Paint.Cap.ROUND);
		debugPaint.setStrokeWidth(dpConverter(LINE_STROKE - 10));

		mPath = new Path();
		mPathMeasure = new PathMeasure();

		polygonPath = new Path();
		polygonPathMeasure = new PathMeasure();

		//matchStatistics = new ArrayList<Boolean>();
		this.hits = 0;
		this.misses = 0;
		startPoint = new PointF(-1, -1);
		doneDrawing = false;

		canDraw = false;
		isAnimating = false;
		markedForClear = false;

		pathCopyLength = 0;
		polyPathLength = 0;

		pid = -1;

		initializeFrames(context);
		currFrame = 0;
	}

	private void initializeFrames(Context context) {
		pageFlipResources = new Bitmap[FRAME_NUMBER];

		for (int i = 0; i < pageFlipResources.length; i++) {
			String resourceName = "pageflip" + (i + 1);
			int resID = getResources().getIdentifier(resourceName, "drawable",
					context.getPackageName());

			pageFlipResources[i] = BitmapFactory.decodeResource(getResources(),
					resID);

		}

	}

	private void resizeFrames(int w, int h) {
		for (int i = 0; i < pageFlipResources.length; i++) {
			pageFlipResources[i] = Bitmap.createScaledBitmap(
					pageFlipResources[i], w, h, true);
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// mBackground = BitmapFactory.decodeResource(getResources(),
		// R.drawable.canvas_background);
		// mBackground = Bitmap.createScaledBitmap(mBackground, w, h, true);

		mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

		// animBitmap = BitmapFactory.decodeResource(getResources(),
		// R.drawable.page_flip_animation);
		// mCanvas = new Canvas(mBitmap);

		resizeFrames(w, h);

		pgen = new PolygonGenerator(w, h);

		pgen.generatePolygon();
		polygonPath = pgen.getPath();
		polygonPathMeasure.setPath(polygonPath, false);
		polyPathLength = polygonPathMeasure.getLength();

	}

	public void touch_start(float x, float y) {
		matchPercentage = 0;
		passedChecks = false;
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
		startPoint.set(x, y);
		// Log.d("Match", "StartX: " + x);
		// Log.d("Match", "StartY: " + y);
	}

	public void touch_move(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (!doneDrawing && (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE)) {

			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;

			mPathMeasure.setPath(mPath, false);
			float pathLength = mPathMeasure.getLength();

			if (pathLength > 1.25 * polyPathLength) {
				doneDrawing = true;
				mPath.reset();
				pgen.resetCheckpoints();
				return;
			}

			if (pathLength > 75 && Math.abs(startPoint.x - x) < 50f
					&& Math.abs(startPoint.y - y) < 50f) {

				doneDrawing = true;
				Log.d("Match",
						"matched start to finish: "
								+ Math.abs((int) startPoint.x - (int) x));
				pathCopyLength = pathLength;
				mPath.reset();
				return;
			}

			double distance = pgen.getShortestDistance(new PointF(x, y));

			if (Math.abs(distance) < (dpConverter(POLY_STROKE) + dpConverter(LINE_STROKE)) / 4) {
				pgen.withinVertex(
						new PointF(x, y),
						(dpConverter(POLY_STROKE) + dpConverter(LINE_STROKE)) / 2);
				if (debugModeNumbers) {
					debugPaint.setColor(0xFF00FF00);
					mCanvas.drawPoint(x, y, debugPaint);
				}
				//matchStatistics.add(true);'
				this.hits++;
			} else {
				if (debugModeNumbers) {
					debugPaint.setColor(0xFF0000FF);
					mCanvas.drawPoint(x, y, debugPaint);
				}

				//matchStatistics.add(false);
				this.misses++;
			}

		}
	}

	public void touch_up() {
		// mPath.lineTo(mX, mY);
		// commit the path to our offscreen
		// mCanvas.drawPath(mPath, mPaint);
		// mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
		// kill this so we don't double draw

		Log.d("STATISTICS", "poly Lenght: " + polyPathLength);
		Log.d("STATISTICS", "path Lenght: " + pathCopyLength);
		if (doneDrawing && pathCopyLength > .75 * polyPathLength
				&& pathCopyLength < 1.25 * polyPathLength) {
			//int matchCount = 0;

//			for (boolean test : matchStatistics) {
//				if (test == true)
//					matchCount++;
//			}

			int totalPoints = this.hits + this.misses;
			
			matchPercentage = (totalPoints > 0) ? 100 * this.hits
					/ totalPoints : 0;

			Log.d("STATISTICS", "Percentage: " + matchPercentage
					+ " polyLength 75: " + .75 * polyPathLength
					+ " polyLength 1.25: " + 1.25 * polyPathLength);

			if (matchPercentage > 60 && pgen.passedAllCheckpoints()) {
				// Toast.makeText(getContext(), "Pass",
				// Toast.LENGTH_SHORT).show();
				int points = generateScore(matchPercentage);
				int time = generateTimeBonus(matchPercentage);
				passedChecks = true;
				((MainActivity) main).animateStats(points, time);

				doPageFlip();

				// ((MainActivity) main).playAnimation();
				((MainActivity) main).playPageFlipSound();
				regenerateShape();
				// ((MainActivity) main).saveScore(score);
			} // else

			// Toast.makeText(getContext(), "Fail", Toast.LENGTH_SHORT).show();
		} else {
			// Toast.makeText(getContext(), "Fail", Toast.LENGTH_SHORT).show();
		}

		reset();
	}

	public boolean passedCheckPts() {
		return passedChecks;
	}

	public int getMatchPercent() {
		return matchPercentage;
	}

	private int generateScore(int matchPerc) {
		return ((matchPerc * matchPerc) / 100);
	}

	private int generateTimeBonus(int matchPerc) {
		return (matchPerc / 10 - 6);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}

	public void setActivity(Activity mainActivity) {
		main = mainActivity;
	}

	private void regenerateShape() {
		Log.d("DRAW AREA", "Regenerate Shape");
		pgen.generatePolygon();
		polygonPath = pgen.getPath();
		polygonPathMeasure.setPath(polygonPath, false);
		polyPathLength = polygonPathMeasure.getLength();
		// mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		// mCanvas.drawPath(polygonPath, shapePaint);
	}

	private void reset() {
		Log.d("DRAW AREA", "Reset");
		pathCopyLength = 0;
		doneDrawing = false;
		this.hits = this.misses = 0;
		mPath.reset();
		pgen.resetCheckpoints();
	}

	public void resetCanvas() {
		Log.d("DRAW AREA", "Reset Canvas");
		reset();
		regenerateShape();
		this.canRun = true;
		pause();
		resume();

	}

	private float dpConverter(int dpSize) {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		float convertedValue = TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, dpSize, dm);

		return convertedValue;
	}

	public synchronized void setCanDraw(boolean state) {
		canDraw = state;
		// mCanvas.drawPath(polygonPath, shapePaint);
	}

	@Override
	public void run() {
		while (canRun) {

			if (markedForClear || canDraw) {
				//Log.d("DRAWING AREA", "Started Drawing");

				if (!altUIHolder.getSurface().isValid())
					continue;

				Canvas test = altUIHolder.lockCanvas();
				test.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

				// if (this.clearFlag) {
				// this.canRun = false;
				// this.clearFlag = false;
				// Log.d("CLEAR", "cleared the canvas");
				// }

				if (!markedForClear) {
					if (!isAnimating) {

						// test.setBitmap(mBitmap);

						// test.drawBitmap(mBackground, 0, 0, null);
						// test.drawBitmap(mBitmap, 0, 0, null);
						// canvas.drawBitmap(animBitmap,0, 0, null);
						test.drawPath(polygonPath, shapePaint);
						test.drawPath(mPath, mPaint);

					} else {

						if (currFrame < pageFlipResources.length) {

							// mBackground =
							// BitmapFactory.decodeResource(getResources(),
							// pageFlipResources[currFrame]);
							// mBackground =
							// Bitmap.createScaledBitmap(mBackground,
							// test.getWidth(), test.getHeight(), true);

							test.drawBitmap(pageFlipResources[currFrame], 0, 0,
									null);
							try {
								Thread.sleep(FRAME_RATE);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							currFrame++;

						} else {
							currFrame = 0;
							isAnimating = false;
						}

					}
				} else {
					this.markedForClear = false;
				}
				altUIHolder.unlockCanvasAndPost(test);
			}
		}
	}

	public void pause() {
		canRun = false;
		if(altUIThread == null)
			return; 
		
		while (true) {
			try {
				altUIThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			break;
		}

		altUIThread = null;

	}

	public void resume() {
		canRun = true;
		altUIThread = new Thread(this);
		altUIThread.start();
	}

	// public void setAnimating(boolean state){
	// this.isAnimating = state;
	// }

	private void doPageFlip() {
		isAnimating = true;

	}

	public void setClearFlag() {
		// Log.d("CLEAR", "cleared the canvas");
		this.markedForClear = true;
	}

}
