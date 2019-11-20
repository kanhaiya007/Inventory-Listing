package com.techkets.balajiconfectioners.view.user;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.techkets.balajiconfectioners.R;
import com.techkets.balajiconfectioners.adapter.ItemListAdapterHome;
import com.techkets.balajiconfectioners.adapter.user_adapter.ConfirmOrderAdapter;
import com.techkets.balajiconfectioners.model.OrderDetails;
import com.techkets.balajiconfectioners.util.Constants;
import com.techkets.balajiconfectioners.view.OrderList;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PreviousOrder extends Fragment {
    private RecyclerView recyclerView;
    private ConfirmOrderAdapter confirmOrderAdapter;

    private FirebaseFirestore firestore;
    private CollectionReference collectionReference;

    private FirebaseAuth firebaseAuth;

    private List<OrderDetails> orderDetailsList=new ArrayList<>();



    public PreviousOrder() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_previous_order, container, false);
        recyclerView=view.findViewById(R.id.confirm_order_recycler);
        confirmOrderAdapter=new ConfirmOrderAdapter(getContext(),orderDetailsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(confirmOrderAdapter);

        getPreviousOrders();


        return view;
    }

    private void getPreviousOrders() {
        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        Log.e("PRORD",firebaseAuth.getCurrentUser().getUid());
        collectionReference=firestore.collection(Constants.CONFIRM_ORDERS).document(firebaseAuth.getCurrentUser().getUid()).collection(Constants.COLLECTION_ORDER_PREVIOUS);
        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()){
                    for (DocumentSnapshot snapshot:queryDocumentSnapshots){
                        OrderDetails orderDetails=snapshot.toObject(OrderDetails.class);
                        orderDetailsList.add(orderDetails);
                        Log.e("PRORD",orderDetails.getItemDetails().getName() );
                    }
                    confirmOrderAdapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(getContext(),"No Data received",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

}
