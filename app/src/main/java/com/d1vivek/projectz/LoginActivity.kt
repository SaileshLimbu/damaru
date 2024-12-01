package com.d1vivek.projectz

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.d1vivek.projectz.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    lateinit var b: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btnLogin.setOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }
    }
}