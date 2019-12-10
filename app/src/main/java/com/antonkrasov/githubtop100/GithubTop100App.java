package com.antonkrasov.githubtop100;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.antonkrasov.githubtop100.data.api.GithubApiService;
import com.antonkrasov.githubtop100.data.db.ReposDB;
import com.antonkrasov.githubtop100.data.repositories.ReposRepository;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

// For sure we need to use DI here...
public class GithubTop100App extends Application {

    private GithubApiService mGithubApiService;
    private ReposDB mReposDB;

    private static ReposRepository sReposRepository;

    @Override
    public void onCreate() {
        super.onCreate();

        initTimber();
        initGithubApiService();
        initDatabase();

        initReposRepository();
    }

    private void initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    private void initGithubApiService() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();

        // let's disable http logs in release builds
        if (!BuildConfig.DEBUG) {
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        } else {
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        }

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        mGithubApiService = retrofit.create(GithubApiService.class);
    }

    private void initDatabase() {
        mReposDB = ReposDB.getDatabase(this);
    }

    private void initReposRepository() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sReposRepository = new ReposRepository(mGithubApiService, mReposDB.reposDao(), sharedPreferences);
    }

    public static ReposRepository getReposRepository() {
        return sReposRepository;
    }

}
