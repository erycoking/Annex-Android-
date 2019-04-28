package com.erycoking.annex.Services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.erycoking.annex.Models.Customer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CustomerService {
    private static final String TAG = "CustomerService";
    public final static String baseUrl = "http://10.0.2.2:7000";
    public final static String failMessage = "Something went wrong try again later";
    public final static String userRegistrationSuccessMessage = "Client successfully added";
    public final static String userUpdateSuccessMessage = "Client successfully updated";

    final Context context;

    public CustomerService(Context context) {
        this.context = context;
    }

    public static Retrofit getRetrofit(){
        Log.d(TAG, "getRetrofit: creating retrofit");

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .readTimeout(3, TimeUnit.MINUTES)
                .addInterceptor(loggingInterceptor)
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request()
                                .newBuilder()
                                .header("accept", "application/json")
                                .build();
                        return chain.proceed(request);
                    }
                })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }

    public ArrayList<Customer> getCustomers(){
        Retrofit retrofit = getRetrofit();

        HttpClient client = retrofit.create(HttpClient.class);
        Call<ArrayList<Customer>> call = client.all();

        final ArrayList<Customer> allCustomers = new ArrayList<>();

        call.enqueue(new Callback<ArrayList<Customer>>() {
            @Override
            public void onResponse(Call<ArrayList<Customer>> call, retrofit2.Response<ArrayList<Customer>> response) {
                if (response.isSuccessful()){
                    allCustomers.addAll(response.body()) ;
                    Log.d(TAG, "onResponse: customer ::"+allCustomers);
                }else{
                    Toast.makeText(context, failMessage, Toast.LENGTH_SHORT);
                    Log.e(TAG, "onResponse: "+ response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Customer>> call, Throwable t) {
                Toast.makeText(context, failMessage, Toast.LENGTH_SHORT);
                Log.e(TAG, "onResponse: "+ t.getMessage());
            }
        });

        return allCustomers;
    }

    public Customer findCustomerBYId(final int id){
        Retrofit retrofit = getRetrofit();

        HttpClient client = retrofit.create(HttpClient.class);
        Call<Customer> call = client.getByCustomerId(id);

        final ArrayList<Customer> allCustomers = new ArrayList<>();

        call.enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, retrofit2.Response<Customer> response) {
                if (response.isSuccessful()){
                    allCustomers.add(response.body());
                }else{
                    Toast.makeText(context, failMessage, Toast.LENGTH_SHORT);
                    Log.e(TAG, "onResponse: "+ response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                Toast.makeText(context, failMessage, Toast.LENGTH_SHORT);
                Log.e(TAG, "onResponse: "+ t.getMessage());
            }
        });

        return allCustomers.get(0);
    }

    public Customer findCustomerByFirstName(final String fname){
        Retrofit retrofit = getRetrofit();

        HttpClient client = retrofit.create(HttpClient.class);
        Call<Customer> call = client.getByFirstName(fname);

        final ArrayList<Customer> allCustomers = new ArrayList<>();

        call.enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, retrofit2.Response<Customer> response) {
                if (response.isSuccessful()){
                    allCustomers.add(response.body());
                }else{
                    Toast.makeText(context, failMessage, Toast.LENGTH_SHORT);
                    Log.e(TAG, "onResponse: "+ response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                Toast.makeText(context, failMessage, Toast.LENGTH_SHORT);
                Log.e(TAG, "onResponse: "+ t.getMessage());
            }
        });

        return allCustomers.get(0);
    }

    public Customer findCustomerByNationalId(final int id){
        Retrofit retrofit = getRetrofit();

        HttpClient client = retrofit.create(HttpClient.class);
        Call<Customer> call = client.getByNationalId(id);

        final ArrayList<Customer> allCustomers = new ArrayList<>();

        call.enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, retrofit2.Response<Customer> response) {
                if (response.isSuccessful()){
                    allCustomers.add(response.body());
                }else{
                    Toast.makeText(context, failMessage, Toast.LENGTH_SHORT);
                    Log.e(TAG, "onResponse: "+ response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                Toast.makeText(context, failMessage, Toast.LENGTH_SHORT);
                Log.e(TAG, "onResponse: "+ t.getMessage());
            }
        });

        return allCustomers.get(0);
    }
}
