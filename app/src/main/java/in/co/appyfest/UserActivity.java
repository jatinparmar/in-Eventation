package in.co.appyfest;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
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

public class UserActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private String mParam1;
    private String mParam2;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseLike;

    private RecyclerView mFestList;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextView mUserEmail,mUserName;
    private ImageView mImageDp;
    private FirebaseUser mUser;
    private EditText mSearchText;
    private DatabaseReference mDatabaseCurrent;
    private ImageButton mSearchButton;


    private boolean mProcessLike=false;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        mSearchButton= (ImageButton)findViewById(R.id.search_btn);
        mSearchText=(EditText)findViewById(R.id.search_field);




        mDatabaseUsers= FirebaseDatabase.getInstance().getReference().child("Organisers");

        mDatabase= FirebaseDatabase.getInstance().getReference().child("Fest");
        mDatabaseLike=FirebaseDatabase.getInstance().getReference().child("Likes");




        mAuth=FirebaseAuth.getInstance();
        mUser= mAuth.getCurrentUser();
        mDatabaseCurrent = FirebaseDatabase.getInstance().getReference().child("Organisers").child(mUser.getUid());

        mDatabaseCurrent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {



                mUserName=(TextView)findViewById(R.id.userName);
                String name = dataSnapshot.child("name").getValue().toString();
                mUserName.setText(name);

                mUserEmail=(TextView)findViewById(R.id.textViewEmail);
                String email = dataSnapshot.child("email").getValue().toString();
                mUserEmail.setText(email);

                mImageDp=(ImageView)findViewById(R.id.imageDp);
                String dp = dataSnapshot.child("logo").getValue().toString();
                Picasso.with(UserActivity.this).load(dp).into(mImageDp);



            }





            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Toast.makeText(UserActivity.this,"Unable to retreive",Toast.LENGTH_SHORT).show();
            }
        });


        mDatabaseUsers.keepSynced(true);
        mDatabaseCurrent.keepSynced(true);
        mDatabase.keepSynced(true);
        mDatabaseLike.keepSynced(true);







        mFestList=(RecyclerView)findViewById(R.id.festsList);

        // mFestList.setLayoutManager(new LinearLayoutManager(this));
        mAuthListener=new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null)
                {
                    Intent loginIntent= new Intent(UserActivity.this,LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);


                }
                else
                {

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


        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        mFestList.setHasFixedSize(true);
        mFestList.setLayoutManager(layoutManager);

        checkUserExist();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }





    @Override
    public void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);




        FirebaseRecyclerAdapter<Fest,FestViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Fest, UserActivity.FestViewHolder>(
                Fest.class,R.layout.fest_row,UserActivity.FestViewHolder.class,mDatabase

        )

        {
            @Override
            protected void populateViewHolder(UserActivity.FestViewHolder viewHolder, Fest model, int position) {

                final String post_key = getRef(position).getKey();

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDescription(model.getDescription());
                viewHolder.setImage(getApplicationContext(),model.getImage());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setDate(model.getDate());
                viewHolder.setDisplay_picture(getApplicationContext(),model.getDisplay_picture());
                viewHolder.setLikeBtn(post_key);

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });



                viewHolder.mLikeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mProcessLike=true;


                        mDatabaseLike.addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {




                                if(mProcessLike)
                                {
                                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                                        mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();

                                        mProcessLike=false;

                                    }
                                    else
                                    {

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

    public static class FestViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        ImageButton mLikeButton;
        DatabaseReference mDatabaseLike;
        FirebaseAuth mAuthLike;


        public FestViewHolder(View itemView) {
            super(itemView);

            mView=itemView;
            mLikeButton=(ImageButton)mView.findViewById(R.id.festLikeButton);
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
            TextView post_title=(TextView)mView.findViewById(R.id.festTitle);
            post_title.setText(title);
        }

        public void setDescription(String description)
        {
            TextView post_description=(TextView)mView.findViewById(R.id.festDescription);
            post_description.setText(description);
        }


        public void setDate(String date)
        {
            TextView post_date=(TextView)mView.findViewById(R.id.festDateTime);
            post_date.setText(date);
        }

        public void setImage(Context ctx,String image)
        {
            ImageView post_image=(ImageView)mView.findViewById(R.id.festImage);
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
            ImageView post_image=(ImageView)mView.findViewById(R.id.festDp);
            Picasso.with(ctx).load(display_picture).into(post_image);
        }

        public void setUsername(String username)
        {
            TextView post_username=(TextView)mView.findViewById(R.id.festUsername);
            post_username.setText(username);
        }





    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }



    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed()
    {


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        else if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        else
        {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);

        }

           //
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
            Intent intent=new Intent(UserActivity.this,SetupActivity.class);
            startActivity(intent);
        }

        if (id==R.id.action_logout)
        {
            //mAuth=FirebaseAuth.getInstance();
            mAuth.signOut();
            LoginManager.getInstance().logOut();
            Intent logout=new Intent(UserActivity.this,ChooseActivity.class);
            startActivity(logout);
            finish();

           // updateUI();


        }
        if(id==R.id.action_add)
        {

            updateDisplay(new AddEventFragment());
        }



        return false;
    }


    private void updateDisplay(Fragment fragment) {

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }



    private void checkUserExist()
    {
        if(mAuth.getCurrentUser()!=null) {
            final String user_id = mAuth.getCurrentUser().getUid();
            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(user_id)) {
                        Intent userintent = new Intent(UserActivity.this, SetupActivity.class);
                        userintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(userintent);
                        Toast.makeText(UserActivity.this, "checkuserexist method works properly", Toast.LENGTH_LONG).show();

                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }


    private void updateUI()
    {
        Toast.makeText(UserActivity.this,"You are logged Out",Toast.LENGTH_LONG).show();
        Intent intent=new Intent(UserActivity.this,ChooseActivity.class);
        startActivity(intent);
        finish();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();



        if (id == R.id.nav_camera) {
            Intent qrIntent= new Intent(UserActivity.this,QRScannerActivity.class);
            startActivity(qrIntent);
        }

        //else if (id == R.id.nav_gallery) {

        //}
        else if (id == R.id.nav_slideshow) {
                Intent i = new Intent(UserActivity.this,CurrentUserPostsActivity.class);
                startActivity(i);
        }
        else if (id == R.id.nav_manage) {
            Intent sponsor = new Intent(UserActivity.this,SponsorsActivity.class);
            startActivity(sponsor);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



}




