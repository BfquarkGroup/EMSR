package com.oufar.emsr.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.oufar.emsr.Model.Order;
import com.oufar.emsr.PlateList;
import com.oufar.emsr.R;

import java.util.ArrayList;

public class RecyclerViewAdapter_2 extends RecyclerView.Adapter<RecyclerViewAdapter_2.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter_2";

    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private FirebaseAuth auth;

    //vars
    private ArrayList<Order> clientList = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter_2(Context context, ArrayList<Order> names) {
        clientList = names;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_in_orders, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        //reference = FirebaseDatabase.getInstance().getReference("Orders").child(firebaseUser.getUid());


        if (!clientList.get(position).getClientImageURL().equals("default")){

            Glide.with(mContext)
                    .asBitmap()
                    .load(clientList.get(position).getClientImageURL())
                    .into(holder.image);
        }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(mContext, "no action yet", Toast.LENGTH_SHORT).show();
            }
        });

        holder.name.setText(clientList.get(position).getClientName());
        holder.phone.setText(clientList.get(position).getClientPhone());
        holder.phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog Alert = new AlertDialog.Builder(mContext)
                        .setMessage("You want to call this customer ("+clientList.get(position).getClientName()+") ?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Intent call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(clientList.get(position).getClientPhone())));
                                mContext.startActivity(call);
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("no", null)
                        .show();
            }
        });

        holder.address.setText(clientList.get(position).getClientAddress());

        holder.box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Intent i = new Intent(mContext, PlateList.class);
                i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra("client_id", clientList.get(position).getClientId());// sending client id
                i.putExtra("client_name", clientList.get(position).getClientName());// sending client name
                i.putExtra("client_image", clientList.get(position).getClientImageURL());// sending client image
                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return clientList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CardView box;
        ImageView image;
        ImageView delete;
        TextView name, phone, address;

        public ViewHolder(View itemView) {
            super(itemView);
            box = itemView.findViewById(R.id.box);
            delete = itemView.findViewById(R.id.delete);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            phone = itemView.findViewById(R.id.phone);
            address = itemView.findViewById(R.id.address);
        }
    }
}
