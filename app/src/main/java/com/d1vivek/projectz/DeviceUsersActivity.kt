package com.d1vivek.projectz

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.d1vivek.projectz.adapters.DeviceUserAdapter
import com.d1vivek.projectz.adapters.User
import com.d1vivek.projectz.databinding.ActivityDeviceUsersBinding

class DeviceUsersActivity : AppCompatActivity() {

    private lateinit var b : ActivityDeviceUsersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityDeviceUsersBinding.inflate(layoutInflater)
        setContentView(b.root)

        val users = listOf(
            User("John", R.drawable.phone_walpaper),
            User("Emily", R.drawable.phone_walpaper),
            User("Sarah", R.drawable.phone_walpaper),
            User("Michael", R.drawable.phone_walpaper)
        )

        val adapter = DeviceUserAdapter(users) { user ->
            Toast.makeText(this, "Selected: ${user.name}", Toast.LENGTH_SHORT).show()
        }

        b.userSelectionRecyclerView.layoutManager = GridLayoutManager(this, 2)
        b.userSelectionRecyclerView.adapter = adapter
    }
}