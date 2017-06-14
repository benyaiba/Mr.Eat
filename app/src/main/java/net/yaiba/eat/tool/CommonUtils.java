package net.yaiba.eat.tool;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yang_lifeng on 2017/06/13.
 */

public class CommonUtils {

    public static String getNowTime(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");//设置日期格式
        return df.format(new Date());
    }

    public static int getYear(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy");//设置日期格式
        return Integer.valueOf(df.format(new Date()));
    }

    public static int getMonthMM(){
        SimpleDateFormat df = new SimpleDateFormat("MM");//设置日期格式
        return Integer.valueOf(df.format(new Date()));
    }

    public static int getDaydd(){
        SimpleDateFormat df = new SimpleDateFormat("dd");//设置日期格式
        return Integer.valueOf(df.format(new Date()));
    }

    public static int getHoureHH(){
        SimpleDateFormat df = new SimpleDateFormat("HH");//设置日期格式
        return Integer.valueOf(df.format(new Date()));
    }

    public static int getMinutemm(){
        SimpleDateFormat df = new SimpleDateFormat("mm");//设置日期格式
        return Integer.valueOf(df.format(new Date()));
    }
}
