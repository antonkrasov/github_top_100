package com.antonkrasov.githubtop100.data.repositories;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

import com.antonkrasov.githubtop100.data.DataResult;
import com.antonkrasov.githubtop100.data.api.GithubApiService;
import com.antonkrasov.githubtop100.data.dao.ReposDao;
import com.antonkrasov.githubtop100.data.models.Contributor;
import com.antonkrasov.githubtop100.data.models.Repo;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import timber.log.Timber;

public class ReposRepository {

    private final static String KEY_LAST_UPDATE = "last_updated_at";

    private static final long CACHE_VALID_TIME_MS = 60 * 1000; // 1 min

    private final GithubApiService mGithubApiService;
    private final ReposDao mReposDao;
    private final SharedPreferences mSharedPreferences;

    private BehaviorSubject<DataResult<List<Repo>>> mReposSubject = BehaviorSubject.create();

    public ReposRepository(GithubApiService githubApiService, ReposDao reposDao, SharedPreferences sharedPreferences) {
        mGithubApiService = githubApiService;
        mReposDao = reposDao;
        mSharedPreferences = sharedPreferences;

        setupReposLoader();
    }

    //TODO: we should handle errors, here...
    private void setupReposLoader() {
        Disposable disposable = mReposDao.getReposToLoad(Repo.CONTRIBUTOR_STATUS_LOADING)
                .flatMapSingle(repos -> Flowable.fromIterable(repos)
//                        .delay(5, TimeUnit.SECONDS) // just for tests...
                                .flatMapSingle(repo -> {
                                    Timber.d("load: %s", repo.fullName);
                                    return mGithubApiService.getContributors(repo.fullName)
                                            .onErrorReturn(ex -> Collections.emptyList())
                                            .doOnSuccess(contributors -> {
                                                if (contributors.isEmpty()) {
                                                    mReposDao.updateTopContributorError(repo.id);
                                                } else {
                                                    // XXX: yeah, need to check if array is empty...
                                                    final Contributor topContributor = contributors.get(0);
                                                    mReposDao.updateTopContributor(repo.id, topContributor.login, topContributor.contributions, topContributor.htmlUrl);
                                                }
                                            })
                                            .map(contributors -> "");
                                })
                                .toList()
                )
                // need to think about the best number here, use number of available processors, etc...
                .subscribeOn(Schedulers.from(Executors.newFixedThreadPool(3)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> Timber.i("DONE!"),
                        Timber::e
                );
    }

    @SuppressLint("CheckResult")
    public Disposable repos(Consumer<DataResult<List<Repo>>> consumer) {
        boolean needToUpdate = isNeedToUpdate();

        if (needToUpdate) {
            loadReposFromTheNetwork();
        }

        // let's notify that we are loading data
        //noinspection unchecked
        mReposSubject.onNext(DataResult.loading());

        //noinspection ResultOfMethodCallIgnored
        mReposDao.getRepos()
                .map(DataResult::data)
                .onErrorReturn(DataResult::error)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(dataResult -> {
                    // let's ignore DB results, if they are empty...
                    // we need this to avoid resetting our loading or error states...
                    if (dataResult.status == DataResult.Status.SUCCESS && dataResult.data.isEmpty()) {
                        return;
                    }

                    mReposSubject.onNext(dataResult);
                });

        return mReposSubject
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
    }

    private void loadReposFromTheNetwork() {
        Disposable disposable = mGithubApiService.repos()
                .map(reposResponse -> reposResponse.items)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(repos -> {
                    saveLastUpdate();
                    mReposDao.setRepos(repos);
                }, ex -> {
                    // let's check if we already have some results from our local cache..
                    // if yes, no need to send an error
                    //
                    // This is valid for a case, when we have something in DB, but also ready to update
                    // data, but we don't have internet connection or it's unstable
                    final DataResult<List<Repo>> dataResult = mReposSubject.getValue();
                    if (dataResult != null && dataResult.status == DataResult.Status.SUCCESS && !dataResult.data.isEmpty()) {
                        return;
                    }

                    //noinspection unchecked
                    mReposSubject.onNext(DataResult.error(ex));
                });
    }

    private void saveLastUpdate() {
        mSharedPreferences.edit().putLong(KEY_LAST_UPDATE, System.currentTimeMillis()).apply();
    }

    /*
    We check when we updated last time, and return if we need to run another update right now
     */
    private boolean isNeedToUpdate() {
        long lastUpdateAt = mSharedPreferences.getLong(KEY_LAST_UPDATE, -1);
        if (lastUpdateAt == -1) {
            return true;
        }

        long currentTime = System.currentTimeMillis();
        return currentTime - lastUpdateAt > CACHE_VALID_TIME_MS;
    }

}
