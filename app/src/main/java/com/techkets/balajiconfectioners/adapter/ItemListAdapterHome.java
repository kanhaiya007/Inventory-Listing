package com.techkets.balajiconfectioners.adapter;

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
import com.techkets.balajiconfectioners.model.ItemDetails;

import java.util.List;

public class ItemListAdapterHome extends RecyclerView.Adapter<ItemListAdapterHome.ViewHolder> {

    private Context context;
    private List<ItemDetails> itemDetailsList;
    private LayoutInflater inflater;

    public ItemListAdapterHome(Context context, List<ItemDetails> itemDetails) {
        this.context = context;
        this.itemDetailsList = itemDetails;
        inflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(inflater.inflate(R.layout.card_list_home, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemDetails itemDetails = itemDetailsList.get(position);
        holder.name.setText(itemDetails.getName());
        holder.category.setText(itemDetails.getCategory());
        holder.brand.setText(itemDetails.getBrand());
        holder.price.setText(String.format("Price: %s", itemDetails.getPrice()));
        holder.quantity.setText(String.format("Price: %s", itemDetails.getQuantity()));
        Picasso.get().load(itemDetails.getImgeUrl()).placeholder(R.drawable.balaji).fit().into(holder.image);
    }

    @Override
    public int getItemCount() {
        return itemDetailsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, brand, quantity, category, price;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.itemNameHome);
            brand = itemView.findViewById(R.id.itemBrandHome);
            quantity = itemView.findViewById(R.id.itemQuantityHome);
            category = itemView.findViewById(R.id.itemCategoryHome);
            price = itemView.findViewById(R.id.itemPriceHome);
            image = itemView.findViewById(R.id.imageViewHome);

        }
    }
}
