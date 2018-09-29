package com.hzl.extensions

import com.hzl.entity.Person

/**
 * @author Hongzhi Liu  2014302580200@whu.edu.cn
 * @date 2018/9/27 10:38 
 */

/**
 * Extension
 * 通过Extension，我们可以向目标对象添加DSL扩展 这一过程通过project中的ExtensionContainer来实现
 * 我们可以通过ExtensionContainer的create来创建新的DSL域，并与一个对应的委托类关联起来（即新建一个DSL域，并委托给一个具体类）
 *
 * Convention
 * 与Extension类似，但是又有所不同，通过Convention的getPlugin方法，我们会把一个类融合到Convention所在的域，而不是新建一个域（具体区别可以参考java插件和android插件）
 */

class ExtensionDemo {
    String extensionName
    Person person
}
