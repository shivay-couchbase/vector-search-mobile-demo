package com.example.quizappbycouchbase

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ScoreActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_score)
        val spinnerCategory: Spinner = findViewById(R.id.spinnerCategory)
        val categories = arrayOf("Category 1", "Category 2", "Category 3")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinnerCategory.adapter = adapter

        val linearLayoutScores: LinearLayout = findViewById(R.id.linearLayoutScores)

        // Sample data for usernames and scores
        val users = arrayOf("User 1", "User 2", "User 3","User 4","User 5","User 6","User 7","User 8","User 9")
        val scores = arrayOf(100, 90, 80,100, 90, 80,100, 90, 80)

        // Populate LinearLayout with usernames and scores as separate columns
        for (i in users.indices) {
            val linearLayoutRow = LinearLayout(this)
            linearLayoutRow.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            linearLayoutRow.orientation = LinearLayout.HORIZONTAL

            val textViewUsername = TextView(this)
            textViewUsername.text = users[i]
            textViewUsername.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
//            textViewUsername.setBackgroundResource(R.drawable.border_background) // Apply border background
            textViewUsername.setTextColor(ContextCompat.getColor(this, R.color.black)) // Apply text color
            textViewUsername.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f) // Apply text size
            textViewUsername.setPadding(8.dpToPx(), 8.dpToPx(), 8.dpToPx(), 8.dpToPx()) // Apply padding
            val textViewScore = TextView(this)
            textViewScore.text = scores[i].toString()
            textViewScore.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )

            linearLayoutRow.addView(textViewUsername)
            linearLayoutRow.addView(textViewScore)
            linearLayoutScores.addView(linearLayoutRow)
            linearLayoutRow.orientation = LinearLayout.HORIZONTAL
            linearLayoutRow.setBackgroundResource(R.drawable.border_background) // Apply border background
            linearLayoutRow.setPadding(4.dpToPx(), 4.dpToPx(), 4.dpToPx(), 4.dpToPx()) // Apply padding

        }
        var homeScreenBtn = findViewById<Button>(R.id.btn_homescreen)
        homeScreenBtn.setOnClickListener{
            val intent = Intent(this, MainScreen::class.java)
            startActivity(intent)
        }
}
    fun Int.dpToPx(): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            Resources.getSystem().displayMetrics
        ).toInt()
    }
}