package com.example.demotodolist.base.Utils.Util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date

class DateToString {
    @SuppressLint("SimpleDateFormat")
    companion object {
        fun convertDateToString(date: Date): String {
            val format1 = "MMM dd,yyyy"
            val format2 = "MMM dd, yyyy,hh:mm aaa"
            val dateInfinity = Date(Constant.MAX_TIMESTAMP)
            return if (dateInfinity.compareTo(date) == 0) "N/A"
            else if (date.time == 0L) {
                val df = SimpleDateFormat(format1)
                df.format(date)
            } else {
                val df = SimpleDateFormat(format2)
                df.format(date)
            }
        }
    }
}