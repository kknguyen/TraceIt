package uwb.css490.TraceIt.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import uwb.css490.TraceIt.MainActivity;
import uwb.css490.TraceIt.R;

public class ScoreFragment extends Fragment {

	private static final int SCORE_NUM = 5;

	TextView[] scoreList;
	int[] scoreNumList;
	ImageView menuButton;
	ScoreFragment temp;
	Activity main;

	public ScoreFragment() {
		scoreList = new TextView[SCORE_NUM];
		scoreNumList = new int[SCORE_NUM];
		initScores();
	}
	
	public ScoreFragment(int[] scores) {
		scoreList = new TextView[SCORE_NUM];
		scoreNumList = scores;
	}

	private void initScores() {
		for (int i = 0; i < scoreNumList.length; i++) {
			scoreNumList[i] = 0;
		}

	}

	private void initScoreList(View rootView) {
		scoreList[0] = (TextView) rootView.findViewById(R.id.Score1);
		scoreList[1] = (TextView) rootView.findViewById(R.id.Score2);
		scoreList[2] = (TextView) rootView.findViewById(R.id.Score3);
		scoreList[3] = (TextView) rootView.findViewById(R.id.Score4);
		scoreList[4] = (TextView) rootView.findViewById(R.id.Score5);
		Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "FredokaOne-Regular.ttf");
		for (int i = 0; i <= 4; i++) {
			scoreList[i].setTypeface(font);
		}
		TextView txt = (TextView) rootView.findViewById(R.id.Title);
		txt.setTypeface(font);
		readScores();


	}

	private void readScores() {
		Log.d("ARRAY", scoreList.length + " : " + scoreNumList.length);
		for (int i = 0; i < scoreList.length; i++) {
			scoreList[i].setText("Score: " + scoreNumList[i]);
		}
		
	}
	
	public void updateScores(int newScore){
		
		// get the min score from high scores
		int lowestScore = this.scoreNumList[this.scoreNumList.length - 1];
		
		// update only if the minscore is less than the new value
		if ( lowestScore < newScore) {
			
			//Log.d("", this.scoreNumList[this.scoreNumList.length - 1] + "");
			
			// replace the lowest score with the new value 
			this.scoreNumList[this.scoreNumList.length - 1] = newScore;
			
			// move untill the correct spot is found
			for (int i = this.scoreNumList.length - 2; i >= 0; i--) {
				if (this.scoreNumList[i] < this.scoreNumList[i + 1]) {
					int temp = this.scoreNumList[i];
					this.scoreNumList[i] = this.scoreNumList[i + 1];
					this.scoreNumList[i + 1] = temp;
				}
			}
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		main = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.score_fragment, container,
				false);
		//rootView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		initScoreList(rootView);
		
		menuButton = (ImageView) rootView.findViewById(R.id.backButton);
		temp = this;

		menuButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((MainActivity) main).detachScoreFragment();
			}
		});

		return rootView;
	}

	public int[] getScores() {
		Log.d("ARRAY", scoreList.length + " : " + scoreNumList.length);
		return scoreNumList;
	}
}
