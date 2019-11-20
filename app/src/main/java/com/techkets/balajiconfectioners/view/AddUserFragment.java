package com.techkets.balajiconfectioners.view;


import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.techkets.balajiconfectioners.R;
import com.techkets.balajiconfectioners.model.UserDetails;
import com.techkets.balajiconfectioners.util.Constants;

import java.util.Objects;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddUserFragment extends Fragment {

    private EditText userEmail;
    private EditText userPass;
    private EditText userName;
    private EditText userAddress;
    private EditText usermobileNo;
    private EditText confirmPass;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    private Button register;

    private FirebaseAuth firebaseAuth;


    private ProgressDialog progressDialog;

    private FirebaseFirestore firestore;
    private CollectionReference collectionReference;


    public AddUserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Registering User");
        progressDialog.setCancelable(false);

        firestore = FirebaseFirestore.getInstance();
        collectionReference = firestore.collection(Constants.COLLECTION_USER_DETAILS);


    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_user, container, false);

        userEmail = view.findViewById(R.id.emailId);
        userName = view.findViewById(R.id.name);
        userPass = view.findViewById(R.id.userPass);
        usermobileNo = view.findViewById(R.id.phNumber);
        userAddress = view.findViewById(R.id.address);
        register = view.findViewById(R.id.register);
        confirmPass = view.findViewById(R.id.confirmPass);


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validation();
            }
        });


        return view;
    }

    private void createUserAccount(String email, String pass) {
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)) {
            firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        getUser(Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()).getUid());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "Something went wrong3", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });
        } else {
            Toast.makeText(getActivity(), "Something went wrong2", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
    }

    private void addUser(UserDetails userDetails) {
        collectionReference.add(userDetails).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                progressDialog.dismiss();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                transaction.replace(R.id.drawerContainer, new HomeFragment()).commit();
                Toast.makeText(getActivity(), "Success", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                //cleanData();
                Toast.makeText(getActivity(), "Failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getUser(String uid) {
        String email = userEmail.getText().toString().trim();
        String pass = userPass.getText().toString().trim();
        String name = userName.getText().toString().trim();
        String mobile = usermobileNo.getText().toString().trim();
        String address = userAddress.getText().toString().trim();
        UserDetails userDetails = new UserDetails();
        userDetails.setUserEmail(email);
        userDetails.setUserName(name);
        userDetails.setUserAddress(address);
        userDetails.setUserMobileNo(Long.parseLong(mobile));
        userDetails.setUserType(Constants.KEY_CUSTOMER);
        userDetails.setUserId(uid);
        userDetails.setUserPassword(pass);
        addUser(userDetails);

    }

    private void validation() {
        String name = userName.getText().toString().trim();
        String email = userEmail.getText().toString().trim();
        String pass = userPass.getText().toString().trim();
        String address = userAddress.getText().toString().trim();
        String mobile = usermobileNo.getText().toString().trim();
        String confirm = confirmPass.getText().toString().trim();

        if (!email.isEmpty()) {
            if (!pass.isEmpty()) {
                if (!name.isEmpty()) {
                    if (!mobile.isEmpty()) {
                        if (!address.isEmpty()) {
                            if (!confirm.isEmpty()) {
                                if (email.matches(emailPattern)) {
                                    if (pass.equals(confirm)) {
                                        if (mobile.length() == 10) {
                                            progressDialog.show();
                                            createUserAccount(email, pass);
                                        } else {
                                            usermobileNo.setError("Enter valid mobile number");
                                        }
                                    } else {
                                        confirmPass.setError("Password Mismatch");
                                        userPass.setError("Password Mismatch");
                                    }

                                } else {
                                    userEmail.setError("Please enter valid email address");
                                }

                            } else {
                                confirmPass.setError("Enter password");
                            }

                        } else {
                            userAddress.setError("Address can't be empty");
                        }

                    } else {
                        usermobileNo.setError("Mobile Number can't be empty");
                    }

                } else {
                    userName.setError("Name can't be empty");
                }

            } else {
                userPass.setError("Password can't be empty");
            }

        } else {
            userEmail.setError("Email can't be empty");
        }

    }


}
