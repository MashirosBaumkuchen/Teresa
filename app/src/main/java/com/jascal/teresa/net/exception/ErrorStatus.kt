package com.jascal.teresa.net.exception

/**
 * @author jascal
 * @time 2018/7/3
 * describe
 * @JvmField:指示Kotlin编译器不为该属性生成getter/setter，并将其作为字段公开。如果用来修饰val变量，就和const关键字的功能一样了
 * @JvmOverloads:指示Kotlin编译器为包含默认参数值的函数生成重载。
 * @JvmStatic:指定从该元素中生成静态方法需要。注意：此注解只能用于被object关键字修饰的类的方法，或者companion object中的方法
 * @JvmSynthetic:用来注解方法和字段，使得被标记的元素只能在kotlin代码中使用，在java代码中无法使用。
 * @JvmSuppressWildcards:用来注解类和方法，使得被标记元素的泛型参数不会被编译成通配符
 */
object ErrorStatus {
    const val SUCCESS = 0

    const val UNKNOWN_ERROR = 1002

    const val SERVER_ERROR = 1003

    const val NETWORK_ERROR = 1004

    const val API_ERROR = 1005
}