package com.kiss.kissnest.util;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class BeanCopyUtil {

    public static Object copy (Object source,Class<?> targetClass){

        try {
            Object target = targetClass.newInstance();

            if (source != null) {
                BeanUtils.copyProperties(source,target);
            }

            return target;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Object> copyList (List<?> source,Class<?> targetClass) {

        List<Object> targetList = new ArrayList<>();

        try {

            if (source != null && source.size() != 0) {
                for (Object object : source) {
                    Object target = targetClass.newInstance();
                    BeanUtils.copyProperties(object,target);
                    targetList.add(target);
                }
            }

            return targetList;
        } catch (Exception e) {
            e.printStackTrace();
            return targetList;
        }
    }
}
