package com.example.quizappbycouchbase

data class Questions (
    val id: Int,
    val question: String,
    val optionOne: String,
    val optionTwo: String,
    val optionThree: String,
    val optionFour: String,
    val correctAns: Int,
    val category: String
)
