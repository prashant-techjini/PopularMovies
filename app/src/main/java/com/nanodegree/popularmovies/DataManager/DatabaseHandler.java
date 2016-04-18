package com.nanodegree.popularmovies.DataManager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nanodegree.popularmovies.Model.MovieModel;

import java.util.ArrayList;

/**
 * @author Prashant Nayak
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    // database version
    private static final int DATABASE_VERSION = 1;
    // database name
    protected static final String DATABASE_NAME = "Database";

    private static DatabaseHandler mInstance;
    // Favourites Table
    public String favourites_table_Name = "favourites_table";
    public String favourites_column_id = "favourites_column_id";
    public String favourites_column_imageurl = "favourites_column_imageurl";
    public String favourites_column_title = "favourites_column_title";
    public String favourites_column_popularity = "favourites_column_popularity";
    public String favourites_column_voteAverage = "favourites_column_voteAverage";
    public String favourites_column_releaseDate = "favourites_column_releaseDate";
    public String favourites_column_overview = "favourites_column_overview";

    public static DatabaseHandler getInstance(Context ctx) {

        if (mInstance == null) {
            mInstance = new DatabaseHandler(ctx.getApplicationContext());
        }
        return mInstance;
    }

    private DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createFavouritesTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void createFavouritesTable(SQLiteDatabase db) {

        String favouritesSql = "";
        favouritesSql = "CREATE TABLE "
                + favourites_table_Name;
        favouritesSql += " ( ";
        favouritesSql += favourites_column_id + " INTEGER PRIMARY KEY, ";
        favouritesSql += favourites_column_imageurl + " TEXT , ";
        favouritesSql += favourites_column_title + " TEXT , ";
        favouritesSql += favourites_column_popularity + " TEXT , ";
        favouritesSql += favourites_column_voteAverage + " TEXT ,";
        favouritesSql += favourites_column_releaseDate + " TEXT ,";
        favouritesSql += favourites_column_overview + " TEXT ";
        favouritesSql += " ) ";
        db.execSQL(favouritesSql);
    }


    public long addFavourite(long id, String imageUrl, String title,
                             String popularity, String voteAverage, String releaseDate, String overview) {

        long createSuccessful = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(this.favourites_column_id, id);
        values.put(this.favourites_column_imageurl, imageUrl);
        values.put(this.favourites_column_title, title);
        values.put(this.favourites_column_popularity, popularity);
        values.put(this.favourites_column_voteAverage, voteAverage);
        values.put(this.favourites_column_releaseDate, releaseDate);
        values.put(this.favourites_column_overview, overview);

        createSuccessful = db.insert(favourites_table_Name, null,
                values);
        db.close();

        if (createSuccessful > 0) {
            //Log.d(TAG, "Favourites added. " + createSuccessful);
        }

        return createSuccessful;
    }


    public ArrayList<MovieModel> getFavourites() {
        ArrayList<MovieModel> listFavourites = new ArrayList<MovieModel>();

        String sql = "SELECT * FROM " + favourites_table_Name;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {

                MovieModel movie = new MovieModel();

                movie.id = cursor.getLong(cursor
                        .getColumnIndex(favourites_column_id));
                movie.poster_path = cursor.getString(cursor
                        .getColumnIndex(favourites_column_imageurl));
                movie.original_title = cursor.getString(cursor
                        .getColumnIndex(favourites_column_title));
                movie.popularity = Double.parseDouble(cursor.getString(cursor
                        .getColumnIndex(favourites_column_popularity)));
                movie.vote_average = Double.parseDouble(cursor.getString(cursor
                        .getColumnIndex(favourites_column_voteAverage)));
                movie.release_date = cursor.getString(cursor
                        .getColumnIndex(favourites_column_releaseDate));
                movie.overview = cursor.getString(cursor
                        .getColumnIndex(favourites_column_overview));

                listFavourites.add(movie);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return listFavourites;
    }


    public boolean isFavourite(long id) {
        ArrayList<MovieModel> listFavourites = new ArrayList<MovieModel>();

        String sql = "SELECT * FROM " + favourites_table_Name + " WHERE " + favourites_column_id + "=" + id;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(sql, null);

        return cursor.getCount() > 0;
    }


}
