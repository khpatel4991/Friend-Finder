package com.phone.kashyap.friendfinder;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Kashyap on 10/18/2014.
 */
public class GPSTracker extends Service implements LocationListener
{
	private static final String LOG_TAG = GPSTracker.class.getSimpleName();
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;    //Meters
	private static final long MIN_TIME_BW_UPDATES = 0;    //1 Minute
	private final Context _context;
	private final TextView _textViewLocation;
	private final TextView _textViewAddress;
	private final ProgressBar _progressBar;
	protected LocationManager locationManager;
	boolean isGPSEnabled = false;
	boolean isNetworkEnabled = false;
	boolean canGetLocation = false;
	Location location;
	double latitude;
	double longitude;

	public GPSTracker(Context context, TextView textViewLocation, TextView textViewAddress, ProgressBar progressBar)
	{
		_context = context;
		_textViewLocation = textViewLocation;
		_textViewAddress = textViewAddress;
		_progressBar = progressBar;
		getLocation();
	}

	private Location getLocation()
	{
		Log.i(LOG_TAG, "getLocation()");
		try
		{
			locationManager = (LocationManager) _context.getSystemService(LOCATION_SERVICE);

			isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

			isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled)
			{

			} else
			{
				this.canGetLocation = true;

				if (isNetworkEnabled)
				{

					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

					if (locationManager != null)
					{
						location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

						if (location != null)
						{

							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}

				}

				if (isGPSEnabled)
				{
					if (location == null)
					{
						locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

						if (locationManager != null)
						{
							location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

							if (location != null)
							{
								latitude = location.getLatitude();
								longitude = location.getLongitude();
							}
						}
					}
				}
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return location;
	}


	public void stopUsingGPS()
	{
		if (locationManager != null)
		{
			locationManager.removeUpdates(GPSTracker.this);
		}
	}

	public double getLatitude()
	{
		if (location != null)
		{
			latitude = location.getLatitude();
		}
		return latitude;
	}

	public double getLongitude()
	{
		if (location != null)
		{
			longitude = location.getLongitude();
		}

		return longitude;
	}

	public boolean canGetLocation()
	{
		return this.canGetLocation;
	}

	public void showSettingsAlert()
	{
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(_context);

		alertDialog.setTitle("GPS is settings");

		alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

		alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				_context.startActivity(intent);
			}
		});

		alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.cancel();
			}
		});

		alertDialog.show();
	}

	@Override
	public void onLocationChanged(Location location)
	{
		Log.i(LOG_TAG, "Location Changed to " + location.toString());
		new GetAddressFromLocationTask(_context, _textViewAddress, _progressBar).execute(location);
		_textViewLocation.setText("Latitude: " + location.getLatitude() + "\nLongitude: " + location.getLongitude());
	}

	@Override
	public void onProviderDisabled(String arg0)
	{
	}

	@Override
	public void onProviderEnabled(String arg0)
	{


	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2)
	{


	}

	@Override
	public IBinder onBind(Intent intent)
	{

		return null;
	}

}