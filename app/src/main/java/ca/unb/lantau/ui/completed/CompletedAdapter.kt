package ca.unb.lantau.ui.completed

import android.annotation.SuppressLint
import android.app.AlertDialog
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


class CompletedAdapter(private val mCompletedList: MutableList<CompletedItem>) :
    RecyclerView.Adapter<CompletedAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val filename: TextView = itemView.findViewById(R.id.compressed_filename)
        val date: TextView = itemView.findViewById(R.id.compressed_date)
        val shareButton: ImageButton = itemView.findViewById(R.id.compressed_share)
        val deleteButton: ImageButton = itemView.findViewById(R.id.compressed_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val compressingItemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_compressed, parent, false)
        return ViewHolder(compressingItemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val compressingItem: CompletedItem = mCompletedList[position]
        holder.filename.text = compressingItem.filename
        holder.date.text = compressingItem.date.toString()
        holder.shareButton.setOnClickListener {
            shareFile(
                holder.itemView.context,
                compressingItem.uri
            )
        }
        holder.deleteButton.setOnClickListener {
            deleteFile(
                holder.itemView.context,
                compressingItem.uri
            )
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun deleteFile(context: Context?, uri: Uri) {
        val policy = VmPolicy.Builder().build()
        StrictMode.setVmPolicy(policy) // this is a hack to allow the file to be deleted

        AlertDialog.Builder(context)
            .setTitle("Delete file")
            .setMessage("Are you sure you want to delete this file?")
            .setPositiveButton("Yes") { _, _ ->
                val file = java.io.File(uri.path!!)
                file.delete()
                refreshList(context!!)
            }
            .setNegativeButton("No") { _, _ -> }
            .show()
    }

    private fun shareFile(context: Context?, uri: Uri) {
        val policy = VmPolicy.Builder().build()
        StrictMode.setVmPolicy(policy) // this is a hack to allow sharing files from the app


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
                val completedItem = CompletedItem(
                    it.name,
                    Date(it.lastModified()),
                    ImageButton(context),
                    ImageButton(context),
                    Uri.fromFile(it)
                )
                mCompletedList.add(completedItem)
            }
        }
        notifyDataSetChanged()
    }

}