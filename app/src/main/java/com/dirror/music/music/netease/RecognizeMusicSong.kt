package com.dirror.music.music.netease
import com.dirror.music.api.API_pro
import com.dirror.music.manager.User
import com.dirror.music.music.netease.data.PersonFMData
import com.dirror.music.music.netease.data.RecognizeMusicData
import com.dirror.music.music.netease.data.SongUrlData
import com.dirror.music.music.netease.data.toSongList
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.util.AppConfig
import com.dirror.music.util.ErrorCode
import com.dirror.music.util.MagicHttp
import com.google.gson.Gson
import okhttp3.FormBody

object RecognizeMusicSong {
    //private  val API = "${User.neteaseCloudMusicApi}/song"
    fun get(success: (ArrayList<StandardSongData>) -> Unit, failure: (Int) -> Unit,kewords:String) {

//        val requestBody = FormBody.Builder()
//            .add("crypto", "weapi")
//            .add("cookie", AppConfig.cookie)
//            .add("withCredentials", "true")
//            .add("realIP", "211.161.244.70")
//            .add("id",id)
//            .build()

//
//        MagicHttp.OkHttpManager().newPost(API, requestBody, {
//            try {
//                val recognizeMusicData = Gson().fromJson(it, RecognizeMusicData::class.java)
//                success(recognizeMusicData.toSongList())
//            } catch (e: Exception) {
//                failure(ErrorCode.ERROR_JSON)
//            }
//        }, {
//            failure(ErrorCode.ERROR_MAGIC_HTTP)
//        })
        var api = User.neteaseCloudMusicApi
        if (api.isEmpty()) {
            api = API_pro
        }
        val requestBody = FormBody.Builder()
            .add("crypto", "api")
            .add("cookie", AppConfig.cookie)
            .add("withCredentials", "true")
            .add("realIP", "211.161.244.70")
            .add("kewords", kewords)
            .build()
        MagicHttp.OkHttpManager().newPost("${api}/song/url", requestBody, {
            try {
                val recognizeMusicData = Gson().fromJson(it, RecognizeMusicData::class.java)
                success.invoke(recognizeMusicData.toSongList())
            } catch (e: Exception) {
                // failure.invoke(ErrorCode.ERROR_JSON)
            }
        }, {
        })
    }


}