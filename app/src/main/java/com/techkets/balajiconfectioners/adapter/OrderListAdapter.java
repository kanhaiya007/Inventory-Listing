package com.techkets.balajiconfectioners.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.techkets.balajiconfectioners.R;
import com.techkets.balajiconfectioners.model.OrderDetails;
import com.techkets.balajiconfectioners.model.OrderList;
import com.techkets.balajiconfectioners.util.Constants;
import com.techkets.balajiconfectioners.view.user.UserView;

import java.util.List;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.Items> {
    private Context context;
    private List<OrderList> orderLists;
    private LayoutInflater inflater;

    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private RecyclerView recyclerView;
    private List<String> orderId;

    private FirebaseFirestore firestore;

    private ProgressDialog progressDialog;


    public OrderListAdapter(Context context, List<OrderList> orderLists, List<String> orderId) {
        this.context = context;
        this.orderLists = orderLists;
        inflater = LayoutInflater.from(context);
        this.orderId = orderId;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
    }

    @NonNull
    @Override
    public Items onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Items(inflater.inflate(R.layout.order_confirm_card, parent, false), context);
    }

    @Override
    public void onBindViewHolder(@NonNull Items holder, int position) {
        holder.ct_name.setText(orderLists.get(position).getUsername());
        holder.ct_phNo.setText(orderLists.get(position).getUserMob());
        holder.ct_add.setText(orderLists.get(position).getUserAddress());
    }

    @Override
    public int getItemCount() {
        return orderLists.size();
    }

    public class Items extends RecyclerView.ViewHolder {
        TextView ct_name, ct_add, ct_phNo;
        Button viewDetails;
        Context ctx;

        public Items(@NonNull View itemView, Context context) {
            super(itemView);
            ctx = context;
            ct_add = itemView.findViewById(R.id.customer_address);
            ct_name = itemView.findViewById(R.id.customer_name);
            ct_phNo = itemView.findViewById(R.id.customer_ph_number);
            viewDetails = itemView.findViewById(R.id.customer_view_order_details);

            viewDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createOrderListDialog();
                }
            });
        }

        private void createOrderListDialog() {
            builder = new AlertDialog.Builder(ctx);
            View view = LayoutInflater.from(ctx).inflate(R.layout.order_details_list, null);
            Button delete, confirm;
            ImageView cancel;
            confirm = view.findViewById(R.id.orderConfirm);
            delete = view.findViewById(R.id.orderDelete);
            cancel = view.findViewById(R.id.cancelDialog);
            RecyclerView recyclerView = view.findViewById(R.id.orderList_recycler_dialog);
            OrderListDialogAdapter orderListDialogAdapter = new OrderListDialogAdapter(ctx, orderLists.get(getAdapterPosition()).getOrderDetailsList());
            recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
            recyclerView.setAdapter(orderListDialogAdapter);
            orderListDialogAdapter.notifyDataSetChanged();
            //Log.d("TAG123", orderLists.get(getAdapterPosition()).getOrderDetailsList().get(0).getOrderQuantity() + " ");

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressDialog.show();
                    confirmOrders();
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressDialog.show();
                    deleteItem();
                }
            });

            builder.setView(view);
            alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.show();

        }

        private void confirmOrders() {
            firestore = FirebaseFirestore.getInstance();
            CollectionReference collectionReference = firestore.collection(Constants.CONFIRM_ORDERS).document(orderLists.get(getAdapterPosition()).getUserId())
                    .collection(Constants.COLLECTION_ORDER_PREVIOUS);
            List<OrderDetails> orderDetailsList = orderLists.get(getAdapterPosition()).getOrderDetailsList();
            for (OrderDetails orderDetails : orderDetailsList) {
                collectionReference.add(orderDetails).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed to Confirm Orders", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
            placedOrders();


        }

        private void placedOrders() {
            firestore = FirebaseFirestore.getInstance();
            CollectionReference collectionReference = firestore.collection(Constants.PLACED_ORDERS);
            collectionReference.add(orderLists.get(getAdapterPosition())).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    deleteItem();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                }
            });

        }

        private void deleteItem() {
            firestore = FirebaseFirestore.getInstance();
            DocumentReference documentReference = firestore.collection(Constants.ORDER_LIST).document(orderId.get(getAdapterPosition()));
            documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    orderLists.remove(getAdapterPosition());
                    orderId.remove(getAdapterPosition());
                    notifyDataSetChanged();

                    if (orderLists.isEmpty()) {
                        context.startActivity(new Intent(context, UserView.class));
                    }
                    alertDialog.dismiss();
                    progressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(context, "Failed to Delete Item", Toast.LENGTH_LONG).show();
                }
            });
        }

    }
}
