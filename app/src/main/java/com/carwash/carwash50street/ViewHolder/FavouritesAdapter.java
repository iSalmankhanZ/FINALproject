package com.carwash.carwash50street.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.carwash.carwash50street.Booking.ServiceDetails;
import com.carwash.carwash50street.Booking.ServiceList;
import com.carwash.carwash50street.Common.Common;
import com.carwash.carwash50street.Interface.ItemClickListner;
import com.carwash.carwash50street.Model.Favourites;
import com.carwash.carwash50street.Model.Order;
import com.carwash.carwash50street.Model.Service;
import com.carwash.carwash50street.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesViewHolder> {

    private Context context;
    private List<Favourites> favouritesList;

    public FavouritesAdapter(Context context, List<Favourites> favouritesList) {
        this.context = context;
        this.favouritesList = favouritesList;
    }

    @NonNull
    @Override
    public FavouritesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.favourites_item,parent,false);
        return new FavouritesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouritesViewHolder viewHolder, int position) {

        viewHolder.service_name.setText(favouritesList.get(position).getServiceName());
        viewHolder.service_price.setText(String.format("Rs %s",favouritesList.get(position).getServicePrice().toString()));
        Picasso.with(context).load(favouritesList.get(position).getServiceImage())
                .into(viewHolder.service_image);


        final Favourites local = favouritesList.get(position);
        viewHolder.setItemClickListner(new ItemClickListner() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {

                //Toast.makeText(ServiceList.this, "" + local.getName(), Toast.LENGTH_SHORT).show();
                //Starting the activity from here
                Intent serviceDetails = new Intent(context, ServiceDetails.class);
                serviceDetails.putExtra("ServiceId", favouritesList.get(position).getServiceId());
                context.startActivity(serviceDetails);
            }
        });

    }

    @Override
    public int getItemCount() {
        return favouritesList.size();
    }
    public void removeItem(int position)
    {
        favouritesList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Favourites item, int position)
    {
        favouritesList.add(position,item);
        notifyItemInserted(position);
    }

    public Favourites getItem(int position)
    {
        return favouritesList.get(position);
    }
}
