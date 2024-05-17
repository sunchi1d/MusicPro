

package com.dirror.music.ui.activity

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import com.dirror.music.R
import com.dirror.music.databinding.ActivityLoginByPhoneBinding
import com.dirror.music.manager.ActivityCollector
import com.dirror.music.manager.User
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.ui.live.NeteaseCloudMusicApiActivity
import com.dirror.music.ui.viewmodel.LoginCellphoneViewModel
import com.dirror.music.util.runOnMainThread
import com.dirror.music.util.sky.SkySecure
import com.dirror.music.util.toast
import com.dso.ext.md5

/**
 * 通过手机号登录

 */
class LoginByPhoneActivity : BaseActivity() {

    private val loginCellphoneViewModel: LoginCellphoneViewModel by viewModels()

    lateinit var binding: ActivityLoginByPhoneBinding

    override fun initBinding() {
        if (getString(R.string.app_name).md5() == SkySecure.getAppNameMd5()) {
            binding = ActivityLoginByPhoneBinding.inflate(layoutInflater)
            setContentView(binding.root)
        } else {
            ActivityCollector.finishAll()
        }
    }

    override fun initListener() {
        binding.itemNeteaseCloudMusicApi.setOnClickListener {
            startActivity(Intent(this, NeteaseCloudMusicApiActivity::class.java))
        }
        binding.btnLoginByPhone.setOnClickListener {
            val phone = binding.etPhone.text.toString()
            val password = binding.etPassword.text.toString()
            if (phone == "" || password == "") {
                toast("请输入手机号或密码")
            } else {
                binding.btnLoginByPhone.visibility = View.GONE
                binding.llLoading.visibility = View.VISIBLE
                binding.lottieLoading.repeatCount = -1
                binding.lottieLoading.playAnimation()
                loginCellphoneViewModel.loginByCellphone(User.neteaseCloudMusicApi, phone, password, {
                    // 发送广播
                    val intent = Intent("com.dirror.music.LOGIN")
                    intent.setPackage(packageName)
                    sendBroadcast(intent)
                    // 通知 Login 关闭
                    setResult(RESULT_OK, Intent())
                    finish()
                }, { code ->
                    runOnMainThread {
                        binding.btnLoginByPhone.visibility = View.VISIBLE
                        binding.llLoading.visibility = View.GONE
                        binding.lottieLoading.cancelAnimation()
                        if (code == 250) {
                            toast("错误代码：250\n当前登录失败，请稍后再试")
                        } else {
                            toast("登录失败，请检查服务、用户名或密码")
                        }
                    }
                })
            }
        }
    }

}