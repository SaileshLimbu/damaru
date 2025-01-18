package com.powersoft.damaruadmin.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.powersoft.common.base.BaseActivity
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.repository.UserRepo
import com.powersoft.common.utils.AlertUtils
import com.powersoft.common.utils.PrefsHelper
import com.powersoft.common.utils.hide
import com.powersoft.common.utils.hideKeyboard
import com.powersoft.common.utils.show
import com.powersoft.common.utils.showKeyboard
import com.powersoft.damaruadmin.R
import com.powersoft.damaruadmin.databinding.ActivityAdminMainBinding
import com.powersoft.damaruadmin.fragment.AdminDevicesFragment
import com.powersoft.damaruadmin.fragment.AdminUsersFragment
import com.powersoft.damaruadmin.viewmodels.AdminMainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AdminMainActivity : BaseActivity() {
    private val viewModel: AdminMainActivityViewModel by viewModels()
    private lateinit var binding: ActivityAdminMainBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    @Inject
    lateinit var prefsHelper: PrefsHelper

    @Inject
    lateinit var userRepo: UserRepo

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPagerAdapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = viewPagerAdapter

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.tabUsers -> {
                    binding.viewPager.currentItem = 0
                    binding.tvTitle.text = getString(R.string.users)
                    true
                }

                R.id.tabDevices -> {
                    binding.viewPager.currentItem = 1
                    binding.tvTitle.text = getString(com.powersoft.common.R.string.devices)
                    true
                }

                else -> false
            }
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.bottomNavigationView.selectedItemId = when (position) {
                    0 -> R.id.tabUsers
                    1 -> R.id.tabDevices
                    else -> R.id.tabUsers
                }
            }
        })

        binding.imgLogout.setOnClickListener {
            AlertUtils.showConfirmDialog(
                this@AdminMainActivity, getString(com.powersoft.common.R.string.logout),
                getString(com.powersoft.common.R.string.are_you_sure_you_want_to_logout),
                getString(com.powersoft.common.R.string.yes),
                getString(com.powersoft.common.R.string.no)
            ) {
                prefsHelper.clear()
                userRepo.logout()
                startActivity(
                    Intent(applicationContext, LoginActivityImpl::class.java).setFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    )
                )
            }
        }

        binding.imgSearch.setOnClickListener {
            binding.viewSearch.show()
            binding.etSearch.showKeyboard()
        }

        binding.etSearch.addTextChangedListener {
            val currentFragment = getCurrentFragment()
            if (currentFragment is AdminUsersFragment) {
                currentFragment.onSearch(it.toString())
            } else if (currentFragment is AdminDevicesFragment) {
                currentFragment.onSearch(it.toString())
            }
        }

        binding.tvCancle.setOnClickListener {
            this@AdminMainActivity.hideKeyboard()
            binding.etSearch.text.clear()
            binding.viewSearch.hide()
        }
    }

    private fun getCurrentFragment(): Fragment? {
        val fragmentId = binding.viewPager.currentItem
        return supportFragmentManager.findFragmentByTag("f$fragmentId")
    }

    // Adapter for ViewPager2
    inner class ViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> AdminUsersFragment()
                1 -> AdminDevicesFragment()
                else -> throw IllegalStateException("Invalid position $position")
            }
        }
    }

    interface SearchableFragment {
        fun onSearch(query: String)
    }
}
