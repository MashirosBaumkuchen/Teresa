package com.jascal.teresa.mvp.model

import android.os.Parcel
import android.os.Parcelable
import com.jascal.teresa.mvp.model.bean.DiscoverBean
import com.jascal.teresa.net.RetrofitManager
import com.jascal.teresa.scheduler.SchedulerUtils
import io.reactivex.Observable

/**
 * @author jascal
 * @time 2018/7/3
 * describe
 */
class DiscoverModel() : Parcelable {
    constructor(parcel: Parcel) : this()

    fun requestDiscoverData(num: Int): Observable<DiscoverBean> {
        return RetrofitManager.service.getFirstHomeData(num)
                .compose(SchedulerUtils.ioToMain())
    }

    fun loadMoreData(url: String): Observable<DiscoverBean> {
        return RetrofitManager.service.getMoreHomeData(url)
                .compose(SchedulerUtils.ioToMain())
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DiscoverModel> {
        override fun createFromParcel(parcel: Parcel): DiscoverModel {
            return DiscoverModel(parcel)
        }

        override fun newArray(size: Int): Array<DiscoverModel?> {
            return arrayOfNulls(size)
        }
    }

}