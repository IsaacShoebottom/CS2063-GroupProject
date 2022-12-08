package ca.unb.lantau

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import ca.unb.lantau.ui.completed.CompletedAdapter
import ca.unb.lantau.ui.completed.CompletedItem
import ca.unb.lantau.ui.compressing.CompressingAdapter
import ca.unb.lantau.ui.compressing.CompressingItem
import ca.unb.lantau.ui.settings.SettingsViewModel
import com.arthenica.ffmpegkit.*
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File
import java.util.*
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val settingsViewModel: SettingsViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // VERIFY PERMISSIONS
        val permission =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
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
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_compressing, R.id.navigation_completed, R.id.navigation_settings
            )
        )
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
                    .setType("video/*")
                    .setAction(Intent.ACTION_GET_CONTENT)
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    .addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    .addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)

                resultLauncher.launch(intent)

                return true
            }
            R.id.addYoutube -> {
                Toast.makeText(
                    applicationContext,
                    "Youtube downloading is currently not available",
                    Toast.LENGTH_LONG
                ).show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //grabs output from pressing files, used for grabbing URI
    @SuppressLint("NotifyDataSetChanged") // Needed because of custom adapter
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {

                // There are no request codes
                val data: Uri? = result.data?.data

                val inUri = FFmpegKitConfig.getSafParameterForRead(this, data)

                val cursor = contentResolver.query(data!!, null, null, null, null)

                cursor?.moveToFirst()

                val fileDate = Date(System.currentTimeMillis())
                val fileName =
                    cursor?.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
                val fileSize =
                    cursor?.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE))
                        ?.toDouble()

                if (fileSize!! / 1000000 > settingsViewModel.getSize()) {

                    val mmr = MediaMetadataRetriever()
                    mmr.setDataSource(this, data)
                    val duration =
                        mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                            ?.toDouble()
                    Log.i("ethan", duration.toString())

                    Log.i("ethan", settingsViewModel.getSize().toString())
                    Log.i("ethan", fileSize.toString())
                    val bitrate = (settingsViewModel.getSize() * 1_000_000) / (duration!! / 1_000)

                    val item = CompressingItem(fileName!!, 0.0, fileDate, "Not started", ImageButton(this),null)

                    val outputFile = File(this.getExternalFilesDir(null), "converted_$fileName")

                    compressingItems.add(item)

                    val handler = Handler(Looper.getMainLooper())

                    val tempFile = File(this.cacheDir, "temp_$fileName")
                    val tempVideoFile = File(this.cacheDir, "tempVideo_$fileName")

                    val firstPass = "-i $inUri -codec:v libx264 -passlogfile ${tempFile.absolutePath} -preset veryfast -b:v $bitrate -maxrate $bitrate -minrate $bitrate -codec:a aac -pass 1 ${tempVideoFile.absolutePath} -y"
                    val secondPass = "-i ${tempVideoFile.absolutePath} -codec:v libx264 -passlogfile ${tempFile.absolutePath} -preset veryfast -b:v $bitrate -maxrate $bitrate -minrate $bitrate -codec:a aac -pass 2  ${outputFile.absolutePath} -y"
                    Log.i("Lantau", firstPass)
                    Log.i("Lantau", secondPass)


                    val firstPassSession = FFmpegKit.executeAsync(firstPass) { session ->
                        if (session.returnCode.isValueSuccess) {
                            val secondPassSession = FFmpegKit.executeAsync(secondPass) { session2 ->
                                if (session2.returnCode.isValueSuccess) {
                                    handler.post {
                                        handler.post {
                                            Toast.makeText(
                                                applicationContext,
                                                "Finished converting $fileName",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            compressingItems.remove(item)
                                            compressingAdapter.notifyDataSetChanged()
                                            completedAdapter.refreshList(applicationContext)
                                            tempFile.delete()
                                            tempVideoFile.delete()
                                        }
                                    }
                                } else {
                                    handler.post {
                                        Toast.makeText(
                                            applicationContext,
                                            "Failed to convert $fileName during second pass",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        compressingItems.remove(item)
                                        compressingAdapter.notifyDataSetChanged()
                                    }
                                }
                            }
                            item.status = "Second pass running"
                            item.session = secondPassSession
                            Log.i("Lantau", Arrays.deepToString(secondPassSession.arguments))
                        } else {
                            handler.post {
                                Toast.makeText(
                                    applicationContext,
                                    "Failed to convert $fileName during first pass",
                                    Toast.LENGTH_SHORT
                                ).show()
                                compressingItems.remove(item)
                                compressingAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                    item.status = "First pass running"
                    item.session = firstPassSession
                    Log.i("Lantau", Arrays.deepToString(firstPassSession.arguments))
                    compressingAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(
                        applicationContext,
                        "File is less than target size",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

    companion object {
        val compressingItems: MutableList<CompressingItem> = mutableListOf()
        val compressingAdapter = CompressingAdapter(compressingItems)

        private val completedItems: MutableList<CompletedItem> = mutableListOf()
        val completedAdapter = CompletedAdapter(completedItems)
    }
}