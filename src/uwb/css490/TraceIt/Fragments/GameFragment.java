package uwb.css490.TraceIt.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import uwb.css490.TraceIt.MainActivity;
import uwb.css490.TraceIt.R;
import android.widget.ImageView;
import android.view.animation.*;
import android.view.animation.Animation.AnimationListener;

public class GameFragment extends Fragment {

	private static final int MAX_COUNTDOWN = 3;
	
	private Activity main;
	private FragmentManager fManager;
	private GameStatsFragment gameStatsFragment;
	private DrawingAreaFragment drawingAreaFragment;
	private PauseFragment pauseFragment;
	private GameOverFragment gameOverFragment;
	private ImageView theCount;
	private int currentCount;
	private int currentCountDown;
	private boolean countDownState;
	private AlphaAnimation animation;

	// default constructor
	public GameFragment() {

		// initialize the fragments controlled by this fragment
		this.gameStatsFragment = new GameStatsFragment();
		this.drawingAreaFragment = new DrawingAreaFragment();
		this.pauseFragment = new PauseFragment();
		this.gameOverFragment = new GameOverFragment();

	}

	/*--------------------------------------Overrides ----------------------------------*/

	@Override
	// called when the fragment gets attached
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		// store the activity
		this.main = activity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.game_fragment, container,
				false);
		
		// attach fragments
		this.fManager = getFragmentManager();
		attachFragments();
		
		this.fManager.beginTransaction().hide(drawingAreaFragment).commit();
		
		// count down
		theCount = (ImageView) rootView.findViewById(R.id.countDown);
		this.currentCountDown = -1;
		this.countDownState = false;
		this.currentCount = MAX_COUNTDOWN;
		
		animation = new AlphaAnimation(1.0f, 0.0f);
		animation.setDuration(1000);
		animation.setAnimationListener(new AnimationListener() {
			public void onAnimationEnd(Animation animation) {
				if(currentCountDown == -2)
					return;
				currentCountDown = currentCount;
				countDownImg(currentCount-1);
			}
			public void onAnimationStart(Animation animation) {
				((MainActivity) main).playCountdownBeepSound();
			}
		    public void onAnimationRepeat(Animation animation) {}
		 });
		
		
		countDownImg(MAX_COUNTDOWN);
		
		return rootView;
	}

	/*--------------------------------------Attach Fragments ----------------------------------*/

	// attaches the gamestats and drawingarea fragments
	public void attachFragments() {

		// check if the gamestats fragment has been previously attached
		// if so attach back, otherwise add it
		if (this.gameStatsFragment.isDetached())
			this.fManager.beginTransaction().attach(this.gameStatsFragment)
					.commit();
		else
			this.fManager.beginTransaction()
					.add(R.id.mainContainer, this.gameStatsFragment).commit();

		// check if the drawingarea fragment has been previously attached
		// if so attach back, otherwise add it
		if (this.drawingAreaFragment.isDetached())
			this.fManager.beginTransaction().attach(this.drawingAreaFragment)
					.commit();
		else
			this.fManager.beginTransaction()
					.add(R.id.mainContainer, this.drawingAreaFragment).commit();

	}

	// attaches the pause fragment
	public void attachPauseFragment() {
		
		// Log.d("CLEAR", "cleared the canvas");

		// // clear the canvas
		// this.drawingAreaFragment.clearCanvas();

		// stop the timer
		this.gameStatsFragment.stopTimer();
		if(this.currentCountDown >= -1 && this.countDownState == false){
			
			stopCountDown();
		} else {
			this.drawingAreaFragment.drawingCanvas.setClearFlag();
			this.drawingAreaFragment.disableCanvas();
			//clearCanvas();
		}
		// check if the pause fragment has been previously attached
		// if so attach back, otherwise add it
		
		if (this.pauseFragment.isDetached())
			this.fManager.beginTransaction().attach(this.pauseFragment)
					.commit();
		else
			this.fManager.beginTransaction()
					.add(R.id.mainContainer, this.pauseFragment).commit();
		

	}

	// attaches the gameover fragment
	public void attachGameOverFragment() {

		// check if the gameover fragment has been previously attached
		// if so attach back, otherwise add it
		if (this.gameOverFragment.isDetached())
			this.fManager.beginTransaction().attach(this.gameOverFragment)
					.commit();
		else
			this.fManager.beginTransaction()
					.add(R.id.mainContainer, this.gameOverFragment).commit();

		// if the pause fragment is still attached, detach it
		if (this.pauseFragment.isResumed())
			this.fManager.beginTransaction().detach(this.pauseFragment)
					.commit();
	}

	/*--------------------------------------Detach Fragments ----------------------------------*/

	// detaches the pause fragment
	public void detachPauseFragment() {
		
		int timerLag = 0;
		
		if(this.currentCountDown == -2 && this.countDownState == false){
			this.currentCountDown = -1;
			countDownImg(MAX_COUNTDOWN);
			timerLag = 3500;
		}
		else{
			this.drawingAreaFragment.enableCanvas();
			Log.d("DETACH PAUSE", "ENABLING CANVAS");
			this.drawingAreaFragment.resetCanvas();
			//this.drawingAreaFragment.drawingCanvas.setClearFlag();
		}

		// detach the pause fragment
		this.fManager.beginTransaction().detach(this.pauseFragment).commit();

		// resume the timer
		if (this.gameStatsFragment.isResumed())
			this.gameStatsFragment.continueTimer(timerLag);
	}

	// detaches the gameover fragment
	public void detachGameOverFragment() {
		Log.d("A", "about to detach gameover");

		// detaches the gameover fragment
		if (this.gameOverFragment.isResumed())
			this.fManager.beginTransaction().detach(this.gameOverFragment)
					.commit();
	}

	// detaches the pause, gamestats and drawingarea fragments
	public void detachFragments() {

		// detach the gameover fragment if it is resumed
		detachGameOverFragment();

		// detach the pause fragment if it is resumed
		if (this.pauseFragment.isResumed())
			this.fManager.beginTransaction().detach(this.pauseFragment)
					.commit();

		// detaches the fragments in order
		this.fManager.beginTransaction().detach(this.gameStatsFragment)
				.commit();
		this.fManager.beginTransaction().detach(this.drawingAreaFragment)
				.commit();

	}

	/*--------------------------------------Score/Time Operations ----------------------------------*/

	// updates the current score and time
	// based on passed score and time values
	public void animateStats(int score, int time) {
		this.drawingAreaFragment.animateScore(score);
		
		updateTime(time);
		this.drawingAreaFragment.animateTime(time);
	}
	
	
	public void updateScore(int score) {
		this.gameStatsFragment.updateScore(score);
	}
	
	public void updateTime(int time) {
		this.gameStatsFragment.updateTime(-time);
	}
	
	public void updateTimeText(int time) {
		this.gameStatsFragment.updateTimeText(-time);
	}

	// returns the current score
	public int getCurrentScore() {
		return this.gameStatsFragment.getCurrentScore();
	}

	// stops the timer
	public void stopTimer() {
		this.gameStatsFragment.stopTimer();

	}
	
	// animates the count down before game start
	private synchronized void countDownImg(final int count) {
		if (count == 3) {
			theCount.setImageResource(R.drawable.three_400);
		}
		else if (count == 2) {
			theCount.setImageResource(R.drawable.two_400);
		}
		else if (count == 1) {
			theCount.setImageResource(R.drawable.one_400);
			
		}
		else if (count == 0){
			Log.d("COUNT","Done with count");
			theCount.setVisibility(View.GONE);
			this.fManager.beginTransaction().show(drawingAreaFragment).commit();
			this.countDownState = true;
			//this.gameStatsFragment.continueTimer(1000);
			return;
		}
		
		currentCount = count;
		theCount.startAnimation(animation);
	}
	
	
	public int stopCountDown(){
		int temp = this.currentCountDown;
		this.currentCountDown = -2;
		return temp;
	}

	/*--------------------------------------Canvas Operations ----------------------------------*/

	// enables the user to draw and shape to be generated
	public void enableCanvas() {
		this.drawingAreaFragment.enableCanvas();
	}

	// resumes the state of the canvas
	public void resume() {
		if (this.drawingAreaFragment != null)
			this.drawingAreaFragment.drawingCanvas.resume();
	}

	// pauses the state of the canvas
	public void pause() {
		if (this.drawingAreaFragment != null)
			this.drawingAreaFragment.drawingCanvas.pause();
	}

	public boolean isPaused() {
		if (this.pauseFragment != null)
			return this.pauseFragment.isVisible();

		return false;
	}





//	public void clearCanvas() {
//		if (this.drawingAreaFragment != null
//				&& this.drawingAreaFragment.drawingCanvas != null)
//			this.drawingAreaFragment.drawingCanvas.setClearFlag();
//
//	}

}
