package com.dirror.music.music.standard

import com.dirror.music.music.standard.data.SOURCE_NETEASE
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.util.MagicHttp
import com.google.gson.Gson

/**
 * 搜索歌词
 */
object SearchLyric {

    fun getLyricString(songData: StandardSongData, success: (String) -> Unit) {
        var url = ""
        when (songData.source) {
            SOURCE_NETEASE -> {
                url = "http://music.eleuu.com/lyric?id=${songData.id}"
            }

        }

        MagicHttp.OkHttpManager().newGet(url, { response ->
            var lyric = response

            when (songData.source) {
                SOURCE_NETEASE -> {
                    if (Gson().fromJson(lyric, NeteaseSongLyric::class.java).lrc != null) {
                        lyric = Gson().fromJson(lyric, NeteaseSongLyric::class.java).lrc?.lyric.toString()
                        success.invoke(lyric)
                    } else {
                        success.invoke("")
                    }
                }
            }

        }, {

        })
    }


    /**
     * 网易云歌词数据类
     */
    data class NeteaseSongLyric(
        val lrc: NeteaseSongLrc?
    ) {
        data class NeteaseSongLrc(
            val lyric: String?
        )
    }

}
