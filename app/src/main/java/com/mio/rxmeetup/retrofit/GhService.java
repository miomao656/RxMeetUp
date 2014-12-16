package com.mio.rxmeetup.retrofit;

import com.mio.rxmeetup.models.Contributor;

import java.util.List;

import retrofit.RestAdapter;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class GhService {

    private static final String GIT_HUB_URL = "https://api.github.com/";

    /*Build rest adapter*/
    private static final RestAdapter restAdapter = new RestAdapter.Builder()
            .setEndpoint(GIT_HUB_URL)
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .build();

    /*Build api manager*/
    private static final IGitHub apiManager = restAdapter.create(IGitHub.class);

    /**
     * Function returns rxObservable and calls GitHub rest api for Contributors
     *
     * @param owner of repo
     * @param repo that provides data
     * @return rxObservable
     */
    public static Observable<List<Contributor>> getContributors(final String owner, final String repo) {
        return Observable.create((Subscriber<? super List<Contributor>> subscriber) -> {
            try {
                subscriber.onNext(apiManager.contributors(owner, repo));
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        }).subscribeOn(Schedulers.io());
    }

}
