package com.techkets.balajiconfectioners.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.techkets.balajiconfectioners.R;
import com.techkets.balajiconfectioners.model.Category;
import com.techkets.balajiconfectioners.util.Constants;

import java.util.List;
import java.util.Objects;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ItemViewHolder> {

    private Context context;
    private List<Category> categoryList;
    private LayoutInflater inflater;
    private FirebaseFirestore firestore;
    private CollectionReference collectionReference;

    private AlertDialog.Builder builder;
    private AlertDialog dialog;

    private ProgressDialog progressDialog;


    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
        inflater = LayoutInflater.from(context);
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Deleting...");
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        return new ItemViewHolder(inflater.inflate(R.layout.category_card,parent,false));
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_card, parent, false);
        return new ItemViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.categoryName.setText(category.getCategorName());
        Log.d(" ", "onBindViewHolder: " + category.getCategorName());

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName, categoryDelete;
        TextView updateCategory;

        public ItemViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;
            categoryName = itemView.findViewById(R.id.category_name_card);
            categoryDelete = itemView.findViewById(R.id.delete_card);
            updateCategory = itemView.findViewById(R.id.update);

            updateCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Category category = categoryList.get(getAdapterPosition());
                    updateCategoryDetails(category, getAdapterPosition());
                }
            });


            categoryDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Category category = categoryList.get(position);
                    deleteItem(category);
                }
            });
        }

        private void updateCategoryDetails(final Category category, final int position) {
            builder = new AlertDialog.Builder(context);
            View view = LayoutInflater.from(context).inflate(R.layout.category_update, null);
            final Button cancel, save;
            final EditText catego;
            cancel = view.findViewById(R.id.cat_cancel_btn);
            save = view.findViewById(R.id.cat_save_btn);
            catego = view.findViewById(R.id.cate_update);
            catego.setText(category.getCategorName());

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (catego.getText().toString().trim().isEmpty() || catego.getText().toString().trim()==null){
                        catego.setError("Enter Valid Name");
                    }
                    else {
                        progressDialog.show();
                        Category newCategory=new Category();
                        newCategory.setCategorName(catego.getText().toString().trim());
                        updateCategoryName(category,newCategory,position);
                    }
                }
            });

            builder.setView(view);
            dialog = builder.create();
            dialog.show();
        }

        private void updateCategoryName(Category category, final Category newCategory, final int position) {
            firestore=FirebaseFirestore.getInstance();
            collectionReference =firestore.collection(Constants.COLLECTION_CATEGORY_DETAILS);
            collectionReference.whereEqualTo("categorName",category.getCategorName()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    String id="";
                    for (DocumentSnapshot snapshot:queryDocumentSnapshots) {
                         id = snapshot.getId();
                    }
                        DocumentReference documentReference=firestore.collection(Constants.COLLECTION_CATEGORY_DETAILS).document(id);
                        documentReference.set(newCategory).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                categoryList.set(position,newCategory);
                                dialog.dismiss();
                                notifyDataSetChanged();
                                progressDialog.dismiss();
                                Toast.makeText(context,"Updated",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void deleteItem(final Category category) {
            builder = new AlertDialog.Builder(context);
            View view = LayoutInflater.from(context).inflate(R.layout.category_delete_confirmation, null);
            Button yes, no;
            yes = view.findViewById(R.id.yes_btn);
            no = view.findViewById(R.id.no_btn);
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
                    collectionReference = firestore.collection(Constants.COLLECTION_CATEGORY_DETAILS);
                    Query query = firestore.collection(Constants.COLLECTION_CATEGORY_DETAILS).whereEqualTo("categorName", category.getCategorName());
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                                    collectionReference.document(snapshot.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            categoryList.remove(getAdapterPosition());
                                            notifyDataSetChanged();
                                            Toast.makeText(context, "Success", Toast.LENGTH_LONG).show();
                                            dialog.dismiss();
                                            progressDialog.dismiss();
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
                            Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show();
                        }
                    });

                }
            });

            builder.setView(view);
            dialog = builder.create();
            dialog.show();
        }

        private FirebaseFirestore getFireStoreInstance() {
            if (firestore == null) {
                firestore = FirebaseFirestore.getInstance();
            }
            return firestore;
        }
    }
}
