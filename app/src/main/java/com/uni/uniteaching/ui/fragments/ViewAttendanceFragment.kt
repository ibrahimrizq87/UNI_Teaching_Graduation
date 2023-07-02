package com.uni.uniteaching.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.uni.uniteaching.R
import com.uni.uniteaching.adapters.AttendanceAdapter
import com.uni.uniteaching.adapters.ScheduleAdapter
import com.uni.uniteaching.classes.Attendance
import com.uni.uniteaching.classes.Lecture
import com.uni.uniteaching.classes.Section
import com.uni.uniteaching.classes.user.UserTeaching
import com.uni.uniteaching.data.Resource
import com.uni.uniteaching.databinding.FragmentScheduleBinding
import com.uni.uniteaching.databinding.FragmentViewAttendanceBinding
import com.uni.uniteaching.ui.HomeScreen
import com.uni.uniteaching.viewModel.AuthViewModel
import com.uni.uniteaching.viewModel.FirebaseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class ViewAttendanceFragment : Fragment() {
    lateinit var binding:FragmentViewAttendanceBinding
    private val viewModel: FirebaseViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    lateinit var currentUser:UserTeaching
    lateinit var attendanceList: MutableList<Attendance>
    lateinit var adapter:AttendanceAdapter

    var  courseCode=""
    var dep=  ""
    var section=""
    var id =""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val bundle = Bundle()
        attendanceList= arrayListOf()

        authViewModel.getSessionStudent {user->
            if (user !=null){
                currentUser=user
                Toast.makeText(context,currentUser.name, Toast.LENGTH_LONG).show()
            }
            else{
                Toast.makeText(context,"error on loading user data please refresh the current screen ", Toast.LENGTH_LONG).show()
            }
        }
        binding = FragmentViewAttendanceBinding.inflate(layoutInflater)

        adapter= AttendanceAdapter(requireContext(),attendanceList, delete = { item ->

          if (section == "no"){
viewModel.deleteLectureAttendance(Lecture(
    id,
    courseCode,
    "",
    "",
    dep,
    "",
    "",
    "",
    "",
    false),item)
            }else{
                viewModel.deleteSectionAttendance(  Section(
                    id,
                    courseCode,
                    "",
                    "",
                    "",
                    section,
                    dep,
                    "",
                    "",
                    "",
                    false),item)
            }
            observeDelete()
        })
        binding.addAttendance.setOnClickListener {
            if (section!="no") {
                bundle.putString("course", courseCode)
                bundle.putString("dep", dep)
                bundle.putString("section", section)
                bundle.putString("id", id)
            } else {
                bundle.putString("course", courseCode)
                bundle.putString("dep", dep)
                bundle.putString("section", "no")
                bundle.putString("id", id)
            }
            val addAttendantFragment = AddAttendantFragment()
            addAttendantFragment.arguments = bundle
            (activity as HomeScreen).replaceFragment(addAttendantFragment)


        }


        val args= this.arguments
        if (args != null) {
            courseCode = args.getString("course","")
            dep = args.getString("dep","")
            section = args.getString("section","")
            id = args.getString("id","")
            Log.e("attendance",courseCode)
            Log.e("attendance",dep)
            Log.e("attendance",section)
            Log.e("attendance",id)

        }
        binding.recyclerAttendance.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerAttendance.adapter = adapter

        if (section == "no"){
             viewModel.getLectureAttendance(
                 Lecture(
                     id,
                     courseCode,
                     "",
                     "",
                     dep,
                     "",
                     "",
                     "",
                     "",
                     false)
             )
        }else{
            viewModel.getSectionAttendance(
                Section(
                    id,
                    courseCode,
                    "",
                    "",
                    "",
                    section,
                    dep,
                    "",
                    "",
                    "",
                    false)
            )
        }
        observeAttendance()
        return binding.root
    }

    private fun observeDelete() {
        lifecycleScope.launchWhenCreated {
            viewModel.deleteAttendance.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {
                        Toast.makeText(context, state.result, Toast.LENGTH_LONG)
                            .show()
                    }
                    is Resource.Failure -> {
                        Toast.makeText(context, state.exception.toString(), Toast.LENGTH_LONG)
                            .show()
                    }
                    else -> {}
                }
            }

        }
    }

    private fun observeAttendance() {
        lifecycleScope.launchWhenCreated {
            viewModel.getAttendance.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {
                        attendanceList.clear()
                         state.result.forEach {
                             Log.e("view ",it.attendanceID)
                             attendanceList.add(it)
                         }
                        adapter.update(attendanceList)
                    }
                    is Resource.Failure -> {
                        Toast.makeText(context, state.exception.toString(), Toast.LENGTH_LONG)
                            .show()
                    }
                    else -> {}
                }
            }

        }
    }

}