package in.co.appyfest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static in.co.appyfest.R.id.phoneNumber;

public class RegisterEventActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseEvents;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private RecyclerView mEventList;
    private Bundle b;
    private FirebaseUser mCurrentUser;
    private String key1, key2;
    private TextView mEventEmail,mEventCollege,mEventUsername,mEventName;
    private EditText mEventPhone;
    private Button mGenerateButton;
    private ImageView mImageDisplayPicture;
    private String event,name,email,college,image,phone;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_event);

        b= getIntent().getExtras();
        key1 = b.getString("current_post_key");
        key2= b.getString("event_post_key");



        mEventEmail= (TextView)findViewById(R.id.emailEvent);
        mEventCollege= (TextView)findViewById(R.id.tv_address) ;
        mEventUsername= (TextView)findViewById(R.id.tv_name);
        mEventName= (TextView)findViewById(R.id.textEventName);
        mEventPhone= (EditText)findViewById(phoneNumber);
        mGenerateButton = (Button) findViewById(R.id.generateButton);
        mImageDisplayPicture=(ImageView)findViewById(R.id.imageDisplayPicture);

        mAuth= FirebaseAuth.getInstance();
        mCurrentUser=mAuth.getCurrentUser();


        mGenerateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextActivity();
            }
        });


        mDatabaseEvents = FirebaseDatabase.getInstance().getReference().child("Fest").child(key1).child("Event").child(key2);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Participants").child((mCurrentUser.getUid()));

        mDatabaseEvents.keepSynced(true);
        mDatabase.keepSynced(true);









        mDatabaseEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 event = dataSnapshot.child("title").getValue().toString();
                mEventName.setText(event);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                  name =dataSnapshot.child("name").getValue().toString();
                mEventUsername.setText(name);

                 email = dataSnapshot.child("email").getValue().toString();
                mEventEmail.setText(email);

                 college = dataSnapshot.child("organiser").getValue().toString();
                mEventCollege.setText(college);

                image = dataSnapshot.child("logo").getValue().toString();
                Picasso.with(RegisterEventActivity.this).load(image).into(mImageDisplayPicture);




            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });









    }




private void nextActivity() {
    phone = mEventPhone.getText().toString().trim();
    if(!TextUtils.isEmpty(phone))
    {
        Intent passIntent = new Intent(RegisterEventActivity.this, EventPassActivity.class);
        passIntent.putExtra("phone", phone);
        passIntent.putExtra("key1",key1);
        passIntent.putExtra("key2",key2);
        passIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(passIntent);
        finish();

        SmsManager.getDefault().sendTextMessage(phone,null,"hi!!" +" "+name+" "+"You are registered Successfully for the event"+" "+event+" "+"and bring the screenshot of the event with you",null,null);





    }
    else
    {
        mEventPhone.setError("This Field is Required !");
    }


    }





}
