package com.techkets.balajiconfectioners.view;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.techkets.balajiconfectioners.R;
import com.techkets.balajiconfectioners.adapter.ItemListAdapterHome;
import com.techkets.balajiconfectioners.adapter.PlacedOrderAdapter;
import com.techkets.balajiconfectioners.model.OrderDetails;
import com.techkets.balajiconfectioners.model.OrderList;
import com.techkets.balajiconfectioners.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfirmOrders extends Fragment {

    private RecyclerView recyclerView;
    private List<OrderList> orderDetailsList=new ArrayList<>();
    private PlacedOrderAdapter placedOrderAdapter;


    private FirebaseFirestore firestore;
    private CollectionReference collectionReference;
    public ConfirmOrders() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_confirm_orders, container, false);
        recyclerView=view.findViewById(R.id.placedOrder_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        placedOrderAdapter=new PlacedOrderAdapter(getContext(),orderDetailsList);
        recyclerView.setAdapter(placedOrderAdapter);

        getPlacedOrders();

        return view;
    }

    private void getPlacedOrders() {
        firestore=FirebaseFirestore.getInstance();
        collectionReference=firestore.collection(Constants.PLACED_ORDERS);
        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()){
                    for (DocumentSnapshot snapshot:queryDocumentSnapshots){
                        OrderList orderList=snapshot.toObject(OrderList.class);
                        orderDetailsList.add(orderList);
                    }
                    placedOrderAdapter.notifyDataSetChanged();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Failed to load Placed Orders",Toast.LENGTH_SHORT).show();
            }
        });
    }


}
