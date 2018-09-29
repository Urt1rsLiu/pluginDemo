package com.hzl

import com.hzl.entity.Person
import com.hzl.extensions.ExtensionDemo
import com.hzl.tasks.TaskDemo
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.execution.TaskExecutionGraphListener
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.initialization.Settings
import org.gradle.api.tasks.TaskState
import org.gradle.internal.reflect.Instantiator
import org.gradle.invocation.DefaultGradle

/**
 * @author Hongzhi Liu  2014302580200@whu.edu.cn
 * @date 2018/9/27 9:36 
 */
class PluginDemo implements Plugin<Project> {
    @Override
    void apply(Project project) {
        Settings

        //groovy中，.class和函数的()一样可以省略
        ExtensionDemo extensionDemo = project.extensions.create("ExtensionDemo", ExtensionDemo)
        Instantiator instantiator = ((DefaultGradle) project.getGradle()).getServices().get(Instantiator)


        /**
         * 执行task的例子
         */
        TaskDemo taskDemo = project.tasks.create('TaskDemo', TaskDemo)
        taskDemo.setPerson(new Person(name: "lhz", age: 23))
        taskDemo.setTargetDirectory(new File("D:\\gradlePluginOut"))
        taskDemo.setFileName("outTaskFile.txt")
        //这里设置task的组名，在:app的Tasks下会出现新的分组
        taskDemo.setGroup('TaskDemo')

        TaskDemo taskDemo2 = project.tasks.create('TaskDemo2', TaskDemo)
        taskDemo2.setPerson(new Person(name: "lhz2", age: 23))
        taskDemo2.setTargetDirectory(new File("D:\\gradlePluginOut"))
        taskDemo2.setFileName("outTaskFile2.txt")
        taskDemo2.setGroup('TaskDemo')


//        taskDemo.dependsOn(taskDemo2)

//        println "----------projectName: ${project.name} ---------------------------"
        /**
         * 这种方式是不能直接获取到系统task，因为该方法apply plugin 时调用，但此时并未configure build.gradle,因此还未确定所有task，也就无法获取系统task
         */
        def generateDebugSourcesTask = project.tasks.findByPath(':app:generateDebugSources')
        assert null == generateDebugSourcesTask

        /**
         *  手动调用execute() 执行task,这里调用的话将会在gradle 配置阶段执行
         *  但多次调用execute()只会执行一遍task
         *  分析源码，发现AbstractTask 继承的TaskInternal 内有TaskStateInternal 这一对象负责记录task 状态
         *  而TaskStateInternal中有TaskExecutionOutcome 这一集合，每次执行task 后会置为Executed 下次执行会跳过
         */
//        taskDemo.execute()

        /**
         * evaluate 过程为执行各个build.gradle 文件
         *
         * 在执行settings.gradle之后，执行build.gradle 之前回调
         * 这里一般可用于更改各build.gradle中对项目的配置
         */
        project.beforeEvaluate {

        }

        /**
         * 在执行build.gradle 之后回调
		 * **只有从这里开始才能读取到系统的task以及ext
         * 这里一般用于构建自定义task以及确定task的依赖关系
         */
        project.afterEvaluate {
//            project.tasks.matching {
//                // 以process开头以ReleaseJavaRes或DebugJavaRes结尾的task
//                it.name.startsWith('process') && (it.name.endsWith('ReleaseJavaRes') || it.name.endsWith('DebugJavaRes'))
//
//            }.each { task ->
//                task.dependsOn(taskDemo, taskDemo2)  // 任务依赖：执行task之前需要执行dependsOn指定的任务
//            }

            /**
             * 在app下的系统task 前完成指定的task
             */
            def systemTask = project.tasks.getByName("javaPreCompileDebug")
            if (systemTask == null) {
                println "-----------------cant find systemTask-------------------"
            } else {
                systemTask.dependsOn(taskDemo)
            }
        }

        /**
         * 每个project 的build.gradle 执行完毕时回调
         * 等同于project.afterEvaluate{}* 通过获取project 所属的gradle 对象监听
         */
        project.gradle.afterProject { Project mProject ->
            println mProject.getName() + " has evaluated"
        }

        /**
         * 顾名思义，新的task 创建后回调
         */
        project.tasks.whenTaskAdded {

        }

        /**
         * task 关系图创建后回调
         */
        project.gradle.taskGraph.whenReady {
            println "task graph ready"
        }

        /**
         * 所有gradle build 完毕时回调
         * 此时所有task 执行完毕
         */
        project.gradle.buildFinished {
            println "-----------all build finish------------"
        }

        /**
         * 监听task开始执行，以及结束执行
         * project.gradle.addListener()可以传入任意类别的监听器，具体监听器类别见源码
         */
        project.gradle.taskGraph.addTaskExecutionListener(new TaskExecutionListener() {
            @Override
            void beforeExecute(Task task) {
                switch (task.name) {
                    case 'TaskDemo':
                    case 'TaskDemo2':
                        println "---------start execute task: ${task.name}------------"
                        break
                    default:
                        break
                }
            }

            @Override
            void afterExecute(Task task, TaskState state) {
                switch (task.name) {
                    case 'TaskDemo':
                    case 'TaskDemo2':
                        println "---------finish execute task: ${task.name}------------"
                        break
                    default:
                        break
                }
            }
        })


        def prop = new Properties()


    }
}
