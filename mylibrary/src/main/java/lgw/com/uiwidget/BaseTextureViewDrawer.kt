package lgw.com.uiwidget

import android.graphics.*
import android.view.TextureView
import java.util.concurrent.locks.ReentrantLock


abstract class BaseTextureViewDrawer() : Any(), TextureView.SurfaceTextureListener {

    var mUpdateOnFrame = 1
    protected var mSurfaceWidth: Int = 0
    protected var mSurfaceHeight: Int = 0
    var mCx: Float = 0.toFloat()
    var mCy: Float = 0.toFloat()
    var mClearPaint: Paint = Paint()
    protected var mPaint: Paint = Paint()

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
        val isSizeChanged = (width != mSurfaceWidth || height != mSurfaceHeight)
        mSurfaceWidth = width
        mSurfaceHeight = height
        mCx = (mSurfaceWidth.toFloat() / 2)
        mCy = (mSurfaceHeight.toFloat() / 2)
        mPaint.isAntiAlias = true
        mClearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        if (isSizeChanged) {
            onFrameSizeChanged()
        }
    }

    abstract fun onFrameSizeChanged()  //reset points

    abstract fun onFrameUpdate(canvas: Canvas, currentFrame: Int)  //reset points

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
        stop()
        return true
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
        onSurfaceTextureSizeChanged(surface, width, height)
    }


    private val lock = ReentrantLock()
    private val lockCondition = lock.newCondition()
    @Volatile
    private var mPause: Boolean = false
    var mStop: Boolean = false
    lateinit var mThread: Thread

    fun start(textureView: TextureView) {
        mPause = false
        mStop = false
        mThread = Thread {
            //if want share thread, extract it
            while (!mStop) {
                lock.lock()
                try {
                    if (mPause) {
                        lockCondition.await()
                    }
                    val canvas = textureView.lockCanvas()
                    if (canvas == null || mSurfaceWidth <= 0 || mSurfaceHeight <= 0) {
                        continue
                    }
                    canvas.drawPaint(mClearPaint)
                    onFrameUpdate(canvas, mUpdateOnFrame++)   // cycle value
                    textureView.unlockCanvasAndPost(canvas)
                    Thread.sleep(32) //30 fps
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                } finally {
                    lock.unlock()
                }
            }
        }
        mThread.start()
    }

    fun resume() {
        lock.lock()
        try {
            lockCondition.signal();
            mPause = false
        } finally {
            lock.unlock();
        }
    }

    fun pause() {
        mPause = true
    }

    fun stop() {
        mStop = true
        resume()
    }
}