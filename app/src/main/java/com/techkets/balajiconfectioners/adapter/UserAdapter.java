package com.techkets.balajiconfectioners.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.techkets.balajiconfectioners.R;
import com.techkets.balajiconfectioners.model.UserDetails;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context context;
    private List<UserDetails> userDetailsList;
    private LayoutInflater inflater;

    public UserAdapter(Context context, List<UserDetails> userDetailsList) {
        this.context = context;
        this.userDetailsList = userDetailsList;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.user_card, parent, false);
        return new UserViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserDetails userDetails = userDetailsList.get(position);
        long mob = userDetails.getUserMobileNo();
        holder.name.setText(userDetails.getUserName());
        holder.address.setText(userDetails.getUserAddress());
        holder.mobNo.setText(mob + "");
        holder.password.setText(userDetails.getUserPassword());
        holder.email.setText(userDetails.getUserEmail());
    }

    @Override
    public int getItemCount() {
        return userDetailsList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        TextView name, email, password, mobNo, address;

        public UserViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;
            name = itemView.findViewById(R.id.userName);
            email = itemView.findViewById(R.id.userEmail);
            password = itemView.findViewById(R.id.userPassword);
            mobNo = itemView.findViewById(R.id.userMob);
            address = itemView.findViewById(R.id.userAddress);

        }
    }
}
