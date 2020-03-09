package e.user.madiambu.Helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import e.user.madiambu.R;

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {

    View myView;

    public CustomInfoWindow(Context context) {
       myView= LayoutInflater.from(context).inflate(R.layout.custom_rider_info_window,null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        TextView txtPicupTitle=((TextView)myView.findViewById(R.id.txtPickupInfo));
        txtPicupTitle.setText(marker.getTitle());

        TextView txtPicupSnippet=((TextView)myView.findViewById(R.id.txtPickupSnippet));
        txtPicupSnippet.setText(marker.getSnippet());

        return myView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
