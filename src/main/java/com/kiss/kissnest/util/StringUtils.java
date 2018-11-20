package com.kiss.kissnest.util;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class StringUtils {

    public static Date utcStringToDefaultString(String utcString,String utcFormatStyle) {

        utcString = utcString.replace("Z", " UTC");
//        SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss Z");
        SimpleDateFormat utcFormat = new SimpleDateFormat(utcFormatStyle);
        try {
            return utcFormat.parse(utcString);
        } catch (ParseException e) {
            e.printStackTrace();
            log.info("格式化{}出错,{}",utcString,new Date());
            return null;
        }
    }
}
