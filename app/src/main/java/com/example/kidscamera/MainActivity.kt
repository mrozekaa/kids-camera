package com.example.kidscamera

import android.Manifest
import android.app.ActivityManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.CameraSelector
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.kidscamera.camera.CameraPreview
import com.example.kidscamera.paint.PaintPreview
import com.example.kidscamera.paint.randomColor
import com.example.kidscamera.ui.theme.KidsCameraTheme
import com.example.kidscamera.util.Permission
import com.google.accompanist.permissions.ExperimentalPermissionsApi


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemBars()
        setContent {
            KidsCameraTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    MainContent(Modifier.fillMaxSize())
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemBars()
    }

    override fun onPause() {
        super.onPause()
        val activityManager = applicationContext
            .getSystemService(ACTIVITY_SERVICE) as ActivityManager
        activityManager.moveTaskToFront(taskId, 0)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return true
    }

    private fun hideSystemBars() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }


    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun MainContent(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        val cameraSelector: MutableState<CameraSelector> = remember {
            mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA)
        }
        var paintEnabled by remember { mutableStateOf(false) }
        var captureButtonColor by remember { mutableStateOf(Color.Red) }
        val fakeGalleryImagesList = listOf(R.drawable.avatar1, R.drawable.avatar2, R.drawable.avatar3, R.drawable.avatar4, R.drawable.avatar5, R.drawable.avatar6)
        var fakeGalleryImage by remember { mutableStateOf(fakeGalleryImagesList.first()) }
        Permission(
            permission = Manifest.permission.CAMERA,
            rationale = "Hi, Hello!\n" +
                    "Your kid wanted to take a picture, so I'm going to have to ask for permission.",
            permissionNotAvailableContent = {
                Column(modifier) {
                    Text("O noes! No Camera!")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            context.startActivity(
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                }
                            )
                        }
                    ) {
                        Text("Open Settings")
                    }
                }
            }
        ) {
            Box {
                Button(
                    modifier = Modifier
                        .size(150.dp)
                        .padding(25.dp)
                        .align(Alignment.BottomCenter),
                    colors = ButtonDefaults.buttonColors(captureButtonColor),
                    border = BorderStroke(8.dp, Color.White),
                    onClick = {
                        captureButtonColor = Color(Int.randomColor())
                        cameraSelector.value =
                            if (cameraSelector.value == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
                            else CameraSelector.DEFAULT_BACK_CAMERA
                    }
                ) {}
                IconButton(
                    modifier = Modifier
                        .size(150.dp)
                        .align(Alignment.BottomEnd),
                    onClick = {
                        paintEnabled = !paintEnabled
                    }
                ) {
                    Icon(painterResource(if (paintEnabled) {
                        R.drawable.baseline_format_paint_24
                    } else {
                        R.drawable.baseline_photo_camera_24
                    }), "Paint button")
                }
                Image(
                    painter = painterResource(fakeGalleryImage),
                    contentDescription = "image from gallery",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(150.dp)
                        .padding(35.dp)
                        .clip(CircleShape)
                        .align(Alignment.BottomStart)
                        .border(2.dp, Color.Gray, CircleShape)
                        .clickable{
                            val index = fakeGalleryImagesList.indexOf(fakeGalleryImage)
                            val nextIndex = if (index == fakeGalleryImagesList.size -1){
                                0
                            }else{
                                index + 1
                            }
                            fakeGalleryImage = fakeGalleryImagesList[nextIndex]
                        }
                )
                CameraPreview(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(0.dp, 0.dp, 0.dp, 150.dp),
                    cameraSelector = cameraSelector.value
                )
                if(paintEnabled){
                    PaintPreview(captureButtonColor)
                }
            }
        }
    }
}