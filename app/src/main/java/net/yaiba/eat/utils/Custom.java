package net.yaiba.eat.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import net.yaiba.eat.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static android.R.attr.format;

public class Custom {

    public static String getVersion(Context context)//获取版本号
    {
        try {
            PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return context.getString(R.string.version_unknown);
        }
    }

    public static int getVersionCode(Context context)//获取版本号(内部识别号)
    {
        try {
            PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static  String getEatTimeValue(String eatTimeName){
        String eatTimeValue = "breakfast";

        switch(eatTimeName) {
            case "早餐": eatTimeValue = "breakfast";break;
            case "上午茶": eatTimeValue = "before_lunch";break;
            case "午餐": eatTimeValue = "lunch";break;
            case "下午茶": eatTimeValue = "before_dinner";break;
            case "晚餐": eatTimeValue = "dinner";break;
            case "夜宵": eatTimeValue = "midnight_snack";break;
            default: break;
        }
        return eatTimeValue;
    }

    public static  String getEatTimeName(String eatTimeVlue){
        String eatTimeValue = "breakfast";

        switch(eatTimeVlue) {
            case "breakfast": eatTimeValue = "早餐";break;
            case "before_lunch": eatTimeValue = "上午茶";break;
            case "lunch": eatTimeValue = "午餐";break;
            case "before_dinner": eatTimeValue = "下午茶";break;
            case "dinner": eatTimeValue = "晚餐";break;
            case "midnight_snack": eatTimeValue = "夜宵";break;
            default: break;
        }
        return eatTimeValue;
    }

    public static  int getEatTimeIndex(String eatTimeVlue){
        int eatTimeValue = 0;

        switch(eatTimeVlue) {
            case "breakfast": eatTimeValue = 0;break;
            case "before_lunch": eatTimeValue = 1;break;
            case "lunch": eatTimeValue = 2;break;
            case "before_dinner": eatTimeValue = 3;break;
            case "dinner": eatTimeValue = 4;break;
            case "midnight_snack": eatTimeValue = 5;break;
            default: break;
        }
        return eatTimeValue;
    }

    public static String dayForWeek(String pTime) {
        Calendar calendar = Calendar.getInstance();

        String[] data = pTime.split("-");
        if(data.length == 3){
            int year = Integer.valueOf(data[0]);
            int month = Integer.valueOf(data[1]);
            int date = Integer.valueOf(data[2]);
            calendar.set(year,month-1,date);
            int number = calendar.get(Calendar.DAY_OF_WEEK)-1;
            //if(number < 0) number = 0 ;
            String[] str = {"日","一","二","三","四","五","六"};
            return str[number];
        } else {
            return "-";
        }




    }

}
