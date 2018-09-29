package com.hzl.entity

/**
 * @author Hongzhi Liu  2014302580200@whu.edu.cn
 * @date 2018/9/27 11:17 
 */
class Person {
    String name
    int age

    @Override
    String toString(){
        return "hello, ${name},you're ${age} years old"
    }
}
