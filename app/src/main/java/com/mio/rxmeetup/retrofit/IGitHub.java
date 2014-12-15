package com.mio.rxmeetup.retrofit;


import com.mio.rxmeetup.models.Contributor;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

public interface IGitHub {

    @GET("/repos/{owner}/{repo}/contributors")
    List<Contributor> contributors(@Path("owner") String owner, @Path("repo") String repo);

    @GET("/repos/{owner}/{repo}/contributors")
    Observable<List<Contributor>> contributorsO(@Path("owner") String owner, @Path("repo") String repo);

}
