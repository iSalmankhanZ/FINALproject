package com.carwash.carwash50street.Booking;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.RelativeLayout;

import com.carwash.carwash50street.Common.Common;
import com.carwash.carwash50street.Database.Database;
import com.carwash.carwash50street.Helper.RecyclerTouchHelper;
import com.carwash.carwash50street.Interface.RecyclerItemTouchHelper;
import com.carwash.carwash50street.Model.Favourites;
import com.carwash.carwash50street.Model.Order;
import com.carwash.carwash50street.R;
import com.carwash.carwash50street.ViewHolder.FavouritesAdapter;
import com.carwash.carwash50street.ViewHolder.FavouritesViewHolder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class FavouritesActivity extends AppCompatActivity implements RecyclerItemTouchHelper {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference serviceList;

    FavouritesAdapter adapter;

    RelativeLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        rootLayout = (RelativeLayout)findViewById(R.id.root_layout);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_fav);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Swipe to delete
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);


    }

    @Override
    public void onSwipe(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof FavouritesViewHolder)
        {
            String name = ((FavouritesAdapter)recyclerView.getAdapter()).getItem(position).getServiceName();

            final Favourites deleteItem = ((FavouritesAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());
            final int deleteIndex = viewHolder.getAdapterPosition();

            adapter.removeItem(viewHolder.getAdapterPosition());
            new Database(getBaseContext()).removeFromFavourites(deleteItem.getServiceId(), Common.currentUser.getPhone());

            Snackbar snackbar = Snackbar.make(rootLayout,name+"removed from cart!",Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.restoreItem(deleteItem,deleteIndex);
                    new Database(getBaseContext()).addToFavourites(deleteItem);

                }
            });
            recyclerView.setAdapter(adapter);
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}
