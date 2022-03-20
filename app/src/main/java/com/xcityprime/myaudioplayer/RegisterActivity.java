package com.xcityprime.myaudioplayer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    private EditText email, password, confirmPassword;
    private Button signUp, skip;
    private TextView already_act;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        confirmPassword = findViewById(R.id.confirmPassword);
        password = findViewById(R.id.password);
        signUp = findViewById(R.id.signUpButton);
        already_act = findViewById(R.id.already_have_acct);
        skip = findViewById(R.id.skipButton);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }
        });
        already_act.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailString = email.getText().toString();
                String passwordString = password.getText().toString();
                String confirmPasswordString = confirmPassword.getText().toString();
                if(emailString.isEmpty()){
                    email.setError("Email is empty");
                }else if (passwordString.isEmpty()){
                    password.setError("Password is empty");
                }else if (confirmPasswordString.isEmpty()){
                    confirmPassword.setError("empty");
                }else {
                    if ((TextUtils.equals(passwordString, confirmPasswordString))){
                        createAccount(emailString, passwordString);
                    }else{
                        confirmPassword.setError("Password doesn't match!");
                        password.setError("Password doesn't match!");
                    }
                }

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        }
    }

    private void createAccount(String email, String password) {
        // [START create_user_with_email]
        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setTitle("Registering "+email);
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.setIcon(R.drawable.picsart2);
        progressDialog.setMessage("loading..");
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null){
                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        String userID = user.getUid();
                                        Users users = new Users(email,
                                                password,
                                                "",
                                                "5","");
                                        db.collection("users").document(user.getUid()).set(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(RegisterActivity.this, "DONE", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                }else{
                                                    Toast.makeText(RegisterActivity.this, "Data Could not be saved", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(RegisterActivity.this, "Failed. "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "Failed. Check your internet connection!", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            // If sign in fails, display a message to the user.
                            updateUI(null);
                        }
                    }
                });
        // [END create_user_with_email]
    }
    private void updateUI(FirebaseUser user) {

    }
}