package com.phone.kashyap.friendfinder.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Kashyap on 10/19/2014.
 */
public class LocationDbHelper extends SQLiteOpenHelper
{
	private static final String LOG_TAG = LocationDbHelper.class.getSimpleName();
	private static final Integer DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "location.db";

	public LocationDbHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase)
	{
		//Create Table

		final String CREATE_LOCATION_TABLE_QUERY_STR = "CREATE TABLE " + LocationContract.LocationEntry.TABLE_NAME + " (" +
				LocationContract.LocationEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				LocationContract.LocationEntry.COLUMN_LATITUDE + " REAL NOT NULL, " +
				LocationContract.LocationEntry.COLUMN_LONGITUDE + " REAL NOT NULL, " +
				LocationContract.LocationEntry.COLUMN_CHECKIN_TIME + " DATETIME DEFAULT CURRENT_TIMESTAMP);";
		Log.d(LOG_TAG, CREATE_LOCATION_TABLE_QUERY_STR);
		sqLiteDatabase.execSQL(CREATE_LOCATION_TABLE_QUERY_STR);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2)
	{
		sqLiteDatabase.execSQL("DROP TABLE IF EXIST " + LocationContract.LocationEntry.TABLE_NAME);
		onCreate(sqLiteDatabase);
	}
}
