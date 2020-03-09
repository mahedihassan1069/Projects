package e.user.madiambu;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class MngAddDriverFragment extends Fragment {

    private EditText mobileNumber,driver_name,driver_gcode;
    private Button addDriver;
    private ProgressBar progressBar;
    private ImageView shadow;
    DatabaseReference driverDatabase;


    public MngAddDriverFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mng_add_driver, container, false);
        mobileNumber=(EditText) v.findViewById(R.id.driver_mobile);
        driver_name=(EditText) v.findViewById(R.id.driver_name);
        driver_gcode=(EditText) v.findViewById(R.id.driver_gcode);
        addDriver=(Button) v.findViewById(R.id.btn_add_driver);
        progressBar=(ProgressBar) v.findViewById(R.id.progressBar_add_driver);
        shadow=(ImageView) v.findViewById(R.id.shadow_add_driver);



        driverDatabase= FirebaseDatabase.getInstance().getReference("AddDriver");

        shadow.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        addDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shadow.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                AddDriver();
            }
        });

        return v;
    }

    private void AddDriver()
    {
        String D_Name=driver_name.getText().toString().trim();
        String D_Number=mobileNumber.getText().toString().trim();
        String D_Code=driver_gcode.getText().toString().trim();

        if (!TextUtils.isEmpty(D_Name))
        {
            String id=driverDatabase.push().getKey();

            AddDriver addDriver=new AddDriver(id,D_Name,D_Code,D_Number);

            driverDatabase.child(id).setValue(addDriver);

            Toast.makeText(getActivity(),"Driver Added Successfully", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getActivity(),"Enter Driver Name First", Toast.LENGTH_SHORT).show();
        }
        shadow.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        driver_name.setText("");
        mobileNumber.setText("");
        driver_gcode.setText("");
    }
}
