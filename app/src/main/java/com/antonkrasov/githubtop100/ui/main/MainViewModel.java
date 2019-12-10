package com.antonkrasov.githubtop100.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.antonkrasov.githubtop100.GithubTop100App;
import com.antonkrasov.githubtop100.data.DataResult;
import com.antonkrasov.githubtop100.data.models.Repo;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class MainViewModel extends ViewModel {

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private MutableLiveData<DataResult<List<Repo>>> mReposLiveData = new MutableLiveData<>();

    public MainViewModel() {
        Disposable disposable = GithubTop100App.getReposRepository()
                .repos(dataResult -> mReposLiveData.setValue(dataResult));
        mCompositeDisposable.add(disposable);
    }

    @Override
    protected void onCleared() {
        mCompositeDisposable.clear();
        super.onCleared();
    }

    LiveData<DataResult<List<Repo>>> getRepos() {
        return mReposLiveData;
    }

}
