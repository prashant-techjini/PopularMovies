package com.nanodegree.popularmovies.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nanodegree.popularmovies.Activity.MovieListActivity;
import com.nanodegree.popularmovies.Model.MovieModel;
import com.nanodegree.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private MovieListActivity mActivity;
    private Context mContext;
    private ArrayList<MovieModel> mMovieList;
    private static final String POSTER_URL = "http://image.tmdb.org/t/p/w185";
    public int mSelectedIndex = 0;

    public MovieListAdapter(Activity activity, Context context, ArrayList<MovieModel> movie_list) {
        if (activity instanceof MovieListActivity) {
            mActivity = (MovieListActivity) activity;
        }
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

        MovieModel movie = mMovieList.get(position);

        if (movie != null && movie.poster_path != null && !movie.poster_path.equals("null")) {
            Picasso.with(mContext).load(POSTER_URL + movie.poster_path).into(viewHolder.imageMovie);
        } else {
            //set to a default image if poster is not available
            viewHolder.imageMovie.setImageResource(R.mipmap.ic_launcher);
        }

        if (mActivity.isTablet && mSelectedIndex == position) {
            viewHolder.imageMovie.setBackground(ContextCompat.getDrawable(mContext, R.drawable.border_rectangle));
        } else {
            viewHolder.imageMovie.setBackgroundColor(Color.TRANSPARENT);
        }

        viewHolder.imageMovie.setTag(position);
        viewHolder.imageMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                mActivity.onMovieSelected(position);
                mSelectedIndex = position;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMovieList != null ? mMovieList.size() : 0;
        //return 10;
    }

    public class MovieItemViewHolder extends RecyclerView.ViewHolder {
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
