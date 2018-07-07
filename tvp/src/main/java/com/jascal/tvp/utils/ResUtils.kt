package com.jascal.tvp.utils

import android.content.Context

//获取Class对象有三种方式：
//1.通过Object类的getClass()方法。例如：
//Class c1 = new String("").getClass();
//
//2.通过Class类的静态方法——forName()来实现：
//Class c2 = Class.forName("MyObject");
//
//3.如果T是一个已定义的类型的话，在java中，它的.class文件名：T.class就代表了与其匹配的Class对象，例如：
//Class c3 = Manager.class;
//Class c4 = int.class;
//Class c5 = Double[].class;

object ResUtil {

    fun getLayoutId(paramContext: Context, paramString: String): Int {
        return paramContext.resources.getIdentifier(paramString,
                "layout", paramContext.packageName)
    }

    fun getStringId(paramContext: Context, paramString: String): Int {
        return paramContext.resources.getIdentifier(paramString,
                "string", paramContext.packageName)
    }

    fun getDrawableId(paramContext: Context, paramString: String): Int {
        return paramContext.resources.getIdentifier(paramString,
                "drawable", paramContext.packageName)
    }

    fun getStyleId(paramContext: Context, paramString: String): Int {
        return paramContext.resources.getIdentifier(paramString,
                "style", paramContext.packageName)
    }

    fun getId(paramContext: Context, paramString: String): Int {
        return paramContext.resources.getIdentifier(paramString,
                "id", paramContext.packageName)
    }

    fun getColorId(paramContext: Context, paramString: String): Int {
        return paramContext.resources.getIdentifier(paramString,
                "color", paramContext.packageName)
    }

    fun getArrayId(paramContext: Context, paramString: String): Int {
        return paramContext.resources.getIdentifier(paramString,
                "array", paramContext.packageName)
    }
}
