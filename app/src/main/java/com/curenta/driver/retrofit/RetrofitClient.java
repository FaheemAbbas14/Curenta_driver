package com.curenta.driver.retrofit;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.curenta.driver.BuildConfig;
import com.curenta.driver.dto.AppElement;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {
    private static Retrofit ourInstance;
    private static int REQUEST_TIMEOUT = 60;
    private static OkHttpClient okHttpClient;

    public static Retrofit getInstance() {
        if (ourInstance == null)
            new RetrofitClient(BuildConfig.logindevURL);
        return ourInstance;
    }

    private RetrofitClient(String baseURL) {
        if (okHttpClient == null)
            initOkHttp();
        Log.d("Retrofitclient", "retrofit using url " + baseURL);
        ourInstance = new Retrofit.Builder()
                .baseUrl(baseURL)
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }

    public static IClientAPI getAPIClient() {
        getInstance();
        IClientAPI apiService = ourInstance.create(IClientAPI.class);
        return apiService;
    }

    private static void initOkHttp() {
        OkHttpClient.Builder httpClient = new OkHttpClient().newBuilder()
                .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        httpClient.addInterceptor(interceptor);

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        .addHeader("Accept", "application/json")
                        .addHeader("Content-Type", "application/json");

                // Adding Authorization token (API Key)
                // Requests will be denied without API key
//                if (!TextUtils.isEmpty(AppElement.token)) {
//                    requestBuilder.addHeader("Authorization", AppElement.token);
//                }

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        okHttpClient = httpClient.build();
    }

    public static void changeApiBaseUrl(String newApiBaseUrl) {
        ourInstance = null;
        new RetrofitClient(newApiBaseUrl);
    }
}