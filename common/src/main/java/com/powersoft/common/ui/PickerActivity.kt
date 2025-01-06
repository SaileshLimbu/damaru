package com.powersoft.common.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.powersoft.common.R
import com.powersoft.common.adapter.PickerAdapter
import com.powersoft.common.databinding.ActivityPickerBinding
import com.powersoft.common.model.PickerEntity
import com.powersoft.common.utils.show

class PickerActivity : AppCompatActivity() {

    lateinit var binding: ActivityPickerBinding

    companion object {
        const val EXTRA_SELECTED_ITEMS = "selected_items"

        fun startForResult(
            activity: Activity, items: List<PickerEntity>, isMultiSelect: Boolean,
            activityResultLauncher: ActivityResultLauncher<Intent>
        ) {
            val intent = Intent(activity, PickerActivity::class.java).apply {
                putExtra("data", ArrayList(items))
                putExtra("isMultiSelect", isMultiSelect)
            }
            activityResultLauncher.launch(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        val items: ArrayList<PickerEntity>? = intent.getParcelableArrayListExtra("data")
        val isMultiSelect = intent.getBooleanExtra("isMultiSelect", false)

        items.let {
            val pickerAdapter = PickerAdapter(items!!, isMultiSelect) { selectedItems ->
            }

            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            binding.recyclerView.adapter = pickerAdapter

            binding.btnSelect.setOnClickListener {
                val selectedItems = pickerAdapter.selectedItems
                val resultIntent = Intent().apply {
                    putExtra(EXTRA_SELECTED_ITEMS, ArrayList(selectedItems))
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }
        if (items.isNullOrEmpty()) {
            binding.errorView.tvError.text = getString(R.string.no_data_found)
            binding.errorView.root.show()
            binding.btnSelect.isEnabled = false
        }
    }
}
