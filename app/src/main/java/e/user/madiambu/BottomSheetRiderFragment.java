package e.user.madiambu;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import e.user.madiambu.Common.Common_d;
import e.user.madiambu.Remote.IGoogleAPIKey;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BottomSheetRiderFragment extends BottomSheetDialogFragment {
    String mLocation,mDestination;

    TextView txtLocation,txtDestination,txtMoney;

    IGoogleAPIKey mService;

    //tap on map
    boolean isTapOnMap;

    public static BottomSheetRiderFragment newInstance(String location,String destination, boolean isTapOnMap)
    {
        BottomSheetRiderFragment f=new BottomSheetRiderFragment();
        Bundle args=new Bundle();
        args.putString("location",location);
        args.putString("destination",destination);
        //tap on map
        args.putBoolean("isTapOnMap",isTapOnMap);
        f.setArguments(args);
        return  f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocation=getArguments().getString("location");
        mDestination=getArguments().getString("destination");
        isTapOnMap=getArguments().getBoolean("isTapOnMap");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.bottom_sheet_rider,container,false);
        //textView....

        txtLocation=(TextView) view.findViewById(R.id.txtLocation);
        txtDestination=(TextView) view.findViewById(R.id.txtDestination);
        txtMoney=(TextView) view.findViewById(R.id.txtMoney);

        mService= Common_d.getGoogleAPIKeyService();

        getPrice(mLocation,mDestination);

        if(!isTapOnMap) {
            //set data..
            txtLocation.setText(mLocation);
            txtDestination.setText(mDestination);
        }

        return view;
    }

    private void getPrice(String mLocation, String mDestination) {

        String requestUrl=null;

        try {
            requestUrl = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&" +
                    "transit_routing_preference=less_driving&" +
                    "origin=" + mLocation + "&" +
                    "destination=" + mDestination + "&" +
                    "key=" + getResources().getString(R.string.google_direction_api);
            Log.d("MOBIN", requestUrl);//print URL for debug;

            mService.getPath(requestUrl).enqueue(new Callback<String>() {
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
                        Double distance_value = Double.parseDouble(distance_txt.replaceAll("[^0-9\\\\.]", ""));

                        JSONObject time = legsObject.getJSONObject("duration");
                        String time_txt = time.getString("text");

                        Integer time_value = Integer.parseInt(time_txt.replaceAll("\\D+", ""));

                        String final_calculate = String.format("%s + %s = %.2f টাকা", distance_txt, time_txt, Common_d.getPrice(distance_value, time_value));

                        txtMoney.setText(final_calculate);


                        if(isTapOnMap)
                        {
                            String start_address=legsObject.getString("start_address");
                            String end_address=legsObject.getString("end_address");

                            txtLocation.setText(start_address);
                            txtDestination.setText(end_address);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }


                @Override
                public void onFailure(Call<String> call, Throwable t) {

                    Log.d("Error", t.getMessage());
                }


            });

        }catch (Exception ex)
        {
            ex.printStackTrace();
        }



    }
}
