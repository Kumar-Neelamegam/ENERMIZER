package com.peter.enermizer.views.reports

import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.github.chrisbanes.photoview.PhotoView
import com.github.chrisbanes.photoview.PhotoViewAttacher
import com.peter.enermizer.R

class CustomPopupDialogFragment(private val imageBitmap: Bitmap) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.popup_image_view, container, false)

        val imageView2 = view.findViewById<ZoomClass>(R.id.zoomableImageView2)
        val dismissButton = view.findViewById<Button>(R.id.dismissButton)
        val imageBitmapRotated = rotateBitmap(imageBitmap)

        // Resize the Bitmap to fit the screen while preserving the aspect ratio
        val screenWidth = resources.displayMetrics.widthPixels
        val screenHeight = resources.displayMetrics.heightPixels
        val aspectRatio = imageBitmapRotated.width.toFloat() / imageBitmapRotated.height

        val targetWidth = if (aspectRatio > 1) screenWidth else (screenHeight * aspectRatio).toInt()
        val targetHeight = if (aspectRatio > 1) (screenWidth / aspectRatio).toInt() else screenHeight

        val scaledBitmap = Bitmap.createScaledBitmap(imageBitmapRotated, targetWidth, targetHeight, true)

        imageView2.setImageBitmap(scaledBitmap)

        // Adjust the PhotoView layout parameters to match the screen dimensions
        val layoutParams = imageView2.layoutParams
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        imageView2.layoutParams = layoutParams

        dismissButton.setOnClickListener {
            dismiss()
        }

        return view
    }

    fun rotateBitmap(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(90f) // Rotate by 90 degrees

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }

}
