package com.project.asm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bugsense.trace.BugSenseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sharad on 4/15/2015.
 */
public class AppUpdateNotifier extends AsyncTask<String, Void, String> {

    private static final String TAG = AppUpdateNotifier.class.getName();
    private Activity activity =null;

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... params) {
        String response = "";

        try {
            Map<String,Object> parameters = new HashMap<String,Object>();
            parameters.put("appCurrentVersion",params[0]);
            response = API.checkAppNotifications(parameters);
        } catch (Exception e) {
            if(API.DEBUG){
                Log.d(TAG, "Could not fetch the notification info", e);
            }
            response = "No Internet";
            BugSenseHandler.sendException(e);
        }
        return response;
    }
    @Override
    protected void onPostExecute(String result) {
        if (!(result.equals("No Internet")) || !(result.equals(""))) {
            try {
                if (result.toString().contains("status") && (new JSONObject(result).getString("status").toString().equals("success"))) {
                    JSONObject obj = new JSONObject(result);
                    JSONArray array = new JSONArray(obj.getString("data"));
                    if(array.length()>0){
                        JSONObject notification = array.getJSONObject(0);
                        if(notification.getBoolean("appUpdate")){
                            //show app update dialog, this should be a forced message, and should be shown everytime until user performs action..
                            showAppUpdateDialog(notification.getString("message"));
                        }else{
                            //show app notification
                            //do not show the icon once shown..
                            SharedPreferences myPrefs = activity.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor prefsEditor = myPrefs.edit();
                            int lastShownNotificationId = myPrefs.getInt("lastShownNotificationId",0);
                            if(lastShownNotificationId < notification.getInt("notificationId")){
                                prefsEditor.putInt("lastShownNotificationId",notification.getInt("notificationId"));
                                prefsEditor.commit();
                                showAppNotification(notification.getString("message"));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.d(TAG,"Error parsing notification",e);
            }
        }
    }

    public void showAppUpdateDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setTitle("ASM App Update!");
        builder.setMessage(message)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("market://details?id=" + activity.getPackageName()));
                        activity.startActivity(goToMarket);
                    }
                });
        builder.show();
        // Create the AlertDialog object and return it
    }

    public void showAppNotification(String message){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity);
        Drawable drawable = null;
        try {
            drawable = activity.getPackageManager().getApplicationIcon(activity.getPackageName());
            mBuilder.setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle("ASM Notification!")
                    .setContentText(message);
            NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            mNotificationManager.notify(9999, mBuilder.build());
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG,"Package name not found ",e);
        }
    }

    public static AppUpdateNotifier initAppUpdateNotifier(Activity activity){
        AppUpdateNotifier  appUpdateNotifier = new AppUpdateNotifier();
        appUpdateNotifier.setActivity(activity);
        appUpdateNotifier.execute(Utils.getAppVersion(activity));
        return appUpdateNotifier;
    }

}

