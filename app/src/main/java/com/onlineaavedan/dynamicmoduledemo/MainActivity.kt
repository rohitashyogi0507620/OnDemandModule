package com.onlineaavedan.dynamicmoduledemo

import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import com.enparadigm.sharpsell.sdk.ErrorListener
import com.enparadigm.sharpsell.sdk.Sharpsell
import com.enparadigm.sharpsell.sdk.SuccessListener
import com.google.android.play.core.splitinstall.*
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import com.onlineaavedan.dynamicmoduledemo.databinding.ActivityMainBinding
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    lateinit var splitInstallManager: SplitInstallManager
    private var mySessionID = 0
    private val TAG = "OnDemandWork"

    lateinit var btnDownload: Button
    lateinit var progresss: ProgressBar
    lateinit var activityMainBinding: ActivityMainBinding


    lateinit var mainViewModel: MainViewModel

    var splitInstallStateUpdatedListener =
        SplitInstallStateUpdatedListener { state: SplitInstallSessionState ->
            if (state.sessionId() == mySessionID) {
                when (state.status()) {
                    SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION ->                             // Large module that has size greater than 10 MB requires user permission
                        try {
                            splitInstallManager.startConfirmationDialogForResult(state, this, 110)
                        } catch (ex: SendIntentException) {
                            Toast.makeText(this, ex.message.toString(), Toast.LENGTH_SHORT).show()

                        }
                    SplitInstallSessionStatus.DOWNLOADING -> {
                        Log.i(TAG, "Downloading")
                        progresss.visibility = View.VISIBLE
                        Toast.makeText(this, "Downloading", Toast.LENGTH_SHORT).show()

                        // The module is being downloaded
                        val totalBytes = state.totalBytesToDownload().toInt()
                        val progress = state.bytesDownloaded().toInt()
                    }
                    SplitInstallSessionStatus.INSTALLING -> {
                        Toast.makeText(this, "Installing", Toast.LENGTH_SHORT).show()
                        Log.i(TAG, "Installing")

                    }
                    SplitInstallSessionStatus.DOWNLOADED -> {
                        Toast.makeText(this, "DOWNLOADED", Toast.LENGTH_SHORT).show()
                        Log.i(TAG, "DOWNLOADED")

                    }
                    SplitInstallSessionStatus.INSTALLED -> {
                        Toast.makeText(this, "INSTALLED", Toast.LENGTH_SHORT).show()
                        Log.i(TAG, "INSTALLED")
                        progresss.visibility = View.GONE
                        startModuleClass()
                    }
                    SplitInstallSessionStatus.CANCELED -> {
                        Toast.makeText(this, "CANCELED", Toast.LENGTH_SHORT).show()
                        Log.i(TAG, "CANCELED")
                    }
                    SplitInstallSessionStatus.PENDING -> {
                        Toast.makeText(this, "PENDING", Toast.LENGTH_SHORT).show()
                        Log.i(TAG, "PENDING")
                    }
                    SplitInstallSessionStatus.FAILED -> {
                        Toast.makeText(this, "FAILED", Toast.LENGTH_SHORT).show()
                        Log.i(TAG, "FAILED")
                    }
                }
            }
        }

    private fun startModuleClass() {
        try {
            Intent().setClassName(
                BuildConfig.APPLICATION_ID,
                "com.onlineaavedan.dynamicposp.BecomePospActivity"
            )
                .also {
                    startActivity(it)
                }
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding=DataBindingUtil.setContentView(this,R.layout.activity_main)

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)


        mainViewModel.stringOtp.observe(this, Observer {

        })

        mainViewModel.stringOtp.postValue("sdgfsdg")


        splitInstallManager = SplitInstallManagerFactory.create(this)
        splitInstallManager.registerListener(splitInstallStateUpdatedListener)

        btnDownload = findViewById(R.id.btnbecomeposp)
        progresss = findViewById(R.id.progress_circular)
        btnDownload.setOnClickListener { v -> onClickDownloadFeatureModule() } // Using JAVA_8


        val userMeta = JSONObject()
        userMeta.put("user_category", "SO12399")
        userMeta.put("unique_id", "7611920581")
        userMeta.put("location_code","Bengaluru1111")
        userMeta.put("so_code", "11")
        userMeta.put("ro_code", "1111")
        userMeta.put("name", "Test So11")
        userMeta.put("doj", "2001-01-21")
        userMeta.put("employee_code", "12345")
        userMeta.put("business_unit", "Micro Business Loan")
        userMeta.put("designation", "DST11")
        userMeta.put("state", "Karnataka 1")
        userMeta.put("city", "Muchandi 1")
        userMeta.put("zone", "South 1")
        userMeta.put("cluster", "Belgaum")
        userMeta.put("branch", "BKC, Mumbai")
        userMeta.put("status", "ACTIVE99")
        userMeta.put("branch_name", "IDFC Bangalore11")
        userMeta.put("reporting_manager", "test.sm11@idfcfirstbank.com")
        userMeta.put("bu_type", "URBAN")
        userMeta.put("user_type","SO99")

//userMeta is optional.


        val data = JSONObject()
        data.put("company_code", "probus");
        data.put("user_unique_id", "7611920581")
        data.put("user_group_id", 2)
        data.put("country_code", null)
        //  data.put("user_meta", userMeta.toString())
        data.put("name", "Rohitash Yogi")
        data.put("mobile_number", "7611920581")
        data.put("email", "rohitash.yogi@probusinsurance.com")
//Pass the below key to enable push notification to be recived on your device
        data.put("fcm_token", "fcmToken");

        Log.d("RequestObject",data.toString())

        activityMainBinding.btnSharpCell.setOnClickListener {

            Sharpsell.initialize(this, data.toString(),
                object : SuccessListener {
                    override fun onSuccess() {

                        Toast.makeText(
                            this@MainActivity,
                            "Initialization Success",
                            Toast.LENGTH_LONG
                        ).show()
                        Sharpsell.open(this@MainActivity)

                    }
                },
                object : ErrorListener<String> {
                    override fun onError(error: String?) {
                        Toast.makeText(
                            this@MainActivity,
                            "Initialization Failed : $error",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.d("Error", error.toString())
                    }
                }
            )
        }


    }

    fun onClickDownloadFeatureModule() {
        if (!splitInstallManager.installedModules.contains("dynamicposp")) {
            val splitInstallRequest = SplitInstallRequest.newBuilder()
                .addModule("dynamicposp")
                .build()
            splitInstallManager.startInstall(splitInstallRequest)
                .addOnSuccessListener { result: Int? ->
                    mySessionID = result!!
                }
                .addOnFailureListener { e: Exception ->
                    Log.i(TAG, "installManager: $e")
                    Toast.makeText(this, "Error ${e.message} ", Toast.LENGTH_SHORT).show()

                }

        } else {
            Toast.makeText(this, "Aleady Downloaded", Toast.LENGTH_SHORT).show()
            Log.i(TAG, "Aleady Downloaded")
            startModuleClass()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 110) {
            if (resultCode == RESULT_OK) {
                Log.i(TAG, "onActivityResult: Install Approved ")
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}