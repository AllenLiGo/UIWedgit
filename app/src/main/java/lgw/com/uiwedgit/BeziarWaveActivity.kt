package lgw.com.uiwedgit

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import lgw.com.uiwidget.ShellTextureView
import lgw.com.uiwidget.WaveTextureViewDrawer

class BeziarWaveActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wave)

        val waveView: ShellTextureView = findViewById(R.id.wave_view)
        waveView.setDrawer(WaveTextureViewDrawer())
    }


}
