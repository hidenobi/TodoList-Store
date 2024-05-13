package com.padi.todo_list.ui.task.dialog.date

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseDialogFragment
import com.padi.todo_list.common.extension.parcelable
import com.padi.todo_list.common.extension.setOnDebounceClickListener
import com.padi.todo_list.common.extension.setSizePercent
import com.padi.todo_list.common.extension.showToast
import com.padi.todo_list.common.utils.BundleKey
import com.padi.todo_list.common.utils.ChipDate.CHIP_3_DAY_LATER
import com.padi.todo_list.common.utils.ChipDate.CHIP_TODAY
import com.padi.todo_list.common.utils.ChipDate.CHIP_TOMORROW
import com.padi.todo_list.common.utils.DateStatusTask
import com.padi.todo_list.common.utils.DateTimeFormatUtils
import com.padi.todo_list.common.utils.RepeatConstants
import com.padi.todo_list.common.utils.UtilsJ
import com.padi.todo_list.common.utils.UtilsJ.convertTimeToMilliseconds
import com.padi.todo_list.common.utils.UtilsJ.getClockModelInput
import com.padi.todo_list.common.utils.UtilsJ.getDateSelectionEnumInput
import com.padi.todo_list.common.utils.UtilsJ.getReminderString
import com.padi.todo_list.common.utils.UtilsJ.getTimeSelectionEnumInput
import com.padi.todo_list.data.local.model.EventTaskEntity
import com.padi.todo_list.databinding.CustomCalendarDayBinding
import com.padi.todo_list.databinding.CustomCalendarHeaderBinding
import com.padi.todo_list.databinding.DialogDateBinding
import com.padi.todo_list.shared.displayText
import com.padi.todo_list.shared.setTextColorRes
import com.padi.todo_list.ui.MainViewModel
import com.padi.todo_list.ui.task.dialog.reminder.ReminderDialog
import com.padi.todo_list.ui.task.dialog.repeat.RepeatDialog
import com.padi.todo_list.ui.task.dialog.time.TimeDialog
import com.padi.todo_list.ui.task.enum_class.DateSelectionEnum
import com.padi.todo_list.ui.task.enum_class.TimeSelectionEnum
import com.padi.todo_list.ui.task.model.ClockModel
import com.padi.todo_list.ui.task.model.OffsetTimeUI
import com.padi.todo_list.ui.task.model.RepeatOptions
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.util.Calendar


@AndroidEntryPoint
class DateDialog(private val onClickDone: (eventTask: EventTaskEntity, listOffSetTime: ArrayList<OffsetTimeUI>) -> Unit) :
    BaseDialogFragment<DialogDateBinding>() {
    private val viewModel: MainViewModel by activityViewModels()
    private var mIsRemind = false
    private lateinit var mEventTaskInput: EventTaskEntity

    private lateinit var selectedDateInput: LocalDate
    private val today = LocalDate.now()
    private var modeCreateNew = false

    override fun getLayout(): Int {
        return R.layout.dialog_date
    }

    override fun initView() {
        setSizePercent(90,90)
        getArgument()
        setupChip()
        setupAction()
        handleBackDevice()
        setupCustomCalendar()

        observeData()
    }


    private fun getArgument() {
        mEventTaskInput = arguments?.parcelable(BundleKey.KEY_EVENT_TASK)!!
        modeCreateNew = arguments?.getBoolean(BundleKey.KEY_MODE_CREATE_NEW, false) ?: false
        setupBindingData(mEventTaskInput)
        viewModel.updateEventLiveData(mEventTaskInput)
    }

    private fun setupBindingData(input: EventTaskEntity) {
        binding.dateSelection = getDateSelectionEnumInput(input)
        viewModel.updateDateSelectionEnum(getDateSelectionEnumInput(input))

        selectedDateInput =
            if (!UtilsJ.isToday(input.dueDate!!)) {
                Instant.ofEpochMilli(input.dueDate!!).atZone(ZoneId.systemDefault()).toLocalDate()
            } else {
                today
            }
        if (modeCreateNew) {
            viewModel.updateLocaleDateSelected(selectedDateInput)
        } else {
        viewModel.updateLocaleDateSelected(
            if (binding.dateSelection!! == DateSelectionEnum.NO_DATE) {
                null
            } else selectedDateInput
        )
        }
        binding.timeSelection = getTimeSelectionEnumInput(input)
        viewModel.updateTimeSelection(getTimeSelectionEnumInput(input), getClockModelInput(input))
        if (input.id != 0L) {
            viewModel.updateRepeatOption(RepeatOptions.getInstance(input.repeat))
            viewModel.updateListOffsetTimeDefault(input)
        }
    }

    private fun setupCustomCalendar() {
        val daysOfWeek = daysOfWeek()
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(200)
        val endMonth = currentMonth.plusMonths(200)


        configureBinders(daysOfWeek)
        binding.exFiveCalendar.setup(startMonth, endMonth, daysOfWeek.first())
        if (modeCreateNew || viewModel.localeDateSelected.value == null) {
            binding.exFiveCalendar.scrollToMonth(currentMonth)
        } else {
            viewModel.localeDateSelected.value?.let {
                val yearMonth = YearMonth.of(it.year, it.monthValue)
                binding.exFiveCalendar.scrollToMonth(yearMonth)
            }
        }

        binding.exFiveCalendar.notifyCalendarChanged()  // update calendarView after 'selectedDate' change

        binding.exFiveCalendar.monthScrollListener = { month ->
            binding.exFiveMonthYearText.text = month.yearMonth.displayText()

            viewModel.localeDateSelected.value?.let {
                binding.exFiveCalendar.notifyDateChanged(it)
            }
        }

        binding.exFiveNextMonthImage.setOnClickListener {
            binding.exFiveCalendar.findFirstVisibleMonth()?.let {
                binding.exFiveCalendar.smoothScrollToMonth(it.yearMonth.nextMonth)
            }
        }

        binding.exFivePreviousMonthImage.setOnClickListener {
            binding.exFiveCalendar.findFirstVisibleMonth()?.let {
                binding.exFiveCalendar.smoothScrollToMonth(it.yearMonth.previousMonth)
            }
        }
    }

    private fun observeData() {
        viewModel.dateSelectionLiveData.observe(viewLifecycleOwner) {
            binding.dateSelection = it
            Timber.d("CALENDAR-dateSelection: ${it.name}")
        }
        viewModel.timeSelectionLiveData.observe(viewLifecycleOwner) {
            binding.timeSelection = it
            setTimeTextView()
        }
        viewModel.clockModelLiveData.observe(viewLifecycleOwner) {
            setRemindTextView()
        }
        viewModel.listOffsetTimeDefaultLiveData.observe(viewLifecycleOwner) {
            Log.d("ReminderDlgViewModel", "it: ${it[2].isChecked}")
            mIsRemind = it.any { model -> model.isChecked }
            setRemindTextView()
        }
        viewModel.repeatOptionLiveData.observe(viewLifecycleOwner) {
            if (it.id != RepeatOptions.NOT_INIT_ID) {
                binding.tvNoRepeat.text = viewModel.repeatOptionLiveData.value!!.name
            } else {
                binding.tvNoRepeat.text = getString(R.string.not_available)
            }
            binding.exFiveCalendar.notifyCalendarChanged()
        }

        viewModel.localeDateSelected.observe(viewLifecycleOwner) {
            it?.let {
                handleSelectDate(it.year, it.monthValue, it.dayOfMonth)
            }
        }

    }

    private fun setupAction() {
        binding.rlTime.setOnDebounceClickListener(1000L) {
            if (viewModel.dateSelectionLiveData.value == DateSelectionEnum.NO_DATE) {
                showToast(
                    requireContext(),
                    requireContext().getString(R.string.please_select_a_date)
                )
            } else {
                val bundle = Bundle()
                bundle.putSerializable(
                    BundleKey.KEY_CLOCK_MODEL,
                    viewModel.clockModelLiveData.value!!
                )
                bundle.putSerializable(
                    BundleKey.KEY_TYPE_TIME,
                    viewModel.timeSelectionLiveData.value!!
                )
                val clockDialog = TimeDialog(onClickDone = { typeTime, clockModel ->
                    viewModel.updateTimeSelection(typeTime, clockModel)
//
                    val notSelect = typeTime == TimeSelectionEnum.NO_TIME
                    viewModel.updateListOffsetTimeDefault(OffsetTimeUI.getData(notSelect))
                })
                clockDialog.arguments = bundle
                clockDialog.show(childFragmentManager, TimeDialog::javaClass.name)
            }
        }
        binding.rlReminder.setOnDebounceClickListener(750) {
            if (binding.timeSelection == TimeSelectionEnum.NO_TIME) {
                showToast(requireContext(), getString(R.string.please_select_time))
            } else {
                val clockModel = viewModel.clockModelLiveData.value!!
                val listOffset = ArrayList<OffsetTimeUI>()
                listOffset.addAll(viewModel.listOffsetTimeDefaultLiveData.value!!)

                Log.d("ReminderDlgViewModel", "${listOffset[2].isChecked}")

                val bundle = Bundle()
                bundle.putSerializable(BundleKey.IS_REMIND, mIsRemind)
                bundle.putSerializable(BundleKey.KEY_CLOCK_MODEL, clockModel)
                bundle.putSerializable(
                    BundleKey.KEY_LIST_REMIND_TIME_UI, ArrayList(listOffset)
                )
                val reminderDialog = ReminderDialog(onClickCancel = { output ->
                    viewModel.updateListOffsetTimeDefault(output)
                }, onClickDone = { allOffsetTimeOutput ->
                    viewModel.updateListOffsetTimeDefault(allOffsetTimeOutput)
                })
                reminderDialog.arguments = bundle
                reminderDialog.show(childFragmentManager, ReminderDialog::javaClass.name)
            }
        }

        binding.rlRepeat.setOnDebounceClickListener(750) {
            if (viewModel.dateSelectionLiveData.value == DateSelectionEnum.NO_DATE) {
                showToast(requireContext(), getString(R.string.please_select_a_date))
            } else {
                val bundle = Bundle()
                bundle.apply {
                    putBoolean(
                        BundleKey.IS_REPEAT,
                        viewModel.repeatOptionLiveData.value!!.isSelected
                    )
                    putSerializable(
                        BundleKey.KEY_REPEAT_OPTION,
                        viewModel.repeatOptionLiveData.value!!
                    )
                    putSerializable(
                        BundleKey.KEY_CLOCK_MODEL,
                        viewModel.clockModelLiveData.value!!
                    )
                }
                val repeatDialog = RepeatDialog(onClickDone = { repeatOption ->
                    viewModel.updateRepeatOption(repeatOption)
                })
                repeatDialog.arguments = bundle
                repeatDialog.show(childFragmentManager, RepeatDialog::javaClass.name)
            }
        }
        binding.btnCancel.setOnDebounceClickListener {
            dismiss()
        }
        binding.btnDone.setOnDebounceClickListener {
            handleClickDone()
        }
    }

    private fun setupChip() {
        val chips = listOf(
            Pair(binding.chipNoDate, null),
            Pair(binding.chipToday, CHIP_TODAY),
            Pair(binding.chipTomorrow, CHIP_TOMORROW),
            Pair(binding.chip3DayLater, CHIP_3_DAY_LATER),
            Pair(binding.chipThisSunday, null)
        )
        chips.forEach { (chip, offsetDays) ->
            chip.setOnClickListener {
                handleChipClick(chip, offsetDays)
            }
        }
    }

    private fun handleSelectDate(year: Int, month: Int, dayOfMonth: Int) {
        Timber.d("CALENDAR: $year, $month, $dayOfMonth")
        Timber.d("CALENDAR-dateSelection: 1??")
        viewModel.updateDateSelectionByYearMonthDay(year, month, dayOfMonth)
        val selectedCalendar = LocalDate.of(year, month, dayOfMonth)

        val todayCalendar = LocalDate.now()
        val tomorrowCalendar =
            LocalDate.of(todayCalendar.year, todayCalendar.monthValue, todayCalendar.dayOfMonth + 1)

        val lastDayOfWeek = today.with(DayOfWeek.SUNDAY)

        val threeDaysLaterCalendar =
            LocalDate.of(todayCalendar.year, todayCalendar.monthValue, todayCalendar.dayOfMonth + 3)

        when (selectedCalendar) {
            todayCalendar -> {
                Timber.d("CALENDAR-dateSelection: 2??")
                viewModel.updateDateSelectionEnum(DateSelectionEnum.TODAY)
            }

            tomorrowCalendar -> {
                viewModel.updateDateSelectionEnum(DateSelectionEnum.TOMORROW)
            }

            threeDaysLaterCalendar -> {
                viewModel.updateDateSelectionEnum(DateSelectionEnum.NEXT_3_DAYS)
            }

            lastDayOfWeek -> {
                viewModel.updateDateSelectionEnum(DateSelectionEnum.THIS_SUNDAY)
            }

            else -> {
                viewModel.updateDateSelectionEnum(DateSelectionEnum.IDLE)
            }
        }
    }

    private fun handleChipClick(chip: Chip, offsetDays: Int?) {
        when (chip) {
            binding.chipNoDate -> {
                viewModel.updateDateSelectionEnum(DateSelectionEnum.NO_DATE)
                viewModel.updateTimeSelection(TimeSelectionEnum.NO_TIME, ClockModel.getInstance())
                viewModel.updateLocaleDateSelected(null)
                viewModel.updateRepeatOption(RepeatOptions.getInstance(RepeatConstants.NO_REPEAT))
                viewModel.updateListOffsetTimeDefault(OffsetTimeUI.getData(isNotSelect = true))
            }

            binding.chipToday -> {
                viewModel.updateDateSelectionEnum(DateSelectionEnum.TODAY)
                viewModel.updateLocaleDateSelected(
                    LocalDate.of(
                        today.year,
                        today.monthValue,
                        today.dayOfMonth
                    )
                )
            }

            binding.chipTomorrow -> {
                viewModel.updateDateSelectionEnum(DateSelectionEnum.TOMORROW)
                viewModel.updateLocaleDateSelected(
                    LocalDate.of(
                        today.year,
                        today.monthValue,
                        today.dayOfMonth + offsetDays!!
                    )
                )
            }

            binding.chip3DayLater -> {
                viewModel.updateDateSelectionEnum(DateSelectionEnum.NEXT_3_DAYS)
                viewModel.updateLocaleDateSelected(
                    LocalDate.of(
                        today.year,
                        today.monthValue,
                        today.dayOfMonth + offsetDays!!
                    )
                )
            }

            binding.chipThisSunday -> {
                viewModel.updateDateSelectionEnum(DateSelectionEnum.THIS_SUNDAY)
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                viewModel.updateLocaleDateSelected(today.with(DayOfWeek.SUNDAY))
            }
        }
        binding.exFiveCalendar.notifyCalendarChanged()
    }

    private fun setRemindTextView() {
        if (binding.timeSelection == TimeSelectionEnum.NO_TIME) {
            binding.tvNoRemind.text = getString(R.string.not_available)
        } else {
            val list =
                viewModel.listOffsetTimeDefaultLiveData.value!!.filter { model -> model.isChecked }
            val dueDate = convertTimeToMilliseconds(viewModel.clockModelLiveData.value!!)
            val resultText = StringBuilder("")
            val listString = getReminderString(dueDate, list)
            listString.forEachIndexed { index, str ->
                if (index == 0) {
                    resultText.append(str)
                } else {
                    resultText.append(" ,$str")
                }
            }
            if (resultText.toString().trim().isBlank()) {
                binding.tvNoRemind.text = getString(R.string.not_available)
            } else {
                binding.tvNoRemind.text = resultText
            }
        }
    }
    private fun setTimeTextView() {
        if (binding.timeSelection == TimeSelectionEnum.NO_TIME) {
            binding.tvNoTime.text = getString(R.string.not_available)
        } else {
            binding.tvNoTime.text =
                DateTimeFormatUtils.convertClockModelToTime(viewModel.clockModelLiveData.value!!)
        }
    }

    private fun handleClickDone() {
        val dateStatus = if (viewModel.dateSelectionLiveData.value!! == DateSelectionEnum.NO_DATE) {
            DateStatusTask.NO_DATE
        } else {
            DateStatusTask.HAS_DATE
        }
        val output = viewModel.eventTaskLiveData.value!!.copy(
            timeStatus = UtilsJ.getDateTimeTaskValue(viewModel.clockModelLiveData.value!!),
            dateStatus = dateStatus,
            hasRemind = mIsRemind
        )
        output.dueDate = convertTimeToMilliseconds(viewModel.clockModelLiveData.value!!)
        output.repeat = viewModel.repeatOptionLiveData.value!!.repeat
        val list =  viewModel.listOffsetTimeDefaultLiveData.value!!
        onClickDone.invoke(
            output,
            list
        )
        dismiss()
    }

    private fun configureBinders(daysOfWeek: List<DayOfWeek>) {
        val calendarView = binding.exFiveCalendar

        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val calendarDayBinding = CustomCalendarDayBinding.bind(view)
            val textView = CustomCalendarDayBinding.bind(view).exFiveDayText

            init {
                view.setOnClickListener {
                    /** update day selected ===>   */

                        if (viewModel.localeDateSelected.value == day.date) {
                            calendarView.notifyDayChanged(day)
                        } else {
                            val oldDate = viewModel.localeDateSelected.value
                            calendarView.notifyDateChanged(day.date)
                            oldDate?.let { calendarView.notifyDateChanged(oldDate) }
                        }
                        viewModel.updateLocaleDateSelected(day.date)
//                      cập nhật lại calendarView với flightTopView sau khi update tùy chọn repeat
                        viewModel.updateRepeatOption(viewModel.repeatOptionLiveData.value!!)

                        Timber.d("CALENDAR-view-click: ${day.date.year}, ${day.date.monthValue}, ${day.date.dayOfMonth}")

                    /** update day selected <===   */

                    /** scroll to next|prev month ===>   */
                    if (day.position > DayPosition.MonthDate) {
                        binding.exFiveCalendar.scrollToMonth(YearMonth.of(day.date.year, day.date.monthValue))

                        Timber.d("CALENDAR-view-click next: ${day.date.year}, ${day.date.monthValue}, ${day.date.dayOfMonth}")
                    } else {
                        binding.exFiveCalendar.scrollToMonth(YearMonth.of(day.date.year, day.date.monthValue))

                        Timber.d("CALENDAR-view-click prev: ${day.date.year}, ${day.date.monthValue}, ${day.date.dayOfMonth}")
                    }
                    /** scroll to next|prev month <===   */
                }
            }
        }
        binding.exFiveCalendar.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                val textView = container.textView
                textView.text = data.date.dayOfMonth.toString()
                val layout = container.calendarDayBinding.exFiveDayLayout

                val flightTopView = container.calendarDayBinding.exFiveDayFlightTop
                if (viewModel.dateSelectionLiveData.value!! != DateSelectionEnum.NO_DATE) {
                    flightTopView.setBackgroundResource(R.drawable.selected_bg)
                } else {
                    flightTopView.setBackgroundResource(0)
                }

                // Ẩn flightTopView cho các textView có date nhỏ hơn selectedDate
                viewModel.localeDateSelected.value?.let { selected ->
                    when (viewModel.repeatOptionLiveData.value!!.repeat) {
                        RepeatConstants.NO_REPEAT -> {
                            flightTopView.visibility = View.INVISIBLE
                        }

                        RepeatConstants.HOURLY -> {
                            if (data.date.isBefore(selected)) {
                                flightTopView.visibility = View.INVISIBLE
                            } else {
                                flightTopView.visibility = View.VISIBLE
                            }
                        }

                        RepeatConstants.DAILY -> {
                            if (data.date.isBefore(selected)) {
                                flightTopView.visibility = View.INVISIBLE
                            } else {
                                flightTopView.visibility = View.VISIBLE
                            }
                        }

                        RepeatConstants.WEEKLY -> {
                            val isInCurrentWeekDay = data.date.dayOfWeek == selected.dayOfWeek
                            flightTopView.visibility =
                                if (isInCurrentWeekDay) View.VISIBLE else View.INVISIBLE
                        }

                        RepeatConstants.MONTHLY -> {
                            val isInCurrentWeekDay = data.date.dayOfMonth == selected.dayOfMonth
                            flightTopView.visibility =
                                if (isInCurrentWeekDay) View.VISIBLE else View.INVISIBLE
                        }

                        RepeatConstants.YEARLY -> {
                            val isInCurrentWeekDay = data.date.dayOfMonth == selected.dayOfMonth
                                    && data.date.month == selected.month
                                    && data.date.year >= selected.year
                            flightTopView.visibility =
                                if (isInCurrentWeekDay) View.VISIBLE else View.INVISIBLE
                        }
                    }
                }

                if (data.position == DayPosition.MonthDate) {
                    when (data.date) {
                        viewModel.localeDateSelected.value -> {
                            textView.setTextColorRes(R.color.white)
                            layout.setBackgroundResource(R.drawable.selected_bg)
                        }

                        today -> {
                            textView.setTextColorRes(R.color.primary_purpul)
                            layout.background = null
                        }

                        else -> {
                            textView.setTextColorRes(R.color.black_text_calendar)
                            layout.background = null
                        }
                    }
                } else {
                    textView.setTextColorRes(R.color.primary_purpul_50)
                    layout.background = null
                }
            }
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout = CustomCalendarHeaderBinding.bind(view).legendLayout.root
        }

        val typeFace = Typeface.create("roboto", Typeface.BOLD)
        binding.exFiveCalendar.monthHeaderBinder =
            object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View) = MonthViewContainer(view)
                override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                    // Setup each header day text if we have not done that already.
                    if (container.legendLayout.tag == null) {
                        container.legendLayout.tag = data.yearMonth
                        container.legendLayout.children.map { it as TextView }
                            .forEachIndexed { index, tv ->
                                tv.text = daysOfWeek[index].displayText(uppercase = false)
                                    .subSequence(0, 2)
                                tv.setTextColorRes(R.color.primary_purpul)
                                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                                tv.typeface = typeFace
                            }
                    }
                }
            }
    }

    private fun handleBackDevice() {
        dialog?.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                dismiss()
                return@setOnKeyListener true
            } else
                return@setOnKeyListener false
        }
    }

}

