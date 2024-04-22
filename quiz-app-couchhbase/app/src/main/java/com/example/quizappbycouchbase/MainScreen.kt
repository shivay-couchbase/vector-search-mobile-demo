package com.example.quizappbycouchbase

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import android.Manifest.permission
import android.Manifest.permission.READ_MEDIA_IMAGES
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.couchbase.lite.CouchbaseLiteException
import com.couchbase.lite.DataSource
import com.couchbase.lite.Expression
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.SelectResult


@Suppress("DEPRECATION")
class MainScreen : AppCompatActivity() {
    private lateinit var  databaseManager: DatabaseManager
    private val INTERNET_PERMISSION_REQUEST_CODE = 1001
    private val STORAGE_PERMISSION_REQUEST_CODE = 101


    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)

        } else {
            Toast.makeText(this, "Permission already granted " + permission, Toast.LENGTH_LONG).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseManager = DatabaseManager(this)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_screen)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val button_start = findViewById<Button>(R.id.button_start)
        val et_name = findViewById<AppCompatEditText>(R.id.et_name)
        val button_score = findViewById<Button>(R.id.button_score)
        button_score.setOnClickListener{
            val intent = Intent(this,ScoreActivity::class.java)
            startActivity(intent)
        }
//        if (ContextCompat.checkSelfPermission(
//                this,
//                permission.INTERNET
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(permission.INTERNET),
//                INTERNET_PERMISSION_REQUEST_CODE
//            )
//        }

        // Request external storage permission
        if (ContextCompat.checkSelfPermission(
                this,
                READ_MEDIA_IMAGES
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(READ_MEDIA_IMAGES),
                STORAGE_PERMISSION_REQUEST_CODE
            )
        }


//        val doubles: List<Double> = listOf(0.0, 0.0, 0.8490194082260132)
//
//// Start a new coroutine in the main thread
//        GlobalScope.launch(Dispatchers.Main) {
//            // Execute the database search operation in the background thread
//            val result = withContext(Dispatchers.IO) {
//                databaseManager.search(doubles)
//            }
//
//            // Handle the search result on the main thread
//            Log.i("SEARCHRESULTSUSINGVECTORSEARCH", result.toString())
//        }

        button_start.setOnClickListener {
            val userName = et_name.text.toString()
            if(userName.isEmpty()){
                Toast.makeText(this,"Please Enter Your Username",Toast.LENGTH_SHORT).show()
            }else
            {
                    val intent = Intent(this,QuizQuestionActivity::class.java)
                    val user = User(userName)
                    databaseManager.insertUser(user)
                    intent.putExtra(Constants.USER_NAME,et_name.text.toString())
                    startActivity(intent)
                    finish()

            }
        }
//        checkPermission(permission.INTERNET,
//            INTERNET_PERMISSION_REQUEST_CODE)
        checkPermission(permission.READ_MEDIA_IMAGES,
            STORAGE_PERMISSION_REQUEST_CODE)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            INTERNET_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Internet permission granted
                } else {
                    // Internet permission denied
                }
            }
            STORAGE_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Storage permission granted
                } else {
                    // Storage permission denied
                }
            }
        }
    }
    private fun isUserNameUnique(userName: String): Boolean {
        // Query the user database to check if the username already exists
        Log.i("Username", "1")
        databaseManager.initializeDatabases()
        val userCollection = databaseManager.collection

        if (userCollection == null) {
            // Handle null database instance appropriately
            Log.e("Username", "User database instance is null")
            return false
        }
        Log.i("Username", "2")
        try {
            val query = QueryBuilder
                .select(SelectResult.all())
                .from(DataSource.collection(databaseManager.collection!!)) // Assuming you have already assigned a value to the 'collection' variable
                .where(Expression.property("username").equalTo(Expression.string(userName)))

            val resultSet = query.execute()
            Log.i("Username", resultSet.toString())
            val resultCount = resultSet.allResults().size
            Log.i("Username", resultCount.toString())
            return resultCount == 0
        }
        catch (e: CouchbaseLiteException) {
            // Handle any exceptions
            Log.e("Username", "Error executing query: ${e.message}")
            return false
        }
    }
}