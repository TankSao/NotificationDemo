package com.example.administrator.notificationdemo;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private int NOTIFICATION_SYSTEM_ID = 1000;//系统通知ID
    private int NOTIFICATION_SELF_ID = 2000;//自定义通知ID
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if(!isNotificationEnable(this)){
            Toast.makeText(MainActivity.this,"通知不可用",Toast.LENGTH_SHORT).show();
        }
    }


    @OnClick({R.id.btn1, R.id.btn2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn1:
                //系统自带通知
                showNotification1();
                break;
            case R.id.btn2:
                //自定义通知
                showNotification2();
                break;
        }
    }


    public void requestAllPower() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.VIBRATE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.VIBRATE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.VIBRATE}, 1);
            }
        }
    }

    public boolean isNotificationEnable(Context context){
            //判断通知是否可用
            AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(APP_OPS_SERVICE);
            ApplicationInfo appInfo = context.getApplicationInfo();
             String pkg = context.getApplicationContext().getPackageName();
             int uid = appInfo.uid;
            Class appOpsClass = null;
             /* Context.APP_OPS_MANAGER */
            try {
                 appOpsClass = Class.forName(AppOpsManager.class.getName());
                 Method checkOpNoThrowMethod = appOpsClass.getMethod("checkOpNoThrow", Integer.TYPE, Integer.TYPE,
                     String.class);
                Field opPostNotificationValue = appOpsClass.getDeclaredField("OP_POST_NOTIFICATION");
                int value = (Integer) opPostNotificationValue.get(Integer.class);
                return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);
                } catch (ClassNotFoundException e) {
                     e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                 } catch (NoSuchFieldException e) {
                     e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                 } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                 return false;
         }

    private void showNotification1() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(MainActivity.this);
        //图标
        builder.setSmallIcon(R.mipmap.ic_launcher);
        //声音、震动
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        //标题
        builder.setContentTitle("系统通知");
        //点击后取消
        builder.setAutoCancel(true);
        //描述
        builder.setContentText("这是系统自带的通知");
        //点击跳转事件
        Intent intent = new Intent(MainActivity.this, NewActivity.class);
        intent.putExtra("data","来自系统通知");
        PendingIntent pi = PendingIntent.getActivity(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pi);
        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_SYSTEM_ID++, notification);
        }
    }
    private void showNotification2() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(MainActivity.this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        //声音、震动
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        //自定义布局
        RemoteViews rv = new RemoteViews(getPackageName(), R.layout.notification_item);
        rv.setTextViewText(R.id.from_name, "自定义通知");
        rv.setTextViewText(R.id.from_message, "这是自定义的通知");
        /*Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
        rv.setImageViewBitmap(R.id.icon, bitmap);*/
        builder.setContent(rv);
        //点击后取消
        builder.setAutoCancel(true);
        //点击跳转事件
        Intent intent = new Intent(MainActivity.this, NewActivity.class);
        intent.putExtra("data","来自自定义通知");
        PendingIntent pi = PendingIntent.getActivity(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pi);
        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_SELF_ID, notification);
        }
    }
}
