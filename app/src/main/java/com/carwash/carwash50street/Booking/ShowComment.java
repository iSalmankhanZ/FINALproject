package com.carwash.carwash50street.Booking;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carwash.carwash50street.Common.Common;
import com.carwash.carwash50street.Model.Rating;
import com.carwash.carwash50street.R;
import com.carwash.carwash50street.ViewHolder.ShowCommentViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ShowComment extends AppCompatActivity {
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference ratingTbl;

    SwipeRefreshLayout swipeRefreshLayout;

    FirebaseRecyclerAdapter<Rating,ShowCommentViewHolder> adapter;

    String serviceId="";

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter != null)
            adapter.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_comment);

        database = FirebaseDatabase.getInstance();
        ratingTbl = database.getReference("Rating");

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerComment);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        //swipe Layout
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(getIntent()!=null)
                    serviceId = getIntent().getStringExtra(Common.INTENT_SERVICE_ID);
                if(!serviceId.isEmpty())
                {
                    Query query = ratingTbl.orderByChild("serviceId").equalTo(serviceId);
                    FirebaseRecyclerOptions<Rating> options = new FirebaseRecyclerOptions.Builder<Rating>()
                            .setQuery(query,Rating.class)
                            .build();

                    adapter = new FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull ShowCommentViewHolder holder, int position, @NonNull Rating model) {
                            holder.ratingBar.setRating(Float.parseFloat(model.getRateValue()));
                            holder.txtComment.setText(model.getComment());
                            holder.txtUserPhone.setText(model.getUserPhone());
                        }
                        @Override
                        public ShowCommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.show_comment_layout,parent,true);
                            return new ShowCommentViewHolder(view);
                        }
                    };
                    loadComment(serviceId);
                }
            }
        });

        //Thread to load on first launch
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                if(getIntent()!=null)
                    serviceId = getIntent().getStringExtra(Common.INTENT_SERVICE_ID);
                if(!serviceId.isEmpty())
                {
                    Query query = ratingTbl.orderByChild("serviceId").equalTo(serviceId);
                    FirebaseRecyclerOptions<Rating> options = new FirebaseRecyclerOptions.Builder<Rating>()
                            .setQuery(query,Rating.class)
                            .build();


                    adapter = new FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull ShowCommentViewHolder holder, int position, @NonNull Rating model) {
                            holder.ratingBar.setRating(Float.parseFloat(model.getRateValue()));
                            holder.txtComment.setText(model.getComment());
                            holder.txtUserPhone.setText(model.getUserPhone());
                        }
                        @Override
                        public ShowCommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.show_comment_layout,parent,false);
                           return new ShowCommentViewHolder(view);
                            // return new ShowCommentViewHolder(view);
                        }
                    };
                    loadComment(serviceId);
                }
            }
        });
    }

    private void loadComment(String serviceId) {
        adapter.startListening();

        mRecyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }
}
