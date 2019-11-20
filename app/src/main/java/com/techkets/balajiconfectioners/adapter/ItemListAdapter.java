package com.techkets.balajiconfectioners.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.techkets.balajiconfectioners.R;
import com.techkets.balajiconfectioners.model.ItemDetails;
import com.techkets.balajiconfectioners.util.Constants;

import java.util.List;
import java.util.Objects;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ItemHolder> {

    private List<ItemDetails> itemDetailsList;
    private Context context;
    private LayoutInflater inflater;


    private FirebaseFirestore firestore;
    private CollectionReference reference;

    private AlertDialog.Builder builder;
    private AlertDialog dialog;

    private ProgressDialog progressDialog;

    public ItemListAdapter(Context context, List<ItemDetails> itemDetailsList) {
        this.context = context;
        this.itemDetailsList = itemDetailsList;
        inflater = LayoutInflater.from(context);
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Deleting");
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_card_view, parent, false);
        return new ItemHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        ItemDetails itemDetails = itemDetailsList.get(position);
        holder.name.setText(String.format("Name: %s", itemDetails.getName()));
        holder.price.setText(String.format("Price: %s", itemDetails.getPrice()));
        holder.category.setText(String.format("Category: %s", itemDetails.getCategory()));
        holder.brand.setText(String.format("Brand: %s", itemDetails.getBrand()));
        holder.quantity.setText(String.format("Quantity: %s", itemDetails.getQuantity()));
        Picasso.get().load(itemDetails.getImgeUrl()).placeholder(R.drawable.balaji).fit().into(holder.image);

    }

    @Override
    public int getItemCount() {
        return itemDetailsList.size();

    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        TextView name, price, quantity, brand, category;
        ImageView image;
        Button update, delete;

        public ItemHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;
            progressDialog=new ProgressDialog(context);
            progressDialog.setCancelable(false);
            name = itemView.findViewById(R.id.itemName);
            price = itemView.findViewById(R.id.itemPrice);
            quantity = itemView.findViewById(R.id.itemQuantity);
            brand = itemView.findViewById(R.id.itemBrand);
            category = itemView.findViewById(R.id.itemCategory);
            image = itemView.findViewById(R.id.imageView);
            update = itemView.findViewById(R.id.updateItem);
            delete = itemView.findViewById(R.id.deleteItem);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    ItemDetails itemDetails = itemDetailsList.get(position);
                    deleteItem(itemDetails);
                }
            });

            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    ItemDetails itemDetails = itemDetailsList.get(position);
                    updateItem(itemDetails, position);
                }
            });
        }

        private void updateItem(final ItemDetails itemDetails, final int position) {
            builder = new AlertDialog.Builder(context);
            progressDialog.setMessage("Updating");
            View view = LayoutInflater.from(context).inflate(R.layout.item_update, null);
            final EditText name, brand, price, quantity;
            Button cancel, save;
            name = view.findViewById(R.id.nameUpdate);
            brand = view.findViewById(R.id.brandUpdate);
            price = view.findViewById(R.id.priceUpdate);
            quantity = view.findViewById(R.id.quantityUpdate);
            save = view.findViewById(R.id.save_btn);
            cancel = view.findViewById(R.id.cancel_btn);

            name.setText(String.format("%s", itemDetails.getName()));
            price.setText(String.format("%s", itemDetails.getPrice()));
            brand.setText(String.format("%s", itemDetails.getBrand()));
            quantity.setText(String.format("%s", itemDetails.getQuantity()));

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!TextUtils.isEmpty(name.getText().toString()) && !TextUtils.isEmpty(price.getText().toString()) && !TextUtils.isEmpty(brand.getText().toString())
                            && !TextUtils.isEmpty(quantity.getText().toString())) {
                        ItemDetails newItem = new ItemDetails();
                        newItem.setName(name.getText().toString());
                        newItem.setBrand(brand.getText().toString());
                        newItem.setPrice(Float.parseFloat(price.getText().toString()));
                        newItem.setCategory(itemDetails.getCategory());
                        newItem.setQuantity(Float.parseFloat(quantity.getText().toString()));
                        newItem.setImgeUrl(itemDetails.getImgeUrl());
                        newItem.setTimestamp(itemDetails.getTimestamp());
                        progressDialog.show();
                        updateItem(itemDetails, newItem, position);
                    } else {
                        Snackbar.make(view, "Empty Fields are not allowed", 2000).show();
                    }
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            builder.setView(view);
            dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();
        }

        private void updateItem(final ItemDetails itemDetails, final ItemDetails newItem, final int position) {
            firestore = FirebaseFirestore.getInstance();
            CollectionReference reference = firestore.collection(Constants.COLLECTION_ITEM_DETAILS);
            reference.whereEqualTo("name", itemDetails.getName()).whereEqualTo("price", itemDetails.getPrice()).whereEqualTo("category", itemDetails.getCategory())
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (queryDocumentSnapshots != null) {
                        String id = "";
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            id = snapshot.getId();
                        }
                        DocumentReference documentReference = firestore.collection(Constants.COLLECTION_ITEM_DETAILS).document(id);
                        documentReference.set(newItem).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                itemDetailsList.set(position, newItem);
                                notifyDataSetChanged();
                                progressDialog.dismiss();
                                Toast.makeText(context, "Update Successful", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(context, "Updating Failed", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Updating Failed", Toast.LENGTH_LONG).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(context, "Updating Failed", Toast.LENGTH_LONG).show();
                }
            });
        }

        private void deleteItem(final ItemDetails itemDetails) {
            builder = new AlertDialog.Builder(context);
            progressDialog.setMessage("Deleting...");
            View view = LayoutInflater.from(context).inflate(R.layout.category_delete_confirmation, null);
            Button yes, no;
            TextView delete_notify;
            delete_notify = view.findViewById(R.id.delete_notify);
            yes = view.findViewById(R.id.yes_btn);
            no = view.findViewById(R.id.no_btn);
            delete_notify.setText("Delete this Item?");

            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressDialog.show();
                    firestore = getFireStoreInstance();
                    reference = firestore.collection(Constants.COLLECTION_ITEM_DETAILS);
                    Query query = firestore.collection(Constants.COLLECTION_ITEM_DETAILS).whereEqualTo("name", itemDetails.getName());
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                                    reference.document(snapshot.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            itemDetailsList.remove(getAdapterPosition());
                                            notifyDataSetChanged();
                                            Toast.makeText(context, "Deleted", Toast.LENGTH_LONG).show();
                                            progressDialog.dismiss();
                                            dialog.dismiss();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
                                        }
                                    });

                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });


            builder.setView(view);
            dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();

        }


    }

    private FirebaseFirestore getFireStoreInstance() {
        if (firestore == null) {
            firestore = FirebaseFirestore.getInstance();
        }
        return firestore;
    }
}
