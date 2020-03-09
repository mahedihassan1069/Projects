package e.user.madiambu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import e.user.madiambu.Common.Common;
import e.user.madiambu.Common.Common_d;
import e.user.madiambu.Helper.DirectionJSONParser;
import e.user.madiambu.Remote.IGoogleAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class DriverTracking extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;



    //play service

    private static final int PLAY_SERIVICES_RES_REQUEST=7001;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;


    private static int UPDATE_INTERVAL=5000;
    private static int FATEST_INTERVAL=3000;
    private static int DISPLACEMENT=10;

    double riderLat,riderLng;

    private Circle riderMarker;
    private Marker driverMarker;
    private Polyline direction;

    IGoogleAPI mService;

    Button btnStartTrip;
    Location pickupLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_tracking);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(getIntent()!=null)
        {

            riderLat=getIntent().getDoubleExtra("lat",-1.0);
            riderLng=getIntent().getDoubleExtra("lng",-1.0);

        }

        mService= Common_d.getGoogleAPI();

        setUpLocation();

        btnStartTrip=(Button) findViewById(R.id.btnStartTrip);

        btnStartTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnStartTrip.getText().equals("START TRIP"))
                {
                    pickupLocation=Common_d.lastLocation;
                    btnStartTrip.setText("DROP OFF HERE");
                }

                else if(btnStartTrip.getText().equals("DROP OFF HERE"))
                {
                    calculateCashFee(pickupLocation,Common_d.lastLocation);
                }
            }
        });
    }

    private void calculateCashFee(final Location pickupLocation, Location lastLocation) {

        String requestApi= null;
        try {
            requestApi="https://maps.googleapis.com/maps/api/directions/json?"+
                    "mode=driving&"+
                    "transit_routing_preference=less_driving&"+
                    "origin="+pickupLocation.getLatitude()+","+pickupLocation.getLongitude()+"&"+
                    "destination="+lastLocation.getLatitude()+","+lastLocation.getLongitude()+"&"+
                    "key="+getResources().getString(R.string.google_direction_api);



            mService.getPath(requestApi)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                            try {

                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                JSONArray routes = jsonObject.getJSONArray("routes");

                                //after get routes ,just get first element of route..
                                JSONObject object = routes.getJSONObject(0);
                                //after getting first element ..we need get array with name..
                                JSONArray legs = object.getJSONArray("legs");
                                //and get first element of kegs array
                                JSONObject legsObject = legs.getJSONObject(0);

                                JSONObject distance = legsObject.getJSONObject("distance");
                                String distance_txt = distance.getString("text");

                                //use regex to extract double from string
                                //this regex remove all text not a digit.
                                Double distance_value = Double.parseDouble(distance_txt.replaceAll("[^0-9\\\\.]+", ""));

                                JSONObject time = legsObject.getJSONObject("duration");
                                String time_txt = time.getString("text");

                                Double time_value = Double.parseDouble(time_txt.replaceAll("[^0-9\\\\.]+", ""));

                                //create new activity..
                                Intent intent=new Intent(DriverTracking.this,TripDetail.class);
                                intent.putExtra("start_address",legsObject.getString("start_address"));
                                intent.putExtra("end_address",legsObject.getString("end_address"));
                                intent.putExtra("time",String.valueOf(time_value));
                                intent.putExtra("distance",String.valueOf(distance_value));
                                intent.putExtra("total", Common.formulaPrice(distance_value,time_value));
                                intent.putExtra("location_start",String.format("%f,%f",pickupLocation.getLatitude(),pickupLocation.getLongitude()));
                                intent.putExtra("location_end",String.format("%f,%f",Common_d.lastLocation.getLatitude(),Common_d.lastLocation.getLongitude()));

                                startActivity(intent);
                                finish();


                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(DriverTracking.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

        }catch (Exception e)
        {
            e.printStackTrace();
        }


    }


    private void setUpLocation() {

        if(checkPlayServices())
        {
            buildGoogleApiClient();
            createLocationRequest();
            displayLocation();
        }
    }

    private void createLocationRequest() {
        mLocationRequest=new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void buildGoogleApiClient() {

        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect(); //Connected Google api client.....
    }

    private boolean checkPlayServices() {
        int resultCode= GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resultCode!=ConnectionResult.SUCCESS)
        {
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                GooglePlayServicesUtil.getErrorDialog(resultCode,this,PLAY_SERIVICES_RES_REQUEST).show();
            else {
                Toast.makeText(this, "This device is not supported ", Toast.LENGTH_SHORT).show();
            }

            return false;

        }

        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {
            boolean isSuccess=googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.uber_style_map));

            if(!isSuccess)
                Log.d("error","map style load failed");
        }catch (Resources.NotFoundException ex)
        {
            ex.printStackTrace();
        }

        mMap = googleMap;

        riderMarker=mMap.addCircle(new CircleOptions()
                .center(new LatLng(riderLat,riderLng))
                .radius(10).strokeColor(Color.RED)
                .fillColor(0x220000FF)
                .strokeWidth(5.0f));


    }

    private void displayLocation() {

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) !=PackageManager.PERMISSION_GRANTED)
        {
            return;
        }

        Common_d.lastLocation=LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(Common_d.lastLocation!=null)
        {

            final double latitude=Common_d.lastLocation.getLatitude();
            final double longitude=Common_d.lastLocation.getLongitude();

            if(driverMarker!=null)
                driverMarker.remove();
            driverMarker=mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude))
                    .title("YOU").icon(BitmapDescriptorFactory.defaultMarker()));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 17.0f));

            if(direction!=null)
                direction.remove();//remove old direction..
            getDirection();

        }
        else
        {
            Log.d("ERROR","Cannot get your location");
        }

    }

    private void getDirection() {
        LatLng currentPosition=new LatLng(Common_d.lastLocation.getLatitude(),Common_d.lastLocation.getLongitude());
        String requestApi= null;
        try {
            requestApi="https://maps.googleapis.com/maps/api/directions/json?"+
                    "mode=driving&"+
                    "transit_routing_preference=less_driving&"+
                    "origin="+currentPosition.latitude+","+currentPosition.longitude+"&"+
                    "destination="+riderLat+","+riderLng+"&"+
                    "key="+getResources().getString(R.string.google_direction_api);

            Log.d("MOBIN",requestApi);//print URL for debug;
            mService.getPath(requestApi)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                            try {

                                new ParserTask().execute(response.body().toString());

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(DriverTracking.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void startLocationUpdate() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) !=PackageManager.PERMISSION_GRANTED)
        {
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,  this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdate();
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

        Common_d.lastLocation=location;
        displayLocation();

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private class ParserTask extends AsyncTask<String,Integer,List<List<HashMap<String,String>>>>
    {
        ProgressDialog mDialog=new ProgressDialog(DriverTracking.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.setMessage("Please waiting..");
            mDialog.show();
        }

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes=null;

            try {
                jObject=new JSONObject(strings[0]);

                DirectionJSONParser parser=new DirectionJSONParser();
                routes=parser.parse(jObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            mDialog.dismiss();

            ArrayList points=null;
            PolylineOptions polylineOptions=null;



            for(int i=0;i<lists.size();i++)
            {
                points=new ArrayList();
                polylineOptions=new PolylineOptions();

                List<HashMap<String,String>> path=lists.get(i);

                for(int j=0;j<path.size();j++)
                {
                    HashMap<String,String> point=path.get(j);

                    try {

                        double lat = Double.parseDouble(Objects.requireNonNull(point.get("lat")));
                        double lng = Double.parseDouble(Objects.requireNonNull(point.get("lng")));


                        LatLng position = new LatLng(lat, lng);


                        points.add(position);

                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }



                }

                polylineOptions.addAll(points);
                polylineOptions.width(10);
                polylineOptions.color(Color.RED);
                polylineOptions.geodesic(true);

            }

            direction=mMap.addPolyline(polylineOptions);
        }
    }
}
