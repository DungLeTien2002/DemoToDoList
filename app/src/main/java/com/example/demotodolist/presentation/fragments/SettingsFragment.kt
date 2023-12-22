package com.example.demotodolist.presentation.fragments

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.example.demotodolist.base.Base_View.BaseFragment
import com.example.demotodolist.databinding.FragmentSettingsBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.review.ReviewManagerFactory


class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {
    private lateinit var sharedPreferences: SharedPreferences
    private val editor by lazy {
        sharedPreferences.edit()
    }
    override fun initData() {

    }

    override fun initListener() {
        binding.rateApp.setOnClickListener {
            val manager = ReviewManagerFactory.create(requireContext())
            val request = manager.requestReviewFlow()
            request.addOnCompleteListener {
                if(it.isSuccessful){
                    val reviewInfo = it.result
                    val flow = manager.launchReviewFlow(activity!!, reviewInfo!!)
                    flow.addOnCompleteListener {  }
                }else {
                    Snackbar.make(binding.root, "Some error occurred!", Snackbar.LENGTH_SHORT).setAction("Action", null).show()
                }
            }
        }

        binding.lowTasks.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("0", isChecked)
            editor.apply()
        }

        binding.midTasks.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("1", isChecked)
            editor.apply()
        }

        binding.highTasks.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("2", isChecked)
            editor.apply()
        }
    }

    override fun initView() {
        sharedPreferences=requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE)
        binding.apply {
            lowTasks.isChecked = sharedPreferences.getBoolean("0", true)
            midTasks.isChecked = sharedPreferences.getBoolean("1", true)
            highTasks.isChecked = sharedPreferences.getBoolean("2", true)

        }
    }

    override fun inflateLayout(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingsBinding? {
        return FragmentSettingsBinding.inflate(inflater,container,false)
    }
}