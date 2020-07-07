package com.example.tokenproject.Model

class BoardReplyModel(
    var seq: Int,
    var user_id: String,
    var reply: String,
    var date: String
) {

    override fun toString(): String {
        return "getBoardReply{" +
                "seq=" + seq +
                ", user_id='" + user_id + '\'' +
                ", reply='" + reply + '\'' +
                ", date='" + date + '\'' +
                '}'
    }

}