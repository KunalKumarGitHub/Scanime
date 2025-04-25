package com.example.scanime.ui.feature.face_detect

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.scanime.utils.FaceDetectorHelper
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import com.google.mediapipe.tasks.vision.facedetector.FaceDetectorResult

@Composable
fun FaceDetectorScreen() {
    val context = LocalContext.current

    var permissionRequested by remember { mutableStateOf(false) }
    var permissionGranted by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGranted = isGranted
        permissionRequested = true

        if (!isGranted) {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            permissionGranted = true
            permissionRequested = true
        }
    }

    if (permissionGranted) {
        FaceDetectorCameraUI()
    } else if (permissionRequested) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }) {
                Text("Retry Permission")
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}


@Composable
fun FaceDetectorCameraUI() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var startCamera by remember { mutableStateOf(false) }

    val faceResults = remember { mutableStateOf<List<FaceDetectorResult>>(emptyList()) }
    val imageWidth = remember { mutableIntStateOf(1) }
    val imageHeight = remember { mutableIntStateOf(1) }

    val detectorListener = remember {
        object : FaceDetectorHelper.DetectorListener {
            override fun onError(error: String, errorCode: Int) {
                Log.e("FaceDetectScreen", "Error: $error")
            }

            override fun onResults(resultBundle: FaceDetectorHelper.ResultBundle) {
                faceResults.value = resultBundle.results
                imageWidth.intValue = resultBundle.inputImageWidth
                imageHeight.intValue = resultBundle.inputImageHeight
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!startCamera) {
            Button(onClick = { startCamera = true }) {
                Text("Start Face Detection")
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        val previewView = PreviewView(ctx).apply {
                            scaleType = PreviewView.ScaleType.FILL_CENTER
                        }

                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }

                            val imageAnalyzer = ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()

                            val faceDetectorHelper = FaceDetectorHelper(
                                context = ctx,
                                delegate = FaceDetectorHelper.DELEGATE_CPU,
                                faceDetectorListener = detectorListener
                            )

                            imageAnalyzer.setAnalyzer(
                                ContextCompat.getMainExecutor(ctx)
                            ) { imageProxy ->
                                faceDetectorHelper.detectLivestreamFrame(imageProxy)
                            }

                            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    cameraSelector,
                                    preview,
                                    imageAnalyzer
                                )
                            } catch (exc: Exception) {
                                Log.e("FaceDetectorScreen", "Camera binding failed", exc)
                            }

                        }, ContextCompat.getMainExecutor(ctx))

                        previewView
                    }
                )

                FaceDetectionOverlay(
                    faceResults = faceResults.value,
                    imageWidth = imageWidth.intValue,
                    imageHeight = imageHeight.intValue,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun FaceDetectionOverlay(
    faceResults: List<FaceDetectorResult>,
    imageWidth: Int,
    imageHeight: Int,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val scaleX = size.width / imageWidth
        val scaleY = size.height / imageHeight

        val refBoxWidth = size.width * 0.5f
        val refBoxHeight = size.height * 0.5f
        val refBoxLeft = (size.width - refBoxWidth) / 2f
        val refBoxTop = (size.height - refBoxHeight) / 2f
        val referenceRect = Rect(
            offset = Offset(refBoxLeft, refBoxTop),
            size = Size(refBoxWidth, refBoxHeight)
        )

        var allFacesInside = true
        var totalDetectedFaces = 0

        faceResults.forEach { result ->
            result.detections().forEach { detection ->
                totalDetectedFaces++
                val box = detection.boundingBox()
                val left = box.left * scaleX
                val top = box.top * scaleY
                val right = box.right * scaleX
                val bottom = box.bottom * scaleY
                val adjustedFaceRect = Rect(left, top, right, bottom)

                if (
                    adjustedFaceRect.left < referenceRect.left ||
                    adjustedFaceRect.top < referenceRect.top ||
                    adjustedFaceRect.right > referenceRect.right ||
                    adjustedFaceRect.bottom > referenceRect.bottom
                ) {
                    allFacesInside = false
                }
            }
        }

        val isBoxGreen = totalDetectedFaces > 0 && allFacesInside

        drawRect(
            color = if (isBoxGreen) Color.Green else Color.Red,
            topLeft = referenceRect.topLeft,
            size = referenceRect.size,
            style = Stroke(width = 4f)
        )
    }
}

