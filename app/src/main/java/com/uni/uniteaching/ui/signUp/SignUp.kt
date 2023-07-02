package com.uni.uniteaching.ui.signUp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.uni.uniteaching.R
import com.uni.uniteaching.classes.user.UserTeaching
import com.uni.uniteaching.data.Resource
import com.uni.uniteaching.data.di.PermissionsRequired
import com.uni.uniteaching.data.di.SignUpKey
import com.uni.uniteaching.data.di.UserTypes
import com.uni.uniteaching.data.di.departement
import com.uni.uniteaching.databinding.ActivitySignUpBinding
import com.uni.uniteaching.ui.HomeScreen
import com.uni.uniteaching.viewModel.AuthViewModel
import com.uni.uniteaching.viewModel.FireStorageViewModel
import com.uni.uniteaching.viewModel.FirebaseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.security.Permission

@AndroidEntryPoint
class SignUp : AppCompatActivity(), FragmentSignUpSubData.CollectDataListener {
    private lateinit var binding: ActivitySignUpBinding
    private val viewModel: AuthViewModel by viewModels()
    private val fireStorageViewModel: FireStorageViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    val TAG = "SignUp"
    private lateinit var progressPar: ProgressBar

    private lateinit var userImageUri: Uri


    companion object {
        const val IMAGE_REQUEST_CODE = 100
    }


    public fun nextFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {

            setCustomAnimations(
                R.anim.enter_right_to_left,
                R.anim.exist_right_to_left,
                R.anim.enter_left_to_right,
                R.anim.exist_left_to_right
            )

            replace(
                R.id.fragment_container,
                fragment
            ).commit()
        }
    }

    public fun previousFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {

            setCustomAnimations(
                R.anim.enter_left_to_right,
                R.anim.exist_left_to_right,
                R.anim.enter_right_to_left,
                R.anim.exist_right_to_left
            )

            replace(
                R.id.fragment_container,
                fragment
            ).commit()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        nextFragment(FragmentSignUpMainData())

        val message = intent.getStringExtra(SignUpKey.FROM_HOME_SCREEN)
        if (message != null) {
            if (message.isNotEmpty()) {
                val rootView = findViewById<View>(android.R.id.content)
                showTopSnackBar(rootView, message)

            }
        }

        userImageUri = Uri.EMPTY
        //----------------
        auth = Firebase.auth
        //----------------

        progressPar = binding.progressBarRegister


    }


    // observing the registration function to check if it run properly
    private fun observeSignUp() {
        lifecycleScope.launchWhenCreated {
            viewModel.register.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {
                        progressPar.visibility = View.VISIBLE

                    }

                    is Resource.Success -> {

                        Toast.makeText(this@SignUp, state.result, Toast.LENGTH_LONG).show()
                        val userId = auth.currentUser?.uid
                        if (userId != null) {
                            fireStorageViewModel.addUri(userId, userImageUri)
                            observeUploadedImage()
                        }
                        startActivity(Intent(this@SignUp, HomeScreen::class.java))
                    }

                    is Resource.Failure -> {
                        progressPar.visibility = View.GONE
                        Toast.makeText(
                            this@SignUp, state.exception.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    else -> {}
                }
            }

        }
    }


    // observing the upload function to check if it run properly
    private fun observeUploadedImage() {
        lifecycleScope.launchWhenCreated {
            fireStorageViewModel.addUri.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        progressPar.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        progressPar.visibility = View.GONE
                        Log.e(TAG, "it.result ")
                    }
                    is Resource.Failure -> {

                        Toast.makeText(this@SignUp, it.exception.toString(), Toast.LENGTH_LONG)
                            .show()
                    }

                    else -> {}
                }

            }
        }

    }


    override fun signUp(bundle: Bundle) {

        userImageUri = Uri.parse(bundle.getString("userImageUri"))

        viewModel.register(
            bundle.getString("emailAddress").toString(),
            bundle.getString("password").toString(),
            UserTeaching(
                bundle.getString("name").toString(),
                "",
                bundle.getString("code").toString(),
                bundle.getString("nationalID").toString(),
                bundle.getString("specialization").toString(),
                bundle.getString("userType").toString(),
                bundle.getString("departement").toString(),
                )
        )
        observeSignUp()
    }

    private fun showTopSnackBar(view: View, message: String) {
        val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)

        val slideInAnimation = AnimationUtils.loadAnimation(view.context, R.anim.slide_in_top)
        val slideOutAnimation = AnimationUtils.loadAnimation(view.context, R.anim.slide_out_bottom)
        snackBar.view.animation = slideInAnimation
        snackBar.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                snackBar.view.animation = slideOutAnimation
            }
        })

        val params = snackBar.view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        snackBar.view.layoutParams = params
        snackBar.show()
    }

    override fun onStart() {
        super.onStart()
        viewModel.getSessionStudent { user ->
            if (user != null) {
                startActivity(Intent(this, HomeScreen::class.java))
            } else {
                showTopSnackBar(binding.root, getString(R.string.notFoundUser))
            }
        }
    }

}