package io.bqbl.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by sam on 9/3/2015.
 */
public class TallGridView extends GridView {
  public TallGridView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public TallGridView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public TallGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  @Override
  public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
        MeasureSpec.AT_MOST);
    super.onMeasure(widthMeasureSpec, expandSpec);
  }

  public TallGridView(Context context) {
    super(context);
  }
}
