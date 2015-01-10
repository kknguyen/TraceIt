package uwb.css490.TraceIt;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import uwb.css490.TraceIt.Fragments.GameFragment;
import uwb.css490.TraceIt.Fragments.ScoreFragment;

public class MainActivity extends Activity {
	private static int NUM_SCORES = 5; // number of high score slots

	// the fragment manager that manages transactions
	private FragmentManager fManager;

	private MainFragment mainFragment; // main fragment
	private ScoreFragment leaderboardFragment; // score fragment
	private GameFragment gameFragment; // game fragment
	private boolean gameOver; // states if the game is being played or no
	private boolean soundState; // keeps the sound state
	private SharedPreferences sp; // holds the saved data after the app is
									// killed

	private MediaPlayer music, pageFlipSound, fiveSeconds, 
		incorrectBuzzer, addPoints, countdownBeep;

	@Override
	// creates the main fragment and leaderboard fragment
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.fManager = getFragmentManager();
		this.sp = getSharedPreferences("NAME", Context.MODE_PRIVATE);
		this.gameOver = false;
		this.soundState = getSoundState();
		this.music = new MediaPlayer();
		this.pageFlipSound = new MediaPlayer();
		this.fiveSeconds = new MediaPlayer();
		this.incorrectBuzzer = new MediaPlayer();
		this.addPoints = new MediaPlayer();
		this.countdownBeep = new MediaPlayer();
		this.gameFragment = new GameFragment();

		// loads the music files
		loadMusic();

		createMainFragment(); // create and attach the main fragment
		createLeaderboardFragment(); // just create the leaderboard fragment

	}

	/*--------------------------------------Initialize Fragements ----------------------------------*/

	// creates the main fragment and attaches it
	private void createMainFragment() {
		// create the main fragment based on the soundstate
		this.mainFragment = new MainFragment(soundState);

		// add and attach the main fragment
		this.fManager.beginTransaction()
				.add(R.id.mainContainer, this.mainFragment).commit();
	}

	// creates but doesn't attach the leaderboard fragment
	private void createLeaderboardFragment() {

		// if there were saved scores
		if (hasSavedScores())
			this.leaderboardFragment = new ScoreFragment(pullScores()); // get
																		// the
																		// scores

		// if there are no saved scores
		else {
			this.leaderboardFragment = new ScoreFragment();
		}
	}

	/*--------------------------------------Overrides ----------------------------------*/

	@Override
	// When the app goes out of sight (minimized or phone turned off)
	// - If a mainFragment exists, pauses the music
	// - Then checks if a gameFragment exists and if it is attached
	// If both criteria are met, pauses the game
	protected void onPause() {
		pauseMusicWithoutState();
		Log.d("GAME OVER STATUS", "" + gameOver);
		if (this.mainFragment != null && !this.mainFragment.isVisible()) {
			if (this.gameFragment != null && !this.gameFragment.isDetached()
					&& !this.gameFragment.isPaused() && !this.gameOver) {
				attachPauseFragment();
			}
			//this.gameFragment.clearCanvas();
		}
		
		
		super.onPause();
	}

	@Override
	// When the app comes back to the sight
	// we start playing the music
	protected void onResume() {
		super.onResume();
		playMusic();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/*--------------------------------------Attach Fragments ----------------------------------*/

	// attaches the score fragment
	public void attachScoreFragment() {
		// replace main fragment with leaderboard fragment
		this.fManager.beginTransaction()
				.replace(this.mainFragment.getId(), this.leaderboardFragment)
				.commit();
	}

	// attaches the game fragment
	public void attachGameFragement() {

		this.fManager.beginTransaction().detach(mainFragment).commit();

		// check whether to add or attach the game fragment
		if (this.gameFragment.isDetached()) {
			Log.d("attachGameFragment", "Attaching");
			this.fManager.beginTransaction().attach(this.gameFragment).commit();
		} else {
			Log.d("attachGameFragment", "adding");
			this.fManager.beginTransaction()
					.add(R.id.mainContainer, this.gameFragment).commit();

		}
	}

	// attaches pause fragmet
	public void attachPauseFragment() {
		this.gameFragment.attachPauseFragment();
	}

	// attaches gameover fragmet and sets the gameover flag
	public void attachGameOverFragment() {
		detachGameFragement();
		this.gameFragment.attachGameOverFragment();
		setGameOver(true);

	}

	// attaches main fragment
	public void attachMainFragment() {
		this.fManager.beginTransaction().attach(this.mainFragment).commit();

	}

	/*--------------------------------------Detach Fragments ----------------------------------*/

	// detaches the score fragment
	public void detachScoreFragment() {
		// replace leaderboard fragment with main fragment
		this.fManager.beginTransaction()
				.replace(this.leaderboardFragment.getId(), this.mainFragment)
				.commit();
	}

	// detaches the game fragment
	public void detachGameFragement() {
		this.gameFragment.stopTimer();
		this.gameFragment.detachFragments();

		updateHighScore();

		// detach the gamefragment
		this.fManager.beginTransaction().detach(gameFragment).commit();

	}

	// detaches the pause fragment
	public void detachPauseFragment() {
		this.gameFragment.detachPauseFragment();
	}

	/*--------------------------------------Gameover Operations ----------------------------------*/

	// detaches the gameover fragment
	public void restartGame() {
		setGameOver(false);

		// stop the timer
		this.gameFragment.stopTimer();

		// detach the game fragment
		this.gameFragment.detachGameOverFragment();

		// reset gameover flag
		setGameOver(false);
		attachGameFragement();
	}

	public void goToMainMenu() {
		setGameOver(false);

		// stop the timer
		this.gameFragment.stopTimer();

		// detach the game fragment
		this.gameFragment.detachGameOverFragment();

		// attach the main fragment
		this.fManager.beginTransaction().attach(this.mainFragment).commit();

	}

	/*--------------------------------------Save Data ----------------------------------*/

	// save the current high scores
	public void saveScores() {

		// gets the current scores from the leaderboard fragment
		int[] currentScores = this.leaderboardFragment.getScores();
		SharedPreferences.Editor editor = this.sp.edit();

		// each score is being saved in a shared preference
		for (int i = 0; i < currentScores.length; i++) {
			editor.putInt("SCORE" + i, currentScores[i]);
			editor.commit();
		}

	}

	// saves whether the game music is on or off
	public void saveSoundState(boolean state) {
		this.sp.edit().putBoolean("MUSICSTATE", state).commit();

		// Log.d("MUSIC STATE", state + "");
	}

	/*--------------------------------------Check/Get Saved Data ----------------------------------*/

	// checks if there has been any socres saved yet
	public boolean hasSavedScores() {
		// Log.d("Array size", this.sp.getAll().size() + "");
		return (this.sp.contains("SCORE0"));
	}

	// returns an array of retrieved scores
	// from the shared preferences
	private int[] pullScores() {
		int size = this.sp.getAll().size();
		int[] scores = new int[size];

		// retrieve each score and save it into array
		for (int i = 0; i < size; i++) {
			scores[i] = (this.sp.getInt("SCORE" + i, 0));
		}

		return scores;
	}

	// returns the saved sound state
	// returns true if there was no state saved
	public boolean getSoundState() {
		// Log.d("MUSIC STATE", this.sp.contains("MUSICSTATE") + " GOT");
		return this.sp.getBoolean("MUSICSTATE", true);
	}

	/*--------------------------------------Get/Set Scores ----------------------------------*/

	// returns the in-game score
	public int getCurrentScore() {
		return this.gameFragment.getCurrentScore();
	}

	// update the high scores
	private void updateHighScore() {

		// get the current game score
		int currentScore = this.gameFragment.getCurrentScore();
		Log.d("Curr Score", currentScore + "");

		// update if necessary
		this.leaderboardFragment.updateScores(currentScore);

		// save scores
		saveScores();
	}

	// add score and time based on completion quality
	public void animateStats(int score, int time) {
		this.gameFragment.animateStats(score, time);
	}
	
	public void updateScore(int score){
		this.gameFragment.updateScore(score);
	}
	
	public void updateTimeText(int time) {
		this.gameFragment.updateTimeText(time);
	}

	
	
	/*--------------------------------------Music Operations ----------------------------------*/

	// loads the music files
	private void loadMusic() {
		try {
			// open up the song from the assets folder
			AssetFileDescriptor descriptor = getAssets().openFd("b.mp3");
			// load the location of the song to the mediaplayer
			this.music.setDataSource(descriptor.getFileDescriptor(),
					descriptor.getStartOffset(), descriptor.getLength());
			descriptor.close();

			// prepare the mediaPlayer, "initialized but stopped"
			this.music.prepare();
			this.music.setVolume(50, 50);
			this.music.setLooping(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			// open up the song from the assets folder
			AssetFileDescriptor descriptor = getAssets().openFd("fivesecs.wav");
			// load the location of the song to the mediaplayer
			this.fiveSeconds.setDataSource(descriptor.getFileDescriptor(),
					descriptor.getStartOffset(), descriptor.getLength());
			descriptor.close();

			// prepare the mediaPlayer, "initialized but stopped"
			this.fiveSeconds.prepare();
			this.fiveSeconds.setVolume(50, 50);
			this.fiveSeconds.setLooping(false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			// open up the song from the assets folder
			AssetFileDescriptor descriptor = getAssets().openFd(
					"pageFlipSound.mp3");
			// load the location of the song to the mediaplayer
			this.pageFlipSound.setDataSource(descriptor.getFileDescriptor(),
					descriptor.getStartOffset(), descriptor.getLength());
			descriptor.close();

			// prepare the mediaPlayer, "initialized but stopped"
			this.pageFlipSound.prepare();
			this.pageFlipSound.setVolume(50, 50);
			this.pageFlipSound.setLooping(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			// open up the song from the assets folder
			AssetFileDescriptor descriptor = getAssets().openFd(
					"incorrectBuzzer.mp3");
			// load the location of the song to the mediaplayer
			this.incorrectBuzzer.setDataSource(descriptor.getFileDescriptor(),
					descriptor.getStartOffset(), descriptor.getLength());
			descriptor.close();

			// prepare the mediaPlayer, "initialized but stopped"
			this.incorrectBuzzer.prepare();
			this.incorrectBuzzer.setVolume(50, 50);
			this.incorrectBuzzer.setLooping(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			// open up the song from the assets folder
			AssetFileDescriptor descriptor = getAssets().openFd(
					"addPoints.mp3");
			// load the location of the song to the mediaplayer
			this.addPoints.setDataSource(descriptor.getFileDescriptor(),
					descriptor.getStartOffset(), descriptor.getLength());
			descriptor.close();

			// prepare the mediaPlayer, "initialized but stopped"
			this.addPoints.prepare();
			this.addPoints.setVolume(50, 50);
			this.addPoints.setLooping(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			// open up the song from the assets folder
			AssetFileDescriptor descriptor = getAssets().openFd(
					"countdownBeep.mp3");
			// load the location of the song to the mediaplayer
			this.countdownBeep.setDataSource(descriptor.getFileDescriptor(),
					descriptor.getStartOffset(), descriptor.getLength());
			descriptor.close();

			// prepare the mediaPlayer, "initialized but stopped"
			this.countdownBeep.prepare();
			this.countdownBeep.setVolume(50, 50);
			this.countdownBeep.setLooping(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// sets the sound state
	public void setSoundState(boolean state) {
		this.soundState = state;
	}

	// pauses the main sound track after checking the sound state
	public void pauseMusic() {
		if (soundState == false) {
			pauseMusicWithoutState();
		}
	}

	// pauses the main sound track
	private void pauseMusicWithoutState() {
		if (this.music.isPlaying()) {
			this.music.pause();
		}
	}

	// resumes the main sound track after checking the sound state
	public void playMusic() {
		if (soundState == true) {
			playMusicWithoutState();
		}
	}

	// resumes the main sound track
	private void playMusicWithoutState() {
		if (!this.music.isPlaying()) {
			this.music.start();
		}
	}

	/*--------------------------------------5 sec Warning Sound ------------------------------*/
	// plays the 5 second warning sound
	public void playFiveSecs() {
		if (!this.fiveSeconds.isPlaying() && soundState == true)
			this.fiveSeconds.start();
	}

	/*--------------------------------------Page Flip Sound ----------------------------------*/

	// plays the page flip sound
	public void playPageFlipSound() {
		if (!this.pageFlipSound.isPlaying() && soundState == true)
			this.pageFlipSound.start();
	}
	
	/*--------------------------------------Incorrect Buzzer Sound ----------------------------*/

	// plays the page flip sound
	public void playIncorrectBuzzerSound() {
		if (!this.incorrectBuzzer.isPlaying() && soundState == true)
			this.incorrectBuzzer.start();
	}
	
	/*--------------------------------------Add Points Sound ----------------------------------*/

	// plays the page flip sound
	public void playAddPointsSound() {
		if (!this.addPoints.isPlaying() && soundState == true)
			this.addPoints.start();
	}
	
	/*--------------------------------------Countdown Beep Sound ------------------------------*/

	// plays the page flip sound
	public void playCountdownBeepSound() {
		if (!this.countdownBeep.isPlaying() && soundState == true)
			this.countdownBeep.start();
	}

	/*--------------------------------------Game Status----------------------------------*/

	// enables the canvas for drawing
	public void enableCanvas() {
		this.gameFragment.enableCanvas();

	}

	// sets the gameover status to the appropriate state
	public void setGameOver(boolean state) {
		Log.d("SET GAME OVER", "State " + state);
		this.gameOver = state;
	}


}
