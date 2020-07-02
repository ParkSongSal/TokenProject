package com.example.tokenproject.Retrofit2;

public class ResultModel {
    int seq;
    String result;


    public ResultModel(int seq, String result) {
        this.seq = seq;
        this.result = result;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "ResultModel{" +
                "seq=" + seq +
                ", result='" + result + '\'' +
                '}';
    }
}
