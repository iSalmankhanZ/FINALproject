package com.carwash.carwash50street.Booking;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.carwash.carwash50street.Common.Common;
import com.carwash.carwash50street.Interface.ItemClickListner;
import com.carwash.carwash50street.Model.BannerActivity;
import com.carwash.carwash50street.Model.Category;
import com.carwash.carwash50street.Model.Token;
import com.carwash.carwash50street.R;
import com.carwash.carwash50street.Verification.SignIn;
import com.carwash.carwash50street.ViewHolder.MenuViewHolder;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase database;
    DatabaseReference category;
    TextView txtFullName;
    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;
    HashMap<String, String> image_list;
    SliderLayout mSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initialize Firebase
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this,Cart.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        FirebaseApp.initializeApp(this);
        View headerView = navigationView.getHeaderView(0);
        txtFullName = (TextView) headerView.findViewById(R.id.tv_Name);
        txtFullName.setText(Common.currentUser.getName());

        recycler_menu = (RecyclerView) findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        //layoutManager = new LinearLayoutManager(this);
        //recycler_menu.setLayoutManager(layoutManager);
        recycler_menu.setLayoutManager(new GridLayoutManager(this, 2));

        if(Common.isConnectedToInternet(this))
        {
            loadMenu();
        }
        else {
            Toast.makeText(Home.this,"Please connect internet.!",Toast.LENGTH_SHORT).show();
            return;
        }//Saving User token
        updateToken(FirebaseInstanceId.getInstance().getToken());
        //Setup ImageSlider
        setupSlider();
    }
    //Loading Menu
    private void loadMenu() {
        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(category, Category.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder viewHolder, final int position, @NonNull Category model) {
                viewHolder.txtMenuName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.imageView);
                final Category clickItem = model;
                viewHolder.setItemClickListner(new ItemClickListner() {
                    @Override
                    public void onClick(View v, int adapterPosition, boolean isLongClick) {
                        Intent serviceList = new Intent(Home.this, ServiceList.class);
                        serviceList.putExtra("CategoryId", adapter.getRef(position).getKey());
                        startActivity(serviceList);
                    }
                });
            }
            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_item, parent, false);
                return new MenuViewHolder(itemView);
            }
        };
        adapter.startListening();
        recycler_menu.setAdapter(adapter);
    }
//Banner for oppers and discounts
    private void setupSlider() {
        mSlider = (SliderLayout) findViewById(R.id.slider);
        image_list = new HashMap<>();
        final DatabaseReference banner = database.getReference("Banner");

        banner.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postsnapShot : dataSnapshot.getChildren()) {
                    BannerActivity banner = postsnapShot.getValue(BannerActivity.class);
                    //concating name and id
                    image_list.put(banner.getName() + "@@@" + banner.getId(), banner.getImage());
                }
                for (String key : image_list.keySet()) {
                    String[] keySplit = key.split("@@@");
                    String nameOfService = keySplit[0];
                    String idOfService = keySplit[1];

                    //Create Slider
                    final TextSliderView textSliderView = new TextSliderView(getBaseContext());
                    textSliderView
                            .description(nameOfService)
                            .image(image_list.get(key))
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {
                                    Intent intent = new Intent(Home.this, ServiceDetails.class);
                                    intent.putExtras(textSliderView.getBundle());
                                    startActivity(intent);
                                }
                            });
                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle().putString("ServiceId", idOfService);
                    mSlider.addSlider(textSliderView);

                    banner.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        mSlider.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
        mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mSlider.setCustomAnimation(new DescriptionAnimation());
        mSlider.setDuration(4000);
    }
    //Saving user token to firebase
    private void updateToken(String token) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference tokens = database.getReference("Tokens");
        Token data = new Token(token, false);
        tokens.child(Common.currentUser.getPhone()).setValue(data);
    }


/*    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }*/

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.refresh) {
            loadMenu();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {

        }

        else if (id == R.id.nav_cart) {
            Intent cartIntent = new Intent(Home.this, Cart.class);
            startActivity(cartIntent);

        }

        else if (id == R.id.nav_orders) {

            Intent orderIntent = new Intent(Home.this, OrderStatus.class);
            startActivity(orderIntent);

        }

        else if (id == R.id.nav_logout) {

            //Delete user and Password
            Paper.book().destroy();
            Intent logout = new Intent(Home.this, SignIn.class);
            logout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logout);

        }

        else if (id == R.id.nav_change_pwd) {
            showChangePasswordDialog();

        }

        else if (id == R.id.nav_home_address) {
            showHomeAddressDialog();

        }

        else if (id == R.id.nav_terms) {
            showTerms();
        }

        else if (id == R.id.nav_feedback) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + "sahanasonu124@gmail.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "50 Street Car Wash: Feedback");
            startActivity(intent);
        }

        else if (id == R.id.nav_contact) {
            showContact();
        }

        else if (id == R.id.nav_news) {
            showNewsDialog();
        }

        else if (id == R.id.nav_favour){
            Intent intent = new Intent(Home.this,FavouritesActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Shoiw Terms and Conditions
    private void showTerms() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Terms and Conditions");
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_terms = inflater.inflate(R.layout.activity_termsandconditions, null);
        alertDialog.setView(layout_terms);
        alertDialog.show().getWindow();
    }

    //Contact Us
    private void showContact() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Contact Us");
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_home = inflater.inflate(R.layout.activity_contact, null);
        alertDialog.setView(layout_home);
        alertDialog.show().getWindow();
    }

    //For Notifications on Offers and Updates
    private void showNewsDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Settings");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_news = inflater.inflate(R.layout.settings_layout, null);

        final CheckBox ckb = (CheckBox) layout_news.findViewById(R.id.ckb_sub_news);
        //Remember option
        Paper.init(this);
        String isSubsribe = Paper.book().read("sub_new");
        if (isSubsribe == null || TextUtils.isEmpty(isSubsribe) || isSubsribe.equals("false"))
            ckb.setChecked(false);
        else
            ckb.setChecked(true);
        alertDialog.setView(layout_news);

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                if (ckb.isChecked()) {
                    FirebaseMessaging.getInstance().subscribeToTopic(Common.topicName);
                    Paper.book().write("sub_new", "true");
                } else {
                    FirebaseMessaging.getInstance().subscribeToTopic(Common.topicName);
                    Paper.book().write("sub_new", "false");
                }

            }
        });
        alertDialog.show();
    }



//Storing User Home Address To firebase
private void showHomeAddressDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Home Address");
        alertDialog.setMessage("Please provide the necessary information");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_home = inflater.inflate(R.layout.home_address_layout, null);

        final MaterialEditText edtHomeAddress = (MaterialEditText) layout_home.findViewById(R.id.edtHomeAddress);
        alertDialog.setView(layout_home);

        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                //Set new Home Address
                Common.currentUser.setHomeAddress(edtHomeAddress.getText().toString());
                //Firebase to store user home address
                String home = edtHomeAddress.getText().toString();
                if (home.isEmpty()) {
                    Toast.makeText(Home.this, "Please enter address", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseDatabase.getInstance().getReference("User")
                            .child(Common.currentUser.getPhone())
                            .setValue(Common.currentUser)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(Home.this, "Update Address Successfull", Toast.LENGTH_SHORT).show();
                                    dialogInterface.dismiss();
                                }
                            });
                }
            }
        });
        alertDialog.show();
    }

//Changing PIN
 private void showChangePasswordDialog() {
     AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
     alertDialog.setTitle("CHANGE PIN");
     alertDialog.setMessage("Please provide the necessary information");

     LayoutInflater inflater = LayoutInflater.from(this);
     View layout_pwd = inflater.inflate(R.layout.change_password_layout, null);

     final MaterialEditText edtPassword = (MaterialEditText) layout_pwd.findViewById(R.id.edtPassword);
     final MaterialEditText edtNewPassword = (MaterialEditText) layout_pwd.findViewById(R.id.edtNewPassword);
     final MaterialEditText edtRepeatPassword = (MaterialEditText) layout_pwd.findViewById(R.id.edtRepeatPassword);
     alertDialog.setView(layout_pwd);

     //Button
     alertDialog.setPositiveButton("CHANGE", new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialogInterface, int i) {
             //password change
             final android.app.AlertDialog waitingDialog = new SpotsDialog(Home.this);
             waitingDialog.show();

             //Checking old password
             if (edtPassword.getText().toString().equals(Common.currentUser.getPassword())) {
                 if (edtNewPassword.getText().toString().equals(edtRepeatPassword.getText().toString())) {
                     Map<String, Object> passwordUpdate = new HashMap<>();
                     passwordUpdate.put("password", edtNewPassword.getText().toString());

                     //Updating Password
                     DatabaseReference user = FirebaseDatabase.getInstance().getReference("User");
                     user.child(Common.currentUser.getPhone())
                             .updateChildren(passwordUpdate)
                             .addOnCompleteListener(new OnCompleteListener<Void>() {
                                 @Override
                                 public void onComplete(@NonNull Task<Void> task) {
                                     waitingDialog.dismiss();
                                     Toast.makeText(Home.this, "Pin Updated", Toast.LENGTH_SHORT).show();
                                 }
                             })
                             .addOnFailureListener(new OnFailureListener() {
                                 @Override
                                 public void onFailure(@NonNull Exception e) {
                                     Toast.makeText(Home.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                 }
                             });
                 } else {
                     waitingDialog.dismiss();
                     Toast.makeText(Home.this, "New Pin dont match", Toast.LENGTH_SHORT).show();
                 }
             } else {
                 waitingDialog.dismiss();
                 Toast.makeText(Home.this, "Old Pin is inncorect", Toast.LENGTH_SHORT).show();
             }
         }
     });

     alertDialog.setNegativeButton("CANCLE", new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialogInterface, int i) {
             dialogInterface.dismiss();
         }
     });
     alertDialog.show();
 }
}
