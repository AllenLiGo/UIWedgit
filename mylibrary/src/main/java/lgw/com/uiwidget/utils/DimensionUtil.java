package lgw.com.uiwidget.utils;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

public class DimensionUtil {

  /**
   * Returns representing margins for any view.
   */
  public static Rect getMargins( View view ) {
    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

    if ( layoutParams instanceof ViewGroup.MarginLayoutParams ) {
      ViewGroup.MarginLayoutParams marginLayoutParams = ( ViewGroup.MarginLayoutParams ) layoutParams;
      return getMarginRect( marginLayoutParams );
    } else {
      return new Rect();
    }
  }

  /**
   * Converts MarginLayoutParams into a representative Rect
   */
  private static Rect getMarginRect( ViewGroup.MarginLayoutParams marginLayoutParams ) {
    return new Rect(
            marginLayoutParams.leftMargin,
            marginLayoutParams.topMargin,
            marginLayoutParams.rightMargin,
            marginLayoutParams.bottomMargin
    );
  }


  public static int[] getMeasureSize( View view, int widthSize ) {
    int widthSpec = View.MeasureSpec.makeMeasureSpec(widthSize, View.MeasureSpec.EXACTLY );
    int heightSpec = View.MeasureSpec.makeMeasureSpec( 0, View.MeasureSpec.UNSPECIFIED );
    view.measure( widthSpec, heightSpec );
    view.layout( 0, 0, view.getMeasuredWidth(), view.getMeasuredHeight() );
    return new int[]{view.getMeasuredWidth(), view.getMeasuredHeight()};
  }

}
