package com.example.quizappbycouchbase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class ResultActivity : AppCompatActivity() {
    private lateinit var  databaseManager: DatabaseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_result)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ResultActivity)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        databaseManager = DatabaseManager(this)

        var tvname: TextView = findViewById<TextView>(R.id.tv_name)
        var tvscore: TextView = findViewById<TextView>(R.id.tv_score)
        var btn_finish: Button = findViewById<Button>(R.id.btn_finish)
        var username = intent.getStringExtra(Constants.USER_NAME).toString()
        var category = intent.getStringExtra(Constants.CATEGORY).toString()
        tvname.text = username

        val totalQuestions = intent.getStringExtra(Constants.TOTAL_QUESTIONS).toString()
        val correctAnswers = intent.getStringExtra(Constants.CORRECT_ANSWERS).toString()
        tvscore.text = "Your Score is $correctAnswers out of $totalQuestions"
        databaseManager.insertUserScore(Scores(username,correctAnswers.toInt(),category))
        btn_finish.setOnClickListener{
            startActivity(Intent(this,MainScreen::class.java))
        }

    }
}