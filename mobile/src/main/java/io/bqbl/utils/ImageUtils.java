package io.bqbl.utils;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.widget.ImageView;

/**
 * Created by sam on 9/2/2015.
 */
public final class ImageUtils {
  private ImageUtils() {
  }

  public static void setGrayScale(ImageView v) {
    ColorMatrix matrix = new ColorMatrix();
    matrix.setSaturation(0); //0 means grayscale
    ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
    v.setColorFilter(cf);
  }
}
