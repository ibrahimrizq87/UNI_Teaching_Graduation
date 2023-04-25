package com.uni.uniteaching.ui.signUp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.uni.uniteaching.R
import com.uni.uniteaching.classes.user.UserTeaching
import com.uni.uniteaching.data.Resource
import com.uni.uniteaching.data.di.PermissionsRequired
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
class SignUp : AppCompatActivity() {
    private val viewModel : AuthViewModel by viewModels()
    private val fireStorageViewModel : FireStorageViewModel by viewModels()
    private val firebaseViewModel : FirebaseViewModel by viewModels()


    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var deprtment: String
    private lateinit var userType: String
    val TAG="SignUp"
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var userImageUri: Uri
    private lateinit var imageView: ImageView
    private lateinit var progress: ProgressBar
    companion object{
        val IMAGE_REQUEST_CODE =100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        deprtment=""
        userType=""
        userImageUri = Uri.EMPTY
        progress=binding.progressBarSignIn
        database = Firebase.database.reference

        imageView = binding.signUserImage


/*
binding.signUpButton.setOnClickListener {
    viewModel.register("testingDoctorAccount@gmail.com","123456",
        UserTeaching(
        "professor Name",
        "",
        "123456",
        "123456",
        "anything",
         UserTypes.professorUser,
            departement.CS,
            arrayListOf()
    ))
}*/
        val departmentList = resources.getStringArray(R.array.departement)
        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(this,R.array.departement,R.layout.spinner_item)
        val autoCom = findViewById<Spinner>(R.id.department_spinner)
        autoCom.adapter = adapter

        autoCom.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0 : AdapterView<*>?, p1: View?, p2:Int, p3: Long) {
                deprtment =departmentList[p2]
                binding.departmentText.text=deprtment
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }


        val userTypeList = resources.getStringArray(R.array.user_type)
        val adapter2: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(this,R.array.departement,R.layout.spinner_item)
        val autoCom2 = findViewById<Spinner>(R.id.department_spinner)
        autoCom2.adapter = adapter2

        autoCom2.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0 : AdapterView<*>?, p1: View?, p2:Int, p3: Long) {
                userType =userTypeList[p2]
                binding.userTypeText.text=userType
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
        binding.imageClick.setOnClickListener {
            pickImageFromGallery()
        }

        binding.signUpBt.setOnClickListener {

            val email=binding.signEmailAddress.text.toString()
            val password = binding.signPassword.text.toString()
            val code = binding.signCode.text.toString()
            val fullName = binding.signName.text.toString()
            val special = binding.signUpSpecial.text.toString()

            if (userImageUri != Uri.EMPTY){
                if (email.isNotEmpty()
                    &&password.isNotEmpty()
                    &&code.isNotEmpty()
                    &&special.isNotEmpty()
                    &&fullName.isNotEmpty()
                    &&deprtment.isNotEmpty()
                    &&userType.isNotEmpty()
                ){
                    if (password.length == 14){
                        viewModel.register(
                            email
                            ,password
                            , UserTeaching(
                            fullName,
                                "",
                                code,
                                password
                                ,special
                                ,userType
                                ,deprtment

                        )
                        )
                        observeUser()
                    }else{
                        Toast.makeText(this,"make sure to write the 14 number of the national id",
                            Toast.LENGTH_SHORT).show()

                    }
                }else{
                    Toast.makeText(this,"all data required", Toast.LENGTH_SHORT).show()

                }
            }else{
                Toast.makeText(this,"make sure to choose pic", Toast.LENGTH_SHORT).show()

            }
        }


    }

    private fun observeUploadedImage() {
        lifecycleScope.launchWhenCreated {
            fireStorageViewModel.addUri.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        Toast.makeText(this@SignUp,it.result,Toast.LENGTH_LONG).show()

                    }
                    is Resource.Failure -> {

                        Toast.makeText(this@SignUp,it.exception.toString(),Toast.LENGTH_LONG).show()
                    }
                    else->{}
                }

            }
        }}

    private fun pickImageFromGallery(){
        val intent = Intent (Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK)
        {
            userImageUri = data?.data!!
            imageView.setImageURI(userImageUri)
        }
    }


private fun observeUser(){
    lifecycleScope.launchWhenCreated {
        viewModel.register.collectLatest { state ->
            when (state) {
                is Resource.Loading -> {
                    progress.visibility=View.VISIBLE
                    Log.e(TAG, "Loading")

                }
                is Resource.Success -> {
                    progress.visibility=View.INVISIBLE
                    Toast.makeText(this@SignUp,state.result, Toast.LENGTH_LONG).show()
                    val userId = auth.currentUser?.uid
                    if (userId !=null){
                        fireStorageViewModel.addUri(userId,userImageUri)
                        observeUploadedImage()
                    }

                    startActivity(Intent(this@SignUp, HomeScreen::class.java))
                }
                is Resource.Failure -> {
                    Log.e(TAG, state.exception.toString())
                    progress.visibility=View.INVISIBLE
                    Toast.makeText(this@SignUp,state.exception.toString(),
                        Toast.LENGTH_LONG).show()
                }
                else->{}
            }
        }

    }
}
    override fun onStart() {
        super.onStart()
        viewModel.getSessionStudent {user->
            if (user !=null){
                startActivity(Intent(this@SignUp, HomeScreen::class.java))
            }
        }
    }

    }






