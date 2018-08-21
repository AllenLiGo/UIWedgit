package lgw.com.uiwedgit;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import lgw.com.uiwedgit.adapter.StickyAdapter;
import lgw.com.uiwidget.StickyHeaderDecoration;
import lgw.com.uiwidget.VirtualView;

public class RecycleViewForStickyActivity extends AppCompatActivity {

  private RecyclerView recycle;
  private List<String> dates = new ArrayList<>();
  private SwipeRefreshLayout mSwipeRefreshLayout;

  @Override
  protected void onCreate( Bundle savedInstanceState ) {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.activity_recycleview );
    recycle =  findViewById( R.id.recyclerView );

    for ( int i = 0; i < 100; i++ ) {
      dates.add( "date : " + i );
    }

    recycle.setLayoutManager( new LinearLayoutManager( this ) );
    recycle.setAdapter( new StickyAdapter( this, dates ) );
    VirtualView virtualView = findViewById( R.id.virtual_view );
    recycle.addItemDecoration( new StickyHeaderDecoration( recycle, virtualView ) );

    mSwipeRefreshLayout = findViewById( R.id.swipeRefreshLayout );
    mSwipeRefreshLayout.setColorSchemeColors( Color.RED, Color.BLUE, Color.GREEN );
    mSwipeRefreshLayout.setEnabled( false );
  }


}
