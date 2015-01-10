package uwb.css490.TraceIt.Fragments;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import uwb.css490.TraceIt.DebugMode;
import uwb.css490.TraceIt.MainActivity;
import uwb.css490.TraceIt.R;

public class GameStatsFragment extends Fragment implements DebugMode
{

	Activity main;
	ImageButton pauseButton;
	TextView scoreText;
	TextView timeText;
	Timer timer;
	TimerTask timerTask;
	int score;
	int timeLeft, timeLeftText;
	boolean isAnimating;

	final Handler handler = new Handler();

	final static long TIME_BEFORE_START = 4000;
	final static long TIME_INTERVAL = 1000;
	final static int DEFAULT_TIME_VALUE = 20;

	public GameStatsFragment()
	{
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		main = activity;

	}

	private void startTimer(long bufferTime)
	{
		timer = new Timer();

		initializeTimerTask();
		timer.schedule(timerTask, bufferTime, TIME_INTERVAL);

	}

	private void initializeTimerTask()
	{
		timerTask = new TimerTask()
		{

			@Override

			public void run() {
				if(timeLeft == DEFAULT_TIME_VALUE + 1){
					((MainActivity) main).enableCanvas();
				}

				handler.post(new Runnable() {
					
					@Override
					public void run() {
						updateTime(1);
						updateTimeText(1);
						if(timeLeft <= 0) {
							stopTimer();
							((MainActivity) main).attachGameOverFragment();
						}
						if(timeLeft < 5 && timeLeft > 0) {
							timeText.setTextColor(Color.RED);	
						}
						else 
							timeText.setTextColor(Color.BLACK);
						if(timeLeft == 5)
							((MainActivity) main).playFiveSecs();
					}
				});

			}
		};

	}

	public void stopTimer()
	{
		if (timer != null)
		{
			timer.cancel();
			timer = null;
		}
	}

	// Pauses the timer by canceling it.
	// Upon resume it simply creates a new timer using the startTimer function
	// To pick up where the former left off
//	public void pauseTimer()
//	{
//		Log.d("PAUSE", "Initiating pause with " + timeLeft + " seconds left");
//		this.timer.cancel();
//		timerTask.cancel();
//	}

	public void continueTimer(long time)
	{
		Log.d("RESUME", "Initiating resume");
		startTimer(time);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.gamestats_fragment,
				container, false);

		//rootView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

		score = 0;
		isAnimating = false;
		if (debugModeNumbers)
		{
			timeLeft = 60;
		}
		else
		{
			timeLeft = DEFAULT_TIME_VALUE;
		}
		pauseButton = (ImageButton) rootView.findViewById(R.id.pauseButton);

		pauseButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				((MainActivity) main).attachPauseFragment();
			}
		});
		Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "FredokaOne-Regular.ttf");
		scoreText = (TextView) rootView.findViewById(R.id.scoreText);
		scoreText.setTextColor(Color.BLACK);
		scoreText.setText("Score: " + this.score);

		scoreText.setTypeface(font);
		timeText = (TextView) rootView.findViewById(R.id.timeLeftText);
		timeText.setText("Time Left: " + this.timeLeft + "s");
		timeText.setTextColor(Color.BLACK);
		timeText.setTypeface(font);
		
		timeLeft++;
		timeLeftText = timeLeft;
		startTimer(TIME_BEFORE_START);

		return rootView;

	}

	public void updateScore(int score)
	{
		this.score += score;
		scoreText.setText("Score: " + this.score);
		((MainActivity) main).playAddPointsSound();
	}

	public void updateTime(int seconds)
	{
		this.timeLeft -= seconds;
	}
	
	public void updateTimeText(int time) {
		this.timeLeftText -= time;
		timeText.setText("Time Left: " + this.timeLeftText + "s");
	}

	public int getCurrentScore()
	{
		// TODO Auto-generated method stub
		return score;
	}



//	public void startAnimation(int totalAnimationTime) {
//		pauseTimer();
//		continueTimer(totalAnimationTime);
//		isAnimating = true;
//	}
}
