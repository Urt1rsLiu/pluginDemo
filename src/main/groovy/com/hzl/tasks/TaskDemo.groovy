package com.hzl.tasks

import com.hzl.entity.Person
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*

/**
 * @author Hongzhi Liu  2014302580200@whu.edu.cn
 * @date 2018/9/27 9:38 
 */

/**
 * 每个task 都是project的成员属性
 * 一个task 必须包含以下几种属性
 *
 *  1.输出
 *  Task的目标，我们的task 都是为了输出，比如jar的输出是jar文件，copy的输出是目标目录
 *
 *  2.输入
 *  只有会影响一个或多个输出结果的才算是输入
 *
 *  3.属性
 *  会影响task 的执行过程，但是不会影响结果
 *
 *  4.TaskAction
 *  Task 执行的具体任务
 *
 *
 *  关于task 的输入输出，支持3 种类型
 *  1.实现了Serializable 接口的类型   ( 如 String int 等基础数据类型)
 *  2.文件类型                       ( 包括java 中的File 和Files 以及Gradle 中的FileCollection 类型)
 *  3.Nested 类型        ( 自定义的类型，不属于上面两种，但是类型里的属性都属于上面两种)
 *
 *  Gradle 中有一个重要的机制为Incremental Build 机制
 *  顾名思义，该机制在task 在输入和输出与上次相比没有任何变化时就会跳过该task 的执行，从而加快构建的速度，然后gradle console 就会显示task up-to-date
 *
 *  因为Gradle的Incremental Build机制，我们必须把输入输出 都标记上才能正确的运行（如果没有标记，可能会出现输入输出改变了，但是Task却跳过了）
 *  但是同时Gradle也不建议把不会影响输出的属性标记为输入，这样会在整体上降低Gradle的构建速度
 */
class TaskDemo extends DefaultTask {

    //dependsOn 表示task 间的依赖关系
    //这里由于要输出lint报告，所以依赖于lint 这个task
    TaskDemo() {
        super()
    }


    @Nested
    Person person

    @InputDirectory
    File targetDirectory

    @Input
    String fileName

    //@TaskAction 注释指定task的具体工作,在该task 执行时会调用指定的方法
    @TaskAction
    def generateFileTask() {
        println "-------start action: generate ${fileName}-----------"
        generateFile()
    }

    /**
     * 输出文件
     */
    def generateFile() {
        def targetFile = new File(targetDirectory, fileName)
        try {
            FileOutputStream fos = new FileOutputStream(targetFile)
            byte[] bytes = person.toString().getBytes()
            fos.write(bytes)
            fos.flush()
            fos.close()
        } catch (FileNotFoundException e) {
            e.printStackTrace()
        } catch (IOException e) {
            e.printStackTrace()
        }

    }
}
