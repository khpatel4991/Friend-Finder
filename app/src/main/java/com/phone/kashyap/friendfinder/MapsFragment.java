package com.phone.kashyap.friendfinder;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MapsFragment extends Fragment
{
	private static final String LOG_TAG = MapsFragment.class.getSimpleName();
	public static FragmentManager fragmentManager;

	private static View rootView;
	/**
	 * Note that this may be null if the Google Play services APK is not
	 * available.
	 */

	private static GPSTracker gps;
	private static GoogleMap mMap;
	private static Double latitude, longitude;


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

	/**
	 * This is where we can add markers or lines, add listeners or move the
	 * camera.
	 * <p/>
	 * This should only be called once and when we are sure that {@link #mMap}
	 * is not null.
	 */
	private static void setUpMap()
	{
		latitude = gps.getLatitude();
		longitude = gps.getLongitude();
		Log.d(LOG_TAG, "latitude = " + String.valueOf(latitude) + " Longitude = " + String.valueOf(longitude));
		// For showing a move to my location button
		mMap.setMyLocationEnabled(true);
		// For zooming automatically to the Dropped PIN Location
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12.0f));
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		fragmentManager = getFragmentManager();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		if (container == null) return null;
		rootView = inflater.inflate(R.layout.fragment_maps, container, false);
		gps = new GPSTracker(getActivity());
		setUpMapIfNeeded(); // For setting up the MapFragment
		return rootView;
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
	public void onDestroyView()
	{
		super.onDestroyView();
		if (mMap != null)
		{
			fragmentManager.beginTransaction().remove(fragmentManager.findFragmentById(R.id.map)).commit();
			mMap = null;
		}

	}
}