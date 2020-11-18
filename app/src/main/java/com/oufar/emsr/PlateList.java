package com.oufar.emsr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.oufar.emsr.Adapter.RecyclerViewAdapter_3;
import com.oufar.emsr.Database.DB;
import com.oufar.emsr.Model.Plate;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlateList extends AppCompatActivity {

    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private FirebaseAuth auth;

    private DB db;

    private TextView nothing_txt, clientName;
    private CircleImageView clientImage;
    private RecyclerView recyclerView;
    private ImageView nothing;
    private ProgressBar progressBar;
    private ArrayList<Plate> mNames = new ArrayList<>();

    String client_id, client_name, client_image, storeName;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plate_list);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getColor(R.color.dark_orange));
        }

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        }
        setNavigationBarButtonsColor(this, R.color.white);

        db = new DB(this);

        Bundle bundle = getIntent().getExtras();
        client_id = bundle.getString("client_id");
        client_name = bundle.getString("client_name");
        client_image = bundle.getString("client_image");

        clientName = findViewById(R.id.clientName);
        clientImage = findViewById(R.id.clientImage);
        recyclerView = findViewById(R.id.recyclerView);
        nothing = findViewById(R.id.nothing);
        nothing_txt = findViewById(R.id.nothing_txt);
        progressBar = findViewById(R.id.progressBar);

        clientName.setText(client_name);

        if (!clientImage.equals("default")) {

            Glide.with(this)
                    .asBitmap()
                    .load(client_image)
                    .into(clientImage);
        }

        checkMenu();
    }

    private void checkMenu() {

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        progressBar.setVisibility(View.VISIBLE);

        reference = FirebaseDatabase.getInstance().getReference("Orders");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(client_id)){

                    downloadMenu();
                }else {

                    progressBar.setVisibility(View.GONE);
                    nothing.setVisibility(View.VISIBLE);
                    nothing_txt.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void downloadMenu() {

        nothing.setVisibility(View.GONE);
        nothing_txt.setVisibility(View.GONE);

        reference.child(client_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                progressBar.setVisibility(View.GONE);

                mNames.clear();

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    String Id = snapshot.child("id").getValue().toString();
                    String Plate = snapshot.child("plate").getValue().toString();
                    String Price = snapshot.child("price").getValue().toString();
                    String Description = snapshot.child("description").getValue().toString();
                    String ImageURL = snapshot.child("imageURL").getValue().toString();
                    String StoreId = snapshot.child("storeId").getValue().toString();
                    String Number = snapshot.child("number").getValue().toString();
                    String Status = snapshot.child("status").getValue().toString();
                    String ClientId = snapshot.child("clientId").getValue().toString();

                    if (StoreId.equals(firebaseUser.getUid())) {

                        Plate plate = new Plate(Id, Plate, Price, Description, ImageURL, Number, Status, ClientId, "");

                        mNames.add(plate);
                    }
                }

                initRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initRecyclerView(){

        Cursor cursor = db.viewData();

        if (cursor.getCount() == 0){

            nothing.setVisibility(View.VISIBLE);

        }else {

            while (cursor.moveToNext()){

                storeName = cursor.getString(1);
            }
        }


        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter_3 adapter = new RecyclerViewAdapter_3(this, mNames, storeName);
        recyclerView.setAdapter(adapter);
    }

    //Plate plate = new Plate(Id, Plate, Price, Description, ImageURL);


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
