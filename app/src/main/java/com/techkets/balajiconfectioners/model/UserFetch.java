package com.techkets.balajiconfectioners.model;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.techkets.balajiconfectioners.util.Constants;

//This class fetches the current user details
public class UserFetch {
    private static UserDetails userDetails;
    private static FirebaseAuth firebaseAuth;
    private static FirebaseUser firebaseUser;
    private static FirebaseFirestore firestore;
    private static DocumentReference documentReference;

    public static UserDetails getUserDetails() {
        if (userDetails != null) {
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseUser = firebaseAuth.getCurrentUser();
            String uid = firebaseUser.getUid();
            firestore = FirebaseFirestore.getInstance();
            documentReference = firestore.collection(Constants.COLLECTION_USER_DETAILS).document(uid);
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    userDetails = documentSnapshot.toObject(UserDetails.class);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });


        }

        return userDetails;
    }


}
