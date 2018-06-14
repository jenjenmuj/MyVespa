package cz.uhk.fim.umte.myvespa;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import cz.uhk.fim.umte.myvespa.Model.MyPlaces;
import cz.uhk.fim.umte.myvespa.Model.Results;
import cz.uhk.fim.umte.myvespa.Remote.IGoogleAPIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
         {

    private static final int MY_PERMISSION_CODE = 1000;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private double latitude, longitude;
    private Location mLastLocation;
    private Marker mMarker;
    private LocationRequest mLocationRequest;

    IGoogleAPIService mService;

    MyPlaces currentPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Init service
        mService = Common.getGoogleApiService();

        // Request Runtime permission
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.action_vespa:
                        nearByPlace("store", "Vespa" );
                        break;
                    case R.id.action_rent:
                        nearByPlace("bicycle_store", "Vespa+for+rent");
                        break;
                    case R.id.action_service:
                        nearByPlace("car_repair", "Moto+Service");
                        break;
                        default:
                            break;
                }
                return true;
            }
        });
    }

    private void nearByPlace(final String placeType, final String keyword) {
        mMap.clear();
        String url = getUrl(latitude, longitude, placeType, keyword);

        mService.getNearByPlaces(url)
                .enqueue(new Callback<MyPlaces>() {

                    @Override
                    public void onResponse(Call<MyPlaces> call, Response<MyPlaces> response) {

                        currentPlace = response.body(); ///Remember current place

                        if (response.isSuccessful()) {
                            for (int i=0; i < response.body().getResults().length; i++) {
                                MarkerOptions markerOptions = new MarkerOptions();
                                Results googlePlace = response.body().getResults() [i];
                                double lat = Double.parseDouble(googlePlace.getGeometry().getLocation().getLat());
                                double lng = Double.parseDouble(googlePlace.getGeometry().getLocation().getLng());
                                String placeName = googlePlace.getName();
                                String vicinity = googlePlace.getVicinity();
                                LatLng latLng = new LatLng(lat, lng);
                                markerOptions.position(latLng);
                                markerOptions.title(placeName);
                                if (placeType.equals("car_repair"))
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_service));
                                else if (placeType.equals("bicycle_store"))
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_time));
                                else if (placeType.equals("store"))
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_moto));

                                else
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                                markerOptions.snippet(String.valueOf(i)); // Assign index for marker

                                // Add markers to Map
                                mMap.addMarker(markerOptions);

                                // Move camera
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MyPlaces> call, Throwable t) {

                    }
                });
    }

    private String getUrl(double latitude, double longitude, String placeType, String keyword) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + 10000);
        googlePlacesUrl.append("&type=" + placeType);
        googlePlacesUrl.append("&keyword=" + keyword);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + getResources().getString(R.string.browser_key));
        Log.d("getUrl", googlePlacesUrl.toString());
        return googlePlacesUrl.toString();
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, MY_PERMISSION_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, MY_PERMISSION_CODE);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null)
                            buildGoogleApiClient();
                        mMap.setMyLocationEnabled(true);
                    }
                } else
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Init Google Play Services
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        // Make eventclick on Marker
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // When user select marker, just get Result of Place and assign to static variable
                Common.currentResult = currentPlace.getResults()[Integer.parseInt(marker.getSnippet())];
                // Start new activity
                startActivity(new Intent(MapsActivity.this, ViewPlace.class));
                return false;
            }
        });
    }

    private synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
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
        mLastLocation = location;
        if (mMarker != null) {
            mMarker.remove();
        }

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        LatLng latLng = new LatLng(latitude, longitude);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title("Your position")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mMarker = mMap.addMarker(markerOptions);

        //Move Camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //if (mGoogleApiClient != null) {
        //    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        //}
    }

}
