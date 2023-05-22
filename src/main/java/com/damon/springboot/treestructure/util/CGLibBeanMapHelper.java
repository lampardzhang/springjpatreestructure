package com.damon.springboot.treestructure.util;



import net.sf.cglib.beans.BeanMap;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CGLibBeanMapHelper {
    private static final ConcurrentMap<Class, BeanMap> beanMapCache = new ConcurrentHashMap();

    public CGLibBeanMapHelper() {
    }

    public static BeanMap createBeanMap(Object obj) {
        Class clazz = obj.getClass();
        BeanMap beanMap = beanMapCache.get(clazz);
        if (beanMap == null) {
            beanMap = BeanMap.create(obj);
            beanMap.setBean(null);
            beanMapCache.putIfAbsent(clazz, beanMap);
        }

        return beanMap.newInstance(obj);
    }
}