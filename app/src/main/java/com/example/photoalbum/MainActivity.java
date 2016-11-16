package com.example.photoalbum;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.photoalbum.LoginActivity;
import com.example.photoalbum.ProfileActivity;
import com.example.photoalbum.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {


    private RecyclerView photoList;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUsers;

    private StorageReference mStorage;
    private ProgressDialog mProgress;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();

        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null){

                    Intent loginIntent=new Intent(MainActivity.this,LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);

                }

            }
        };

        mStorage = FirebaseStorage.getInstance().getReference();

        //To read or write data from the database, i need an instance of DatabaseReference
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Photo_album");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);
        mDatabase.keepSynced(true);

        photoList=(RecyclerView)findViewById(R.id.photo_list);

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        photoList.setHasFixedSize(true);
        photoList.setLayoutManager(layoutManager);


    }

    @Override
    protected void onStart() {
        super.onStart();

        //checkUserExist();
        mAuth.addAuthStateListener(mAuthListener);

        FirebaseRecyclerAdapter<Photo,PhotoViewHolder> firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<Photo, PhotoViewHolder>(

                Photo.class,
                R.layout.photo_row,
                PhotoViewHolder.class,
                mDatabase

        ) {

            @Override
            protected void populateViewHolder(PhotoViewHolder viewHolder, Photo model, int position) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setLocation(model.getLocation());
                viewHolder.setImage(getApplicationContext(),model.getImage());
            }
        };

        photoList.setAdapter(firebaseRecyclerAdapter);
    }


    private void checkUserExist() {
        final String user_id= mAuth.getCurrentUser().getUid();

        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChild(user_id)){


                    /*Intent mainIntent=new Intent(MainActivity.this,LoginActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);*/
                    Intent setupIntent=new Intent(MainActivity.this,SetupActivity.class);
                    setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(setupIntent);

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });


    }


   /* @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }*/


    public static class PhotoViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public PhotoViewHolder(View itemView) {
            super(itemView);

            mView=itemView;
        }

        public void setTitle(String title){

            TextView post_title=(TextView)mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }

        public void setLocation(String location){

            TextView post_location=(TextView)mView.findViewById(R.id.post_location);
            post_location.setText(location);

        }

        public void setImage(Context ctx, String image){

            ImageView post_image = (ImageView) mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(image).into(post_image);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add){
            startActivity(new Intent(MainActivity.this, PhotoActivity.class));
        }

        else if (item.getItemId() == R.id.action_logout){

            mAuth.signOut();
            finish();
            startActivity(new Intent(MainActivity.this,LoginActivity.class));


        }
        return super.onOptionsItemSelected(item);
    }




}
