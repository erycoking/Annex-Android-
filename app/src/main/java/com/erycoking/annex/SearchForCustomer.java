package com.erycoking.annex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.erycoking.annex.Models.Customer;
import com.erycoking.annex.Services.CustomerService;
import com.erycoking.annex.Services.HttpClient;

import java.util.ArrayList;

public class SearchForCustomer extends AppCompatActivity {
    private static final String TAG = "SearchForCustomer";

    private Spinner criteria;
    private EditText searchKey;
    private Button searchBtn;
    private ViewPager viewPager;
    private String searchBy, searchValue;
    private ViewPagerAdapter adapter;

    AwesomeValidation mAwesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_for_customer);

        init();
    }

    private void init() {
        criteria = findViewById(R.id.criteria);
        searchKey = findViewById(R.id.search_key);
        searchBtn = findViewById(R.id.search_btn);
        viewPager = findViewById(R.id.client_view_pager);

        mAwesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        mAwesomeValidation.addValidation(SearchForCustomer.this, R.id.criteria, "[a-zA-Z0-9]+", R.string.criteria);
        mAwesomeValidation.addValidation(SearchForCustomer.this, R.id.search_key, "[a-zA-Z0-9]+", R.string.searchKey);

        ArrayAdapter<String> filters = new ArrayAdapter<>(
                SearchForCustomer.this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.filters)
        );
        filters.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        criteria.setAdapter(filters);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mAwesomeValidation.validate()){
                    Log.d(TAG, "onClick: selected item -> "+ criteria.getSelectedItem());
                    searchBy = criteria.getSelectedItem().toString();
                    searchValue = searchKey.getText().toString();

                    getClientByKey(searchBy, searchValue);
                }
            }
        });

        getAllCustomers();


    }

    private void getAllCustomers() {
        Retrofit retrofit = CustomerService.getRetrofit();

        HttpClient client = retrofit.create(HttpClient.class);
        Call<ArrayList<Customer>> call = client.all();

        call.enqueue(new Callback<ArrayList<Customer>>() {
            @Override
            public void onResponse(Call<ArrayList<Customer>> call, retrofit2.Response<ArrayList<Customer>> response) {
                if (response.isSuccessful()){
                    final ArrayList<Customer> customers = new ArrayList<>();
                    customers.addAll(response.body()) ;
                    Log.d(TAG, "onResponse: customer ::"+customers);

                    getIntent().putExtra("customers", customers);

                    adapter = new ViewPagerAdapter(getSupportFragmentManager());
                    ClientViewFragment fragment = new ClientViewFragment();
                    adapter.addFragment(fragment);
                    viewPager.setAdapter(adapter);

                }else{
                    Toast.makeText(SearchForCustomer.this, "failed", Toast.LENGTH_SHORT);
                    Log.e(TAG, "onResponse: "+ response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Customer>> call, Throwable t) {
                Toast.makeText(SearchForCustomer.this, "failed", Toast.LENGTH_SHORT);
                Log.e(TAG, "onResponse: "+ t.getMessage());
            }
        });
    }

    private void getClientByKey(String searchBy, String searchValue) {
//        R.string.customer_id
        switch (searchBy){
            case "Customer ID":
                getByCustomerId(searchValue);
                break;
            case "Name":
                getByName(searchValue);
                break;
            case "National ID":
                getByNationalId(searchValue);
                break;
        }

    }

    private void getByNationalId(String searchValue) {
        Retrofit retrofit = CustomerService.getRetrofit();

        HttpClient client = retrofit.create(HttpClient.class);
        Call<Customer> call = client.getByNationalId(Integer.valueOf(searchValue));

        call.enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                if (response.isSuccessful()){
                    final ArrayList<Customer> customers = new ArrayList<>();
                    customers.add(response.body());
                    Log.d(TAG, "onResponse: retrieved customer " + customers);
                    getIntent().putExtra("customers", customers);

                    adapter = new ViewPagerAdapter(getSupportFragmentManager());
                    ClientViewFragment fragment = new ClientViewFragment();
                    adapter.addFragment(fragment);
                    viewPager.setAdapter(adapter);

                }else {
                    Toast.makeText(SearchForCustomer.this, "Could not retrieve customer", Toast.LENGTH_SHORT);
                    Log.d(TAG, "onResponse: error retrieving customer -> \n" + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                Toast.makeText(SearchForCustomer.this, "Could not retrieve customer", Toast.LENGTH_SHORT);
                Log.d(TAG, "onResponse: error retrieving customer -> \n" + t.getMessage());
            }
        });
    }

    private void getByName(String searchValue) {
        Retrofit retrofit = CustomerService.getRetrofit();

        HttpClient client = retrofit.create(HttpClient.class);
        Call<Customer> call = client.getByName(searchValue);

        call.enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                if (response.isSuccessful()){
                    final ArrayList<Customer> customers = new ArrayList<>();
                    customers.add(response.body());
                    Log.d(TAG, "onResponse: retrieved customer " + customers);
                    getIntent().putExtra("customers", customers);

                    adapter = new ViewPagerAdapter(getSupportFragmentManager());
                    ClientViewFragment fragment = new ClientViewFragment();
                    adapter.addFragment(fragment);
                    viewPager.setAdapter(adapter);

                }else {
                    Toast.makeText(SearchForCustomer.this, "Could not retrieve customer", Toast.LENGTH_SHORT);
                    Log.d(TAG, "onResponse: error retrieving customer -> \n" + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                Toast.makeText(SearchForCustomer.this, "Could not retrieve customer", Toast.LENGTH_SHORT);
                Log.d(TAG, "onResponse: error retrieving customer -> \n" + t.getMessage());
            }
        });
    }

    private void getByCustomerId(String searchValue) {
        Retrofit retrofit = CustomerService.getRetrofit();

        HttpClient client = retrofit.create(HttpClient.class);
        Call<Customer> call = client.getByCustomerId(Integer.valueOf(searchValue));

        call.enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                if (response.isSuccessful()){
                    final ArrayList<Customer> customers = new ArrayList<>();
                    customers.add(response.body());
                    Log.d(TAG, "onResponse: retrieved customer " + customers);
                    getIntent().putExtra("customers", customers);

                    adapter = new ViewPagerAdapter(getSupportFragmentManager());
                    ClientViewFragment fragment = new ClientViewFragment();
                    adapter.addFragment(fragment);
                    viewPager.setAdapter(adapter);

                }else {
                    Toast.makeText(SearchForCustomer.this, "Could not retrieve customer", Toast.LENGTH_SHORT);
                    Log.d(TAG, "onResponse: error retrieving customer -> \n" + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                Toast.makeText(SearchForCustomer.this, "Could not retrieve customer", Toast.LENGTH_SHORT);
                Log.d(TAG, "onResponse: error retrieving customer -> \n" + t.getMessage());
            }
        });
    }

}
