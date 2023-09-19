package com.hendev.artbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.hendev.artbook.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var artsList: ArrayList<ArtModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        getDataFromDb();
    }

    fun getDataFromDb() {
        artsList = ArrayList<ArtModel>()
        try {
            val database = this.openOrCreateDatabase("ArtsBook", MODE_PRIVATE, null)
            val cursor = database.rawQuery("SELECT * FROM Arts", null)
            val idIx = cursor.getColumnIndex("id")
            val artNameIx = cursor.getColumnIndex("art")
            val artistNameIx = cursor.getColumnIndex("artist")
            val yearIx = cursor.getColumnIndex("year")
            val imageIx = cursor.getColumnIndex("image")

            while (cursor.moveToNext()) {
                val id = cursor.getInt(idIx)
                val name = cursor.getString(artNameIx)
                val artist = cursor.getString(artistNameIx)
                val year = cursor.getString(yearIx)
                val image = cursor.getBlob(imageIx)

                var model = ArtModel(name, artist, year, image, id)
                artsList.add(model)
            }
            cursor.close()

            Toast.makeText(this,"Data Collected",Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.art_add_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.add_item) {
            startActivity(Intent(this, DetailArt::class.java))
        }

        if (item.itemId == R.id.remove_item) {
            println("Remove Menu Item Clicked")
        }

        return super.onOptionsItemSelected(item)
    }
}