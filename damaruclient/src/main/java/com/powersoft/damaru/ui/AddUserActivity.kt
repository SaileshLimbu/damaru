package com.powersoft.damaru.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.powersoft.damaru.databinding.ActivityAddUserBinding

class AddUserActivity : AppCompatActivity() {
    private lateinit var b: ActivityAddUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityAddUserBinding.inflate(layoutInflater)
        setContentView(b.root)
    }
}