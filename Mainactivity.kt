package com.example.imageresizer

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var widthInput: EditText
    private lateinit var heightInput: EditText
    private var selectedImageBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        widthInput = findViewById(R.id.widthInput)
        heightInput = findViewById(R.id.heightInput)
        val selectImageButton = findViewById<Button>(R.id.selectImageButton)
        val resizeButton = findViewById<Button>(R.id.resizeButton)
        val saveButton = findViewById<Button>(R.id.saveButton)

        // Select Image
        selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 100)
        }

        // Resize Image
        resizeButton.setOnClickListener {
            val width = widthInput.text.toString().toIntOrNull()
            val height = heightInput.text.toString().toIntOrNull()

            if (selectedImageBitmap != null && width != null && height != null) {
                selectedImageBitmap = Bitmap.createScaledBitmap(selectedImageBitmap!!, width, height, true)
                imageView.setImageBitmap(selectedImageBitmap)
                Toast.makeText(this, "Image resized!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please select an image and enter valid dimensions", Toast.LENGTH_SHORT).show()
            }
        }

        // Save Image
        saveButton.setOnClickListener {
            if (selectedImageBitmap != null) {
                saveImageToFile(selectedImageBitmap!!)
                Toast.makeText(this, "Image saved successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No image to save!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            val imageUri: Uri? = data?.data
            val inputStream = contentResolver.openInputStream(imageUri!!)
            selectedImageBitmap = BitmapFactory.decodeStream(inputStream)
            imageView.setImageBitmap(selectedImageBitmap)
        }
    }

    private fun saveImageToFile(bitmap: Bitmap) {
        val directory = File(getExternalFilesDir(null), "ResizedImages")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val file = File(directory, "resized_image_${System.currentTimeMillis()}.png")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
    }
}
