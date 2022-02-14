package com.myapp.accelerationsample.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.myapp.accelerationsample.model.value.Rotate
import com.myapp.accelerationsample.ui.Screens
import kotlin.math.abs

class AndroidStateViewModel :ViewModel() {

    // 状態管理
    private val _screen: MutableLiveData<Screens> = MutableLiveData(Screens.HomeScreen)
    val screen: LiveData<Screens> = _screen
    private val _x: MutableState<Double> = mutableStateOf(0.0)
    val x: State<Double> = _x
    private val _y: MutableState<Double> = mutableStateOf(0.0)
    val y: State<Double> = _y
    private val _z: MutableState<Double> = mutableStateOf(0.0)
    val z: State<Double> = _z
    private val _rotate: MutableState<Rotate> = mutableStateOf(Rotate.FRONT)
    val rotate: State<Rotate> = _rotate

    fun setTilt(x: Double, y: Double, z: Double) {
        _x.value = x
        _y.value = y
        _z.value = z
        if (abs(abs(x) - abs(y)) > 1) {
            _rotate.value =
                if (abs(x) > abs(y)) {
                    if (x > 0) {
                        Rotate.LEFT
                    } else {
                        Rotate.RIGHT
                    }
                } else {
                    if (y > 0) {
                        Rotate.FRONT
                    } else {
                        Rotate.REVERSE
                    }
                }
        }
    }

    fun setScreen(screen: Screens) {
        _screen.value = screen
    }
}