package com.techkets.balajiconfectioners.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.techkets.balajiconfectioners.R;
import com.techkets.balajiconfectioners.adapter.OrderListAdapter;
import com.techkets.balajiconfectioners.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class OrderList extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private CollectionReference collectionReference;

    private View view;

    private RecyclerView recyclerView;
    private OrderListAdapter orderListAdapter;
    private List<com.techkets.balajiconfectioners.model.OrderList> orderListList = new ArrayList<>();
    List<String> orderId=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        view = LayoutInflater.from(this).inflate(R.layout.activity_order_list, null);
        recyclerView = findViewById(R.id.order_list_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderListAdapter = new OrderListAdapter(OrderList.this, orderListList,orderId);
        recyclerView.setAdapter(orderListAdapter);
        getOrderList();
    }

    private void getOrderList() {
        firestore = FirebaseFirestore.getInstance();
        collectionReference = firestore.collection(Constants.ORDER_LIST);
        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        com.techkets.balajiconfectioners.model.OrderList orderList = snapshot.toObject(com.techkets.balajiconfectioners.model.OrderList.class);
                        orderListList.add(orderList);
                        orderId.add(snapshot.getId());
                    }
                    orderListAdapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(OrderList.this,"No orders till now",Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

    }
}
