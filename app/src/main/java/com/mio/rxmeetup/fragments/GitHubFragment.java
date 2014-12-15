package com.mio.rxmeetup.fragments;

import android.app.ListFragment;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mio.rxmeetup.R;
import com.mio.rxmeetup.adapter.RepoContributorAdapter;
import com.mio.rxmeetup.models.Contributor;
import com.mio.rxmeetup.retrofit.GhService;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.android.observables.ViewObservable;
import rx.subscriptions.Subscriptions;

public class GitHubFragment extends ListFragment {

    public static final String TAG = GitHubFragment.class.getSimpleName();

    @InjectView(R.id.button_obs)
    Button button;
    @InjectView(R.id.owner)
    TextView owner;
    @InjectView(R.id.repo)
    TextView repo;

    private Subscription listSub = Subscriptions.empty();
    private RepoContributorAdapter repoContributorAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_layout, container, false);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initAdapter();

        //Use instead of broadcast receiver
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        AndroidObservable.fromBroadcast(getActivity(), filter)
                .subscribe(intent -> {/*handleConnectivityChange(intent)*/});

        if (owner.getText().length() > 0 && repo.getText().length() > 0) {
            //subscription to github rest service
            //type the owner and then the repo
            listSub = callGhRest(owner.getText().toString().trim(), repo.getText().toString().trim());
        }

        //click from a view
        ViewObservable.clicks(button).subscribe(view ->
                {
                    if (owner.getText().length() > 0 && repo.getText().length() > 0) {
                        //subscription to github rest service
                        //type the owner and then the repo
                        listSub = callGhRest(owner.getText().toString().trim(), repo.getText().toString().trim());
                    }
                }
        );

        //click on items in list
        ViewObservable.itemClicks(this.getListView()).subscribe(
                onItemClickEvent -> Toast.makeText(getActivity(),
                        "Type: " + ((Contributor) repoContributorAdapter.getItem((int) onItemClickEvent.id)).getType(), Toast.LENGTH_SHORT).show());

    }

    private Subscription callGhRest(String owner, String repo) {
        return //subscription to github rest service
                //type the owner and then the repo
                AndroidObservable.bindFragment(this, GhService.getContributors(owner, repo))
                        //return a list of observables
                        .subscribe(
                                //send results to adapter
                                repoContributorAdapter::updateContributors,
                                //catch an error
                                Throwable::printStackTrace,
                                //finish
                                () -> Log.d(TAG, "finished")
                        );
    }

    private void initAdapter() {
        repoContributorAdapter = new RepoContributorAdapter(getActivity());
        setListAdapter(repoContributorAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        listSub.unsubscribe();
    }
}
