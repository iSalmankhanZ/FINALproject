package com.carwash.carwash50street.Booking;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.carwash.carwash50street.Common.Common;
import com.carwash.carwash50street.Database.Database;
import com.carwash.carwash50street.Interface.ItemClickListner;
import com.carwash.carwash50street.Model.Favourites;
import com.carwash.carwash50street.Model.Service;
import com.carwash.carwash50street.ViewHolder.ServiceViewHolder;
import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import com.carwash.carwash50street.R;

public class ServiceList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference serviceList;

    String categoryId = "";

    FirebaseRecyclerAdapter<Service,ServiceViewHolder> adapter;

    //Search Functionality

    FirebaseRecyclerAdapter<Service,ServiceViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    //Favourites
    Database localDB;

    //Facebook Share
    CallbackManager callbackManager;
    ShareDialog shareDialog;


    //create Target from Picasso
    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            //convet photo to bitmap
            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();
            if(ShareDialog.canShow(SharePhotoContent.class))
            {
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();
                shareDialog.show(content);
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_list);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_more);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(ServiceList.this,Home.class);
                startActivity(cartIntent);
                finish();
            }
        });


        //Firebase invoke
        database = FirebaseDatabase.getInstance();
        serviceList = database.getReference("Services");

        //SQLite DB invoke
        localDB = new Database(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_service);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Accesing Intent
        if (getIntent() != null)
            categoryId = getIntent().getStringExtra("CategoryId");
        if (!categoryId.isEmpty() && categoryId != null) {
            if(Common.isConnectedToInternet(getBaseContext()))
                loadListService(categoryId);
            else {
                Toast.makeText(ServiceList.this,"Please connect internet.!",Toast.LENGTH_SHORT).show();
                return;
            }
        }

        //Search
        materialSearchBar = (MaterialSearchBar)findViewById(R.id.searchbar);
        materialSearchBar.setHint("Search here!");
        loadSuggest();//fuction for loading
        //materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //giving suggestions

                List<String> suggest = new ArrayList<String>();
                for (String search : suggestList) {
                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase())) ;
                    suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {

            @Override
            public void onSearchStateChanged(boolean enabled) {
                //When search bar is closed restore original page
                if(!enabled)
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //When search finish
                //show result of search adapter
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {
            }
        });
    }

    private void startSearch(CharSequence text) {
        //Condition for searching by name
        Query searchByName = serviceList.orderByChild("name").equalTo(text.toString());
        //Creating options with query
        FirebaseRecyclerOptions<Service> serviceOption = new FirebaseRecyclerOptions.Builder<Service>()
                .setQuery(searchByName,Service.class)
                .build();

        searchAdapter = new FirebaseRecyclerAdapter<Service, ServiceViewHolder>(serviceOption) {
            @Override
            protected void onBindViewHolder(@NonNull ServiceViewHolder viewHolder, int position, @NonNull Service model) {

                viewHolder.service_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.service_image);

                final Service local = model;
                viewHolder.setItemClickListner(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent serviseDetails = new Intent(ServiceList.this,ServiceDetails.class);
                        serviseDetails.putExtra("ServiceId",searchAdapter.getRef(position).getKey());
                        startActivity(serviseDetails);
                    }
                });
            }

            @NonNull
            @Override
            public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.service_item,parent,false);
                return  new ServiceViewHolder(itemView);
            }
        };
        searchAdapter.startListening();




                /*new FirebaseRecyclerAdapter<Service, ServiceViewHolder>(
                Service.class,
                R.layout.service_item,
                ServiceViewHolder.class,
                serviceList.orderByChild("Name")
                        .equalTo(text.toString())
        ) {
            @Override
            protected void populateViewHolder(ServiceViewHolder viewHolder, Service model, int position) {
                viewHolder.service_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.service_image);

                final Service local = model;
                viewHolder.setItemClickListner(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent serviseDetails = new Intent(ServiceList.this,ServiceDetails.class);
                        serviseDetails.putExtra("ServiceId",searchAdapter.getRef(position).getKey());
                        startActivity(serviseDetails);
                    }
                });
            }
        };*/
        recyclerView.setAdapter(searchAdapter);
    }

    private void loadSuggest() {
        serviceList.orderByChild("menuId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                        {
                            Service item = postSnapshot.getValue(Service.class);
                            suggestList.add(item.getName());
                        }
                        materialSearchBar.setLastSuggestions(suggestList);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void loadListService(String categoryId) {

        //Condition for searching by Category Id
        Query searchByName = serviceList.orderByChild("menuId").equalTo(categoryId);
        //Creating options with query
        FirebaseRecyclerOptions<Service> serviceOption = new FirebaseRecyclerOptions.Builder<Service>()
                .setQuery(searchByName,Service.class)
                .build();



        adapter = new FirebaseRecyclerAdapter<Service, ServiceViewHolder>(serviceOption) {
            @Override
            protected void onBindViewHolder(@NonNull final ServiceViewHolder viewHolder, final int position, @NonNull final Service model) {
                viewHolder.service_name.setText(model.getName());
                viewHolder.service_price.setText(String.format("Rs %s", model.getPrice()));
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.service_image);

                //Adding Favourites
                if (localDB.isFavourites(adapter.getRef(position).getKey(),Common.currentUser.getPhone()))
                    viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_border_black_24dp);

                //click to share
                viewHolder.share_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Picasso.with(getApplicationContext())
                                .load(model.getImage())
                                .into(target);
                    }
                });

                //click to change state of favourite
                viewHolder.fav_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Favourites favourites = new Favourites();
                        favourites.setServiceId(adapter.getRef(position).getKey());
                        favourites.setServiceName(model.getName());
                        favourites.setServiceDescription(model.getDescription());
                        favourites.setServiceDiscount(model.getDiscount());
                        favourites.setServiceImage(model.getImage());
                        favourites.setServiceMenuId(model.getMenuId());
                        favourites.setUserPhone(Common.currentUser.getPhone());
                        favourites.setServicePrice(model.getPrice());


                        if (localDB.isFavourites(adapter.getRef(position).getKey(),Common.currentUser.getPhone())) {
                            localDB.addToFavourites(favourites);
                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(ServiceList.this, "" + model.getName() + " Added to Favourites", Toast.LENGTH_SHORT).show();
                        } else {
                            localDB.removeFromFavourites(adapter.getRef(position).getKey(),Common.currentUser.getPhone());
                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(ServiceList.this, "" + model.getName() + " Removed from Favourites", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


                final Service local = model;
                viewHolder.setItemClickListner(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        //Toast.makeText(ServiceList.this, "" + local.getName(), Toast.LENGTH_SHORT).show();
                        //Starting the activity from here
                        Intent serviceDetails = new Intent(ServiceList.this, ServiceDetails.class);
                        serviceDetails.putExtra("ServiceId", adapter.getRef(position).getKey());
                        startActivity(serviceDetails);
                    }
                });
            }

            @NonNull
            @Override
            public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.service_item,parent,false);
                return  new ServiceViewHolder(itemView);
            }
        };
        /*new FirebaseRecyclerAdapter<Service, ServiceViewHolder>(Service.class,
                R.layout.service_item,
                ServiceViewHolder.class,
                serviceList.orderByChild("menuId").equalTo(categoryId) //Similar to Select * from Service Where MenuId=
        ){
            @Override
            protected void populateViewHolder(final ServiceViewHolder viewHolder, final Service model, final int position) {
                viewHolder.service_name.setText(model.getName());
                viewHolder.service_price.setText(String.format("Rs %s",model.getPrice()));
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.service_image);

                //Adding Favourites
                if(localDB.isFavourites(adapter.getRef(position).getKey()))
                    viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_border_black_24dp);

                //click to share
                viewHolder.share_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Picasso.with(getApplicationContext())
                                .load(model.getImage())
                                .into(target);
                    }
                });

                //click to change state of favourite
                viewHolder.fav_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(localDB.isFavourites(adapter.getRef(position).getKey()))
                        {
                            localDB.addToFavourites(adapter.getRef(position).getKey());
                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(ServiceList.this,""+model.getName()+" Added to Favourites",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            localDB.removeFromFavourites(adapter.getRef(position).getKey());
                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(ServiceList.this,""+model.getName()+" Removed from Favourites",Toast.LENGTH_SHORT).show();
                        }
                    }
                });


                final Service local = model;
                viewHolder.setItemClickListner(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        //Toast.makeText(ServiceList.this, "" + local.getName(), Toast.LENGTH_SHORT).show();
                        //Starting the activity from here
                        Intent serviceDetails = new Intent(ServiceList.this,ServiceDetails.class);
                        serviceDetails.putExtra("ServiceId",adapter.getRef(position).getKey());
                        startActivity(serviceDetails);
                    }
                });
            }
        };*/

        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
