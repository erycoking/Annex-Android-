package com.erycoking.annex;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.erycoking.annex.Models.Customer;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ClientRecyclerViewAdapter extends RecyclerView.Adapter<ClientRecyclerViewAdapter.ClientViewHolder>{

    private static final String TAG = "ClientRecyclerViewAdapt";

    Context mContext;
    ArrayList<Customer> customers = new ArrayList<>();

    public ClientRecyclerViewAdapter(Context mContext, ArrayList<Customer> customers) {
        this.mContext = mContext;
        this.customers.clear();
        this.customers.addAll(customers);
    }

    @NonNull
    @Override
    public ClientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.client_list_layout, parent, false);
        ClientViewHolder viewHolder = new ClientViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ClientViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: initializing the view holder");
        holder.name.setText(customers.get(position).getFullName());
        holder.customer_id.setText(String.valueOf(customers.get(position).getCustomerId()));
        holder.mobile.setText(String.valueOf(customers.get(position).getMobileNo()));
        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Customer customer =  customers.get(position);
                Intent intent = new Intent(mContext, EditClientActivity.class);
                intent.putExtra("customer", customer);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    public class ClientViewHolder extends RecyclerView.ViewHolder{

        TextView name, customer_id, mobile;
        LinearLayout parentLayout;
        Button editBtn;

        public ClientViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            customer_id = itemView.findViewById(R.id.customer);
            mobile = itemView.findViewById(R.id.mobile);
            editBtn = itemView.findViewById(R.id.edit_btn);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
