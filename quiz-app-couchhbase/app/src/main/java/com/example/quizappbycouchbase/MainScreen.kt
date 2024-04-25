package com.example.quizappbycouchbase

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import android.Manifest.permission
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.os.Build
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Suppress("DEPRECATION")
class MainScreen : AppCompatActivity() {
    private lateinit var  databaseManager: DatabaseManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_screen)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermessionLauncher.launch(READ_MEDIA_IMAGES)
        } else {
            requestPermessionLauncher.launch(READ_EXTERNAL_STORAGE)
        }

        val button_start = findViewById<Button>(R.id.button_start)
        val et_name = findViewById<AppCompatEditText>(R.id.et_name)
        val button_score = findViewById<Button>(R.id.button_score)
//        val applicationContext = this

        button_score.setOnClickListener{
            val intent = Intent(this,ScoreActivity::class.java)
            startActivity(intent)
        }
        databaseManager = DatabaseManager(this)


        button_start.setOnClickListener {
            val userName = et_name.text.toString()
            if(userName.isEmpty()){
                Toast.makeText(this,"Please Enter Your Username",Toast.LENGTH_SHORT).show()
            }else
            {
                val user = User(userName)
                databaseManager.insertUser(user)
                val intent = Intent(this,UploadImage::class.java)

//                    val double = listOf(0.0,0.32411807775497437,0.0)
//                CoroutineScope(Dispatchers.Main).launch {
//                // Call the database function asynchronously
//                val result = withContext(Dispatchers.IO) {
//                    databaseManager.search(double)
//                }
//                    Log.i("SEARCHUSINGVECTORSEARCH",result.toString())
//                }
                intent.putExtra(Constants.USER_NAME,et_name.text.toString())
                Log.i("username",et_name.text.toString())
                startActivity(intent)
                finish()

            }
        }

    }
    val requestPermessionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        // Check if the permission is granted
        if (isGranted) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show()
        } else {
            // Show a toast message asking the user to grant the permission
            Toast.makeText(this, "Please grant permission", Toast.LENGTH_LONG).show()
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