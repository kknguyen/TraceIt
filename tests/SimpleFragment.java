package uwb.css390.FragmentCommunication;

import uwb.css390.FragmentCommunication.R;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SimpleFragment extends Fragment {

	Button mCountBtn;
	TextView mEcho;
	
	int mCount = 0;
	
	// call back listener
	OnSimpleFragmentCallbackListener mCallbackObject = null;
	
	public SimpleFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.simple_fragment_layout, container, false);
					// simple_fragment_layout: is the name of the layout (name of the xml file)!
		
		mCountBtn = (Button) rootView.findViewById(R.id.f_countButton);
		mEcho = (TextView) rootView.findViewById(R.id.f_CountEcho);
		
		mCountBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mCount++;
				mEcho.setText("Fragment Count=" + mCount);
				
				// see if anyone cares about what we have done ...
				if (null != mCallbackObject) { 
					mCallbackObject.onSimpleFragmentCallback(mCount);
				} else {
					Log.i("MyDebug", "Count:" + mCount);
					Toast.makeText(getActivity(), "count not updated", 1000).show();
				}
			}
			
		});
		
		Bundle b = getArguments();
		if (null != b) {
			if (b.containsKey(MainActivity.skInitialCount)) 
				mCount = b.getInt(MainActivity.skInitialCount);	
		}
		
		mEcho.setText("Fragment Count=" + mCount);
		
		return rootView;
	}
	
	// Region: support for communication
	
	// define an interface
	public interface OnSimpleFragmentCallbackListener {
		public void onSimpleFragmentCallback(int someData);
	}
	
	// allow setting of the listener
	public void setSimpleFragmentCallbackListener(OnSimpleFragmentCallbackListener listener) {
		mCallbackObject = listener;
	}
	// EndRegion
	
}
