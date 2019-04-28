package com.erycoking.annex;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.erycoking.annex.Services.CustomerService;
import com.erycoking.annex.Services.HttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegistrationActivity extends AppCompatActivity {
    private static final String TAG = "RegistrationActivity";
    CustomerService service;
    AwesomeValidation mAwesomeValidation;

    private EditText et_first_name, et_other_names, et_addess, et_national_id, et_mobile_no;
    private ImageView photo;
    private Button uploadImage, saveClient;
    private final int IMG_Request = 1;
    private Bitmap bitmap;
    private Customer customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_form);

        init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG_Request && requestCode == RESULT_OK && data != null){
            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                photo.setImageBitmap(bitmap);
                photo.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public String ImageToString(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void init() {
        Log.d(TAG, "init: initializing widgets");

        mAwesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        service = new CustomerService(RegistrationActivity.this);

        et_first_name = findViewById(R.id.first_name);
        et_other_names = findViewById(R.id.other_name);
        et_addess = findViewById(R.id.address);
        et_national_id = findViewById(R.id.national_id);
        et_mobile_no = findViewById(R.id.mobileNo);
        uploadImage = findViewById(R.id.client_photo);
        photo = findViewById(R.id.client_image);
        saveClient = findViewById(R.id.saveClient);

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
                register();
            }
        });
    }

    private void register() {
        validate();
        customer = new Customer(
                et_first_name.getText().toString(),
                et_other_names.getText().toString(),
                et_addess.getText().toString(),
                Integer.valueOf(et_national_id.getText().toString()),
                Integer.valueOf(et_mobile_no.getText().toString()),
                ImageToString(bitmap)
        );

        Retrofit retrofit = CustomerService.getRetrofit();

        HttpClient client = retrofit.create(HttpClient.class);
        Call<Response> call = client.addNewClient(customer);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, Response<Response> response) {
                if (response.isSuccessful()){
                    Toast.makeText(RegistrationActivity.this, CustomerService.userRegistrationSuccessMessage, Toast.LENGTH_SHORT);
                    Log.d(TAG, "onResponse: body -> " + response.body());
                }else{

                    Toast.makeText(RegistrationActivity.this, CustomerService.failMessage, Toast.LENGTH_SHORT);
                    Log.e(TAG, "onResponse: "+ response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Toast.makeText(RegistrationActivity.this, CustomerService.failMessage, Toast.LENGTH_SHORT);
                Log.e(TAG, "onResponse: "+ t.getMessage());
            }
        });

    }

    private void validate() {

        mAwesomeValidation.addValidation(RegistrationActivity.this, R.id.first_name, "[a-zA-Z]{3,}", R.string.first_name_error);
        mAwesomeValidation.addValidation(RegistrationActivity.this, R.id.other_name, "[a-zA-Z{3,}", R.string.first_name_error);
        mAwesomeValidation.addValidation(RegistrationActivity.this, R.id.address, "[a-zA-Z\\s]+", R.string.address_error);
        mAwesomeValidation.addValidation(RegistrationActivity.this, R.id.national_id, "\\d{4,10}", R.string.national_id_error);
        mAwesomeValidation.addValidation(RegistrationActivity.this, R.id.mobileNo, "(2547|07)\\d{8}", R.string.mobile_error);

        if (!photo.isDirty()){
            Toast.makeText(RegistrationActivity.this, "Photo is required", Toast.LENGTH_SHORT);
            saveClient.setEnabled(false);
        }
    }


}
