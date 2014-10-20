package com.phone.kashyap.friendfinder;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.phone.kashyap.friendfinder.data.LocationContract;
import com.phone.kashyap.friendfinder.data.LocationDbHelper;


public class MainActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks
{


	private static final String LOG_TAG = MainActivity.class.getSimpleName();
	/**
	 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
	}

	@Override
	public void onNavigationDrawerItemSelected(int position)
	{
		// update the main content by replacing fragments
		Log.d(LOG_TAG, "onNavigationDrawerItemSelected = " + String.valueOf(position));
		FragmentManager fragmentManager = getFragmentManager();

		switch (position)
		{
			case 0:
			{
				fragmentManager.beginTransaction().replace(R.id.container, PlaceholderFragment.newInstance(position + 1)).commit();
				break;
			}
			case 1:
			{
				fragmentManager.beginTransaction().replace(R.id.container, new CheckinsFragment()).commit();
				break;
			}
			case 2:
			{
				fragmentManager.beginTransaction().replace(R.id.container, new MapsFragment()).commit();
				break;
			}
		}
	}

	public void onSectionAttached(int number)
	{
		switch (number)
		{
			case 1:
				mTitle = getString(R.string.title_location);
				break;
			case 2:
				mTitle = getString(R.string.title_location_history);
				break;
			case 3:
				mTitle = getString(R.string.title_map);
				break;
		}
	}

	public void restoreActionBar()
	{
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		if (!mNavigationDrawerFragment.isDrawerOpen())
		{
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment
	{
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";
		GPSTracker gps;
		//LocationDbHelper _dbHelper = new LocationDbHelper(getActivity());

		public PlaceholderFragment() {}

		/**
		 * Returns a new instance of this fragment for the given section
		 * number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber)
		{
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			final LocationDbHelper locationDbHelper = new LocationDbHelper(getActivity());
			ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.address_progress);
			TextView textViewLocation = (TextView) rootView.findViewById(R.id.textLocation);
			TextView textViewAddress = (TextView) rootView.findViewById(R.id.textAddress);
			gps = new GPSTracker(getActivity(), textViewLocation, textViewAddress, progressBar);
			Button button_save = (Button) rootView.findViewById(R.id.button_save);
			final double latitude, longitude;
			if (gps.canGetLocation())
			{
				latitude = gps.getLatitude();
				longitude = gps.getLongitude();
				textViewLocation.setText("Latitude: " + latitude + "\nLongitude: " + longitude);
				new GetAddressFromLocationTask(getActivity(), textViewAddress, progressBar).execute(gps.location);


				button_save.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View view)
					{
						SQLiteDatabase db = locationDbHelper.getWritableDatabase();
						ContentValues values = new ContentValues();
						values.put(LocationContract.LocationEntry.COLUMN_LATITUDE, latitude);
						values.put(LocationContract.LocationEntry.COLUMN_LONGITUDE, longitude);
						long newRowId = db.insert(LocationContract.LocationEntry.TABLE_NAME, null, values);
						Toast.makeText(getActivity(), String.valueOf(newRowId), Toast.LENGTH_SHORT).show();
						db.close();
					}
				});
			} else gps.showSettingsAlert();

			return rootView;
		}

		@Override
		public void onDestroyView()
		{
			super.onDestroyView();
			gps.stopUsingGPS();
		}

		@Override
		public void onAttach(Activity activity)
		{
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
		}
	}
}
