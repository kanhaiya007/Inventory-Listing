package com.techkets.balajiconfectioners.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.techkets.balajiconfectioners.R;

public class ForgotPassword extends AppCompatActivity {
    private EditText userEmail;
    private Button sendEmail;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        firebaseAuth = FirebaseAuth.getInstance();
        userEmail = findViewById(R.id.useremail);
        sendEmail = findViewById(R.id.sendMail);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending Mail...");
        progressDialog.setCancelable(false);


        userEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                sendEmail.setTextColor(Color.argb(50, 255, 255, 255));
                sendEmail.setEnabled(false);
                firebaseAuth.sendPasswordResetEmail(userEmail.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ForgotPassword.this, "Check Your Mail", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        startActivity(new Intent(ForgotPassword.this, SignInActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ForgotPassword.this, "Failed", Toast.LENGTH_SHORT).show();
                        sendEmail.setTextColor(Color.WHITE);
                        sendEmail.setEnabled(true);
                    }
                });
            }
        });


    }

    private void checkInputs() {
        String email = userEmail.getText().toString().trim();
        if (!TextUtils.isEmpty(email) && email.length() > 8 &&email.matches(emailPattern)) {
            sendEmail.setTextColor(Color.WHITE);
            sendEmail.setEnabled(true);
        }
        else{
            userEmail.setError("Enter valid Email");
        }
    }
}
