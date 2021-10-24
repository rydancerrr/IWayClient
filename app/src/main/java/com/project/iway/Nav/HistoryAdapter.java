package com.project.iway.Nav;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.project.iway.Model.Data;
import com.project.iway.R;

public class HistoryAdapter extends FirebaseRecyclerAdapter<Data, HistoryAdapter.PastViewHolder> {

    private Context mContext;
    private  ViewGroup parent;

    public HistoryAdapter(@NonNull FirebaseRecyclerOptions<Data> options, Context context) {
        super(options);
        mContext = context;
    }


    @Override
    protected void onBindViewHolder(@NonNull PastViewHolder holder, int i, @NonNull final Data data) {
        int item = getItemCount();
        holder.address.setText(data.getEditAddress());
        holder.name.setText(data.getEditName());
        holder.status.setText(data.getEditStatus());
        holder.time.setText(data.getEditTime().toString());
        Glide.with(mContext).load(data.getImageUrl()).into(holder.imgurl);
        holder.number.setText("#" .concat(String.valueOf(item-i)));
        holder.imgurl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            idCode(data);
            }
        });
    }

    @NonNull
    @Override
    public PastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_results, parent, false);
        this.parent = parent;
        return new PastViewHolder(view);
    }

    class PastViewHolder extends RecyclerView.ViewHolder{


        TextView address,name, time, status, number;
        ImageView imgurl;


        public PastViewHolder(@NonNull View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.address);
            name = itemView.findViewById(R.id.name);
            time = itemView.findViewById(R.id.time);
            status = itemView.findViewById(R.id.status);
            imgurl = itemView.findViewById(R.id.imageShow);

            number = itemView.findViewById(R.id.number);


        }
        ;
    }

    public void idCode (Data data) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_image_slider, parent, false);
        ImageView imgViewer = view.findViewById(R.id.imgViewer);
        Glide.with(view).load(data.getImageUrl()).into(imgViewer);

        final Dialog dialog = new Dialog(mContext);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.setTitle("Set your Value");
        dialog.setContentView(view);
        dialog.show();

        imgViewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }
}