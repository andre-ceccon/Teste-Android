package br.com.ceccon.andre.utils

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import br.com.ceccon.andre.R
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

fun Activity.verifyStoragePermission(
    onPermissionsGranted: () -> Unit
) {
    Dexter.withContext(this).withPermissions(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ).withListener(object : MultiplePermissionsListener {
        override fun onPermissionsChecked(report: MultiplePermissionsReport) {
            when {
                report.areAllPermissionsGranted() -> onPermissionsGranted.invoke()
                report.isAnyPermissionPermanentlyDenied -> {
                    AlertDialog.Builder(this@verifyStoragePermission).setTitle(R.string.title_dialog_permissao_negada)
                        .setMessage(R.string.Need_storage_permission)
                        .setPositiveButton(R.string.hint_possitiveButton_dialogPermissao) { dialog, _ ->
                            dialog.dismiss()
                            this@verifyStoragePermission.startActivity(
                                Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts(
                                        "package", this@verifyStoragePermission.packageName, null
                                    )
                                )
                            )
                        }
                        .setNegativeButton(R.string.hint_negativeButton_dialogPermissao) { dialog, _ ->
                            dialog.dismiss()
                        }.create().show()
                }
                else -> {
                    Toast.makeText(
                        this@verifyStoragePermission, R.string.Need_storage_permission, Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        override fun onPermissionRationaleShouldBeShown(
            permissions: List<PermissionRequest>,
            token: PermissionToken,
        ) {
            token.continuePermissionRequest()
        }
    }).withErrorListener {
        Toast.makeText(
            this@verifyStoragePermission, "${this@verifyStoragePermission.getString(R.string.error_permission)} ${it.name}", Toast.LENGTH_LONG
        ).show()
    }.check()
}

fun Activity.savePhotoToExternalStorage(
    displayName: String, bmp: Bitmap
): Pair<Boolean, Uri?> {
    Timber.d("Salvando")
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$displayName.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.WIDTH, bmp.width)
            put(MediaStore.Images.Media.HEIGHT, bmp.height)
        }

        var uriFinal: Uri? = null

        try {
            this.contentResolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues
            )?.also { uri ->
                this.contentResolver.openOutputStream(uri).use { outputStream ->
                    if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)) {
                        throw IOException("Couldn't save bitmap")
                    } else {
                        uriFinal = uri
                    }
                }
            } ?: throw IOException("Couldn't create MediaStore entry")
            Pair(true, uriFinal)
        } catch (e: IOException) {
            Timber.d("Error %s", e.message)
            e.printStackTrace()
            Pair(false, uriFinal)
        }
    } else {
        try {
            val filePath = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "$displayName.jpg"
            )

            val outputStream: OutputStream = FileOutputStream(filePath)
            bmp.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)
            outputStream.flush()
            outputStream.close()

            val uri = FileProvider.getUriForFile(
                this,
                applicationContext.packageName + ".provider",
                filePath
            )

            Timber.d(
                "Sucessfully saved in Download Folder, %s, %s, URI: %s",
                filePath.exists(),
                filePath.absolutePath,
                uri
            )
            Pair(true, uri)
        } catch (e: Exception) {
            Timber.d("Error %s", e.message)
            Pair(false, null)
        }
    }
}