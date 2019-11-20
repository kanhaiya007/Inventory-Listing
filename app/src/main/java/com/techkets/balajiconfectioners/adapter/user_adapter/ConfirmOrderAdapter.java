package com.techkets.balajiconfectioners.adapter.user_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.techkets.balajiconfectioners.R;
import com.techkets.balajiconfectioners.model.OrderDetails;

import java.util.List;

public class ConfirmOrderAdapter extends RecyclerView.Adapter<ConfirmOrderAdapter.Items> {
    private Context context;
    private List<OrderDetails> orderDetailsList;
    private LayoutInflater inflater;

    public ConfirmOrderAdapter(Context context, List<OrderDetails> orderDetailsList) {
        this.context = context;
        this.orderDetailsList = orderDetailsList;
        inflater=LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public Items onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Items(inflater.inflate(R.layout.confirm_order_card,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull Items holder, int position) {
        holder.name.setText(orderDetailsList.get(position).getItemDetails().getName());
        holder.brand.setText(orderDetailsList.get(position).getItemDetails().getBrand());
        holder.quantity.setText(String.valueOf(orderDetailsList.get(position).getOrderQuantity()));
        holder.price.setText(String.valueOf(orderDetailsList.get(position).getItemDetails().getPrice()));
        Picasso.get().load(orderDetailsList.get(position).getItemDetails().getImgeUrl()).placeholder(R.drawable.balaji).fit().into(holder.image);
    }

    @Override
    public int getItemCount() {
        return orderDetailsList.size();
    }

    public class Items extends RecyclerView.ViewHolder{
        TextView name,brand,price,quantity;
        ImageView image;
        public Items(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.confirm_order_name);
            brand=itemView.findViewById(R.id.confirm_order_brand);
            price=itemView.findViewById(R.id.confirm_order_price);
            quantity=itemView.findViewById(R.id.confirm_order_quantity);
            image=itemView.findViewById(R.id.confirm_order_image);
        }
    }
}
