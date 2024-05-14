package com.dirror.music.api

import com.dirror.music.manager.User

/**
 * 网易云 API 地址
 */
object CloudMusicApi {

    /**
     * 默认搜索关键词
     * 说明 : 调用此接口 , 可获取默认搜索关键词
     */
    val SEARCH_DEFAULT = "${User.neteaseCloudMusicApi}/search/default"

    /**
     * 热搜列表(详细)
     * 说明 : 调用此接口,可获取热门搜索列表
     */
    val SEARCH_HOT_DETAIL = "${User.neteaseCloudMusicApi}/search/hot/detail"

    /**
     * 获取歌手部分信息和歌手单曲
     * 说明 : 调用此接口 , 传入歌手 id, 可获得歌手部分信息和热门歌曲
     * 必选参数 : id: 歌手 id, 可由搜索接口获得
     * 接口地址 : /artists
     * 调用例子 : /artists?id=6452
     */
     val ARTISTS = "${User.neteaseCloudMusicApi}/artists"

    /**
     * 获取歌词
     * 说明 : 调用此接口 , 传入音乐 id 可获得对应音乐的歌词 ( 不需要登录 )
     * 必选参数 : id: 音乐 id
     * 接口地址 : /lyric
     * 调用例子 : /lyric?id=33894312
     */
     val LYRIC = "${User.neteaseCloudMusicApi}/lyric"

    /**
     * 获取用户歌单
     * 说明 : 登录后调用此接口 , 传入用户 id, 可以获取用户歌单
     * 必选参数 : uid : 用户 id
     * 可选参数 :
     * limit : 返回数量 , 默认为 30
     * offset : 偏移数量，用于分页 , 如 :( 页数 -1)*30, 其中 30 为 limit 的值 , 默认为 0
     * 接口地址 : /user/playlist
     * 调用例子 : /user/playlist?uid=32953014
     */
     val USER_PLAYLIST = "${User.neteaseCloudMusicApi}/user/playlist"

    /**
     * 推荐新音乐
     * 说明 : 调用此接口 , 可获取推荐新音乐
     * 可选参数 : limit: 取出数量 , 默认为 10 (不支持 offset)
     * 接口地址 : /personalized/newsong
     * 调用例子 : /personalized/newsong
     */
    //const val PERSONALIZED_NEW_SONG = "${API_MUSIC_ELEUU}/personalized/newsong"
     val PERSONALIZED_NEW_SONG = "${User.neteaseCloudMusicApi}/personalized/newsong"
}