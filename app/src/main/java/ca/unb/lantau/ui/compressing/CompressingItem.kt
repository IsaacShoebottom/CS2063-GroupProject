package ca.unb.lantau.ui.compressing

import android.widget.ImageButton
import com.arthenica.ffmpegkit.FFmpegSession
import java.util.*

data class CompressingItem(val filename: String, val progress: Double, val date: Date, var status: String, val cancelButton: ImageButton, var session: FFmpegSession?)