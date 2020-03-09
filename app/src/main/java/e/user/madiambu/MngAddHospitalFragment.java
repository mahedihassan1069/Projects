package e.user.madiambu;


/*import android.content.Intent;*/
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

/*import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import static android.app.Activity.RESULT_OK;*/


/**
 * A simple {@link Fragment} subclass.
 */
public class MngAddHospitalFragment extends Fragment {

    private EditText hospital_name,hospital_email,hospital_number,h_lat,h_long;
    private Button pickButton,add_hospital_button;
    private DatabaseReference hrefe;

    DatabaseReference href;

    //int PLACE_PICKER_REQUEST=1;




    public MngAddHospitalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mng_add_hospital, container, false);

        hospital_name=(EditText) v.findViewById(R.id.hospital_name);
        hospital_email=(EditText) v.findViewById(R.id.hospital_email);
        hospital_number=(EditText) v.findViewById(R.id.hospital_number);
        h_lat=(EditText) v.findViewById(R.id.hospital_lat);
        h_long=(EditText) v.findViewById(R.id.hospital_long);

        hrefe=FirebaseDatabase.getInstance().getReference().child("H_Information");

        pickButton=(Button) v.findViewById(R.id.btn_lat_long);
        add_hospital_button=(Button) v.findViewById(R.id.btn_add_hospital);

        add_hospital_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HospitalInformation();
            }
        });

        pickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                href= FirebaseDatabase.getInstance().getReference().child("H_Location");



                href.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String longitude= Objects.requireNonNull(dataSnapshot.child("longitude").getValue()).toString();
                        String latitude= Objects.requireNonNull(dataSnapshot.child("latitude").getValue()).toString();

                        h_lat.setText(latitude);
                        h_long.setText(longitude);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        return v;
    }

    private void HospitalInformation()
    {
        String hname=hospital_name.getText().toString().trim();
        String hemail=hospital_email.getText().toString().trim();
        String hmobile=hospital_number.getText().toString().trim();
       Double hlat= Double.valueOf(h_lat.getText().toString().trim());
       Double hlong= Double.valueOf(h_long.getText().toString().trim());

       if(!TextUtils.isEmpty(hname))
       {
           String id=hrefe.push().getKey();
           HospitalInfo hospitalInfo=new HospitalInfo(hname,hemail,hmobile,hlat,hlong);
           hrefe.child(id).setValue(hospitalInfo);
           Toast.makeText(getActivity(), "Hospital Add successfully", Toast.LENGTH_SHORT).show();
       }

       else
       {
           Toast.makeText(getActivity(), "Enter Hospital Name First", Toast.LENGTH_SHORT).show();
       }

        hospital_name.getText().clear();
        hospital_email.getText().clear();
        hospital_number.getText().clear();
        h_lat.getText().clear();
        h_long.getText().clear();
    }

   /* @Override
    public void onClick(View v) {

        if(v==add_hospital_button)
        {
            HospitalInformation();
            hospital_name.getText().clear();
            hospital_email.getText().clear();
            hospital_number.getText().clear();
            h_lat.getText().clear();
            h_long.getText().clear();
        }

    }*/

}
