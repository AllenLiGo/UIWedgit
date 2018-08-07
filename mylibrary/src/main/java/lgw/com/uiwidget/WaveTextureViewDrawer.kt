package lgw.com.uiwidget

import android.graphics.*
import java.util.*

class WaveTextureViewDrawer() : BaseTextureViewDrawer() {

    private enum class Direction {
        LEFT, TOP, RIGHT, BOTTOM
    }

    private enum class Process {
        UP, WAVE, DOWN, IDLE
    }

    private enum class Position {
        TOP, BOTTOM
    }

    //wave
    private lateinit var mHorizontalWavePoints: HorizontalWavePoints

    override fun onFrameSizeChanged() {
        mHorizontalWavePoints = HorizontalWavePoints(mSurfaceWidth.toFloat(), mSurfaceHeight.toFloat())
    }

    override fun onFrameUpdate(canvas: Canvas, currentFrame: Int) {
        mHorizontalWavePoints?.onDrawWave(canvas)
    }

    private val mRandom = Random()
    private val mColors = intArrayOf( Color.BLUE, Color.GREEN, Color.MAGENTA)
    private fun getRandomColor(): Int {
        return mColors[(mRandom.nextInt(mColors.size))]
    }

    internal inner class HorizontalWavePoints(private val mWidth: Float, private val mHeight: Float) {

        private var mFrames = 0
        var mPointSize: Int = 0
        lateinit var mPointsList: ArrayList<PointF>
        var mWaveHeight: Float = 0.toFloat()
        var mWaveWidth: Float = 0.toFloat()
        var mLeftSide: Float = 0.toFloat()
        var mSpeedX: Float = 0.toFloat()
        var mWavePath: Path
        val WAVE_WIDTH_WEIGHT = 0.75f
        val WAVE_HEIGHT_WEIGHT = 0.03f

        var xOffset = 0f

        private val FRAMES_UP = 256
        private val FRAMES_WAVE = 128
        private val FRAMES_DOWN = 256
        private var mProcess = Process.IDLE
        private var mPosition = Position.BOTTOM
        private var mDirection = Direction.LEFT

        init {
            mPaint = Paint()
            mPaint.isAntiAlias = true
            mPaint.style = Paint.Style.FILL
            setWaveColor(getRandomColor())
            mWavePath = Path()
            setWaveSize(WAVE_WIDTH_WEIGHT, WAVE_HEIGHT_WEIGHT)
        }

        fun setWaveColor(color: Int) {
            mPaint.color = color
        }

        fun setWaveSize(widthWeight: Float, heightWeight: Float) {
            mWaveWidth = mWidth * widthWeight
            mWaveHeight = mWidth * heightWeight
            initPointList(Position.BOTTOM, Direction.LEFT)
        }

        fun start() {
            mFrames = 0
            mProcess = Process.UP
        }

        private fun initPointList(position: Position, direction: Direction) {
            mPosition = position
            mDirection = direction

            when (mDirection) {
                Direction.LEFT -> {
                    mLeftSide = -mWaveWidth
                    mSpeedX = mSurfaceWidth.toFloat() / 50
                }
                Direction.RIGHT -> {
                    mLeftSide = 0f
                    mSpeedX = -mSurfaceWidth.toFloat() / 50
                }
            }
            val n = Math.round(mWidth / mWaveWidth + 0.5).toInt()
            mPointSize = 4 * n + 5
            mPointsList = ArrayList(mPointSize)
            for (i in 0 until mPointSize) {
                val x = i * mWaveWidth / 4 + mLeftSide
                var y = 0f
                when (i % 4) {
                    0, 2 -> y = 0f
                    1 -> y = -mWaveHeight
                    3 -> y = mWaveHeight
                }
                mPointsList?.add(PointF(x, y))
            }
        }

        fun onDrawWave(canvas: Canvas) {
            if (mFrames > 100000) {
                mFrames = 0
            } else {
                mFrames++
            }
            when (mProcess) {
                Process.UP -> if (mFrames > FRAMES_UP) {
                    mFrames = 0
                    mProcess = Process.WAVE
                    onDrawWave(canvas)
                } else {
                    draw(canvas, mWaveHeight * (mFrames * 1.0f / FRAMES_UP))
                }
                Process.WAVE -> if (mFrames > FRAMES_WAVE) {
                    mFrames = 0
                    mProcess = Process.DOWN
                    onDrawWave(canvas)
                } else {
                    draw(canvas, mWaveHeight)
                }
                Process.DOWN -> if (mFrames > FRAMES_DOWN) {
                    xOffset = 0f
                    mFrames = 0
                    mProcess = Process.IDLE
                    onDrawWave(canvas)
                } else {
                    draw(canvas, mWaveHeight * (1 - mFrames * 1.0f / FRAMES_DOWN))
                }
                Process.IDLE -> if (mFrames > 10) {
                    startRandomWave()
                }
            }
        }

        fun startRandomWave() {
            val i = Random().nextInt(4)
            when (i) {
                0 -> mHorizontalWavePoints.initPointList(Position.TOP, Direction.RIGHT)
                1 -> mHorizontalWavePoints.initPointList(Position.TOP, Direction.LEFT)
                2 -> mHorizontalWavePoints.initPointList(Position.BOTTOM, Direction.RIGHT)
                3 -> mHorizontalWavePoints.initPointList(Position.BOTTOM, Direction.LEFT)
            }
            mHorizontalWavePoints.setWaveColor(getRandomColor())
            mHorizontalWavePoints.start()
        }

        private fun draw(canvas: Canvas, yOffset: Float) {
            xOffset += mSpeedX *yOffset  / mWaveHeight
            //xOffset = Math.max(xOffset, 20.0f)
            if (Math.abs(xOffset) >= mWaveWidth) {
                xOffset = 0f
            }
            var levelLine = yOffset
            when (mPosition) {
                Position.TOP -> levelLine = yOffset
                Position.BOTTOM -> levelLine = mHeight - yOffset
            }
            mWavePath.reset()
            mWavePath.moveTo(mPointsList[0].x + xOffset, mPointsList[0].y + levelLine)
            var i: Int
            var tempY: Float
            i = 0
            while (i < mPointSize - 2) {
                tempY = mPointsList?.let { it[i + 1].y }
                mWavePath.quadTo(mPointsList[i + 1].x + xOffset, if (tempY > 0)
                    levelLine + yOffset
                else
                    levelLine - yOffset, mPointsList[i + 2].x + xOffset, mPointsList[i + 2].y + levelLine)
                i = i + 2
            }
            when (mPosition) {
                Position.TOP -> {
                    mWavePath.lineTo(mPointsList[i].x + (mLeftSide + mWaveWidth) + xOffset, 0f)
                    mWavePath.lineTo(mLeftSide, 0f)
                }
                Position.BOTTOM -> {
                    mWavePath.lineTo(mPointsList[i].x + (mLeftSide + mWaveWidth) + xOffset, mHeight)
                    mWavePath.lineTo(mLeftSide, mHeight)
                }
            }
            mWavePath.close()
            canvas.drawPath(mWavePath, mPaint)
        }
    }
}