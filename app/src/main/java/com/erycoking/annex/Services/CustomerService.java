package com.erycoking.annex.Services;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.erycoking.annex.Models.Customer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import androidx.loader.content.CursorLoader;
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

    final Context mContext;

    public CustomerService(Context context) {
        this.mContext = context;
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

    /*public ArrayList<Customer> sendCustomers(){
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
*/
    public String getRealPathFromUri(final Uri uri) {
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(mContext, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(mContext, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(mContext, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(mContext, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private String getDataColumn(Context context, Uri uri, String selection,
                                 String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

}
