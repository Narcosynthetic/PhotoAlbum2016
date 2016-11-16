package com.example.photoalbum;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class PhotoActivity extends AppCompatActivity {

    ImageView iv;
    ImageView iv_camera;
    ImageView iv_gallery;
    ImageView iv_upload;
   /* Button b;
    Button b_gallery;
    Button b_upload;*/



    private static final int CAMERA_REQUEST_CODE=1;
    private static final int GALLERY_INTENT=2;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);


        iv = (ImageView) findViewById(R.id.imageView1);
        iv_camera = (ImageView) findViewById(R.id.imageView);
        iv_gallery = (ImageView) findViewById(R.id.imageView2);
        iv_upload = (ImageView) findViewById(R.id.imageView3);


        iv_camera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
               /* startActivityForResult(intent, 0);*/
                startActivityForResult(intent,CAMERA_REQUEST_CODE);


            }
        });

        iv_gallery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
               /* startActivityForResult(intent, 0);*/
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_INTENT);


            }
        });

        iv_upload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(new Intent(PhotoActivity.this,PostActivity.class));



            }
        });

    }


   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Bitmap bm = (Bitmap) data.getExtras().get("data");
        iv.setImageBitmap(bm);
        super.onActivityResult(requestCode, resultCode, data);
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CAMERA_REQUEST_CODE && resultCode==RESULT_OK && data!=null){


            Bitmap bm = (Bitmap) data.getExtras().get("data");

            iv.setImageBitmap(bm);
            super.onActivityResult(requestCode, resultCode, data);

          /*  mProgress.setMessage("Uploading Image...");
            mProgress.show();


            Uri uri = data.getData();
            StorageReference filepath=mStorage.child("Photos").child(uri.getLastPathSegment());

            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    mProgress.dismiss();

                    Uri downloadUri=taskSnapshot.getDownloadUrl();

                    Picasso.with(MainActivity.this).load(downloadUri).fit().centerCrop().into(iv);

                    Toast.makeText(MainActivity.this, "Uploading Finished..", Toast.LENGTH_SHORT).show();

                }
            });*/



        }

        else if (requestCode== GALLERY_INTENT && resultCode==RESULT_OK  && data!=null) {


            Uri uri = data.getData();

            Picasso.with(PhotoActivity.this).load(uri).noPlaceholder().centerCrop().fit()
                    .into(iv);

            /*Picasso.with(MainActivity.this).load(uri).placeholder(R.drawable.add_ima).centerCrop().fit()
                    .into(iv);*/

/*
            mProgress.setMessage("Uploading Image...");
            mProgress.show();

            StorageReference filepath=mStorage.child("Photos").child(uri.getLastPathSegment());

            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    mProgress.dismiss();

                    Uri downloadUri=taskSnapshot.getDownloadUrl();

                    Picasso.with(MainActivity.this).load(downloadUri).fit().centerCrop().into(iv);

                    Toast.makeText(MainActivity.this, "Uploading Finished..", Toast.LENGTH_SHORT).show();

                }
            });*/


        }

    }


   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add){
            startActivity(new Intent(PhotoActivity.this, PostActivity.class));
        }

        else if (item.getItemId() == R.id.action_logout){

            //mAuth.signOut();
            finish();
            startActivity(new Intent(this,LoginActivity.class));


        }
        return super.onOptionsItemSelected(item);
    }

}
