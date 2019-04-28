package com.erycoking.annex.Services;

import com.erycoking.annex.Models.Customer;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface HttpClient {

    @GET("/api/Customers")
    Call<ArrayList<Customer>> all();

    @GET("/api/Customers/{id}")
    Call<Customer> getByCustomerId(@Path(value = "id") int id);

    @GET("/api/Customers/{name}")
    Call<Customer> getByFirstName(@Path(value = "name") String name);

    @GET("/api/Customers/NationalId/{id}")
    Call<Customer> getByNationalId(@Path(value = "id") int id);

    @POST("/api/Customers")
    @FormUrlEncoded
    Call<Response> addNewClient(@Body Customer customer);

    @PUT("/api/Customers/{id}")
    @FormUrlEncoded
    Call<Response> updateClient(@Path(value = "id") int id, @Body Customer customer);

    @DELETE("/api/Customers/{id}")
    Call<Customer> deleteClient(@Path(value = "id") int id);

}
