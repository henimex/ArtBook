package com.hendev.artbook


import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.hendev.artbook.databinding.ActivityDetailArtBinding
import java.io.ByteArrayOutputStream

class DetailArt : AppCompatActivity() {

    private lateinit var binding: ActivityDetailArtBinding
    private lateinit var activityResult: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var selectedBitmap: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailArtBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        startIntentLauncherForImage();
        startPermissionLauncher();
    }

    fun saveButtonOp(view: View) {

        val artName = binding.txtArtName.text.toString()
        val artistName = binding.txtArtist.text.toString()
        val year = binding.txtYear.text.toString()

        if (selectedBitmap != null){
            val smallBitmap = makeSmallerBitmap(selectedBitmap!!,300)
            val outputStream = ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG,50, outputStream)
            val byteArray = outputStream.toByteArray()

            val model = ArtModel(artName, artistName, year, byteArray)
            databaseOperations(model);
        }
    }

    private fun databaseOperations(data: ArtModel){
        try {
            val database = this.openOrCreateDatabase("ArtsBook", MODE_PRIVATE, null)
            database.execSQL("CREATE TABLE IF NOT EXISTS Arts (id INTEGER PRIMARY KEY, art VARCHAR, artist VARCHAR, year VARCHAR, image BLOB)")
            val sqlString = "INSERT INTO Arts (art, artist, year, image) VALUES (?, ?, ?, ?)"
            val statement = database.compileStatement(sqlString)
            statement.bindString(1, data.artName)
            statement.bindString(2, data.artistName)
            statement.bindString(3, data.artYear)
            statement.bindBlob(4, data.imageArray)
            statement.execute()

        }catch (e: Exception){
            e.printStackTrace()
        }

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun makeSmallerBitmap(image: Bitmap, maxSize:Int): Bitmap {
        var width = image.width
        var height = image.width

        val bitmapRatio : Double = width.toDouble() / height.toDouble()

        if (bitmapRatio > 1){
            //Landscape
            width = maxSize
            val scaledHeight = width / bitmapRatio
            height = scaledHeight.toInt()
        }else{
            //Portrait
            height = maxSize
            val scaledWidth = height * bitmapRatio
            width = scaledWidth.toInt()
        }

        return Bitmap.createScaledBitmap(image,width,height,true);
    }

    fun selectImage(view: View) {
        getImage(view, activityResult, permissionLauncher)
    }

    private fun startIntentLauncherForImage() {
        activityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val intentFromResult = result.data
                    if (intentFromResult != null) {
                        val imageData = intentFromResult.data
                        if (imageData != null) {
                            try {
                                if (Build.VERSION.SDK_INT >= 28) {
                                    val source = ImageDecoder.createSource(contentResolver, imageData)
                                    selectedBitmap = ImageDecoder.decodeBitmap(source)
                                    binding.imgArt.setImageBitmap(selectedBitmap)
                                } else {
                                    selectedBitmap = MediaStore.Images.Media.getBitmap( contentResolver, imageData)
                                    binding.imgArt.setImageBitmap(selectedBitmap)
                                }
                            } catch (exception: Exception) {
                                exception.printStackTrace()
                            }
                        }
                    }
                }
            }
    }
    private fun startPermissionLauncher() {
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                if (result){
                    val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResult.launch(intentToGallery)
                }else {
                    Toast.makeText(this, "Permission Needed", Toast.LENGTH_LONG).show()
                }
            }
    }

    //TODO::Refactor Area

    private fun grantPermMainMethod(view: View, permName: String, title: String, message: String, intentLauncher: ActivityResultLauncher<Intent>, permLauncher: ActivityResultLauncher<String>){
        if (ContextCompat.checkSelfPermission(this, permName) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permName)) {
                Snackbar
                    .make(view, message, Snackbar.LENGTH_INDEFINITE)
                    .setAction(title, View.OnClickListener {permLauncher.launch(permName)})
                    .show()
            } else {
                permLauncher.launch(permName)
            }
        } else {
            val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intentLauncher.launch(intentToGallery)
        }
    }

    private fun getImage(view: View, intentLauncher: ActivityResultLauncher<Intent>, permLauncher: ActivityResultLauncher<String>){
        val highPermName = Manifest.permission.READ_MEDIA_IMAGES
        val lowerPermName = Manifest.permission.READ_EXTERNAL_STORAGE
        val titleForSnackBar = "GRANT"
        val messageForSnackBar = "Permission Needed"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            grantPermMainMethod(view, highPermName, titleForSnackBar, messageForSnackBar, intentLauncher, permLauncher)
        } else {
            grantPermMainMethod(view, lowerPermName, titleForSnackBar, messageForSnackBar, intentLauncher, permLauncher)
        }
    }

    //Deprecated Area
    private fun getImageLegacy(view: View){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)) {
                    Snackbar
                        .make(view, "Gallery Permission Needed", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Give Permission", View.OnClickListener {permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)})
                        .show()
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            } else {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResult.launch(intentToGallery)
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Snackbar
                        .make(view, "Gallery Permission Needed", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Give Permission", View.OnClickListener {permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)})
                        .show()
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResult.launch(intentToGallery)
            }
        }
    }
}