package com.myapp.accelerationsample.ui.viewmodel

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.myapp.accelerationsample.model.value.Rotate

class RightTopViewModel: ViewModel() {

    private val _imageList: MutableState<List<Pair<Uri, Rotate>>> = mutableStateOf(listOf())
    val imageList: State<List<Pair<Uri, Rotate>>> = _imageList

    fun setImage(uri: Uri, rotate: Rotate) {
        val list = _imageList.value.toMutableList()
        list.add(Pair(uri, rotate))
        _imageList.value = list
    }
}