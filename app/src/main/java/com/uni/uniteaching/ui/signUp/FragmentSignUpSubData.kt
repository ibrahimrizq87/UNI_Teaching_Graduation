package com.uni.uniteaching.ui.signUp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.google.android.material.snackbar.Snackbar
import com.uni.uniteaching.R
import com.uni.uniteaching.data.di.SignUpKey
import com.uni.uniteaching.databinding.FragmentSignUpSubDataBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FragmentSignUpSubData : Fragment() {
    private lateinit var binding: FragmentSignUpSubDataBinding
    private lateinit var mCollectData: CollectDataListener

    private lateinit var userImage: ImageView
    var code=""
    var specialization=""
    var departement=""
    var userType=""

    private lateinit var mainDataBundle: Bundle
    private lateinit var userImageUri: Uri
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {



        binding = FragmentSignUpSubDataBinding.inflate(layoutInflater)

        mainDataBundle = bundleOf()
        userImageUri = Uri.EMPTY
        userImage = binding.signUserImage


//------------------------------------//

        setUserTypeSpinner()
        setDepartementSpinner()
//------------------------------------//
        parentFragmentManager.setFragmentResultListener(
            SignUpKey.MAIN_DATA, this
        ) { _, result -> mainDataBundle = result }

        binding.chooseImageBtn.setOnClickListener {
            pickImageFromGallery()
        }
        binding.signUpBtn.setOnClickListener {

            code = binding.signCode
                .text.trim().toString()

            specialization = binding.specializationText
                .text.trim().toString()


            if (userImageUri != Uri.EMPTY) {

                if (code.isNotEmpty()
                    && specialization.isNotEmpty()
                    && departement.isNotEmpty()
                    && userType.isNotEmpty())
                {
                    mainDataBundle.putString("code", code)
                    mainDataBundle.putString("specialization", specialization)
                    mainDataBundle.putString("departement", departement)
                    mainDataBundle.putString("userType", userType)

                    mainDataBundle.putString("userImageUri", userImageUri.toString())
                    /// ----------- ///
                    mCollectData.signUp(mainDataBundle)
                    /// ------------- ///

                } else {
                    showTopSnackBar(binding.root,  R.string.alldata)

                }
            } else {
                showTopSnackBar(binding.root, R.string.notPickImage)

            }
        }

        binding.backBtn.setOnClickListener {
            parentFragmentManager.setFragmentResult(SignUpKey.BACK_DATA, mainDataBundle)
            (activity as SignUp).previousFragment(FragmentSignUpMainData())
        }

        return binding.root
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CollectDataListener) {
            mCollectData = context
        } else {
            Toast.makeText(requireContext(), "error in collect data listener", Toast.LENGTH_SHORT).show()
        }
    }


    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, SignUp.IMAGE_REQUEST_CODE)
    }

    // To send all data to signUp activity
    public interface CollectDataListener {
        fun signUp(bundle: Bundle)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SignUp.IMAGE_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            userImageUri = data?.data!!
            userImage.setImageURI(userImageUri)

        }
    }


    private fun setUserTypeSpinner() {

        val userTypeList = resources.getStringArray(R.array.userType)
        val adapter: ArrayAdapter<CharSequence> = context?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.userType,
                R.layout.spinner_item
            )
        } as ArrayAdapter<CharSequence>
        val gradeSpinner = binding.userTypeSpinner
        gradeSpinner.adapter = adapter

        gradeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                userType = userTypeList[p2]

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    private fun setDepartementSpinner() {

        val departementList = resources.getStringArray(R.array.departement)
        val adapter: ArrayAdapter<CharSequence> = context?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.departement,
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

    private fun showTopSnackBar(view: View, message: Int) {
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
        //  params.setMargins(10,10,10,10)
        snackBar.view.layoutParams = params
        snackBar.show()
    }
}