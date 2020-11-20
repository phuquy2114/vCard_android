package com.uits.vcard

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.uits.vcard.utils.SavingFileUtil
import ezvcard.Ezvcard
import java.io.File
import java.net.URI
import java.net.URISyntaxException


class MainActivity : AppCompatActivity() {

    private val TAG = "xxx"
    private val FILE_SELECT_CODE = 0
    private var filePath: String? = null
    private lateinit var mTxtSelector: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mTxtSelector = findViewById(R.id.mTxtSelector)

        mTxtSelector.setOnClickListener {
            showFileChooser()
        }
    }

    private fun showFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE)
        } catch (ex: ActivityNotFoundException) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            FILE_SELECT_CODE -> if (resultCode == RESULT_OK) {
                // Get the Uri of the selected file
                val uri: Uri? = data!!.data
                Log.d(TAG, "File Uri: " + uri.toString())
                // Get the path

                val path: String? = SavingFileUtil.getPathFromUri(this@MainActivity, uri)
                Log.d(TAG, "File Path: $path")
                readVCF(path.toString())
                // Get the file instance
                // File file = new File(path);
                // Initiate the upload
            }
        }
    }

    fun getRealPathFromURI(context: Context, contentUri: Uri?): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri!!, proj, null, null, null)
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(column_index)
        } finally {
            cursor?.close()
        }
    }

    fun readVCF(path: String) {
        try {
            val file: File = File(path)
            val vcards = Ezvcard.parse(file).all()
            for (vcard in vcards) {
                println("Name: " + vcard.formattedName.value)
                println("Telephone numbers:")
                for (tel in vcard.telephoneNumbers) {
                    println(tel.types.toString() + ": " + tel.text)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(URISyntaxException::class)
    fun getPath(context: Context, uri: Uri): String? {
        if ("content".equals(uri.scheme, ignoreCase = true)) {
            val projection = arrayOf("_data")
            var cursor: Cursor? = null
            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null)
                val column_index: Int = cursor!!.getColumnIndexOrThrow("_data")
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index)
                }
            } catch (e: java.lang.Exception) {
                // Eat it
            }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }
}