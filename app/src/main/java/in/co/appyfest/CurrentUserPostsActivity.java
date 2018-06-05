package in.co.appyfest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class CurrentUserPostsActivity extends AppCompatActivity {


    private DatabaseReference mDatabaseCurrentUser;
    private FirebaseAuth mAuth;
    private Query mQueryCurrentUser;
    private DatabaseReference mDatabaseLike;
    private RecyclerView mFestListCurrent;
    private ProgressDialog mProgress;
    private FirebaseUser mCurrentUser;
    private boolean mProcessLike = false;
    private DatabaseReference mDatabaseCurrent;
    private FirebaseUser mUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_user_posts);

        mDatabaseCurrentUser = FirebaseDatabase.getInstance().getReference().child("Fest");
        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
        mAuth = FirebaseAuth.getInstance();
        mUser= mAuth.getCurrentUser();
        mDatabaseCurrent = FirebaseDatabase.getInstance().getReference().child("Organisers").child(mUser.getUid());


        mCurrentUser=mAuth.getCurrentUser();
        String currentUserId = mCurrentUser.getUid();
        mFestListCurrent = (RecyclerView) findViewById(R.id.festsListCurrent);


        mQueryCurrentUser = mDatabaseCurrentUser.orderByChild("uid").equalTo(currentUserId);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        mDatabaseLike.keepSynced(true);
        mDatabaseCurrent.keepSynced(true);
        mFestListCurrent.setHasFixedSize(true);
        mFestListCurrent.setLayoutManager(layoutManager);


    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Fest, FestViewHolder2> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Fest, CurrentUserPostsActivity.FestViewHolder2>(


                Fest.class, R.layout.fest_row, CurrentUserPostsActivity.FestViewHolder2.class, mQueryCurrentUser

        )

        {
            @Override
            protected void populateViewHolder(CurrentUserPostsActivity.FestViewHolder2 viewHolder, Fest model, int position) {

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

                       // Toast.makeText(CurrentUserPostsActivity.this,post_key,Toast.LENGTH_LONG);

                        Intent eventsIntent= new Intent(CurrentUserPostsActivity.this,EventsActivity.class);
                        eventsIntent.putExtra("postkey",post_key);
                        startActivity(eventsIntent);


                    }
                });


                viewHolder.mDeleteButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {


                    mDatabaseCurrentUser.child(post_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(CurrentUserPostsActivity.this,"Post Deleted Successfully !!",Toast.LENGTH_LONG).show();
                        }
                    });

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

        mFestListCurrent.setAdapter(firebaseRecyclerAdapter);


    }


    public static class FestViewHolder2 extends RecyclerView.ViewHolder {
        View mView;
        ImageButton mLikeButton;
        DatabaseReference mDatabaseLike;
        ImageButton mDeleteButton;
        FirebaseAuth mAuthLike;

        public FestViewHolder2(View itemView) {
            super(itemView);

            mView = itemView;
            mLikeButton = (ImageButton) mView.findViewById(R.id.festLikeButton);
            mDeleteButton=(ImageButton)mView.findViewById(R.id.imageDeleteButton);
            mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
            mAuthLike = FirebaseAuth.getInstance();

            mDatabaseLike.keepSynced(true);
        }

        public void setLikeBtn(final String post_key) {
            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(post_key).hasChild(mAuthLike.getCurrentUser().getUid())) {
                        mLikeButton.setImageResource(R.mipmap.ic_thumb_up_red_24dp);
                    } else {
                        mLikeButton.setImageResource(R.mipmap.ic_thumb_up_grey_24dp);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        public void setTitle(String title) {
            TextView post_title = (TextView) mView.findViewById(R.id.festTitle);
            post_title.setText(title);
        }

        public void setDescription(String description) {
            TextView post_description = (TextView) mView.findViewById(R.id.festDescription);
            post_description.setText(description);
        }


        public void setDate(String date) {
            TextView post_date = (TextView) mView.findViewById(R.id.festDateTime);
            post_date.setText(date);
        }

        public void setImage(Context ctx, String image) {
            ImageView post_image = (ImageView) mView.findViewById(R.id.festImage);
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

        public void setDisplay_picture(Context ctx, String display_picture) {
            ImageView post_image = (ImageView) mView.findViewById(R.id.festDp);
            Picasso.with(ctx).load(display_picture).into(post_image);
        }

        public void setUsername(String username) {
            TextView post_username = (TextView) mView.findViewById(R.id.festUsername);
            post_username.setText(username);
        }


    }
}