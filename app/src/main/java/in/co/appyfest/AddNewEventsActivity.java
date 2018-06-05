package in.co.appyfest;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Date;


public class AddNewEventsActivity extends AppCompatActivity {

    private Uri mImageUri = null;
    private static final int GALLERY_REQUEST = 1;
    private ImageButton mEventImageButton;
    private EditText mEventTitle, mEventDescription, mEventCharge;
    private DatabaseReference mDatabase;
    private Button mEventSubmit;
    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private ProgressDialog mProgress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_events);

        Bundle bundle= getIntent().getExtras();
        String key = bundle.getString("currentkey");

        mEventImageButton = (ImageButton) findViewById(R.id.eventAddImgButtonThe);

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Fest").child(key).child("Event");

        mEventTitle = (EditText) findViewById(R.id.eventPostTheTitle);
        mEventDescription = (EditText) findViewById(R.id.eventPostDescription);
        mEventSubmit = (Button) findViewById(R.id.eventSubmitButton);
        mEventCharge = (EditText) findViewById(R.id.eventCharges);


        mProgress = new ProgressDialog(this);

        mEventImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });


        mEventSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                startPosting();
            }
        });


    }


    public void clearText()
    {
        mEventTitle.setText("");
        mEventDescription.setText("");
        mEventCharge.setText("");
        mEventImageButton.setImageURI(null);

    }
    private void startPosting()

    {


        mProgress.setMessage("Uploading to Server...");

        final String title_val = mEventTitle.getText().toString().trim();
        final String desc_val = mEventDescription.getText().toString().trim();
        final String currentDateandTime = new SimpleDateFormat("dd-MM-yyyy || HH:mm:ss").format(new Date());
        final String eventCharge = mEventCharge.getText().toString().trim();
        if (!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(desc_val) && mImageUri != null) {
            mProgress.show();
            StorageReference filePath = mStorage.child("Fest Images").child(mImageUri.getLastPathSegment());

            filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    final DatabaseReference newPost = mDatabase.push();

                    newPost.child("title").setValue(title_val);
                    newPost.child("image").setValue(downloadUrl.toString());
                    newPost.child("Fees").setValue(eventCharge);
                    newPost.child("description").setValue(desc_val);
                    newPost.child("date").setValue(currentDateandTime).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                mProgress.dismiss();
                                Toast.makeText(AddNewEventsActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AddNewEventsActivity.this, EventsActivity.class);
                                startActivity(intent);

                            } else {
                                Toast.makeText(AddNewEventsActivity.this, "Error while adding data", Toast.LENGTH_SHORT).show();
                                mProgress.dismiss();
                            }

                        }


                    });

                }
            });

            filePath.putFile(mImageUri).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddNewEventsActivity.this,"Error",Toast.LENGTH_SHORT).show();
                    mProgress.dismiss();
                }
            });



        }


        else
        {
            mProgress.dismiss();
            Dialog dialog = new Dialog(this);
            dialog.setTitle("Please Add an Image to Proceed...");

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
                mEventImageButton.setImageURI(mImageUri);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }


    }





}
