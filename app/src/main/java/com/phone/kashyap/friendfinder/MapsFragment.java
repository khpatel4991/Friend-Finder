package com.phone.kashyap.friendfinder;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.concurrent.ExecutionException;

public class MapsFragment extends Fragment
{
	private static final String LOG_TAG = MapsFragment.class.getSimpleName();
	private static final String[] FRIENDS = new String[]{"mickey", "donald", "goofy", "garfield"};
	private static final double[] LATS = new double[]{40.517838, 40.513189, 40.495527, 40.497127};
	private static final double[] LONGS = new double[]{-74.465297, -74.433849, -74.467142, -74.417056};
	private static final int NO_OF_FRIENDS = 4;
	private static final String BASE_URL = "http://winlab.rutgers.edu/~huiqing/";
	public static FragmentManager fragmentManager;
	private static double[] _distance = new double[4];
	private static LayoutInflater _layoutInflater;
	private static Resources _res;
	private static View rootView;
	private static GPSTracker gps;
	private static GoogleMap mMap;
	private static Double latitude, longitude;
	private MapView mMapView;


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
		mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter()
		{
			@Override
			public View getInfoWindow(Marker marker)
			{
				return null;
			}

			@Override
			public View getInfoContents(Marker marker)
			{
				View v = _layoutInflater.inflate(R.layout.custom_map_marker, null);
				TextView title = (TextView) v.findViewById(R.id.marker_title);
				TextView snippet = (TextView) v.findViewById(R.id.marker_snippet);

				ImageView img = (ImageView) v.findViewById(R.id.marker_icon);
				//img.setImageBitmap(BitmapFactory.decodeResource(_res, R.drawable.ic_launcher));

				// Setting the latitude
				title.setText(marker.getTitle());

				// Setting the longitude
				snippet.setText(marker.getSnippet());

				ShowPopupTask k = new ShowPopupTask();
				if (marker.getTitle().contentEquals(FRIENDS[1]) || marker.getTitle().contentEquals(FRIENDS[3]))
				{
					k.execute(BASE_URL + marker.getTitle() + ".jpg");
					try
					{
						img.setImageBitmap(k.get());
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					} catch (ExecutionException e)
					{
						e.printStackTrace();
					}
				} else
				{
					k.execute(BASE_URL + marker.getTitle() + ".png");
					try
					{
						img.setImageBitmap(k.get());
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					} catch (ExecutionException e)
					{
						e.printStackTrace();
					}
				}

				// Returning the view containing InfoWindow contents
				return v;
			}
		});

		addMarkers();
		// For showing a move to my location button
		mMap.setMyLocationEnabled(true);
		// For zooming automatically to the Dropped PIN Location
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12.0f));
	}

	private static void addMarkers()
	{
		for (int i = 0; i < NO_OF_FRIENDS; i++)
		{
			_distance[i] = distance(latitude, longitude, LATS[i], LONGS[i], 'M');
			LatLng latLng = new LatLng(LATS[i], LONGS[i]);
			mMap.addMarker(new MarkerOptions().position(latLng).title(FRIENDS[i]).snippet(String.format("%.3f", _distance[i])));
		}
	}

	private static double distance(double lat1, double lon1, double lat2, double lon2, char unit)
	{
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		if (unit == 'K')
		{
			dist = dist * 1.609344;
		} else if (unit == 'N')
		{
			dist = dist * 0.8684;
		}
		return (dist);
	}

	private static double deg2rad(double deg)
	{
		return (deg * Math.PI / 180.0);
	}

	private static double rad2deg(double rad)
	{
		return (rad * 180.0 / Math.PI);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		fragmentManager = getFragmentManager();
		_layoutInflater = getActivity().getLayoutInflater();
		_res = getResources();
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
				Log.i(LOG_TAG, "onClick Marker with Title " + marker.getTitle());
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
		Log.i(LOG_TAG, "Map Service Closed");
		mMapView.onDestroy();
		if (mMap != null) mMap = null;
	}

	@Override
	public void onLowMemory()
	{
		super.onLowMemory();
		mMapView.onLowMemory();
	}
}