package com.example.quizappbycouchbase

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
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
    private lateinit var  databaseManager: DatabaseManager
    private lateinit var category : String
    private lateinit var button: Button
    private lateinit var mUsername: String

    val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:5000")
   //    .baseUrl("http://127.0.0.1:5000")
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
        databaseManager = DatabaseManager(this)
        mUsername = intent.getStringExtra(Constants.USER_NAME).toString()
        imageView = findViewById(R.id.image_view)
        val selectImageButton: Button = findViewById(R.id.upload_image)
        button = findViewById(R.id.button_startquiz)
        selectImageButton.setOnClickListener {
            openGallery()
        }
        button.setOnClickListener{
            if (imageView.drawable == null) {
                // Display a toast message
                Toast.makeText(applicationContext, "No image uploaded", Toast.LENGTH_SHORT).show()
            }else{

            val intent = Intent(this,QuizQuestionActivity::class.java)
            intent.putExtra(Constants.USER_NAME,mUsername)
            intent.putExtra(Constants.CATEGORY,category)
            startActivity(intent)
            finish()}
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
            apiService.getImageEmbedding(body).enqueue(object : Callback<EmbeddingResponse> {
                override fun onResponse(call: Call<EmbeddingResponse>, response: Response<EmbeddingResponse>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        val embeddingData = responseBody?.embedding
                        Log.i("RESP", embeddingData?.toString() ?: "Empty response body")
                        // Handle the response body here
                        val resp = databaseManager.search(embeddingData!!)
                        Log.i("CATEGORY1", resp.toString())
                        category = findMostFrequentCategory(resp)!!
                        Log.i("CATEGORY", category.toString())

                    } else {
                        // Handle unsuccessful response
                        Log.i("RESP", "FAIL: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<EmbeddingResponse>, t: Throwable) {
                    // Handle failure
                    Log.e("RESP", "Upload failed", t)
                }
            })
        }
    }

    fun findMostFrequentCategory(imageObjects: List<ImageObject>): String? {
        // Create a map to store the count of each category
        val categoryCountMap = mutableMapOf<String, Int>()

        // Iterate over the list of ImageObject and count the occurrences of each category
        for (imageObject in imageObjects) {
            val category = imageObject.category
            val count = categoryCountMap.getOrDefault(category, 0)
            categoryCountMap[category] = count + 1
        }

        // Find the category with the highest count
        var maxCategory: String? = null
        var maxCount = 0
        for ((category, count) in categoryCountMap) {
            if (count > maxCount) {
                maxCategory = category
                maxCount = count
            }
        }

        return maxCategory
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