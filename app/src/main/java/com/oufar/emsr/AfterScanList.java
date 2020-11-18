package com.oufar.emsr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.oufar.emsr.Adapter.RecyclerViewAdapter_2;
import com.oufar.emsr.Model.Order;

import java.util.ArrayList;

public class AfterScanList extends AppCompatActivity {

    private SwipeRefreshLayout swipeLayout;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView guyName, nothing_txt;
    private ImageView image, QRCode;

    private RecyclerViewAdapter_2 recyclerViewAdapter_2;

    private String GUY_ID, GUY_NAME, GUY_IMAGE, GUY_DESCRIPTION, GUY_EMAIL, GUY_PHONE;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_scan_list);

        Bundle bundle = getIntent().getExtras();
        GUY_ID = bundle.getString("guyId");
        /*GUY_NAME = bundle.getString("guyName");
        GUY_IMAGE = bundle.getString("guyImage");
        GUY_DESCRIPTION = bundle.getString("guyDescription");
        GUY_EMAIL = bundle.getString("guyEmail");*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getColor(R.color.custom_green));
        }

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.custom_green));
        }
        setNavigationBarButtonsColor(this, R.color.custom_green);

        swipeLayout = findViewById(R.id.swipe);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        image = findViewById(R.id.image);
        guyName = findViewById(R.id.guyName);
        QRCode = findViewById(R.id.QRCode);
        nothing_txt = findViewById(R.id.nothing_txt);

        FirebaseFirestore firestore;
        firestore = FirebaseFirestore.getInstance();
        firestore.collection("DeliveryGuy").document(GUY_ID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                String GUY_NAME = documentSnapshot.get("username").toString();
                String GUY_PHONE = documentSnapshot.get("phone").toString();
                String GUY_DESCRIPTION = documentSnapshot.get("description").toString();
                String GUY_EMAIL = documentSnapshot.get("email").toString();
                String GUY_IMAGE = documentSnapshot.get("imageURL").toString();

                inf(GUY_NAME, GUY_IMAGE);

                //Toast();
            }
        });

        QRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AfterScanList.this, Scan.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("guyId", "...");// sending id
                intent.putExtra("guyName", "...");// sending name
                intent.putExtra("guyImage", "...");// sending image
                intent.putExtra("guyDescription", "...");// sending description
                intent.putExtra("guyEmail", "...");// sending email
                startActivity(intent);
            }
        });

        checkOrders();

    }

    private void inf(String guy_name, String guy_image) {

        Glide.with(this)
                .asBitmap()
                .load(guy_image)
                .into(image);

        guyName.setText(guy_name);
    }

    private void checkOrders() {

        final FirebaseUser firebaseUser;
        DatabaseReference reference;

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference("Orders");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                    ArrayList<String> arrayList = new ArrayList<>();
                    arrayList.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                        for (DataSnapshot shot : snapshot.getChildren()){

                            String storeId = shot.child("storeId").getValue().toString();
                            String clientId = shot.child("clientId").getValue().toString();
                            String delivery = shot.child("delivery").getValue().toString();

                            if (storeId.equals(firebaseUser.getUid()) && delivery.equals(GUY_ID)){

                                arrayList.add(clientId);
                            }
                        }
                    }

                    deleteOccurrenceValues(arrayList);

                }else {

                    progressBar.setVisibility(View.GONE);
                    nothing_txt.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteOccurrenceValues(ArrayList<String> arrayList) {

        for(int i = 0; i < arrayList.size();i++){

            if (i != 0) {

                if (arrayList.get(i).equals(arrayList.get(i - 1))) {
                    arrayList.remove(i--);
                }
            }
        }

        int ordersN = arrayList.size();

        getOrderHolderInformation(arrayList);
    }

    private void getOrderHolderInformation(ArrayList<String> arrayList) {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        final ArrayList<Order> clientList = new ArrayList<>();
        clientList.clear();

        if (arrayList.size() > 0){

            nothing_txt.setVisibility(View.GONE);
        }

        for (int i = 0; i < arrayList.size(); i++){

            firestore.collection("Client").document(arrayList.get(i)).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {


                    assert documentSnapshot != null;

                    if (documentSnapshot != null) {

                        String clientId_ = documentSnapshot.get("id").toString();
                        String name = documentSnapshot.get("username").toString();
                        String phone = documentSnapshot.get("phone").toString();
                        String address = documentSnapshot.get("wilaya").toString();
                        String imageURL = documentSnapshot.get("imageURL").toString();

                        Order client_order = new Order(clientId_, name, phone, address, imageURL);

                        clientList.add(client_order);

                        initRecyclerView(clientList);

                    }

                }
            });
        }
    }

    private void initRecyclerView(ArrayList<Order> clientList) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter_2 = new RecyclerViewAdapter_2(this, clientList);
        recyclerView.setAdapter(recyclerViewAdapter_2);
    }

    private void setNavigationBarButtonsColor(Activity activity, int navigationBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            View decorView = activity.getWindow().getDecorView();
            int flags = decorView.getSystemUiVisibility();
            if (isColorLight(navigationBarColor)) {
                flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            } else {
                flags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            }
            decorView.setSystemUiVisibility(flags);
        }
    }

    private boolean isColorLight(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness < 0.5;
    }
}
