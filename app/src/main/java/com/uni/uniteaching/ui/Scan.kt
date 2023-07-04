package com.uni.uniteaching.ui


import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.uni.uniteaching.R
import com.uni.uniteaching.classes.Hall
import com.uni.uniteaching.classes.Lecture
import com.uni.uniteaching.classes.Section
import com.uni.uniteaching.data.Resource
import com.uni.uniteaching.databinding.ActivityScanBinding
import com.uni.uniteaching.ui.fragments.CustomDialog
import com.uni.uniteaching.viewModel.FireStorageViewModel
import com.uni.uniteaching.viewModel.FirebaseRealtimeModelView
import com.uni.uniteaching.viewModel.FirebaseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

import java.io.IOException



@AndroidEntryPoint
class Scan : AppCompatActivity() {
    private val viewModel: FirebaseViewModel by viewModels()

    private val viewModelRealTime: FirebaseRealtimeModelView by viewModels()
    private val requestCodeCameraPermission = 1001
    var hallId = ""
    var code = ""
    private lateinit var cameraSource: CameraSource
    private lateinit var barcodeDetector: BarcodeDetector
    private var scannedValue = ""
    private lateinit var binding: ActivityScanBinding
    private var isPermissionDenied = false
    private lateinit var customDialog: CustomDialog
    var course=""
    var dep=""
    var section=""
    var id=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        customDialog = CustomDialog(this)
        setContentView(binding.root)
        val intent = intent
        course = intent.getStringExtra("course")!!
        dep = intent.getStringExtra("dep")!!
        section = intent.getStringExtra("section")!!
        id = intent.getStringExtra("id")!!
        binding.icBack.setOnClickListener { finish() }


        binding.startSession.setOnClickListener {
            if (hallId.isNotEmpty()&&code.isNotEmpty()){
                viewModelRealTime.startGeneratingCode(Hall(hallId,true))
                if (section == "no"){
                    viewModel.updateLectureState( Lecture(
                        id,
                        course,
                        "",
                        "",
                        dep,
                        "",
                        "",
                        "",
                        "",
                        false),true)
                }else{
                    viewModel.updateSectionState(Section(
                        id,
                        course,
                        "",
                        "",
                        "",
                        section,
                        dep,
                        "",
                        "",
                        "",
                        false),true)
                }
                observeUpdate()
                observeStarting()
            }else{
                Toast.makeText(this,"make sure to scan the right code",Toast.LENGTH_SHORT).show()
            }

        }

        val permission =
            ContextCompat.checkSelfPermission(this@Scan, android.Manifest.permission.CAMERA)
        if (permission != PackageManager.PERMISSION_GRANTED
        ) {
            askForCameraPermission()
        } else {
            setupControls()
        }

        val aniSlide: Animation = AnimationUtils.loadAnimation(this@Scan, R.anim.scanner_animation)
        binding.barcodeLine.startAnimation(aniSlide)
    }

    private fun observeStarting() {
        lifecycleScope.launchWhenCreated {
            viewModelRealTime.startGeneratingCode.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {
                        Toast.makeText(this@Scan, "machine started generating code", Toast.LENGTH_LONG)
                            .show()
                    }
                    is Resource.Failure -> {
                        Toast.makeText(this@Scan, state.exception.toString(), Toast.LENGTH_LONG)
                            .show()
                    }
                    else -> {}
                }
            }

        }
    }

    private fun observeUpdate() {
        lifecycleScope.launchWhenCreated {
            viewModel.updateScheduleState.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {
                        Toast.makeText(this@Scan, state.result, Toast.LENGTH_LONG)
                            .show()
                        finish()
                    }
                    is Resource.Failure -> {
                        Toast.makeText(this@Scan, state.exception.toString(), Toast.LENGTH_LONG)
                            .show()
                    }
                    else -> {}
                }
            }

        }
    }

    private fun setupControls() {
        barcodeDetector =
            BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build()

        cameraSource = CameraSource.Builder(this, barcodeDetector)
            .setRequestedPreviewSize(250, 250)
            .setAutoFocusEnabled(true) //you should add this feature
            .build()

        binding.cameraSurfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            @SuppressLint("MissingPermission")
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {//Start preview after 1s delay

                    cameraSource.start(holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            @SuppressLint("MissingPermission")
            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                try {
                    cameraSource.start(holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        })


        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
                Toast.makeText(applicationContext, "Scanner has been closed", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() == 1) {
                    scannedValue = barcodes.valueAt(0).rawValue
                    //TODO @walid CHECK QR HERE
                    if (scannedValue.contains('@')) {
                        val strings = scannedValue.split('@')
                        if (strings.size == 2) {
                            hallId = strings[0]
                             code = strings[1]


                            runOnUiThread {
                                binding.barcodeLine.startAnimation(
                                    AnimationUtils.loadAnimation(
                                        this@Scan,
                                        R.anim.scanner_animation_stop
                                    )
                                )
                                cameraSource.stop()
                                binding.codeTextView.text="hallId: $hallId code: $code"
                                /*Toast.makeText(
                                    this@Scan,
                                    "hall = $hallId code= $code",
                                    Toast.LENGTH_SHORT
                                ).show()*/


                            }


                        } else {
                            runOnUiThread {
                               customDialog.showNotOurCode() //TODO @Walid
                            }
                        }
                    } else {
                        runOnUiThread {
                          customDialog.showNotOurCode() //TODO @Walid
                        }
                    }
                }
            }
        })
    }

    private fun askForCameraPermission() {
        ActivityCompat.requestPermissions(
            this@Scan,
            arrayOf(android.Manifest.permission.CAMERA),
            requestCodeCameraPermission
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCodeCameraPermission && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                setupControls()
                setContentView(binding.root)
                val aniSlide: Animation =
                    AnimationUtils.loadAnimation(this@Scan, R.anim.scanner_animation)
                binding.barcodeLine.startAnimation(aniSlide)

            } else {
                isPermissionDenied = true
                Toast.makeText(applicationContext, R.string.permissionDenied, Toast.LENGTH_SHORT).show()

            }
        }
    }






}