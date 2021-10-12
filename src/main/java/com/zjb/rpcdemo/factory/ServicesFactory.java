package com.zjb.rpcdemo.factory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 * @date 2021/10/11 22:51
 **/
public class ServicesFactory {
    static Map<Class<?>, Object> map = new HashMap<>(16);

    public static Object getInstance(Class<?> interfaceClass) {
        try {
            Class<?> clazz = Class.forName("com.zjb.rpcdemo.service.HelloService");
            Object instance = Class.forName("com.zjb.rpcdemo.service.impl.HelloServiceImpl").newInstance();
            map.put(clazz, instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map.get(interfaceClass);
    }
}
