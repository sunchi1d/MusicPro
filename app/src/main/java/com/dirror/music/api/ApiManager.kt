package com.dirror.music.api

import com.dirror.music.manager.User


/**
 *MusicPro 默认 API
 */
const val API_DEFAULT = "https://cloudmusic.moriafly.xyz"

/**
 *MusicPro  备用接口 vercel部署
 */
const val API_DSO = "https://netease-cloud-music-houz20ft2-sunchi1ds-projects.vercel.app"
//const val API_DSO = "https://service-4bv4633i-1324183288.nj.tencentapigw.com"
//const val API_DSO = "https://netease-cloud-music-api-lemon.vercel.app"
/**
 * @名称 音乐 网易云 api
 * @限制 暂无
 * @测试 主要 api，绝大多数稳定
 * @类型 适用 NodeJs 版
 */
const val API_MUSIC_API = "http://musicapi.leanapp.cn"

/**
 * @名称 网易云简单 api
 * @限制 暂无，很快，稳定
 * @测试 用于歌词，很简单
 * @获取歌词 https://api.fczbl.vip/163/?type=lrc&id=1438470159 // 传入歌曲 id
 *
 */
const val API_FCZBL_VIP = "https://api.fczbl.vip/163"



/**
 * 网易云官方 api
 */
const val API_NETEASE = "https://music.163.com/api"

/**
 * @类型 适用 NodeJs 版
 * @例子 http://music.eleuu.com/banner?type=1
 */
const val API_MUSIC_ELEUU = "http://music.eleuu.com"





