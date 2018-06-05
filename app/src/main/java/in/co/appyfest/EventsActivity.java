package in.co.appyfest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class EventsActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseEvents;
    private FirebaseAuth mAuth;
    private RecyclerView mEventList;
    private String key;
    private Bundle b;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        mEventList = (RecyclerView) findViewById(R.id.eventsList);

         b= getIntent().getExtras();
         key = b.getString("postkey");

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(EventsActivity.this, SetupActivity.class);
            startActivity(intent);
        }

        if (id == R.id.action_logout) {
            //mAuth=FirebaseAuth.getInstance();
            mAuth.signOut();
            LoginManager.getInstance().logOut();
            Intent logout=new Intent(EventsActivity.this,ChooseActivity.class);
            startActivity(logout);
            finish();

            // updateUI();


        }
        if (id == R.id.action_add)


        {

            Intent intentKey = new Intent(EventsActivity.this,AddNewEventsActivity.class);
            intentKey.putExtra("currentkey",key);
            startActivity(intentKey);
            //Toast.makeText(EventsActivity.this,key,Toast.LENGTH_SHORT).show();
        }
        return false;
    }



        @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Event, EventViewHolder> firebaseRecyclerAdapter =
        new FirebaseRecyclerAdapter<Event, EventViewHolder>(

                Event.class,
                R.layout.event_row,
                EventViewHolder.class,
                mDatabaseEvents
        ) {
            @Override
            protected void populateViewHolder(EventViewHolder viewHolder, Event model, int position) {

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDescription(model.getDescription());
                viewHolder.setImage(getApplicationContext(), model.getImage());
                viewHolder.setDate(model.getDate());

            }
        };


        mEventList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class EventViewHolder extends RecyclerView.ViewHolder

    {

        View mView;
        public EventViewHolder(View itemView) {
            super(itemView);
            mView=itemView;

        }

        public void setTitle(String title) {
            TextView post_title = (TextView) mView.findViewById(R.id.eventTitle);
            post_title.setText(title);
        }

        public void setDescription(String description) {
            TextView post_description = (TextView) mView.findViewById(R.id.eventDescription);
            post_description.setText(description);
        }


        public void setDate(String date) {
            TextView post_date = (TextView) mView.findViewById(R.id.eventDateTime);
            post_date.setText(date);
        }

        public void setImage(Context ctx, String image) {
            ImageView post_image = (ImageView) mView.findViewById(R.id.eventImage);
            Picasso.with(ctx).load(image).into(post_image);

        }


    }


}
