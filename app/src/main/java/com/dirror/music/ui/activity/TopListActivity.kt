package com.dirror.music.ui.activity

import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.dirror.music.App
import com.dirror.music.R
import com.dirror.music.adapter.TopListAdapter
import com.dirror.music.databinding.ActivityTopListBinding
import com.dirror.music.music.netease.TopList
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.ui.playlist.TAG_NETEASE
import com.dirror.music.util.runOnMainThread
import eightbitlab.com.blurview.RenderScriptBlur

class TopListActivity : BaseActivity() {

    private lateinit var binding: ActivityTopListBinding

    override fun initBinding() {
        binding = ActivityTopListBinding.inflate(layoutInflater)
        miniPlayer = binding.miniPlayer
        setContentView(binding.root)
    }

    override fun initView() {
        TopList.getTopList(this, {
            runOnMainThread {
                binding.rvTopList.layoutManager = LinearLayoutManager(this)
                binding.rvTopList.adapter = TopListAdapter(it) { listData ->
                    App.activityManager.startPlaylistActivity(this, TAG_NETEASE, listData.id.toString())
                }
            }
        }, {

        })

        val radius = 20f
        val decorView: View = window.decorView
        val windowBackground: Drawable = decorView.background
        binding.blurView.setupWith(decorView.findViewById(R.id.rvTopList))
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(this))
            .setBlurRadius(radius)
            .setHasFixedTransformationMatrix(true)
    }

}