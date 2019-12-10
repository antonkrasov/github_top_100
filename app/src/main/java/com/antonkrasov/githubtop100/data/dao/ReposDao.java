package com.antonkrasov.githubtop100.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.antonkrasov.githubtop100.data.models.Repo;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public abstract class ReposDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(List<Repo> repos);

    @Query("DELETE FROM repos")
    public abstract void deleteAll();

    @Query("SELECT * from repos ORDER BY stargazers_count DESC")
    public abstract Flowable<List<Repo>> getRepos();

    @Query("SELECT * from repos WHERE contributor_status like :contributor_status ORDER BY stargazers_count DESC")
    public abstract Flowable<List<Repo>> getReposToLoad(int contributor_status);

    // I think it would be much better to have contributors as a separate table...
    @Query("UPDATE repos SET " +
            "top_contributor_login = :top_contributor_login, " +
            "top_contributor_contributions = :top_contributor_contributions, " +
            "top_contributor_url = :top_contributor_url, " +
            "contributor_status = 2 " +
            "WHERE id = :id")
    public abstract void updateTopContributor(long id, String top_contributor_login, int top_contributor_contributions, String top_contributor_url);

    @Transaction
    public void setRepos(List<Repo> repos) {
        deleteAll();
        insert(repos);
    }

}
