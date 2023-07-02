package com.uni.uniteaching.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.uni.uniteaching.adapters.ScheduleAdapter
import com.uni.uniteaching.classes.Courses
import com.uni.uniteaching.classes.ScheduleDataType
import com.uni.uniteaching.classes.user.UserTeaching
import com.uni.uniteaching.data.Resource
import com.uni.uniteaching.data.di.UserTypes
import com.uni.uniteaching.databinding.FragmentScheduleBinding
import com.uni.uniteaching.ui.HomeScreen
import com.uni.uniteaching.ui.Scan
import com.uni.uniteaching.viewModel.AuthViewModel
import com.uni.uniteaching.viewModel.FirebaseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ScheduleFragment : Fragment() {
    private lateinit var binding: FragmentScheduleBinding
    private val viewModel: FirebaseViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var progress: ProgressBar
    private lateinit var currentUser: UserTeaching
    private lateinit var coursesList: MutableList<Courses>
    private lateinit var adapter: ScheduleAdapter
    private lateinit var scheduleList: MutableList<ScheduleDataType>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScheduleBinding.inflate(layoutInflater)
        val bundle = Bundle()
        coursesList = arrayListOf()
        scheduleList = arrayListOf()
        currentUser = UserTeaching()
        authViewModel.getSessionStudent { user ->
            if (user != null) {
                currentUser = user
            } else {
                Toast.makeText(
                    context,
                    "there is an error on loading user data",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        progress = binding.progressSchedule


        adapter = ScheduleAdapter(requireContext(), scheduleList,
            onItemClick = {item->
                if (item.type == ScheduleAdapter.VIEW_TYPE_ONE) {
                bundle.putString("course", item.courseID)
                bundle.putString("dep", item.dep)
                bundle.putString("section", item.section)
                bundle.putString("id", item.eventId)
                } else {
                    bundle.putString("course", item.courseID)
                    bundle.putString("dep", item.dep)
                    bundle.putString("section", "no")
                    bundle.putString("id", item.eventId)
                }
                val viewAttendanceFragment = ViewAttendanceFragment()
                viewAttendanceFragment.arguments = bundle
                (activity as HomeScreen).replaceFragment(viewAttendanceFragment)
                }
            ,

            onAttendClicked = {  item ->
val intent= Intent(requireContext(), Scan::class.java)
                if (item.type == ScheduleAdapter.VIEW_TYPE_ONE) {
                    intent.putExtra("course", item.courseID)
                    intent.putExtra("dep", item.dep)
                    intent.putExtra("section", item.section)
                    intent.putExtra("id", item.eventId)

                } else {


                    intent.putExtra("course", item.courseID)
                    intent.putExtra("dep", item.dep)
                    intent.putExtra("section", "no")
                    intent.putExtra("id", item.eventId)

                }
                startActivity(intent)
            })

        binding.recyclerSchedule.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerSchedule.adapter = adapter

        if (currentUser.userType== UserTypes.assistantUser){
            viewModel.getCoursesByAssistantID(currentUser.code)
        }else{
            viewModel.getCoursesByProfessorID(currentUser.code)
        }


        observeCourses()

        return binding.root
    }
   /* fun setListener(dataPasser1: DataPasser){
        dataPasser = dataPasser1
    }*/
    private fun observeCourses() {
        var sectionList=(activity as HomeScreen).sectionList
       sectionList.forEach {
           Log.e("schedhdh",it.sectionName)
       }
        lifecycleScope.launchWhenCreated {
            viewModel.getCourse.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        coursesList.clear()
                        it.result.forEach {
                            coursesList.add(it)
                        }
                        if (currentUser.userType==UserTypes.assistantUser){

                            viewModel.getSection(sectionList)
                            delay(300)
                            observeSection()

                        }else{
                            viewModel.getLecture(coursesList,currentUser.department)
                            delay(300)
                            observeLecture()
                        }

                    }
                    is Resource.Failure -> {
                        Toast.makeText(requireContext(),it.exception, Toast.LENGTH_SHORT).show()
                    }
                    else->{}
                }
            }
        }
    }

    private fun observeLecture() {
        lifecycleScope.launchWhenCreated {
            viewModel.getLecture.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        scheduleList.clear()
                        it.result.forEach {
                            val item=ScheduleDataType(
                                it.lectureId
                                ,it.courseName
                                ,it.courseCode
                                ,it.hallID
                                ,""
                                ,it.dep
                                ,it.professorName
                                ,it.day
                                ,it.time
                                ,it.endTime
                                ,ScheduleAdapter.VIEW_TYPE_ONE
                                ,false)
                            scheduleList.add(item)
                        }
                        adapter.update(scheduleList)
                    }
                    is Resource.Failure -> {
                        Toast.makeText(requireContext(),it.exception, Toast.LENGTH_SHORT).show()
                    }
                    else->{}
                }
            }
        }
    }

    private fun observeSection() {
        lifecycleScope.launchWhenCreated {
            viewModel.getSection.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        scheduleList.clear()
                        it.result.forEach {
                            val item=ScheduleDataType(
                                it.sectionId
                                ,it.courseName
                                ,it.courseCode
                                ,it.lapID
                                ,it.section
                                ,it.dep
                                ,it.assistantName
                                ,it.day
                                ,it.time
                                ,it.endTime
                                ,ScheduleAdapter.VIEW_TYPE_ONE
                                ,false)
                            scheduleList.add(item)
                        }
adapter.update(scheduleList)
                    }
                    is Resource.Failure -> {
                        Toast.makeText(requireContext(),it.exception, Toast.LENGTH_SHORT).show()
                    }
                    else->{}
                }
            }
        }
    }


}
