package uwb.css490.TraceIt.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import uwb.css490.TraceIt.MainActivity;
import uwb.css490.TraceIt.R;

public class PauseFragment extends Fragment
{

	Activity main;
	ImageButton resumeButton;
	ImageButton menuButton;

	public PauseFragment()
	{
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		main = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.pause_fragment, container,
				false);

		//rootView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

		resumeButton = (ImageButton) rootView.findViewById(R.id.resumeButton);

		resumeButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				((MainActivity) main).detachPauseFragment();
			}
		});

		menuButton = (ImageButton) rootView.findViewById(R.id.menuButton);

		menuButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				((MainActivity) main).detachGameFragement();
				((MainActivity) main).attachMainFragment();
			}
		});

		return rootView;

	}
}
