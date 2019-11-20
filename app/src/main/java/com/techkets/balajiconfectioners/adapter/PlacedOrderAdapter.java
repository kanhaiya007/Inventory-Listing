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

public class PlacedOrderAdapter extends RecyclerView.Adapter<PlacedOrderAdapter.Items> {
    private Context context;
    private List<OrderList> orderLists;
    private LayoutInflater inflater;

    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private RecyclerView recyclerView;
    private FirebaseFirestore firestore;

    private ProgressDialog progressDialog;


    public PlacedOrderAdapter(Context context, List<OrderList> orderLists) {
        this.context = context;
        this.orderLists = orderLists;
        inflater = LayoutInflater.from(context);

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
            View view = LayoutInflater.from(ctx).inflate(R.layout.placed_orders_dialog, null);
            ImageView cancel;

            cancel = view.findViewById(R.id.cancelDialog);
            RecyclerView recyclerView = view.findViewById(R.id.placed_order_recycler);
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


            builder.setView(view);
            alertDialog = builder.create();
            alertDialog.setCancelable(true);
            alertDialog.show();

        }

    }
}
