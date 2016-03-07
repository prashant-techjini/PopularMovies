package com.nanodegree.popularmovies.Activity;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nanodegree.popularmovies.Model.MovieModel;
import com.nanodegree.popularmovies.R;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    private static final String POSTER_URL = "http://image.tmdb.org/t/p/w185";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        String jsonString = getIntent().getStringExtra("MOVIE_DETAIL");

        Gson gson = new Gson();
        MovieModel movie = gson.fromJson(jsonString, MovieModel.class);

        ((TextView) findViewById(R.id.tv_title_value)).setText(movie.title);
        ((TextView) findViewById(R.id.tv_date_value)).setText(movie.releaseDate);
        ((TextView) findViewById(R.id.tv_rating_value)).setText(movie.voteAverage + "");
        ((TextView) findViewById(R.id.tv_overview_text)).setText(movie.overview);

        ImageView imagePoster = (ImageView) findViewById(R.id.iv_movie_poster);
        if (movie != null && !movie.imageUrl.equals("null")) {
            Picasso.with(this).load(POSTER_URL + movie.imageUrl).into(imagePoster);
        } else {
            imagePoster.setImageResource(R.mipmap.ic_launcher);
        }
    }
}
