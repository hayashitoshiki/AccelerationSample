package com.myapp.accelerationsample.ui.component

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.MediaActionSound
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import com.myapp.accelerationsample.model.value.Rotate
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * カメライベント処理
 *
 * @param onClickPositiveAction 写真撮影後処理
 * @param onClickNegativeAction キャンセルイベント
 */
@Composable
fun CameraEvent(
    rotate: Rotate,
    onClickPositiveAction: (Uri, Rotate) -> Unit,
    onClickNegativeAction: () -> Unit
) {
    var isPermission by remember { mutableStateOf(false) }
    Scaffold(backgroundColor = Color.Transparent) {
        if (!isPermission) {
            CameraPermissionHandler {
                if (it) {
                    isPermission = true
                } else {
                    onClickNegativeAction()
                }
            }
        } else {
            CameraView(
                rotate = rotate,
                onImageCaptured = { onClickPositiveAction(it, rotate) },
                onError = {},
                onBack = { onClickNegativeAction() }
            )
        }
    }
}

/**
 * カメラパーミッション
 *
 * @param onGranted 判定結果
 */
@Composable
private fun CameraPermissionHandler(onGranted: (Boolean) -> Unit) {
    val permission = Permissions.CAMERA
    val customPermissionDialog = @Composable{
        CustomCameraDialog(
            onClickPositiveAction = { onGranted(false) },
            onClickNegativeAction = { onGranted(false) }
        )
    }
    PermissionHandler(
        onGranted = onGranted,
        permission = permission,
        CustomPermissionDialog = customPermissionDialog
    )
}

/**
 * カメラ撮影画面
 *
 * @param onImageCaptured 画像キャプチャ
 * @param onError エラー処理
 */
@Composable
fun CameraView(
    rotate: Rotate,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val enableShutterButton = remember { mutableStateOf(true) }
    val context = LocalContext.current
    val imageCapture: ImageCapture = remember {
        ImageCapture.Builder().build()
    }
    val errorAction: (ImageCaptureException) -> Unit = {
        enableShutterButton.value = true
        onError(it)
    }
    val imageCaptureAction: (Uri) -> Unit = {
        onImageCaptured(it)
    }

    CameraPreviewView(
        rotate = rotate,
        imageCapture = imageCapture,
        cameraUIAction = { imageCapture.takePicture(rotate, context, imageCaptureAction, errorAction) },
        enableShutterButton = enableShutterButton
    )
}

/**
 * カメラプレビュー
 *
 * @param imageCapture 画像
 * @param cameraUIAction 画像キャプチャ処理
 * @param enableShutterButton シャッターボタンの活性・非活性制御
 */
@SuppressLint("RestrictedApi")
@Composable
private fun CameraPreviewView(
    rotate: Rotate,
    imageCapture: ImageCapture,
    cameraUIAction: () -> Unit,
    enableShutterButton: MutableState<Boolean>
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val preview = Preview.Builder().build()
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
        .build()

    val previewView = remember { PreviewView(context) }
    LaunchedEffect(CameraSelector.LENS_FACING_BACK) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.Bottom
        ) {
            CameraBottomBar(rotate, cameraUIAction, enableShutterButton)
        }
    }
}

suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { cameraProvider ->
        cameraProvider.addListener({
            continuation.resume(cameraProvider.get())
        }, ContextCompat.getMainExecutor(this))
    }
}


/**
 * カメラBottomBarのコンテンツ
 *
 * @param cameraUIAction クリックアクション
 */
@Composable
private fun CameraBottomBar(
    rotate: Rotate,
    cameraUIAction: () -> Unit,
    enableShutterButton: MutableState<Boolean>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(16.dp),
    ) {
        CameraBottomItem(
            Icons.Sharp.Add,
            enabled = enableShutterButton.value,
            modifier= Modifier
                .size(64.dp)
                .padding(1.dp)
                .border(1.dp, Color.White, CircleShape)
                .align(Alignment.TopCenter),
            onClick = {
                enableShutterButton.value = false
                cameraUIAction()
            }
        )
        Text(
            text = rotate.label,
            color = Color.White,
            fontSize = 24.sp,
            modifier= Modifier.align(Alignment.CenterEnd)
        )
    }
}

/**
 * アイコンボタン
 *
 * @param onClick クリックアクション
 * @param imageVector アイコン画像
 * @param modifier レイアウト
 */
@Composable
private fun CameraBottomItem(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
    ) {
        Icon(
            imageVector,
            contentDescription = null,
            modifier = modifier,
            tint = Color.White
        )
    }
}


/**
 * ImageCaptureユースケースの拡張関数
 */
private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
private const val PHOTO_EXTENSION = ".jpg"


/**
 * 写真保存機能
 *
 * @param context コンテキスト
 * @param onImageCaptured 撮影成功アクション
 * @param onError 撮影失敗アクション
 */
private fun ImageCapture.takePicture(
    rotate: Rotate,
    context: Context,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    // 画像ファイル生成
    val outputDirectory = context.filesDir
    val fileName = SimpleDateFormat(FILENAME, Locale.US).format(System.currentTimeMillis()) + PHOTO_EXTENSION
    val photoFile = File(outputDirectory, fileName)
    val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    // シャッター音
    val sound = MediaActionSound()
    sound.load(MediaActionSound.SHUTTER_CLICK)

    // 画像ファイル保存
    this.takePicture(
        outputFileOptions,
        Executors.newSingleThreadExecutor(),
        object : ImageCapture.OnImageSavedCallback {
            // 成功
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
                sound.play(MediaActionSound.SHUTTER_CLICK)
                fixPicture(context, savedUri, rotate)
                Log.d("画像保存成功", "saveUri = " + savedUri)
                onImageCaptured(savedUri)
            }
            // 失敗
            override fun onError(exception: ImageCaptureException) {
                Log.d("画像保存失敗", "exception = " + exception)
                onError(exception)
            }
        })
}

/**
 * 撮影した写真をExifと端末の回転を考慮して、
 * 写真が撮影した時の向きになるように保存
 *
 * @param context Context
 * @param savedUri 修正する画像のURI
 * @param rotate 端末の傾き
 */
private fun fixPicture(context: Context, savedUri: Uri, rotate: Rotate) {

    val prefix = "file://"
    val folder = context.filesDir.toString()
    val filePath = "$prefix$folder/"
    val file = if (Regex(filePath).containsMatchIn(savedUri.toString())) {
        savedUri.toString().removePrefix(filePath)
    } else {
        savedUri.toString()
    }
    try {
        val exifInterface = ExifInterface(savedUri.path!!)
        // 回転設定
        val angle = when(exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        } + rotate.angle
        // 画像修正
        BufferedInputStream(context.openFileInput(file)).use { bufferedInputStream ->
            val bitmap1 = BitmapFactory.decodeStream(bufferedInputStream)
            val imageWidth = bitmap1.width
            val imageHeight = bitmap1.height
            val matrix = Matrix()
            matrix.setRotate(angle, (imageWidth / 2).toFloat(), (imageHeight / 2).toFloat())
            val bitmap2 = Bitmap.createBitmap(bitmap1, 0, 0, imageWidth, imageHeight, matrix, true)
            val outputStream = ByteArrayOutputStream()
            bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            val byteArray = outputStream.toByteArray()
            File(savedUri.path!!).writeBytes(byteArray)
        }
    } catch (exception: Exception) {
        throw exception
    }
}

/**
 * １度拒否された後の再設定用ダイアログ
 *
 * @param onClickPositiveAction ポジティブアクション
 * @param onClickNegativeAction ネガティブアクション
 */
@Composable
fun CustomCameraDialog(
    onClickPositiveAction: () -> Unit,
    onClickNegativeAction: () -> Unit
) {
    val context = LocalContext.current
    Dialog(onDismissRequest = {}) {
        Surface() {
            Row(modifier = Modifier.padding(15.dp)) {
                Button(onClick = { onClickNegativeAction() }) {
                    Text(text = "いいえ")
                }
                Button(
                    onClick = {
                        openApplicationDetailsSettings(context)
                        onClickPositiveAction()
                    },
                    modifier = Modifier.padding(start = 15.dp)
                ) {
                    Text(text = "はい")
                }
            }
        }
    }
}

/**
 * アプリの権限設定画面へ遷移
 *
 * @param context コンテキスト
 */
private fun openApplicationDetailsSettings(context: Context) {
    val intent = Intent().also {
        it.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        it.data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}