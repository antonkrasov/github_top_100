package com.antonkrasov.githubtop100.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.antonkrasov.githubtop100.R;
import com.antonkrasov.githubtop100.data.models.Repo;

import java.util.ArrayList;
import java.util.List;

public class ReposAdapter extends RecyclerView.Adapter<ReposAdapter.ViewHolder> {

    private List<Repo> mItems = new ArrayList<>();
    private LayoutInflater mLayoutInflater;

    ReposAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView repoNameView;
        TextView starsCountView;
        TextView topContributorView;
        View topContributorProgress;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            repoNameView = itemView.findViewById(R.id.repo_name);
            starsCountView = itemView.findViewById(R.id.stars_text);
            topContributorView = itemView.findViewById(R.id.top_contributor);
            topContributorProgress = itemView.findViewById(R.id.top_contributor_progress);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.adapter_item_repo, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Repo repo = mItems.get(position);

        holder.repoNameView.setText(repo.fullName);
        holder.starsCountView.setText(String.valueOf(repo.stargazersCount));

        if (repo.contributorStatus == Repo.CONTRIBUTOR_STATUS_LOADED) {
            holder.topContributorProgress.setVisibility(View.GONE);
            holder.topContributorView.setVisibility(View.VISIBLE);

            // we can use Truss or something like this here...
            //noinspection StringBufferReplaceableByString
            StringBuilder builder = new StringBuilder();
            builder.append(repo.topContributorLogin);
            builder.append(": ");
            builder.append(repo.topContributorContributions);
            builder.append(" contributions");
            builder.append('\n');
            builder.append(repo.topContributorUrl);

            holder.topContributorView.setText(builder.toString());
        } else if (repo.contributorStatus == Repo.CONTRIBUTOR_STATUS_LOADING) {
            holder.topContributorProgress.setVisibility(View.VISIBLE);
            holder.topContributorView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    /*
    Maybe we should use default DiffUtils from Pagination lib, but just to save some time and make it more simple, we
    have this primitive comparing algorithm... Also the problem is it's done in the main thread,
    not the best idea, but don't have enough time to improve it...
     */
    public void setData(@NonNull List<Repo> items) {
        if (items.size() == mItems.size()) {
            // let's just iterate items and update items we need...
            for (int i = 0; i < mItems.size(); i++) {
                if (mItems.get(i).contributorStatus != items.get(i).contributorStatus) {
                    mItems.set(i, items.get(i));

                    notifyItemChanged(i);
                }
            }
        } else {
            mItems = new ArrayList<>(items);
            notifyDataSetChanged();
        }
    }

}
