package e.user.madiambu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Tag;

import org.w3c.dom.Text;

public class hInfo extends AppCompatActivity {

    private TextView hosp_name,hosp_email,hosp_num;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h_info);

        hosp_name=(TextView) findViewById(R.id.hosp_name);
        hosp_email=(TextView) findViewById(R.id.hosp_email);
        hosp_num=(TextView) findViewById(R.id.hosp_number);

        final String id = getIntent().getStringExtra("id").toString();
        final String position = id.substring(1);


        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference1=firebaseDatabase.getReference("H_Information");

        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count=0;
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    if(count==Integer.parseInt(position)) {
                        hosp_name.setText(dataSnapshot1.child("hname").getValue().toString());
                        hosp_email.setText(dataSnapshot1.child("hemail").getValue().toString());
                        hosp_num.setText(dataSnapshot1.child("hmobile").getValue().toString());
                        break;
                    }
                    count++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
