package com.example.scanime.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.os.SystemClock
import android.util.Log
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facedetector.FaceDetector
import com.google.mediapipe.tasks.vision.facedetector.FaceDetectorResult
import java.io.ByteArrayOutputStream

class FaceDetectorHelper(
    private val context: Context,
    private val threshold: Float = THRESHOLD_DEFAULT,
    private val delegate: Int = DELEGATE_CPU,
    private val faceDetectorListener: DetectorListener
) {

    private lateinit var faceDetector: FaceDetector

    init {
        setupFaceDetector()
    }

    private fun setupFaceDetector() {
        val baseOptionsBuilder = BaseOptions.builder()

        when (delegate) {
            DELEGATE_CPU -> baseOptionsBuilder.setDelegate(Delegate.CPU)
            DELEGATE_GPU -> baseOptionsBuilder.setDelegate(Delegate.GPU)
        }

        baseOptionsBuilder.setModelAssetPath("blaze_face_short_range.tflite")

        try {
            val options = FaceDetector.FaceDetectorOptions.builder()
                .setBaseOptions(baseOptionsBuilder.build())
                .setMinDetectionConfidence(threshold)
                .setRunningMode(RunningMode.LIVE_STREAM)
                .setResultListener(this::returnLivestreamResult)
                .setErrorListener(this::returnLivestreamError)
                .build()

            faceDetector = FaceDetector.createFromOptions(context, options)

        } catch (e: Exception) {
            faceDetectorListener.onError(
                "Face detector failed to initialize: ${e.message}",
                GPU_ERROR
            )
            Log.e(TAG, "Error initializing face detector: ${e.message}")
        }
    }

    fun detectLivestreamFrame(imageProxy: ImageProxy) {
        val frameTime = SystemClock.uptimeMillis()

        val bitmapBuffer = imageProxyToBitmap(imageProxy)
        imageProxy.close() // Important!

        val matrix = Matrix().apply {
            postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
            postScale(-1f, 1f, imageProxy.width.toFloat(), imageProxy.height.toFloat())
        }

        val rotatedBitmap = Bitmap.createBitmap(
            bitmapBuffer,
            0,
            0,
            bitmapBuffer.width,
            bitmapBuffer.height,
            matrix,
            true
        )

        val mpImage = BitmapImageBuilder(rotatedBitmap).build()
        faceDetector.detectAsync(mpImage, frameTime)
    }

    private fun returnLivestreamResult(
        result: FaceDetectorResult,
        input: MPImage
    ) {
        val inferenceTime = SystemClock.uptimeMillis() - result.timestampMs()
        faceDetectorListener.onResults(
            ResultBundle(listOf(result), inferenceTime, input.height, input.width)
        )
    }

    private fun returnLivestreamError(error: RuntimeException) {
        faceDetectorListener.onError(error.message ?: "Unknown error")
    }

    fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val vBuffer = image.planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, image.width, image.height), 100, out)
        val yuv = out.toByteArray()
        return BitmapFactory.decodeByteArray(yuv, 0, yuv.size)
    }


    data class ResultBundle(
        val results: List<FaceDetectorResult>,
        val inferenceTime: Long,
        val inputImageHeight: Int,
        val inputImageWidth: Int,
    )

    interface DetectorListener {
        fun onError(error: String, errorCode: Int = OTHER_ERROR)
        fun onResults(resultBundle: ResultBundle)
    }

    companion object {
        const val DELEGATE_CPU = 0
        const val DELEGATE_GPU = 1
        const val THRESHOLD_DEFAULT = 0.5F
        const val OTHER_ERROR = 0
        const val GPU_ERROR = 1
        const val TAG = "FaceDetectorHelper"
    }
}
