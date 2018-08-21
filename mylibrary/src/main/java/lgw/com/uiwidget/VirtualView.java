package lgw.com.uiwidget;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ExploreByTouchHelper;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.FrameLayout;


/**
 * the base view with editable and expanding state
 * Created by allen on 17/3/9.
 */

public class VirtualView extends FrameLayout {

  private ExploreByTouchHelper mVirtualViewHelper;

  public VirtualView( Context context ) {
    super( context );
  }

  public VirtualView( Context context, @Nullable AttributeSet attrs ) {
    super( context, attrs );
  }

  public VirtualView( Context context, @Nullable AttributeSet attrs, int defStyle ) {
    super( context, attrs, defStyle );
  }

  public void setExploreByTouchHelper( ExploreByTouchHelper touchHelper ) {
    mVirtualViewHelper = touchHelper;
    if ( mVirtualViewHelper != null ) {
      ViewCompat.setAccessibilityDelegate( this, mVirtualViewHelper );
    }
  }


  @Override
  public boolean dispatchHoverEvent( MotionEvent event ) {
    boolean handled = mVirtualViewHelper == null ? false : mVirtualViewHelper.dispatchHoverEvent( event );
    return handled || super.dispatchHoverEvent( event );
  }

  @Override
  public boolean dispatchKeyEvent( KeyEvent event ) {
    boolean handled = mVirtualViewHelper == null ? false : mVirtualViewHelper.dispatchKeyEvent( event );
    return handled || super.dispatchKeyEvent( event );
  }

  @Override
  public void onFocusChanged( boolean gainFocus, int direction, Rect previouslyFocusedRect ) {
    super.onFocusChanged( gainFocus, direction, previouslyFocusedRect );
    if ( mVirtualViewHelper != null ) {
      mVirtualViewHelper.onFocusChanged( gainFocus, direction, previouslyFocusedRect );
    }

  }

}
