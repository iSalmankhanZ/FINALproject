package com.carwash.carwash50street.Helper;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.carwash.carwash50street.Interface.RecyclerItemTouchHelper;
import com.carwash.carwash50street.Model.Favourites;
import com.carwash.carwash50street.ViewHolder.CartViewHolder;
import com.carwash.carwash50street.ViewHolder.FavouritesViewHolder;

public class RecyclerTouchHelper extends ItemTouchHelper.SimpleCallback{

    private RecyclerItemTouchHelper listener;

    public RecyclerTouchHelper(int dragDirs, int swipeDirs, RecyclerItemTouchHelper listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if(listener!=null)
            listener.onSwipe(viewHolder,direction,viewHolder.getAdapterPosition());

    }

    //Ctrl+o
    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    //Ctrl+o
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof CartViewHolder) {
            View foreground = ((CartViewHolder) viewHolder).view_fg;
            getDefaultUIUtil().clearView(foreground);
            //super.clearView(recyclerView, viewHolder);
        } else if(viewHolder instanceof FavouritesViewHolder)  {
            View foreground = ((FavouritesViewHolder) viewHolder).view_fg;
            getDefaultUIUtil().clearView(foreground);
        }
    }

    //ctrl+o
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (viewHolder instanceof CartViewHolder) {
            View foreground = ((CartViewHolder) viewHolder).view_fg;
            getDefaultUIUtil().onDraw(c, recyclerView, foreground, dX, dY, actionState, isCurrentlyActive);
            //super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }else if (viewHolder instanceof FavouritesViewHolder) {
            View foreground = ((FavouritesViewHolder) viewHolder).view_fg;
            getDefaultUIUtil().onDraw(c, recyclerView, foreground, dX, dY, actionState, isCurrentlyActive);
        }
    }

    //ctrl+o

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        //super.onSelectedChanged(viewHolder, actionState);
        if (viewHolder!=null)
        {
            if(viewHolder instanceof CartViewHolder){
                View foreground = ((CartViewHolder)viewHolder).view_fg;
                getDefaultUIUtil().onSelected(foreground);
            }
            else if(viewHolder instanceof FavouritesViewHolder){
                View foreground = ((FavouritesViewHolder)viewHolder).view_fg;
                getDefaultUIUtil().onSelected(foreground);
            }
        }
    }

    //ctrl+o
    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (viewHolder instanceof CartViewHolder) {
            View foreground = ((CartViewHolder) viewHolder).view_fg;
            getDefaultUIUtil().onDrawOver(c, recyclerView, foreground, dX, dY, actionState, isCurrentlyActive);
            //super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        } else if (viewHolder instanceof FavouritesViewHolder) {
            View foreground = ((FavouritesViewHolder) viewHolder).view_fg;
            getDefaultUIUtil().onDrawOver(c, recyclerView, foreground, dX, dY, actionState, isCurrentlyActive);
        }
    }
}
