package com.techkets.balajiconfectioners.view;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.techkets.balajiconfectioners.R;
import com.techkets.balajiconfectioners.adapter.ItemListAdapterHome;
import com.techkets.balajiconfectioners.model.Category;
import com.techkets.balajiconfectioners.model.ItemDetails;
import com.techkets.balajiconfectioners.util.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    private static final int GALLERY_CODE = 1;
    private Button addItem;
    private Button addCategory;
    private Button cancel, save;
    private TextView catCount, itemCount;

    private EditText categoryName;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private Uri imageUri;
    private ImageView itemImageView;

    private ProgressDialog progressDialog;

    private List<String> categoryList = new ArrayList<>();
    private ArrayAdapter<String> categoryAdapter;
    private Spinner categorySpinner;

    private String categorySelect;

    //RecyclerView Variables for item layout
    private RecyclerView recyclerView;
    private ItemListAdapterHome adapterHome;
    private List<ItemDetails> itemDetailsList = new ArrayList<>();

    //Firestore Variables;

    private FirebaseFirestore firestore;
    private CollectionReference collectionReference;

    private StorageReference storageReference;


    public HomeFragment() {

        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storageReference = FirebaseStorage.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        addCategory = view.findViewById(R.id.addCategoryHome);
        addItem = view.findViewById(R.id.addItemHome);
        catCount = view.findViewById(R.id.homeCategoryCount);
        itemCount = view.findViewById(R.id.homeItemCount);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        addCategory.setOnClickListener(this);
        addItem.setOnClickListener(this);
        recyclerView = view.findViewById(R.id.homeRecyclerItem);
        adapterHome = new ItemListAdapterHome(getContext(), itemDetailsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapterHome);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressDialog.show();
        getCountDetails();

    }

    private void getCountDetails() {
        firestore = FirebaseFirestore.getInstance();
        collectionReference = firestore.collection(Constants.COLLECTION_CATEGORY_DETAILS);

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int len = queryDocumentSnapshots.size();
                catCount.setText(String.format("Category        %d", len));

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                catCount.setText(0 + "");
            }
        });

        collectionReference = firestore.collection(Constants.COLLECTION_ITEM_DETAILS);
        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int item = queryDocumentSnapshots.size();
                itemCount.setText(String.format("Item               %d", item));
                itemDetailsList.clear();
                for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                    ItemDetails itemDetails = snapshot.toObject(ItemDetails.class);
                    itemDetailsList.add(itemDetails);
                    adapterHome.notifyDataSetChanged();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                itemCount.setText(String.format("%d", 0));
            }
        });
        progressDialog.dismiss();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addCategoryHome:
                createCategoryPopup();
                break;
            case R.id.addItemHome:
                createItemPopup();
                break;
        }
    }

    private void createCategoryPopup() {
        builder = new AlertDialog.Builder(getActivity());
        View view = getLayoutInflater().inflate(R.layout.add_category_dialog, null);

        cancel = view.findViewById(R.id.categoryCancel);
        save = view.findViewById(R.id.categorySave);
        categoryName = view.findViewById(R.id.categoryName);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cat = categoryName.getText().toString().trim();
                if (!TextUtils.isEmpty(cat)) {
                    progressDialog.show();
                    Category category = new Category(cat);
                    addCatToFirebase(category);
                } else Toast.makeText(getActivity(), "Enter Category", Toast.LENGTH_LONG).show();
            }
        });


        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }

    private void addCatToFirebase(Category category) {

        collectionReference = firestore.collection(Constants.COLLECTION_CATEGORY_DETAILS);
        collectionReference.add(category).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                getCountDetails();
                progressDialog.cancel();
                dialog.cancel();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.cancel();
                Toast.makeText(getActivity(), "Failed", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void createItemPopup() {
        builder = new AlertDialog.Builder(getActivity());
        View view = getLayoutInflater().inflate(R.layout.add_item_dialog, null);

        getCategoryList();

        final EditText item, price, quantity, brand;
        Button uploadImage;


        cancel = view.findViewById(R.id.itemCancel);

        item = view.findViewById(R.id.enterItem);
        price = view.findViewById(R.id.enterPrice);
        quantity = view.findViewById(R.id.enterQuantity);
        brand = view.findViewById(R.id.enterBrand);

        categorySpinner = view.findViewById(R.id.spinCategory);
        categoryAdapter = new ArrayAdapter<>(getActivity(), R.layout.category_list_spinner, categoryList);
        uploadImage = view.findViewById(R.id.itemImage);
        itemImageView = view.findViewById(R.id.itemImageView);
        save = view.findViewById(R.id.itemSave);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                if (position > 0) {
                    categorySelect = categoryList.get(position);
                    Log.d("Category", "onItemSelected: " + categorySelect);
                } else categorySelect = null;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryList.clear();
                dialog.cancel();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.show();
                validateItem(item.getText().toString().trim(), price.getText().toString().trim(), quantity.getText().toString().trim(), brand.getText().toString().trim());
            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
            }
        });
        categorySpinner.setAdapter(categoryAdapter);
        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }


    private void getCategoryList() {
        categoryList.clear();
        CollectionReference collectionReference = firestore.collection(Constants.COLLECTION_CATEGORY_DETAILS);
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    categoryList.add("Select Category");
                    for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                        Category category = snapshot.toObject(Category.class);
                        Log.d("category", "onComplete: " + category.getCategorName());
                        categoryList.add(category.getCategorName());
                        categoryAdapter.notifyDataSetChanged();
                    }
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Failed Loading Category", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void validateItem(String item, String price, String quantity, String brand) {
        if (!TextUtils.isEmpty(item) && !TextUtils.isEmpty(price) &&
                !TextUtils.isEmpty(quantity) && !TextUtils.isEmpty(brand) &&
                !TextUtils.isEmpty(categorySelect) && categorySelect != null) {
            ItemDetails itemDetails = new ItemDetails();
            itemDetails.setBrand(brand);
            itemDetails.setCategory(categorySelect);
            itemDetails.setName(item);
            itemDetails.setPrice(Float.parseFloat(price));
            itemDetails.setQuantity(Float.parseFloat(quantity));
            itemDetails.setTimestamp(new Timestamp(new Date()).toString());
            addItemToFirebase(itemDetails);

        } else {
            progressDialog.dismiss();
            Toast.makeText(getActivity(), "Validate fields", Toast.LENGTH_LONG).show();
        }
    }

    private void addItemToFirebase(final ItemDetails itemDetails) {
        final StorageReference filepath = storageReference.child(Constants.KEY_IMAGE_FOLDER).child("item_image_" + Timestamp.now().getSeconds());
        if (imageUri != null) {
            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            itemDetails.setImgeUrl(imageUrl);
                            addItemNow(itemDetails);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Failed Image url", Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Failed", Toast.LENGTH_LONG).show();

                }
            });
        } else {
            progressDialog.dismiss();
            Toast.makeText(getActivity(), "Upload Image", Toast.LENGTH_LONG).show();
        }
    }

    private void addItemNow(ItemDetails itemDetails) {
        CollectionReference collectionReference = firestore.collection(Constants.COLLECTION_ITEM_DETAILS);
        collectionReference.add(itemDetails).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                getCountDetails();
                progressDialog.dismiss();
                dialog.cancel();
                Toast.makeText(getActivity(), "Item Added", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
                itemImageView.setImageURI(imageUri);
            }
        }
    }

}


