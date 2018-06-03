package com.carwash.carwash50street.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.carwash.carwash50street.Interface.ItemClickListner;
import com.carwash.carwash50street.R;

public class ServiceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView service_name,service_price;
    public ImageView service_image,fav_image,share_image;

    private ItemClickListner itemClickListner;

    public ServiceViewHolder(View itemView) {
        super(itemView);
        service_name= (TextView)itemView.findViewById(R.id.service_name);
        service_image = (ImageView)itemView.findViewById(R.id.service_image);
        fav_image = (ImageView)itemView.findViewById(R.id.fav);
        share_image = (ImageView)itemView.findViewById(R.id.btnShare);
        service_price = (TextView)itemView.findViewById(R.id.service_price);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListner(ItemClickListner itemClickListner) {
        this.itemClickListner = itemClickListner;
    }

    @Override
    public void onClick(View v) {
        itemClickListner.onClick(v,getAdapterPosition(),false);
    }
}
