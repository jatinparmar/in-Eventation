package in.co.appyfest;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class SponsorsActivity extends AppCompatActivity {


    private DatabaseReference mDatabaseSponsor;
    private FirebaseAuth mAuth;
    private RecyclerView mSponsorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sponsors);

        mDatabaseSponsor= FirebaseDatabase.getInstance().getReference().child("Sponsor");
        mAuth =FirebaseAuth.getInstance();

        mDatabaseSponsor.keepSynced(true);
        mSponsorList=(RecyclerView)findViewById(R.id.sponsorRecycler);

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mSponsorList.setHasFixedSize(true);
        mSponsorList.setLayoutManager(layoutManager);

    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Sponsor,SponsorViewHolder> firebaseRecyclerAdapter=
        new FirebaseRecyclerAdapter<Sponsor, SponsorsActivity.SponsorViewHolder>(

                Sponsor.class,
                R.layout.sponsor_row,
                SponsorViewHolder.class,
                mDatabaseSponsor


        ) {
            @Override
            protected void populateViewHolder(SponsorsActivity.SponsorViewHolder viewHolder, Sponsor model, int position) {

                viewHolder.setName(model.getName());
                viewHolder.setAddress(model.getAddress());
                viewHolder.setContact(model.getContact());
                viewHolder.setLogo(getApplicationContext(),model.getLogo());



            }
        };

        mSponsorList.setAdapter(firebaseRecyclerAdapter);

    }


    public static class SponsorViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public SponsorViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setName(String name)
        {
            TextView sponsor_name=(TextView)mView.findViewById(R.id.sponsorName);
            sponsor_name.setText(name);
        }

        public void setContact(String contact)
        {
            TextView sponsor_contact=(TextView)mView.findViewById(R.id.sponsorContact);
            sponsor_contact.setText(contact);

        }

        public void setAddress(String address)
        {
            TextView sponsor_address=(TextView)mView.findViewById(R.id.sponsorAddress);
            sponsor_address.setText(address);
        }

        public void setLogo(Context ctx, String logo)
        {
            ImageView sponsor_logo=(ImageView)mView.findViewById(R.id.sponsorLogo);
            Picasso.with(ctx).load(logo).into(sponsor_logo);
        }



    }

}
