package lgw.com.uiwedgit;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import lgw.com.uiwedgit.adapter.RefreshAdapter;

public class RecycleViewForSwipeActivity extends AppCompatActivity {

  RecyclerView mRecyclerView;
  SwipeRefreshLayout mSwipeRefreshLayout;

  List<String> mDatas = new ArrayList<>();
  private RefreshAdapter mRefreshAdapter;
  private LinearLayoutManager mLinearLayoutManager;
  private Handler mHandler;

  @Override
  protected void onCreate( Bundle savedInstanceState ) {
    super.onCreate( savedInstanceState );
    requestWindowFeature( Window.FEATURE_NO_TITLE );
    setContentView( R.layout.activity_recycleview );
    initView();
    initData();
    initListener();
  }


  private void initView() {
    mRecyclerView = findViewById( R.id.recyclerView );
    mSwipeRefreshLayout = findViewById( R.id.swipeRefreshLayout );
    mSwipeRefreshLayout.setColorSchemeColors( Color.RED, Color.BLUE, Color.GREEN );
  }

  private void initData() {
    for ( int i = 0; i < 20; i++ ) {
      mDatas.add( " Item " + i );
    }
    initRecyclerView();
  }

  private void initRecyclerView() {
    mRefreshAdapter = new RefreshAdapter( this, mDatas );
    mLinearLayoutManager = new LinearLayoutManager( this, LinearLayoutManager.VERTICAL, false );
    mRecyclerView.setLayoutManager( mLinearLayoutManager );
    mRecyclerView.setAdapter( mRefreshAdapter );

    ItemTouchHelper touchHelper = new ItemTouchHelper( new SimpleItemTouchHelperCallback() );
    touchHelper.attachToRecyclerView( mRecyclerView );
  }

  private void initListener() {
    mHandler = new Handler(  );
    initPullRefresh();
    initLoadMoreListener();
  }


  private void initPullRefresh() {
    mSwipeRefreshLayout.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        mHandler.postDelayed( new Runnable() {
          @Override
          public void run() {
            List<String> headDatas = new ArrayList<String>();
            for ( int i = 0; i < 5; i++ ) {
              headDatas.add( "updated item  " + i );
            }
            mRefreshAdapter.updateItems( headDatas, 5 );
            mSwipeRefreshLayout.setRefreshing( false );
            Toast.makeText( RecycleViewForSwipeActivity.this, "updated " + headDatas.size() + " items", Toast.LENGTH_SHORT ).show();
          }

        }, 3000 );

      }
    } );
  }

  private void initLoadMoreListener() {

    mRecyclerView.addOnLayoutChangeListener( new View.OnLayoutChangeListener() {
      boolean checked = false;
      @Override
      public void onLayoutChange( View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom ) {
        if(!checked){
          checked = true;
          if(mRecyclerView.computeVerticalScrollRange() > mRecyclerView.computeVerticalScrollExtent()){
            mHandler.post( new Runnable() {
              @Override
              public void run() {
                mRefreshAdapter.setLoadState( RefreshAdapter.LOAD_DEFAULT );
              }
            } );
          }
        }
      }
    } );

    mRecyclerView.addOnScrollListener( new RecyclerView.OnScrollListener() {
      int count = 0;

      @Override
      public void onScrollStateChanged( RecyclerView recyclerView, int newState ) {
        super.onScrollStateChanged( recyclerView, newState );
        //scroll to bottom
        if ( newState == RecyclerView.SCROLL_STATE_IDLE && !mRefreshAdapter.isLoadGone() && !recyclerView.canScrollVertically( 1 ) ) {
          new Handler().postDelayed( new Runnable() {
            @Override
            public void run() {
              List<String> footerDatas = new ArrayList<String>();
              for ( int i = 0; i < 10; i++ ) {
                footerDatas.add( "footer  item" + i );
              }
              count++;
              mRefreshAdapter.addItems( footerDatas );
              if ( count > 1 ) {
                mRefreshAdapter.setLoadState( RefreshAdapter.LOAD_GONE );
              } else {
                mRefreshAdapter.setLoadState( RefreshAdapter.LOAD_DEFAULT );
              }
              Toast.makeText( RecycleViewForSwipeActivity.this, "load more " + footerDatas.size() + " items", Toast.LENGTH_SHORT ).show();
            }
          }, 3000 );
          mRefreshAdapter.setLoadState( RefreshAdapter.LOADING );
        }
      }
    } );

  }


  private class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

    @Override
    public int getMovementFlags( RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder ) {
      int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
      int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
      return makeMovementFlags( dragFlags, swipeFlags );
    }

    @Override
    public boolean isLongPressDragEnabled() {
      return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
      return true;
    }

    @Override
    public boolean onMove( RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target ) {
      mRefreshAdapter.onItemMove( viewHolder.getAdapterPosition(), target.getAdapterPosition() );
      return true;
    }

    @Override
    public void onSwiped( final RecyclerView.ViewHolder viewHolder, int direction ) {
      mRefreshAdapter.onItemDismiss( viewHolder.getAdapterPosition() );
    }


    @Override
    public void clearView( RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder ) {
      super.clearView( recyclerView, viewHolder );
      //reset
      viewHolder.itemView.setScrollX( 0 );
    }

    @Override
    public void onChildDraw( Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive ) {
      if ( actionState == ItemTouchHelper.ACTION_STATE_SWIPE ) {
        if ( Math.abs( dX ) <= getSlideLimitation( viewHolder ) ) {
          viewHolder.itemView.scrollTo(  -( int ) dX, 0 );
        }
      } else {
        super.onChildDraw( c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive );
      }
    }

    int width =0 ;

    public int getSlideLimitation( RecyclerView.ViewHolder viewHolder ) {
      if(width==0){
        width = viewHolder.itemView.findViewById( R.id.tv_text ).getWidth();
      }
      return width;
    }
  }

}
