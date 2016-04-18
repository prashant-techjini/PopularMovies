package com.nanodegree.popularmovies.Activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.gson.Gson;
import com.nanodegree.popularmovies.Adapter.MovieListAdapter;
import com.nanodegree.popularmovies.Fragment.MovieDetailFragment;
import com.nanodegree.popularmovies.Fragment.MovieListFragment;
import com.nanodegree.popularmovies.R;

public class MovieListActivity extends AppCompatActivity {
    private ProgressDialog mProgressDialog;
    private MovieListFragment mMovieListFragment;

    private static final String POPULARITY_DESC = "popular";
    private static final String VOTE_AVERAGE_DESC = "top_rated";
    private static final String FAVOURITES = "favourites";
    public boolean isTablet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        mMovieListFragment = (MovieListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_movie_list);

        FrameLayout layoutMovieDetail = (FrameLayout) findViewById(R.id.fl_container_movie_detail);
        if (layoutMovieDetail != null) {
            isTablet = true;

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fl_container_movie_detail, new MovieDetailFragment());
            fragmentTransaction.commitAllowingStateLoss();
        } else {
            isTablet = false;
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_sort_popular:
                mMovieListFragment.updateMovieList(POPULARITY_DESC);
                break;

            case R.id.action_sort_rating:
                mMovieListFragment.updateMovieList(VOTE_AVERAGE_DESC);
                break;

            case R.id.action_show_favourites:
                mMovieListFragment.updateMovieList(FAVOURITES);
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onMovieSelected(int position) {

        if (isTablet) {
            MovieListAdapter adapter = (MovieListAdapter) mMovieListFragment.mMovieListView.getAdapter();
            int lastSelectedIndex = adapter.mSelectedIndex;
            MovieListAdapter.MovieItemViewHolder viewHolderLastSelected = (MovieListAdapter.MovieItemViewHolder) mMovieListFragment.mMovieListView.
                    findViewHolderForLayoutPosition(lastSelectedIndex);

            if (viewHolderLastSelected != null) {
                viewHolderLastSelected.imageMovie.setBackgroundColor(Color.TRANSPARENT);
            }

            MovieListAdapter.MovieItemViewHolder viewHolder = (MovieListAdapter.MovieItemViewHolder) mMovieListFragment.mMovieListView.findViewHolderForLayoutPosition(position);
            if (viewHolder != null) {
                viewHolder.imageMovie.setBackground(ContextCompat.getDrawable(this, R.drawable.border_rectangle));
            }

            adapter.mSelectedIndex = position;

            MovieDetailFragment movieDetailFragment = new MovieDetailFragment();

            Gson gson = new Gson();
            String jsonString = gson.toJson(mMovieListFragment.mListMovies.get(position));

            getIntent().putExtra("MOVIE_DETAIL", jsonString);

//            Bundle arguments = new Bundle();
//            arguments.putString("MOVIE_DETAIL", jsonString);
//            movieDetailFragment.setArguments(arguments);

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fl_container_movie_detail, movieDetailFragment);
            fragmentTransaction.commitAllowingStateLoss();
        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class);
            Gson gson = new Gson();
            String jsonString = gson.toJson(mMovieListFragment.mListMovies.get(position));
            intent.putExtra("MOVIE_DETAIL", jsonString);
            startActivity(intent);
        }
    }

}
