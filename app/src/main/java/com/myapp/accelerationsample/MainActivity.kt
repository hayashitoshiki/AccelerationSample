package com.myapp.accelerationsample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import com.myapp.accelerationsample.ui.screen.HomeScreen
import com.myapp.accelerationsample.ui.theme.AccelerationSampleTheme
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.hardware.SensorEventListener
import android.content.Context
import android.hardware.Sensor
import android.util.Log
import com.myapp.accelerationsample.ui.viewmodel.AndroidStateViewModel


class MainActivity : ComponentActivity() , SensorEventListener {

    // SensorManagerインスタンス
    private var sensorManager: SensorManager? = null
    private val viewModel = AndroidStateViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // SensorManagerのインスタンスを取得する
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager!!.registerListener(
            this, sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_FASTEST
        )

        setContent {
            AccelerationSampleTheme {
                Surface(color = MaterialTheme.colors.background) {
                    HomeScreen(viewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // センサー再開
        if (sensorManager == null) {
            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        }
        sensorManager!!.registerListener(
            this, sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_FASTEST
        )
    }
    override fun onPause() {
        super.onPause()
        // センサー停止
        if (sensorManager == null) {
            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        }
        sensorManager?.unregisterListener(this)
    }

    // センサーの値が変化すると呼ばれる
    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0].toDouble()
        val y = event.values[1].toDouble()
        val z = event.values[2].toDouble()
        viewModel.setTilt(x, y, z)
        Log.d("MainActivity", "x = $x : y = $y : z = $z")
    }

    // センサーの精度が変更されると呼ばれる
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
