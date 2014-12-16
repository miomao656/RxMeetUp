package com.mio.rxmeetup;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.mio.rxmeetup.fragments.GitHubFragment;
import com.mio.rxmeetup.utils.FragmentHelper;

import rx.android.observables.AndroidObservable;
import rx.subjects.PublishSubject;


public class MyActivity extends Activity {

    /*Subject to notify is connection lost*/
    public static PublishSubject<Boolean> isConnected = PublishSubject.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        FragmentHelper.prepareAndShowFragment(this, R.id.frame_container, new GitHubFragment(),
                false, GitHubFragment.TAG);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*Create intent filter for connectivity action*/
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
         /*Use instead of broadcast receiver*/
        AndroidObservable.fromBroadcast(this, filter)
                .subscribe(intent -> {
                /*Handle connectivity change(intent)*/
                    final ConnectivityManager connectivityManager = (ConnectivityManager)
                            this.getSystemService(Context.CONNECTIVITY_SERVICE);
                    final NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

                    if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
                        /*Broadcast event to notify if connected*/
                        isConnected.onNext(true);
                    } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                        /*Broadcast event to notify if disconnected*/
                        isConnected.onNext(false);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }
}
