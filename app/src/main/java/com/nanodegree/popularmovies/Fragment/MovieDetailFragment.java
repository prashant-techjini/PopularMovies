package com.nanodegree.popularmovies.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nanodegree.popularmovies.Constant.Constants;
import com.nanodegree.popularmovies.DataManager.DatabaseHandler;
import com.nanodegree.popularmovies.Model.GetReviewsResponseModel;
import com.nanodegree.popularmovies.Model.GetTrailorsResponseModel;
import com.nanodegree.popularmovies.Model.MovieModel;
import com.nanodegree.popularmovies.Model.ReviewModel;
import com.nanodegree.popularmovies.Model.TrailorModel;
import com.nanodegree.popularmovies.R;
import com.nanodegree.popularmovies.Retrofit.ApiInterface;
import com.nanodegree.popularmovies.Retrofit.ApiProvider;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Prashant Nayak
 */
public class MovieDetailFragment extends Fragment implements View.OnClickListener {
    private Activity mActivity;
    private Context mContext;
    private ProgressDialog mProgressDialog;

    private static final String POSTER_URL = "http://image.tmdb.org/t/p/w185";
    MovieModel mMovie;
    private LinearLayout mLayoutTrailors;
    private LinearLayout mLayoutReviews;
    private RelativeLayout mProgressTrailors;
    private RelativeLayout mProgressReviews;
    private Button mButtonFavourite;
    private DatabaseHandler database;

    private ArrayList<ReviewModel> mListReviews = new ArrayList<ReviewModel>();
    private ArrayList<TrailorModel> mListTrailors = new ArrayList<TrailorModel>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        database = DatabaseHandler.getInstance(mContext);

//        Bundle arguments = getArguments();
//        String jsonString = arguments.getString("MOVIE_DETAIL");

        String jsonString = mActivity.getIntent().getStringExtra("MOVIE_DETAIL");

        Gson gson = new Gson();
        mMovie = gson.fromJson(jsonString, MovieModel.class);

        if (mMovie != null) {
            ((TextView) view.findViewById(R.id.tv_title_value)).setText(mMovie.original_title);
            ((TextView) view.findViewById(R.id.tv_date_value)).setText(mMovie.release_date);
            ((TextView) view.findViewById(R.id.tv_rating_value)).setText(mMovie.vote_average + "");
            ((TextView) view.findViewById(R.id.tv_overview_text)).setText(mMovie.overview);
        } else {
            view.findViewById(R.id.rl_movie_detail).setVisibility(View.GONE);
        }

        mButtonFavourite = (Button) view.findViewById(R.id.btn_favourite);
        mButtonFavourite.setOnClickListener(this);

        mLayoutTrailors = (LinearLayout) view.findViewById(R.id.ll_trailor_list);
        mLayoutReviews = (LinearLayout) view.findViewById(R.id.ll_review_list);

        mProgressTrailors = (RelativeLayout) view.findViewById(R.id.rl_progress_tailor);
        mProgressReviews = (RelativeLayout) view.findViewById(R.id.rl_progress_review);

        ImageView imagePoster = (ImageView) view.findViewById(R.id.iv_movie_poster);
        if (mMovie != null && mMovie.poster_path != null && !mMovie.poster_path.equals("null")) {
            Picasso.with(mContext).load(POSTER_URL + mMovie.poster_path).into(imagePoster);
        } else {
            imagePoster.setImageResource(R.mipmap.ic_launcher);
        }

        if (mMovie == null) {
            return view;
        }

        //load trailors
        ApiInterface service = ApiProvider.getApiService(mContext);
        Call<GetTrailorsResponseModel> callTrailorList = service.trailor_list(mMovie.id, Constants.API_KEY_VALUE);

        callTrailorList.enqueue(new Callback<GetTrailorsResponseModel>() {
            @Override
            public void onResponse(Call<GetTrailorsResponseModel> call, Response<GetTrailorsResponseModel> response) {
                if (response.isSuccessful()) {
                    handleApiSuccessGetTrailors(response.body());
                } else {
                    handleApiFailureGetTrailors();
                }
            }

            @Override
            public void onFailure(Call<GetTrailorsResponseModel> call, Throwable t) {
                handleApiFailureGetTrailors();
            }
        });


        //load reviews
        Call<GetReviewsResponseModel> callReviewList = service.review_list(mMovie.id, Constants.API_KEY_VALUE);

        callReviewList.enqueue(new Callback<GetReviewsResponseModel>() {
            @Override
            public void onResponse(Call<GetReviewsResponseModel> call, Response<GetReviewsResponseModel> response) {
                if (response.isSuccessful()) {
                    handleApiSuccessGetReviews(response.body());
                } else {
                    handleApiFailureGetReviews();
                }
            }

            @Override
            public void onFailure(Call<GetReviewsResponseModel> call, Throwable t) {
                handleApiFailureGetReviews();
            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_favourite:
                if (database.isFavourite(mMovie.id)) {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.already_favourite), Toast.LENGTH_SHORT).show();
                    break;
                }

                if (database.addFavourite(mMovie.id, mMovie.poster_path, mMovie.original_title,
                        mMovie.popularity.toString(), mMovie.vote_average.toString(),
                        mMovie.release_date, mMovie.overview) > 0) {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.favourite_added), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.favourite_add_error), Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }


    private void handleApiSuccessGetTrailors(GetTrailorsResponseModel response) {
        mListTrailors = response.results;
        mProgressTrailors.setVisibility(View.GONE);

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 0; i < response.results.size(); i++) {
            View view = inflater.inflate(R.layout.list_item_trailor, mLayoutTrailors, false);
            ((TextView) view.findViewById(R.id.tv_trailor)).setText(
                    mContext.getResources().getString(R.string.trailor) + " " + (i + 1));

            RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.rl_trailor_item);
            layout.setTag(i);

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int index = (int) v.getTag();

                    String key = mListTrailors.get(index).key;
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + key)));
                }
            });

            mLayoutTrailors.addView(view);
        }
    }

    private void handleApiFailureGetTrailors() {

    }

    private void handleApiSuccessGetReviews(GetReviewsResponseModel response) {
        mListReviews = response.results;

        mProgressReviews.setVisibility(View.GONE);

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 0; i < response.results.size(); i++) {
            View view = inflater.inflate(R.layout.list_item_review, mLayoutReviews, false);
            ((TextView) view.findViewById(R.id.tv_author)).setText(
                    response.results.get(i).author);
            ((TextView) view.findViewById(R.id.tv_review)).setText(
                    response.results.get(i).content);

            mLayoutReviews.addView(view);
        }
    }

    private void handleApiFailureGetReviews() {

    }
}
