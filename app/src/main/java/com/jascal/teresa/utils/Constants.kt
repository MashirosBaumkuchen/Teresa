package com.jascal.teresa.utils

/**
 * @author jascal
 * @time 2018/7/3
 * describe
 */
class Constants private constructor() {
    companion object {
        const val BUNDLE_VIDEO_DATA = "video_data"
        const val BUNDLE_CATEGORY_DATA = "category_data"
        const val BUGLY_APPID = "176aad0d9e"    //腾讯 Bugly APP id
        const val FILE_WATCH_HISTORY_NAME = "watch_history_file"   //sp 存储的文件名, 观看记录
        const val FILE_COLLECTION_NAME = "collection_file"    //收藏视屏缓存的文件名
    }
}