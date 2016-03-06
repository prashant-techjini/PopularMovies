package com.nanodegree.popularmovies.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.nanodegree.popularmovies.Activity.MovieDetailActivity;
import com.nanodegree.popularmovies.Model.MovieModel;
import com.nanodegree.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<MovieModel> mMovieList;
    private static final String POSTER_URL = "http://image.tmdb.org/t/p/w185";

    public MovieListAdapter(Context context, ArrayList<MovieModel> movie_list) {
        mContext = context;
        mMovieList = movie_list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_movie, parent, false);
        RecyclerView.ViewHolder viewHolder = new MovieItemViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MovieItemViewHolder viewHolder = (MovieItemViewHolder) holder;

        Picasso.with(mContext).load(POSTER_URL + mMovieList.get(position).imageUrl).into(viewHolder.imageMovie);

        viewHolder.imageMovie.setTag(position);
        viewHolder.imageMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();

                Intent intent = new Intent(mContext, MovieDetailActivity.class);
                Gson gson = new Gson();
                String jsonString = gson.toJson(mMovieList.get(position));
                intent.putExtra("MOVIE_DETAIL", jsonString);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMovieList != null ? mMovieList.size() : 0;
        //return 10;
    }

    private class MovieItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageMovie;

        public MovieItemViewHolder(View itemView) {
            super(itemView);
            imageMovie = (ImageView) itemView.findViewById(R.id.iv_movie_poster);
        }
    }

    public void setData(ArrayList<MovieModel> listMovies) {
        mMovieList = listMovies;
        notifyDataSetChanged();
    }

}
