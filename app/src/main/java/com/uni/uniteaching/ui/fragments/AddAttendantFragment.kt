package com.uni.uniteaching.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.uni.uniteaching.R
import com.uni.uniteaching.adapters.ScheduleAdapter
import com.uni.uniteaching.adapters.StudentAdapter
import com.uni.uniteaching.classes.Attendance
import com.uni.uniteaching.classes.Lecture
import com.uni.uniteaching.classes.Section
import com.uni.uniteaching.classes.user.UserStudent
import com.uni.uniteaching.data.Resource
import com.uni.uniteaching.databinding.FragmentAddAttendantBinding
import com.uni.uniteaching.databinding.FragmentViewAttendanceBinding
import com.uni.uniteaching.ui.HomeScreen
import com.uni.uniteaching.viewModel.FirebaseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AddAttendantFragment : Fragment() {
lateinit var binding: FragmentAddAttendantBinding
var grade=""
    var departement=""
    var section=""
    lateinit var bundle:Bundle
    lateinit var adapter :StudentAdapter
    lateinit var studentList: MutableList<UserStudent>
    private val viewModel: FirebaseViewModel by viewModels()
    var  courseCode=""
    var dep=  ""
    var sec=""
    var id =""



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bundle = Bundle()
        val args= this.arguments
        if (args != null) {
            courseCode = args.getString("course","")
            dep = args.getString("dep","")
            sec = args.getString("section","")
            id = args.getString("id","")
        }
        studentList= arrayListOf()
        binding = FragmentAddAttendantBinding.inflate(layoutInflater)
        setGradeSpinner()
        setSectionSpinner()
        setDepartementSpinner()



        adapter= StudentAdapter(requireContext(),studentList,
            addPerm = {pos,item->
                Log.e("add at","I am here")
                if (sec=="no"){
                    viewModel.addLectureAttendance(
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
                        false), Attendance(
                            "",
                                    item.name
                        ,
                            item.code

                        )
                    )
                }else{
                    viewModel.addSectionAttendance(  Section(
                        id,
                        courseCode,
                        "",
                        "",
                        "",
                        sec,
                        dep,
                        "",
                        "",
                        "",
                        false), Attendance(
                        "",
                        item.name
                        ,
                        item.code

                    ))
                }
                observeAddedAttendance()
            }
        )
        binding.searchStudent.setOnClickListener {
            Log.e("add","I am here")
            val studentID=binding.userIDAttendance.text.toString()
            Log.e("add id id ",studentID)
            if (grade.isNotEmpty()){
                if (departement=="any departement"&&section=="any section"){
                    if (studentID.isNotEmpty()){
                        Log.e("add","I am here2")
                        viewModel.searchStudentByID(grade,studentID)

                    }else{
                        Log.e("add","I am here3")
                        viewModel.searchStudentAll(grade)
                    }
                }else if(departement!="any departement"&&section!="any section"){
                    Log.e("add","I am here4")
                    viewModel.searchStudentBySection(grade,departement,section)
                }else if(departement!="any departement"&&section=="any section"){
                    Log.e("add","I am here5")
                    viewModel.searchStudentByDepartment(grade,departement)
                }else{
                    Log.e("add","I am here6")
                    Toast.makeText(context,"more data needed",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(context,"make sure to choose grade",Toast.LENGTH_SHORT).show()
            }
            binding.searchStudentRecycler.layoutManager = LinearLayoutManager(requireContext())
            binding.searchStudentRecycler.adapter = adapter
        observeStudentByID()
            observeStudent()
        }
        return binding.root
    }

    private fun observeStudent() {
        lifecycleScope.launchWhenCreated {
            viewModel.searchStudentAll.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {
                        studentList.clear()
                        state.result.forEach {
                            studentList.add(it)
                        }

                        adapter.update(studentList)
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

    private fun observeStudentByID() {
        lifecycleScope.launchWhenCreated {
            viewModel.searchStudentByID.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {
                        studentList.clear()
                        val student =state.result
                        studentList.add(student)
                        Log.e("student",student.name)
                        adapter.update(studentList)
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
    private fun observeAddedAttendance() {
        lifecycleScope.launchWhenCreated {
            viewModel.addAttendance.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {
                        Toast.makeText(context, state.result, Toast.LENGTH_LONG)
                            .show()
/*
                        if (sec!="no") {
                            bundle.putString("course", courseCode)
                            bundle.putString("dep", dep)
                            bundle.putString("section", sec)
                            bundle.putString("id", id)
                        } else {
                            bundle.putString("course", courseCode)
                            bundle.putString("dep", dep)
                            bundle.putString("section", "no")
                            bundle.putString("id", id)
                        }
                        (activity as HomeScreen).replaceFragment(ViewAttendanceFragment())*/
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
    private fun setGradeSpinner() {
        val gradeList = resources.getStringArray(R.array.grades)
        val adapter: ArrayAdapter<CharSequence> = context?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.grades,
                R.layout.spinner_item
            )
        } as ArrayAdapter<CharSequence>
        val gradeSpinner = binding.gradeSpinner
        gradeSpinner.adapter = adapter

        gradeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                grade = gradeList[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    private fun setDepartementSpinner() {

        val departementList = resources.getStringArray(R.array.departement2)
        val adapter: ArrayAdapter<CharSequence> = context?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.departement2,
                R.layout.spinner_item
            )
        } as ArrayAdapter<CharSequence>
        val gradeSpinner = binding.departementSpinner
        gradeSpinner.adapter = adapter

        gradeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                departement = departementList[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    private fun setSectionSpinner() {

        val sectionList = resources.getStringArray(R.array.Section2)
        val adapter: ArrayAdapter<CharSequence> = context?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.Section2,
                R.layout.spinner_item
            )
        } as ArrayAdapter<CharSequence>
        val gradeSpinner = binding.sectionSpinner
        gradeSpinner.adapter = adapter

        gradeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                section = sectionList[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }
}