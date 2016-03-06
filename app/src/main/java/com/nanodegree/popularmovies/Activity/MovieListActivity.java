package com.nanodegree.popularmovies.Activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.nanodegree.popularmovies.Adapter.MovieListAdapter;
import com.nanodegree.popularmovies.Model.MovieModel;
import com.nanodegree.popularmovies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MovieListActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView mMovieListView;
    private GridLayoutManager mLayoutManager;
    private MovieListAdapter mAdapter;
    private ArrayList<MovieModel> mListMovies;
    private ProgressDialog mProgressDialog;

    private static final String MOVIE_LIST_URL = "http://api.themoviedb.org/3/discover/movie?";
    private static final String API_KEY_VALUE = "";
    private static final String API_KEY = "api_key";
    private static final String SORT_BY = "sort_by";
    private static final String POPULARITY_DESC = "popularity.desc";
    private static final String VOTE_AVERAGE_DESC = "vote_average.desc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        mMovieListView = (RecyclerView) findViewById(R.id.recycler_movie_list);
        mLayoutManager = new GridLayoutManager(this, 2);
        mAdapter = new MovieListAdapter(this, mListMovies);

        mMovieListView.setLayoutManager(mLayoutManager);
        mMovieListView.setAdapter(mAdapter);

        updateMovieList(POPULARITY_DESC);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            default:
                break;
        }
    }

    public class FetchMovieListTask extends AsyncTask<String, String, ArrayList<MovieModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog(MovieListActivity.this,
                    MovieListActivity.this.getResources().getString(R.string.loading));
        }

        @Override
        protected ArrayList<MovieModel> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader bufferedReader = null;
            String jsonResponse = null;

            Uri uri = Uri.parse(MOVIE_LIST_URL).buildUpon()
                    .appendQueryParameter(SORT_BY, params[0])
                    .appendQueryParameter(API_KEY, API_KEY_VALUE).build();

            try {
                URL url = new URL(uri.toString());
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream inputStream = connection.getInputStream();

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                StringBuilder sc = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    sc.append(line);
                }

                jsonResponse = sc.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (connection != null) {
                connection.disconnect();
            }

            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return parseJsonResponse(jsonResponse);
        }

        @Override
        protected void onPostExecute(ArrayList<MovieModel> listMovies) {
            super.onPostExecute(listMovies);
            removeProgressDialog();

            mAdapter.setData(listMovies);
        }
    }

    public void showProgressDialog(final Context context, String msg) {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(msg);
        mProgressDialog.show();
    }

    public void removeProgressDialog() {
        if (null != mProgressDialog && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    private ArrayList<MovieModel> parseJsonResponse(String jsonString) {
        JSONObject responseObject = null;
        ArrayList<MovieModel> listMovies = new ArrayList<MovieModel>();

        try {
            responseObject = new JSONObject(jsonString);

            JSONArray movieList = responseObject.optJSONArray("results");

            for (int i = 0; i < movieList.length(); i++) {
                JSONObject movieItem = movieList.optJSONObject(i);
                MovieModel movieModel = new MovieModel();

                movieModel.id = movieItem.getInt("id");
                movieModel.imageUrl = movieItem.getString("poster_path");
                movieModel.title = movieItem.getString("original_title");
                movieModel.popularity = movieItem.getDouble("popularity");
                movieModel.voteAverage = movieItem.getDouble("vote_average");
                movieModel.releaseDate = movieItem.getString("release_date");
                movieModel.overview = movieItem.getString("overview");

                listMovies.add(movieModel);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return listMovies;
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
                updateMovieList(POPULARITY_DESC);
                break;

            case R.id.action_sort_rating:
                updateMovieList(VOTE_AVERAGE_DESC);
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateMovieList(String sort_by) {
        (new FetchMovieListTask()).execute(sort_by);
    }
}
