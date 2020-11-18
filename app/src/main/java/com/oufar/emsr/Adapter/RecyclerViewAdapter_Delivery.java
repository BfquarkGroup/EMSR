package com.oufar.emsr.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.oufar.emsr.Model.DeliveryGuy;
import com.oufar.emsr.R;
import com.oufar.emsr.Scan;

import java.util.ArrayList;

public class RecyclerViewAdapter_Delivery extends RecyclerView.Adapter<RecyclerViewAdapter_Delivery.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter_Delivery";

    private FirebaseAuth auth;

    //vars
    private ArrayList<DeliveryGuy> mNames = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter_Delivery(Context context, ArrayList<DeliveryGuy> names) {

        mNames = names;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_delivery, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        if (!mNames.get(position).getGuyImageURL().equals("default")){

            Glide.with(mContext)
                    .asBitmap()
                    .load(mNames.get(position).getGuyImageURL())
                    .into(holder.image);
        }

        holder.guy.setText(mNames.get(position).getGuyName());
        holder.phone.setText(mNames.get(position).getGuyPhone());
        holder.description.setText(mNames.get(position).getGuyDescription());
        holder.number.setVisibility(View.GONE);

        holder.scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, Scan.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("guyId", mNames.get(position).getGuyId());// sending id
                intent.putExtra("guyName", mNames.get(position).getGuyName());// sending name
                intent.putExtra("guyImage", mNames.get(position).getGuyImageURL());// sending image
                intent.putExtra("guyDescription", mNames.get(position).getGuyDescription());// sending description
                intent.putExtra("guyEmail", mNames.get(position).getGuyImageURL());// sending email
                mContext.startActivity(intent);

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
        TextView guy, phone, description, number;
        LinearLayout scan;

        public ViewHolder(View itemView) {
            super(itemView);
            box = itemView.findViewById(R.id.box);
            //container = itemView.findViewById(R.id.container);
            image = itemView.findViewById(R.id.image);
            guy = itemView.findViewById(R.id.guy);
            phone = itemView.findViewById(R.id.phone);
            description = itemView.findViewById(R.id.description);
            scan = itemView.findViewById(R.id.scan);
            number = itemView.findViewById(R.id.number);
            indicator = itemView.findViewById(R.id.indicator);
        }
    }
}
