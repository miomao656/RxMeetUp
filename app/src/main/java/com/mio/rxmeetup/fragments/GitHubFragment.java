package com.mio.rxmeetup.fragments;

import android.app.ListFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mio.rxmeetup.MyActivity;
import com.mio.rxmeetup.R;
import com.mio.rxmeetup.adapter.RepoContributorAdapter;
import com.mio.rxmeetup.models.Contributor;
import com.mio.rxmeetup.retrofit.GhService;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.android.observables.ViewObservable;
import rx.functions.Action0;
import rx.functions.Action1;
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

        /*Check if text field is empty*/
        if (owner.getText().length() > 0 && repo.getText().length() > 0) {
            /*subscription to github rest service type the owner and then the repo*/
            listSub = callGhRest(owner.getText().toString().trim(), repo.getText().toString().trim());
        }

        /*click from a view*/
        ViewObservable.clicks(button).subscribe(view ->
                {
                    if (owner.getText().length() > 0 && repo.getText().length() > 0) {
                        /*subscription to github rest service
                        type the owner and then the repo*/
                        listSub = callGhRest(owner.getText().toString().trim(), repo.getText().toString().trim());
                    }
                }
        );

        /*click on items in list*/
        ViewObservable.itemClicks(this.getListView()).subscribe(onItemClickEvent ->
                        Toast.makeText(getActivity(), "Type: " +
                                ((Contributor) repoContributorAdapter
                                        .getItem((int) onItemClickEvent.id)).getType(), Toast.LENGTH_SHORT).show());

        /*Subscribed to Publish Subject from MyActivity for notifications about connection change
        Intentionally left in Java 7 syntax to demonstrate the difference*/
        MyActivity.isConnected.subscribe(
                new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        /*Handle connection change*/
                        Log.d(TAG, "Connected: " + aBoolean);
                    }
                },
                new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        /*Handle error if trowed*/
                        Log.e(TAG, "Error: " + throwable.toString());
                    }
                },
                new Action0() {
                    @Override
                    public void call() {
                        /*handle on complete*/
                        Log.d(TAG, "Completed");
                    }
                }
        );

    }

    /**
     * Function that returns Subscription and calls the Github api for data about Contributors
     *
     * @param owner of repo
     * @param repo that provides the data
     * @return Subscription
     */
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
