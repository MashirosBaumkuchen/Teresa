package com.jascal.teresa.ui.activity

import android.Manifest
import android.content.Intent
import com.jascal.teresa.R
import com.jascal.teresa.base.BaseActivity
import com.jascal.teresa.utils.showToast
import com.orhanobut.logger.Logger
import me.weyye.hipermission.HiPermission
import me.weyye.hipermission.PermissionCallback
import me.weyye.hipermission.PermissionItem

/**
 * @author jascal
 * @time 2018/6/28
 * describe splash activity, welcome page
 */
class SplashActivity : BaseActivity() {
    override fun layoutID(): Int = R.layout.activity_splash

    override fun initData() {

    }

    override fun initView() {
        checkPermission()
    }

    private fun toHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun checkPermission() {
        val permissionItems = ArrayList<PermissionItem>()
        permissionItems.add(PermissionItem(Manifest.permission.READ_EXTERNAL_STORAGE, "read", R.drawable.permission_ic_storage))
        permissionItems.add(PermissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE, "write", R.drawable.permission_ic_storage))
        HiPermission.create(this)
                .title("Dear Mrs.Jane")
                .msg("please allow these permission!")
                .permissions(permissionItems)
                .style(R.style.PermissionDefaultNormalStyle)
                .animStyle(R.style.PermissionAnimScale)
                .checkMutiPermission(object : PermissionCallback {
                    override fun onClose() {
                        Logger.i("permission_onClose")
                        showToast("用户关闭了权限")
                    }

                    override fun onFinish() {
                        showToast("初始化完毕！")
                        toHome()
                    }

                    override fun onDeny(permission: String, position: Int) {
                        Logger.i("permission_onDeny")
                    }

                    override fun onGuarantee(permission: String, position: Int) {
                        Logger.i("permission_onGuarantee")
                    }
                })
    }

}