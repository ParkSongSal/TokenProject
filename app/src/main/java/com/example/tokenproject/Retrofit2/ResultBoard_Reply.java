package com.example.tokenproject.Retrofit2;

public class ResultBoard_Reply {

    int seq;
    String result;
    int boardSeq;
    String reply;
    String date;
    String nick;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public int getBoardSeq() {
        return boardSeq;
    }

    public void setBoardSeq(int boardSeq) {
        this.boardSeq = boardSeq;
    }
    @Override
    public String toString() {
        return "ResultBoard_Reply{" +
                "seq=" + seq +
                ", result='" + result + '\'' +
                ", boardSeq=" + boardSeq +
                ", reply='" + reply + '\'' +
                ", date='" + date + '\'' +
                ", nick='" + nick + '\'' +
                '}';
    }
}
