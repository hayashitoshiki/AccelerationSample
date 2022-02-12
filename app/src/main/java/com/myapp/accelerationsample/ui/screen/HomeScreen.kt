package com.myapp.accelerationsample.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.myapp.accelerationsample.ui.viewmodel.AndroidStateViewModel

@Composable
fun HomeScreen(viewModel: AndroidStateViewModel) {
    Column {
        Text(text = "傾きセンサー　サンプル")
        Text(text = "x = " + viewModel.x.value)
        Text(text = "y = " + viewModel.y.value)
        Text(text = "Z = " + viewModel.z.value)
        Text(text = "傾き = " + viewModel.tilt.value)
    }
}