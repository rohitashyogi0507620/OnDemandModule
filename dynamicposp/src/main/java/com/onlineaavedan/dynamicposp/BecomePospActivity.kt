package com.onlineaavedan.dynamicposp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.enparadigm.sharpsell.sdk.ErrorListener
import com.enparadigm.sharpsell.sdk.Sharpsell
import com.enparadigm.sharpsell.sdk.SuccessListener
import org.json.JSONObject

class BecomePospActivity : BaseSplitActivity() {
    val data = JSONObject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_become_posp)


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

        Log.d("RequestObject", data.toString())


    }

    fun fun_sharpcell(view: View) {
        Sharpsell.initialize(this, data.toString(),
            object : SuccessListener {
                override fun onSuccess() {

                    Toast.makeText(
                        applicationContext,
                        "Initialization Success",
                        Toast.LENGTH_LONG
                    ).show()
                    Sharpsell.open(applicationContext)

                }
            },
            object : ErrorListener<String> {
                override fun onError(error: String?) {
                    Toast.makeText(
                        applicationContext,
                        "Initialization Failed : $error",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d("Error", error.toString())
                }
            }
        )
    }
}