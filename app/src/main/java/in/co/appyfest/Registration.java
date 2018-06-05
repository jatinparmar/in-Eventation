package in.co.appyfest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Registration extends AppCompatActivity {


    private EditText mRegUsername,mRegEmail,mRegPassword,mRegCollege,mRegCity,mRegState;
    private Button mRegSubmit;
    private ImageButton mRegIcon;
    private FirebaseAuth mAuth;
    private static final int GALLERY_REQUEST=1;
    private Uri mImageUri=null;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private ProgressDialog mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        printKeyHash();

        mRegUsername=(EditText)findViewById(R.id.addUsername);
        mRegEmail=(EditText)findViewById(R.id.addEmail);
        mRegPassword=(EditText)findViewById(R.id.addPassword);
        mRegCity=(EditText)findViewById(R.id.addCity);
        mRegCollege=(EditText)findViewById(R.id.addCollege);
        mRegState=(EditText)findViewById(R.id.addState);
        mRegSubmit=(Button)findViewById(R.id.addSubmit);
        mRegIcon=(ImageButton)findViewById(R.id.addClgeIcon);

        mAuth=FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Organisers");
        mStorage= FirebaseStorage.getInstance().getReference().child("Organisers Profile Images/");
        mProgress=new ProgressDialog(this);

        mRegIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,1);

            }
        });


        mRegSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegister();
            }
        });

    }
    public void cleartext()
    {
        mRegUsername.setText("");
        mRegEmail.setText("");
        mRegPassword.setText("");
        mRegCity.setText("");
        mRegCollege.setText("");
        mRegState.setText("");
        mRegUsername.requestFocus();
    }

    private void startRegister() {


        final String name = mRegUsername.getText().toString().trim();
        final String email = mRegEmail.getText().toString().trim();
        final String password = mRegPassword.getText().toString().trim();
        final String college = mRegCollege.getText().toString().trim();
        final String city = mRegCity.getText().toString().trim();
        final String state = mRegState.getText().toString().trim();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(college) && !TextUtils.isEmpty(city) && !TextUtils.isEmpty(state) && mImageUri != null) {

            mProgress.setMessage("Signing Up...");
            mProgress.show();
            StorageReference filepath = mStorage.child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final String download_uri = taskSnapshot.getDownloadUrl().toString();

                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                String userid = mAuth.getCurrentUser().getUid();
                                DatabaseReference current_user_db = mDatabase.child(userid);
                                current_user_db.child("name").setValue(name);
                                current_user_db.child("email").setValue(email);
                                current_user_db.child("organiser").setValue(college);
                                current_user_db.child("city").setValue(city);
                                current_user_db.child("state").setValue(state);
                                current_user_db.child("logo").setValue(download_uri);
                                Toast.makeText(Registration.this, "Registration Successful!!", Toast.LENGTH_SHORT).show();
                                cleartext();
                                mProgress.dismiss();


                                Intent intent = new Intent(Registration.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);

                            } else

                            {
                                Toast.makeText(Registration.this, "Error!!", Toast.LENGTH_SHORT).show();
                                mProgress.dismiss();
                            }
                        }
                    });

                }
            });


        }
    }

    private void printKeyHash()
    {
        try {
            PackageInfo info=getPackageManager().getPackageInfo("in.co.appyfest", PackageManager.GET_SIGNATURES);
            for(Signature signature:info.signatures)
            {
                MessageDigest md=MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("Keyhash", Base64.encodeToString(md.digest(),Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
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
                mRegIcon.setImageURI(mImageUri);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }


    }






}
