package com.myapp.accelerationsample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.myapp.accelerationsample.ui.theme.AccelerationSampleTheme
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.hardware.SensorEventListener
import android.content.Context
import android.hardware.Sensor
import android.util.Log
import android.widget.Toast
import androidx.compose.material.Scaffold
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.myapp.accelerationsample.ui.AppNavHost
import com.myapp.accelerationsample.ui.Screens
import com.myapp.accelerationsample.ui.component.BottomBar
import com.myapp.accelerationsample.ui.viewmodel.AndroidStateViewModel


class MainActivity : ComponentActivity() , SensorEventListener {

    // SensorManagerインスタンス
    private var sensorManager: SensorManager? = null
    private val viewModel = AndroidStateViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.screen.observe(this) {
            // 特定の画面のみ加速度センサーを起動
            if (it == Screens.SecondScreen) {
                startAccelerometer()
            } else {
                stopAccelerometer()
            }
        }

        setContent {
            val navController = rememberNavController()
            AccelerationSampleTheme {
                Scaffold(
                    bottomBar = { BottomBar(navController) },
                    backgroundColor = Color(0xfff5f5f5)
                ) {
                    AppNavHost(
                        navController = navController,
                        androidStateViewModel = viewModel
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.screen.value == Screens.SecondScreen) {
            startAccelerometer()
        }
    }

    override fun onPause() {
        super.onPause()
        stopAccelerometer()
    }

    private fun startAccelerometer() {
        if (sensorManager == null) {
            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        }
        sensorManager!!.registerListener(
            this, sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_FASTEST
        )
        Toast.makeText(this , "加速度センサー開始", Toast.LENGTH_SHORT).show();
    }

    private fun stopAccelerometer() {
        if (sensorManager == null) {
            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        }
        sensorManager?.unregisterListener(this)
        Toast.makeText(this , "加速度センサー停止", Toast.LENGTH_SHORT).show();
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
