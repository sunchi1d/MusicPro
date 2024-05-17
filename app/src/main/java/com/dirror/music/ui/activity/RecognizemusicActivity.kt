package com.dirror.music.ui.activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.dirror.music.App
import com.dirror.music.R
import com.dirror.music.adapter.TopListAdapter
import com.dirror.music.databinding.ActivityRecognizemusicBinding
import com.dirror.music.music.netease.TopList
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.ui.playlist.TAG_NETEASE
import com.dirror.music.util.runOnMainThread
import com.dirror.music.widget.RippleAnimationView
import eightbitlab.com.blurview.RenderScriptBlur

class RecognizemusicActivity : BaseActivity(){
    private lateinit var binding:ActivityRecognizemusicBinding
//    var imageView: ImageView? = null
    lateinit var rippleAnimationView: RippleAnimationView
    override fun initBinding() {
        binding = ActivityRecognizemusicBinding.inflate(layoutInflater)
        miniPlayer = binding.miniPlayer
        setContentView(binding.root)
    }

    override fun initView() {
        //setContentView(R.layout.activity_recognizemusic)
//        imageView =findViewById(R.id.ImageView)
        rippleAnimationView=findViewById(R.id.layout_RippleAnimation)
        val radius = 20f
        val decorView: View = window.decorView
        val windowBackground: Drawable = decorView.background
        binding.blurView.setupWith(decorView.findViewById(R.id.clRecognize))
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(this))
            .setBlurRadius(radius)
            .setHasFixedTransformationMatrix(true)
        binding.ImageView.setOnClickListener({
            if (rippleAnimationView.isAnimationRunning()) {
                rippleAnimationView.stopRippleAnimation()
            } else {
                rippleAnimationView.startRippleAnimation()


            }
        })

    }

}