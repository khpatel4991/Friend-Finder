package com.phone.kashyap.friendfinder;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsFragment extends Fragment
{
	private static final String LOG_TAG = MapsFragment.class.getSimpleName();
	private static final String[] FRIENDS = new String[]{"mickey", "donald", "goofy", "garfield"};
	private static final double[] LATS = new double[]{40.517838, 40.513189, 40.495527, 40.497127};
	private static final double[] LONGS = new double[]{-74.465297, -74.433849, -74.467142, -74.417056};
	private static final int NO_OF_FRIENDS = 4;
	private static final String BASE_URL = "http://winlab.rutgers.edu/~huiqing/";
	public static FragmentManager fragmentManager;
	private static View rootView;
	/**
	 * Note that this may be null if the Google Play services APK is not
	 * available.
	 */

	private static GPSTracker gps;
	private static GoogleMap mMap;
	private static Double latitude, longitude;
	private MapView mMapView;

/*
	private static LocationDbHelper _locationDbHelper;
	private static SimpleCursorAdapter _adapter;
	private static SQLiteDatabase _db;
*/


	public MapsFragment() {}

	public static MapsFragment newInstance()
	{
		MapsFragment fragment = new MapsFragment();
		return fragment;
	}

	/**
	 * ** Sets up the map if it is possible to do so ****
	 */
	public static void setUpMapIfNeeded()
	{
		// Do a null check to confirm that we have not already instantiated the map.
		if (mMap == null)
		{
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((MapFragment) fragmentManager.findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) setUpMap();
		}
	}

	private static void setUpMap()
	{
		latitude = gps.getLatitude();
		longitude = gps.getLongitude();
		addMarkers();
		// For showing a move to my location button
		mMap.setMyLocationEnabled(true);
		// For zooming automatically to the Dropped PIN Location
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12.0f));
	}

	private static void addMarkers()
	{
		//My Checkins
		/*Cursor cursor = _locationDbHelper.getMarkersCursor(_db);

		cursor.moveToFirst();
		while (cursor.isAfterLast() == false)
		{
			double lati = cursor.getDouble(0);
			double longi = cursor.getDouble(1);
			String timestamp = cursor.getString(2);

			LatLng latLng = new LatLng(lati, longi);
			mMap.addMarker(new MarkerOptions().position(latLng).title(timestamp).snippet("Snippet"));
			cursor.moveToNext();
		}*/


		for (int i = 0; i < NO_OF_FRIENDS; i++)
		{
			LatLng latLng = new LatLng(LATS[i], LONGS[i]);
			mMap.addMarker(new MarkerOptions().position(latLng).title(FRIENDS[i]));
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		fragmentManager = getFragmentManager();
		/*_locationDbHelper = new LocationDbHelper(getActivity());
		_db = _locationDbHelper.getReadableDatabase();*/
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// inflate and return the layout
		View v = inflater.inflate(R.layout.fragment_maps, container, false);
		mMapView = (MapView) v.findViewById(R.id.map);
		mMapView.onCreate(savedInstanceState);

		mMapView.onResume();// needed to get the map to display immediately

		try
		{
			MapsInitializer.initialize(getActivity().getApplicationContext());
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		mMap = mMapView.getMap();
		gps = new GPSTracker(getActivity());
		setUpMapIfNeeded(); // For setting up the MapFragment
		// Perform any camera updates here

		mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
		{
			@Override
			public boolean onMarkerClick(Marker marker)
			{
				if (marker.getTitle().contentEquals(FRIENDS[1]) || marker.getTitle().contentEquals(FRIENDS[3]))
					new DownloadFriendIconTask(getActivity(), marker).execute(BASE_URL + marker.getTitle() + ".jpg");
				else
					new DownloadFriendIconTask(getActivity(), marker).execute(BASE_URL + marker.getTitle() + ".png");
				Toast.makeText(getActivity(), marker.getTitle(), Toast.LENGTH_SHORT).show();
				return false;
			}
		});

		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		if (mMap != null) setUpMap();
		if (mMap == null)
		{
			// Try to obtain the map from the MapFragment.
			mMap = ((MapFragment) fragmentManager.findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) setUpMap();
		}
	}

	/**
	 * * The map fragment's id must be removed from the FragmentManager
	 * *** or else if the same it is passed on the next time then
	 * *** app will crash ***
	 */

	@Override
	public void onResume()
	{
		super.onResume();
		mMapView.onResume();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		mMapView.onPause();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		mMapView.onDestroy();
		if (mMap != null)
			mMap = null;
	}

	@Override
	public void onLowMemory()
	{
		super.onLowMemory();
		mMapView.onLowMemory();
	}
}