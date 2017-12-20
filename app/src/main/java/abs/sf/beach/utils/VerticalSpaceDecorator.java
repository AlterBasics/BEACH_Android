package abs.sf.beach.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;


public class VerticalSpaceDecorator extends RecyclerView.ItemDecoration {
    private int space;

    public VerticalSpaceDecorator(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
        outRect.bottom = this.space;
    }
}
