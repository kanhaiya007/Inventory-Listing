package com.techkets.balajiconfectioners.view.user;


import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.techkets.balajiconfectioners.R;
import com.techkets.balajiconfectioners.adapter.user_adapter.UserHomeAdapter;
import com.techkets.balajiconfectioners.model.Category;
import com.techkets.balajiconfectioners.model.ItemDetails;
import com.techkets.balajiconfectioners.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserHome extends Fragment {

    private RecyclerView recyclerView;
    private UserHomeAdapter userHomeAdapter;
    private List<ItemDetails> itemDetailsList = new ArrayList<>();
    private List<ItemDetails> initallist = new ArrayList<>();

    private Spinner spinner;
    private List<String> categoryList = new ArrayList<>();
    private ArrayAdapter<String> stringArrayAdapter;

    private FirebaseFirestore firestore;
    private CollectionReference collectionReference;

   // private ProgressDialog progressDialog;


    public UserHome() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_home, container, false);
//        progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setCancelable(false);
//        progressDialog.setMessage("Loading");
        recyclerView = view.findViewById(R.id.user_recycler_view);
        userHomeAdapter = new UserHomeAdapter(getContext(), itemDetailsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        spinner = view.findViewById(R.id.user_home_Spinner);
        stringArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.user_category_spinner, categoryList);
        spinner.setAdapter(stringArrayAdapter);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.setAdapter(userHomeAdapter);
        getItemList();
        getCatList();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                filterItems(categoryList.get(i));

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    private void filterItems(String category) {
        itemDetailsList.clear();
        if (category.equals(categoryList.get(0))) {
            itemDetailsList.addAll(initallist);
            userHomeAdapter.notifyDataSetChanged();

        } else {
            for (int i = 0; i < initallist.size(); i++) {
                if (category.equals(initallist.get(i).getCategory())) {
                    itemDetailsList.add(initallist.get(i));
                }
            }
            userHomeAdapter.notifyDataSetChanged();
    //        progressDialog.dismiss();
        }
    }

    private void getItemList() {
        setFirebase();

        collectionReference = firestore.collection(Constants.COLLECTION_ITEM_DETAILS);
        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null) {
                    itemDetailsList.clear();
                    initallist.clear();
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        ItemDetails itemDetails = snapshot.toObject(ItemDetails.class);
                        itemDetailsList.add(itemDetails);
                        initallist.add(itemDetails);
                        userHomeAdapter.notifyDataSetChanged();
                    }
                } else {
      //              progressDialog.dismiss();
                    Toast.makeText(getActivity(), "No item found", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
              //  progressDialog.dismiss();
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getCatList() {
        categoryList.clear();
        setFirebase();
        collectionReference = firestore.collection(Constants.COLLECTION_CATEGORY_DETAILS);
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    categoryList.add("Select Category");
                    for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                        Category category = snapshot.toObject(Category.class);
                        // Log.d("category", "onComplete: " + category.getCategorName());
                        categoryList.add(category.getCategorName());
                        stringArrayAdapter.notifyDataSetChanged();
                    }
        //            progressDialog.dismiss();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
          //      progressDialog.dismiss();
                Toast.makeText(getActivity(), "Failed Loading Category", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void setFirebase() {
        if (firestore == null) {
            firestore = FirebaseFirestore.getInstance();
        }
    }
}
