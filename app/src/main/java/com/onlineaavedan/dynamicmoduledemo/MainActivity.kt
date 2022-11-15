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
import com.google.android.play.core.splitinstall.*
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus


class MainActivity : AppCompatActivity() {

    lateinit var splitInstallManager: SplitInstallManager
    private var mySessionID = 0
    private val TAG = "OnDemandWork"

    lateinit var btnDownload: Button
    lateinit var progresss: ProgressBar

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
        setContentView(R.layout.activity_main)
        splitInstallManager = SplitInstallManagerFactory.create(this)
        splitInstallManager.registerListener(splitInstallStateUpdatedListener)

        btnDownload = findViewById(R.id.btnbecomeposp)
        progresss = findViewById(R.id.progress_circular)
        btnDownload.setOnClickListener { v -> onClickDownloadFeatureModule() } // Using JAVA_8


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