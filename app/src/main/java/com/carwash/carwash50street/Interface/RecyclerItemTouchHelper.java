package com.carwash.carwash50street.Interface;

import android.support.v7.widget.RecyclerView;

public interface RecyclerItemTouchHelper {
    void onSwipe(RecyclerView.ViewHolder viewHolder,int direction,int position);
}
