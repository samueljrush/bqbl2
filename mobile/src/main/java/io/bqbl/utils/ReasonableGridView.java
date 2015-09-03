package io.bqbl.utils;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.widget.GridView;
import android.widget.ListAdapter;

/**
 * Created by sam on 8/30/2015.
 */
public class ReasonableGridView extends GridView {

  private int mAvailableSpace = -1;

  public ReasonableGridView(Context context) {
    super(context);
  }

  public ReasonableGridView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public ReasonableGridView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public ReasonableGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    // display everything, no scrolling
    int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);

    super.onMeasure(widthMeasureSpec, expandSpec);

    int widthMode = MeasureSpec.getMode(widthMeasureSpec);
    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
    int heightSize = MeasureSpec.getSize(heightMeasureSpec);

    if (widthMode == MeasureSpec.UNSPECIFIED) {
      if (getColumnWidth() > 0) {
        widthSize = getColumnWidth() + getListPaddingLeft() + getListPaddingRight();
      } else {
        widthSize = getListPaddingLeft() + getListPaddingRight();
      }
      widthSize += getVerticalScrollbarWidth();
    }

    mAvailableSpace = widthSize - getListPaddingLeft() + getListPaddingRight();

    if (getAdapter() != null) {
      setBestNumColumns(mAvailableSpace);
    }
    super.onMeasure(widthMeasureSpec, expandSpec);  // measure now that we have the right number of columns
    int width = getNumColumns() * (getColumnWidth() + getHorizontalSpacing()) - getHorizontalSpacing()
        + getPaddingLeft() + getPaddingRight();
    setMeasuredDimension(width, getMeasuredHeight());
  }

  @Override
  public void setAdapter(ListAdapter adapter) {
    super.setAdapter(adapter);
    if (adapter != null) {
      adapter.registerDataSetObserver(new DataSetObserver() {
        @Override
        public void onChanged() {
          updateBestNumColumns();
        }

        @Override
        public void onInvalidated() {
          updateBestNumColumns();
        }
      });
    }
  }

  protected int determineMaxColumns(int availableSpace) {
    int requestedHorizontalSpacing = getRequestedHorizontalSpacing();
    return (availableSpace + requestedHorizontalSpacing) / (getRequestedColumnWidth() + requestedHorizontalSpacing);
  }

  private int setBestNumColumns(int availableSpace) {
    if (getAdapter() != null) {
      return setBestNumColumns(availableSpace, getAdapter().getCount());
    }
    return -1;
  }

  private int updateBestNumColumns() {
    if (mAvailableSpace != -1 && getAdapter() != null) {
      return setBestNumColumns(mAvailableSpace, getAdapter().getCount());
    }
    return -1;
  }

  private int setBestNumColumns(int availableSpace, float numItems) {
    int maxColumns = determineMaxColumns(availableSpace);
    int numRows = (int) Math.ceil(numItems/maxColumns);
    int bestNumColumns = (int) Math.ceil(numItems / numRows);
    setNumColumns(bestNumColumns);
    return bestNumColumns;
  }
}
