package com.example.myapplication.ui.compressing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CompressingViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is compressing Fragment"
    }
    val text: LiveData<String> = _text
}