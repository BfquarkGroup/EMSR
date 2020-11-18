package com.oufar.emsr.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.oufar.emsr.Model.Plate;
import com.oufar.emsr.R;

import java.util.ArrayList;

public class RecyclerViewAdapter_3 extends RecyclerView.Adapter<RecyclerViewAdapter_3.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter_3";

    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private FirebaseAuth auth;

    //vars
    private ArrayList<Plate> mNames = new ArrayList<>();
    private Context mContext;
    private String storeName;

    public RecyclerViewAdapter_3(Context context, ArrayList<Plate> names, String store_name) {

        mNames = names;
        mContext = context;
        storeName = store_name;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_in_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        //reference = FirebaseDatabase.getInstance().getReference("Orders").child(firebaseUser.getUid());


        if (!mNames.get(position).getImageURL().equals("default")){

            Glide.with(mContext)
                    .asBitmap()
                    .load(mNames.get(position).getImageURL())
                    .into(holder.image);
        }

        holder.plate.setText(mNames.get(position).getPlate());
        holder.price.setText(mNames.get(position).getPrice());
        holder.description.setText(mNames.get(position).getDescription());
        holder.counter_txt.setText(mNames.get(position).getNumber());

        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference reference;
                reference = FirebaseDatabase.getInstance().getReference("Orders").child(mNames.get(position).getClientId());

                if (mNames.get(position).getStatus().equals("on hold")){

                    reference.child(mNames.get(position).getId()).child("status").setValue("accepted");
                    reference.child(mNames.get(position).getId()).child("storeName").setValue(storeName);
                }
            }
        });

        if (mNames.get(position).getStatus().equals("on hold")){

            holder.indicator.setImageResource(R.drawable.time);

            }else if (mNames.get(position).getStatus().equals("accepted")){

            holder.indicator.setImageResource(R.drawable.online);

        }else if (mNames.get(position).getStatus().equals("refused")){

            holder.indicator.setImageResource(R.drawable.offline);
        }

        holder.refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference reference;
                reference = FirebaseDatabase.getInstance().getReference("Orders").child(mNames.get(position).getClientId());

                if (mNames.get(position).getStatus().equals("on hold")){

                    reference.child(mNames.get(position).getId()).child("status").setValue("refused");
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CardView box;
        ImageView image, indicator;
        TextView plate, price, description, counter_txt;
        LinearLayout counter, accept, refuse;
        //RelativeLayout container;

        public ViewHolder(View itemView) {
            super(itemView);
            box = itemView.findViewById(R.id.box);
            //container = itemView.findViewById(R.id.container);
            image = itemView.findViewById(R.id.image);
            plate = itemView.findViewById(R.id.plate);
            price = itemView.findViewById(R.id.price);
            description = itemView.findViewById(R.id.description);
            accept = itemView.findViewById(R.id.accept);
            refuse = itemView.findViewById(R.id.refuse);
            counter = itemView.findViewById(R.id.counter);
            counter_txt = itemView.findViewById(R.id.counter_txt);
            indicator = itemView.findViewById(R.id.indicator);
        }
    }
}
