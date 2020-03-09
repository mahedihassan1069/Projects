package e.user.madiambu;

import androidx.appcompat.app.AppCompatActivity;
import e.user.madiambu.Common.Common_d;
import e.user.madiambu.Model.Data;
import e.user.madiambu.Model.MySingleton;
import e.user.madiambu.Model.Sender;
import e.user.madiambu.Remote.IFCMService;
import e.user.madiambu.Remote.IGoogleAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CustommerCall extends AppCompatActivity {

    TextView txtTime,txtAddress,txtDistance;

    Button btnAccept,btnCancel;

    MediaPlayer mediaPlayer;

    IGoogleAPI mService;
    IFCMService mFCMService;

    String customerId;
    double lat,lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custommer_call);

        mService=Common_d.getGoogleAPI();
        mFCMService=Common_d.getFCMService();

        //init view..
        txtAddress=(TextView)findViewById(R.id.txtAddress);
        txtDistance=(TextView) findViewById(R.id.txtDistance);
        txtTime=(TextView) findViewById(R.id.txtTime);

        //accept and acncel button..
        btnAccept=(Button)findViewById(R.id.btnAccept);
        btnCancel=(Button)findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(customerId))
                {
                    cancelBokking(customerId);
                }
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(CustommerCall.this,DriverTracking.class);



                //send coustomer location to new activity..
                intent.putExtra("lat",lat);
                intent.putExtra("lng",lng);

                startActivity(intent);
                finish();

            }
        });

        //set ringtone..
        mediaPlayer=MediaPlayer.create(this,R.raw.ringtone);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();


        if(getIntent() != null)
        {
            lat =getIntent().getDoubleExtra("lat",-1.0);
            lng =getIntent().getDoubleExtra("lng",-1.0);
            customerId=getIntent().getStringExtra("riderToken");

            getDirection(lat,lng);
        }

    }

    private void cancelBokking(String customerId) {

        //Token token=new Token(customerId);

        Data data=new Data("Notice","A driver has cancelled your booking","user");

        Sender sender= new Sender(data, customerId);


        try {
            JSONObject senderJsonObj = new JSONObject(new Gson().toJson(sender));

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", senderJsonObj, new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("JSON_RESPONSE", "onResponse: " + response.toString());
                    Toast.makeText(getApplicationContext(), "Request cancelled", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("JSON_ERROR", "onResponse: " + error.toString());

                    //Toast.makeText(Home.this, "Request Failed", Toast.LENGTH_SHORT).show();

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


            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }

       /* mFCMService.sendMessage(sender).enqueue(new Callback<FCMResponse>() {
            @Override
            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                if(response.body().success==1)
                {
                    Toast.makeText(CustommerCall.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<FCMResponse> call, Throwable t) {

            }
        });*/


    }

    private void getDirection(double lat,double lng) {




        String requestApi= null;
        try {
            requestApi="https://maps.googleapis.com/maps/api/directions/json?"+
                    "mode=driving&"+
                    "transit_routing_preference=less_driving&"+
                    "origin="+ Common_d.lastLocation.getLatitude()+","+Common_d.lastLocation.getLongitude()+"&"+
                    "destination="+lat+","+lng+"&"+
                    "key="+getResources().getString(R.string.google_direction_api);

            Log.d("MOBIN",requestApi);//print URL for debug;
            mService.getPath(requestApi)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                            try {
                                JSONObject jsonObject=new JSONObject(response.body().toString());

                                JSONArray routes=jsonObject.getJSONArray("routes");

                                //after get routes ,just get first element of route..
                                JSONObject object=routes.getJSONObject(0);

                                //after getting first element ..we need get array with name..
                                JSONArray legs=object.getJSONArray("legs");
                                //and get first element of kegs array
                                JSONObject legsObject=legs.getJSONObject(0);

                                //now get distance..
                                JSONObject distance=legsObject.getJSONObject("distance");
                                txtDistance.setText(distance.getString("text"));

                                //now get time..
                                JSONObject time=legsObject.getJSONObject("duration");
                                txtTime.setText(time.getString("text"));

                                //now get address..
                                String address=legsObject.getString("end_address");
                                txtAddress.setText(address);

                                Log.d("RECIEVED", address);



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(CustommerCall.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

        }catch (Exception e)
        {
            e.printStackTrace();
            Log.d("PROBLEM", e.toString());

        }

    }

    @Override
    protected void onStop() {
        mediaPlayer.release();
        super.onStop();
    }

    @Override
    protected void onPause() {
        mediaPlayer.release();
        super.onPause();
    }

    @Override
    protected void onResume() {

        super.onResume();
        try {
            mediaPlayer.start();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


}
