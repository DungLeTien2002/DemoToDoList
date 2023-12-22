package com.example.demotodolist.presentation.fragments


import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.demotodolist.base.Base_View.BaseFragment
import com.example.demotodolist.data.model.CategoryInfo
import com.example.demotodolist.data.model.TaskCategoryInfo
import com.example.demotodolist.data.model.TaskInfo
import com.example.demotodolist.databinding.FragmentHomeBinding
import com.example.demotodolist.presentation.MainActivity
import com.example.demotodolist.presentation.MainActivityViewModel
import com.example.demotodolist.presentation.adapter.CategoryAdapter
import com.example.demotodolist.presentation.adapter.TasksAdapter
import com.example.demotodolist.presentation.br.AlarmReceiver
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date


class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    //region variable
    private val viewModel by lazy {
        (activity as MainActivity).viewModel
    }
    private val adapter by lazy {
        TasksAdapter()
    }
    private val adapter2 by lazy {
        CategoryAdapter()
    }

    override fun initData() {
        initRecyclerView1()
        initRecyclerView2()
        viewModel.getUncompletedTask().observe(viewLifecycleOwner, Observer {
            if (it.isEmpty()) binding.noResultAnimationView.visibility = View.VISIBLE
            else binding.noResultAnimationView.visibility = View.GONE
            adapter.differ.submitList(it)
        })
        viewModel.getNoOfTaskForEachCategory().observe(viewLifecycleOwner, Observer {
            if (it.isEmpty()) binding.categoryAnimationView.visibility = View.VISIBLE
            else binding.categoryAnimationView.visibility = View.GONE
            adapter2.differ.submitList(it)
        })
    }

    override fun initListener() {
        binding.fab.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToNewTaskFragment(null)
            findNavController().navigate(action)
            //(activity as MainActivity).gone()
           // Log.e("aaaa","AAAAAAAAA")
        }
        adapter.setOnItemClickListener {
            editTaskInformation(it)
        }
        adapter.setOnTaskStatusChangedListener {
            updateTaskStatus(viewModel, it)
        }
        adapter2.setOnItemClickListener {
            goToTaskCategoryFragment(it)
        }

        val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val taskInfo = adapter.differ.currentList[position]?.taskInfo
                    val categoryInfo = adapter.differ.currentList[position]?.categoryInfo?.get(0)
                    if (taskInfo != null && categoryInfo != null) {
                        deleteTask(viewModel, taskInfo, categoryInfo)
                        Snackbar.make(binding.root, "Deleted Successfully", Snackbar.LENGTH_LONG)
                            .apply {
                                setAction("Undo") {
                                    viewModel.insertTaskAndCategory(taskInfo, categoryInfo)
                                }
                                show()
                            }
                    }
                }
            }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.tasksRecyclerView)
    }

    private fun deleteTask(
        viewModel: MainActivityViewModel,
        taskInfo: TaskInfo,
        categoryInfo: CategoryInfo
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            if(viewModel.getCountOfCategory(categoryInfo.categoryInformation)==1) {
                viewModel.deleteTaskAndCategory(taskInfo, categoryInfo)
            }else {
                viewModel.deleteTask(taskInfo)
            }
            val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(requireContext(), AlarmReceiver::class.java)
            intent.putExtra("task_info", taskInfo)
            val pendingIntent = PendingIntent.getBroadcast(requireContext(), taskInfo.id, intent, PendingIntent.FLAG_IMMUTABLE)
            alarmManager.cancel(pendingIntent)
        }
    }

    private fun initRecyclerView1() {
        binding.categoriesRecyclerView.adapter = adapter2
        binding.categoriesRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun initRecyclerView2() {
        binding.tasksRecyclerView.adapter = adapter
        binding.tasksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun goToTaskCategoryFragment(category: String) {
        val action = HomeFragmentDirections.actionHomeFragmentToTaskCategoryFragment(category)
        findNavController().navigate(action)
    }

    private fun updateTaskStatus(viewModel: MainActivityViewModel, taskInfo: TaskInfo) {
        viewModel.updateTaskStatus(taskInfo)
        lifecycleScope.launch(Dispatchers.IO) {
            val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(requireContext(), AlarmReceiver::class.java)
            intent.putExtra("task_info", taskInfo)
            val pendingIntent = PendingIntent.getBroadcast(requireContext(), taskInfo.id, intent, PendingIntent.FLAG_IMMUTABLE)

            if(taskInfo.status){
                alarmManager.cancel(pendingIntent)
            }else {
                val date = Date()
                if(taskInfo.date > date && taskInfo.date.seconds == 5){
                    val mainActivityIntent = Intent(requireContext(), MainActivity::class.java)
                    val basicPendingIntent = PendingIntent.getActivity(requireContext(), taskInfo.id, mainActivityIntent, PendingIntent.FLAG_IMMUTABLE)
                    val clockInfo = AlarmManager.AlarmClockInfo(taskInfo.date.time, basicPendingIntent)
                    alarmManager.setAlarmClock(clockInfo, pendingIntent)
                }
            }
        }
    }

    private fun editTaskInformation(taskCategoryInfo: TaskCategoryInfo) {
        val action = HomeFragmentDirections.actionHomeFragmentToNewTaskFragment(taskCategoryInfo)
        findNavController().navigate(action)
    }

    override fun initView() {

    }

    override fun inflateLayout(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding? {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }
}