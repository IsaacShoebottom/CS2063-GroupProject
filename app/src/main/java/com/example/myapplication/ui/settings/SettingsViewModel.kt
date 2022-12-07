package com.example.myapplication.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is settings Fragment"
    }
    val text: LiveData<String> = _text

    //size of the file, grab this for use
    private val _size = MutableLiveData<Double>().apply {
        value = 25.0
    }
    val size: MutableLiveData<Double> = _size

    fun getSize(): Double {
        return size.value!!
    }
}