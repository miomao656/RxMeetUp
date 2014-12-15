package com.mio.rxmeetup.retrofit;

import com.mio.rxmeetup.models.Contributor;

import java.util.List;

import retrofit.RestAdapter;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class GhService {

    private static final String GIT_HUB_URL = "https://api.github.com/";

    private static final RestAdapter restAdapter = new RestAdapter.Builder()
            .setEndpoint(GIT_HUB_URL)
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .build();

    private static final IGitHub apiManager = restAdapter.create(IGitHub.class);

    public static Observable<List<Contributor>> getContributors(final String first, final String last) {
        return Observable.create((Subscriber<? super List<Contributor>> subscriber) -> {
            try {
                subscriber.onNext(apiManager.contributors(first, last));
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        }).subscribeOn(Schedulers.io());
    }

}
