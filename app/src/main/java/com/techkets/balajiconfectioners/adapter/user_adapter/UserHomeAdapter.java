package com.techkets.balajiconfectioners.adapter.user_adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.techkets.balajiconfectioners.R;
import com.techkets.balajiconfectioners.model.ItemDetails;
import com.techkets.balajiconfectioners.model.OrderDetails;
import com.techkets.balajiconfectioners.util.Constants;

import java.util.List;

public class UserHomeAdapter extends RecyclerView.Adapter<UserHomeAdapter.ViewHolder> {
    private List<ItemDetails> itemDetailsList;
    private Context context;
    private LayoutInflater inflater;


    //Firestore variables
    private FirebaseFirestore firestore;
    private CollectionReference collectionReference;

    //Firebase Auth variables to fetch user uid so that document id can be set as user id

    private FirebaseAuth auth;
    // private FirebaseUser firebaseUser;

    //progress dialog
    private ProgressDialog progressDialog;

    public UserHomeAdapter(Context context, List<ItemDetails> itemDetails) {
        this.context = context;
        this.itemDetailsList = itemDetails;
        this.inflater = LayoutInflater.from(context);
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Wait...");

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.user_home_card, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemDetails itemDetails = itemDetailsList.get(position);
        holder.name.setText(itemDetails.getName());
        holder.price.setText(String.valueOf(itemDetails.getPrice()));
        holder.category.setText(itemDetails.getCategory());
        holder.brand.setText(itemDetails.getBrand());
        Picasso.get().load(itemDetails.getImgeUrl()).placeholder(R.drawable.balaji).fit().into(holder.image);

    }

    @Override
    public int getItemCount() {
        return itemDetailsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, brand, category, price;
        Button order;
        ImageView cancel;
        EditText orderCount;
        ImageView image;

        public ViewHolder(@NonNull final View itemView, Context ctx) {
            super(itemView);
            context = ctx;
            name = itemView.findViewById(R.id.user_itemName);
            brand = itemView.findViewById(R.id.user_itemBrand);
            category = itemView.findViewById(R.id.user_itemCategory);
            price = itemView.findViewById(R.id.user_itemPrice);
            cancel = itemView.findViewById(R.id.user_cancelorder);
            order = itemView.findViewById(R.id.user_order);
            orderCount = itemView.findViewById(R.id.user_order_count);
            image = itemView.findViewById(R.id.user_imageView);

            order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!orderCount.getText().toString().equals("0") && orderCount.getText().toString() != null && !orderCount.getText().toString().equals("")) {
                        progressDialog.show();
                        OrderDetails orderDetails = new OrderDetails();
                        orderDetails.setItemDetails(itemDetailsList.get(getAdapterPosition()));
                        orderDetails.setOrderQuantity(Integer.parseInt(orderCount.getText().toString()));
                        addToCart(orderDetails);
                    } else {
                        Snackbar.make(itemView, "Enter Quantity First", 2000).show();
                    }

                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    orderCount.setText("0");
                    order.setEnabled(true);
                    order.setTextColor(Color.WHITE);
                }
            });

        }

        private void addToCart(OrderDetails orderDetails) {
            firestore = FirebaseFirestore.getInstance();
            auth = FirebaseAuth.getInstance();
            collectionReference = firestore.collection(Constants.ADD_TO_CART).document(auth.getCurrentUser().getUid()).collection(Constants.CART_ITEMS);
            collectionReference.add(orderDetails).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Snackbar.make(itemView, "Added to Cart", 2000).show();
                    order.setEnabled(false);
                    order.setTextColor(Color.argb(80,255,255,255));
                    progressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Snackbar.make(itemView, "Failed", 2000).show();
                    progressDialog.dismiss();
                }
            });
        }
    }
}
