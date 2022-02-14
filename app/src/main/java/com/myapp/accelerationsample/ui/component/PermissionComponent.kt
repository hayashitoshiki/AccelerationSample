package com.myapp.accelerationsample.ui.component

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


/**
 * パーミッション
 *
 * @param onGranted 判定結果
 * @param permission パーミッショn
 * @param CustomPermissionDialog すでに１度拒否されているときに表示するダイアログ
 */
@Composable
fun PermissionHandler(
    onGranted: (Boolean) -> Unit,
    permission: Permissions,
    CustomPermissionDialog: @Composable () -> Unit
) {
    val context = LocalContext.current
    val cameraRationale = ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, permission.value)
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        onGranted(it)
    }

    if (ContextCompat.checkSelfPermission(context, permission.value) == PackageManager.PERMISSION_GRANTED) {
        // 許可済
        onGranted(true)
    } else {
        if (!cameraRationale) {
            // 初回時
            SideEffect {
                launcher.launch(permission.value)
            }
        } else {
            // ２回目以降で拒否時
            CustomPermissionDialog()
        }
    }
}

/**
 * 使用するパーミッション
 *
 * @property value パーミッション値
 */
enum class Permissions(val value: String) {
    CAMERA(Manifest.permission.CAMERA)
}