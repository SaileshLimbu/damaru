package com.powersoft.common.base

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.powersoft.common.databinding.ActivityBaseBinding

abstract class BaseActivity : AppCompatActivity() {
    abstract fun getViewModel() : BaseViewModel?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(getViewModel()?.loading?.value == true){
                    getViewModel()?.hideLoader()
                }else {
                    finish()
                }
            }
        })
    }

    override fun setContentView(view: View?) {
        val b = ActivityBaseBinding.inflate(layoutInflater)
        b.content.addView(view)

        getViewModel()?.loading?.observe(this) {
            b.loader.visibility = if (it) View.VISIBLE else View.GONE
        }
        super.setContentView(b.root)
    }
}