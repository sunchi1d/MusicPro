

package com.dirror.music.ui.viewmodel

import androidx.annotation.Keep
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dirror.music.manager.User
import com.dirror.music.music.netease.data.UserDetailData
import com.dirror.music.util.AppConfig
import com.dirror.music.util.EMPTY
import com.dirror.music.util.ErrorCode
import com.dirror.music.util.HttpUtils
import com.dirror.music.util.sky.SkySecure
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 手机号登录 ViewModel

 */
@Keep
class LoginCellphoneViewModel : ViewModel() {

    /**
     * 手机号登录
     */
    fun loginByCellphone(
        api: String,
        phone: String,
        password: String,
        success: (UserDetailData) -> Unit,
        failure: (Int) -> Unit
    ) {
        val passwordMD5 = SkySecure.getMD5(password)
        viewModelScope.launch {
            val map = HashMap<String, String>()
            map["phone"] = phone
            map["countrycode"] = "86"
            map["md5_password"] = passwordMD5
            val userDetail =
                HttpUtils.loginPost("${api}/login/cellphone", map, UserDetailData::class.java)
            if (userDetail != null && userDetail.code == 200) {
                User.apply {
                    AppConfig.cookie = userDetail.cookie ?: String.EMPTY
                    uid = userDetail.profile.userId
                    vipType = userDetail.profile.vipType
                }
                success.invoke(userDetail)
            } else {
                failure.invoke(ErrorCode.ERROR_MAGIC_HTTP)
            }
        }
    }

}