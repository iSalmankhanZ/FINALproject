package com.carwash.carwash50street.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.carwash.carwash50street.Interface.ItemClickListner;
import com.carwash.carwash50street.R;

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtMenuName;
    public ImageView imageView;
    private ItemClickListner itemClickListner;

    public MenuViewHolder(View itemView) {
        super(itemView);

        txtMenuName = (TextView)itemView.findViewById(R.id.menu_name);
        imageView = (ImageView)itemView.findViewById(R.id.menu_image);
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
