package com.example.tokenproject.Model


class BoardModel(
    var seq: Int,
    var user_id: String,
    var title: String,
    var content: String,
    var date: String,
    var path: String?,
    var reply_count: String
) {

    override fun toString(): String {
        return "getServerImage{" +
                "seq=" + seq +
                ", user_id='" + user_id + '\'' +
                ", Title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", date='" + date + '\'' +
                ", path='" + path + '\'' +
                ", reply_count='" + reply_count + '\'' +
                '}'
    }

}
