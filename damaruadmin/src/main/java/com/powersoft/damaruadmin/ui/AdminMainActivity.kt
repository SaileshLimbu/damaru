package com.powersoft.damaruadmin.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.powersoft.common.base.BaseActivity
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.ui.helper.AlertHelper
import com.powersoft.common.utils.PrefsHelper
import com.powersoft.damaruadmin.R
import com.powersoft.damaruadmin.databinding.ActivityAdminMainBinding
import com.powersoft.damaruadmin.fragment.AdminDevicesFragment
import com.powersoft.damaruadmin.fragment.AdminHomeFragment
import com.powersoft.damaruadmin.viewmodels.AdminMainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AdminMainActivity : BaseActivity() {
    private val viewModel : AdminMainActivityViewModel by viewModels()
    private lateinit var binding: ActivityAdminMainBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    @Inject
    lateinit var prefsHelper: PrefsHelper
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
                R.id.tabHome -> {
                    binding.viewPager.currentItem = 0
                    true
                }

                R.id.tabDevices -> {
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
                    1 -> R.id.tabDevices
                    else -> R.id.tabHome
                }
            }
        })

        binding.imgLogout.setOnClickListener {
            AlertHelper.showAlertDialog(this@AdminMainActivity, getString(com.powersoft.common.R.string.logout), getString(com.powersoft.common.R.string.are_you_sure_you_want_to_logout), getString(com.powersoft.common.R.string.yes), getString(com.powersoft.common.R.string.no),
                onPositiveButtonClick = {
                    prefsHelper.clear()
                    startActivity(
                        Intent(applicationContext, LoginActivityImpl::class.java).setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        )
                    )
                })
        }
    }

    // Adapter for ViewPager2
    inner class ViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> AdminHomeFragment()
                1 -> AdminDevicesFragment()
                else -> throw IllegalStateException("Invalid position $position")
            }
        }
    }
}
