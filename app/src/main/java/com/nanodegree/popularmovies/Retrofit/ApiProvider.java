package com.nanodegree.popularmovies.Retrofit;

import android.content.Context;

import com.nanodegree.popularmovies.Constant.Constants;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Prashant Nayak
 */
public class ApiProvider {
    private static ApiInterface service = null;
    private static Retrofit retrofit;

    private ApiProvider() {
    }

    public static ApiInterface getApiService(final Context context) {
        if (service == null) {
//            Interceptor interceptor = new Interceptor() {
//                @Override
//                public okhttp3.Response intercept(Chain chain) throws IOException {
////                    Request newRequest = chain.request();
////                    HttpUrl httpUrl = newRequest.newBuilder().url();
////                    return chain.proceed(newRequest);
//                }
//            };
//
//            OkHttpClient.Builder builder = new OkHttpClient.Builder();
//            builder.interceptors().add(interceptor);
//            OkHttpClient client = builder.build();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();

            HttpLoggingInterceptor interceptorLog = new HttpLoggingInterceptor();
            interceptorLog.setLevel(HttpLoggingInterceptor.Level.BODY);
            //OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptorLog).build();

            builder.interceptors().add(interceptorLog);
            OkHttpClient client = builder.build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();

            service = retrofit.create(ApiInterface.class);
        }
        return service;
    }

    public static Retrofit getRetrofit() {
        return retrofit;
    }
}
