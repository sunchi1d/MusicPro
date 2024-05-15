package com.dirror.music.music.netease


import com.dirror.music.api.API_DSO
import com.dirror.music.manager.User
import com.dirror.music.music.dirror.SearchSong
import com.dirror.music.music.netease.data.SongUrlData
import com.dirror.music.util.AppConfig
import com.dirror.music.util.HttpUtils
import com.dirror.music.util.MagicHttp
import com.google.gson.Gson
import okhttp3.FormBody

object SongUrl {


    fun getSongUrl(id: String): String {
        return if (SearchSong.getDirrorSongUrl(id) != "") {
            SearchSong.getDirrorSongUrl(id)
        } else {
            "https://localhost:4070/v1/wy/song?id=${id}"
        }
    }

    fun getSongUrlCookie(id: String, success: (String) -> Unit) {
        var api = User.neteaseCloudMusicApi
        if (api.isEmpty()) {
            api = "https://olbb.vercel.app"
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

    suspend fun getSongUrlN(id: String): String {
        val url = "$API_DSO/song/url?id=$id"
        val result = HttpUtils.get(url, SongUrlData::class.java)
        return result?.data?.get(0)?.url ?: getSongUrl(id)
    }

}