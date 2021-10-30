package com.app.ride.authentication.utility;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VerticalSpaceChatItemDecoration extends RecyclerView.ItemDecoration {
    int verticalSpaceHeight;
    public VerticalSpaceChatItemDecoration(int verticalSpaceHeight){
        this.verticalSpaceHeight = verticalSpaceHeight;
    }
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount()) {
            outRect.top = verticalSpaceHeight;
        }
        outRect.bottom = verticalSpaceHeight;
    }
}
