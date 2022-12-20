package com.onlineaavedan.dynamicmoduledemo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel:ViewModel() {

    var stringOtp = MutableLiveData<String>("1234")

}