package com.techkets.balajiconfectioners.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.techkets.balajiconfectioners.R;
import com.techkets.balajiconfectioners.model.UserDetails;
import com.techkets.balajiconfectioners.util.Constants;
import com.techkets.balajiconfectioners.view.user.UserView;

public class SignInActivity extends AppCompatActivity {
    private EditText password;
    private EditText email;
    private Button loginBtn;
    private TextView needHelp;
    private CheckBox remember;

    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    private ProgressDialog progressDialog;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in");
        progressDialog.setCancelable(false);

        sharedPreferences=getSharedPreferences("BalajiConfectioners", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();


        email = findViewById(R.id.enterEmail);
        password = findViewById(R.id.enterPasswd);
        loginBtn = findViewById(R.id.loginBtn);
        needHelp = findViewById(R.id.forgotPassword);
        remember = findViewById(R.id.checkbox);

        needHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this, ForgotPassword.class));
            }
        });


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidation();
            }
        });
        if (sharedPreferences.getString("Username",null)!=null && sharedPreferences.getString("Password",null)!=null){
            String email=sharedPreferences.getString("Username","");
            String pass=sharedPreferences.getString("Password","");
            loginuser(email,pass);
        }


    }

    private void checkValidation() {
        String email = this.email.getText().toString().trim();
        String pass = password.getText().toString().trim();
        if (!email.isEmpty()){
            if (!pass.isEmpty()){
                if (email.matches(emailPattern) && email.length()>8){
                    if (pass.length()>6){
                        loginuser(email,pass);
                    }else {
                        password.setError("Enter Valid Password");
                    }
                }else {
                 this.email.setError("Enter Valid Email");
                }
            }
            else {
              password.setError("Enter Password");
            }

        }else {
           this.email.setError("Enter Email");
        }
    }



    private void loginuser(String email, String password) {
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    signInMain(task.getResult().getUser().getUid());
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(SignInActivity.this, "Login failed", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignInActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void signInMain(String uid) {
        CollectionReference collectionReference = firestore.collection(Constants.COLLECTION_USER_DETAILS);
        collectionReference.whereEqualTo("userId", uid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                UserDetails userDetails;
                for (QueryDocumentSnapshot querySnapshot : queryDocumentSnapshots) {
                    userDetails = querySnapshot.toObject(UserDetails.class);
                    if (userDetails.getUserType().equals(Constants.KEY_ADMIN)) {
                        progressDialog.dismiss();
                        if (remember.isChecked()){
                            editor.putString("Username",email.getText().toString().trim());
                            editor.putString("Password",password.getText().toString().trim());
                            editor.commit();
                        }
                        startActivity(new Intent(SignInActivity.this, MainDrawer.class));
                        finish();
                    } else if (userDetails.getUserType().equals(Constants.KEY_CUSTOMER)) {
                        progressDialog.dismiss();
                        if (remember.isChecked()){
                            editor.putString("Username",email.getText().toString().trim());
                            editor.putString("Password",password.getText().toString().trim());
                            editor.commit();
                        }
                        startActivity(new Intent(SignInActivity.this, UserView.class));
                       // Toast.makeText(SignInActivity.this, "Sorry", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(SignInActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        break;
                    }
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.cancel();
                Toast.makeText(SignInActivity.this, "Failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
