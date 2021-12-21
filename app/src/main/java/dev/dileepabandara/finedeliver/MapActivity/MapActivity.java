/*
   --------------------------------------
      Developed by
      Dileepa Bandara
      https://dileepabandara.github.io
      contact.dileepabandara@gmail.com
      ©dileepabandara.dev
      2021
   --------------------------------------
*/

package dev.dileepabandara.finedeliver.MapActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import dev.dileepabandara.finedeliver.R;
import dev.dileepabandara.finedeliver.User.UserItems.UserPostItem;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private Boolean IS_LOCATION_PERMISSION_GRANTED = false;
    private GoogleMap mMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    SupportMapFragment mapFragment;
    private static final float DEFAULT_ZOOM = 15f;

    private EditText txtSearchMapLocation;
    ImageView imgFocusMyLocation;
    ImageView imgConfirm;
    ImageButton btnSearchMapIcon;
    TextView textView14;

    String[] origin = {"0"};
    String[] destination = {"0"};

    //  --------------------------------------- On create the activity ---------------------------------------
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_map);

        txtSearchMapLocation = findViewById(R.id.txtSearchMapLocation);
        btnSearchMapIcon = findViewById(R.id.btnSearchMapIcon);
        textView14 = findViewById(R.id.textView14);
        imgFocusMyLocation = findViewById(R.id.imgFocusMyLocation);
        imgConfirm = findViewById(R.id.imgConfirm);

        getLocationPermission();
        Log.d(TAG, "initializeMap: Map is initialize");
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(MapActivity.this);

        // Selected location ImageButton
        btnSearchMapIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //geoLocate();
            }
        });

        //  Focus on my location
        imgFocusMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceCurrentLocation();
            }
        });

        //  Confirm deliver locations
        imgConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
                builder.setTitle("Confirm origin and destination");
                builder.setMessage("Origin: \n" + Arrays.toString(origin) + "\n\n\nDestination: \n" + Arrays.toString(destination));
                builder.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MapActivity.this, "Your locations added", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(MapActivity.this, UserPostItem.class);
                        intent.putExtra("origin", origin);
                        intent.putExtra("destination", destination);
                        setResult(RESULT_OK, intent);
                        finish();

                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();


            }
        });
    }


    //  --------------------------------------- On map ready ---------------------------------------
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: Map is ready");
        mMap = googleMap;

        if (IS_LOCATION_PERMISSION_GRANTED) {
            getDeviceCurrentLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            searchForLocation();
            longTapForSelectLocation();
        }

    }


    //  --------------------------------------- Grant location runtime permission ---------------------------------------
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permission");
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                IS_LOCATION_PERMISSION_GRANTED = true;
                getDeviceCurrentLocation();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d(TAG, "onRequestPermissionsResult: called success");
        IS_LOCATION_PERMISSION_GRANTED = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            IS_LOCATION_PERMISSION_GRANTED = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    IS_LOCATION_PERMISSION_GRANTED = true;
                    getDeviceCurrentLocation();
                }
            }
        }

    }


    //  --------------------------------------- Search location EditText ---------------------------------------
    private void searchForLocation() {
        Log.d(TAG, "init: initializing");
        Toast.makeText(this, "Let's find a location", Toast.LENGTH_SHORT).show();
        //autocompletePLace();
        txtSearchMapLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER) {

                    Toast.makeText(MapActivity.this, "Finding for location", Toast.LENGTH_SHORT).show();
                    //Methods for searching
                    geoLocate();
                }

                return false;
            }
        });

        hiddenSoftKeyboard();
    }


    //This won't run until turn on google cloud billing service
    //  --------------------------------------- Autocomplete Place API ---------------------------------------
    private void autocompletePLace() {
        // Initialize places
        Places.initialize(getApplicationContext(), "AIzaSyDfC1iOeT0r-PBpWobPfjamHanrwSakuiQ");

        //Set onClickListener
        txtSearchMapLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Initialize place field list
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);

                // Create intent
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(MapActivity.this);

                // Start activity for result
                startActivityForResult(intent, 1523);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && requestCode == RESULT_OK) {
            // When success initialize place
            Place place = Autocomplete.getPlaceFromIntent(data);

            // Set address
            txtSearchMapLocation.setText(place.getAddress());
            Toast.makeText(this, "Address: " + place.getAddress(), Toast.LENGTH_SHORT).show();
            // Set locally name
            Toast.makeText(this, "Locally Name: %s" + place.getName(), Toast.LENGTH_SHORT).show();
            // Set lat and lon
            Toast.makeText(this, "LatLan: " + place.getLatLng(), Toast.LENGTH_SHORT).show();

        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(this, "" + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "" + status.getStatusMessage());
        }

    }

    //  --------------------------------------- Search entered location ---------------------------------------
    private void geoLocate() {
        Log.d(TAG, "geoLocate: geoLocating");

        String searchString = txtSearchMapLocation.getText().toString();

        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (Exception e) {
            Log.d(TAG, "geoLocate: IOException" + e.getMessage());
        }
        if (list.size() > 0) {
            Address address = list.get(0);
            Log.d(TAG, "geoLocate: foundLocation" + address.toString());
            Toast.makeText(this, "Found it", Toast.LENGTH_SHORT).show();

            textView14.setText(address.toString());
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
        }
    }

    //  --------------------------------------- Get long tapped location ---------------------------------------
    private void longTapForSelectLocation() {
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                try {
                    Log.d(TAG, "geoLocate: OnMapLongClickListener: " + latLng.toString());
                    List<Address> list = new Geocoder(MapActivity.this).getFromLocation(latLng.latitude, latLng.longitude, 1);

                    if (list.size() > 0) {
                        Address oneAddress = list.get(0);
                        String sAddress = oneAddress.getAddressLine(0);
                        String sCity = oneAddress.getLocality();
                        String sProvince = oneAddress.getAdminArea();
                        String sLatitude = String.valueOf(oneAddress.getLatitude());
                        String sLongitude = String.valueOf(oneAddress.getLongitude());

                        MarkerOptions options;
                        //mMap.clear();
                        options = new MarkerOptions()
                                .position(latLng)
                                .title(sAddress)
                                .icon(BitmapDescriptorFactory.fromBitmap(customMapMarkerSelected()));
                        mMap.addMarker(options);

                        showOptionForLocation(sAddress, sCity, sProvince, sLatitude, sLongitude);
                    }

                } catch (Exception e) {
                    Toast.makeText(MapActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //  --------------------------------------- Show options on touch map marker ---------------------------------------
    private void showOptionForLocation(String address, String city, String province, String latitude, String longitude) {

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                // AlertDialog for select location
                AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
                builder.setTitle("Select location");
                builder.setMessage("What would be this location?");
                builder.setPositiveButton("Origin", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MapActivity.this, "Origin location selected", Toast.LENGTH_SHORT).show();
                        origin = new String[]{address, city, province, latitude, longitude};
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Destination", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MapActivity.this, "Destination location selected", Toast.LENGTH_SHORT).show();
                        destination = new String[]{address, city, province, latitude, longitude};
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });

    }

    //  --------------------------------------- Get current location of thw device ---------------------------------------
    private void getDeviceCurrentLocation() {
        Log.d(TAG, "getDeviceCurrentLocation: getting device current location");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (IS_LOCATION_PERMISSION_GRANTED) {
                Task<Location> task = fusedLocationProviderClient.getLastLocation();
                task.addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Log.d(TAG, "onSuccess: Location Found");

                            //Sync Map
                            mapFragment.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(GoogleMap googleMap) {
                                    //Initialize lat lng
                                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                    moveCamera(latLng, DEFAULT_ZOOM, "My Location");
                                    Toast.makeText(MapActivity.this, "Found you", Toast.LENGTH_SHORT).show();

                                    try {
                                        List<Address> list = new Geocoder(MapActivity.this).getFromLocation(latLng.latitude, latLng.longitude, 1);
                                        showOptionForLocation(list.get(0).getAddressLine(0),
                                                list.get(0).getLocality(), list.get(0).getAdminArea(),
                                                String.valueOf(location.getLatitude()),
                                                String.valueOf(location.getLongitude()));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        } else {
                            Log.d(TAG, "onComplete: Location Null");
                            Toast.makeText(MapActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        } catch (SecurityException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }


    //  --------------------------------------- Move camera ---------------------------------------
    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lon: " + latLng.latitude);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        //  CreateMarker Option

        if (title.equals("My Location")) {
            MarkerOptions options;
            //mMap.clear();
            options = new MarkerOptions()
                    .position(latLng)
                    .title(title)
                    .icon(BitmapDescriptorFactory.fromBitmap(customMapMarkerDevice()));
            mMap.addMarker(options);
        }


        hiddenSoftKeyboard();


    }

    //Custom Map Marker png image
    private Bitmap customMapMarkerDevice() {
        int height = 80;
        int width = 80;
        BitmapDrawable bitmapDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.img_sign_up_phone_verify);
        Bitmap b = bitmapDraw.getBitmap();
        final Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        return smallMarker;
    }

    //Custom Map Marker png image
    private Bitmap customMapMarkerSelected() {
        int height = 80;
        int width = 80;
        BitmapDrawable bitmapDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.findedeliver_logo_icon_50p);
        Bitmap b = bitmapDraw.getBitmap();
        final Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        return smallMarker;
    }

    //Custom Map Marker vector assert image
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    //  --------------------------------------- Hidden Soft Keyboard ---------------------------------------
    private void hiddenSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


}
