package com.example.photoalbum;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class PostActivity extends AppCompatActivity {

    private ImageButton ib;
    private EditText etPostFile;
    private EditText etPostLoc;
    private Button submitBtn;

    private Uri imageUri = null;

    private static final int GALLERY_REQUEST = 1;

    private ProgressDialog mProgress;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUser;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    String titleVal;
    String locVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser=mAuth.getCurrentUser();

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Photo_album");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        ib = (ImageButton) findViewById(R.id.imageSelect);
        etPostFile = (EditText) findViewById(R.id.titleField);
        etPostLoc = (EditText) findViewById(R.id.locField);
        submitBtn = (Button) findViewById(R.id.submitButton);

        mProgress = new ProgressDialog(this);

        ib.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
               /* startActivityForResult(intent, 0);*/
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);

                /*
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
               /* startActivityForResult(intent, 0);*/
                /*galleryIntent.putExtra("crop", "true");
                galleryIntent.putExtra("aspectX", 1);
                galleryIntent.putExtra("aspectY",1);
                galleryIntent.putExtra("outputX", 200);
                galleryIntent.putExtra("outputY", 200);
                galleryIntent.putExtra("return-data", true);
                startActivityForResult(galleryIntent, 2);*/

            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startingPosting();


            }


        });


    }

    private void startingPosting() {

        mProgress.setMessage("Posting image...");
        mProgress.show();

        titleVal = etPostFile.getText().toString().trim();
        locVal = etPostLoc.getText().toString().trim();

        if (!TextUtils.isEmpty(titleVal) && !TextUtils.isEmpty(locVal) && imageUri != null) {

            StorageReference filepath = mStorage.child("Photos").child(imageUri.getLastPathSegment());

            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {


                   final Uri downloadUri = taskSnapshot.getDownloadUrl();
                   final DatabaseReference newPost = mDatabase.push();


                    mDatabaseUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            newPost.child("title").setValue(titleVal);
                            newPost.child("location").setValue(locVal);
                            newPost.child("image").setValue(downloadUri.toString());
                            newPost.child("uid").setValue(mCurrentUser.getUid());
                            newPost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){

                                        startActivity(new Intent(PostActivity.this, MainActivity.class));
                                    }

                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    mProgress.dismiss();
                    Toast.makeText(PostActivity.this, "Uploading Finished..", Toast.LENGTH_SHORT).show();




                }
            });



        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode== GALLERY_REQUEST && resultCode==RESULT_OK  && data!=null) {

            /*if (requestCode== 2 && resultCode==RESULT_OK  && data!=null) {*/


            imageUri = data.getData();

            Picasso.with(PostActivity.this).load(imageUri).noPlaceholder().centerCrop().fit()
                    .into(ib);

           /*ib.setImageURI(imageUri);

               /* Bundle extras = data.getExtras();
                Bitmap image=extras.getParcelable("data");
                ib.setImageBitmap(image);*/
        }

    }
}
