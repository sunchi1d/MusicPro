package com.dirror.music.music.netease


import com.dirror.music.api.API_pro
import com.dirror.music.manager.User

import com.dirror.music.music.netease.data.SongUrlData
import com.dirror.music.util.AppConfig
import com.dirror.music.util.HttpUtils
import com.dirror.music.util.MagicHttp
import com.google.gson.Gson
import okhttp3.FormBody

object SongUrl {

    fun getSongUrlCookie(id: String, success: (String) -> Unit) {
        var api = User.neteaseCloudMusicApi
        if (api.isEmpty()) {
            api = API_pro
        }
        val requestBody = FormBody.Builder()
            .add("crypto", "api")
            .add("cookie", AppConfig.cookie)
            .add("withCredentials", "true")
            .add("realIP", "211.161.244.70")
            .add("id", id)
            .build()
        MagicHttp.OkHttpManager().newPost("${api}/song/url", requestBody, {
            try {
                val songUrlData = Gson().fromJson(it, SongUrlData::class.java)
                success.invoke(songUrlData.data[0].url ?: "")
            } catch (e: Exception) {
                // failure.invoke(ErrorCode.ERROR_JSON)
            }
        }, {

        })
    }



}