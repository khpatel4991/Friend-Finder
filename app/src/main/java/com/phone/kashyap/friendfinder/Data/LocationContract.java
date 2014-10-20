package com.phone.kashyap.friendfinder.data;

import android.provider.BaseColumns;

/**
 * Created by Kashyap on 10/19/2014.
 */
public class LocationContract
{
	private static final String LOG_TAG = LocationContract.class.getSimpleName();
	;

	public LocationContract() {}

	public static abstract class LocationEntry implements BaseColumns
	{
		public static final String TABLE_NAME = "locations_table";
		public static final String COLUMN_ID = "entryid";
		public static final String COLUMN_LATITUDE = "latitude";
		public static final String COLUMN_LONGITUDE = "longitude";
		public static final String COLUMN_CHECKIN_TIME = "checkin_time";
	}
}
