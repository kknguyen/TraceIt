package uwb.css490.TraceIt.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import uwb.css490.TraceIt.MainActivity;
import uwb.css490.TraceIt.R;

public class GameOverFragment extends Fragment {
	
	private Activity main;
	private ImageButton restartButton;
	private ImageButton menuButton;
	private TextView score;
	
	
	// default constructor: UNUSED
	public GameOverFragment() {}
	

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
		View rootView = inflater.inflate(R.layout.gameover_fragment,
				container, false);

		// get the score text view
		this.score = (TextView) rootView.findViewById(R.id.scoreDisp);
		
		// set the text of the score 
		this.score.setText("Trace Score: " + ((MainActivity) this.main).getCurrentScore());
		
		// create and set the font of the score
		Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "FredokaOne-Regular.ttf");
		this.score.setTypeface(font);
		
		// get the restart button
		this.restartButton = (ImageButton) rootView.findViewById(R.id.restartButton);
		this.restartButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((MainActivity) main).restartGame();
			}
		});
		
		// get the menu button
		this.menuButton = (ImageButton) rootView.findViewById(R.id.quitButton);
		this.menuButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				 ((MainActivity) main).goToMainMenu();
			}
		});
		
		
		return rootView;
	}

}
