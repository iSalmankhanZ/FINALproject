package com.carwash.carwash50street.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.carwash.carwash50street.Common.Common;
import com.carwash.carwash50street.Interface.ItemClickListner;
import com.carwash.carwash50street.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        View.OnCreateContextMenuListener{

    public TextView txt_cart_name,txt_price;
    public ImageView img_cart_count;

    public RelativeLayout view_background;
    public LinearLayout view_fg;

    private ItemClickListner itemClickListner;

    public void setTxt_cart_name(TextView txt_cart_name) {
        this.txt_cart_name = txt_cart_name;
    }

    public CartViewHolder(View itemView) {
        super(itemView);
        txt_cart_name = (TextView)itemView.findViewById(R.id.cart_item_name);
        txt_price = (TextView)itemView.findViewById(R.id.card_item_Price);
        img_cart_count = (ImageView)itemView.findViewById(R.id.cart_item_count);
        view_background = (RelativeLayout)itemView.findViewById(R.id.view_background);
        view_fg = (LinearLayout)itemView.findViewById(R.id.view_foreground);

        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        contextMenu.setHeaderTitle("Select action");
        contextMenu.add(0,0,getAdapterPosition(), Common.DELETE);
    }
}