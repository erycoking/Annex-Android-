package com.erycoking.annex;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.erycoking.annex.Models.Customer;
import com.erycoking.annex.Models.SentCustomer;
import com.erycoking.annex.Services.CustomerService;
import com.erycoking.annex.Services.HttpClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegistrationActivity extends AppCompatActivity {
    private static final String TAG = "RegistrationActivity";
    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    CustomerService service;
    AwesomeValidation mAwesomeValidation;

    private EditText et_first_name, et_other_names, et_address, et_national_id, et_mobile_no;
    private ImageView imageView;
    private Button uploadImage, saveClient, cancelBtn;
    private final int IMG_Request = 1;
    private SentCustomer customer;
    private String imagePath;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_form);
        init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG_Request && resultCode == RESULT_OK && data != null){
            uri = data.getData();
            Log.d(TAG, "onActivityResult: image data " + uri);
            imagePath = service.getRealPathFromUri(uri);
            try {
                imageView.setImageURI(uri);
                imageView.setVisibility(View.VISIBLE);
                Log.d(TAG, "onActivityResult: image visible");
                saveClient.setClickable(true);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void init() {
        Log.d(TAG, "init: initializing widgets");

        mAwesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        service = new CustomerService(RegistrationActivity.this);

        et_first_name = findViewById(R.id.first_name);
        et_other_names = findViewById(R.id.other_name);
        et_address = findViewById(R.id.address);
        et_national_id = findViewById(R.id.national_id);
        et_mobile_no = findViewById(R.id.mobileNo);
        uploadImage = findViewById(R.id.client_photo);
        imageView = findViewById(R.id.client_image);
        saveClient = findViewById(R.id.saveClient);
        cancelBtn= findViewById(R.id.cancel_btn);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
            }
        });

        addValidation();

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, IMG_Request);
            }
        });

        saveClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });
    }

    private void register() {
        Log.d(TAG, "register: validation and registration");
        if (mAwesomeValidation.validate()){
            if (imagePath == null || imagePath.isEmpty()){
                Toast.makeText(RegistrationActivity.this, "Photo is required", Toast.LENGTH_SHORT);
                Log.e(TAG, "register: imageFile " + imagePath);
                saveClient.setClickable(false);
            }else{
                Log.d(TAG, "register: imageFile " + imagePath);

                customer = new SentCustomer(
                        et_first_name.getText().toString(),
                        et_other_names.getText().toString(),
                        et_address.getText().toString(),
                        Integer.valueOf(et_national_id.getText().toString()),
                        Integer.valueOf(et_mobile_no.getText().toString())
                );

                Log.d(TAG, "register: customer details \n"+ customer);

                Retrofit retrofit = CustomerService.getRetrofit();
                HttpClient client = retrofit.create(HttpClient.class);

                Call<ResponseBody> call = client.addNewClient(getCustomerMap(customer), getPhoto());
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){

                            et_first_name.setText("");
                            et_other_names.setText("");
                            et_address.setText("");
                            et_national_id.setText("");
                            et_mobile_no.setText("");
                            imageView.setImageURI(null);

                            Toast.makeText(RegistrationActivity.this, CustomerService.userRegistrationSuccessMessage, Toast.LENGTH_SHORT);
                            Log.d(TAG, "onResponse: body -> " + response.body().toString());
                        }else{

                            Toast.makeText(RegistrationActivity.this, CustomerService.failMessage, Toast.LENGTH_SHORT);
                            Log.e(TAG, "onResponse: error ->"+ response.errorBody().toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(RegistrationActivity.this, CustomerService.failMessage, Toast.LENGTH_SHORT);
                        Log.e(TAG, "onResponse: "+ t.getMessage());
                    }
                });
            }

        }else {
            Toast.makeText(RegistrationActivity.this, "Error", Toast.LENGTH_SHORT);
        }

    }

    private RequestBody getCustomerMap(SentCustomer customer) {
        Map<String, Object> map = new HashMap<>();
        map.put("FirstName", customer.getFirstName());
        map.put("OtherNames", customer.getOtherNames());
        map.put("Address", customer.getAddress());
        map.put("NationalId", customer.getNationalId());
        map.put("MobileNo", customer.getMobileNo());
        Gson gson = new Gson();
        String json = gson.toJson(map);
        Log.d(TAG, "getCustomerMap: json -> "+ json);
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), json);
        return body;
    }


    private void addValidation() {
        Log.d(TAG, "validate: adding validation");

        mAwesomeValidation.addValidation(RegistrationActivity.this, R.id.first_name, "[a-zA-Z]{3,}", R.string.first_name_error);
        mAwesomeValidation.addValidation(RegistrationActivity.this, R.id.other_name, "[a-zA-Z\\s]+", R.string.first_name_error);
        mAwesomeValidation.addValidation(RegistrationActivity.this, R.id.address, ".+", R.string.address_error);
        mAwesomeValidation.addValidation(RegistrationActivity.this, R.id.national_id, "\\d{4,10}", R.string.national_id_error);
        mAwesomeValidation.addValidation(RegistrationActivity.this, R.id.mobileNo, "(2547|07)\\d{8}", R.string.mobile_error);
    }

    public MultipartBody.Part getPhoto(){

        //Create a file object using file path
        if (imagePath != null && !imagePath.isEmpty()) {
            Log.d(TAG, "getPhoto: imagePath -> " + imagePath);
            File file = new File(imagePath);
            if (file.exists()) {
                // Create a request body with file and image media type
                Log.d(TAG, "getPhoto: file -> " + file.getPath() );
                RequestBody fileReqBody = RequestBody.create(MediaType.parse(getContentResolver().getType(uri)), file);
                Log.d(TAG, "getPhoto: filereqbody -> " + fileReqBody.toString());

                // Create MultipartBody.Part using file request-body,file name and part name
                Log.d(TAG, "getPhoto: filename -> "+ file.getName());
                return MultipartBody.Part.createFormData("imageFile", file.getName(), fileReqBody);
//                 return fileReqBody;
            }else{
                Log.e(TAG, "getPhoto: file doen't exists");
            }
        }else {
            Log.e(TAG, "getPhoto: filepath is null/empty");
        }

        return null;
    }

    private void checkPermission(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(RegistrationActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegistrationActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(RegistrationActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            register();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    register();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    String permission_msg = "Permission required in order to register customer";
                    Toast.makeText(RegistrationActivity.this, permission_msg, Toast.LENGTH_LONG);
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


}
