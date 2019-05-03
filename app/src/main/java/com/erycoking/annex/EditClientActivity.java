package com.erycoking.annex;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.erycoking.annex.Models.SentCustomer;
import com.erycoking.annex.Services.CustomerService;
import com.erycoking.annex.Services.HttpClient;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EditClientActivity extends AppCompatActivity {
    private static final String TAG = "RegistrationActivity";
    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    CustomerService service;
    AwesomeValidation mAwesomeValidation;

    private EditText et_customer_id, et_first_name, et_other_names, et_address, et_national_id, et_mobile_no;
    private ImageView imageView;
    private Button uploadImage, saveClient, cancelButton;
    private final int IMG_Request = 1;
    private Bitmap bitmap;
    private String imagePath;
    private SentCustomer customer;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_client_details);

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
        service = new CustomerService(EditClientActivity.this);

        et_first_name = findViewById(R.id.first_name);
        et_other_names = findViewById(R.id.other_name);
        et_address = findViewById(R.id.address);
        et_national_id = findViewById(R.id.national_id);
        et_mobile_no = findViewById(R.id.mobileNo);
        et_customer_id = findViewById(R.id.customerId);
        uploadImage = findViewById(R.id.client_photo);
        imageView = findViewById(R.id.client_image);
        saveClient = findViewById(R.id.saveUpdate);
        cancelButton = findViewById(R.id.cancel_update);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditClientActivity.this, SearchForCustomer.class));
            }
        });


        addValidation();

        Customer customer = (Customer) getIntent().getSerializableExtra("customer");
        et_first_name.setText(customer.getFirstName());
        et_other_names.setText(customer.getOtherNames());
        et_address.setText(customer.getAddress());
        et_customer_id.setText(String.valueOf(customer.getCustomerId()));
        et_national_id.setText(String.valueOf(customer.getNationalId()));
        et_mobile_no.setText(String.valueOf(customer.getMobileNo()));


        String photoUrl = CustomerService.baseUrl + customer.getPhoto();
        Log.d(TAG, "init: photoUrl -> " + photoUrl);
        /*ImageLoader imageLoader = ImageLoader.getInstance();
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(getApplicationContext());
        imageLoader.init(configuration);
        imageLoader.loadImage(photoUrl, new SimpleImageLoadingListener(){
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                imageView.setImageBitmap(loadedImage);
                imageView.setImageURI(Uri.parse(imageUri));
                bitmap = loadedImage;
            }
        });*/

        et_customer_id.setEnabled(false);

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
            if (bitmap == null){
                Log.e(TAG, "register: bitmap is null" );
                Toast.makeText(EditClientActivity.this, "Photo is required", Toast.LENGTH_SHORT);
                saveClient.setClickable(false);
            }else{
                Log.d(TAG, "register: bitmap is not null" );

                customer = new SentCustomer(
                        Integer.valueOf(et_customer_id.getText().toString()),
                        et_first_name.getText().toString(),
                        et_other_names.getText().toString(),
                        et_address.getText().toString(),
                        Integer.valueOf(et_national_id.getText().toString()),
                        Integer.valueOf(et_mobile_no.getText().toString())
                );

                RequestBody customerRequestBody = RequestBody.create(MediaType.parse("application/json"), customer.toString());

                Log.d(TAG, "register: customer deatils \n"+ customer);

                Retrofit retrofit = CustomerService.getRetrofit();

                HttpClient client = retrofit.create(HttpClient.class);
                MultipartBody.Part imageFile = getPhoto();
                Call<ResponseBody> call = client.updateClient(customer.getCustomerId(), customerRequestBody, imageFile);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            Toast.makeText(EditClientActivity.this, CustomerService.userUpdateSuccessMessage, Toast.LENGTH_SHORT);
                            startActivity(new Intent(EditClientActivity.this, SearchForCustomer.class));
                            Log.d(TAG, "onResponse: body -> " + response.body());
                        }else{

                            Toast.makeText(EditClientActivity.this, CustomerService.failMessage, Toast.LENGTH_SHORT);
                            Log.e(TAG, "onResponse: "+ response.errorBody());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(EditClientActivity.this, CustomerService.failMessage, Toast.LENGTH_SHORT);
                        Log.e(TAG, "onResponse: "+ t.getMessage());
                    }
                });
            }

        }else {
            Toast.makeText(EditClientActivity.this, "Error", Toast.LENGTH_SHORT);
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

        mAwesomeValidation.addValidation(EditClientActivity.this, R.id.first_name, "[a-zA-Z]{3,}", R.string.first_name_error);
        mAwesomeValidation.addValidation(EditClientActivity.this, R.id.other_name, "[a-zA-Z\\s]+", R.string.first_name_error);
        mAwesomeValidation.addValidation(EditClientActivity.this, R.id.address, ".+", R.string.address_error);
        mAwesomeValidation.addValidation(EditClientActivity.this, R.id.national_id, "\\d{4,10}", R.string.national_id_error);
        mAwesomeValidation.addValidation(EditClientActivity.this, R.id.mobileNo, "(2547|07)\\d{8}", R.string.mobile_error);
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
        if (ContextCompat.checkSelfPermission(EditClientActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(EditClientActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(EditClientActivity.this,
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
                    Toast.makeText(EditClientActivity.this, permission_msg, Toast.LENGTH_LONG);
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}
