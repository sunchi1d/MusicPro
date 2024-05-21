package com.dirror.music.ui.activity


import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.webkit.JavascriptInterface
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dirror.music.App
import com.dirror.music.R
import com.dirror.music.adapter.SingerAdapter
import com.dirror.music.adapter.SongAdapter
import com.dirror.music.data.SearchType
import com.dirror.music.databinding.ActivityRecognizemusicBinding
import com.dirror.music.music.netease.RecognizeMusicSong
import com.dirror.music.music.netease.data.toStandardSongDataArrayList
import com.dirror.music.music.standard.data.StandardSinger
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.service.PlayQueue
import com.dirror.music.service.playMusic
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.ui.dialog.SongMenuDialog
import com.dirror.music.ui.playlist.SongPlaylistActivity
import com.dirror.music.ui.playlist.TAG_NETEASE
import com.dirror.music.ui.viewmodel.RecognizemusicViewmodel
import com.dirror.music.ui.viewmodel.RecommendActivityViewModel
import com.dirror.music.util.Api
import com.dirror.music.util.runOnMainThread
import com.dirror.music.util.toast
import com.dirror.music.widget.RippleAnimationView
import com.dso.ext.toArrayList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RecognizemusicActivity : BaseActivity() {

    private lateinit var binding: ActivityRecognizemusicBinding
    lateinit var webView: WebView
    var uploadMessage: ValueCallback<Array<Uri>>? = null
    private var mUploadMessage: ValueCallback<Uri>? = null
    //    var imageView: ImageView? = null
    lateinit var rippleAnimationView: RippleAnimationView
    val REQUEST_SELECT_FILE: Int = 100
    private val FILECHOOSER_RESULTCODE = 2
    private val REQUEST_RECORD_AUDIO_PERMISSION = 200
    private val RECORD_AUDIO_PERMISSION_REQUEST_CODE = 123
    lateinit var songname:String
    private val REQUEST_SECOND_ACTIVITY = 1
    private val recognizemusicViewmodel: RecognizemusicViewmodel by viewModels()

    override fun initBinding() {
        binding = ActivityRecognizemusicBinding.inflate(layoutInflater)
        miniPlayer = binding.miniPlayer
        setContentView(binding.root)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun initView() {
        super.initView()
        webView=findViewById(R.id.WebView)
        val webSettings: WebSettings = webView.getSettings()
        webView.settings.allowFileAccess =true
        webView.settings.domStorageEnabled = true
        webView.settings.setSupportZoom(true)
        webView.settings.builtInZoomControls = true
        webView.settings.displayZoomControls = false
        webView.settings.allowFileAccess = true
        webView.settings.allowFileAccessFromFileURLs = true
        webView.settings.allowUniversalAccessFromFileURLs = true
        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.settings.javaScriptCanOpenWindowsAutomatically=true
        val timestamp = System.currentTimeMillis()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO_PERMISSION)
            }
        }
        webSettings.setAllowContentAccess(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE)
        webSettings.setDomStorageEnabled(true)
        webSettings.setBuiltInZoomControls(false);
        webSettings.setJavaScriptEnabled(true)
      //  webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE)
        webSettings.setLoadWithOverviewMode(true)
        webSettings.setAllowFileAccess(true)
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                //view.loadUrl("file:///android_asset/index.html?timestamp=$timestamp")
                //view.loadUrl("http://www.baidu.com")
                return false
            }
        }

        WebView.setWebContentsDebuggingEnabled(true)

        webView.setWebChromeClient(object : WebChromeClient() {

            override fun onPermissionRequest(request: PermissionRequest?) {
                val permissions = request?.resources ?: return
                if (permissions.contains(PermissionRequest.RESOURCE_AUDIO_CAPTURE)) {
                    val parent = webView
                    if (ContextCompat.checkSelfPermission(this@RecognizemusicActivity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this@RecognizemusicActivity, arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_PERMISSION_REQUEST_CODE)
                    } else {
                        request.grant(arrayOf(PermissionRequest.RESOURCE_AUDIO_CAPTURE))
                    }
                }
            }

            // For Lollipop 5.0+ Devices
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onShowFileChooser(
                mWebView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                if (uploadMessage != null) {
                    uploadMessage!!.onReceiveValue(null)
                    uploadMessage = null
                }
                uploadMessage = filePathCallback
                val intent = fileChooserParams.createIntent()
                try {
                    startActivityForResult(intent, REQUEST_SELECT_FILE)
                } catch (e: ActivityNotFoundException) {
                    uploadMessage = null
                    Toast.makeText(baseContext, "Cannot Open File Chooser", Toast.LENGTH_LONG)
                        .show()
                    return false
                }
                return true
            }
        })
        val url = "file:///android_asset/index.html?timestamp=$timestamp"
        //val url = "https://mos9527.github.io/ncm-afp/"
         webView.loadUrl(url)
       //webView.loadUrl("https://www.baidu.com")
//        webView.addJavascriptInterface(new LocalJavaScript(this),"anime")
        webView.addJavascriptInterface(WebAppInterface(), "Android")
    }

    inner class WebAppInterface {
        @JavascriptInterface
        fun receiveinfo(songname: String) {
            // 处理从 HTML 页面接收到的歌曲 ID

                //this@RecognizemusicActivity.songname=songname
                //App.musicController.value?.setPersonFM(false)

//                GlobalScope.launch{
//                    val result= Api.searchMusic(songname, SearchType.SINGLE)
//                    withContext(Dispatchers.Main){
//                        if (result != null) {
//                        }
//                    }
//                }

                //App.musicController.value?.setRecognize(songname)

                val intent = Intent(this@RecognizemusicActivity, SearchActivity::class.java)
                intent.putExtra("keyword", songname)
               startActivityForResult(intent, REQUEST_SECOND_ACTIVITY) // 启动搜索页面

               // App.activityManager.startPlayerActivity(this@RecognizemusicActivity)
//                Toast.makeText(baseContext, "name=$songname", Toast.LENGTH_LONG)
//                    .show()
            
        }
    }
//    private fun search(){
//        var keywords = songname
//        if (keywords != "") {
//
//            val intent = Intent(this, SearchActivity::class.java)
//            intent.putExtra("keyword", songname)
//            startActivity(intent) // 启动搜索页面
//
//
//        }
//    }




    @Override
    public override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
                if (uploadMessage == null) return
                uploadMessage!!.onReceiveValue(
                    WebChromeClient.FileChooserParams.parseResult(
                        resultCode,
                        intent
                    )
                )
                uploadMessage = null
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage) return
            // Use MainActivity.RESULT_OK if you're implementing WebView inside Fragment
            // Use RESULT_OK only if you're implementing WebView inside an Activity
            val result =
                if (intent == null || resultCode != RESULT_OK) null else intent.data
            mUploadMessage!!.onReceiveValue(result)
            mUploadMessage = null
        } else Toast.makeText(baseContext, "无法上传", Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RECORD_AUDIO_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 用户授权了录音权限
                    webView.reload() // 重新加载 WebView 来应用权限
                } else {
                    // 用户拒绝了录音权限
                }
            }
        }
    }
}

