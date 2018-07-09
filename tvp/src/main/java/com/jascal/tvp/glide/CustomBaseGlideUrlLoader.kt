package com.jascal.teresa.glide

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.*
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader
import java.io.InputStream
import java.util.regex.Pattern

/**
 * @author jascal
 * @time 2018/7/3
 * describe
 */
class CustomBaseGlideUrlLoader(concreteLoader: ModelLoader<GlideUrl, InputStream>, modelCache: ModelCache<String, GlideUrl>) : BaseGlideUrlLoader<String>(concreteLoader, modelCache) {

    // 控制加载的图片的大小
    override fun getUrl(model: String, width: Int, height: Int, options: Options): String {
        val m = PATTERN.matcher(model)
        var bestBucket: Int
        if (m.find()) {
            val found = m.group(1).split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (bucketStr in found) {
                bestBucket = Integer.parseInt(bucketStr)
                if (bestBucket >= width) {
                    // the best bucket is the first immediately bigger than the requested width
                    break
                }
            }

        }
        return model
    }

    override fun handles(s: String): Boolean {
        return true
    }

    // 工厂来构建CustomBaseGlideUrlLoader对象
    class Factory : ModelLoaderFactory<String, InputStream> {
        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<String, InputStream> {
            return CustomBaseGlideUrlLoader(multiFactory.build(GlideUrl::class.java, InputStream::class.java), urlCache)
        }

        override fun teardown() {
        }
    }

    companion object {
        private val urlCache = ModelCache<String, GlideUrl>(150)
        private val PATTERN = Pattern.compile("__w-((?:-?\\d+)+)__") // Url的匹配规则
    }
}
