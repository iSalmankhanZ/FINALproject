package com.carwash.carwash50street.Verification;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.carwash.carwash50street.Common.Common;
import com.carwash.carwash50street.Model.User;
import com.carwash.carwash50street.R;
import com.fasterxml.jackson.databind.deser.impl.InnerClassProperty;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignUp extends AppCompatActivity {

    MaterialEditText edtPhone, edtPassword, edtName;
    Button btnSignUp;
    TextView txthavepin;

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
        setContentView(R.layout.activity_sign_up);

        edtName = (MaterialEditText) findViewById(R.id.edtName);
        edtPassword = (MaterialEditText) findViewById(R.id.edtPassword);
        edtPhone = (MaterialEditText) findViewById(R.id.edtPhone);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        txthavepin = (TextView) findViewById(R.id.texthavepin);

        txthavepin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, SignIn.class);
                startActivity(intent);
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference user_table = database.getReference("User");



            btnSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Common.isConnectedToInternet(getBaseContext())) {
                        final ProgressDialog mDialog = new ProgressDialog(SignUp.this);
                        mDialog.setMessage("Please Wait");
                        mDialog.show();


                        user_table.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                    mDialog.dismiss();
                                    Toast.makeText(SignUp.this, "oops!! User already exists", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SignUp.this, SignIn.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    mDialog.dismiss();
                                    String password = edtPassword.getText().toString();
                                    String name = edtName.getText().toString();
                                    String phone = edtPhone.getText().toString();
                                    if (password.isEmpty() || password.length() < 6 || password.length() > 6||name.isEmpty()||phone.isEmpty() || phone.length() < 10 || phone.length() > 12) {
                                        edtPassword.setError("Pin must be exactly 6 numbers!");
                                    } else {

                                        User user = new User(edtName.getText().toString(), edtPassword.getText().toString());
                                        user_table.child(edtPhone.getText().toString()).setValue(user);
                                        Toast.makeText(SignUp.this, "Create Pin!! ", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SignUp.this, SignIn.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    } else {
                        Toast.makeText(SignUp.this, "Please connect internet.!", Toast.LENGTH_SHORT).show();
                        return;

                    }
                }
            });
        }
    }