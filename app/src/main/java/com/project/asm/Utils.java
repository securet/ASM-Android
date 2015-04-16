package com.project.asm;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.DisplayMetrics;
import android.widget.TextView;

@SuppressLint("all")
public class Utils {
    private static double dayInSecs = 86400;
    private static double hourInSecs = 3600;
    private static double minInSecs = 60;

    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }

    public static int dpToPix(DisplayMetrics metrics, int dp) {
        int pixels = (int) (metrics.density * dp + 0.5f);
        return pixels;
    }

    public static String formatTimeinDaysHrsMinsSec(long timeInSec){
        StringBuilder sb = new StringBuilder();
        double delta =0;

        double days = Math.floor(timeInSec/dayInSecs);
        delta = delta - (days * dayInSecs);

        double hours = Math.floor(delta / hourInSecs)% 24;
        delta = delta - (hours * hourInSecs);

        double minutes = Math.floor(delta / minInSecs)%60;
        delta = delta - (minutes * 60);

        double seconds = delta % 60;

        int count=0;
        if (days>0){
            appendTimeString(sb, days,"day");
            count++;
        }
        if (hours>0){
            appendTimeString(sb, hours,"hour");
            count++;
        }

        if(count<2){
            if (minutes>0){
                appendTimeString(sb, minutes,"minute");
                count++;
            }
        }
        if(count<2){
            if (seconds>0){
                appendTimeString(sb, seconds,"second");
                count++;
            }
        }
        return sb.toString();
    }

    private static void appendTimeString(StringBuilder sb, double timeValue, String timeString) {
        sb.append(timeValue);
        if(timeValue>1){
            sb.append(" ").append(timeString).append("s");
        }else{
            sb.append(" ").append(timeString);
        }
    }

    public static void setAppVersion(Activity activity) {
        TextView appVersionView = (TextView) activity.findViewById(R.id.appVersion);
        Context applicationContext =  activity.getApplicationContext();
        PackageManager packageManager = applicationContext.getPackageManager();
        String curVersion = null;
        try {
            curVersion = packageManager.getPackageInfo(applicationContext.getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES).versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        appVersionView.setText("v"+curVersion);
    }

    public static String getAppVersion(Activity activity) {
        Context applicationContext =  activity.getApplicationContext();
        PackageManager packageManager = applicationContext.getPackageManager();
        String curVersion = null;
        try {
            curVersion = packageManager.getPackageInfo(applicationContext.getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES).versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return curVersion;
    }

}