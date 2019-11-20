package com.techkets.balajiconfectioners.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.techkets.balajiconfectioners.R;
import com.techkets.balajiconfectioners.model.OrderDetails;

import java.util.List;

public class OrderListDialogAdapter extends RecyclerView.Adapter<OrderListDialogAdapter.Items> {
    private List<OrderDetails> orderDetailsList;
    private Context context;
    private LayoutInflater inflater;

    public OrderListDialogAdapter(Context context,List<OrderDetails> orderDetailsList){
        this.context=context;
        this.orderDetailsList=orderDetailsList;
        inflater=LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public Items onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Items(inflater.inflate(R.layout.card_order_list,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull Items holder, int position) {
        holder.itemName.setText(orderDetailsList.get(position).getItemDetails().getName());
        holder.itemQuantity.setText(String.valueOf(orderDetailsList.get(position).getOrderQuantity()));
        holder.itemCategory.setText(orderDetailsList.get(position).getItemDetails().getCategory());
    }

    @Override
    public int getItemCount() {
        return orderDetailsList.size();
    }

    public class Items extends RecyclerView.ViewHolder {
        TextView itemName,itemQuantity,itemCategory;
        public Items(@NonNull View itemView) {
            super(itemView);
            itemName=itemView.findViewById(R.id.orderName);
            itemCategory=itemView.findViewById(R.id.orderCategory);
            itemQuantity=itemView.findViewById(R.id.orderQuantity);
        }
    }
}
