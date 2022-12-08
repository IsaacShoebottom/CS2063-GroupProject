package ca.unb.lantau.ui.compressing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class CompressingAdapter(private val mCompressingItems: MutableList<CompressingItem>) :
    RecyclerView.Adapter<CompressingAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val filename: TextView = itemView.findViewById(R.id.compressing_filename)
        val date: TextView = itemView.findViewById(R.id.compressing_date)
        val progress: ProgressBar = itemView.findViewById(R.id.compressing_progress)
        val status: TextView = itemView.findViewById(R.id.compressing_status)
        val cancelButton: ImageButton = itemView.findViewById(R.id.compressing_cancel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val compressingItemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_compressing, parent, false)
        // Use gesture controls to swipe to delete
        // Reference here: https://www.tutorialspoint.com/how-to-detect-swipe-direction-between-left-right-and-up-down-in-android
        return ViewHolder(compressingItemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val compressingItem: CompressingItem = mCompressingItems[position]
        holder.filename.text = compressingItem.filename
        holder.date.text = compressingItem.date.toString()
        holder.progress.progress = compressingItem.progress.toInt()
        holder.progress.visibility = View.INVISIBLE
        holder.status.text = compressingItem.status
        holder.status.visibility = View.INVISIBLE
        holder.cancelButton.setOnClickListener {
            compressingItem.session!!.cancel()
        }
    }

    override fun getItemCount(): Int {
        return mCompressingItems.size
    }
}