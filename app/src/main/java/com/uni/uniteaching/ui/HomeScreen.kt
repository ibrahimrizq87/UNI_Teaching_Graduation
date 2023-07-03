package com.uni.uniteaching.ui

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.uni.uniteaching.R
import com.uni.uniteaching.classes.SectionData
import com.uni.uniteaching.classes.user.UserTeaching
import com.uni.uniteaching.data.Resource
import com.uni.uniteaching.data.di.UserTypes
import com.uni.uniteaching.databinding.ActivityHomeScreenBinding
import com.uni.uniteaching.ui.fragments.HomeScreenFragment
import com.uni.uniteaching.ui.fragments.NotificationFragment
import com.uni.uniteaching.ui.fragments.ProfileFragment
import com.uni.uniteaching.ui.fragments.ScheduleFragment
import com.uni.uniteaching.ui.signUp.SignUp
import com.uni.uniteaching.viewModel.AuthViewModel
import com.uni.uniteaching.viewModel.FireStorageViewModel
import com.uni.uniteaching.viewModel.FirebaseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class HomeScreen : AppCompatActivity() {
    private val viewModel: AuthViewModel by viewModels()
    private val storageViewModel: FireStorageViewModel by viewModels()
    private val fireViewModel: FirebaseViewModel by viewModels()

    public lateinit var sectionList: MutableList<SectionData>

    lateinit var currentUser: UserTeaching
    public  var addPost=false
    private lateinit var binding: ActivityHomeScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sectionList= arrayListOf()
        currentUser= UserTeaching()
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    replaceFragment(HomeScreenFragment())
                    binding.profileData.visibility = View.VISIBLE
                }

                R.id.notification -> {
                    replaceFragment(NotificationFragment())
                    binding.profileData.visibility = View.VISIBLE
                }

                R.id.profile -> {
                    replaceFragment(ProfileFragment())
                    binding.profileData.visibility = View.GONE
                }

                R.id.schedule_and_attendees -> {
                    replaceFragment(ScheduleFragment())
                    updateUser(currentUser)
                    binding.profileData.visibility = View.VISIBLE
                }


                else -> {
                }
            }
            true
        }
    }
    fun getSectionData(){
        fireViewModel.getSectionData(currentUser.code)
        lifecycleScope.launchWhenCreated {
            fireViewModel.getSectionData.collectLatest { uri ->
                when (uri) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        sectionList.clear()
                        uri.result.forEach {
                            sectionList.add(it)
                        }
                    }
                    is Resource.Failure -> {
                        Toast.makeText(this@HomeScreen, uri.exception.toString(), Toast.LENGTH_LONG)
                            .show()
                    }

                    else -> {
                    }
                }
            }


        }
    }
    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun updateUser(user: UserTeaching) {
        viewModel.getUserStudent(user.userId)
    }

    private fun observeImage() {
        lifecycleScope.launchWhenCreated {
            storageViewModel.getUri.collectLatest { uri ->
                when (uri) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        binding.progressBarImage.visibility = View.GONE
                        Glide.with(this@HomeScreen)
                            .load(uri.result)
                            .placeholder(R.drawable.user_image)
                            .into(binding.userImage)
                    }

                    is Resource.Failure -> {
                        Toast.makeText(this@HomeScreen, uri.exception.toString(), Toast.LENGTH_LONG)
                            .show()
                    }

                    else -> {
                    }
                }
            }


        }
    }
    private fun observeUser() {
        lifecycleScope.launchWhenCreated {
            viewModel.userStudent.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        val user = state.result
                        if (user != null) {

                            viewModel.setSession(state.result)
                            binding.userGrade.text = user.department
                            binding.userDepartment.text = user.userType
                            binding.userName.text = user.name

                        }
                    }

                    is Resource.Failure -> {
                        Toast.makeText(
                            this@HomeScreen,
                            state.exception.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    else -> {
                    }
                }
            }

        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.getSessionStudent { user ->
            if (user != null) {

                settingsOnStartApp()
                updateUser(user)
                currentUser = user
                if (checkForInternet(this)) {
                    storageViewModel.getUri(user.userId)
                }
                if (user.userType== UserTypes.assistantUser){
                    getSectionData()
                }
                observeUser()
                observeImage()
            } else {
                Toast.makeText(this, "no user found. have to register", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, SignUp::class.java))
            }
        }
    }

    private fun settingsOnStartApp() {
        binding.bottomNavigationView.itemIconTintList = null
        binding.bottomNavigationView.selectedItemId = R.id.home

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {

                R.id.home -> {
                    replaceFragment(HomeScreenFragment())
                    binding.profileData.visibility = View.VISIBLE
                }
                R.id.profile -> {
                    replaceFragment(ProfileFragment())
                    binding.profileData.visibility = View.GONE
                }

                R.id.schedule_and_attendees -> {
                    replaceFragment(ScheduleFragment())
                    updateUser(currentUser)
                    binding.profileData.visibility = View.VISIBLE
                }

                R.id.notification -> {
                    replaceFragment(NotificationFragment())
                    binding.profileData.visibility = View.VISIBLE
                }

                else -> {
                }
            }
            true
        }

    }

    private fun checkForInternet(context: Context): Boolean {

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
}
