package com.example.myapplication.ui.completed

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import java.util.*


class CompletedAdapter(private val mCompletedList: MutableList<CompletedItem>): RecyclerView.Adapter<CompletedAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val filename: TextView = itemView.findViewById(R.id.compressed_filename)
        val date: TextView = itemView.findViewById(R.id.compressed_date)
        var shareButton: ImageButton = itemView.findViewById(R.id.compressed_share)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val compressingItemView = LayoutInflater.from(parent.context).inflate(R.layout.item_compressed, parent, false)
        return ViewHolder(compressingItemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val compressingItem: CompletedItem = mCompletedList[position]
        holder.filename.text = compressingItem.filename
        holder.date.text = compressingItem.date.toString()
        holder.shareButton.setOnClickListener { shareFile(holder.itemView.context, compressingItem.uri) }

    }

    private fun shareFile(context: Context?, uri: Uri) {
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build()) // this is a hack to allow sharing files from the app


        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "video/mp4"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        context?.startActivity(Intent.createChooser(intent, "Share Video"))
    }

    override fun getItemCount(): Int {
        return mCompletedList.size
    }

    @SuppressLint("NotifyDataSetChanged") //this is fine because it is a custom adapter
    fun refreshList(context: Context) {
        mCompletedList.clear()
        context.getExternalFilesDir(null)?.listFiles()?.forEach {
            if (it.name.endsWith(".mp4")) {
                val completedItem = CompletedItem(it.name, Date(it.lastModified()), ImageButton(context), Uri.fromFile(it))
                mCompletedList.add(completedItem)
            }
        }
        notifyDataSetChanged()
    }

}