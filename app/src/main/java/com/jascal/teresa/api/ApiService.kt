package com.jascal.teresa.api

import com.jascal.teresa.mvp.model.bean.DiscoverBean
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * @author jascal
 * @time 2018/7/3
 * describe
 */
interface ApiService {
    // 首页
    @GET("v2/feed?")
    fun getFirstHomeData(@Query("num") num: Int): Observable<DiscoverBean>

    // 下一页
    @GET
    fun getMoreHomeData(@Url url: String): Observable<DiscoverBean>

    // 相关视频
    @GET("v4/video/related?")
    fun getRelatedData(@Query("id") id: Long): Observable<DiscoverBean.Issue>
}