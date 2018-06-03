package com.carwash.carwash50street.Booking;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.carwash.carwash50street.Common.Common;
import com.carwash.carwash50street.Database.Database;
import com.carwash.carwash50street.Model.Order;
import com.carwash.carwash50street.Model.Rating;
import com.carwash.carwash50street.Model.Service;
import com.carwash.carwash50street.R;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.Arrays;

import info.hoang8f.widget.FButton;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ServiceDetails extends AppCompatActivity implements RatingDialogListener {

    TextView service_name,service_price,service_description;
    ImageView service_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart,btnRating;
    ElegantNumberButton numberButton;
    RatingBar ratingBar;
    FButton btnComment;

    String serviceId="";

    FirebaseDatabase database;
    DatabaseReference services;
    DatabaseReference ratingTbl;

    Service currentService;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_details);


        //Firebase
        database = FirebaseDatabase.getInstance();
        services = database.getReference("Services");
        ratingTbl = database.getReference("Rating");

        //initializing values
        numberButton = (ElegantNumberButton)findViewById(R.id.number_button);
        btnCart = (FloatingActionButton)findViewById(R.id.btnCart);
        btnRating = (FloatingActionButton)findViewById(R.id.btn_rating);
        ratingBar = (RatingBar)findViewById(R.id.ratingBar);
        btnComment = (FButton)findViewById(R.id.btnShowComment);

        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(ServiceDetails.this,ShowComment.class);
                intent.putExtra(Common.INTENT_SERVICE_ID,serviceId);
                startActivity(intent);
            }
        });

        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog();
            }
        });

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Database(getBaseContext()).addToCart(new Order(
                        serviceId,
                        currentService.getName(),
                        numberButton.getNumber(),
                        currentService.getPrice(),
                        currentService.getDiscount()
                ));

                Toast.makeText(ServiceDetails.this,"Added to Cart",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ServiceDetails.this,Cart.class);
                startActivity(intent);
            }
        });

        service_description = (TextView)findViewById(R.id.service_description);
        service_name = (TextView)findViewById(R.id.service_name);
        service_price = (TextView)findViewById(R.id.service_price);
        service_image = (ImageView) findViewById(R.id.img_service);

        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsedAppbar);

        //Fetching ServiceId from Intent
        if(getIntent()!=null)
            serviceId = getIntent().getStringExtra("ServiceId");
        if(!serviceId.isEmpty())
        {
            if(Common.isConnectedToInternet(getBaseContext()))
            {
                getDetailService(serviceId);
                getRatingService(serviceId);
            }
            else
            {
                Toast.makeText(ServiceDetails.this,"Please connect to internet..!!",Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    private void getRatingService(String serviceId) {
        Query serviceRating = ratingTbl.orderByChild("serviceId").equalTo(serviceId);

        serviceRating.addValueEventListener(new ValueEventListener() {
            int count=0,sum=0;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    Rating item = postSnapshot.getValue(Rating.class);
                    assert item != null;
                    sum+=Integer.parseInt(item.getRateValue());
                    count++;
                }
                if(count!=0)
                {
                    float average = sum/count;
                    ratingBar.setRating(average);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancle")
                .setNoteDescriptions(Arrays.asList("Poor","Not Bad","Quite Good","Very Good","Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate this Service")
                .setDescription("Please give your valuable rating")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Please Write your comment here..")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(android.R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(ServiceDetails.this)
                .show();

    }

    private void getDetailService(String serviceId) {
        services.child(serviceId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentService = dataSnapshot.getValue(Service.class);
                //Setting Image
                Picasso.with(getBaseContext()).load(currentService.getImage())
                        .into(service_image);
                collapsingToolbarLayout.setTitle(currentService.getName());

                service_price.setText(currentService.getPrice());

                service_name.setText(currentService.getName());

                service_description.setText(currentService.getDescription());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onPositiveButtonClicked(int value, String comments) {
        //getting ratings and uploading it to firebase
        final Rating rating = new Rating(Common.currentUser.getPhone(),
                serviceId,
                String.valueOf(value),
                comments);
        //user can rate multiple times
        ratingTbl.push()
                .setValue(rating)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ServiceDetails.this,"Thank you..!!",Toast.LENGTH_SHORT).show();
                    }
                });
        /*ratingTbl.child(Common.currentUser.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(Common.currentUser.getPhone()).exists())
                {
                    ratingTbl.child(Common.currentUser.getPhone()).removeValue();
                    //Updating new values
                    ratingTbl.child(Common.currentUser.getPhone()).setValue(rating);
                }
                else
                {
                    ratingTbl.child(Common.currentUser.getPhone()).setValue(rating);
                }
                Toast.makeText(ServiceDetails.this,"Thank you..!!",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
    }

    @Override
    public void onNegativeButtonClicked() {

    }
}