/*
This file is part of ZAX.

	ZAX is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	ZAX is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with ZAX.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.inovex.zabbixmobile.activities.fragments;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.inovex.zabbixmobile.R;
import com.inovex.zabbixmobile.adapters.BaseSeverityPagerAdapter;
import com.inovex.zabbixmobile.listeners.OnListItemSelectedListener;
import com.inovex.zabbixmobile.model.Sharable;
import com.inovex.zabbixmobile.model.TriggerSeverity;

/**
 * Base class for a details fragment of a data type to be filtered by severity.
 * 
 * @param <T>
 *            the data type
 */
public abstract class BaseSeverityFilterDetailsFragment<T extends Sharable>
		extends BaseServiceConnectedFragment {

	public static final String TAG = BaseSeverityFilterDetailsFragment.class
			.getSimpleName();

	private static final String ARG_SEVERITY = "arg_severity";
	private static final String ARG_SPINNER_VISIBLE = "arg_spinner_visible";

	protected ViewPager mDetailsPager;
	protected TriggerSeverity mSeverity = TriggerSeverity.ALL;
	private OnListItemSelectedListener mCallbackMain;
	protected BaseSeverityPagerAdapter<T> mDetailsPagerAdapter;

	private boolean mLoadingSpinnerVisible;

	protected ShareActionProvider mShareActionProvider;

	protected MenuItem mMenuItemShare;
	private int mCurrentPosition;

	/**
	 * Selects an item which shall be displayed in the view pager.
	 * 
	 * @param position
	 *            list position
	 */
	public void selectItem(int position) {
		mCurrentPosition = position;
		Log.d(TAG, "selectItem(" + position + ")");
		if (mDetailsPagerAdapter == null
				|| mDetailsPagerAdapter.getCount() == 0) {
			updateMenu();
			return;
		}
		if (position > mDetailsPagerAdapter.getCount() - 1)
			position = 0;
		setPosition(position);
//		updateMenu();
	}

	/**
	 * Updates the currently selected page using the position saved in the pager
	 * adapter.
	 */
	public void refreshItemSelection() {
		if (mDetailsPagerAdapter == null)
			return;
		setPosition(mDetailsPagerAdapter.getCurrentPosition());
		updateMenu();
	}

	/**
	 * This sets the current position and updates the pager, pager adapter and
	 * page indicator.
	 *
	 * @param position
	 */
	private void setPosition(int position) {
		mCurrentPosition = position;
		mDetailsPager.setCurrentItem(position);
//		mDetailsPagerAdapter.setCurrentPosition(position);
//		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
//			mCallbackMain.onListItemSelected(position,0);
//		}
	}

	/**
	 * Sets the current severity and updates the pager adapter.
	 * 
	 * @param severity
	 *            current severity
	 */
	public void setSeverity(TriggerSeverity severity) {
		// exchange adapter if it's necessary
		this.mSeverity = severity;
		if (mZabbixDataService != null) {
			retrievePagerAdapter();
			// the adapter could be fresh -> set fragment manager
			mDetailsPagerAdapter.setFragmentManager(getChildFragmentManager());
			mDetailsPager.setAdapter(mDetailsPagerAdapter);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		Log.d(TAG, "onCreate");
		if (savedInstanceState != null) {
			mSeverity = TriggerSeverity.getSeverityByNumber(savedInstanceState
					.getInt(ARG_SEVERITY, TriggerSeverity.ALL.getNumber()));
			mLoadingSpinnerVisible = savedInstanceState.getBoolean(
					ARG_SPINNER_VISIBLE, false);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_severity_details,
				container, false);
		Log.d(TAG, "onCreateView");
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if (mLoadingSpinnerVisible)
			showLoadingSpinner();
		Log.d(TAG, "onViewCreated");
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		super.onServiceConnected(name, service);
		setupDetailsViewPager();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(ARG_SEVERITY, mSeverity.getNumber());
		outState.putBoolean(ARG_SPINNER_VISIBLE, mLoadingSpinnerVisible);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d(TAG, "onAttach");

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallbackMain = (OnListItemSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnListItemSelectedListener.");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		// This is a fix for the issue with the child fragment manager;
		// described here:
		// http://stackoverflow.com/questions/15207305/getting-the-error-java-lang-illegalstateexception-activity-has-been-destroyed
		// and here: https://code.google.com/p/android/issues/detail?id=42601
		// If the fragment manager is not set to null, there will be issues when
		// the activity is destroyed and there are pending transactions
		if (mDetailsPagerAdapter != null)
			mDetailsPagerAdapter.setFragmentManager(null);
	}

	/**
	 * 
	 */
	protected abstract void retrievePagerAdapter();

	/**
	 * Performs the setup of the view pager used to swipe between details pages.
	 */
	protected void setupDetailsViewPager() {
		Log.d(TAG, "setupViewPager");

		retrievePagerAdapter();
		mDetailsPagerAdapter.setFragmentManager(getChildFragmentManager());

		// initialize the view pager
		mDetailsPager = (ViewPager) getView().findViewById(
				R.id.severity_view_pager);
		mDetailsPager.setAdapter(mDetailsPagerAdapter);
		mDetailsPager.setCurrentItem(mCurrentPosition);

		Log.d(TAG,
				"current position: "
						+ mDetailsPagerAdapter.getCurrentPosition());
		// mDetailsPagerAdapter.setCurrentPosition(mPosition);
		// mDetailsPageIndicator.setCurrentItem(mPosition);
		mDetailsPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				mCallbackMain.onListItemSelected(position,mDetailsPagerAdapter.getItemId(position));
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
	}

	/**
	 * Shows a loading spinner instead of this page's list view.
	 */
	public void showLoadingSpinner() {
		mLoadingSpinnerVisible = true;
		if (getView() != null) {
			LinearLayout progressLayout = (LinearLayout) getView()
					.findViewById(R.id.severity_details_progress_layout);
			if (progressLayout != null)
				progressLayout.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Dismisses the loading spinner view.
	 * 
	 * If the view has not yet been created, the status is saved and when the
	 * view is created, the spinner will not be shown at all.
	 */
	public void dismissLoadingSpinner() {
		mLoadingSpinnerVisible = false;
		if (getView() != null) {
			LinearLayout progressLayout = (LinearLayout) getView()
					.findViewById(R.id.severity_details_progress_layout);
			if (progressLayout != null) {
				progressLayout.setVisibility(View.GONE);
			}
		}

	}

	/**
	 * Refreshes the data of the item currently displayed in the view pager.
	 */
	public abstract void refreshCurrentItem();

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.severity_details_fragment, menu);

		mMenuItemShare = menu.findItem(R.id.menuitem_share);
		mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(mMenuItemShare);
		updateShareIntent();
	}

	protected void setShareIntent(String text) {
		if (mShareActionProvider == null)
			return;
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_TEXT, text);
		shareIntent.setType("text/plain");
		mShareActionProvider.setShareIntent(shareIntent);
	}

	public void updateShareIntent() {
		if (mDetailsPagerAdapter == null)
			return;
		T currentObject = mDetailsPagerAdapter.getCurrentObject();
		if (currentObject != null)
			setShareIntent(currentObject.getSharableString(this.getActivity()));
	}

	protected void updateMenu() {
		if (mMenuItemShare == null)
			return;
		if (mDetailsPagerAdapter == null
				|| mDetailsPagerAdapter.getCount() == 0) {
			mMenuItemShare.setVisible(false);
			return;
		}
		T e = mDetailsPagerAdapter.getCurrentObject();
		if (e != null)
			mMenuItemShare.setVisible(true);
		else
			mMenuItemShare.setVisible(false);
		updateShareIntent();
	}

	@Override
	public void setHasOptionsMenu(boolean hasMenu) {
		super.setHasOptionsMenu(hasMenu);
		if (hasMenu == false)
			return;
		updateMenu();
	}
}
