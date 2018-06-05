package in.co.appyfest;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;

public class EventPassActivity extends AppCompatActivity {

    private Bundle b;
    private String event_key,post_key,contact;
    private ImageButton mQr,mDp;
    private TextView mPassUsername,mPassEmail,mPassEvent,mPassFest,mPassCollege,mPhone;
    private DatabaseReference mDatabaseFest,mDatabaseEvent,mDatabaseUser;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private String  email,college, image, event, fest;
    String textQr;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_pass);

        b= getIntent().getExtras();
        post_key = b.getString("key1");
        event_key =b.getString("key2");
        contact=b.getString("phone");

        mPassUsername =(TextView)findViewById(R.id.passUsername);
        mPassEmail=(TextView)findViewById(R.id.passEmail);
        mPassEvent=(TextView)findViewById(R.id.passEventName);
        mPassFest=(TextView)findViewById(R.id.txtFestName);
        mPassCollege =(TextView)findViewById(R.id.passCollege);
        mQr=(ImageButton)findViewById(R.id.passQr);
        mDp=(ImageButton)findViewById(R.id.passDp);
        mPhone=(TextView)findViewById(R.id.passPhone);

        mPhone.setText(contact);
        mAuth= FirebaseAuth.getInstance();
        mCurrentUser=mAuth.getCurrentUser();

        mDatabaseEvent = FirebaseDatabase.getInstance().getReference().child("Fest").child(post_key).child("Event").child(event_key);
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Participants").child((mCurrentUser.getUid()));
        mDatabaseFest =FirebaseDatabase.getInstance().getReference().child("Fest").child(post_key);



        mDatabaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                 mPassUsername.setText(name);

               // textQr= mPassUsername.getText().toString().trim();
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try
                {
                    BitMatrix bitMatrix= multiFormatWriter.encode(name, BarcodeFormat.QR_CODE,200,200);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    mQr.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }




                email = dataSnapshot.child("email").getValue().toString();
                mPassEmail.setText(email);

                college = dataSnapshot.child("organiser").getValue().toString();
                mPassCollege.setText(college);

                image = dataSnapshot.child("logo").getValue().toString();
                Picasso.with(EventPassActivity.this).load(image).into(mDp);


            }







            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mDatabaseEvent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 event = dataSnapshot.child("title").getValue().toString();
                mPassEvent.setText(event);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseFest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 fest = dataSnapshot.child("title").getValue().toString();
                mPassFest.setText(fest);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    /*

    private void generate()
    {


    }
    */



}
