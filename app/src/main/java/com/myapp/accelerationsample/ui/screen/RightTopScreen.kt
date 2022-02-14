package com.myapp.accelerationsample.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.myapp.accelerationsample.model.value.Rotate
import com.myapp.accelerationsample.ui.component.CameraEvent
import com.myapp.accelerationsample.ui.viewmodel.AndroidStateViewModel
import com.myapp.accelerationsample.ui.viewmodel.RightTopViewModel

@Composable
fun RightTopScreen(androidStateViewModel: AndroidStateViewModel) {
    val viewModel = RightTopViewModel()
    RightTopContent(androidStateViewModel.rotate.value, viewModel)
}

@Composable
fun RightTopContent(
    rotate: Rotate,
    viewModel: RightTopViewModel
) {

    val isCamera = remember { mutableStateOf(false) }

    // data
    val imageList = viewModel.imageList.value

    Surface(color = MaterialTheme.colors.background) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Button(
                modifier = Modifier.padding(bottom = 8.dp),
                onClick = { isCamera.value = true }
            ) {
                Text(text = "カメラ起動")
            }

            // list
            imageList.forEachIndexed { index, uri ->
                Card(elevation = 8.dp) {
                    Column(modifier = Modifier.size(200.dp)) {
                        Row {
                            Text(
                                text = index.toString(),
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(text = "向き：" + uri.second.label)
                        }
                        Image(
                            painter = rememberImagePainter(uri.first),
                            contentScale = ContentScale.Crop,
                            contentDescription = null
                        )
                    }
                }
            }
            Text(
                text = "カメラ起動",
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        if (isCamera.value) {
            CameraEvent(
                rotate = rotate,
                onClickPositiveAction = { uri, rotate ->
                    viewModel.setImage(uri, rotate)
                    isCamera.value = false
                },
                onClickNegativeAction = { isCamera.value = false }
            )
        }
    }
}