package o7;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

class HeightAlignedImageView extends ImageView {

    public HeightAlignedImageView(Context context) { super(context); }
    public HeightAlignedImageView(Context context, AttributeSet attrs) { super(context, attrs); }
    public HeightAlignedImageView(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        if (w != h) {
            setMeasuredDimension(h, h);
        }
    }
}