package com.nanodegree.popularmovies.Model;

import java.util.ArrayList;

public class GetReviewsResponseModel {
    public long id = 0;
    public long page = 0;
    public long total_results = 0;
    public long total_pages = 0;
    public ArrayList<ReviewModel> results = new ArrayList<ReviewModel>();
}
