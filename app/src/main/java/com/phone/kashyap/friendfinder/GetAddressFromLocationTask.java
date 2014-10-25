package com.phone.kashyap.friendfinder;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Kashyap on 10/19/2014.
 */
public class GetAddressFromLocationTask extends AsyncTask<Location, Void, String>
{
	private static final String LOG_TAG = GetAddressFromLocationTask.class.getSimpleName();
	private final Context _context;
	private final TextView _textview;
	private final ProgressBar _progressBar;

	public GetAddressFromLocationTask(Context context, TextView textView, ProgressBar progressBar)
	{
		super();
		_context = context;
		_textview = textView;
		_progressBar = progressBar;
	}

	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		_progressBar.setVisibility(View.VISIBLE);
	}

	@Override
	protected String doInBackground(Location... locations)
	{
		Geocoder geocoder = new Geocoder(_context, Locale.getDefault());
		// Get the current location from the input parameter list
		Location loc = locations[0];
		// Create a list to contain the result address
		List<Address> addresses = null;
		try
		{
				/*
				 * Return 1 address.
                 */
			addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
			Log.i(LOG_TAG, "Address Retrieved Successfully.");
		} catch (IOException e1)
		{
			Log.e(LOG_TAG, "IO Exception in getFromLocation()");
			e1.printStackTrace();
			return ("You are in a middle of nowhere or Google doesn't have your Address!");
		} catch (IllegalArgumentException e2)
		{
			// Error message to post in the log
			String errorString = "Illegal arguments " +
					Double.toString(loc.getLatitude()) +
					" , " +
					Double.toString(loc.getLongitude()) +
					" passed to address service";
			Log.e(LOG_TAG, errorString);
			e2.printStackTrace();
			return errorString;
		}
		// If the reverse geocode returned an address
		if (addresses != null && addresses.size() > 0)
		{
			// Get the first address
			Address address = addresses.get(0);
				/*
				 * Format the first line of address (if available),
                 * city, and country name.
                 */
			String addressText = String.format("%s, %s, %s",
					// If there's a street address, add it
					address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
					// Locality is usually a city
					address.getLocality(),
					// The country of the address
					address.getCountryName());
			// Return the text
			return addressText;
		} else return "No Address Found";
	}

	@Override
	protected void onPostExecute(String address)
	{
		super.onPostExecute(address);
		_progressBar.setVisibility(View.GONE);
		_textview.setText(address);
	}
}
