package com.techkets.balajiconfectioners.view;


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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.techkets.balajiconfectioners.R;
import com.techkets.balajiconfectioners.adapter.ItemListAdapter;
import com.techkets.balajiconfectioners.model.Category;
import com.techkets.balajiconfectioners.model.ItemDetails;
import com.techkets.balajiconfectioners.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemFragment extends Fragment {

    private RecyclerView recyclerView;
    private ItemListAdapter adapter;

    private FirebaseFirestore firestore;
    private CollectionReference collectionReference;
    private List<ItemDetails> itemDetailsList=new ArrayList<>();
    private List<ItemDetails> filterItemsList=new ArrayList<>();

    private Spinner itemSpinner;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> categoryList = new ArrayList<>();

    public ItemFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blank, container, false);

        recyclerView = view.findViewById(R.id.recycler_item);
        itemSpinner = view.findViewById(R.id.itemSpinner);
        adapter = new ItemListAdapter(getContext(), filterItemsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        getCatList();

        arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.category_list_spinner, categoryList);
        itemSpinner.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        itemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        filterItemsList.clear();
        if (category.equals(categoryList.get(0))) {
            filterItemsList.addAll(itemDetailsList);
            adapter.notifyDataSetChanged();
        } else {
            for (int i = 0; i < itemDetailsList.size(); i++) {
                if (category.equals(itemDetailsList.get(i).getCategory())) {
                    filterItemsList.add(itemDetailsList.get(i));
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void getCatList() {
        categoryList.clear();
        firestore = FirebaseFirestore.getInstance();
        collectionReference = firestore.collection(Constants.COLLECTION_CATEGORY_DETAILS);
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    categoryList.add("Select Category");
                    for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                        Category category = snapshot.toObject(Category.class);
                        Log.d("category", "onComplete: " + category.getCategorName());
                        categoryList.add(category.getCategorName());
                        arrayAdapter.notifyDataSetChanged();
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        collectionReference = firestore.collection(Constants.COLLECTION_ITEM_DETAILS);
        getItemList();

    }

    private void getItemList() {
        itemDetailsList.clear();
        filterItemsList.clear();
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null) {
                        for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                            ItemDetails itemDetails = snapshot.toObject(ItemDetails.class);
                            filterItemsList.add(itemDetails);
                            itemDetailsList.add(itemDetails);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(getContext(), "Empty List", Toast.LENGTH_LONG).show();
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

}
