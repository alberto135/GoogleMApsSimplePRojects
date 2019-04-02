package com.example.bew.typicaldesigntask;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class LoginActivity extends AppCompatActivity {
   //maps declerations
    private GoogleMap mMap;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private Boolean mLocationPermissionsGranted = false ;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    // ends here

    private final String TAG = "LoginActivity";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        getSupportActionBar().hide();

        Button login =findViewById(R.id.loginbtn);



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText email = findViewById(R.id.emaileditview);
                EditText password = findViewById(R.id.passwordEdittext) ;

                TextView alertmail =findViewById(R.id.emailalert2) ;
                TextView alertpass =findViewById(R.id.passwordalert2) ;
                alertmail.setVisibility(View.GONE);
                alertpass.setVisibility(View.GONE);


                if( TextUtils.isEmpty(email.getText().toString())||TextUtils.isEmpty(password.getText().toString())){


                    if (TextUtils.isEmpty(email.getText().toString())) {


                        alertmail.setVisibility(View.VISIBLE);

                    }
                    if (TextUtils.isEmpty(password.getText().toString())) {

                        alertpass.setVisibility(View.VISIBLE);
                    }

                } else {

                    Loginuser(email.getText().toString(),password.getText().toString());
                    final ProgressBar progressBar2 = findViewById(R.id.loginprogressbar);
                    progressBar2.setVisibility(View.VISIBLE);
                    progressBar2.setMax(100);
                    progressBar2.setProgress(1);

                }
            }
        });
    }
    private void Loginuser (final String email , String Password) {
        final FirebaseAuth mAuth  ;
        mAuth = FirebaseAuth.getInstance();
        final Bundle b = new Bundle();

        mAuth.signInWithEmailAndPassword(email, Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    String name ;
                    String email ;
                    LatLng latLng ;
                    double lat;
                    double lng ;
                    final static float zoom = 15 ;

                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            setContentView(R.layout.profile_layout);
                            getLocationPermission();
                            // Sign in success, update UI with the signed-in user's information

                            Log.d(TAG, "signInWithEmail:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
                            DatabaseReference currentuser= databaseReference.child(mAuth.getCurrentUser().getUid());
                            DatabaseReference nameref = currentuser.child("name");
                            DatabaseReference emailref = currentuser.child("e_mail");
                            final DatabaseReference locref =currentuser.child("Location");

                            nameref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    name = dataSnapshot.getValue(String.class);
                                    Log.w(TAG, "name is "+name, task.getException());
                                    TextView user_name = findViewById(R.id.user_name) ;
                                    user_name.setText(name);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            emailref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    email = dataSnapshot.getValue(String.class);
                                    Log.w(TAG, "name is "+email, task.getException());
                                    TextView user_email= findViewById(R.id.user_email);
                                    user_email.setText(email);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            locref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                    lat = dataSnapshot.child("latitude").getValue(double.class);
                                    lng = dataSnapshot.child("longitude").getValue(double.class);
                                    latLng = new LatLng(lat,lng);
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
                                    MarkerOptions markerOptions= new MarkerOptions() ;
                                    markerOptions.position(latLng);
                                    mMap.addMarker(markerOptions);
                                    Log.w(TAG, "Lat is "+latLng.latitude, task.getException());
                                    Log.w(TAG, "lng is  "+latLng.longitude, task.getException());


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.
                            TextView wrongpasstxt = findViewById(R.id.wrongpassaletr);
                            wrongpasstxt.setVisibility(View.VISIBLE);
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }


                    }
                });
    }



    //map function

    private void init(){
        Log.d(TAG, "init: initializing");



    }

    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapProfile);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                if (mLocationPermissionsGranted) {

                    if (ActivityCompat.checkSelfPermission(LoginActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LoginActivity.this,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);

                    init();
                }

            }
        });

    }
    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }
    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

}
