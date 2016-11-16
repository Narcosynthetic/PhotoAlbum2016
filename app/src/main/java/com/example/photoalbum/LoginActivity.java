package com.example.photoalbum;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    //define the views//
    private Button buttonsignIn;
    private EditText editTextEmailAddress;
    private EditText editTextPassword;
    private TextView textViewSignUp;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabaseUsers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialize views

        firebaseAuth = FirebaseAuth.getInstance();

        //chech if already logged in
        if(firebaseAuth.getCurrentUser()!=null){
            //start profile activity
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));

        }

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        mDatabaseUsers.keepSynced(true);
        progressDialog =new ProgressDialog(this);

        editTextEmailAddress = (EditText) findViewById(R.id.editTextPassword);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        buttonsignIn = (Button) findViewById(R.id.buttonsignIn);

        textViewSignUp = (TextView) findViewById(R.id.textViewSignUp);

        //attach Listener to button and textview register
        buttonsignIn.setOnClickListener(this);
        textViewSignUp.setOnClickListener(this);




    }

    private void userLogin(){
        String email = editTextEmailAddress.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        //check string is empty
        if(TextUtils.isEmpty(email)){
            //mail is empty
            Toast.makeText(this, "Please Enter Your Email", Toast.LENGTH_SHORT) .show();
            //Stop further execution
            return;
        }
        if(TextUtils.isEmpty(password)){
            //password is empty
            Toast.makeText(this, "Please Enter Your Password", Toast.LENGTH_SHORT) .show();
            //Stop further execution
            return;
        }
        //if all OK show progressbar
        progressDialog.setMessage("Checking Login...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    //start profile activity
                   // finish();
                   // startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    progressDialog.dismiss();
                    checkUserExist();
                }

                else{
                    progressDialog.dismiss();

                    Toast.makeText(LoginActivity.this, "Error Login", Toast.LENGTH_SHORT) .show();

                }

            }
        });
    }

   /* private void userLogin() {
        String email = editTextEmailAddress.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        //check string is empty
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            //mail is empty


            //if all OK show progressbar
            //progressDialog.setMessage("Registering User");
            //progressDialog.show();

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    // progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        //start profile activity
                        //finish();
                        //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        checkUserExist();
                    } else {

                        Toast.makeText(LoginActivity.this, "Error Login", Toast.LENGTH_SHORT).show();

                    }

                }
            });
        }
    }*/



    private void checkUserExist() {
        final String user_id= firebaseAuth.getCurrentUser().getUid();

        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(user_id)){

                    Intent mainIntent=new Intent(LoginActivity.this,MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);

                }

                else{
                    //Toast.makeText(LoginActivity.this, "You need to set up your account", Toast.LENGTH_SHORT) .show();
                    Intent setupIntent=new Intent(LoginActivity.this,SetupActivity.class);
                    setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(setupIntent);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onClick(View view) {
        if(view == buttonsignIn){
            userLogin();
        }
        if(view == textViewSignUp){
            finish();
            startActivity(new Intent(this, RegisterActivity.class));

        }


    }
}
