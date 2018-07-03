package com.jascal.teresa.mvp.model

import com.jascal.teresa.mvp.model.bean.DiscoverBean
import com.jascal.teresa.net.RetrofitManager
import com.jascal.teresa.scheduler.SchedulerUtils
import io.reactivex.Observable

/**
 * @author jascal
 * @time 2018/7/3 
 * describe
 */
class VideoDetailModel {
    fun requestRelatedData(id:Long):Observable<DiscoverBean.Issue>{
        return RetrofitManager.service.getRelatedData(id)
                .compose(SchedulerUtils.ioToMain())
    }
}