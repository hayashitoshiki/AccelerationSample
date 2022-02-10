package com.myapp.accelerationsample.ui.screen

import androidx.compose.material.Text
import androidx.compose.runtime.Composable


@Composable
fun HomeScreen(name: String) {
    Text(text = "Hello $name!")
}