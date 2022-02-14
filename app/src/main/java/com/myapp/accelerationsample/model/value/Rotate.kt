package com.myapp.accelerationsample.model.value

enum class Rotate(val label: String, val angle: Float) {
    FRONT("正面", 0.0f),
    LEFT("左", 90.0f),
    RIGHT("右", 90.0f),
    REVERSE("逆", 180.0f),
}