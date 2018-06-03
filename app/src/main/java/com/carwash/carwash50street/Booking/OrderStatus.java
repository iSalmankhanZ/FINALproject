package com.carwash.carwash50street.Booking;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.carwash.carwash50street.Common.Common;
import com.carwash.carwash50street.Interface.ItemClickListner;
import com.carwash.carwash50street.Model.Request;
import com.carwash.carwash50street.Model.Service;
import com.carwash.carwash50street.R;
import com.carwash.carwash50street.ViewHolder.OrderViewHolder;
import com.carwash.carwash50street.ViewHolder.ServiceViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class OrderStatus extends AppCompatActivity {
    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request,OrderViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference requests;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("New_Orders");

        recyclerView = (RecyclerView)findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if(getIntent()!= null)
            loadOrders(Common.currentUser.getPhone());
        else
            loadOrders(getIntent().getStringExtra("userPhone"));
    }
    private void loadOrders(String phone) {

        Query getOrderByUser = requests.orderByChild("phone")
                .equalTo(phone);

        FirebaseRecyclerOptions<Request> orderOption = new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(getOrderByUser,Request.class)
                .build();



        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(orderOption) {
            @NonNull
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder viewHolder, int position, @NonNull Request model) {
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderAddress.setText(model.getAddress());
                viewHolder.txtOrderPhone.setText(model.getPhone());
                viewHolder.setItemClickListner(new ItemClickListner() {
                    @Override
                    public void onClick(View v, int adapterPosition, boolean isLongClick) {
                        Toast.makeText(OrderStatus.this, "", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.order_layout,parent,false);
                return  new OrderViewHolder(itemView);
            }
        };
        adapter.startListening();



                /*new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests.orderByChild("phone")
                        .equalTo(phone)
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Request model, int position) {
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderAddress.setText(model.getAddress());
                viewHolder.txtOrderPhone.setText(model.getPhone());
            }
        };*/
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}

