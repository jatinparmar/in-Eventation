package in.co.appyfest;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class UserParticipantActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseLike;

    private RecyclerView mFestList;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextView mUserEmail, mUserName;
    private ImageView mImageDp;
    private EditText mSearchText;
    private ImageButton mSearchButton;
    private DatabaseReference mDatabaseCurrent;
    private FirebaseUser mUser;


    private boolean mProcessLike = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_participant);
        /*
        mUserEmail = (TextView) findViewById(R.id.textViewEmail);
        mUserName = (TextView) findViewById(R.id.userName);


        mImageDp = (ImageView) findViewById(R.id.imageDp);
        */
        mSearchButton = (ImageButton) findViewById(R.id.search_btn);
        mSearchText = (EditText) findViewById(R.id.search_field);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mDatabaseCurrent = FirebaseDatabase.getInstance().getReference().child("Organisers").child(mUser.getUid());
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Participants");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Fest");
        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");


        mDatabaseUsers.keepSynced(true);
        mDatabase.keepSynced(true);
        mDatabaseLike.keepSynced(true);
        mDatabaseCurrent.keepSynced(true);

        mFestList = (RecyclerView) findViewById(R.id.userParticipantList);


        // mFestList.setLayoutManager(new LinearLayoutManager(this));
        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(UserParticipantActivity.this, LoginParticipantActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);


                } else {

                }
            }
        };


        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String searchText = mSearchText.getText().toString().trim();
                Query firebaseSearchQuery = mDatabase.orderByChild("title").startAt(searchText).endAt(searchText + "\uf8ff");
            }
        });


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        mFestList.setHasFixedSize(true);
        mFestList.setLayoutManager(layoutManager);

        checkUserExist();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
            Intent intent=new Intent(UserParticipantActivity.this,SetupActivity.class);
            startActivity(intent);
        }

        if (id==R.id.action_logout)
        {
            //mAuth=FirebaseAuth.getInstance();
            mAuth.signOut();
            LoginManager.getInstance().logOut();
            Intent logout=new Intent(UserParticipantActivity.this,ChooseActivity.class);
            logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(logout);
            finish();

            // updateUI();


        }



        return false;
    }




    @Override
    public void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

        mDatabase.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                /*
                if (dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())) {
                    String name = (String) dataSnapshot.child("name").getValue();
                    String email = (String) dataSnapshot.child("email").getValue();
                    String dp = (String) dataSnapshot.child("logo").getValue();

                    //  mUserEmail.setText(email);
                    //  mUserName.setText(name);
                    Picasso.with(UserActivity.this).load(dp).into(mImageDp);
                }
                */
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Toast.makeText(UserActivity.this,"Unable to retreive",Toast.LENGTH_SHORT).show();
            }
        });


        FirebaseRecyclerAdapter<Fest, FestViewHolder3> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Fest, UserParticipantActivity.FestViewHolder3>(
                Fest.class, R.layout.participant_event_row, UserParticipantActivity.FestViewHolder3.class, mDatabase

        )

        {
            @Override
            protected void populateViewHolder(UserParticipantActivity.FestViewHolder3 viewHolder, Fest model, int position) {

                final String post_key = getRef(position).getKey();

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDescription(model.getDescription());
                viewHolder.setImage(getApplicationContext(), model.getImage());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setDate(model.getDate());
                viewHolder.setDisplay_picture(getApplicationContext(), model.getDisplay_picture());
                viewHolder.setLikeBtn(post_key);

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent =  new Intent(UserParticipantActivity.this,EventParticipationActivity.class);
                        intent.putExtra("event_key",post_key);
                        startActivity(intent);
                    }
                });


                viewHolder.mLikeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mProcessLike = true;


                        mDatabaseLike.addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {


                                if (mProcessLike) {
                                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                                        mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();

                                        mProcessLike = false;

                                    } else {




                                        mDatabaseCurrent.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                String email= dataSnapshot.child("email").getValue().toString();

                                                mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue(email);

                                                mProcessLike = false;

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });


                                    }


                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });


            }
        };

        mFestList.setAdapter(firebaseRecyclerAdapter);

    }


    public static class FestViewHolder3 extends RecyclerView.ViewHolder
    {
        View mView;
        ImageButton mLikeButton;
        DatabaseReference mDatabaseLike;
        FirebaseAuth mAuthLike;

        public FestViewHolder3(View itemView) {
            super(itemView);

            mView=itemView;
            mLikeButton=(ImageButton)mView.findViewById(R.id.festParticipantLikeButton);
            mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
            mAuthLike = FirebaseAuth.getInstance();

            mDatabaseLike.keepSynced(true);
        }

        public void setLikeBtn(final String post_key)
        {
            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.child(post_key).hasChild(mAuthLike.getCurrentUser().getUid()))
                    {
                        mLikeButton.setImageResource(R.mipmap.ic_thumb_up_red_24dp);
                    }
                    else
                    {
                        mLikeButton.setImageResource(R.mipmap.ic_thumb_up_grey_24dp);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        public void setTitle(String title)
        {
            TextView post_title=(TextView)mView.findViewById(R.id.festParticipantTitle);
            post_title.setText(title);
        }

        public void setDescription(String description)
        {
            TextView post_description=(TextView)mView.findViewById(R.id.festParticipantDescription);
            post_description.setText(description);
        }


        public void setDate(String date)
        {
            TextView post_date=(TextView)mView.findViewById(R.id.festParticipantDateTime);
            post_date.setText(date);
        }

        public void setImage(Context ctx, String image)
        {
            ImageView post_image=(ImageView)mView.findViewById(R.id.festParticipantImage);
            Picasso.with(ctx).load(image).into(post_image);
            /*
            Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(post_image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                }
            });
            */

        }
        public void setDisplay_picture(Context ctx,String display_picture)
        {
            ImageView post_image=(ImageView)mView.findViewById(R.id.festParticipantDp);
            Picasso.with(ctx).load(display_picture).into(post_image);
        }

        public void setUsername(String username)
        {
            TextView post_username=(TextView)mView.findViewById(R.id.festParticipantUsername);
            post_username.setText(username);
        }





    }



    private void checkUserExist()
    {
        if(mAuth.getCurrentUser()!=null) {
            final String user_id = mAuth.getCurrentUser().getUid();
            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(user_id)) {
                        Intent userintent = new Intent(UserParticipantActivity.this, SetupParticipantActivity.class);
                        userintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(userintent);
                        Toast.makeText(UserParticipantActivity.this, "checkuserexist method works properly", Toast.LENGTH_LONG).show();

                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }












}




























