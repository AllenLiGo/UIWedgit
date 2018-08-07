package lgw.com.uiwedgit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import lgw.com.uiwidget.WaveTextureViewDrawer

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById(R.id.act_main_recycler_view) as RecyclerView
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.setLayoutManager(layoutManager)

        val mDataSet = ArrayList<MainItem>()
        addItems(mDataSet)

        val adapter = MainAdapter(mDataSet)
        recyclerView.setAdapter(adapter)
    }

    private fun addItems(dataSet: MutableList<MainItem>) {
        dataSet.add(MainItem(WaveTextureViewDrawer::class.java!!, BeziarWaveActivity::class.java!!))
    }

    private class MainAdapter internal constructor(private val mDataSet: List<MainItem>?) : RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false) as TextView
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val v = holder.mTextView
            val item = mDataSet!![position]
            v.setBackgroundResource(if (position % 2 == 0) R.drawable.ic_sample_selector_primary else R.drawable.ic_sample_selector_normal)
            v.setText(item.mViewClz.simpleName)
            v.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View) {
                    v.getContext().startActivity(Intent(v.getContext(), item.mTargetActivityClz))
                }
            })
        }

        override fun getItemCount(): Int {
            return mDataSet?.size ?: 0
        }
    }

    private class ViewHolder internal constructor(internal var mTextView: TextView) : RecyclerView.ViewHolder(mTextView)

    private class MainItem {

        internal val mViewClz: Class<*>
        internal val mTargetActivityClz: Class<out Activity>

        internal constructor(viewClz: Class<*>, targetActivityClz: Class<out Activity>) {
            mViewClz = viewClz
            mTargetActivityClz = targetActivityClz
        }

        internal constructor(targetActivityClz: Class<out Activity>) {
            mViewClz = targetActivityClz
            mTargetActivityClz = targetActivityClz
        }
    }
}
