package lgw.com.uiwidget

import android.content.Context
import android.util.AttributeSet
import android.view.TextureView
import android.view.View

class ShellTextureView : TextureView {
    constructor(context: Context?) : super(context) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView()
    }

    var mDrawer : BaseTextureViewDrawer ?= null;

    private fun initView() {
        isOpaque = false
        isClickable = false
    }

    fun setDrawer(drawer : BaseTextureViewDrawer){
        mDrawer = drawer;
        surfaceTextureListener = drawer;
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        if(visibility== View.VISIBLE)
            mDrawer?.resume()
        else
            mDrawer?.pause()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mDrawer?.stop()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mDrawer?.start(this)
    }


}