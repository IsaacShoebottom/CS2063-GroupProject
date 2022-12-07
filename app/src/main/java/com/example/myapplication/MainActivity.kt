package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegKitConfig
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.ui.compressing.CompressingAdapter
import com.example.myapplication.ui.compressing.CompressingItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // VERIFY PERMISSIONS
        val permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        }

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
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    .addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    .addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)

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
    @SuppressLint("NotifyDataSetChanged") // Needed because of custom adapter
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {

            // There are no request codes
            val data: Uri? = result.data?.data

            val inUri = FFmpegKitConfig.getSafParameterForRead(this, data)

            val cursor = contentResolver.query(data!!, null, null, null, null)

            cursor?.moveToFirst()

            val fileDate = Date(System.currentTimeMillis())
            val fileName = cursor?.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))

            val item = CompressingItem(fileName!!, 0.0, fileDate)

            val outputFile = File(this.getExternalFilesDir(null), "converted_$fileName")

            compressingItems.add(item)

            val handler = Handler(Looper.getMainLooper())


            val command = "-i $inUri -c:v mpeg4 ${outputFile.absolutePath} -y"
            val session = FFmpegKit.executeAsync(command) {
                compressingItems.remove(item)

                handler.post {
                    Toast.makeText(this, "Finished converting $fileName", Toast.LENGTH_SHORT).show()
                    adapter.notifyDataSetChanged()
                }
            }


            Log.i("Tag", Arrays.deepToString(session.arguments))
            Log.i("Tag", session.output)






            adapter.notifyDataSetChanged()
        }
    }

    companion object {
        val compressingItems: MutableList<CompressingItem> = mutableListOf()

        val adapter = CompressingAdapter(compressingItems)
    }
}