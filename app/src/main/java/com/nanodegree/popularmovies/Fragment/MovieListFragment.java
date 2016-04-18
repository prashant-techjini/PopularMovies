package com.nanodegree.popularmovies.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nanodegree.popularmovies.Activity.MovieListActivity;
import com.nanodegree.popularmovies.Adapter.MovieListAdapter;
import com.nanodegree.popularmovies.Constant.Constants;
import com.nanodegree.popularmovies.DataManager.DatabaseHandler;
import com.nanodegree.popularmovies.Model.GetMoviesResponseModel;
import com.nanodegree.popularmovies.Model.MovieModel;
import com.nanodegree.popularmovies.R;
import com.nanodegree.popularmovies.Retrofit.ApiInterface;
import com.nanodegree.popularmovies.Retrofit.ApiProvider;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Prashant Nayak
 */
public class MovieListFragment extends Fragment {
    public RecyclerView mMovieListView;
    private GridLayoutManager mLayoutManager;
    private MovieListAdapter mAdapter;
    public ArrayList<MovieModel> mListMovies;
    private ProgressDialog mProgressDialog;
    private MovieListActivity mActivity;

    private static final String POPULARITY_DESC = "popular";
    private static final String VOTE_AVERAGE_DESC = "top_rated";
    private static final String FAVOURITES = "favourites";
    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MovieListActivity) getActivity();
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);

        mMovieListView = (RecyclerView) view.findViewById(R.id.recycler_movie_list);
        mLayoutManager = new GridLayoutManager(mContext, 2);
        mAdapter = new MovieListAdapter(mActivity, mContext, mListMovies);

        mMovieListView.setLayoutManager(mLayoutManager);
        mMovieListView.setAdapter(mAdapter);

        updateMovieList(POPULARITY_DESC);

        return view;
    }

    public void updateMovieList(String category) {

        if (category.equals(FAVOURITES)) {
            DatabaseHandler database = DatabaseHandler.getInstance(mContext);
            mListMovies = database.getFavourites();
            mAdapter.setData(mListMovies);

            if (mListMovies.size() > 0 && mActivity.isTablet) {
                mActivity.onMovieSelected(0);
            }

            return;
        }

        //(new FetchMovieListTask()).execute(category);

        showProgressDialog(mContext, mContext.getResources().getString(R.string.loading));

        ApiInterface service = ApiProvider.getApiService(mContext);
        Call<GetMoviesResponseModel> callMoviesList;
        if (category.equals(POPULARITY_DESC)) {
            callMoviesList = service.movie_list_sort_popular(Constants.API_KEY_VALUE);
        } else /*if(category.equals(VOTE_AVERAGE_DESC))*/ {
            callMoviesList = service.movie_list_sort_toprated(Constants.API_KEY_VALUE);
        }

        callMoviesList.enqueue(new Callback<GetMoviesResponseModel>() {
            @Override
            public void onResponse(Call<GetMoviesResponseModel> call, Response<GetMoviesResponseModel> response) {
                if (response.isSuccessful()) {
                    handleApiSuccessGetMovies(response.body());
                } else {
                    handleApiFailureGetMovies();
                }
            }

            @Override
            public void onFailure(Call<GetMoviesResponseModel> call, Throwable t) {
                handleApiFailureGetMovies();
            }
        });

    }

    private void handleApiSuccessGetMovies(GetMoviesResponseModel response) {
        removeProgressDialog();
        mListMovies = response.results;
        mAdapter.setData(mListMovies);

        if (mListMovies.size() > 0 && mActivity.isTablet) {
            mActivity.onMovieSelected(0);
        }
    }

    private void handleApiFailureGetMovies() {
        Toast.makeText(mContext, mContext.getResources().getString(R.string.api_error), Toast.LENGTH_SHORT).show();
        removeProgressDialog();
    }

    public void showProgressDialog(final Context context, String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
        }
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(msg);
        mProgressDialog.show();
    }

    public void removeProgressDialog() {
        if (null != mProgressDialog && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
}
