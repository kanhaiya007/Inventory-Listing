package com.techkets.balajiconfectioners.view;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.techkets.balajiconfectioners.R;
import com.techkets.balajiconfectioners.adapter.CategoryAdapter;
import com.techkets.balajiconfectioners.data.FirebaseHandler;
import com.techkets.balajiconfectioners.model.Category;
import com.techkets.balajiconfectioners.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment{
    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private TextView emptyText;
    private List<Category> categoryList=new ArrayList<>();


    //Firestore instances declaration
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference collectionReference;

    public CategoryFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseFirestore=FirebaseFirestore.getInstance();
        collectionReference=firebaseFirestore.collection(Constants.COLLECTION_CATEGORY_DETAILS);
        getCategoryList();
    }

    private void getCategoryList() {
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult()!=null) {
                        for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                            Category category = snapshot.toObject(Category.class);
                            Log.d("category", "onComplete: " + category.getCategorName());
                            categoryList.add(category);
                            adapter.notifyDataSetChanged();
                        }
                    }
                    else {
                       Toast.makeText(getActivity(),"Category Not Found",Toast.LENGTH_LONG).show();
                    }
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Failed Loading Category", Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        recyclerView=view.findViewById(R.id.category_recyclerView);
        adapter=new CategoryAdapter(getContext(),categoryList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        emptyText=view.findViewById(R.id.empty_text_category);
        return view;
    }

}
