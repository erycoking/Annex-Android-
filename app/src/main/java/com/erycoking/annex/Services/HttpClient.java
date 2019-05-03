package com.erycoking.annex.Services;

import com.erycoking.annex.Models.Customer;
import com.erycoking.annex.Models.SentCustomer;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

public interface HttpClient {

    @GET("/api/Customers")
    Call<ArrayList<Customer>> all();

    @GET("/api/Customers/{id}")
    Call<Customer> getByCustomerId(@Path(value = "id") int id);

    @GET("/api/Customers/{name}")
    Call<Customer> getByName(@Path(value = "name") String name);

    @GET("/api/Customers/NationalId/{id}")
    Call<Customer> getByNationalId(@Path(value = "id") int id);

    @POST("/api/Customers")
    @Headers({"accept: application/json"})
    @Multipart
    Call<ResponseBody> addNewClient(@Part("customer") RequestBody customer, @Part MultipartBody.Part  imageFile);


    @PUT("/api/Customers/{id}")
    @Headers({"accept: application/json"})
    @Multipart
    Call<ResponseBody> updateClient(@Path(value = "id") int id, @Part("customer") RequestBody customer, @Part MultipartBody.Part  imageFile);

    @DELETE("/api/Customers/{id}")
    Call<Customer> deleteClient(@Path(value = "id") int id);

}
