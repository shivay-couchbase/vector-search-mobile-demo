package com.example.quizappbycouchbase

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class QuizQuestionActivity : AppCompatActivity(), View.OnClickListener {

    private var mCurrentPosition:Int = 1
    private var mQuestionsList:ArrayList<Questions>? =null
    private var mSelectedOptionPosition:Int = 0
    private var isSelectedAnsOnce:Boolean = false
    private var mCorrectAnswerCount:Int = 0
    private lateinit var progressBar: ProgressBar
    private lateinit var tvprogressbar: TextView
    private lateinit var tvquestion: TextView
    private lateinit var optionOne: TextView
    private lateinit var optionTwo: TextView
    private lateinit var optionThree: TextView
    private lateinit var optionFour: TextView
    private lateinit var btn_submit: Button
    private lateinit var mUsername: String
    private lateinit var mCategory: String
    private lateinit var  databaseManager: DatabaseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_quiz_question)
        mUsername = intent.getStringExtra(Constants.USER_NAME).toString()
        mCategory = intent.getStringExtra(Constants.CATEGORY).toString()
        Log.i("USERNAME",mUsername)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        databaseManager = DatabaseManager(this)
        progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        tvprogressbar = findViewById<TextView>(R.id.tv_progress)
        tvquestion = findViewById<TextView>(R.id.tv_question)
        optionOne = findViewById<TextView>(R.id.tv_option1)
        optionTwo = findViewById<TextView>(R.id.tv_option2)
        optionThree = findViewById<TextView>(R.id.tv_option3)
        optionFour = findViewById<TextView>(R.id.tv_option4)
        btn_submit = findViewById<Button>(R.id.btn_submit)
//        mQuestionsList = databaseManager.getQuestions()
//        for (question in mQuestionsList!!) {
//            databaseManager.insertQuestion(question)
//        }
        mQuestionsList = databaseManager.getQuestions()
        mQuestionsList = mQuestionsList?.filter { it.category == mCategory } as ArrayList<Questions>?
        progressBar.max = mQuestionsList!!.size
        setQuestion()
        optionOne.setOnClickListener(this)
        optionTwo.setOnClickListener(this)
        optionThree.setOnClickListener(this)
        optionFour.setOnClickListener(this)
        btn_submit.setOnClickListener(this)


    }

    private fun setQuestion(){
        isSelectedAnsOnce =false
        val question = mQuestionsList!!.get(mCurrentPosition-1)
        defaultOptionsView()
        if(mCurrentPosition == mQuestionsList!!.size){
            btn_submit.text = "FINISH"
        }else{
            btn_submit.text = "SUBMIT"
        }
        progressBar.progress = mCurrentPosition
        tvprogressbar.text = "$mCurrentPosition"+"/"+mQuestionsList!!.size
        tvquestion.text = question.question
        optionOne.text = question.optionOne
        optionTwo.text = question.optionTwo
        optionThree.text = question.optionThree
        optionFour.text = question.optionFour

    }

    private fun defaultOptionsView(){
        val options = ArrayList<TextView>()
        options.add(0,optionOne)
        options.add(1,optionTwo)
        options.add(2,optionThree)
        options.add(3,optionFour)
        for (option in options){
            option.setTextColor(Color.parseColor("#7A8089"))
            option.typeface = Typeface.DEFAULT
            option.background = ContextCompat.getDrawable(this@QuizQuestionActivity,R.drawable.default_option_border_bg)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_option1 -> {
                selectedOptionView(optionOne, 1)
            }
            R.id.tv_option2 -> {
                selectedOptionView(optionTwo, 2)
            }
            R.id.tv_option3 -> {
                selectedOptionView(optionThree, 3)
            }
            R.id.tv_option4 -> {
                selectedOptionView(optionFour, 4)
            }
            R.id.btn_submit -> {
                if (mSelectedOptionPosition==0){
                    mCurrentPosition++

                    when{
                        mCurrentPosition <= mQuestionsList!!.size -> {
                            setQuestion()
                        } else -> {
                            val intent = Intent(this,ResultActivity::class.java)
                            intent.putExtra(Constants.USER_NAME,mUsername)
                            intent.putExtra(Constants.CATEGORY,mCategory)
                            intent.putExtra(Constants.TOTAL_QUESTIONS,mQuestionsList!!.size.toString())
                            intent.putExtra(Constants.CORRECT_ANSWERS,mCorrectAnswerCount.toString())
                            startActivity(intent)
                            finish()
                        }
                    }
                }else{
                        val question = mQuestionsList?.get(mCurrentPosition-1)
                        if (question!!.correctAns != mSelectedOptionPosition){
                            answerView(mSelectedOptionPosition,R.drawable.wrong_option_border_bg)
                        }else{
                            mCorrectAnswerCount++
                        }
                        answerView(question.correctAns,R.drawable.correct_option_border_bg)

                        if(mCurrentPosition == mQuestionsList!!.size){
                            btn_submit.text = "FINISH"
                        }else{
                            btn_submit.text = "GO TO NEXT QUESTION"
                    }
                    isSelectedAnsOnce = true
                    mSelectedOptionPosition = 0
                }
            }
        }
    }

    private fun answerView(answer:Int,drawableView:Int){
        when (answer){
            1 -> {
                optionOne.background = ContextCompat.getDrawable(this,drawableView)
            }
            2 -> {
                optionTwo.background = ContextCompat.getDrawable(this,drawableView)
            }
            3 -> {
                optionThree.background = ContextCompat.getDrawable(this,drawableView)
            }
            4 -> {
                optionFour.background = ContextCompat.getDrawable(this,drawableView)
            }
        }
    }
    private fun selectedOptionView(tv: TextView, selectedOptionNum: Int) {
            if(isSelectedAnsOnce==false) {
                defaultOptionsView()

                mSelectedOptionPosition = selectedOptionNum

                tv.setTextColor(
                    Color.parseColor("#363A43")
                )
                tv.setTypeface(tv.typeface, Typeface.BOLD)
                tv.background = ContextCompat.getDrawable(
                    this@QuizQuestionActivity,
                    R.drawable.selected_option_border_bg
                )
            }
    }
}