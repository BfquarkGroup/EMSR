package com.oufar.emsr.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.oufar.emsr.Adapter.RecyclerViewAdapter_2;
import com.oufar.emsr.Home;
import com.oufar.emsr.Model.Order;
import com.oufar.emsr.R;

import java.util.ArrayList;

public class Fragment_2 extends Fragment {

    private TextView counter, nothing_txt;
    //private FloatingActionButton refresh;
    private SwipeRefreshLayout swipeLayout;
    private ImageView nothing;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private Home home;

    private RecyclerViewAdapter_2 recyclerViewAdapter_2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_fragment_2, container, false);


        home = (Home) getActivity();

        counter = view.findViewById(R.id.counter);
        swipeLayout = view.findViewById(R.id.swipe);
        //refresh = view.findViewById(R.id.refresh);
        nothing = view.findViewById(R.id.nothing);
        nothing_txt = view.findViewById(R.id.nothing_txt);
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // cancle the Visual indication of a refresh
                        swipeLayout.setRefreshing(false);

                        //Toast.makeText(getContext(), "Refreshing...", Toast.LENGTH_SHORT).show();

                        checkOrders();
                    }
                }, 500);
            }
        });

        /*refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getContext(), "Refreshing...", Toast.LENGTH_SHORT).show();

                checkOrders();
            }
        });*/

        checkOrders();

        return view;
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

                            if (storeId.equals(firebaseUser.getUid())){

                                arrayList.add(clientId);
                            }
                        }
                    }

                    deleteOccurrenceValues(arrayList);

                }else {

                    progressBar.setVisibility(View.GONE);
                    nothing.setVisibility(View.VISIBLE);
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

        calculate(ordersN);

        getOrderHolderInformation(arrayList);
    }

    private void calculate(int ordersN) {

        //counter.setText(ordersN+" customer(s) waiting.");
        counter.setText(ordersN+"");
    }

    private void getOrderHolderInformation(ArrayList<String> arrayList) {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        final ArrayList<Order> clientList = new ArrayList<>();
        clientList.clear();

        if (arrayList.size() > 0){

            nothing.setVisibility(View.GONE);
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

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter_2 = new RecyclerViewAdapter_2(getContext(), clientList);
        recyclerView.setAdapter(recyclerViewAdapter_2);
    }


}
