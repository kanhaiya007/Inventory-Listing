package com.techkets.balajiconfectioners.view.user;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.techkets.balajiconfectioners.R;
import com.techkets.balajiconfectioners.model.UserDetails;
import com.techkets.balajiconfectioners.util.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileUser extends Fragment {

    private TextView name;
    private TextView address;
    private TextView mobileNo;
    private TextView email;

    private FirebaseFirestore firestore;
    private CollectionReference collectionReference;
    private FirebaseAuth firebaseAuth;


    public ProfileUser() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_user, container, false);
        name=view.findViewById(R.id.userP_name);
        email=view.findViewById(R.id.userP_email);
        mobileNo=view.findViewById(R.id.userP_mobile);
        address=view.findViewById(R.id.userP_add);

        getUserData();


        return view;
    }

    private void getUserData() {
        firebaseAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        collectionReference=firestore.collection(Constants.COLLECTION_USER_DETAILS);
        collectionReference.whereEqualTo("userId",firebaseAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()){
                    UserDetails userDetails;
                    for (DocumentSnapshot snapshot:queryDocumentSnapshots){
                        userDetails=snapshot.toObject(UserDetails.class);
                        name.setText(userDetails.getUserName());
                        address.setText(userDetails.getUserAddress());
                        email.setText(userDetails.getUserEmail());
                        mobileNo.setText(String.valueOf(userDetails.getUserMobileNo()));
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

    }


}
