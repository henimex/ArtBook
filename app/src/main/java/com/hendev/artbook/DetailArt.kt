package com.hendev.artbook



import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.hendev.artbook.databinding.ActivityDetailArtBinding

class DetailArt : AppCompatActivity() {

    private lateinit var binding : ActivityDetailArtBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailArtBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    fun saveButtonOp(view: View) {

    }

    fun selectImage(view: View){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view, "Gallery Permission Needed", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",View.OnClickListener {
                    
                }).show()
            }
        }else{
            val goToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            //START PROCESS
        }
    }
}