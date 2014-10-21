package com.phone.kashyap.friendfinder;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.phone.kashyap.friendfinder.data.LocationContract;
import com.phone.kashyap.friendfinder.data.LocationDbHelper;

public class CheckinsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
	private static final String[] PROJECTION = new String[]{LocationContract.LocationEntry.COLUMN_ID, LocationContract.LocationEntry.COLUMN_LATITUDE, LocationContract.LocationEntry.COLUMN_LONGITUDE, LocationContract.LocationEntry.COLUMN_CHECKIN_TIME};
	private static LocationDbHelper locationDbHelper;
	private static FragmentManager fragmentManager;
	private AbsListView mListView;
	private SimpleCursorAdapter _adapter;
	private SQLiteDatabase _db;

	public CheckinsFragment() {}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		locationDbHelper = new LocationDbHelper(getActivity());
		_db = locationDbHelper.getReadableDatabase();
		fragmentManager = getFragmentManager();
		//TODO: Create progressbar till db loads
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_checkins_list, container, false);
		ListView listView = (ListView) rootView.findViewById(R.id.listview_checkins);

		_adapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item_checkin, locationDbHelper.getAllRowsCursor(_db),
				// the column names to use to fill the textviews
				PROJECTION,
				// the textviews to fill with the data pulled from the columns above
				new int[]{R.id.list_item_id_textview, R.id.list_item_latitude_textview, R.id.list_item_longitude_textview, R.id.list_item_date_textview}, 0);

		//_adapter.bindView(rootView, getActivity(), c);
		listView.setAdapter(_adapter);
		//getLoaderManager().initLoader(0, null, this);

		return rootView;
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		//fragmentManager.beginTransaction().remove(fragmentManager.findFragmentById(R.id.listview_checkins)).commit();
		if (_adapter != null && _db != null)
		{
			_adapter = null;
			_db = null;
		}
	}

	/**
	 * The default content for this Fragment has a TextView that is shown when
	 * the list is empty. If you would like to change the text, call this method
	 * to supply the text it should use.
	 */
	public void setEmptyText(CharSequence emptyText)
	{
		View emptyView = mListView.getEmptyView();

		if (emptyText instanceof TextView)
		{
			((TextView) emptyView).setText(emptyText);
		}
	}


	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle)
	{
		// Now create and return a CursorLoader that will take care of
		// creating a Cursor for the data being displayed.
		return new CursorLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
	{

	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursorLoader)
	{

	}

}
