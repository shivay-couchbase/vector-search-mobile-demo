package com.example.quizappbycouchbase

import java.util.Date

class User(val username: String) {
    var dateCreated: Date = Date()

    init {
        dateCreated = Date() // Initialize dateCreated with the current date
    }
}