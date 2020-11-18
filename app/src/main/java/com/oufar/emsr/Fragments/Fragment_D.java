package com.oufar.emsr.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.oufar.emsr.Adapter.RecyclerViewAdapter_Delivery;
import com.oufar.emsr.Home;
import com.oufar.emsr.Model.DeliveryGuy;
import com.oufar.emsr.R;
import com.oufar.emsr.Scan;

import java.util.ArrayList;

public class Fragment_D extends Fragment {

    private SwipeRefreshLayout swipeLayout;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView guyNumbers;
    private Button scan;

    private Home home;

    private ArrayList<DeliveryGuy> deliveryGuyArrayList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment__d, container, false);


        home = (Home) getActivity();

        swipeLayout = view.findViewById(R.id.swipe);
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        guyNumbers = view.findViewById(R.id.guyNumbers);
        scan = view.findViewById(R.id.scan);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // cancle the Visual indication of a refresh
                        swipeLayout.setRefreshing(false);

                        //Toast.makeText(getContext(), "Refreshing...", Toast.LENGTH_SHORT).show();

                        checkOrders_();
                    }
                }, 500);
            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), Scan.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("guyId", "...");// sending id
                intent.putExtra("guyName", "...");// sending name
                intent.putExtra("guyImage", "...");// sending image
                intent.putExtra("guyDescription", "...");// sending description
                intent.putExtra("guyEmail", "...");// sending email
                getContext().startActivity(intent);
            }
        });


        checkOrders_();

        return view;
    }

    private void checkOrders_() {

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
                            String delivery = shot.child("delivery").getValue().toString();

                            if (storeId.equals(firebaseUser.getUid()) && !delivery.equals("on hold")){

                                arrayList.add(delivery);
                            }
                        }
                    }

                    deleteOccurrenceValues(arrayList);

                }else {

                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /*private void checkOrders() {

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
                            String delivery = shot.child("delivery").getValue().toString();

                            if (storeId.equals(firebaseUser.getUid()) && !delivery.equals("on hold")){

                                arrayList.add(delivery);
                            }
                        }
                    }

                    deleteOccurrenceValues(arrayList);

                }else {

                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/

    private void deleteOccurrenceValues(ArrayList<String> arrayList) {

        for(int i = 0; i < arrayList.size();i++){

            if (i != 0) {

                if (arrayList.get(i).equals(arrayList.get(i - 1))) {
                    arrayList.remove(i--);
                }
            }
        }

        guyNumbers.setText(arrayList.size()+"");

        getDeliveryGuyInformation(arrayList);
    }

    private void getDeliveryGuyInformation(ArrayList<String> arrayList) {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        final ArrayList<DeliveryGuy> deliveryGuys = new ArrayList<>();
        deliveryGuys.clear();

        if (arrayList.size() > 0){

            //nothing.setVisibility(View.GONE);
        }

        for (int i = 0; i < arrayList.size(); i++){

            firestore.collection("DeliveryGuy").document(arrayList.get(i)).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {


                    assert documentSnapshot != null;

                    if (documentSnapshot != null) {

                        String id = documentSnapshot.get("id").toString();
                        String name = documentSnapshot.get("username").toString();
                        String phone = documentSnapshot.get("phone").toString();
                        String address = documentSnapshot.get("wilaya").toString();
                        String imageURL = documentSnapshot.get("imageURL").toString();
                        String email = documentSnapshot.get("email").toString();
                        String wilaya = documentSnapshot.get("wilaya").toString();
                        String city = documentSnapshot.get("city").toString();

                        DeliveryGuy deliveryGuy = new DeliveryGuy(id, name, phone, address, imageURL, email, wilaya, city );

                        deliveryGuys.add(deliveryGuy);

                        initRecyclerView(deliveryGuys);

                    }

                }
            });
        }
    }

    private void initRecyclerView(ArrayList<DeliveryGuy> deliveryGuyArrayList) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter_Delivery recyclerViewAdapter_delivery = new RecyclerViewAdapter_Delivery(getContext(), deliveryGuyArrayList);
        recyclerView.setAdapter(recyclerViewAdapter_delivery);
    }


}
