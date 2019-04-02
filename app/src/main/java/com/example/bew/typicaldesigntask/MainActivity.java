package com.example.bew.typicaldesigntask;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private MapView mapView ;




    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private Boolean mLocationPermissionsGranted = false ;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    LatLng locationc ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        getLocationPermission();


         EditText fullname = findViewById(R.id.fullname) ;
         EditText email = findViewById(R.id.email) ;
         EditText pass  =findViewById(R.id.pass1) ;
         TextView existingacc = findViewById(R.id.existingaccount);



         String passwordtxt = pass.getText().toString();

        final TextView alertname = findViewById(R.id.namealert) ;
        final TextView alertmail = findViewById(R.id.emailalert) ;
        final TextView alertpass = findViewById(R.id.passwordalert) ;
        alertname.setVisibility(View.GONE);
        alertmail.setVisibility(View.GONE);
        alertpass.setVisibility(View.GONE);

        Button regbtn =findViewById(R.id.regbtn) ;
        regbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                EditText fullname = findViewById(R.id.fullname) ;
                EditText email = findViewById(R.id.email) ;
                EditText pass  =findViewById(R.id.pass1) ;
                ProgressBar progressBar = findViewById(R.id.top_progress_bar);

                String emailtxt = email.getText().toString() ;
                String nametxt = fullname.getText().toString();
                String passwordtxt = pass.getText().toString();

                alertname.setVisibility(View.GONE);
                alertmail.setVisibility(View.GONE);
                alertpass.setVisibility(View.GONE);

                if(TextUtils.isEmpty(fullname.getText().toString()) ||TextUtils.isEmpty(email.getText().toString())||TextUtils.isEmpty(pass.getText().toString())){

                if (TextUtils.isEmpty(fullname.getText().toString())) {


                    alertname.setVisibility(View.VISIBLE);

                }
                if (TextUtils.isEmpty(email.getText().toString())) {


                    alertmail.setVisibility(View.VISIBLE);

                }
                if (TextUtils.isEmpty(pass.getText().toString())) {

                    alertpass.setVisibility(View.VISIBLE);
                }

                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setMax(100);
                    progressBar.setProgress(1);
                    regesteruser(fullname.getText().toString(), email.getText().toString(), pass.getText().toString(), locationc);
                }

            }


        });

        existingacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i3 = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(i3);
                setContentView(R.layout.login_activity);

            }
        });






    }
    private void regesteruser (final String name , final String email , String password , final LatLng location) {


        final FirebaseAuth mAuth  ;
        mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        ProgressBar progressBar = findViewById(R.id.top_progress_bar);
                        progressBar.setVisibility(View.VISIBLE);
                        progressBar.setMax(100);
                        progressBar.setProgress(1);
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            DatabaseReference mydatabase =FirebaseDatabase.getInstance().getReference().child("users");
                            DatabaseReference currentuser = mydatabase.child(mAuth.getCurrentUser().getUid());
                            currentuser.child("name").setValue(name);
                            currentuser.child("e_mail").setValue(email);
                            currentuser.child("Location").setValue(location);
                            Log.w(TAG, "data updated" , task.getException());
                            progressBar.setVisibility(View.GONE);
                            double lat = location.latitude ;
                            double lng =location.longitude ;


                            Intent i = new Intent(getApplicationContext(),LoginActivity.class);
                            startActivity(i);
                            setContentView(R.layout.login_activity);



                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        }
                    }
                });

    }
    private void locationgetter (Location l){


        locationc = new LatLng(l.getLatitude() ,l.getLongitude());

    }

    private void locationgetter (LatLng latLng)
    {
        locationc =latLng ;

    }
    // map functions
    private void init(){
        Log.d(TAG, "init: initializing");
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                MarkerOptions markerOptions= new MarkerOptions() ;
                markerOptions.position(latLng);
                mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.addMarker(markerOptions);
                locationgetter(latLng);

        }
        });









        //button that finds your location

        Button FindMyLocatiion = findViewById(R.id.location);


        FindMyLocatiion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked gps icon");

                getDeviceLocation();
            }
        });

        hideSoftKeyboard();
    }
    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM,
                                    "MyLocation");
                            locationgetter(currentLocation);

                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MainActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }
    private void moveCamera(LatLng latLng, float zoom, String title){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );


        if(!title.equals("MyLocation")){
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }else
        {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        }

        hideSoftKeyboard();
    }
    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                if (mLocationPermissionsGranted) {
                    getDeviceLocation();

                    if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this,
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
