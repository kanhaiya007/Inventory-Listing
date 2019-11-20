package com.techkets.balajiconfectioners.view.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.techkets.balajiconfectioners.R;
import com.techkets.balajiconfectioners.adapter.user_adapter.CartAdapter;
import com.techkets.balajiconfectioners.model.OrderDetails;
import com.techkets.balajiconfectioners.model.OrderList;
import com.techkets.balajiconfectioners.model.UserDetails;
import com.techkets.balajiconfectioners.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class UserCart extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<OrderDetails> orderDetailsList = new ArrayList<>();

    private FirebaseFirestore firestore;
    private CollectionReference collectionReference;
    private FirebaseAuth firebaseAuth;

    private Button orderPlace;

    private String uid;                  // This will hold current user uid;
    private View view;

    private OrderList orderList = new OrderList();
    private List<OrderDetails> detailsList = new ArrayList<>();
    private UserDetails userDetails = new UserDetails();

    private List<String> docId = new ArrayList<>();
    private List<String> orderId = new ArrayList<>();

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = LayoutInflater.from(this).inflate(R.layout.activity_main2, null);
        setContentView(view);
        progressDialog = new ProgressDialog(UserCart.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait");
        recyclerView = findViewById(R.id.cart_recycler);
        cartAdapter = new CartAdapter(UserCart.this, orderDetailsList, orderId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(cartAdapter);
        orderPlace = findViewById(R.id.place_order);
        getCartOrders();
        getUserDetails();


        orderPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                placeOrder();
            }
        });
    }

    private void getUserDetails() {
        String uid=getUserUid();
        Log.d("TAGS", uid);
        firestore=FirebaseFirestore.getInstance();
        collectionReference=firestore.collection(Constants.COLLECTION_USER_DETAILS);
        collectionReference.whereEqualTo("userId",uid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()){
                    UserDetails userDetails=new UserDetails();
                    for (DocumentSnapshot snapshot:queryDocumentSnapshots){
                        userDetails=snapshot.toObject(UserDetails.class);
                        Log.d("USERGET", "onSuccess: "+userDetails.getUserId()+" "+userDetails.getUserName());
                    }
                    orderList.setUsername(userDetails.getUserName());
                    orderList.setUserMob(String.valueOf(userDetails.getUserMobileNo()));
                    orderList.setUserId(userDetails.getUserId());
                    orderList.setUserAddress(userDetails.getUserAddress());

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void placeOrder() {
        firestore = FirebaseFirestore.getInstance();
        String uid = getUserUid();
        collectionReference = firestore.collection(Constants.ADD_TO_CART).document(uid).collection(Constants.CART_ITEMS);
        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null) {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        OrderDetails orderDetails = snapshot.toObject(OrderDetails.class);
                        docId.add(snapshot.getId());
                        detailsList.add(orderDetails);
                    }
                    if (!detailsList.isEmpty()) {
                        orderList.setOrderDetailsList(detailsList);
                        sendOrderToAdmin();
                    }
                    else {
                        Snackbar.make(view,"Empty cart",3000).show();
                    }

                } else {
                    Snackbar.make(view, "Cart is Empty", 3000).show();
                    progressDialog.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(view, "Something went wrong!", 3000).show();
                progressDialog.dismiss();

            }
        });
    }

    private void sendOrderToAdmin() {

        firestore = FirebaseFirestore.getInstance();
        collectionReference = firestore.collection(Constants.ORDER_LIST);
        collectionReference.add(orderList).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                deleteCartItems();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(view, "Something went wrong!", 3000).show();
                progressDialog.dismiss();
            }
        });
    }

    private void deleteCartItems() {
        firestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference;
        for (String id : docId) {
            documentReference = firestore.collection(Constants.ADD_TO_CART).document(getUserUid()).collection(Constants.CART_ITEMS).document(id);
            documentReference.delete().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //progressDialog.dismiss();
                    Snackbar.make(view, "Something went wrong!", 3000).show();
                }
            });
        }
        orderDetailsList.clear();
        cartAdapter.notifyDataSetChanged();
        //progressDialog.dismiss();

        Snackbar.make(view, "Order Successful", 3000).show();

        startActivity(new Intent(UserCart.this, UserView.class));
        finish();

    }

    private void getCartOrders() {
        String uid = getUserUid();
        firestore = FirebaseFirestore.getInstance();
        collectionReference = firestore.collection(Constants.ADD_TO_CART).document(uid).collection(Constants.CART_ITEMS);
        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                    OrderDetails orderDetails = snapshot.toObject(OrderDetails.class);
                    orderDetailsList.add(orderDetails);
                    orderId.add(snapshot.getId());
                }
                cartAdapter.notifyDataSetChanged();
                if (orderDetailsList.isEmpty()) {
                    Toast.makeText(UserCart.this, "No item in Cart", Toast.LENGTH_LONG).show();
                    onBackPressed();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(view, "Failed to Load Cart Items", 3000).show();
                progressDialog.dismiss();
            }
        });


    }

    private String getUserUid() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid();
        return uid;
    }


}