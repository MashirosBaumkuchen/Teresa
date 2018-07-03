package com.jascal.teresa.net.exception

import com.google.gson.JsonParseException
import com.orhanobut.logger.Logger

import org.json.JSONException

import java.net.ConnectException

import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.ParseException

/**
 * @author jascal
 * @time 2018/7/3
 * describe
 */
class ExceptionHandler {
    companion object {
        var errorCode = ErrorStatus.UNKNOWN_ERROR
        var errorMsg = "请求失败，请稍后重试"
        fun handleException(e: Throwable): String {
            e.printStackTrace()
            when (e) {
                is SocketTimeoutException -> {
                    Logger.e("TAG", "网络连接异常: " + e.message)
                    errorMsg = "网络连接异常"
                    errorCode = ErrorStatus.NETWORK_ERROR
                }
                is ConnectException -> {
                    Logger.e("TAG", "网络连接异常: " + e.message)
                    errorMsg = "网络连接异常"
                    errorCode = ErrorStatus.NETWORK_ERROR
                }
                is JsonParseException -> {
                    Logger.e("TAG", "数据解析异常: " + e.message)
                    errorMsg = "数据解析异常"
                    errorCode = ErrorStatus.SERVER_ERROR
                }
                is JSONException -> {
                    Logger.e("TAG", "数据解析异常: " + e.message)
                    errorMsg = "数据解析异常"
                    errorCode = ErrorStatus.SERVER_ERROR
                }
                is ParseException -> {
                    Logger.e("TAG", "数据解析异常: " + e.message)
                    errorMsg = "数据解析异常"
                    errorCode = ErrorStatus.SERVER_ERROR
                }
                is UnknownHostException -> {
                    Logger.e("TAG", "网络连接异常: " + e.message)
                    errorMsg = "网络连接异常"
                    errorCode = ErrorStatus.NETWORK_ERROR
                }
                is IllegalArgumentException -> {
                    errorMsg = "参数错误"
                    errorCode = ErrorStatus.SERVER_ERROR
                }
                else -> {
                    try {
                        Logger.e("TAG", "错误: " + e.message)
                    } catch (e1: Exception) {
                        Logger.e("TAG", "未知错误Debug调试 ")
                    }
                    errorMsg = "未知错误，可能抛锚了吧~"
                    errorCode = ErrorStatus.UNKNOWN_ERROR
                }
            }
            return errorMsg
        }

    }


}
