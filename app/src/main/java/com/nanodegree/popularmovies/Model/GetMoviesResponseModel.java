package com.nanodegree.popularmovies.Model;

import java.util.ArrayList;

public class GetMoviesResponseModel {
    public long page = 0;
    public long total_results = 0;
    public long total_pages = 0;
    public ArrayList<MovieModel> results = new ArrayList<MovieModel>();
}
