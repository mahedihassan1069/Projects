package e.user.madiambu;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class MngProfileFragment extends Fragment {

    private ImageView image;
    private TextView name,email;

    private Button editProfile,signOut;


    public MngProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mng_profile, container, false);

        image = v.findViewById(R.id.mng_profile_image);
         name = v.findViewById(R.id.mng_profile_name);
         email = v.findViewById(R.id.mng_profile_email);

         editProfile = v.findViewById(R.id.mng_edit_profile_btn);
         signOut = v.findViewById(R.id.mng_log_out_btn);

        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("USERS").child("Hospital Manager").child(UID);


        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name.setText(dataSnapshot.child("NAME").getValue().toString());
                email.setText(dataSnapshot.child("EMAIL").getValue().toString());
                Picasso.get().load(dataSnapshot.child("PHOTO").getValue().toString()).resize(300,300).transform(new CircleTransform()).into(image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), splash.class);
                startActivity(intent);
                getActivity().finish();
            }
        });



        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfile.class);
                startActivity(intent);
            }
        });



        return v;
    }

}
