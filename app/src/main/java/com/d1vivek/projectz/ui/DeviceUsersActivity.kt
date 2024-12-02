package com.d1vivek.projectz.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.d1vivek.projectz.R
import com.d1vivek.projectz.adapters.DeviceUserAdapter
import com.d1vivek.projectz.adapters.User
import com.d1vivek.projectz.databinding.ActivityDeviceUsersBinding

class DeviceUsersActivity : AppCompatActivity() {

    private lateinit var b: ActivityDeviceUsersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityDeviceUsersBinding.inflate(layoutInflater)
        setContentView(b.root)

        val users = listOf(
            User("John", R.drawable.phone_walpaper),
            User("Emily", R.drawable.phone_walpaper),
            User("Sarah", R.drawable.phone_walpaper),
            User("Michael", R.drawable.phone_walpaper),
            User("", -1)
        )

        val adapter = DeviceUserAdapter(users) { user ->
            if (user.name.isEmpty() && user.profileImage == -1) {
                startActivity(Intent(this@DeviceUsersActivity, AddUserActivity::class.java))
            } else {
                startActivity(Intent(this@DeviceUsersActivity, PinActivity::class.java))
            }
        }

        b.userSelectionRecyclerView.layoutManager = GridLayoutManager(this, 3)
        b.userSelectionRecyclerView.adapter = adapter
    }
}