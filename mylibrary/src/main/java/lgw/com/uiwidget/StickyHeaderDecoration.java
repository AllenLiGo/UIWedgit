package lgw.com.uiwidget;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.widget.ExploreByTouchHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

import java.util.ArrayList;
import java.util.List;

import lgw.com.uiwidget.utils.DimensionUtil;


public class StickyHeaderDecoration extends RecyclerView.ItemDecoration {

  public interface StickHeaderInterface {

    boolean isSticky( int position );

    View getStickyView( RecyclerView parent, int position );

    String getStickyViewAx( int position );
  }

  private RecyclerView mRecyclerView;
  private RecyclerView.Adapter mAdapter;
  private StickHeaderInterface mStickHeaderInterface;
  private int mHeaderHeight = 0, mHeaderWidth = 0, mHeaderMarginBottom = 0;
  private ArrayList<VirtualViewData> mHeaderViews = new ArrayList();
  private VirtualView mVirtualView;


  public StickyHeaderDecoration( RecyclerView recyclerView, VirtualView virtualView ) {
    this.mRecyclerView = recyclerView;
    mVirtualView = virtualView;
    mVirtualView.setExploreByTouchHelper( new AssessmentVirtualViewHelper( mVirtualView ) );
    this.mAdapter = recyclerView.getAdapter();
    if ( mAdapter == null ) {
      throw new RuntimeException( "please set Decoration after set mAdapter" );
    }

    if ( mAdapter instanceof StickHeaderInterface ) {
      mStickHeaderInterface = ( StickHeaderInterface ) mAdapter;
      return;
    }
    throw new RuntimeException( "please make your mAdapter implements StickHeaderInterface" );
  }

  /**
   * set the item offset if want insert the decoration, or skip it just to show float view
   */
  @Override
  public void getItemOffsets( Rect outRect, View view, RecyclerView parent, RecyclerView.State state ) {
    super.getItemOffsets( outRect, view, parent, state );

    int itemPosition = parent.getChildAdapterPosition( view );
    if ( itemPosition != RecyclerView.NO_POSITION ) {
      if ( mStickHeaderInterface.isSticky( itemPosition ) ) {
        View header = mStickHeaderInterface.getStickyView( parent, itemPosition );
        Rect headerMargins = DimensionUtil.getMargins( header );

        if ( mHeaderWidth == 0 ) {
          int[] size = DimensionUtil.getMeasureSize( header, mRecyclerView.getWidth() );
          mHeaderWidth = size[0];
          mHeaderHeight = size[1];
          mHeaderMarginBottom = headerMargins.bottom;
        }
        outRect.top = mHeaderHeight + ( itemPosition == 0 ? 0 : headerMargins.top ) + mHeaderMarginBottom;
      }
    }
  }

  @Override
  public void onDrawOver( Canvas canvas, RecyclerView parent, RecyclerView.State state ) {
    super.onDrawOver( canvas, parent, state );
    if ( parent.getChildCount() <= 0 || mAdapter.getItemCount() <= 0 ) {
      return;
    }

    mHeaderViews.clear();
    //get the secondary header
    int secondaryTop = 0, firstViewPosition = 0, top;
    for ( int i = 0; i < parent.getChildCount(); i++ ) {
      View itemView = parent.getChildAt( i );
      int position = parent.getChildAdapterPosition( itemView );
      if ( i == 0 ) {
        firstViewPosition = position;
      }
      if ( position == RecyclerView.NO_POSITION ) {
        continue;
      }
      if ( mStickHeaderInterface.isSticky( position ) ) {
        top = itemView.getTop() - mHeaderHeight - 2 * mHeaderMarginBottom;
        if ( secondaryTop == 0 ) {
          secondaryTop = top;
        }
        View header = mStickHeaderInterface.getStickyView( parent, position );
        drawHeader( canvas, header, top, position );
      }
    }
    //draw the top one
    View header = mStickHeaderInterface.getStickyView( parent, firstViewPosition );
    top = secondaryTop > 0 && secondaryTop < mHeaderHeight + mHeaderMarginBottom ? secondaryTop - mHeaderHeight - mHeaderMarginBottom : 0;
    drawHeader( canvas, header, top, firstViewPosition );
    resetVirtualViewState();
  }

  private void drawHeader( Canvas canvas, View header, int top, int position ) {
    canvas.save();
    canvas.translate( 0, top );
    header.draw( canvas );
    canvas.restore();
    mHeaderViews.add( new VirtualViewData( new Rect( 0, top, mHeaderWidth, top + mHeaderHeight ), mStickHeaderInterface.getStickyViewAx( position ) ) );
  }

  public void resetVirtualViewState() {
    //if no virtual views, do not focus on it, or can not navigate to next view automatically
    mVirtualView.setImportantForAccessibility( mHeaderViews.isEmpty() ? View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS : View.IMPORTANT_FOR_ACCESSIBILITY_YES );
  }


  private class AssessmentVirtualViewHelper extends ExploreByTouchHelper {

    public AssessmentVirtualViewHelper( View host ) {
      super( host );
    }

    @Override
    protected int getVirtualViewAt( float x, float y ) {
      int size = mHeaderViews.size();
      for ( int i = 0; i < size; i++ ) {
        VirtualViewData data = mHeaderViews.get( i );
        if ( data.mRect.contains( ( int ) x, ( int ) y ) ) {
          return i;
        }
      }
      return INVALID_ID;
    }

    @Override
    protected void getVisibleVirtualViews( List<Integer> virtualViewIds ) {
      int size = mHeaderViews.size();
      for ( int i = 0; i < size; i++ ) {
        virtualViewIds.add( i );
      }
    }

    @Override
    protected void onPopulateEventForVirtualView( int virtualViewId, AccessibilityEvent event ) {
      VirtualViewData data = getVirtualViewData( virtualViewId );
      if ( data != null ) {
        event.setContentDescription( data.axText );
      }
    }

    @Override
    protected void onPopulateNodeForVirtualView( int virtualViewId, AccessibilityNodeInfoCompat node ) {
      VirtualViewData data = getVirtualViewData( virtualViewId );
      if ( node != null ) {
        node.setContentDescription( data.axText );
        node.addAction( AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK );
        Rect rect = ( data == null ? new Rect( 0, 0, 0, 0 ) : data.mRect );
        node.setBoundsInParent( rect );
      }
    }

    @Override
    protected boolean onPerformActionForVirtualView( int virtualViewId, int action, Bundle arguments ) {
      return true;
    }

    private VirtualViewData getVirtualViewData( int virtualViewId ) {
      if ( virtualViewId >= 0 && virtualViewId < mHeaderViews.size() ) {
        return mHeaderViews.get( virtualViewId );
      }
      return null;
    }
  }

  class VirtualViewData {

    Rect mRect;
    String axText;

    VirtualViewData( Rect r, String s ) {
      mRect = r;
      axText = s;
    }

  }

}
