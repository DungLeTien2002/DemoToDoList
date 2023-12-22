package com.example.demotodolist.base.Base_View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.example.demotodolist.base.Utils.Util.Constant
import com.example.demotodolist.base.Utils.extesion.finishWithSlide
import com.example.demotodolist.base.Utils.extesion.handleBackPressed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {
    //region variable
    companion object {
        const val TAG = Constant.TAG
        const val TIME_DELAY_CLICK=200L
    }

    lateinit var binding:VB
    private var isAvailableClick=true
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=inflateViewBinding(layoutInflater)
        setContentView(binding.root)
        initView()
        initData()
        initListener()
        handleBackPressed{
            onBack()
        }
    }

    abstract fun inflateViewBinding(inflater: LayoutInflater): VB

    abstract fun initListener()

    abstract fun initData()

    abstract fun initView()

    open fun onBack() {
        if(supportFragmentManager.backStackEntryCount>0){
            onBackPressedDispatcher.onBackPressed()
        }else{
            finishWithSlide()
        }
    }

    private fun delayClick(){
        lifecycleScope.launch ( Dispatchers.IO){
            isAvailableClick=false
            delay(TIME_DELAY_CLICK)
            isAvailableClick=true
        }
    }

    fun View.clickSafety(action: () -> Unit) {
        this.setOnClickListener {
            if (isAvailableClick) {
                action()
                delayClick()
            }
        }
    }
}