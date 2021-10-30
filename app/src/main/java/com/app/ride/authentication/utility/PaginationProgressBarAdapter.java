package com.app.ride.authentication.utility;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.app.ride.R;
import com.paginate.recycler.LoadingListItemCreator;

public class PaginationProgressBarAdapter  implements LoadingListItemCreator {
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_progressbar, parent, false));

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View inflate) {
            super(inflate);
        }
    }
}
