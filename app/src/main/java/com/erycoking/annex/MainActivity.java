package com.erycoking.annex;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.os.Bundle;
import android.util.Log;

import com.erycoking.annex.Models.Customer;
import com.erycoking.annex.Services.CustomerService;
import com.erycoking.annex.Services.HttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    CustomerService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        Log.d(TAG, "init: initializing widgets");
        service = new CustomerService(MainActivity.this);
        ArrayList<Customer> allCustomers = service.getCustomers();
//        Log.d(TAG, "init: all customers::" + allCustomers);
    }


}
