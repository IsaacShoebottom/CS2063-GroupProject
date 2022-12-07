package com.example.myapplication.ui.completed

import android.net.Uri
import android.widget.ImageButton
import java.util.*

data class CompletedItem(val filename: String, val date: Date, val button: ImageButton, val uri: Uri)