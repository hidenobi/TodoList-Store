package com.padi.todo_list.ui.calendar

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseBindingFragment
import com.padi.todo_list.databinding.FragmentCalendarBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CalendarFragment : BaseBindingFragment<CalendarViewModel, FragmentCalendarBinding>(R.layout.fragment_calendar) {

    override val viewModel: CalendarViewModel by viewModels()

    override fun initView(view: View, savedInstanceState: Bundle?) {
        viewModel.text.observe(viewLifecycleOwner) {
            viewBinding.textGallery.text = it
        }
    }
}