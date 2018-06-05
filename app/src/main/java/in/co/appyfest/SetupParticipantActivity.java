package in.co.appyfest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class SetupParticipantActivity extends AppCompatActivity {

    private EditText mSetupName,mSetupCollege,mSetupCity,mSetupState;
    private Button mSetupButton;
    private ImageButton mSetupImageButton;
    private static final int GALLERY_REQUEST=1;
    private Uri mImageUri=null;
    private DatabaseReference mDatabaseSetup;
    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_participant);


        mDatabaseSetup= FirebaseDatabase.getInstance().getReference().child("Participants");
        mAuth=FirebaseAuth.getInstance();
        mStorage= FirebaseStorage.getInstance().getReference().child("Participants Profile Images/");

        mProgress=new ProgressDialog(this);

        mSetupName=(EditText)findViewById(R.id.setupParticipantName);
        mSetupCollege=(EditText)findViewById(R.id.setupParticipantCollege);
        mSetupCity=(EditText)findViewById(R.id.setupParticipantCity);
        mSetupState=(EditText)findViewById(R.id.setupParticipantState);
        mSetupButton=(Button)findViewById(R.id.setupParticipantButton);
        mSetupImageButton=(ImageButton) findViewById(R.id.setupParticipantImage);

        mSetupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSetupAccount();
            }
        });


        mSetupImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,1);

            }
        });

    }


    private void startSetupAccount() {

        final String name=mSetupName.getText().toString().trim();
        final String user_id=mAuth.getCurrentUser().getUid();
        final String email=mAuth.getCurrentUser().getEmail();
        final String organiser=mSetupCollege.getText().toString().trim();
        final String city=mSetupCity.getText().toString().trim();
        final String state=mSetupState.getText().toString().trim();


        if(!TextUtils.isEmpty(name)&&mImageUri != null)
        {

            mProgress.setMessage("Finishing Updating !!");
            mProgress.show();
            StorageReference filepath= mStorage.child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    String download_uri=taskSnapshot.getDownloadUrl().toString();

                    mDatabaseSetup.child(user_id).child("name").setValue(name);
                    mDatabaseSetup.child(user_id).child("logo").setValue(download_uri);
                    mDatabaseSetup.child(user_id).child("email").setValue(email);
                    mDatabaseSetup.child(user_id).child("college").setValue(city);
                    mDatabaseSetup.child(user_id).child("state").setValue(state);
                    mDatabaseSetup.child(user_id).child("organiser").setValue(organiser);


                    mProgress.dismiss();
                    Toast.makeText(SetupParticipantActivity.this,"Update Successful !!",Toast.LENGTH_LONG).show();

                    Intent intent=new Intent(SetupParticipantActivity.this,UserActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }
            });
            filepath.putFile(mImageUri).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mProgress.dismiss();
                    Toast.makeText(SetupParticipantActivity.this,"Uploading Failed!",Toast.LENGTH_SHORT).show();
                }
            });

        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri imgUri = data.getData();
        if(requestCode==GALLERY_REQUEST && resultCode==RESULT_OK)
        {

            CropImage.activity(imgUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                mSetupImageButton.setImageURI(mImageUri);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }


    }






}
