package e.user.madiambu;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class driver_Code extends AppCompatActivity {

    private EditText D_code;
    private Button D_add;

    private DatabaseReference driverDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_code);

        D_code=findViewById(R.id.driver_code);
        D_add=findViewById(R.id.btn_driver_add);

        D_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String CODE=D_code.getText().toString().trim();



                if(CODE.isEmpty())
                {
                    D_code.setError("Please give a valid code");

                }

                else
                {

                    DriverADD(CODE);

                }
            }
        });
    }

    private void DriverADD(final String uid)
    {

       driverDatabase= FirebaseDatabase.getInstance().getReference().child("AddDriver");

       driverDatabase.orderByChild("driverCode").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.exists())
               {
                   Intent intent = new Intent(driver_Code.this, DriverInformation.class);
                     startActivity(intent);
               }

               else
               {
                   Toast.makeText(driver_Code.this, "Enter Correct Code", Toast.LENGTH_SHORT).show();
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });

//       driverDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//           @Override
//           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//              for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//
//                  if(snapshot.child("driverCode").toString().equals(uid)){
//
//                      Intent intent = new Intent(driver_Code.this, DriverInformation.class);
//                      startActivity(intent);
//                  }
//              }
//           }
//
//           @Override
//           public void onCancelled(@NonNull DatabaseError databaseError) {
//
//           }
//       });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.still, R.anim.slow_fade_out);
    }
}
