package com.mio.rxmeetup.utils;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.util.Log;

public class FragmentHelper {

    /**
     * Replace current Fragment with new one.
     *
     * @param newFragmentLayoutId
     * @param newFragment
     */
    public static void prepareAndShowFragment(Activity fragmentActivity, int newFragmentLayoutId,
                                              Fragment newFragment, boolean addToBackstack, String tag) {
        // Add the fragment to the activity, pushing this transaction on to the back stack.
        Log.e("FRAGMENT BACKSTACK", "Before count: " + fragmentActivity.getFragmentManager().getBackStackEntryCount() + " TAG: " + tag);
        FragmentTransaction ft = fragmentActivity.getFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN & FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        if (addToBackstack) {
            ft.addToBackStack(null);
        }
        ft.replace(newFragmentLayoutId, newFragment, tag);
        ft.commit();
        Log.e("FRAGMENT BACKSTACK", "After count: " + fragmentActivity.getFragmentManager().getBackStackEntryCount() + " TAG: " + tag);
    }
}
