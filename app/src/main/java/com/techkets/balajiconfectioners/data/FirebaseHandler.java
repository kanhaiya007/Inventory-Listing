package com.techkets.balajiconfectioners.data;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseHandler {
    protected static FirebaseFirestore firestore;

    public FirebaseHandler() {


    }

    public static FirebaseFirestore getFirestoreInstance() {
        if (firestore != null) {
            firestore = FirebaseFirestore.getInstance();
        }
        return firestore;
    }

}
