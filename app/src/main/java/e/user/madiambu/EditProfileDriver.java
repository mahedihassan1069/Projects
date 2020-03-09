package e.user.madiambu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

public class EditProfileDriver extends AppCompatActivity {

    private static final int GALLERY_REQUEST = 2;
    private Uri uri = null;
    ImageButton photoPicker;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;
    private ProgressBar progressBar;
    private ImageView shadow;
    public String photo_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_driver);

        photoPicker = findViewById(R.id.photo_chooser_driver_profile);
        progressBar = findViewById(R.id.progressBar_driver_profile);
        shadow = findViewById(R.id.shadow_sign_up_driver_profile);
        shadow.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        final EditText name = findViewById(R.id.name_driver_profile),
                passwordOld = findViewById(R.id.password_driver_profile_old),
                passwordNew = findViewById(R.id.password_driver_profile_new);
        Button update = findViewById(R.id.btn_update_driver_profile);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("USERS/DRIVER_USER").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name.setText(dataSnapshot.child("NAME").getValue().toString());
                Picasso.get().load(dataSnapshot.child("PHOTO").getValue().toString()).resize(420,420).transform(new CircleTransform()).centerCrop().into(photoPicker);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        photoPicker.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            2000);
                }
                else {
                    startGallery();
                }
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String NAME = name.getText().toString().trim(),
                        EMAIL = FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                        PASSWORD_OLD = passwordOld.getText().toString(),
                        PASSWORD_NEW = passwordNew.getText().toString();

                if(NAME.isEmpty()){
                    name.setError("Please type your name");
                }
                else if(PASSWORD_OLD.isEmpty()){
                    passwordOld.setError("Password can't be empty");
                }
                else if(PASSWORD_NEW.isEmpty()){
                    passwordNew.setError("Password can't be empty");
                }
                else {
                    shadow.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    updateUser(NAME, EMAIL, PASSWORD_OLD, PASSWORD_NEW);
                }
            }
        });
    }

    private void updateUser(final String NAME, final String EMAIL, String PASSWORD_OLD, final String PASSWORD_NEW){
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        AuthCredential credential = EmailAuthProvider.getCredential(EMAIL, PASSWORD_OLD);

        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    user.updatePassword(PASSWORD_NEW).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                final String UID = user.getUid();

                                if(uri!=null) {
                                    mStorageRef = FirebaseStorage.getInstance().getReference();

                                    final StorageReference riversRef = mStorageRef.child("PROFILE_IMAGES/DRIVER/" + NAME + " " + UID + ".jpg");

                                    riversRef.putFile(uri)
                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {
                                                            String downloadUrl = uri.toString();
                                                            mDatabase = FirebaseDatabase.getInstance().getReference().child("USERS").child("DRIVER_USER").child(UID);
                                                            mDatabase.child("NAME").setValue(NAME);
                                                            mDatabase.child("PHOTO").setValue(downloadUrl);
                                                            shadow.setVisibility(View.GONE);
                                                            progressBar.setVisibility(View.GONE);
                                                            onBackPressed();
                                                            finish();
                                                        }
                                                    });
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    Toast.makeText(getApplicationContext(), "Upload image failed\nPlease select a valid image",
                                                            Toast.LENGTH_SHORT).show();
                                                    shadow.setVisibility(View.GONE);
                                                    progressBar.setVisibility(View.GONE);
                                                }
                                            });
                                }else{
                                    mDatabase = FirebaseDatabase.getInstance().getReference().child("USERS").child("GENERAL_USERS").child(UID);
                                    mDatabase.child("NAME").setValue(NAME);
                                    shadow.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.GONE);
                                    onBackPressed();
                                    finish();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void startGallery() {

        Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        cameraIntent.setType("image/*");
        if (cameraIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            startActivityForResult(cameraIntent, 1000);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            if(requestCode == 1000){
                Uri imageUri = data.getData();
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(this);
            }

            if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri resultUri = result.getUri();
                Bitmap bitmapImage = null;
                try {
                    //bitmapImage = new Compressor(getActivity()).compressToBitmap(new File(resultUri.toString()));
                    bitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                photoPicker.setImageBitmap(bitmapImage);
                uri = resultUri;
            }
        }else{
        }



        Glide
                .with(this)
                .load(uri)
                .apply(new RequestOptions()
                        .centerCrop()
                        .circleCrop())
                .into(photoPicker);
    }
}
