package com.antonkrasov.githubtop100.data.models;

import com.google.gson.annotations.SerializedName;

public class Contributor {

    @SerializedName("login")
    public String login;

    @SerializedName("contributions")
    public int contributions;

    @SerializedName("html_url")
    public String htmlUrl;

}
