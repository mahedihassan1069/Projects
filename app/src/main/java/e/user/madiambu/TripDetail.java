package e.user.madiambu;

import androidx.fragment.app.FragmentActivity;
import e.user.madiambu.Common.Common;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;

public class TripDetail extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private TextView txtDate,txtFee,txtBaseFare,txtTime,txtDistance,txtEstimatePayout,txtFrom,txtTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        txtDate=(TextView) findViewById(R.id.txtDate);
        txtFee=(TextView) findViewById(R.id.txtFee);
        txtBaseFare=(TextView) findViewById(R.id.txtBaseFare);
        txtTime=(TextView) findViewById(R.id.txtTime);
        txtDistance=(TextView) findViewById(R.id.txtDistance);
        txtEstimatePayout=(TextView) findViewById(R.id.txtEstimatePayout);
        txtFrom=(TextView) findViewById(R.id.txtFrom);
        txtTo=(TextView) findViewById(R.id.txtTo);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        settingInformation();
    }

    private void settingInformation() {

        if(getIntent() != null)
        {
            //set text..
            Calendar calendar=Calendar.getInstance();
            String date=String.format("%s,%d/%d",convertToDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)),calendar.get(Calendar.DAY_OF_MONTH),calendar.get(Calendar.MONTH));

            txtDate.setText(date);
            txtFee.setText(String.format("ট %.2f",getIntent().getDoubleExtra("total",0.0)));
            txtEstimatePayout.setText(String.format("ট %.2f",getIntent().getDoubleExtra("total",0.0)));
            txtBaseFare.setText(String.format("ট %.2f", Common.base_fare));
            txtTime.setText(String.format("%s min",getIntent().getStringExtra("time")));
            txtDistance.setText(String.format("%s km",getIntent().getStringExtra("distance")));
            txtFrom.setText(getIntent().getStringExtra("start_address"));
            txtTo.setText(getIntent().getStringExtra("end_address"));

            //add marker..
            String[] location_end=getIntent().getStringExtra("location_end").split(",");
            LatLng drop_off=new LatLng(Double.parseDouble(location_end[0]),Double.parseDouble(location_end[1]));

            mMap.addMarker(new MarkerOptions().position(drop_off).title("Drop off here").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(drop_off,12.0f));



        }
    }

    private String convertToDayOfWeek(int day) {

        switch (day)
        {

            case Calendar.SUNDAY:
                return "SUNDAY";
            case Calendar.MONDAY:
                return "MONDAY";
            case Calendar.TUESDAY:
                return "TUESDAY";
            case Calendar.WEDNESDAY:
                return "WEDNESDAY";
            case Calendar.THURSDAY:
                return "THURSDAY";
            case Calendar.FRIDAY:
                return "FRIDAY";
            case Calendar.SATURDAY:
                return "SATURDAY";
            default:
                return "UNK";

        }

    }
}
