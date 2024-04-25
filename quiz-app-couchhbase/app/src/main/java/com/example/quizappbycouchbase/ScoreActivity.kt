package com.example.quizappbycouchbase

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class ScoreActivity : AppCompatActivity() {
    private lateinit var databaseManager: DatabaseManager
    private var mScoreList: ArrayList<Scores>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_score)
        databaseManager = DatabaseManager(this)
        val spinnerCategory: Spinner = findViewById(R.id.spinnerCategory)
        mScoreList = databaseManager.getAllScores()
        val categories = getUniqueCategoriesFromScores()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinnerCategory.adapter = adapter

        val linearLayoutScores: LinearLayout = findViewById(R.id.linearLayoutScores)

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedCategory = categories[position]
                displayScoresForCategory(selectedCategory)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        // Display scores for the initially selected category
        val initialCategory = categories.firstOrNull()
        initialCategory?.let { displayScoresForCategory(it) }

        var homeScreenBtn = findViewById<Button>(R.id.btn_homescreen)
        homeScreenBtn.setOnClickListener {
            val intent = Intent(this, MainScreen::class.java)
            startActivity(intent)
        }
    }

    private fun displayScoresForCategory(category: String) {
        val linearLayoutScores: LinearLayout = findViewById(R.id.linearLayoutScores)
        linearLayoutScores.removeAllViews()

        // Filter scores for the selected category
        val scoresForCategory = mScoreList?.filter { it.category == category }

        // Keep only the highest score for each username
        val highestScores = scoresForCategory?.groupBy { it.username }?.mapValues { (_, scores) ->
            scores.maxByOrNull { it.score }
        }?.values

        highestScores?.forEach { score ->
            val linearLayoutRow = LinearLayout(this)
            linearLayoutRow.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            linearLayoutRow.orientation = LinearLayout.HORIZONTAL

            val textViewUsername = TextView(this)
            textViewUsername.text = score?.username
            textViewUsername.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            textViewUsername.setTextColor(ContextCompat.getColor(this, R.color.black))
            textViewUsername.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            textViewUsername.setPadding(8.dpToPx(), 8.dpToPx(), 8.dpToPx(), 8.dpToPx())

            val textViewScore = TextView(this)
            textViewScore.text = score?.score.toString()
            textViewScore.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )

            linearLayoutRow.addView(textViewUsername)
            linearLayoutRow.addView(textViewScore)
            linearLayoutScores.addView(linearLayoutRow)
            linearLayoutRow.orientation = LinearLayout.HORIZONTAL
            linearLayoutRow.setBackgroundResource(R.drawable.border_background)
            linearLayoutRow.setPadding(4.dpToPx(), 4.dpToPx(), 4.dpToPx(), 4.dpToPx())
        }
    }


    fun getUniqueCategoriesFromScores(): ArrayList<String> {
        val uniqueCategories = HashSet<String>()
        mScoreList?.forEach { score ->
            uniqueCategories.add(score.category)
        }
        Log.i("uniquecategory",ArrayList(uniqueCategories).toString())
        return ArrayList(uniqueCategories)
    }
    fun Int.dpToPx(): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            Resources.getSystem().displayMetrics
        ).toInt()
    }

}

