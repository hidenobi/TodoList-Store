package com.padi.todo_list.ui.my

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewParent
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseBindingFragment
import com.padi.todo_list.common.extension.formatLocalDateTime
import com.padi.todo_list.common.extension.gone
import com.padi.todo_list.common.extension.invisible
import com.padi.todo_list.common.extension.setOnDebounceClickListener
import com.padi.todo_list.common.extension.showPeriodMenu
import com.padi.todo_list.common.extension.visible
import com.padi.todo_list.common.utils.BundleKey
import com.padi.todo_list.common.utils.COLOR_PIE_CHART
import com.padi.todo_list.common.utils.DateTimeFormat
import com.padi.todo_list.common.utils.UtilsJ.getDaysOfWeek
import com.padi.todo_list.common.utils.UtilsJ.getViewPointOnScr
import com.padi.todo_list.data.local.model.CategoryEntity
import com.padi.todo_list.data.local.model.EventTaskEntity
import com.padi.todo_list.data.local.prefs.AppPrefs
import com.padi.todo_list.databinding.FragmentMyBinding
import com.padi.todo_list.ui.setting.SettingActivity
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.util.SortedMap


@AndroidEntryPoint
class MyFragment : BaseBindingFragment<MyViewModel, FragmentMyBinding>(R.layout.fragment_my) {

    override val viewModel: MyViewModel by viewModels()
    private val sevenDayAdapter: TaskSevenDayAdapter = TaskSevenDayAdapter()
    private val pieAdapter: LabelAdapter = LabelAdapter()
    val listDayOfWeek = getDaysOfWeek()
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                SettingActivity.ACTION_CHANGE_DATE_TIME_FORMAT ->{
                    viewModel.getTaskSevenDay()
                    viewModel.getDayOfWeek()
                }
            }
        }
    }

    override fun initView(view: View, savedInstanceState: Bundle?) {
        setAction()
        viewBinding.apply {
            viewModel.run {
                listTask.observe(viewLifecycleOwner) { list ->
                    handleOverview(list)
                }
                listTaskCmp.observe(viewLifecycleOwner) {
                    initDataLineChart(it)
                }
                listTaskCmp7Day.observe(viewLifecycleOwner) {
                    handleListComplete(it)
                }
                listTaskPending.observe(viewLifecycleOwner) {
                    initDataPieChart(it)
                }
                getAllTasks()
                getDayOfWeek()
                getTaskSevenDay()
                handlePieChart(KEY_ONE_WEEK)
            }
        }
        initBroadcast()
    }

    private fun initBroadcast() {
        val intentFilter = IntentFilter().apply {
            addAction(SettingActivity.ACTION_CHANGE_DATE_TIME_FORMAT)
        }

        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(
            receiver,
            intentFilter
        )
    }
    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(receiver)
        super.onDestroy()
    }
    private fun handleListComplete(list: List<EventTaskEntity>?) {
        viewBinding.rcvTaskNext.run {
            layoutManager =
                LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL,
                    false
                )
            adapter = sevenDayAdapter
            sevenDayAdapter.submitList(list)
        }
    }

    private fun handleOverview(list: List<EventTaskEntity>?) {
        list?.let {
            viewBinding.tvCountComplete.text = it.count { item ->
                item.isCompleted
            }.toString()
            viewBinding.tvCountPending.text = it.count { item ->
                !item.isCompleted
            }.toString()
        }
    }

    private fun initDataLineChart(map: SortedMap<LocalDateTime, Int>?) {
        val format =
            if (AppPrefs.getOptionDay() == BundleKey.DAY_MONTH_YEAR)
                DateTimeFormat.DATE_TIME_FORMAT_15
            else
                DateTimeFormat.DATE_TIME_FORMAT_11
        map?.let { sortedMap ->
            viewBinding.tvDateChart.text = String.format(
                "%s-%s",
                sortedMap.firstKey()
                    .formatLocalDateTime(format),
                sortedMap.lastKey()
                    .formatLocalDateTime(format),
            )
            initChart(sortedMap)
        }
    }

    private fun initDataPieChart(pair: Pair<List<EventTaskEntity>, List<CategoryEntity>>) {
        val listEntry = arrayListOf<PieEntry>()
        val otherTaskSize = mutableListOf<Float>()
        pair.first.groupBy { it.categoryId }
            .toList()
            .sortedByDescending { it.second.size }
            .forEachIndexed { index, (categoryId, taskList) ->
                if (index < 5) {
                    listEntry.add(
                        PieEntry(
                            taskList.size.toFloat(),
                            pair.second.find { it.id == categoryId }?.name ?: requireContext().getString(R.string.no_category)
                        )
                    )
                } else {
                    otherTaskSize.add(taskList.size.toFloat())
                }
            }
        if (otherTaskSize.isNotEmpty()) {
            val otherSize = otherTaskSize.sum()
            listEntry.add(PieEntry(otherSize, "Other"))
        }
        if (listEntry.size > 0) {
            viewBinding.pieChart.visible()
        } else {
            viewBinding.pieChart.gone()
        }
        initPieChart(listEntry)
        handleListLabel(listEntry)
    }

    private fun handleListLabel(listEntry: ArrayList<PieEntry>) {
        viewBinding.apply {
            rcvPieLabel.run {
                layoutManager =
                    LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                adapter = pieAdapter
                pieAdapter.submitList(listEntry)
            }
        }
    }

    private fun initPieChart(list: List<PieEntry>?) {
        viewBinding.pieChart.apply {
            description.text = ""
            setTouchEnabled(false)
            setDrawEntryLabels(false)
            transparentCircleRadius = 0F
            val dataSet = PieDataSet(list, "")
            dataSet.colors = COLOR_PIE_CHART.toList()
            dataSet.setDrawValues(false)
            val pieData = PieData(dataSet)
            legend.isEnabled = false
            data = pieData
            invalidate()
        }
    }

    private fun initChart(sortedMap: SortedMap<LocalDateTime, Int>?) {
        viewBinding.apply {
            viewModel.run {
                sortedMap?.let { map ->
                    val entries = mutableListOf<Entry>()
                    map.entries.forEachIndexed { index, entry ->
                        val (_, numberOfCompletedTasks) = entry
                        entries.add(Entry(index.toFloat(), numberOfCompletedTasks.toFloat()))
                    }
                    val lineDataSet = LineDataSet(entries, "Completed Data Set").apply {
                        mode = LineDataSet.Mode.HORIZONTAL_BEZIER
                        cubicIntensity = 0.5f
                        color = ContextCompat.getColor(requireContext(), R.color.color_line_chart)
                        lineWidth = 2f
                        setDrawCircles(false)
                        getEntryForIndex(map.size - 1).icon = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_oval_line_chart
                        )
                        setDrawValues(false)
                        setDrawHorizontalHighlightIndicator(false)
                        setDrawVerticalHighlightIndicator(false)
                    }

                    val dataSets = mutableListOf<ILineDataSet>()
                    dataSets.add(lineDataSet)

                    lineChart.apply {
                        data = LineData(dataSets)
                        setDrawGridBackground(true)
                        setGridBackgroundColor(Color.TRANSPARENT)
                        isDragEnabled = false
                        isDoubleTapToZoomEnabled = false
                        setPinchZoom(false)
                        setScaleEnabled(false)
                        legend.isEnabled = false
                        description = null
                        setBackgroundColor(Color.TRANSPARENT)
                        setExtraOffsets(0F, 0F, 0F, 10F)
                        xAxis.apply {
                            position = XAxis.XAxisPosition.BOTTOM
                            gridColor =
                                ContextCompat.getColor(requireContext(), R.color.axis_x_line)
                            textColor = ContextCompat.getColor(requireContext(), R.color.grey_1)
                            textSize = 13F
                            isEnabled = true
                            axisLineColor = Color.TRANSPARENT
                            setDrawAxisLine(true)
                            valueFormatter = object : IndexAxisValueFormatter() {
                                override fun getFormattedValue(value: Float): String {
                                    return listDayOfWeek[value.toInt()]
                                }
                            }
                        }
                        axisRight.isEnabled = false
                        axisLeft.apply {
                            isEnabled = true
                            setDrawAxisLine(false)
                            gridColor = Color.TRANSPARENT
                            textColor =
                                ContextCompat.getColor(requireContext(), R.color.text_y_line_chart)
                            axisMinimum = -0.2F
                            granularity = if (map.values.all { it == 0 }) 4F else 1F
                        }
                    }
                    lineChart.invalidate()
                }
            }
        }
    }

    private fun setAction() {
        viewBinding.apply {
            viewModel.run {
                root.setOnDebounceClickListener {
                    cardInfo.gone()
                }
                ivInfo.setOnDebounceClickListener {
                    if (cardInfo.visibility == View.VISIBLE) cardInfo.gone() else cardInfo.visible()
                }
                ivArrowLeft.setOnDebounceClickListener {
                    ivArrowRight.visible()
                    getDayOfWeek(KEY_BACK)
                }
                ivArrowRight.setOnDebounceClickListener {
                    getDayOfWeek(KEY_NEXT)
                    if (numWeek == 0) ivArrowRight.invisible()
                }
                ivArrowDown.setOnDebounceClickListener { view ->
                    view.parent?.let {
                        showPieChartDialog(it,view)
                    }
                }
            }
        }
    }

    private fun showPieChartDialog(parent: ViewParent, view: View) {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val screenHeight =
            getViewPointOnScr(requireActivity().windowManager).y
        val disToBottom = screenHeight - (location[1] + view.height)
        showPeriodMenu(
            context = requireContext(),
            anchorView = view,
            parent = parent,
            disToBottom = disToBottom,
            onClickMenu = { str ->
                viewBinding.tvPeriod.text = requireContext().getString(
                    when (str) {
                        KEY_ONE_WEEK -> (R.string.in_7_days)
                        KEY_ONE_MONTH -> (R.string.in_30_days)
                        else -> (R.string.all)
                    }
                )
                viewModel.handlePieChart(str)
            }
        )
    }
}