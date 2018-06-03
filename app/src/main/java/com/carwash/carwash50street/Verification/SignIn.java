package com.carwash.carwash50street.Verification;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.carwash.carwash50street.Booking.Home;
import com.carwash.carwash50street.Booking.MainActivity;
import com.carwash.carwash50street.Common.Common;
import com.carwash.carwash50street.MainScreen;
import com.carwash.carwash50street.Model.User;
import com.carwash.carwash50street.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignIn extends AppCompatActivity {

    EditText edtPhone, edtPassword;
    Button btnSignIn;
    CheckBox ckbRemember;
    TextView txtPingen,txtForgetPin;

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
        setContentView(R.layout.activity_sign_in);

        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        ckbRemember = (CheckBox) findViewById(R.id.ckbRemember);
        txtPingen = (TextView)findViewById(R.id.textPingen);
        txtForgetPin = (TextView)findViewById(R.id.textForgetPin);

        txtPingen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignIn.this,SignUp.class);
                startActivity(intent);
            }
        });

        txtForgetPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignIn.this, "", Toast.LENGTH_SHORT).show();
            }
        });

        //Inir paperdb
        Paper.init(this);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference user_table = database.getReference("User");
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }
    private void signIn() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference user_table = database.getReference("User");
        if (Common.isConnectedToInternet(getBaseContext())) {

            //Save User name and Password
            if (ckbRemember.isChecked()) {
                Paper.book().write(Common.USER_KEY, edtPhone.getText().toString());
                Paper.book().write(Common.PWD_KEY, edtPassword.getText().toString());
            }
            //Progress dialog
            final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
            mDialog.setMessage("Please Wait");
            mDialog.show();

            user_table.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {

                        mDialog.dismiss();
                        User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);
                        user.setPhone(edtPhone.getText().toString());//set Phone
                        if (user.getPassword().equals(edtPassword.getText().toString())) {
                            {
                                Intent homeintent = new Intent(SignIn.this, MainActivity.class);
                                Common.currentUser = user;
                                startActivity(homeintent);
                                finish();

                                user_table.removeEventListener(this);
                            }
                        } else {
                            Toast.makeText(SignIn.this, "oops!! Wrong Password", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        mDialog.dismiss();
                        Toast.makeText(SignIn.this, "Sign Up first", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } else {
            Toast.makeText(SignIn.this, "Please connect internet.!", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}