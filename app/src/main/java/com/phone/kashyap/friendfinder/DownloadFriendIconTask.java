package com.phone.kashyap.friendfinder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Kashyap on 10/20/2014.
 */
public class DownloadFriendIconTask extends AsyncTask<String, Void, Bitmap>
{
	private static final String LOG_TAG = DownloadFriendIconTask.class.getSimpleName();

	Context _context;
	Marker _marker;

	public DownloadFriendIconTask(Context context, Marker marker)
	{
		_context = context;
		_marker = marker;
	}

	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		_marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.loader));
	}

	@Override
	protected Bitmap doInBackground(String... strings)
	{
		try
		{
			Log.i(LOG_TAG, strings[0]);
			URL url = new URL(strings[0]);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPostExecute(Bitmap bitmap)
	{
		super.onPostExecute(bitmap);
		_marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
	}
}
