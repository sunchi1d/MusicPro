package com.dirror.music.util

import android.net.Uri
import android.util.Log
import com.dirror.music.data.*
import com.dirror.music.manager.User
import com.dirror.music.music.compat.CompatSearchData
import com.dirror.music.music.compat.compatSearchDataToStandardPlaylistData
import com.dirror.music.music.netease.Playlist

import com.dirror.music.music.standard.data.*
import com.dso.ext.averageAssignFixLength
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object Api {

    private const val TAG = "API"

    private const val SPLIT_PLAYLIST_NUMBER = 1000 // 切割歌单
    private const val CHEATING_CODE = -460 // Cheating 错误

    suspend fun getPlayListInfo(id: Long): DetailPlaylistInnerData? {
        val url = "${getDefaultApi()}/playlist/detail?id=${id}"
        return HttpUtils.get(url, DetailPlaylistData::class.java, true)?.playlist
    }

    suspend fun getPlayList(id: Long, useCache: Boolean): PackedSongList {
        val params = HashMap<String, String>()
        params["id"] = id.toString()
        if (User.hasCookie) {
            params["cookie"] = AppConfig.cookie
        }
        val url = "${getDefaultApi()}/playlist/detail?hash=${params.hashCode()}"
        val result = HttpUtils.postWithCache(url, params, Playlist.PlaylistData::class.java, useCache)
        val trackIds = ArrayList<Long>()
        result?.result?.playlist?.trackIds?.forEach {
            trackId -> trackIds.add(trackId.id)
        }
        val list = ArrayList<StandardSongData>()
        if (trackIds.size > 0) {
            trackIds.averageAssignFixLength(SPLIT_PLAYLIST_NUMBER).forEach lit@ { subTrackIds ->
                Log.d(TAG, "subTrackIds size is ${subTrackIds.size}")
                val idsBuilder = StringBuilder()
                for (trackId in subTrackIds) {
                    if (idsBuilder.isNotEmpty()) {
                        idsBuilder.append(",")
                    }
                    idsBuilder.append(trackId)
                }
                val ids = idsBuilder.toString()
                val params = HashMap<String, String>()
                params["ids"] = ids
                params["cookie"] = AppConfig.cookie
                val data = HttpUtils.postWithCache("${getDefaultApi()}/song/detail?hash=${ids.hashCode()}",
                    params, CompatSearchData::class.java, useCache)
//                val data = HttpUtils.get("${getDefaultApi()}/song/detail?ids=${ids}", CompatSearchData::class.java)
                data?.result?.apply {
                    if (code == CHEATING_CODE) {
                        toast("-460 Cheating")
                        // 发生了欺骗立刻返回
                        return@lit
                    } else {
                        Log.i(TAG, "get result ${songs.size}")
                        list.addAll(compatSearchDataToStandardPlaylistData(this))
                    }
                }
            }

        }
        Log.d(TAG, "get playlist id $id, size:${list.size} , origin size:${trackIds.size}")
        return PackedSongList(list, result?.isCache?:false)
    }

    suspend fun searchMusic(keyword:String, type:SearchType): StandardSearchResult? {
        val url = "${getDefaultApi()}/cloudsearch?keywords=$keyword&limit=100&type=${SearchType.getSearchTypeInt(type)}"
        val result = HttpUtils.get(url, NeteaseSearchResult::class.java)
        return result?.result?.toStandardResult()
    }

    suspend fun getAlbumSongs(id:Long): StandardAlbumPackage? {
        val url = "${getDefaultApi()}/album?id=${id}"
        HttpUtils.get(url, NeteaseAlbumResult::class.java)?.let {
            return StandardAlbumPackage(it.album.switchToStandard(), it.switchToStandardSongs())
        }
        return null
    }

    suspend fun getSingerSongs(id: Long): StandardSingerPackage? {
        val songs = ArrayList<StandardSongData>()
        var result: ArtistsSongs?
        do {
            val url = "${getDefaultApi()}/artist/songs?id=$id&offset=${songs.size}"
            result = HttpUtils.get(url, ArtistsSongs::class.java, true)
            result?.let {
//                Log.d(TAG, "getSingerSongs result${result.songs.size} ")
                songs.addAll(it.switchToStandardSongs())
            }
        } while (result?.more == true && result.songs.isNotEmpty())

        HttpUtils.get("${getDefaultApi()}/artist/detail?id=$id", ArtistInfoResult::class.java, true)?.data?.artist?.let {
            return StandardSingerPackage(it.switchToStandardSinger(), songs)
        }
        return null
    }




    suspend fun getLoginKey(): NeteaseGetKey? {
        return HttpUtils.get("${getLoginUrl()}/login/qr/key?timestamp=${Date().time}", NeteaseGetKey::class.java)
    }

    suspend fun getLoginQRCode(key: String): NeteaseQRCodeResult? {
        return HttpUtils.get("${getLoginUrl()}/login/qr/create?key=$key&qrimg=1&timestamp=${Date().time}", NeteaseQRCodeResult::class.java)
    }

    suspend fun checkLoginResult(key: String): NeteaseLoginResult? {
        return HttpUtils.get("${getLoginUrl()}/login/qr/check?key=$key&timestamp=${Date().time}", NeteaseLoginResult::class.java)
    }

    suspend fun getUserInfo(cookie: String): NeteaseUserInfo? {
        return HttpUtils.post("${getLoginUrl()}/user/account", Utils.toMap("cookie", cookie) , NeteaseUserInfo::class.java)
    }

    private fun getLoginUrl() :String {
        return User.neteaseCloudMusicApi
    }

    private fun getDefaultApi() :String {
        var api = User.neteaseCloudMusicApi
        if (api.isEmpty()) {
            api = "https://olbb.vercel.app"
        }
        return api
    }

}