
@file:Suppress("unused", "UNUSED_PARAMETER")

package com.dirror.music

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.Keep
import androidx.lifecycle.MutableLiveData
import com.dirror.music.manager.ActivityManager
import com.dirror.music.manager.CloudMusicManager
import com.dirror.music.room.AppDatabase
import com.dirror.music.service.MusicService
import com.dirror.music.service.MusicServiceConnection
import com.dirror.music.util.*
import com.tencent.mmkv.MMKV
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import kotlinx.coroutines.*
import java.io.File
import java.util.*
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Music App
 *
 */
@Keep
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        // 全局 context
        context = applicationContext
        // MMKV 初始化
        MMKV.initialize(context)
        mmkv = MMKV.defaultMMKV()!!
        // 管理初始化
        activityManager = ActivityManager()
        cloudMusicManager = CloudMusicManager()
        // 初始化数据库
        appDatabase = AppDatabase.getDatabase(this)
        // 安全检查
        checkSecure()

        if (mmkv.decodeBool(Config.DARK_THEME, false)) {
            DarkThemeUtil.setDarkTheme(true)
        }

        realIP = "175.16.1.195"
        coroutineScope.launch {
            val lastIP = "LAST_IP"
            val lastIPExpiredTime = "LAST_IP_TIME" // 过期时间
            val ip = mmkv.decodeString(lastIP, "")
            val now = System.currentTimeMillis()
            val expiredTime = mmkv.decodeLong(lastIPExpiredTime, now)
            if (ip == null || ip.isEmpty() || expiredTime < now) {
                Log.i(TAG, "ip is expired.")
                realIP = ChineseIPData.getRandomIP(this@App)
                mmkv.encode(lastIP, realIP)
                mmkv.encode(lastIPExpiredTime, now + 24 * 60 * 60 * 1000)
            } else{
                realIP = ip
            }
        }
    }

    /**
     * 安全检查
     */
    private fun checkSecure() {
        if (Secure.isSecure()) {
            // 初始化友盟
            UMConfigure.init(context, UM_APP_KEY, "", UMConfigure.DEVICE_TYPE_PHONE, "")
            // 选用 AUTO 页面采集模式
            MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)
            // 开启音乐服务
            startMusicService()
        } else {
            Secure.killMyself()
        }
    }

    /**
     * 启动音乐服务
     */
    private fun startMusicService() {
        // 通过 Service 播放音乐，混合启动
        val intent = Intent(this, MusicService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        // 绑定服务
        bindService(intent, musicServiceConnection, BIND_AUTO_CREATE)
    }

    companion object {

        private val TAG = this::class.java.simpleName

        const val UM_APP_KEY = "5fb38e09257f6b73c0961382"

        lateinit var mmkv: MMKV

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        //MutableLiveData 是 Android Jetpack 中 LiveData 类的一个可变子类，用于持有和管理可观察的数据。
        // 它是 MVVM（Model-View-ViewModel）架构的核心组件之一，
        // 常用于在视图模型（ViewModel）中保存数据，并在数据变化时通知观察者（通常是 UI 控件）进行更新。
        var musicController = MutableLiveData<MusicService.MusicController?>()

        val musicServiceConnection by lazy { MusicServiceConnection() } // 音乐服务连接

        @Deprecated("过时，使用 StartActivity")
        lateinit var activityManager: ActivityManager

        lateinit var cloudMusicManager: CloudMusicManager

        val coroutineScope = CoroutineScope(EmptyCoroutineContext)

        /** 数据库 */
        lateinit var appDatabase: AppDatabase

        lateinit var realIP: String
    }

}