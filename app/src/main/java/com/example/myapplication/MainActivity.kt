package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.ui.compressing.CompressingAdapter
import com.example.myapplication.ui.compressing.CompressingItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class MainActivity : AppCompatActivity() {

    private val compressingItems: MutableList<CompressingItem> = mutableListOf()

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
        val compressingRecycler = findViewById<View>(R.id.compressing_recycler_view) as? RecyclerView
        compressingRecycler?.adapter = CompressingAdapter(compressingItems)
        compressingRecycler?.layoutManager = LinearLayoutManager(this)

        return when (item.itemId) {



            //runs when pressing "Files"
            R.id.addFile -> {
                val intent = Intent()
                    .setType("*/*")
                    .setAction(Intent.ACTION_GET_CONTENT)

                resultLauncher.launch(intent)

                Toast.makeText(applicationContext, "Files", Toast.LENGTH_LONG).show()

                compressingItems.add(CompressingItem("Testing", 0.5, Date(1)))

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
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Uri? = result.data?.data
            Toast.makeText(applicationContext, data.toString(), Toast.LENGTH_LONG).show()
        }
    }
}