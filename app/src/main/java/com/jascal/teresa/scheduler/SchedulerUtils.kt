package com.jascal.teresa.scheduler

/**
 * @author jascal
 * @time 2018/7/3
 * describe
 */
object SchedulerUtils {
    fun <T> ioToMain(): IoMainScheduler<T> {
        return IoMainScheduler()
    }
}
