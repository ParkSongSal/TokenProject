package com.example.tokenproject.Model


data class BoardModel(
    var seq: Int,
    var user_id: String,
    var title: String,
    var content: String,
    var date: String,
    var path: String?,
    var path2 : String?,
    var path3 : String?,
    var path4 : String?,
    var reply_count: String
)
