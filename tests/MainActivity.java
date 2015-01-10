package uwb.css390.FragmentCommunication;

import uwb.css390.FragmentCommunication.R;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	static public String skInitialCount = "InitCount"; 

	TextView mCountEcho;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			createAndInitFragments();	
		}
		mCountEcho = (TextView) findViewById(R.id.a_countEcho);
	}

	/**
	 * create and initialize the upper and lower fragments
	 */
	private void createAndInitFragments() {
		
		// define communication call back object
		// this is what we want to do if a Fragment calls us
		SimpleFragment.OnSimpleFragmentCallbackListener listener = 
			new SimpleFragment.OnSimpleFragmentCallbackListener() {
				@Override
				public void onSimpleFragmentCallback(int someData) {
					mCountEcho.setText("Count in Main=" + someData);
				}
		};		
		
		// define the upper fragment
		SimpleFragment upperFragment = new SimpleFragment();
			// parameter for the upper fragment, start counting at 3
		Bundle ub = new Bundle();
		ub.putInt(skInitialCount, 3);
		upperFragment.setArguments(ub);
			// we want to receive upper fragment's callback
		upperFragment.setSimpleFragmentCallbackListener(listener);
		
		// now define the lower fragment
		SimpleFragment lowerFragment = new SimpleFragment();
			// parameter for the lower fragment, start from -2
		Bundle lb = new Bundle();
		lb.putInt(skInitialCount, -2);
		lowerFragment.setArguments(lb);
		// notice lowerFragment does not have a call back object set
		
		// Let's start the two fragments
		FragmentManager fManager = getFragmentManager();		
		FragmentTransaction fTransaction = fManager.beginTransaction();

		// since mainContainer is a vertical linear layout, the first is upper the second will be lower
		fTransaction.add(R.id.mainContainer, upperFragment);
		fTransaction.add(R.id.mainContainer, lowerFragment);
		
		fTransaction.commit();	// commit the two add's
	}
}
