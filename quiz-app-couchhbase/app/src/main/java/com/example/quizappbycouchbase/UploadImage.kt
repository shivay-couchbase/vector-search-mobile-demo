package com.example.quizappbycouchbase

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.documentfile.provider.DocumentFile
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

@Suppress("DEPRECATION")
class UploadImage : AppCompatActivity() {
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var imageView: ImageView
    val retrofit = Retrofit.Builder()
//        .baseUrl("http://10.0.2.2:5000")
        .baseUrl("http://127.0.0.1:5000")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_upload_image)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imageView = findViewById(R.id.image_view)
        val selectImageButton: Button = findViewById(R.id.upload_image)

        selectImageButton.setOnClickListener {
            openGallery()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            imageView.setImageURI(imageUri)
            uploadImage(imageUri)
        }
    }


    private fun uploadImage(imageUri: Uri?) {
        // Implement image upload logic here using Retrofit or Volley
        // Make sure to handle server response in the callback
        imageUri?.let {
            val imageFile = File(getRealPathFromURI(imageUri))
            val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

            // Make the request
            apiService.getImageEmbedding(body).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        Log.i("RESP", responseBody?.string() ?: "Empty response body")
                        // Handle the response body here
                    } else {
                        // Handle unsuccessful response
                        Log.i("RESP", "FAIL: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    // Handle failure
                    Log.e("RESP", "Upload failed", t)
                }
            })
        }
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        var filePath: String? = null
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, filePathColumn, null, null, null)
//        cursor?.use { cursor ->
//            if (cursor.moveToFirst()) {
//                val columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
//                filePath = if (columnIndex != -1) {
//                    cursor.getString(columnIndex)
//                } else {
//                    // The column index is not valid, try other methods to get the file path
//                    // For example, for cloud providers or non-standard URIs
//                    null
//                }
//            }
//        }
        cursor?.moveToFirst()
        val columnIndex: Int = cursor?.getColumnIndex(filePathColumn[0]) ?: -1
        val imgDecodableString: String? = if (columnIndex != -1) cursor?.getString(columnIndex) else null
        cursor?.close()
        Log.i("URI",imgDecodableString!!)
        return imgDecodableString
    }



}