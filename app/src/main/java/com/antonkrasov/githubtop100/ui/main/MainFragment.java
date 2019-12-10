package com.antonkrasov.githubtop100.ui.main;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.antonkrasov.githubtop100.ErrorUtils;
import com.antonkrasov.githubtop100.R;
import com.antonkrasov.githubtop100.data.DataResult;
import com.antonkrasov.githubtop100.data.models.Repo;

import java.util.List;

import timber.log.Timber;

public class MainFragment extends Fragment {

    private View mLoadingIndicatorView;
    private RecyclerView mListView;
    private TextView mErrorView;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        RecyclerView list = view.findViewById(R.id.list);
        ReposAdapter adapter = new ReposAdapter(getContext());

        list.setLayoutManager(new LinearLayoutManager(getContext()));
        list.setAdapter(adapter);

        viewModel.getRepos().observe(this, this::renderRepos);

        mLoadingIndicatorView = view.findViewById(R.id.loading_indicator);
        mErrorView = view.findViewById(R.id.error_text);
        mListView = list;
    }

    private void renderRepos(DataResult<List<Repo>> reposDataResult) {
        Timber.i("renderRepos: %s", reposDataResult);

        switch (reposDataResult.status) {
            case LOADING:
                mLoadingIndicatorView.setVisibility(View.VISIBLE);
                mErrorView.setVisibility(View.GONE);
                mListView.setVisibility(View.GONE);
                break;
            case ERROR:
                // some retry button would be great here...

                mLoadingIndicatorView.setVisibility(View.GONE);
                mErrorView.setVisibility(View.VISIBLE);
                mListView.setVisibility(View.GONE);

                mErrorView.setText(ErrorUtils.getHumanReadableText(reposDataResult.error));
                break;
            case SUCCESS:
                mErrorView.setVisibility(View.GONE);
                mLoadingIndicatorView.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);

                getReposAdapter().setData(reposDataResult.data);
                break;
        }
    }

    private ReposAdapter getReposAdapter() {
        return (ReposAdapter) mListView.getAdapter();
    }

}
