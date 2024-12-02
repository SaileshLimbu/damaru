package com.d1vivek.projectz.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.d1vivek.projectz.databinding.ActivityAddUserBinding

class AddUserActivity : AppCompatActivity() {
    private lateinit var b: ActivityAddUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityAddUserBinding.inflate(layoutInflater)
        setContentView(b.root)
    }
}