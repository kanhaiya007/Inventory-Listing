package com.techkets.balajiconfectioners.view;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.techkets.balajiconfectioners.R;
import com.techkets.balajiconfectioners.adapter.UserAdapter;
import com.techkets.balajiconfectioners.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserDetails extends Fragment {

    private FirebaseFirestore firestore;
    private CollectionReference collectionReference;

    private RecyclerView recyclerView;
    private UserAdapter adapter;

    private List<com.techkets.balajiconfectioners.model.UserDetails> userDetailsList = new ArrayList<>();

    public UserDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_details, container, false);
        recyclerView = view.findViewById(R.id.userRecycler);
        adapter = new UserAdapter(getContext(), userDetailsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        getUserDetails();
        recyclerView.setAdapter(adapter);
        return view;
    }

    private void getUserDetails() {
        firestore = FirebaseFirestore.getInstance();
        collectionReference = firestore.collection(Constants.COLLECTION_USER_DETAILS);
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    userDetailsList.clear();
                    for (DocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                        com.techkets.balajiconfectioners.model.UserDetails userDetails = snapshot.toObject(com.techkets.balajiconfectioners.model.UserDetails.class);
                        userDetailsList.add(userDetails);
                        adapter.notifyDataSetChanged();
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
