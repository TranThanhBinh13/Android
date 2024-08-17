package com.example.app_tv.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.example.app_tv.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginButton = findViewById<Button>(R.id.buttonLogin)
        val registerButton = findViewById<Button>(R.id.buttonRegister)

        loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Thiết lập VideoView
        val videoView = findViewById<VideoView>(R.id.videoView)

        // Đặt đường dẫn video. Có thể là từ local hoặc URL
        val videoPath = "android.resource://" + packageName + "/" + R.raw.sample_video

        val uri = Uri.parse(videoPath)
        videoView.setVideoURI(uri)

        // Thêm MediaController để có các nút điều khiển như Play, Pause, etc.
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

        // Thiết lập lắng nghe sự kiện khi video đã sẵn sàng để phát
        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.setVolume(0f, 0f) // Tắt tiếng video
            videoView.start() // Tự động phát video
        }

        // Thiết lập lắng nghe sự kiện khi video phát xong để phát lại
        videoView.setOnCompletionListener {
            videoView.start() // Tự động phát lại video
        }
    }
}

