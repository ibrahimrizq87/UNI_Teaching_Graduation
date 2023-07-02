package com.uni.uniteaching.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.uni.uniteaching.R
import com.uni.uniteaching.adapters.PostsAdapter
import com.uni.uniteaching.classes.Courses
import com.uni.uniteaching.classes.PostData
import com.uni.uniteaching.classes.Posts
import com.uni.uniteaching.classes.SectionData

import com.uni.uniteaching.classes.user.UserTeaching
import com.uni.uniteaching.data.Resource
import com.uni.uniteaching.data.di.PostType
import com.uni.uniteaching.data.di.UserTypes
import com.uni.uniteaching.databinding.FragmentHomeScreenBinding
import com.uni.uniteaching.ui.HomeScreen
import com.uni.uniteaching.viewModel.AuthViewModel
import com.uni.uniteaching.viewModel.FireStorageViewModel
import com.uni.uniteaching.viewModel.FirebaseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class HomeScreenFragment : Fragment() {
    private lateinit var binding: FragmentHomeScreenBinding
    private val viewModel: FirebaseViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val storageViewModel: FireStorageViewModel by viewModels()
    private lateinit var progress: ProgressBar
    private lateinit var currentUser: UserTeaching
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var studentID: EditText
    private lateinit var coursesList: MutableList<Courses>

    private lateinit var adapter: PostsAdapter
    private var isFloatingBtnClick = false
    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            context, R.anim.rotate_open_anim
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            context, R.anim.rotate_close_anim
        )
    }
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            context, R.anim.from_bottom_anim
        )
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            context, R.anim.to_bottom_anim
        )
    }
    private lateinit var btnAddSchedule: FloatingActionButton
    private lateinit var btnAddCourse: FloatingActionButton
    private lateinit var btnAddPost: FloatingActionButton
    private lateinit var createPostBtnTxt: TextView
    private lateinit var addScheduleBtnTxt: TextView
    private lateinit var addCourseBtnTxt: TextView
    private lateinit var postsList: MutableList<PostData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {


// update user data --------------------------------------------------------------------------------
        currentUser = UserTeaching()
        authViewModel.getSessionStudent { user ->
            if (user != null) {
                currentUser = user
            } else {
                Toast.makeText(
                    context, "there is an error on loading user data", Toast.LENGTH_SHORT
                ).show()
            }

        }
        // update user data --------------------------------------------------------------------------------
        binding = FragmentHomeScreenBinding.inflate(layoutInflater)

        btnAddSchedule = binding.addScheduleBtn
        btnAddCourse = binding.addCourseBtn
        btnAddPost = binding.createPostBtn
        createPostBtnTxt = binding.createPostBtnTxt
        addScheduleBtnTxt = binding.addScheduleBtnTxt
        addCourseBtnTxt = binding.addCourseBtnTxt



        binding.addFloatingBtn.setOnClickListener {
            replaceFragment(AddPostFragment())
        }


        return binding.root
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
            .addToBackStack(null).commit()
    }

/*
    private fun onAddClicked() {
        isFloatingBtnClick = !isFloatingBtnClick
        btnAddSchedule.animation = null
        btnAddCourse.animation = null
        btnAddPost.animation = null
        binding.addFloatingBtn.animation = null

        if (isFloatingBtnClick) {
            openFloatingButton()
        } else {
            closeFloatingButton()
        }
    }

    private fun openFloatingButton() {



        btnAddPost.visibility = View.VISIBLE

        createPostBtnTxt.visibility = View.VISIBLE


        createPostBtnTxt.animation = fromBottom



        btnAddPost.animation = fromBottom
        binding.addFloatingBtn.animation = rotateOpen
    }

    private fun closeFloatingButton() {


        createPostBtnTxt.animation = toBottom
        addScheduleBtnTxt.animation = toBottom
        addCourseBtnTxt.animation = toBottom

        btnAddSchedule.animation = toBottom
        btnAddCourse.animation = toBottom
        btnAddPost.animation = toBottom

        binding.addFloatingBtn.animation = rotateClose

        btnAddSchedule.visibility = View.GONE
        btnAddCourse.visibility = View.GONE
        btnAddPost.visibility = View.GONE

        createPostBtnTxt.visibility = View.GONE
        addScheduleBtnTxt.visibility = View.GONE
        addCourseBtnTxt.visibility = View.GONE
    }
*/
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.home_recycler)
        progress = view.findViewById(R.id.progress_par_home)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)


        coursesList = arrayListOf()
        postsList = arrayListOf()


        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
          updatePost()
        }

        adapter = PostsAdapter(requireContext(), postsList,

            onItemClicked = { _, item ->
                Toast.makeText(requireContext(), item.authorName, Toast.LENGTH_SHORT).show()
            }, onComment = { _, item ->
                val bundle = Bundle()
                bundle.putString("postId", item.postID)
                bundle.putString("aud", item.audience)

                when (item.audience) {
                    PostType.course -> {
                        bundle.putString("course", item.courseID)
                        bundle.putString("section", "")
                        bundle.putString("department", "")
                        bundle.putString("studentID", "")
                    }

                    PostType.personal_posts -> {
                        bundle.putString("course", "")
                        bundle.putString("section", "")
                        bundle.putString("department", "")
                        bundle.putString("studentID", studentID.text.toString())

                    }

                    PostType.section_posts -> {
                        val location = item.courseID.split("/")

                        bundle.putString("course", "")
                        bundle.putString("section", location[0])
                        bundle.putString("department", location[1])
                        bundle.putString("studentID", "")

                    }
                }

                val commentFragment = CommentFragment()
                commentFragment.arguments = bundle
                (activity as HomeScreen).replaceFragment(commentFragment)

            }, deletePost = { post ->
                if (post.type==PostsAdapter.WITH_IMAGE){
                    storageViewModel.deletePostImage(post.postID)
                    observeDeletedImage()
                }

                when (post.audience) {
                    PostType.course -> {
                        viewModel.deletePostCourse(post.postID, post.courseID)
                    }
                    PostType.section_posts -> {
                        val location = post.courseID.split("/")

                        viewModel.deletePostSection(post.postID, location[1], location[0])
                    }

                    PostType.general -> {
                        viewModel.deletePostGeneral(post.postID)
                    }

                    PostType.personal_posts -> {
                        viewModel.deletePostPersonal(post.postID, post.courseID)
                    }
                }
                observeDeletePost()
            })

//-------------- setting the recycler data---------------------------//
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
//-------------- setting the recycler data---------------------------//


    updatePost()
    observeCourses()


    }
    private fun observeDeletedImage() {
        lifecycleScope.launchWhenCreated {
            storageViewModel.deletePostImage.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {
                        progress.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        Toast.makeText(context,state.result,Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Failure -> {
                        progress.visibility = View.INVISIBLE
                        Toast.makeText(context, state.exception.toString(), Toast.LENGTH_LONG)
                            .show()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun observeCourses() {
        lifecycleScope.launchWhenCreated {
            viewModel.getCourse.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {
                        progress.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        coursesList.clear()

                        state.result.forEach {
                            coursesList.add(it)
                        }

                        viewModel.getPostsGeneral()
                        viewModel.getPostsCourse(coursesList)

                        progress.visibility = View.VISIBLE
                        // ---------------------------- wait until the data is updated because of the delay done because of the loops---------------------//
                        delay(200)
                        // ---------------------------- wait until the data is updated because of the delay done because of the loops---------------------//
                        progress.visibility = View.INVISIBLE
                        observe()
                        delay(200)
                        observeCoursesPost()
                        delay(200)
                        observeSectionPost()


                    }

                    is Resource.Failure -> {
                        progress.visibility = View.INVISIBLE
                        Toast.makeText(context, state.exception.toString(), Toast.LENGTH_LONG)
                            .show()
                    }

                    else -> {}
                }
            }

        }
    }

    private fun observeDeletePost() {
        lifecycleScope.launchWhenCreated {
            viewModel.deletePost.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {
                        progress.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        Toast.makeText(context, state.result, Toast.LENGTH_SHORT).show()
                    }

                    is Resource.Failure -> {
                        progress.visibility = View.INVISIBLE
                        Toast.makeText(context, state.exception.toString(), Toast.LENGTH_LONG)
                            .show()
                    }

                    else -> {}
                }
            }

        }
    }

    private fun observeCoursesPost() {
        lifecycleScope.launchWhenCreated {
            viewModel.getPostsCourses.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {
                        progress.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        state.result.forEach {
                            var post = PostData(
                                it.description,
                                it.authorName,
                                false,
                                it.postID,
                                it.courseID,
                                it.time,
                                it.audience,
                                it.type
                            )
                            if (it.authorId == currentUser.userId) {
                                post.myPost = true
                            }
                            postsList.add(post)
                        }
                        adapter.update(postsList)
                        if (currentUser.userType!=UserTypes.assistantUser){
                            adapter.update(postsList)
                        }

                    }

                    is Resource.Failure -> {
                        progress.visibility = View.INVISIBLE
                        Toast.makeText(context, state.exception.toString(), Toast.LENGTH_LONG)
                            .show()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun observe() {
        lifecycleScope.launchWhenCreated {
            viewModel.getPosts.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {
                        progress.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        postsList.clear()
                        state.result.forEach {

                            var post = PostData(
                                it.description,
                                it.authorName,
                                false,
                                it.postID,
                                it.courseID,
                                it.time,
                                it.audience,
                                it.type
                            )
                            if (it.authorId == currentUser.userId) {
                                post.myPost = true
                            }
                            postsList.add(post)
                        }
                        progress.visibility = View.INVISIBLE
                    }

                    is Resource.Failure -> {
                        progress.visibility = View.INVISIBLE
                        Toast.makeText(context, state.exception.toString(), Toast.LENGTH_LONG)
                            .show()
                    }

                    else -> {}
                }
            }
        }
    }
private fun updatePost(){
    postsList.clear()

    val sectionList= (activity as HomeScreen).sectionList
    sectionList.forEach {
        Log.e("home section data",it.sectionName)
        Log.e("home section data",it.dep)
    }
    viewModel.getPostsGeneral()

    if (currentUser.userType == UserTypes.assistantUser){
        viewModel.getSectionPost(sectionList)
        viewModel.getCoursesByAssistantID(currentUser.code)
    }else{
        viewModel.getCoursesByProfessorID(currentUser.code)
    }


}

    private fun observeSectionPost() {
        lifecycleScope.launchWhenCreated {
            viewModel.getSectionPost.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {
                        progress.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        state.result.forEach {
                            Log.e("home section post",it.postID)
                            var post = PostData(
                                it.description,
                                it.authorName,
                                false,
                                it.postID,
                                it.courseID,
                                it.time,
                                it.audience,
                                it.type
                            )
                            if (it.authorId == currentUser.userId) {
                                post.myPost = true
                            }
                            postsList.add(post)
                        }
                        adapter.update(postsList)
                        progress.visibility = View.INVISIBLE
                    }

                    is Resource.Failure -> {
                        progress.visibility = View.INVISIBLE
                        Toast.makeText(context, state.exception.toString(), Toast.LENGTH_LONG)
                            .show()
                    }

                    else -> {}
                }
            }
        }
    }
}
