package lgw.com.uiwedgit.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import lgw.com.uiwedgit.R;
import lgw.com.uiwidget.StickyHeaderDecoration;

public class StickyAdapter extends RecyclerView.Adapter<StickyAdapter.InnerHolder> implements StickyHeaderDecoration.StickHeaderInterface {

  public StickyAdapter( Activity activity, List<String> dates ) {
    this.activity = activity;
    this.dates = dates;
  }

  @Override
  public boolean isSticky( int position ) {
    return position % 10 == 0;
  }

  Activity activity;
  private List<String> dates;

  @Override
  public InnerHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
    View inflate = LayoutInflater.from( activity ).inflate( R.layout.item_refresh_recylerview, parent, false );
    return new InnerHolder( inflate );
  }

  @Override
  public void onBindViewHolder( InnerHolder holder, int position ) {
    holder.itemView.setBackgroundResource( android.R.color.white );
    holder.tvText.setTextColor( Color.DKGRAY );
    holder.tvText.setText( dates.get( position ) );
  }

  @Override
  public int getItemCount() {
    return dates.size();
  }

  View stickyView;
  TextView textView;

  @Override
  public View getStickyView( RecyclerView parent, int position ) {
    if ( stickyView == null ) {
      stickyView = LayoutInflater.from( activity ).inflate( R.layout.item_refresh_recylerview, parent, false );
      textView = stickyView.findViewById( R.id.tvContent );
      textView.setTextColor( Color.RED );
      textView.setBackgroundColor( Color.BLUE );
    }
    textView.setText( position / 10 + "" );
    return stickyView;
  }

  @Override
  public String getStickyViewAx( int position ) {
    return position / 10 + " is clicked";
  }

  class InnerHolder extends RecyclerView.ViewHolder {

    TextView tvText;

    public InnerHolder( View itemView ) {
      super( itemView );
      tvText = ( TextView ) itemView.findViewById( R.id.tvContent );
    }
  }
}
