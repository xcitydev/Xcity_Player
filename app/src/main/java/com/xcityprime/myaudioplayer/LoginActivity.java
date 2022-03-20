package com.xcityprime.myaudioplayer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private Button signIn;
    private TextView i_don_have_an_account;
    private FirebaseAuth mAuth;
    private String emailString, passwordString;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.emailSignIn);
        password = findViewById(R.id.passwordSignIn);
        signIn = findViewById(R.id.signInButton);
        i_don_have_an_account = findViewById(R.id.dont_have_acct);
        mAuth = FirebaseAuth.getInstance();
        i_don_have_an_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailString = email.getText().toString();
                passwordString = password.getText().toString();
                if(emailString.isEmpty()){
                    email.setError("Email is empty");
                }else if (passwordString.isEmpty()){
                    password.setError("Password is empty");
                }else {
                    signIn(emailString, passwordString);
                }
            }
        });

    }

    private void signIn(String email, String password) {
        // [START sign_in_with_email]
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Registering "+email);
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.setIcon(R.drawable.picsart2);
        progressDialog.setMessage("loading..");
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            if (user.isEmailVerified()){
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            }else {
                                Toast.makeText(LoginActivity.this, "Kindly Verify your email!", Toast.LENGTH_SHORT).show();
                            }
                            updateUI(user);
                        } else {
                            progressDialog.dismiss();
                            // If sign in fails, display a message to the user.
                            updateUI(null);
                        }
                    }
                });
        // [END sign_in_with_email]
    }

    private void updateUI(FirebaseUser user) {

    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
        }
    }
}