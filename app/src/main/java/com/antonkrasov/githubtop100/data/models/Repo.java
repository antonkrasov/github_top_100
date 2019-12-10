package com.antonkrasov.githubtop100.data.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

// Maybe not mix objects from the API and DB, but I think for this example it's fine
@Entity(tableName = "repos")
public class Repo {

    public static final int CONTRIBUTOR_STATUS_LOADING = 0;
    public static final int CONTRIBUTOR_STATUS_ERROR = 1;
    public static final int CONTRIBUTOR_STATUS_LOADED = 2;

    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    public long id;

    @ColumnInfo(name = "full_name")
    @SerializedName("full_name")
    public String fullName;

    @ColumnInfo(name = "stargazers_count")
    @SerializedName("stargazers_count")
    public int stargazersCount;

    @ColumnInfo(name = "contributor_status")
    public int contributorStatus = CONTRIBUTOR_STATUS_LOADING;

    @ColumnInfo(name = "top_contributor_login")
    public String topContributorLogin;
    @ColumnInfo(name = "top_contributor_contributions")
    public String topContributorContributions;
    @ColumnInfo(name = "top_contributor_url")
    public String topContributorUrl;

    @Override
    public String toString() {
        return "Repo{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", contributorStatus='" + contributorStatus + '\'' +
                '}';
    }
}
