package in.co.appyfest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class EventParticipationActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseEvents;
    private FirebaseAuth mAuth;
    private RecyclerView mEventList;
    private Bundle b;
    private Button mEventRegister;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_participation);


        mEventList = (RecyclerView) findViewById(R.id.eventsParticipatioinList);

        b= getIntent().getExtras();
        key = b.getString("event_key");



        mDatabaseEvents = FirebaseDatabase.getInstance().getReference().child("Fest").child(key).child("Event");
        mAuth = FirebaseAuth.getInstance();
        mDatabaseEvents.keepSynced(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        mEventList.setHasFixedSize(true);
        mEventList.setLayoutManager(layoutManager);




    }



    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Event, EventParticipationActivity.EventViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Event, EventParticipationActivity.EventViewHolder>(

                        Event.class,
                        R.layout.event_participant_row,
                        EventParticipationActivity.EventViewHolder.class,
                        mDatabaseEvents
                ) {
                    @Override
                    protected void populateViewHolder(EventParticipationActivity.EventViewHolder viewHolder, Event model, int position) {

                        final String event_post_key = getRef(position).getKey();

                        viewHolder.setTitle(model.getTitle());
                        viewHolder.setDescription(model.getDescription());
                        viewHolder.setImage(getApplicationContext(), model.getImage());
                        viewHolder.setDate(model.getDate());



                        viewHolder.mRegisterParticipant.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent registerEventsIntent= new Intent(EventParticipationActivity.this,RegisterEventActivity.class);
                                registerEventsIntent.putExtra("event_post_key",event_post_key);
                                registerEventsIntent.putExtra("current_post_key",key);
                                startActivity(registerEventsIntent);

                                Toast.makeText(EventParticipationActivity.this,key,Toast.LENGTH_SHORT).show();
                            }
                        });




                    }
                };


        mEventList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class EventViewHolder extends RecyclerView.ViewHolder

    {
        Button mRegisterParticipant;
        View mView;
        public EventViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            mRegisterParticipant =(Button)mView.findViewById(R.id.participantPRegister);


        }

        public void setTitle(String title) {
            TextView post_title = (TextView) mView.findViewById(R.id.eventParticipantTitle);
            post_title.setText(title);
        }

        public void setDescription(String description) {
            TextView post_description = (TextView) mView.findViewById(R.id.eventParticipantDescription);
            post_description.setText(description);
        }


        public void setDate(String date) {
            TextView post_date = (TextView) mView.findViewById(R.id.eventParticipantDateTime);
            post_date.setText(date);
        }

        public void setImage(Context ctx, String image) {
            ImageView post_image = (ImageView) mView.findViewById(R.id.eventParticipantImage);
            Picasso.with(ctx).load(image).into(post_image);

        }


    }


}
