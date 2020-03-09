package e.user.madiambu;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import e.user.madiambu.Common.Common;
import e.user.madiambu.Common.Common_d;
import e.user.madiambu.Helper.CustomInfoWindow;
import e.user.madiambu.Model.Data;
import e.user.madiambu.Model.MySingleton;
import e.user.madiambu.Model.Sender;
import e.user.madiambu.Model.Token;
import e.user.madiambu.Remote.IFCMService;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class GenAmbulanceFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    SupportMapFragment mapFragment;

    //Location.....
    private GoogleMap mMap;

    private static final int MY_PERMISSION_REQUEST_CODE=7000;
    private static final int PLAY_SERIVICES_RES_REQUEST=7001;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location lastLocation;

    private static int UPDATE_INTERVAL=5000;
    private static int FATEST_INTERVAL=3000;
    private static int DISPLACEMENT=10;
    private AutocompleteSupportFragment places_src,places_des;

    DatabaseReference riders;
    GeoFire geoFire;
    Marker mUserMarker,markerDestination;
    long isSent = 0;

    private FragmentActivity myContext;

    //BottomSheet...
    ImageView imgExpandable;
    BottomSheetRiderFragment mBottomSheet;
    Button btnPickupRequest;
    //find driver..
    boolean isDriverFound = false;
    String driverId="";
    int radius=1;//1km
    int distance=1;//3km
    private static final int LIMIT=3;

    IFCMService mService;

    //presence system..
    DatabaseReference driverAvaliable;

    //for place location and place destination
    String mPlaceLocation,mPlaceDestination;


    //migrate
    //migrate...
    PlacesClient placesClient;
    List<Place.Field> placeField= Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ID,com.google.android.libraries.places.api.model.Place.Field.LAT_LNG
            , com.google.android.libraries.places.api.model.Place.Field.NAME, com.google.android.libraries.places.api.model.Place.Field.ADDRESS);





    private OnFragmentInteractionListener mListener;

    public GenAmbulanceFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static GenAmbulanceFragment newInstance(String param1, String param2) {
        GenAmbulanceFragment fragment = new GenAmbulanceFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_gen_ambulance, container, false);
        initPlaces();

        mService=Common_d.getFCMService();


        SupportMapFragment mapFragment=(SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.googleMap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        //init View for bottomsheet...
        imgExpandable=(ImageView)view.findViewById(R.id.imgExpendable);


        setUpLocation();
        btnPickupRequest=(Button)view.findViewById(R.id.btnPickupRequest);
        btnPickupRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isDriverFound) {
                    requestPickupHere(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    Log.d("Mytag", "not found");
                } else {
                    sendRequestToDriver(driverId);
                    Log.d("Mytag", "found");
                }
            }
        });



        updateFirebaseToken();
        //search place...
        places_src=(AutocompleteSupportFragment)getChildFragmentManager().findFragmentById(R.id.place_src);
        places_src.setPlaceFields(placeField);
        places_des=(AutocompleteSupportFragment)getChildFragmentManager().findFragmentById(R.id.place_des);
        places_des.setPlaceFields(placeField);

        places_src.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {


                try {

                    mPlaceLocation = place.getAddress().toString();

                    //remove old marker
                    mMap.clear();


                    //add new marker on new location..
                    mUserMarker = mMap.addMarker(new MarkerOptions().position(place.getLatLng())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)).title("Pickup hare"));


                    //Move camera from this position..

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15.0f));


                }catch (Exception e)

                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });

        places_des.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mPlaceDestination=place.getAddress().toString();

                //add new marker on new location..

                mUserMarker=mMap.addMarker(new MarkerOptions().position(Objects.requireNonNull(place.getLatLng()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_marker)).title("Destination"));

                //Move camera from this position..
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(),15.0f));

                //show information in bottom..
                BottomSheetRiderFragment mBottom=BottomSheetRiderFragment.newInstance(mPlaceLocation,mPlaceDestination,false);
                mBottom.show(myContext.getSupportFragmentManager(),mBottom.getTag());

            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });


        return view;


    }

    private void updateFirebaseToken() {

        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference tokens=db.getReference(Common_d.token_tbl);

        Token token=new Token(FirebaseInstanceId.getInstance().getToken());
        tokens.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(token);

    }

    private void sendRequestToDriver(String driverId) {

        final DatabaseReference tokens = FirebaseDatabase.getInstance().getReference(Common_d.token_tbl);

        Log.d("inside", driverId);

        if (!driverId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            tokens.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                    final long total = dataSnapshot.getChildrenCount();

                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {

                        if (!Objects.equals(postSnapShot.getKey(), FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            Token token = postSnapShot.getValue(Token.class); //fet token object from database with key..

                            assert token != null;
                            Log.d("Mytag", token.getToken());
                            //make raw payload..convert LatLng to json
                            //String json_lat_lng = new Gson().toJson(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
                            String riderToken= FirebaseInstanceId.getInstance().getToken();

                            Data data = new Data(riderToken, lastLocation.getLongitude(), lastLocation.getLatitude(),"driver"); //send it to driver app and we will deserialize it again..

                            Sender sender = new Sender(data, token.getToken());//send this data to token ..
                            //Log.d("Test", sender.getNotification().body);

                            try {
                                JSONObject senderJsonObj = new JSONObject(new Gson().toJson(sender));

                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", senderJsonObj, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("JSON_RESPONSE", "onResponse: " + response.toString());
                                        isSent++;
                                        if (total==isSent){
                                            Toast.makeText(myContext, "Request sent to all available drivers", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d("JSON_ERROR", "onResponse: " + error.toString());
                                        if (total==isSent){
                                            Toast.makeText(myContext, "Request Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }) {
                                    @Override
                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                        Map<String, String> headers = new HashMap<>();
                                        headers.put("Content-Type", "application/json");
                                        headers.put("Authorization", "key=AAAAJwI3OcE:APA91bEmj2ntjHFSkzpcgttGOPr2DvCvHR0dIkrnQyvVdJ6rYRa51BJhYSXxaPcRLn4QB-Z0VxOasUzx7_XOnbqi1lK3-hDwUbARWNs2zk1-K_EJKB8hQd5tthYn7P55xEPqlsGW-lCR");
                                        return headers;
                                    }
                                };


                                MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsonObjectRequest);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }



//                            mService.sendMessage(sender)
//                                    .enqueue(new Callback<FCMResponse>() {
//                                        @Override
//                                        public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
//
//                                            Log.d("Test", response.body().toString());
//
//                                            if (response.body() != null){
//                                                Toast.makeText(Home.this, "Request sent!", Toast.LENGTH_SHORT).show();
//
//                                            }
//                                            else{
//                                                Toast.makeText(Home.this, "Failed", Toast.LENGTH_SHORT).show();
//
//                                            }
//
//                                        }
//
//                                        @Override
//                                        public void onFailure(Call<FCMResponse> call, Throwable t) {
//
//                                            Log.d("ERROR", t.getMessage());
//
//                                        }
//                                    });


                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }


    private void requestPickupHere(String uid) {
            DatabaseReference dbRequest= FirebaseDatabase.getInstance().getReference(Common.pickup_request_tbl);
            GeoFire mGeoFire=new GeoFire(dbRequest);
            mGeoFire.setLocation(uid,new GeoLocation(lastLocation.getLatitude(),lastLocation.getLongitude()));
            if(mUserMarker.isVisible())
                mUserMarker.remove();
            //addmarker...
            mUserMarker= mMap.addMarker(new MarkerOptions()
                    .title("Pickup Here").snippet("").position(new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
            mUserMarker.showInfoWindow();

            btnPickupRequest.setText("Getting Yours Drivers...");
            findDriver();

        }

    private void findDriver() {
        DatabaseReference drivers=FirebaseDatabase.getInstance().getReference(Common.driver_tbl);
        GeoFire gfDriver=new GeoFire(drivers);

        GeoQuery geoQuery=gfDriver.queryAtLocation(new GeoLocation(lastLocation.getLatitude(),lastLocation.getLongitude()),
                radius);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                //if found..
                if(!isDriverFound)
                {
                    isDriverFound=true;
                    driverId=key;
                    btnPickupRequest.setText("Call Driver");
                    //Toast.makeText(GenAmbulanceFragment.this, ""+key, Toast.LENGTH_SHORT).show();
                    //Toast.makeText(myContext, ""+key, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

                //if still not found driver ..increase distance...
                if(!isDriverFound && radius < LIMIT)
                {
                    radius++;
                    findDriver();
                }

                else {
                    Toast.makeText(myContext, "Driver Found", Toast.LENGTH_SHORT).show();
                    btnPickupRequest.setText("pickup request");
                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void initPlaces() {

        Places.initialize(getActivity().getApplicationContext(),getString(R.string.google_direction_api));
        placesClient=Places.createClient(getActivity());
        if (!Places.isInitialized()) {
            Places.initialize(getActivity().getApplicationContext().getApplicationContext(),getString(R.string.google_direction_api));
        }
    }

    //we request run time permission ,we need override OnRequestPermission Result..
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case MY_PERMISSION_REQUEST_CODE:
                if(grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED)
                {
                    if(checkPlayServices())
                    {
                        buildGoogleApiClient();
                        createLocationRequest();
                        displayLocation();
                    }
                }

                break;
        }
    }

    private void setUpLocation() {
        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) !=PackageManager.PERMISSION_GRANTED)
        {
            //Request Runtime permission...
            ActivityCompat.requestPermissions(getActivity(),new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            },MY_PERMISSION_REQUEST_CODE);
        }

        else
        {
            if(checkPlayServices())
            {
                buildGoogleApiClient();
                createLocationRequest();
                displayLocation();
            }
        }
    }

    private void displayLocation() {

        try {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        lastLocation= LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        Common_d.lastLocation = lastLocation;

        if(lastLocation!=null)
        {

            //presence system..
            driverAvaliable=FirebaseDatabase.getInstance().getReference(Common.driver_tbl);
            driverAvaliable.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //if has any driver Table , we will reload all driver avaliable
                    loadAllAvailableDriver(new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            final double latitude=lastLocation.getLatitude();
            final double longitude=lastLocation.getLongitude();



            loadAllAvailableDriver(new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude()));


            Log.d("MOBIN",String.format("Your location was changed: %f/%f",latitude,longitude));
        }
        else

            Log.d("ERROR","Cannot get your location");


    }

    private void loadAllAvailableDriver(final LatLng location) {

        //add marker
        //marker remove already
        mMap.clear();
        mUserMarker=mMap.addMarker(new MarkerOptions()
                .position(location)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))

                .title("YOUR LOCATION"));
        //Move camera from this position..
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location,15.0f));


        //Load all avaliable driver between 3 km distance..
        DatabaseReference driverLocation=FirebaseDatabase.getInstance().getReference(Common.driver_tbl);
        GeoFire gf=new GeoFire(driverLocation);
        GeoQuery geoQuery=gf.queryAtLocation(new GeoLocation(location.latitude, location.longitude),
                distance);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, final GeoLocation location) {
                //use key to get email from driver users
                //Table user is table when driver register account and update information
                //Just Open your Driver to check this table name..
                FirebaseDatabase.getInstance().getReference("USERS/DRIVER_USER").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //because rider and user model has same properties..
                        //so we can use rider model to get users here..


                        //add driver to map...
                       try {
                           mMap.addMarker(new MarkerOptions()
                                   .position(new LatLng(location.latitude, location.longitude))
                                   .flat(true)
                                   .title(dataSnapshot.child("NAME").getValue().toString())
                                   .snippet("Phone:" + dataSnapshot.child("MOBILE_NO").getValue().toString())
                                   .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
                       }catch (Exception e)
                       {
                           e.printStackTrace();
                       }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if(distance<=LIMIT)//find driver in 3km...
                {

                    distance++;
                    loadAllAvailableDriver(location);

                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }

    private void createLocationRequest() {

        mLocationRequest=new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);

    }

    private void buildGoogleApiClient() {
        mGoogleApiClient=new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect(); //Connected Google api client.....
    }

    private boolean checkPlayServices() {
        int resultCode= GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if(resultCode!= ConnectionResult.SUCCESS)
        {
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                GooglePlayServicesUtil.getErrorDialog(resultCode,getActivity(),PLAY_SERIVICES_RES_REQUEST).show();
            else {
                Toast.makeText(myContext, "This device is not supported ", Toast.LENGTH_SHORT).show();
            }

            return false;

        }

        return true;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
           // throw new RuntimeException(context.toString()
                   // + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        displayLocation();
        startLocationUpdate();

    }

    private void startLocationUpdate() {
       try {
           if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                   ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
               return;
           }
       }catch (Exception e)
       {
           e.printStackTrace();
       }

       try {
           LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,  this);
       }catch (Exception e)
       {
           e.printStackTrace();
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

        lastLocation=location;
        displayLocation();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {



        try {
            boolean isSuccess=googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(),R.raw.uber_style_map));

            if(!isSuccess)
                Log.d("error","map style load failed");
        }catch (Resources.NotFoundException ex)
        {
            ex.printStackTrace();
        }




        mMap=googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.setInfoWindowAdapter(new CustomInfoWindow(getActivity()));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                if(markerDestination!=null)
                    markerDestination.remove();
                markerDestination=mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_marker))
                        .position(latLng).title("Destination"));

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15.0f));

                //show in bottom..

                BottomSheetRiderFragment mBottom=BottomSheetRiderFragment.newInstance(String.format("%f,%f",lastLocation.getLatitude(),lastLocation.getLongitude()),
                        String.format("%f,%f",latLng.latitude,latLng.longitude),true);
                mBottom.show(myContext.getSupportFragmentManager(),mBottom.getTag());




            }
        });

    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }
}
