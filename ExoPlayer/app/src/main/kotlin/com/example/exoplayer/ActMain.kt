package com.example.exoplayer

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.example.exoplayer.databinding.ActMainBinding

class ActMain : AppCompatActivity() {

    private lateinit var mBinding: ActMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initView()
        setClickListener()
    }

    private fun initView() {}

    private fun setClickListener() {

        mBinding.btnVideo.setOnClickListener {

            Intent(this@ActMain, ActVideo::class.java).apply {

                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(this)
            }
        }
    }
}