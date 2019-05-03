package com.erycoking.annex;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.erycoking.annex.Models.Customer;

import java.util.ArrayList;

public class ClientViewFragment extends Fragment {
    private static final String TAG = "ClientViewFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_client_view, container, false);

        ArrayList<Customer> customers = (ArrayList<Customer>) getActivity().getIntent().getSerializableExtra("customers");
        Log.d(TAG, "onCreateView: received customers -> " + customers);

        RecyclerView recyclerView = view.findViewById(R.id.client_recycler_view);
        ClientRecyclerViewAdapter adapter = new ClientRecyclerViewAdapter(getContext(), customers);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

}
