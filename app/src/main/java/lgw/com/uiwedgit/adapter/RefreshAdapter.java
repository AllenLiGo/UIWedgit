package lgw.com.uiwedgit.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import lgw.com.uiwedgit.R;

public class RefreshAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  public static final int LOAD_DEFAULT = 0;
  public static final int LOADING = 1;
  public static final int LOAD_GONE = 2;

  Context mContext;
  LayoutInflater mInflater;
  List<String> mDatas;
  private static final int TYPE_ITEM = 0;
  private static final int TYPE_FOOTER = 1;
  private int mLoadState = LOAD_GONE;


  public RefreshAdapter( Context context, List<String> datas ) {
    mContext = context;
    mInflater = LayoutInflater.from( context );
    mDatas = datas;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType ) {

    if ( viewType == TYPE_ITEM ) {
      View itemView = mInflater.inflate( R.layout.item_refresh_recylerview, parent, false );
      return new ItemViewHolder( itemView );
    } else if ( viewType == TYPE_FOOTER ) {
      View itemView = mInflater.inflate( R.layout.load_more_footview_layout, parent, false );
      return new FooterViewHolder( itemView );
    }
    return null;
  }

  @Override
  public void onBindViewHolder( RecyclerView.ViewHolder holder, int position ) {

    if ( holder instanceof ItemViewHolder ) {
      ItemViewHolder itemViewHolder = ( ItemViewHolder ) holder;
      String str = mDatas.get( position );
      itemViewHolder.mTvContent.setText( str );
    } else if ( holder instanceof FooterViewHolder ) {
      FooterViewHolder footerViewHolder = ( FooterViewHolder ) holder;
      switch ( mLoadState ) {
        case LOAD_DEFAULT:
          footerViewHolder.mTvLoadText.setText( "pull up to load more" );
          break;
        case LOADING:
          footerViewHolder.mTvLoadText.setText( "loading..." );
          break;
        case LOAD_GONE:
          break;
      }
    }

  }

  public void setLoadState( int loadState ) {
    mLoadState = loadState;
    if ( mLoadState == LOAD_GONE ) {
      notifyItemRemoved( getItemCount() - 1 );
    } else {
      notifyItemChanged( getItemCount() - 1 );
    }
  }

  public boolean isLoadGone() {
    return mLoadState == LOAD_GONE;
  }

  @Override
  public int getItemCount() {
    return mDatas.size() + ( isLoadGone() ? 0 : 1 );
  }

  @Override
  public int getItemViewType( int position ) {
    if ( position + 1 == getItemCount() && !isLoadGone() ) {
      return TYPE_FOOTER;
    }
    return TYPE_ITEM;
  }

  public class ItemViewHolder extends RecyclerView.ViewHolder {

    TextView mTvContent;

    public ItemViewHolder( View itemView ) {
      super( itemView );
      mTvContent = itemView.findViewById( R.id.tvContent );
      initListener( itemView );
    }

    private void initListener( View itemView ) {
      itemView.setOnClickListener( new View.OnClickListener() {
        @Override
        public void onClick( View v ) {
          Toast.makeText( mContext, "poistion " + getAdapterPosition(), Toast.LENGTH_SHORT ).show();
        }
      } );
    }
  }

  public class FooterViewHolder extends RecyclerView.ViewHolder {

    TextView mTvLoadText;

    public FooterViewHolder( View itemView ) {
      super( itemView );
      mTvLoadText = itemView.findViewById( R.id.tvLoadText );
    }
  }


  public void updateItems( List<String> items, int from ) {
    for ( int i = 0; i < items.size(); i++ ) {
      mDatas.set( from + i, items.get( i ) );
    }
    notifyItemRangeChanged( from, items.size() );
  }

  public void addItems( List<String> items ) {
    mDatas.addAll( items );
    notifyDataSetChanged();
  }


  public void onItemMove( int fromPosition, int toPosition ) {
    Collections.swap( mDatas, fromPosition, toPosition );
    notifyItemMoved( fromPosition, toPosition );
  }

  public void onItemDismiss( int position ) {
    mDatas.remove( position );
    notifyItemRemoved( position );
  }
}