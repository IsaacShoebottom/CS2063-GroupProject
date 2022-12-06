package com.example.myapplication

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.FileUtils
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.ui.compressing.CompressingAdapter
import com.example.myapplication.ui.compressing.CompressingFragment
import com.example.myapplication.ui.compressing.CompressingItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.arthenica.ffmpegkit.FFmpegKit
import java.io.File
import java.io.FileInputStream
import java.util.*

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_compressing, R.id.navigation_completed,R.id.navigation_settings))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_bar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //adds actions for when you press buttons
    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        return when (item.itemId) {



            //runs when pressing "Files"
            R.id.addFile -> {
                val intent = Intent()
                    .setType("*/*")
                    .setAction(Intent.ACTION_GET_CONTENT)

                resultLauncher.launch(intent)

                Toast.makeText(applicationContext, "Files", Toast.LENGTH_LONG).show()

                return true
            }
            R.id.addYoutube ->{
                Toast.makeText(applicationContext, "Youtube downloading is currently not available", Toast.LENGTH_LONG).show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //grabs output from pressing files, used for grabbing URI
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            // There are no request codes
            val data: Uri? = result.data?.data

            val contentResolver: ContentResolver = contentResolver
            val fileDescriptor = contentResolver.openFileDescriptor(data!!, "r")
            val fd = fileDescriptor?.fileDescriptor
            val inputStream = FileInputStream(fd)

            val file = File(data.toString())
            Log.i("Tag", file.absolutePath)

            val fu = com.example.myapplication.utils.FileUtils(this)

            val test = "-i " + fu.getPath(data) + " -c:v libx264 -preset ultrafast -crf 28 -c:a aac -b:a 128k -movflags +faststart " + fu.getPath(data) + "_converted.mp4"
            FFmpegKit.execute(test)


            val cursor = contentResolver.query(data, null, null, null, null)

            cursor?.moveToFirst()

            val fileDate = Date(System.currentTimeMillis())
            val fileName = cursor?.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))

            compressingItems.add(CompressingItem(fileName!!, 0.0, fileDate))

            adapter.notifyDataSetChanged()

            Toast.makeText(applicationContext, data.toString(), Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        val compressingItems: MutableList<CompressingItem> = mutableListOf()
        val adapter = CompressingAdapter(compressingItems)
    }
}