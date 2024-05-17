

package com.dirror.music.manager

import android.os.Parcelable
import com.dirror.music.App.Companion.mmkv
import com.dirror.music.music.netease.data.UserDetailData
import com.dirror.music.util.AppConfig
import com.dirror.music.util.Config
import com.dirror.music.util.EMPTY
import kotlinx.parcelize.Parcelize

// @IgnoredOnParcel
private const val DEFAULT_UID = 0L

// @IgnoredOnParcel
private const val DEFAULT_VIP_TYPE = 0

/**
 * 网易云音乐用户

 */
object User {

    val dsoUser: DsoUser = mmkv.decodeParcelable(Config.DSO_USER, DsoUser::class.java, DsoUser())!!

    /** 用户 uid */
    var uid: Long = DEFAULT_UID
        get() = mmkv.decodeLong(Config.UID, DEFAULT_UID)
        set(value) {
            mmkv.encode(Config.UID, value)
            field = value
        }

    /** 用户 Cookie */
    var cookie: String = AppConfig.cookie

    /**
     * 获取用户配置的 NeteaseCloudMusicApi
     */
    var neteaseCloudMusicApi: String = String.EMPTY
        get() = mmkv.decodeString(Config.USER_NETEASE_CLOUD_MUSIC_API_URL, String.EMPTY)!!
        set(value) {
            mmkv.encode(Config.USER_NETEASE_CLOUD_MUSIC_API_URL, value)
            field = value
        }

    /**
     * 用户 VIP 类型
     */
    var vipType: Int = DEFAULT_VIP_TYPE
        get() = mmkv.decodeInt(Config.VIP_TYPE, DEFAULT_VIP_TYPE)
        set(value) {
            mmkv.encode(Config.VIP_TYPE, value)
            field = value
        }

    /** 是否通过 uid 登录 */
    val isUidLogin: Boolean
        get() {
            val uid = mmkv.decodeLong(Config.UID, DEFAULT_UID)
            return uid != DEFAULT_UID
        }

    /** 是否有 cookie */
    val hasCookie: Boolean
        get() = AppConfig.cookie.isNotEmpty()

    /**
     * 是否是 VIP 用户
     */
    fun isVip(): Boolean {
        return vipType != 0
    }

}

/**
 * Dso Music 用户
 */
@Parcelize
data class DsoUser(

    /** 昵称 */
    var nickname: String = String.EMPTY

): Parcelable {

    /**
     * 从网络更新用户数据
     */
    fun updateFromNet(userDetailData: UserDetailData) {
        nickname = userDetailData.profile.nickname
        save()
    }

    /**
     * 保存数据
     */
    private fun save() {
        mmkv.encode(Config.DSO_USER, this)
    }

}



