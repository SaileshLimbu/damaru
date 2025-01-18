package com.powersoft.damaru.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.powersoft.common.repository.UserRepo
import com.powersoft.common.utils.AlertUtils
import com.powersoft.common.utils.PrefsHelper
import com.powersoft.common.utils.visibility
import com.powersoft.damaru.R
import com.powersoft.damaru.databinding.ActivityMainBinding
import com.powersoft.damaru.fragments.AccountsFragment
import com.powersoft.damaru.fragments.HomeFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    @Inject
    lateinit var prefsHelper: PrefsHelper

    @Inject
    lateinit var userRepo: UserRepo

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
                    binding.tvTitle.text = "Devices"
                    true
                }

                R.id.tabAccounts -> {
                    binding.viewPager.currentItem = 1
                    binding.tvTitle.text = "Accounts"
                    true
                }

                else -> false
            }
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.bottomNavigationView.selectedItemId = when (position) {
                    0 -> R.id.tabHome
                    1 -> R.id.tabAccounts
                    else -> R.id.tabHome
                }
            }
        })

        binding.bottomNavigationView.visibility(userRepo.seasonEntity.value?.isRootUser == true)

        binding.imgLogout.setOnClickListener {
            AlertUtils.showConfirmDialog(this@MainActivity, getString(com.powersoft.common.R.string.logout),
                getString(com.powersoft.common.R.string.are_you_sure_you_want_to_logout)) {
                prefsHelper.clear()
                userRepo.logout()
                startActivity(
                    Intent(applicationContext, LoginActivityImpl::class.java).setFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    )
                )
            }
        }
    }

    // Adapter for ViewPager2
    inner class ViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = if (userRepo.seasonEntity.value?.isRootUser == true) 2 else 1

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> HomeFragment()
                1 -> AccountsFragment()
                else -> throw IllegalStateException("Invalid position $position")
            }
        }
    }
}
