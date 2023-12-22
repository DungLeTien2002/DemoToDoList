package com.example.demotodolist.base.Utils.extesion

import android.os.Build

fun isSdk34()=isSdkUpSideDownCake()
fun isSdkUpSideDownCake()= Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE