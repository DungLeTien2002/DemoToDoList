package com.example.demotodolist.presentation.fragments


import android.app.AlarmManager
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.demotodolist.R
import com.example.demotodolist.base.Base_View.BaseFragment
import com.example.demotodolist.base.Utils.Util.Constant
import com.example.demotodolist.base.Utils.Util.DateToString
import com.example.demotodolist.data.model.CategoryInfo
import com.example.demotodolist.data.model.TaskCategoryInfo
import com.example.demotodolist.data.model.TaskInfo
import com.example.demotodolist.databinding.CategoryDialogBinding
import com.example.demotodolist.databinding.FragmentNewTaskBinding
import com.example.demotodolist.presentation.MainActivity
import com.example.demotodolist.presentation.MainActivityViewModel
import com.example.demotodolist.presentation.br.AlarmReceiver
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat.CLOCK_24H
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.Objects
import java.util.Random


class NewTaskFragment : BaseFragment<FragmentNewTaskBinding>() {
    // region variable
    private var taskInfo = TaskInfo(0, "", Date(Constant.MAX_TIMESTAMP), 0, false, "")
    private var categoryInfo = CategoryInfo("", "#0000000")
    private var colorString = "#000000"
    private var isCategorySelected = false
    private lateinit var colorView: View
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var prevTaskCategory: TaskCategoryInfo
    private lateinit var navController: NavController
    private val args: NewTaskFragmentArgs by navArgs()

    //endregion

    override fun initData() {

    }

    override fun initListener() {
        binding.dateAndTimePicker.setOnClickListener {
            showDateTimePicker()
        }

        binding.isCompleted.setOnCheckedChangeListener { _, it -> taskInfo.status = it }

        binding.fab.setOnClickListener {
            addTask()
        }

        binding.priorityChipGroup.setOnCheckedStateChangeListener { chipGroup, i ->
            changePriority(chipGroup, i)
        }

        binding.categoryChipGroup.setOnCheckedStateChangeListener { chipGroup, i ->
            listenToCategoryClick(chipGroup, i)
        }
    }

    override fun initView() {
        viewModel = (activity as MainActivity).viewModel
        navController = findNavController()
        createNotification()
        if (args.newTaskArg != null) initUpdate()
        setInitialValues()
        loadAllCategories()
    }

    private fun initUpdate() {
        taskInfo = args.newTaskArg!!.taskInfo
        categoryInfo = args.newTaskArg!!.categoryInfo[0]
        binding.fab.text = "Update"
        colorString = categoryInfo.color
        prevTaskCategory = TaskCategoryInfo(
            TaskInfo(
                taskInfo.id,
                taskInfo.description,
                taskInfo.date,
                taskInfo.priority,
                taskInfo.status,
                taskInfo.category
            ),
            listOf(CategoryInfo(categoryInfo.categoryInformation, categoryInfo.color))
        )
        isCategorySelected = true
    }

    private fun loadAllCategories() {
        viewModel.getCategories().observe(viewLifecycleOwner) {
            for (category in it) {
                val chip = Chip(context)
                chip.text = category.categoryInformation
                val drawable = ChipDrawable.createFromAttributes(
                    requireContext(),
                    null,
                    0,
                    com.google.android.material.R.style.Widget_MaterialComponents_Chip_Choice
                )
                chip.setChipDrawable(drawable)
                chip.tag = category.color
                chip.isChecked = chip.text == taskInfo.category
                binding.categoryChipGroup.addView(chip)
            }
        }
    }

    private fun setInitialValues() {
        var str = DateToString.convertDateToString(taskInfo.date)
        if (str == "N/A") str == "Due Date"
        binding.apply {
            editText.setText(taskInfo.description)
            dateAndTimePicker.text = str
            isCompleted.isChecked = taskInfo.status
            when (taskInfo.priority) {
                0 -> low.isChecked = true
                1 -> mid.isChecked = true
                else -> high.isChecked = true
            }
        }
    }

    override fun inflateLayout(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentNewTaskBinding {
        return FragmentNewTaskBinding.inflate(layoutInflater, container, false)
    }

    private fun listenToCategoryClick(chipGroup: ChipGroup, i: List<Int>) {
        val id = i[0]
        val chip = chipGroup.findViewById(id) as Chip
        if (chip.text.toString() == "+ Add New Category") {
            displayCategoryChooseDialog()
            isCategorySelected = false
        } else {
            taskInfo.category = chip.text.toString()
            categoryInfo.categoryInformation = chip.text.toString()
            categoryInfo.color = chip.tag.toString()
            colorString = categoryInfo.color
            isCategorySelected = true
        }
    }

    private fun displayCategoryChooseDialog() {
        colorString = generateRandomColor()
        val dialog = Dialog(requireContext())
        val dialogBinding = CategoryDialogBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        colorView = dialogBinding.viewColor
        dialogBinding.viewColor.setBackgroundColor(Color.parseColor(colorString))
        dialogBinding.addColor.setOnClickListener {
            displayColorPickerDialog()
        }
        dialogBinding.addCategory.setOnClickListener {
            if (dialogBinding.editText.text.isNullOrBlank()) showSnackBar(getString(R.string.please_add_category))
            else {
                addNewCategoryChip(dialogBinding.editText.text.toString())
            }
            dialog.dismiss()
        }

        Objects.requireNonNull(dialog.window)
            ?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun addNewCategoryChip(category: String) {
        val chip = Chip(context)
        val drawable = ChipDrawable.createFromAttributes(
            requireContext(),
            null,
            0,
            com.google.android.material.R.style.Widget_MaterialComponents_Chip_Choice
        )
        chip.apply {
            setChipDrawable(drawable)
            text = category
            isCheckable = true
            isChecked = true
            tag = colorString
        }
        taskInfo.category = chip.text.toString()
        categoryInfo.categoryInformation = chip.text.toString()
        categoryInfo.color = colorString
        binding.categoryChipGroup.addView(chip)
        isCategorySelected = true
    }

    private fun displayColorPickerDialog() {
        ColorPickerDialogBuilder.with(context).setTitle("Choose color")
            .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE).density(12)
            .setOnColorSelectedListener { selectedColor ->
                colorString = "#" + Integer.toHexString(selectedColor)
            }.setPositiveButton("OK") { _, _, _ ->
                colorView.setBackgroundColor(Color.parseColor(colorString))
            }.setNegativeButton("Cancel") { _, _ ->
                colorString = "#000000"
            }.build().show()
    }

    private fun generateRandomColor(): String {
        val random = Random()
        val color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
        return "#" + Integer.toHexString(color)
    }

    private fun showDateTimePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker().build()
        val timePicker = MaterialTimePicker.Builder().setTimeFormat(CLOCK_24H).build()
        datePicker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            taskInfo.date = calendar.time
            binding.dateAndTimePicker.text = DateToString.convertDateToString(taskInfo.date)
            timePicker.show(childFragmentManager, Constant.TAG)
        }

        timePicker.addOnPositiveButtonClickListener {
            val cal = Calendar.getInstance()
            cal.time = taskInfo.date
            cal.set(Calendar.HOUR_OF_DAY, timePicker.hour)
            cal.set(Calendar.MINUTE, timePicker.minute)
            cal.set(Calendar.SECOND, 5)
            taskInfo.date = cal.time
            binding.dateAndTimePicker.text = DateToString.convertDateToString(taskInfo.date)
        }
        datePicker.show(childFragmentManager, Constant.TAG)
    }


    private fun addTask() {
        val date = Date()
        taskInfo.description = binding.editText.text.toString()
        if (taskInfo.description.isNullOrBlank()) showSnackBar(getString(R.string.please_add_description))
        else if (taskInfo.category.isNullOrBlank() || categoryInfo.categoryInformation.isNullOrBlank() || !isCategorySelected) showSnackBar(
            getString(R.string.please_select_a_category)
        )
        else {
            if (binding.fab.text.equals("Update")) {
                updateTask()
            } else {
                val diff = (Date().time / 1000) - Constant.sDate
                taskInfo.id = diff.toInt()
                viewModel.insertTaskAndCategory(taskInfo, categoryInfo)
                if (!taskInfo.status && taskInfo.date > date && taskInfo.date.seconds == 5)
                    setAlarm(taskInfo)
            }
            navController.popBackStack()
        }
    }

    private fun setAlarm(taskInfo: TaskInfo) {
        val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        intent.putExtra("task_info", taskInfo)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            taskInfo.id,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val mainActivityIntent = Intent(requireContext(), MainActivity::class.java)
        val basicPendingIntent = PendingIntent.getActivity(
            requireContext(),
            taskInfo.id,
            mainActivityIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val clockInfo = AlarmManager.AlarmClockInfo(taskInfo.date.time, basicPendingIntent)
        alarmManager.setAlarmClock(clockInfo, pendingIntent)
    }

    private fun createNotification() {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel =
            NotificationChannel("demo_to_do_list", "Tasks Notification Channel", importance).apply {
                description = "Notification for Tasks"
            }
        val notificationManager =
            activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun updateTask() {
        val date = Date()
        if (taskInfo.category == prevTaskCategory.taskInfo.category)
            viewModel.updateTaskAndAddCategory(taskInfo, categoryInfo)
        else {
            CoroutineScope(Dispatchers.Main).launch {
                if (viewModel.getCountOfCategory(prevTaskCategory.taskInfo.category) == 1) {
                    viewModel.updateTaskAndAddDeleteCategory(
                        taskInfo,
                        categoryInfo,
                        prevTaskCategory.categoryInfo[0]
                    )
                } else {
                    viewModel.updateTaskAndAddCategory(taskInfo, categoryInfo)
                }
            }
        }

        if (!taskInfo.status && taskInfo.date > date && taskInfo.date.seconds == 5)
            setAlarm(taskInfo)
        else removeAlarm(taskInfo)
    }

    private fun removeAlarm(taskInfo: TaskInfo) {
        val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        intent.putExtra("task_info", taskInfo)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            taskInfo.id,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    private fun showSnackBar(s: String) {
        Snackbar.make(
            binding.root, s.toString(), Snackbar.LENGTH_SHORT
        ).setAction("Action", null).show()
    }

    private fun changePriority(chipGroup: ChipGroup, i: List<Int>) {
        val id = i[0]
        val chip = chipGroup.findViewById(id) as Chip

        when (chip.text) {
            "Low" -> taskInfo.priority = 0
            "Medium" -> taskInfo.priority = 1
            else -> taskInfo.priority = 2
        }
    }
}