package com.antonkrasov.githubtop100.data.api;

import com.antonkrasov.githubtop100.data.api.models.ReposResponse;
import com.antonkrasov.githubtop100.data.models.Contributor;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface GithubApiService {

    @GET("search/repositories?sort=stars&order=desc&q=stars%3A%3E100&per_page=100")
    Single<ReposResponse> repos();

    // Had to add it just because of request rate limit, this token doesn't have access to my private
    // repos, don't even try :)
    @Headers({
            "Authorization: token 1521029c84ba80ce848a8bd533fa5c68f0129161"
    })
    @GET("repos/{repo}/contributors?q=contributions&order=desc")
    Single<List<Contributor>> getContributors(@Path(value = "repo", encoded = true) String repo);

}
