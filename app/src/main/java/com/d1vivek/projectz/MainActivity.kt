package com.d1vivek.projectz

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.d1vivek.projectz.databinding.ActivityMainBinding
import com.d1vivek.projectz.fragments.HomeFragment
import com.d1vivek.projectz.fragments.MoreFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPagerAdapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = viewPagerAdapter

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.tabHome -> {
                    binding.viewPager.currentItem = 0
                    true
                }

                R.id.tabMore -> {
                    binding.viewPager.currentItem = 1
                    true
                }

                else -> false
            }
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.bottomNavigationView.selectedItemId = when (position) {
                    0 -> R.id.tabHome
                    1 -> R.id.tabMore
                    else -> R.id.tabHome
                }
            }
        })
    }

    // Adapter for ViewPager2
    inner class ViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> HomeFragment()
                1 -> MoreFragment()
                else -> throw IllegalStateException("Invalid position $position")
            }
        }
    }
}
