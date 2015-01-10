package uwb.css490.TraceIt;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class MainFragment extends Fragment {
	private Activity main;
	private ImageButton soundMode;
	private ImageButton scoreBoard;
	private ImageButton playButton;
	private boolean soundState;

	// constructor that takes the sound state as parameter
	public MainFragment(boolean soundState) {
		
		// initialize the sound state
		this.soundState = soundState;
	}

	
	/*--------------------------------------Overrides ----------------------------------*/
	
	@Override
	// called when the main fragment is attached
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.main = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.main_fragment, container,
				false);

		this.soundMode = (ImageButton) rootView.findViewById(R.id.soundMode);
		this.scoreBoard = (ImageButton) rootView.findViewById(R.id.leaderboard);
		this.playButton = (ImageButton) rootView.findViewById(R.id.playButton);

	
		// determins the initial icon for the sound on/off button
		if (this.soundState == true) {
			this.soundMode.setImageResource(R.drawable.sound_on_button);
			
			// if the soundState is true play the music 
			((MainActivity) main).playMusic();
		} else {
			this.soundMode.setImageResource(R.drawable.sound_off_button);
		}

		// when soundMode button is clicked 
		this.soundMode.setOnClickListener(new OnClickListener() {
			@Override
			
			// toggle between the on/off icons
			public void onClick(View v) {
				if (soundMode
						.getDrawable()
						.getConstantState()
						.equals(getResources().getDrawable(
								R.drawable.sound_on_button).getConstantState())) {
					soundMode.setImageResource(R.drawable.sound_off_button);
					soundState = false;
					((MainActivity) main).setSoundState(false);
					((MainActivity) main).pauseMusic();
					
				} else {
					soundMode.setImageResource(R.drawable.sound_on_button);
					soundState = true;
					((MainActivity) main).setSoundState(true);
					((MainActivity) main).playMusic();
					
				}
				
				// save the sound state
				((MainActivity) main).saveSoundState(soundState);
			}
		});

		
		// when the score button is clicked
		this.scoreBoard.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				// attach the score fragment 
				((MainActivity) main).attachScoreFragment();
			}
		});

		
		// when the play button is clicked
		this.playButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				// attach the game fragment 
				((MainActivity) main).attachGameFragement();
			}
		});

		return rootView;
	}
	
	
	


}
