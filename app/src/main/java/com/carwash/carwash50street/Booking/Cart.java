package com.carwash.carwash50street.Booking;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.carwash.carwash50street.Common.Common;
import com.carwash.carwash50street.Common.Config;
import com.carwash.carwash50street.Database.Database;
import com.carwash.carwash50street.Helper.RecyclerTouchHelper;
import com.carwash.carwash50street.Interface.RecyclerItemTouchHelper;
import com.carwash.carwash50street.Model.MyResponse;
import com.carwash.carwash50street.Model.Notification;
import com.carwash.carwash50street.Model.Order;
import com.carwash.carwash50street.Model.Request;
import com.carwash.carwash50street.Model.Sender;
import com.carwash.carwash50street.Model.TimeSlot;
import com.carwash.carwash50street.Model.Token;
import com.carwash.carwash50street.Model.User;
import com.carwash.carwash50street.R;
import com.carwash.carwash50street.Remote.APIService;
import com.carwash.carwash50street.Remote.IGoogleService;
import com.carwash.carwash50street.Verification.SignIn;
import com.carwash.carwash50street.Verification.SignUp;
import com.carwash.carwash50street.ViewHolder.CartAdapter;
import com.carwash.carwash50street.ViewHolder.CartViewHolder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.razorpay.Checkout.RZP_REQUEST_CODE;

public class Cart extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener,PaymentResultListener, RecyclerItemTouchHelper {

    private static final int PAYPAL_REQUEST_CODE = 9999;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RelativeLayout rootLayout;

    FirebaseDatabase database;
    DatabaseReference requests;

    TextView txtTotalPrice;
    FButton btnPlace,btnDate;

    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;
    int payamount;

    Place shippingAddress;
    public DatePickerDialog.OnDateSetListener mDateSetListener;
    public static final String TAG = "Cart";
    Calendar cal = Calendar.getInstance();
    private Spinner spinner,spincar;


    //Paypal Payment

    static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);

    String address, comment,regnumber;

    //lOCATION
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mlastLocation;

    private static final int UPDATE_INTERVAL = 5000;
    private static final int FASTEST_INTERVAL = 3000;
    private static final int DISPLACEMENT = 10;

    private static final int LOCATION_REQUEST_CODE = 9999;
    private static final int PLAY_SERVICES_REQUEST = 9997;
    public Button mDisplayDate;

    //Declaring Google Map Api Retrofit
    IGoogleService mGoogleMapService;
    APIService mService;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/n_font.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_cart);

        rootLayout = (RelativeLayout)findViewById(R.id.root_layout);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_more);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(Cart.this,Home.class);
                startActivity(cartIntent);
                finish();
            }
        });


        //Runtime Permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION

                    }, LOCATION_REQUEST_CODE);
        } else {
            if (checkPlayServices()) //Tocheck if phone support play services
            {
                buildGoogleApiClient();
                createLocationRequest();
            }
        }

        //init Google map
        mGoogleMapService = Common.getGoogleMapAPI();


        //Init Paymnet
        Intent pay_intent = new Intent(this, PayPalService.class);
        pay_intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(pay_intent);

        //Initialising Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("New_Orders");

        //Init Services
        mService = Common.getFCMService();

        recyclerView = (RecyclerView) findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Swipe to delete
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);


        txtTotalPrice = (TextView) findViewById(R.id.total);
        btnPlace = (FButton) findViewById(R.id.btnPlaceOrder);
        btnDate = (FButton)findViewById(R.id.btnSelectDate);
        spinner = (Spinner) findViewById(R.id.spinTime);
        spincar = (Spinner)findViewById(R.id.spinCar);

        final ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(Cart.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.time));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myAdapter);


        final ArrayAdapter<String> myAdaptercar = new ArrayAdapter<String>(Cart.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.car));
        myAdaptercar.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spincar.setAdapter(myAdaptercar);



        //Selecting Date
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        Cart.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: dd/mm/yyy: " + day + "/" + month + "/" + year);
                String date = day + "/" + month + "/" + year;
                btnDate.setText(date);
            }
        };

        //Place Order Button
        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating new Request
            /*    if (cart.size() > 0)
                    showAlertDialog();
                else
                    Toast.makeText(Cart.this, "Empty Cart..!!", Toast.LENGTH_SHORT).show();*/
            if(cart.isEmpty())
            { Toast.makeText(Cart.this, "Empty Cart..!!", Toast.LENGTH_SHORT).show(); }
            else
                showAlertDialog();
            }
        });

        loadListService();
    }

    private void showTimeDialog() {
        final AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(Cart.this);
        alertDialog1.setTitle("Select Time");
        final LayoutInflater inflater1 = this.getLayoutInflater();
        final View time_slot = inflater1.inflate(R.layout.time_slots, null);


        //Radio
        final RadioButton time1 = (RadioButton) time_slot.findViewById(R.id.time1);
        final RadioButton time2 = (RadioButton) time_slot.findViewById(R.id.time2);
        final RadioButton time3 = (RadioButton) time_slot.findViewById(R.id.time3);
        final RadioButton time4 = (RadioButton) time_slot.findViewById(R.id.time4);
        final RadioButton time5 = (RadioButton) time_slot.findViewById(R.id.time5);
        final RadioButton time6 = (RadioButton) time_slot.findViewById(R.id.time6);
        final RadioButton time7 = (RadioButton) time_slot.findViewById(R.id.time7);
        final RadioButton time8 = (RadioButton) time_slot.findViewById(R.id.time8);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference time_table = database.getReference("Time");

    }
    //Google Locations
    @SuppressLint("RestrictedApi")
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    //Google Locations
    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();
    }

    //Tocheck if phone support play services
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_REQUEST).show();
            } else {
                Toast.makeText(this, "Device not  Supported!!", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("Final Step");

        final LayoutInflater inflater = this.getLayoutInflater();
        View order_address_comment = inflater.inflate(R.layout.order_address_comment, null);

        //final MaterialEditText edtAddress = (MaterialEditText) order_address_comment.findViewById(R.id.edtAddress);
        final PlaceAutocompleteFragment edtAddress = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        //Hiding Search icon before fragments

        edtAddress.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);
        //Set Hint for AutoComplete EditText
        ((EditText) edtAddress.getView().findViewById(R.id.place_autocomplete_search_input))
                .setHint("Search Address From Google");
        //Setting text Size
        ((EditText) edtAddress.getView().findViewById(R.id.place_autocomplete_search_input))
                .setTextSize(14);

        //GetAddress from Place AutoComplete
        edtAddress.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                shippingAddress = place;
            }

            @Override
            public void onError(Status status) {
                Log.e("ERROR", status.getStatusMessage());
            }
        });

        //Radio
        //final RadioButton rdiShipToAddress = (RadioButton) order_address_comment.findViewById(R.id.rdiShipToAddress);
        final RadioButton rdiHomeAddress = (RadioButton) order_address_comment.findViewById(R.id.rdiHomeAddress);
        final RadioButton rdiHardCash = (RadioButton) order_address_comment.findViewById(R.id.rdiCod);
        final RadioButton rdiPaypal = (RadioButton) order_address_comment.findViewById(R.id.rdiPaypal);
        final RadioButton rdiRazorpay = (RadioButton) order_address_comment.findViewById(R.id.rdiRazorpay);
        final RadioButton rdiDrive = (RadioButton) order_address_comment.findViewById(R.id.rdiDrive);

        //Radio Event
        rdiHomeAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    if (Common.currentUser.getHomeAddress() != null ||
                            !TextUtils.isEmpty(Common.currentUser.getHomeAddress())) {
                        address = Common.currentUser.getHomeAddress();
                        ((EditText) edtAddress.getView().findViewById(R.id.place_autocomplete_search_input))
                                .setText(address);
                    } else {
                        Toast.makeText(Cart.this, "Please Update your home address first", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        rdiDrive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    address = "I Will Drive";
                    ((EditText) edtAddress.getView().findViewById(R.id.place_autocomplete_search_input))
                            .setText(address);
                }
            }
        });

        final MaterialEditText edtComment = (MaterialEditText) order_address_comment.findViewById(R.id.edtComment);
        final MaterialEditText edtRegNum = (MaterialEditText) order_address_comment.findViewById(R.id.edtRegNum);

        alertDialog.setView(order_address_comment);
        alertDialog.setIcon(R.drawable.ic_local_car_wash_black_24dp);

         alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    //Show Paypal to payment
                    //fetch address and comment from Alert Dialog
                    //Conditions for Fetching address
                    //1: if user selects address from place Fragment, just use it
                    //2: if user selects Home address , get HomeAddress from profile and use it
                    //3: if user selects Ship to Address, get Address from location and use it

                    if (!rdiHomeAddress.isChecked() && !rdiDrive.isChecked()) {
                        //If both radio buttons are not selected
                        if (shippingAddress != null)
                            address = shippingAddress.getAddress().toString();
                        else {
                            Toast.makeText(Cart.this, "Please enter address or select option", Toast.LENGTH_SHORT).show();
                            //Fix crash Fragment
                            getFragmentManager().beginTransaction()
                                    .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                                    .commit();
                            return;
                        }
                    }
                    if (TextUtils.isEmpty(address)) {
                        Toast.makeText(Cart.this, "Please enter your address", Toast.LENGTH_SHORT).show();
                        //Fix crash Fragment
                        getFragmentManager().beginTransaction()
                                .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                                .commit();
                        return;
                    }

                    //Taking Comment
                    String cmt = edtComment.getText().toString();
                    if (cmt.isEmpty()) {
                        Toast.makeText(Cart.this, "Enter Comment", Toast.LENGTH_SHORT).show();
                    } else {
                        comment = edtComment.getText().toString();
                    }

                    //Taking RegNumber
                    String reg = edtRegNum.getText().toString();
                    if (reg.isEmpty()) {
                        Toast.makeText(Cart.this, "Enter Reg_Number", Toast.LENGTH_SHORT).show();
                    } else {
                        regnumber = edtRegNum.getText().toString();
                    }

                    if (cmt.isEmpty() && reg.isEmpty()) {
                        Toast.makeText(Cart.this, "sorry", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!rdiHardCash.isChecked() && !rdiPaypal.isChecked() && !rdiRazorpay.isChecked()) {
                            Toast.makeText(Cart.this, "Please Select a payment option", Toast.LENGTH_SHORT).show();

                            getFragmentManager().beginTransaction()
                                    .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                                    .commit();
                            return;
                        } else if (rdiPaypal.isChecked()) {

                            String formatAmount = txtTotalPrice.getText().toString()
                                    .replace("₹", "0")
                                    .replace(",", "");

                            PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(formatAmount),
                                    "USD",
                                    "Car Wash Payment",
                                    PayPalPayment.PAYMENT_INTENT_SALE);
                            Intent pintent = new Intent(getApplicationContext(), PaymentActivity.class);
                            pintent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                            pintent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
                            startActivityForResult(pintent, PAYPAL_REQUEST_CODE);
                        } else if (rdiRazorpay.isChecked()) {
                            startPayment();

                        } else if (rdiHardCash.isChecked()) {
                            Request request = new Request(
                                    Common.currentUser.getPhone(),
                                    Common.currentUser.getName(),
                                    address,
                                    txtTotalPrice.getText().toString(),
                                    btnDate.getText().toString(),
                                    spinner.getSelectedItem().toString(),
                                    spincar.getSelectedItem().toString(),
                                    regnumber,
                                    "0",
                                    comment,
                                    "HardCash",
                                    "Unpaid",
                                    //String.format("%s,%s", mlastLocation.getLatitude(), mlastLocation.getLongitude()), //coordinates when user orders
                                    cart
                            );

                            Date todayDate = Calendar.getInstance().getTime();
                            SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy hh_mm_ss a");
                            String order_number = formatter.format(todayDate);
                            requests.child(order_number)
                                    .setValue(request);

                            //Deleting Cart
                            new Database(getBaseContext()).cleanCart();
                            sendNotification(order_number);

                            Toast.makeText(Cart.this, "Thank you", Toast.LENGTH_SHORT).show();
                            Intent loadIntent = new Intent(Cart.this, OrderStatus.class);
                            startActivity(loadIntent);
                            finish();

                        }
                    }
                    //Remove Fragment
                    getFragmentManager().beginTransaction()
                            .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                            .commit();
                }
            });

            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                    //Remove fragment
                    getFragmentManager().beginTransaction()
                            .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                            .commit();
                }
            });

            alertDialog.show();

        }

    private void startPayment() {

        String value = txtTotalPrice.getText().toString();
        value = value.replaceAll("\\D+",""); //REGEX
        int multiple = 1*Integer.parseInt(value);

        //payamount = Integer.parseInt(txtTotalPrice.getText().toString());
        Checkout checkout = new Checkout();
        checkout.setImage(R.mipmap.ic_launcher);
        final Activity activity = this;

        try{
            JSONObject object  = new JSONObject();
            object.put("description","Order #123456");
            object.put("currency","INR");
            object.put("amount",multiple);
            checkout.open(activity,object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Ctrl+o
    //Because we are accessing runtime permission so onRequest
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayServices()) //Tocheck if phone support play services
                    {
                        buildGoogleApiClient();
                        createLocationRequest();
                    }
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (requestCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    try {
                        String paymentDetail = confirmation.toJSONObject().toString(4);
                        JSONObject jsonObject = new JSONObject(paymentDetail);


                        //Creating new request

                        Request request = new Request(
                                Common.currentUser.getPhone(),
                                Common.currentUser.getName(),
                                address,
                                txtTotalPrice.getText().toString(),
                                btnDate.getText().toString(),
                                //btnTimes.getText().toString(),
                                spinner.getSelectedItem().toString(),
                                spincar.getSelectedItem().toString(),
                                regnumber,
                                "0",
                                comment,
                                "Paypal",
                                jsonObject.getJSONObject("response").getString("state"),
                                //state fron Json
//                                String.format("%s,%s", shippingAddress.getLatLng().latitude, shippingAddress.getLatLng().longitude),
                                cart
                        );

                        //Submit to Firebase using System.CurrentMilli
                        // requests.child(String.valueOf(System.currentTimeMillis()))
                        //       .setValue(request);
/*
                      String order_number = String.valueOf(System.currentTimeMillis());
                        requests.child(String.valueOf(order_number))
                                .setValue(request);


                        //Deleting Cart
                        new Database(getBaseContext()).cleanCart();
                        sendNotification(order_number);*/

                        Date todayDate = Calendar.getInstance().getTime();
                        SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy hh_mm_ss a");
                        String order_number = formatter.format(todayDate);
                        requests.child(order_number)
                                .setValue(request);

                        //Deleting Cart
                        new Database(getBaseContext()).cleanCart();
                        sendNotification(order_number);

                        Toast.makeText(Cart.this, "Thank you", Toast.LENGTH_SHORT).show();
                        Intent loadIntent = new Intent(Cart.this, OrderStatus.class);
                        startActivity(loadIntent);
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED)
                Toast.makeText(this, "Payment Cancaled", Toast.LENGTH_SHORT).show();
            else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
                Toast.makeText(this, "Invalid Payment", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendNotification(final String order_number) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query data = tokens.orderByChild("isServerToken").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSanpshot : dataSnapshot.getChildren()) {
                    Token serverToken = postSanpshot.getValue(Token.class);
                    //Create raw payload to send
                    Notification notification = new Notification("50 street Car Wash ", " You have new Order" + order_number);
                    Sender context = new Sender(serverToken.getToken(), notification);

                    mService.sendNotification(context)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success == 1) {
                                            Toast.makeText(Cart.this, "Thank you, Order Placed..!!", Toast.LENGTH_SHORT).show();
                                            // Intent loadIntent = new Intent(Cart.this, OrderStatus.class);
                                            //startActivity(loadIntent);
                                            finish();
                                        } else {
                                            Toast.makeText(Cart.this, "Oops Failed..!!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.e("ERROR", t.getMessage());
                                }
                            });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void loadListService() {
        cart = new Database(this).getCarts();
        adapter = new CartAdapter(cart, this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        //calculating final price

        int total = 0;
        int total1 = 0;
        for (Order order : cart)
            total += ((Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()))-(Integer.parseInt(order.getDiscount())));
//        txtTotalPrice.setText(total)
// ;
        Locale locale = new Locale("en_IN", "IN");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        txtTotalPrice.setText(fmt.format(total));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());
        return true;
    }

    private void deleteCart(int position) {
        //to remove item at List<Order> by position
        cart.remove(position);
        //Deleting Data from SQLlite
        new Database(this).cleanCart();
        for (Order item : cart)
            new Database(this).addToCart(item);
        //Refresh
        loadListService();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mlastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mlastLocation != null) {
            Log.d("LOCATION", "Your location : " + mlastLocation.getLatitude() + "," + mlastLocation.getLongitude());
        } else {
            Log.d("LOCATION", "Could not get your location");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mlastLocation = location;
        displayLocation();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Cart.this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPaymentSuccess(String s) {
        Request request = new Request(
                Common.currentUser.getPhone(),
                Common.currentUser.getName(),
                address,
                txtTotalPrice.getText().toString(),
                btnDate.getText().toString(),
                spinner.getSelectedItem().toString(),
                spincar.getSelectedItem().toString(),
                regnumber,
                "0",
                comment,
                "RazorPay",
                "Paid",
                //String.format("%s,%s", mlastLocation.getLatitude(), mlastLocation.getLongitude()), //coordinates when user orders
                cart
        );

        /*
        String order_number = String.valueOf(System.currentTimeMillis());
        requests.child(order_number)
                .setValue(request);
        //Deleting Cart

        new Database(getBaseContext()).cleanCart();
        sendNotification(order_number);*/

        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy hh_mm_ss a");
        String order_number = formatter.format(todayDate);
        requests.child(order_number)
                .setValue(request);

        //Deleting Cart
        new Database(getBaseContext()).cleanCart();
        sendNotification(order_number);


        Toast.makeText(Cart.this, "Thank you", Toast.LENGTH_SHORT).show();
        Intent loadIntent = new Intent(Cart.this, OrderStatus.class);
        startActivity(loadIntent);
        finish();
        Toast.makeText(this, "Payment successfull", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Request request = new Request(
                Common.currentUser.getPhone(),
                Common.currentUser.getName(),
                address,
                txtTotalPrice.getText().toString(),
                btnDate.getText().toString(),
                spinner.getSelectedItem().toString(),
                spincar.getSelectedItem().toString(),
                regnumber,
                "0",
                comment,
                "RazorPay",
                "Unpaid",
                //String.format("%s,%s", mlastLocation.getLatitude(), mlastLocation.getLongitude()), //coordinates when user orders
                cart
        );

        Toast.makeText(Cart.this, "Error Occured", Toast.LENGTH_SHORT).show();
        Intent loadIntent = new Intent(Cart.this, OrderStatus.class);
        startActivity(loadIntent);
        finish();
        Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSwipe(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof CartViewHolder)
        {
            String name = ((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()).getProductName();

            final Order deleteItem = ((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());
            final int deleteIndex = viewHolder.getAdapterPosition();

            adapter.removeItem(deleteIndex);
            new Database(getBaseContext()). removeFromCart(deleteItem.getProductId(),Common.currentUser.getPhone());

            //update txttotal
            //calculate total price
            int total = 0;
            List<Order> orders = new Database(getBaseContext()).getCarts();
            for (Order item : orders)
                total+=(Integer.parseInt(item.getPrice()))*(Integer.parseInt(item.getQuantity()));
            Locale locale = new Locale("en","IN");
            NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

            txtTotalPrice.setText(fmt.format(total));

            Snackbar snackbar = Snackbar.make(rootLayout,name+"removed from cart!",Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        adapter.restoreItem(deleteItem,deleteIndex);
                        new Database(getBaseContext()).addToCart(deleteItem);

                    //update txttotal
                    //calculate total price
                    int total = 0;
                    List<Order> orders = new Database(getBaseContext()).getCarts();
                    for (Order item : orders)
                        total+=(Integer.parseInt(item.getPrice()))*(Integer.parseInt(item.getQuantity()));
                    Locale locale = new Locale("en","IN");
                    NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

                    txtTotalPrice.setText(fmt.format(total));

                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}
