package com.zjb.rpcdemo.service.impl;

import com.zjb.rpcdemo.service.HelloService;

/**
 * @author Administrator
 * @date 2021/10/12 22:51
 **/
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHi(String name) {
        return "你好，" + name;
    }
}
