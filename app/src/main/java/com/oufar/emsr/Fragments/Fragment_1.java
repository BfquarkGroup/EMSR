package com.oufar.emsr.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.oufar.emsr.Adapter.RecyclerViewAdapter;
import com.oufar.emsr.Home;
import com.oufar.emsr.Model.Plate;
import com.oufar.emsr.R;

import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class Fragment_1 extends Fragment {

    private ProgressBar progressBar;
    private ImageView plate_picture;
    private TextView nothing;
    private Button btn_add_plate;
    private RecyclerView recyclerView;
    private EditText plate, price, description;
    private String CONFIRM = "";

    private Home home;

    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private FirebaseAuth auth;

    private HashMap<String, String> hashMap;

    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private String imageURL;
    private StorageReference storageReference;
    private StorageTask uploadTask;

    //vars
    private ArrayList<Plate> mNames = new ArrayList<>();


    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_fragment_1, container, false);

        home = (Home) getActivity();


        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference("Uploads_2");

        hashMap = new HashMap<>();
        hashMap.put("imageURL", "default");

        progressBar = view.findViewById(R.id.progressBar);
        plate_picture = view.findViewById(R.id.plate_picture);
        nothing = view.findViewById(R.id.nothing);
        btn_add_plate = view.findViewById(R.id.btn_add_plate);
        plate = view.findViewById(R.id.edit_plate);
        price = view.findViewById(R.id.edit_price);
        description = view.findViewById(R.id.edit_description);

        plate_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog Alert = new AlertDialog.Builder(getContext())
                        .setMessage("Choose a decent and clear image for your plate")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                openFileChooser();
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("Cancel", null)
                        .show();

            }
        });

        btn_add_plate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CONFIRM = "confirmed";
                addPlate();

                //addPlate();
            }
        });

        recyclerView = view.findViewById(R.id.recyclerView);

        //getImages();

        checkMenu();
        return view;
    }

    // adding picture

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()){

                Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
            }else {

                //confirm();
                addPlate();

                //uploadImage();
            }

        }

    }

    private void loadImage() {

        if (current_image != "default") {

            // imageURL = home.ImageURL();

            if (imageURL.equals("default")) {

                plate_picture.setImageResource(R.drawable.black_food);
            } else {
                Glide.with(getContext()).load(imageURL).into(plate_picture);
            }
        }else {

            // imageURL = home.ImageURL();

            if (imageURL.equals("default")) {

                plate_picture.setImageResource(R.drawable.black_food);
            } else {
                Glide.with(getContext()).load(imageURL).into(plate_picture);
            }
        }
    }

    String mUri = "default";
    String current_image;
    public void addPlate() {

        if (plate.getText().toString().isEmpty() || price.getText().toString().isEmpty() ){

            Toast.makeText(getContext(), "add your plate to the menu", Toast.LENGTH_SHORT).show();
            return;
        }

        //reference = FirebaseDatabase.getInstance().getReference("Store_Menu").child(firebaseUser.getUid()).push();



        // image part-------------------------------------------------------------------------------
        current_image = imageURL;

        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading...");
        pd.show();

        if (imageUri != null){

            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if (!task.isSuccessful()){

                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()){

                        Uri downloadUri = task.getResult();
                        mUri = downloadUri.toString();

                        hashMap.put("imageURL", mUri);
                        imageURL = mUri;
                        addPlateTextPart(mUri);
                        pd.dismiss();

                    }else {

                        Toast.makeText(getContext(), "Failed loading image", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getContext(),  e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });


        }else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
            plate_picture.setImageResource(R.drawable.wallpaper1);
            pd.dismiss();
        }

    }

    private void addPlateTextPart(String mUri) {

        Home home;
        home = (Home) getActivity();
        String storeName = home.Username();

        final FirebaseFirestore firestore;
        firestore = FirebaseFirestore.getInstance();
        //firestore.collection("StoreMenu").document(firebaseUser.getUid());

        //String id = firestore.collection(firebaseUser.getUid()).document().getId();

        // text part-------------------------------------------------------------------------------
        //hashMap.put("id", );
        hashMap.put("plate", plate.getText().toString());
        hashMap.put("price", price.getText().toString());
        hashMap.put("description", description.getText().toString());
        hashMap.put("imageURL", mUri);
        hashMap.put("storeName", storeName);
        hashMap.put("availability", "Available");
        mUri = "default";

        firestore.collection("Store").document(firebaseUser.getUid()).
        collection("StoreMenu").add(hashMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

                if (task.isSuccessful()){

                    Toast.makeText(getContext(), "Plate added", Toast.LENGTH_SHORT).show();

                    plate.getText().clear();
                    price.getText().clear();
                    description.getText().clear();
                }else {

                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                }

            }
        });

        Glide.with(getContext()).load(imageURL).into(plate_picture);

        checkMenu();
    }

    // loading...

    private void checkMenu(){

        progressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Store").document(firebaseUser.getUid()).
                collection("StoreMenu").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                progressBar.setVisibility(View.GONE);

                mNames.clear();

                assert queryDocumentSnapshots != null;

                if (firebaseUser != null && queryDocumentSnapshots != null) {

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {

                        assert doc != null;

                        String plate = doc.getString("plate");
                        String price = doc.getString("price");
                        String description = doc.getString("description");
                        String imageURL = doc.getString("imageURL");
                        String availability = doc.getString("availability");

                        Plate plate_ = new Plate("", plate, price, description, imageURL, "", "", "", availability);
                        mNames.add(plate_);

                    }
                }

                initRecyclerView();

            }
        });
    }

    private void initRecyclerView(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(getContext(), mNames);
        recyclerView.setAdapter(adapter);
    }

}
