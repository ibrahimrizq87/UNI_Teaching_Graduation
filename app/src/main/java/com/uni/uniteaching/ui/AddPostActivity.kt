package com.uni.uniteaching.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uni.uniteaching.adapters.SpinnerItemAdapter
import com.uni.uniteaching.adapters.StudentAdapter
import com.uni.uniteaching.R
import com.uni.uniteaching.adapters.PostsAdapter
import com.uni.uniteaching.classes.Posts
import com.uni.uniteaching.classes.SpinnerItem
import com.uni.uniteaching.classes.user.UserStudent
import com.uni.uniteaching.classes.user.UserTeaching
import com.uni.uniteaching.data.Resource
import com.uni.uniteaching.data.di.PostType
import com.uni.uniteaching.data.di.UserTypes
import com.uni.uniteaching.ui.signUp.SignUp
import com.uni.uniteaching.viewModel.AuthViewModel
import com.uni.uniteaching.viewModel.FireStorageViewModel
import com.uni.uniteaching.viewModel.FirebaseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.util.*

@AndroidEntryPoint
class AddPostActivity : AppCompatActivity() {
    private val viewModelAuth: AuthViewModel by viewModels()
    private val fireStorageViewModel: FireStorageViewModel by viewModels()
    private val viewModel: FirebaseViewModel by viewModels()
    private lateinit var currentUser: UserTeaching
    private lateinit var department: String
    private lateinit var section: String
    private lateinit var course: String
    private lateinit var coursesList: MutableList<SpinnerItem>
    lateinit var recyAdapter: StudentAdapter
    private lateinit var studentsList: MutableList<UserStudent>
    private lateinit var gradeAdapter: ArrayAdapter<CharSequence>
    private lateinit var courseAdapter: SpinnerItemAdapter
    private lateinit var userImageUri: Uri
    private lateinit var imageView: ImageView
    private lateinit var depSpinner: Spinner
    private lateinit var depArrowDown: ImageView
    private lateinit var sectionSpinner: Spinner
    private lateinit var sectionArrowDown: ImageView
    private lateinit var courseSpinner: Spinner
    private lateinit var courseArrowDown: ImageView
    private lateinit var studentIdEdt: EditText
    private lateinit var studentIdBtn: Button
    private lateinit var studentIdRv: RecyclerView

    var grade = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)
        currentUser = UserTeaching()
        viewModelAuth.getSessionStudent { user ->
            if (user != null) {
                currentUser = user
                Log.e("add post", user.name)
            } else {
                Toast.makeText(this, "there is an error on loading user data", Toast.LENGTH_SHORT)
                    .show()
            }

        }
        chooseGrade()
        chooseToWho()
        studentsList = arrayListOf()



        imageView = findViewById(R.id.post_image)


        val postText = findViewById<EditText>(R.id.post_description)

        val addImage = findViewById<ImageButton>(R.id.add_image_post_bt)
        val closeBtn = findViewById<ImageButton>(R.id.close_create_post)
        val addGeneralPost = findViewById<Button>(R.id.add_general_post)

        closeBtn.setOnClickListener {
            finish()
        }
        studentIdEdt = findViewById(R.id.post_student_ID)
        studentIdBtn = findViewById(R.id.search_student_post)
        studentIdRv = findViewById(R.id.post_search_recy)
        courseArrowDown = findViewById(R.id.arrow_down_course)
        depArrowDown = findViewById(R.id.arrow_down1)
        sectionArrowDown = findViewById(R.id.arrow_down2)

        userImageUri = Uri.EMPTY
        coursesList = arrayListOf()
        department = ""
        section = ""
        course = ""
        recyAdapter = StudentAdapter(this, studentsList,

            addPerm = { pos, item ->
                val description = postText.text.toString()
                val id = studentIdEdt.text.toString()

                if (description.isNotEmpty() && id.isNotEmpty() && grade.isNotEmpty()) {
                    if (userImageUri == Uri.EMPTY) {
                        viewModel.addPostPersonal(
                            Posts(
                                description, currentUser.name,
                                currentUser.userId, "",
                                id, Date(), "Personal for $id", PostsAdapter.WITHOUT_IMAGE
                            ), item.userId
                        )
                        observePost(false, Uri.EMPTY)
                    } else {
                        viewModel.addPostPersonal(
                            Posts(
                                description,
                                currentUser.name,
                                currentUser.userId,
                                "",
                                id,
                                Date(),
                                "grade: ${grade} dep: ${department} sec: ${section}",
                                PostsAdapter.WITH_IMAGE
                            ), item.userId
                        )
                        observePost(true, userImageUri)
                    }
                } else {
                    Toast.makeText(this, "make sure to choose all data ", Toast.LENGTH_SHORT).show()
                }
            })

        studentIdRv.layoutManager = LinearLayoutManager(this)
        studentIdRv.adapter = recyAdapter


        studentIdBtn.setOnClickListener {
            val id = studentIdEdt.text.toString()
            if (id.isNotEmpty() && grade.isNotEmpty()) {
                viewModel.searchStudentByID(grade, id)
                observeStudents()
            } else {
                Toast.makeText(this, "make sure to choose all data ", Toast.LENGTH_SHORT).show()

            }
        }

        addGeneralPost.setOnClickListener {

            val description = postText.text.toString()
            if (course.isNotEmpty() && description.isNotEmpty()) {
                if (userImageUri == Uri.EMPTY) {
                    viewModel.addPostCourse(
                        Posts(
                            description, currentUser.name,
                            currentUser.userId, "",
                            course, Date(), PostType.course, PostsAdapter.WITHOUT_IMAGE
                        ), course
                    )
                    observePost(false, Uri.EMPTY)
                } else {
                    viewModel.addPostCourse(
                        Posts(
                            description, currentUser.name,
                            currentUser.userId, "",
                            course, Date(), PostType.course, PostsAdapter.WITH_IMAGE
                        ), course
                    )
                    observePost(true, userImageUri)
                }
            } else if (department.isNotEmpty() && section.isNotEmpty() && description.isNotEmpty()) {
                if (userImageUri == Uri.EMPTY) {
                    Log.e("add post2", currentUser.name)
                    viewModel.addPostSection(
                        Posts(
                            description,
                            currentUser.name,
                            currentUser.userId,
                            "",
                            "${section}/${department}",
                            Date(),
                            PostType.section_posts,
                            PostsAdapter.WITHOUT_IMAGE
                        ), section, department
                    )
                    observePost(false, Uri.EMPTY)
                } else {
                    viewModel.addPostSection(
                        Posts(
                            description, currentUser.name,
                            currentUser.userId, "",
                            "", Date(), PostType.section_posts, PostsAdapter.WITH_IMAGE
                        ), section, department
                    )
                    observePost(true, userImageUri)
                }
            } else if (description.isNotEmpty()) {
                if (userImageUri == Uri.EMPTY) {
                    viewModel.addPostGeneral(
                        Posts(
                            description, currentUser.name,
                            currentUser.userId, "",
                            "", Date(), PostType.general, PostsAdapter.WITHOUT_IMAGE
                        )
                    )
                    observePost(false, Uri.EMPTY)
                } else {
                    viewModel.addPostGeneral(
                        Posts(
                            description, currentUser.name,
                            currentUser.userId, "",
                            "", Date(), PostType.general, PostsAdapter.WITH_IMAGE
                        )
                    )
                    observePost(true, userImageUri)
                }
            } else {
                Toast.makeText(this, "make sure to choose all data ", Toast.LENGTH_SHORT).show()
            }
        }
        addImage.setOnClickListener {
            pickImageFromGallery()
        }


        val departmentList = resources.getStringArray(R.array.departement)
        val depAdapter: ArrayAdapter<CharSequence> =
            ArrayAdapter.createFromResource(this, R.array.departement, R.layout.spinner_item)
        depSpinner = findViewById(R.id.department_spinner_post)
        depSpinner.adapter = depAdapter
        depSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                department = departmentList[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }


        val sectionList = resources.getStringArray(R.array.Section)
        val sectionAdapter: ArrayAdapter<CharSequence> =
            ArrayAdapter.createFromResource(this, R.array.Section, R.layout.spinner_item)
        sectionSpinner = findViewById(R.id.section_spinner_post)
        sectionSpinner.adapter = sectionAdapter
        sectionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                section = sectionList[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        courseAdapter = SpinnerItemAdapter(
            this,
            coursesList
        )

        courseSpinner = findViewById(R.id.course_post)
        courseSpinner.adapter = courseAdapter
        courseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                course = coursesList[p2].textDownLeft
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        if (currentUser.userType == UserTypes.assistantUser) {
            viewModel.getCoursesByAssistantID(currentUser.code)
        } else {
            viewModel.getCoursesByProfessorID(currentUser.code)
        }


        observeCourses()

    }


        private fun observeStudents() {
            lifecycleScope.launchWhenCreated {
                viewModel.searchStudentByID.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                        }
                        is Resource.Success -> {
                            studentsList.clear()
                            val student=it.result
                            if(student.name.isNotEmpty()){studentsList.add(student)
                                recyAdapter.update(studentsList)}
                            }
                        is Resource.Failure -> {
                            Toast.makeText(this@AddPostActivity,it.exception, Toast.LENGTH_SHORT).show()
                        }
                        else->{}

                    }


                }
            }
        }


    private fun observePost(hasImage: Boolean, uri: Uri) {
        if (hasImage) {
            lifecycleScope.launchWhenCreated {
                viewModel.addPost.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                        }

                        is Resource.Success -> {
                            fireStorageViewModel.addPostUri(it.result, uri)
                            observePostImage()
                        }

                        is Resource.Failure -> {
                            Toast.makeText(this@AddPostActivity, it.exception, Toast.LENGTH_SHORT)
                                .show()
                        }

                        else -> {}
                    }
                }
            }
        } else {
            lifecycleScope.launchWhenCreated {
                viewModel.addPost.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                        }

                        is Resource.Success -> {
                            Toast.makeText(
                                this@AddPostActivity,
                                "post added successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is Resource.Failure -> {
                            Toast.makeText(this@AddPostActivity, it.exception, Toast.LENGTH_SHORT)
                                .show()
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun observePostImage() {
        lifecycleScope.launchWhenCreated {
            fireStorageViewModel.addPostUri.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        Toast.makeText(
                            this@AddPostActivity,
                            "post added successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is Resource.Failure -> {
                        Toast.makeText(this@AddPostActivity, it.exception, Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun observeCourses() {
        lifecycleScope.launchWhenCreated {
            viewModel.getCourse.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        coursesList.clear()
                        it.result.forEach {
                            val courses = SpinnerItem(
                                it.courseName, it.courseCode,
                                it.grade
                            )
                            coursesList.add(courses)
                        }
                        courseAdapter.update(coursesList)
                    }

                    is Resource.Failure -> {
                        Toast.makeText(this@AddPostActivity, it.exception, Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, SignUp.IMAGE_REQUEST_CODE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SignUp.IMAGE_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {

            userImageUri = data?.data!!
            imageView.setImageURI(userImageUri)
            imageView.visibility = View.VISIBLE
        }
    }

    fun chooseGrade() {
        val gradeList = resources.getStringArray(R.array.grades)
        val adapter: ArrayAdapter<CharSequence> =
            ArrayAdapter.createFromResource(this, R.array.grades, R.layout.spinner_item)
        val autoCom = findViewById<Spinner>(R.id.grad_spinner_post)
        autoCom.adapter = adapter

        autoCom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                grade = gradeList[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    fun chooseToWho() {
        val gradeList = resources.getStringArray(R.array.toWho)
        val adapter: ArrayAdapter<CharSequence> =
            ArrayAdapter.createFromResource(this, R.array.toWho, R.layout.spinner_item)
        val autoCom = findViewById<Spinner>(R.id.post_to_spinner)
        autoCom.adapter = adapter

        autoCom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                when (gradeList[p2]) {
                    "Department and Section" ->{
                        course =""
                        depSpinner.visibility = View.VISIBLE
                        courseSpinner.visibility = View.GONE
                        courseArrowDown.visibility=View.GONE
                        sectionSpinner.visibility = View.VISIBLE
                        depArrowDown.visibility = View.VISIBLE
                        depArrowDown.visibility = View.VISIBLE
                        hideStudentId()
                    }
                    "General" -> {
                        course =""
                        department =""
                        section =""
                        depSpinner.visibility = View.GONE
                        courseSpinner.visibility = View.GONE
                        courseArrowDown.visibility = View.GONE
                        sectionArrowDown.visibility = View.GONE
                        depArrowDown.visibility = View.GONE
                        sectionSpinner.visibility = View.GONE
                        hideStudentId()
                    }


                    "Course" -> {

                        department =""
                        section =""
                        depSpinner.visibility = View.GONE
                        courseSpinner.visibility = View.VISIBLE
                        courseArrowDown.visibility = View.VISIBLE
                        sectionSpinner.visibility = View.GONE
                        sectionArrowDown.visibility = View.GONE
                        depArrowDown.visibility = View.GONE
                        hideStudentId()
                    }

                    "Student" -> {
                        depSpinner.visibility = View.GONE
                        courseSpinner.visibility = View.GONE
                        sectionSpinner.visibility = View.GONE
                        courseArrowDown.visibility = View.GONE
                        sectionArrowDown.visibility = View.GONE
                        depArrowDown.visibility = View.GONE
                        showStudentId()

                    }

                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun hideStudentId() {
        studentIdBtn.visibility = View.GONE
        studentIdEdt.visibility = View.GONE
        studentIdRv.visibility = View.GONE
    }

    private fun showStudentId() {
        studentIdBtn.visibility = View.VISIBLE
        studentIdEdt.visibility = View.VISIBLE
        studentIdRv.visibility = View.VISIBLE
    }
}