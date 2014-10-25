package com.phone.kashyap.friendfinder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Kashyap on 10/20/2014.
 */
public class ShowPopupTask extends AsyncTask<String, Void, Bitmap>
{
	private static final String LOG_TAG = ShowPopupTask.class.getSimpleName();

	public ShowPopupTask() {}

	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
	}

	@Override
	protected Bitmap doInBackground(String... strings)
	{
		try
		{
			Log.i(LOG_TAG, "Getting Image from URL: " + strings[0]);
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
		Log.i(LOG_TAG, "onPostExecute");
	}
}
