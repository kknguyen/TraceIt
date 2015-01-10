package uwb.css490.TraceIt.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import uwb.css490.TraceIt.DrawArea;
import uwb.css490.TraceIt.MainActivity;
import uwb.css490.TraceIt.R;
import android.widget.Toast;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.view.animation.*;
import android.view.animation.Animation.AnimationListener;

public class DrawingAreaFragment extends Fragment 
{
	DrawingAreaFragment temp;
	DrawArea drawingCanvas;
	Activity main;
	ImageView userFeedback;
	TextView scoreFeedback;
	TextView timeFeedback;
	AlphaAnimation fadeAnimation;
	
	TranslateAnimation scoreTranslateAnimation;
	AnimationSet scoreFadeAndMove;
	
	TranslateAnimation timeTranslateAnimation;
	AnimationSet timeFadeAndMove;
	
	int scoreToAdd;
	int timeToAdd;
	
	@Override
	public void onAttach(Activity activity)
	{
		Log.d("DRAWING AREA Fragment", "onAttach");
		super.onAttach(activity);
		main = activity;
	}
	
	@Override
	public void onPause() {
		Log.d("DRAWING AREA Fragment", "onPause");
		super.onPause();
		drawingCanvas.pause();
	}
	
	@Override
	public void onResume() {
		Log.d("DRAWING AREA Fragment", "onResume");
		super.onResume();
		drawingCanvas.resume();
		
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		Log.d("HIDDEN STATE","State: " + hidden);
		super.onHiddenChanged(hidden);
	}
	

	public DrawingAreaFragment()
	{
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.drawingarea_canvas,
				container, false);
		
		this.scoreToAdd = 0;
		this.timeToAdd = 0;
		
		Log.d("DRAWING AREA","BEFORE CANVAS");
		
//		rootView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//		drawingCanvas = new DrawArea(main);
//		drawingCanvas.setBackgroundResource(R.drawable.canvas_background);
		//drawingCanvas.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		drawingCanvas = (DrawArea)rootView.findViewById(R.id.drawArea);
		drawingCanvas.setActivity(getActivity());
		//drawingCanvas.resume();
		userFeedback = (ImageView) rootView.findViewById(R.id.userFeedback);
		userFeedback.setVisibility(View.GONE);
		
		scoreFeedback = (TextView) rootView.findViewById(R.id.scoreAdded);
		scoreFeedback.setVisibility(View.GONE);
		scoreFeedback.setTextColor(Color.BLACK);
		
		timeFeedback = (TextView) rootView.findViewById(R.id.timeAdded);
		timeFeedback.setVisibility(View.GONE);
		timeFeedback.setTextColor(Color.BLACK);
		
		
		// fade animation
		fadeAnimation = new AlphaAnimation(1.0f, 0.0f);
		fadeAnimation.setDuration(1000);
		fadeAnimation.setAnimationListener(new AnimationListener() {
			public void onAnimationEnd(Animation animation) {
				userFeedback.setVisibility(View.GONE);
				scoreFeedback.setVisibility(View.GONE);
			}
			public void onAnimationStart(Animation animation) {}
		    public void onAnimationRepeat(Animation animation) {}
		 });
		
		
		//Log.d("X COORD", " " + scoreFeedback.getLeft());
		
		
		//score translate animation
		scoreTranslateAnimation = new TranslateAnimation(-100f,  -300f, 0, -650f);
		scoreTranslateAnimation.setDuration(1000);
		scoreTranslateAnimation.setAnimationListener(new AnimationListener() {
			public void onAnimationEnd(Animation translateAnimation) {
				scoreFeedback.setVisibility(View.GONE);
				//int score = Integer.parseInt(scoreFeedback.getText().toString().substring(1));
				((MainActivity) main).updateScore(scoreToAdd);
			}
			public void onAnimationStart(Animation translateAnimation) {}
		    public void onAnimationRepeat(Animation translateAnimation) {}
		 });
		
		scoreFadeAndMove = new AnimationSet(false);
		scoreFadeAndMove.addAnimation(scoreTranslateAnimation);
		scoreFadeAndMove.addAnimation(fadeAnimation);
		
		
		//time translate animation
		timeTranslateAnimation = new TranslateAnimation(100f,  300f, 0, -650f);
		timeTranslateAnimation.setDuration(1000);
		timeTranslateAnimation.setAnimationListener(new AnimationListener() {
			public void onAnimationEnd(Animation translateAnimation) {
				timeFeedback.setVisibility(View.GONE);
				//int score = Integer.parseInt(scoreFeedback.getText().toString().substring(1));
				((MainActivity) main).updateTimeText(timeToAdd);
			}
			public void onAnimationStart(Animation translateAnimation) {}
		    public void onAnimationRepeat(Animation translateAnimation) {}
		 });
		
		timeFadeAndMove = new AnimationSet(false);
		timeFadeAndMove.addAnimation(timeTranslateAnimation);
		timeFadeAndMove.addAnimation(fadeAnimation);
		
		drawingCanvas.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
        		float x = event.getX();
        		float y = event.getY();

        		// get pointer index from the event object
        		int pointerIndex = event.getActionIndex();

        		// get pointer ID
        		int pointerId = event.getPointerId(pointerIndex);

        		// get masked (not specific to a pointer) action
        		int maskedAction = event.getActionMasked();

        		switch (maskedAction) {
        		case MotionEvent.ACTION_POINTER_DOWN:

        		case MotionEvent.ACTION_POINTER_UP:
        			drawingCanvas.mPath.reset();
        		case MotionEvent.ACTION_DOWN:
        			drawingCanvas.pid = pointerId;
        			drawingCanvas.touch_start(x, y);
        			break;

        		case MotionEvent.ACTION_MOVE:
        			if (!drawingCanvas.canDraw) {
        				drawingCanvas.touch_start(x, y);
        				return false;
        			}
        			if (pointerId == drawingCanvas.pid) {
        				drawingCanvas.touch_move(x, y);
        			}
        			break;
        		case MotionEvent.ACTION_UP:
        			drawingCanvas.touch_up();
        			giveFeedback();
        			break;
        		}
        		return true;
            }
        });
		return rootView;

	}

	
	private void giveFeedback() {
		if (drawingCanvas.getMatchPercent() > 90
			&& drawingCanvas.passedCheckPts()) {
			
			userFeedback.setImageResource(R.drawable.great);
			userFeedback.setVisibility(View.VISIBLE);
		}
		else if (drawingCanvas.getMatchPercent() > 60
			&& drawingCanvas.passedCheckPts()) {
			
			userFeedback.setImageResource(R.drawable.good);
			userFeedback.setVisibility(View.VISIBLE);
		}
		else {
			userFeedback.setImageResource(R.drawable.fail);
			userFeedback.setVisibility(View.VISIBLE);
			((MainActivity) main).playIncorrectBuzzerSound();
		}

		
		userFeedback.startAnimation(fadeAnimation);
		
	}
	
	public void resetCanvas() {
		drawingCanvas.resetCanvas();
	}

	public synchronized void enableCanvas() {
		Log.d("CANVAS", "ENABLED");
		drawingCanvas.setCanDraw(true);
	}
	
	public synchronized void disableCanvas() {
		Log.d("CANVAS", "DISABLED");
		drawingCanvas.setCanDraw(false);
	}

	public void animateScore(int score) {
		this.scoreToAdd = score;
		scoreFeedback.setText("+" + score);
		scoreFeedback.setVisibility(View.VISIBLE);			
		scoreFeedback.startAnimation(scoreFadeAndMove);
		Log.d("DRAWING AREA FRAGMENT", "SCORE: " + score);
	}

	public void animateTime(int time) {
		this.timeToAdd = time;
		timeFeedback.setText("+" + time);
		timeFeedback.setVisibility(View.VISIBLE);			
		timeFeedback.startAnimation(timeFadeAndMove);
		
	}

//	public void clearCanvas() {
//		drawingCanvas.setClearFlag();		
//	}
}
