package com.carwash.carwash50street.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.carwash.carwash50street.Interface.ItemClickListner;
import com.carwash.carwash50street.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtOrderId,txtOrderStatus,txtOrderPhone,txtOrderAddress;
    private ItemClickListner itemClickListner;

    public OrderViewHolder(View itemView) {
        super(itemView);
        txtOrderId = (TextView)itemView.findViewById(R.id.order_id);
        txtOrderStatus = (TextView)itemView.findViewById(R.id.order_status);
        txtOrderAddress = (TextView)itemView.findViewById(R.id.order_address);
        txtOrderPhone = (TextView)itemView.findViewById(R.id.order_phone);
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
