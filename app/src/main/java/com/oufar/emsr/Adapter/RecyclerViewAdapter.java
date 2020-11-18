package com.oufar.emsr.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.oufar.emsr.Fragments.Fragment_1;
import com.oufar.emsr.Model.Plate;
import com.oufar.emsr.R;

import java.util.ArrayList;
import java.util.HashMap;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    final Fragment_1 fragment_1 = new Fragment_1();

    private static final String TAG = "RecyclerViewAdapter";

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = auth.getCurrentUser();
    private HashMap<String, Object> map = new HashMap<>();
    //private DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Store_Menu").child(firebaseUser.getUid()).push();


    //vars
    private ArrayList<Plate> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter(Context context, ArrayList<Plate> names) {
        mNames = names;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_in_home, parent, false);


        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {


                return true;
            }
        });


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        String url = mNames.get(position).getImageURL();

        if (url.equals("default")){

            url = "https://images.favordelivery.com/homepage/featured/salvation.jpg/w-3000_q-50";


            Glide.with(mContext)
                    .asBitmap()
                    .load(url)
                    .into(holder.image);
        }else {

            Glide.with(mContext)
                    .asBitmap()
                    .load(url)
                    .into(holder.image);
        }

        holder.plate.setText(mNames.get(position).getPlate());
        holder.price.setText(mNames.get(position).getPrice());
        holder.description.setText(mNames.get(position).getDescription());

        if (url == "default"){

            Glide.with(mContext)
                    .asBitmap()
                    .load("https://images.favordelivery.com/homepage/featured/salvation.jpg/w-3000_q-50")
                    .into(holder.image);
        }


        if (mNames.get(position).getAvailability().equals("available")){

            holder.unavailable.setVisibility(View.GONE);
            holder.available.setVisibility(View.VISIBLE);
        }


        if (mNames.get(position).getAvailability().equals("unavailable")){

            holder.available.setVisibility(View.GONE);
            holder.unavailable.setVisibility(View.VISIBLE);
        }


        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog Alert = new AlertDialog.Builder(mContext)
                        .setMessage("You want to delete this plate ("+mNames.get(position).getPlate()+") ?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                String plate = mNames.get(position).getPlate();
                                String price = mNames.get(position).getPrice();
                                String description = mNames.get(position).getDescription();
                                String imageURL = mNames.get(position).getImageURL();

                                deletePlate(plate, price, description, imageURL);
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("no", null)
                        .show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CardView box;
        ImageView image, delete;
        TextView plate, price, description;
        RelativeLayout relativeLayout;
        LinearLayout available, unavailable;

        public ViewHolder(View itemView) {
            super(itemView);
            box = itemView.findViewById(R.id.box);
            delete = itemView.findViewById(R.id.delete);
            image = itemView.findViewById(R.id.image);
            plate = itemView.findViewById(R.id.plate);
            price = itemView.findViewById(R.id.price);
            description = itemView.findViewById(R.id.description);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            available = itemView.findViewById(R.id.available);
            unavailable = itemView.findViewById(R.id.unavailable);
        }
    }

    public void deletePlate(final String Plate, final String Price, final String Description, final String ImageURL){

        final FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        fStore.collection("Store").document(firebaseUser.getUid()).
                collection("StoreMenu").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {


                //mNames.clear();

                assert queryDocumentSnapshots != null;

                if (firebaseUser != null && queryDocumentSnapshots != null) {

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {

                        assert doc != null;

                        String plate = doc.getString("plate");
                        String price = doc.getString("price");
                        String description = doc.getString("description");
                        String imageURL = doc.getString("imageURL");

                        if (plate.equals(Plate) && price.equals(Price) &&
                                description.equals(Description) && imageURL.equals(ImageURL)){

                            String documentId = doc.getId();

                            final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

                            firebaseFirestore.collection("Store").document(firebaseUser.getUid()).
                                    collection("StoreMenu").document(documentId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){

                                        // delete image from storage
                                        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                                        StorageReference storageReference_2 = firebaseStorage.getReferenceFromUrl(ImageURL);
                                        storageReference_2.delete();

                                        Toast.makeText(mContext, "Plate deleted", Toast.LENGTH_SHORT).show();
                                    }else {

                                        Toast.makeText(mContext, "ERROR", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                        }

                        //Plate plate_ = new Plate("", plate, price, description, imageURL, "", "", "");
                        //mNames.add(plate_);

                    }
                }
            }
        });
    }

    public void availability(final String Plate, final String Price, final String Description, final String ImageURL, final String Availability){

        final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Store").document(firebaseUser.getUid()).
                collection("StoreMenu").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {


                //mNames.clear();

                assert queryDocumentSnapshots != null;

                if (firebaseUser != null && queryDocumentSnapshots != null) {

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {

                        assert doc != null;

                        String plate = doc.getString("plate");
                        String price = doc.getString("price");
                        String description = doc.getString("description");
                        String imageURL = doc.getString("imageURL");

                        if (plate.equals(Plate) && price.equals(Price) &&
                                description.equals(Description) && imageURL.equals(ImageURL)){

                            String documentId = doc.getId();

                            map.put("availability", Availability);

                            final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

                            firebaseFirestore.collection("Store").document(firebaseUser.getUid()).
                                    collection("StoreMenu").document(documentId).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){

                                    }else {

                                    }

                                }
                            });

                        }

                        //Plate plate_ = new Plate("", plate, price, description, imageURL, "", "", "");
                        //mNames.add(plate_);

                    }
                }
            }
        });
    }

}
