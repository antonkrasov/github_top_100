package com.antonkrasov.githubtop100.data.api.models;

import com.antonkrasov.githubtop100.data.models.Repo;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReposResponse {

    @SerializedName("items")
    public List<Repo> items;

}
