package com.powersoft.common.utils

import android.view.View

fun View.hide(){
    this.visibility = View.GONE
}

fun View.show(){
    this.visibility = View.VISIBLE
}

fun View.invisible(){
    this.visibility = View.INVISIBLE
}

fun View.visibility(expression: Boolean){
    if (expression){
        this.show()
    }else{
        this.hide()
    }
}