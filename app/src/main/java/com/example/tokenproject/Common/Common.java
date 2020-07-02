package com.example.tokenproject.Common;

import android.app.Activity;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {


    //현재 시간
    public static String nowDate(String format){
        // 현재시간을 msec 으로 구한다.
        long now = System.currentTimeMillis();
        // 현재시간을 date 변수에 저장한다.
        Date nowdate = new Date(now);
        // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
        SimpleDateFormat sdfNow = new SimpleDateFormat(format);
        // nowDate 변수에 값을 저장한다.
        String formatDate = sdfNow.format(nowdate);

        return formatDate;
    }


    public static void intentCommon(Activity activity, Class cls2) {
        Intent intent = new Intent(activity, cls2);
        activity.startActivity(intent);
    }


}
