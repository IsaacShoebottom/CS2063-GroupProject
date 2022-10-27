package com.example.myapplication.ui.compressing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class CompressingAdapter(private val mCompressingItems: MutableList<CompressingItem>): RecyclerView.Adapter<CompressingAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val filename: TextView = itemView.findViewById(R.id.compressing_filename)
        val date: TextView = itemView.findViewById(R.id.compressing_date)
        val progress: ProgressBar = itemView.findViewById(R.id.compressing_progress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val compressingItemView = LayoutInflater.from(parent.context).inflate(R.layout.item_compressing, parent, false)
        return ViewHolder(compressingItemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val compressingItem: CompressingItem = mCompressingItems[position]
        holder.filename.text = compressingItem.filename
        holder.date.text = compressingItem.date.toString()
        holder.progress.progress = compressingItem.progress.toInt()

    }

    override fun getItemCount(): Int {
        return mCompressingItems.size
    }
}