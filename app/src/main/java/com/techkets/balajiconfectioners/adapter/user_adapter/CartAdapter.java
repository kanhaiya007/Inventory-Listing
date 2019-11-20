package com.techkets.balajiconfectioners.adapter.user_adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.techkets.balajiconfectioners.R;
import com.techkets.balajiconfectioners.model.OrderDetails;
import com.techkets.balajiconfectioners.util.Constants;

import java.util.List;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.Items> {
    private Context context;
    private List<OrderDetails> orderDetailsList;
    private LayoutInflater inflater;
    private List<String> orderId;
    private ProgressDialog progressDialog;

    ///////////////////////////////
    private FirebaseFirestore firestore;
    private DocumentReference documentReference;
    private FirebaseAuth auth;

    public CartAdapter(Context context, List<OrderDetails> orderDetailsList, List<String> orderId) {
        this.context = context;
        this.orderDetailsList = orderDetailsList;
        inflater = LayoutInflater.from(context);
        this.orderId = orderId;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Deleting..");
    }

    @NonNull
    @Override
    public Items onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Items(inflater.inflate(R.layout.cart_card, parent, false), context);
    }

    @Override
    public void onBindViewHolder(@NonNull Items holder, int position) {
        OrderDetails orderDetails = orderDetailsList.get(position);
        holder.itemName.setText(String.format("Name: %s", orderDetails.getItemDetails().getName()));
        holder.itemPrice.setText(String.format("Price: %s", orderDetails.getItemDetails().getPrice()));
        holder.itemCategory.setText(String.format("Category: %s", orderDetails.getItemDetails().getCategory()));
        holder.itemBrand.setText(String.format("Brand: %s", orderDetails.getItemDetails().getBrand()));
        holder.itemQuantity.setText(String.format("Quantity: %s", orderDetails.getOrderQuantity()));
        Picasso.get().load(orderDetails.getItemDetails().getImgeUrl()).placeholder(R.drawable.balaji).fit().into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return orderDetailsList.size();
    }

    public class Items extends RecyclerView.ViewHolder {
        TextView itemName, itemBrand, itemCategory, itemPrice, itemQuantity;
        Button delete;
        ImageView imageView;

        public Items(@NonNull View itemView, Context ctx) {
            super(itemView);

            itemName = itemView.findViewById(R.id.cart_itemName);
            itemBrand = itemView.findViewById(R.id.cart_itemBrand);
            itemCategory = itemView.findViewById(R.id.cart_itemCategory);
            itemPrice = itemView.findViewById(R.id.cart_itemPrice);
            itemQuantity = itemView.findViewById(R.id.cart_itemQuantity);
            delete = itemView.findViewById(R.id.cart_delete);
            imageView = itemView.findViewById(R.id.cart_imageView);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressDialog.show();
                    deleteitem(getAdapterPosition());
                }
            });


        }

        private void deleteitem(final int adapterPosition) {
            firestore = FirebaseFirestore.getInstance();
            auth = FirebaseAuth.getInstance();
            documentReference = firestore.collection(Constants.ADD_TO_CART).document(auth.getUid()).collection(Constants.CART_ITEMS)
                    .document(orderId.get(adapterPosition));
            documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    orderDetailsList.remove(adapterPosition);
                    orderId.remove(adapterPosition);
                    notifyDataSetChanged();
                    progressDialog.dismiss();
                    Snackbar.make(itemView,"Deleted",Snackbar.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Snackbar.make(itemView,"Deleted",Snackbar.LENGTH_LONG).show();
                }
            });

        }
    }
}
