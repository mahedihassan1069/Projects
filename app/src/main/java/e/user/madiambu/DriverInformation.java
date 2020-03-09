package e.user.madiambu;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

public class DriverInformation extends AppCompatActivity {

    private static final int GALLERY_REQUEST = 2;
    private Uri uri = null;
    ImageButton photoPickerD;
    private FirebaseAuth dAuth;
    private DatabaseReference dDatabase;
    private StorageReference dStorageRef;
    private ProgressBar dprogressBar;
    public String photo_uri;

    private EditText dName,dEmail,dPass,dMobile;
    private Button update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_information);
        photoPickerD=(ImageButton) findViewById(R.id.photo_chooser_driver);
        dprogressBar=(ProgressBar) findViewById(R.id.progressBar_driver_add);
        dprogressBar.setVisibility(View.GONE);

        dName=(EditText) findViewById(R.id.name_driver);
        dEmail=(EditText) findViewById(R.id.email_driver);
        dPass=(EditText) findViewById(R.id.password_driver);
        dMobile=(EditText) findViewById(R.id.mobile_driver);
        update=(Button) findViewById(R.id.btn_update_info);

        photoPickerD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(DriverInformation.this,
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
                String NAME=dName.getText().toString().trim();
                String EMAIL=dEmail.getText().toString().trim();
                String PASSWORD=dPass.getText().toString().trim();
                String MOBILE=dMobile.getText().toString().trim();

                if(uri == null){
                    Toast.makeText(DriverInformation.this, "Please select an image", Toast.LENGTH_SHORT).show();
                }
                else if(NAME.isEmpty()){
                    dName.setError("Please type your name");
                }
                else if(EMAIL.isEmpty()){
                    dEmail.setError("Please give a valid email");
                }
                else if(PASSWORD.isEmpty()){
                    dPass.setError("Password can't be empty");
                }
                else if(MOBILE.isEmpty()){
                    dMobile.setError("Give a valid mobile number");
                }

                else {

                    dprogressBar.setVisibility(View.VISIBLE);
                    createUser(NAME, EMAIL, PASSWORD, MOBILE);
                }

            }
        });
    }

    private void createUser(final String NAME, final String EMAIL, String PASSWORD, final String MOBILE)
    {
        dAuth = FirebaseAuth.getInstance();

        dAuth.createUserWithEmailAndPassword(EMAIL, PASSWORD)
                .addOnCompleteListener(DriverInformation.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            FirebaseUser user = dAuth.getCurrentUser();
                            final String UID = user.getUid();

                            dStorageRef = FirebaseStorage.getInstance().getReference();

                            final StorageReference riversRef = dStorageRef.child("PROFILE_IMAGES/DRIVER/" + NAME + " " + UID + ".jpg");
                            riversRef.putFile(uri)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                            riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {

                                                    String downloadUrl = uri.toString();
                                                    dDatabase = FirebaseDatabase.getInstance().getReference().child("USERS").child("DRIVER_USER").child(UID);
                                                    dDatabase.child("NAME").setValue(NAME);
                                                    dDatabase.child("EMAIL").setValue(EMAIL);
                                                    dDatabase.child("PHOTO").setValue(downloadUrl);
                                                    dDatabase.child("MOBILE_NO").setValue(MOBILE);
                                                    Toast.makeText(DriverInformation.this, "Welcome " + NAME,
                                                            Toast.LENGTH_SHORT).show();

                                                    dprogressBar.setVisibility(View.GONE);
                                                    Intent intent = new Intent(DriverInformation.this, driverHome.class);
                                                    startActivity(intent);
                                                    DriverInformation.this.finish();

                                                }
                                            });

                                        }

                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            Toast.makeText(DriverInformation.this, "Upload image failed\nPlease select a valid image",
                                                    Toast.LENGTH_SHORT).show();

                                            dprogressBar.setVisibility(View.GONE);
                                        }
                                    });
                        }else {
                            Toast.makeText(DriverInformation.this, "Authentication failed"+task.getException().toString(),
                                    Toast.LENGTH_SHORT).show();

                            dprogressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void startGallery() {
        Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        cameraIntent.setType("image/*");
        if (cameraIntent.resolveActivity(DriverInformation.this.getPackageManager()) != null) {
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
                        .start(DriverInformation.this);
                Log.d("Mobin", "OK1");
            }

            if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Log.d("mobin", "OK2");
                Uri resultUri = result.getUri();
                Log.d("mobin", resultUri.toString());
                Bitmap bitmapImage = null;
                try {
                    //bitmapImage = new Compressor(getActivity()).compressToBitmap(new File(resultUri.toString()));
                    bitmapImage = MediaStore.Images.Media.getBitmap(DriverInformation.this.getContentResolver(), resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                photoPickerD.setImageBitmap(bitmapImage);
                uri = resultUri;
            }
        }else{
            Log.d("mobin", "NOT WORKING");
        }



        Glide
                .with(this)
                .load(uri)
                .apply(new RequestOptions()
                        .centerCrop()
                        .circleCrop())
                .into(photoPickerD);
    }
}
